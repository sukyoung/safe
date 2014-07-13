/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.scala_src.useful

import scala.collection.mutable.ListBuffer

class WorkManager
{
  ////////////////////////////////////////////////////////////////////////////////
  // private variables
  ////////////////////////////////////////////////////////////////////////////////
  private var initialized: Boolean = false
  private var workThreads: Array[Thread] = null
  private var workThreadContexts: Array[Any] = null
  private var idleWorkThreadCount: Int = 0
  private val workQueue: ListBuffer[WorkTrait] = new ListBuffer[WorkTrait]()
  private val workFinishEvent: AnyRef = new AnyRef()

  ////////////////////////////////////////////////////////////////////////////////
  // Initialize & Deinitialize
  ////////////////////////////////////////////////////////////////////////////////
  /**
   * Check whether the WorkManager is initialized.
   * @return                   Returns true if it is initialized, otherwise false.
   */
  def isInitialized(): Boolean = return initialized

  /**
   * Get total work thread count.
   * @return                   Returns total work thread count.
   */
  def getTotalWorkThreadCount(): Int = workThreads.length

  /**
   * Get idle work thread count.
   * @return                   Returns idle work thread count.
   */
  def getIdleWorkThreadCount(): Int = idleWorkThreadCount

  /**
   * Initialize the WorkManager.
   * @param    threadCount     The number of thread count to create.
   * @return                   Returns true if succeeds, otherwise false.
   */
  def initialize(newWorkThreadContext: () => Any = null, threadCount: Int = 0): Boolean = {
    if(initialized == true) return false
    initialized = true

    // If threadCount is 0 then set threadCount to the number of processor.
    var CPUCount: Int = threadCount
    if(CPUCount <= 0) CPUCount = Runtime.getRuntime().availableProcessors()
    workThreads = new Array[Thread](CPUCount)

    // Set the WorkThreadContexts
    workThreadContexts = new Array[Any](CPUCount)
    setNewWorkThreadContexts(newWorkThreadContext)

    // Creates work threads and starts them.
    for(i <- 0 until CPUCount) {
      workThreads(i) = new Thread() {
        override def run(): Unit = {
          while(true) {
            // Get a work.
            val workTrait: WorkTrait = popWork()
            // If WorkManager is deinitialized then return.
            if(workTrait == null) return
            // Call the user defined doit method.
            workTrait.workThreadContext = workThreadContexts(i)
            workTrait.doit()
          }
        }
      }
      workThreads(i).start()
    }

    return true
  }

  /**
   * Deinitialize the WorkManager.
   */
  def deinitialize(): Unit = {
    if(initialized == false) return
    initialized = false

    // Notify all work threads to terminate them.
    workQueue.synchronized {
      workQueue.notifyAll()
    }

    // Wait for threads until all threads are terminated.
    for(i <- 0 until workThreads.length) workThreads(i).join()
    workThreads = null
    workThreadContexts = null

    // Clear the WorkQueue.
    workQueue.clear()
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Work push & pop
  ////////////////////////////////////////////////////////////////////////////////
  /**
   * Push a work.
   * Work threads will pop and process these works.
   * @param    workTrait       Returns a work to push.
   */
  def pushWork(workTrait: WorkTrait): Unit = {
    if(initialized == false) return

    workQueue.synchronized {
      workQueue.append(workTrait)
      workQueue.notify()
    }
  }

  /**
   * Pop a work.
   * @return                   Returns a work if WorkManager pop successfully, otherwise null.
   */
  private def popWork(): WorkTrait = {
    var workTrait: WorkTrait = null
    while(workTrait == null) {
      if(initialized == false) return null

      workQueue.synchronized {
        // Increase an idle thread count
        idleWorkThreadCount+= 1

        // If workQueue is empty then wait for a work.
        if(workQueue.isEmpty == true) {
          //System.out.println(Thread.currentThread() + " is idle(count = " + idleWorkThreadCount + ")")

          // If all works are finished then set an event.
          if(idleWorkThreadCount == workThreads.length) {
            workFinishEvent.synchronized {
              workFinishEvent.notify()
            }
          }
          // Wait for a work.
          //System.out.println(Thread.currentThread() + " waits... (idleWorkThreadCount = " + idleWorkThreadCount + ")")
          workQueue.wait()
          //System.out.println(Thread.currentThread() + " woke up. (idleWorkThreadCount = " + (idleWorkThreadCount - 1) + ")")
        }
        // Get a work.
        else workTrait = workQueue.remove(0)

        // Decrease an idle thread count
        idleWorkThreadCount-= 1
      }
    }

    return workTrait
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Utility
  ////////////////////////////////////////////////////////////////////////////////
  /**
   * Set new work thread context.
   * This work thread context is passed to WorkTrait to indicate which thread is processing the work.
   * Work thread context can be used to store independent variables from the other threads.
   */
  def setNewWorkThreadContexts(newWorkThreadContext: () => Any): Boolean = {
    if(initialized == false) return false

    // Creates work thread contexts.
    for(i <- 0 until workThreadContexts.length) {
      if(newWorkThreadContext == null) workThreadContexts(i) = null
      else workThreadContexts(i) = newWorkThreadContext()
    }

    return true
  }

  def foreachWorkThreadContext[workThreadContext](Callback: (workThreadContext) => Unit): Unit = {
    if(initialized == false) return

    // Call callback function for each work thread context
    for(i <- 0 until workThreadContexts.length) {
      Callback(workThreadContexts(i).asInstanceOf[workThreadContext])
    }
  }

  /**
   * Check all works are finished.
   * If this method is called frequently then performance will decrease.
   * Use waitFinishEvent method to receive a work finish event instead of polling this method.
   * @return                   Returns true if the WorkQueue is empty and all work threads are in idle state.
   */
  def isFinished(): Boolean = {
    if(initialized == false) return true

    workQueue.synchronized {
      return workQueue.isEmpty == true && idleWorkThreadCount == workThreads.length
    }
  }

  /**
   * Wait until all works are finished.
   */
  def waitFinishEvent(): Unit = {
    if(isFinished() == true) return
    workFinishEvent.synchronized {
      workFinishEvent.wait()
    }
  }
}
