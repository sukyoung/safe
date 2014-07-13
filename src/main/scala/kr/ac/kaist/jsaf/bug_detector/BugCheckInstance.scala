/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import scala.collection.mutable.{HashMap => MHashMap}
import scala.collection.mutable.ListBuffer
import kr.ac.kaist.jsaf.analysis.cfg.FunctionId
import kr.ac.kaist.jsaf.analysis.typing.CallContext
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.nodes_util.Span

class BugCheckInstance() {
  ////////////////////////////////////////////////////////////////////////////////
  // Check instance class
  ////////////////////////////////////////////////////////////////////////////////
  class CheckInstance {
    var span:                                   Span = null
    var callContext:                            CallContext = null
    var state:                                  State = null
    var bugKind:                                BugKind = 0
    var fid:                                    FunctionId = 0
    var loc1:                                   Loc = 0
    var loc2:                                   Loc = 0
    var value1:                                 Value = null
    var value2:                                 Value = null
    var pValue:                                 PValue = null
    var absValue:                               AbsDomain = null
    var valueType:                              Int = 0

    // Additional strings for general purpose
    var string1:                                String = null
    var string2:                                String = null
    var string3:                                String = null
    var string4:                                String = null
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Bug and not bug list
  ////////////////////////////////////////////////////////////////////////////////
  var bugList                                   = new ListBuffer[CheckInstance]()
  val notBugList                                = new ListBuffer[CheckInstance]()
  var groupedBugList:                           MHashMap[Int, ListBuffer[CheckInstance]] = null

  // Insert bug check instance
  def insert(isBug: Boolean, span: Span, callContext: CallContext, state: State): CheckInstance = {
    // Create a new instance
    val checkInstance = new CheckInstance
    checkInstance.span = span
    checkInstance.callContext = callContext
    checkInstance.state = state

    if(isBug) bugList.append(checkInstance)
    else notBugList.append(checkInstance)

    checkInstance
  }

  def insertWithStrings(isBug: Boolean, span: Span, callContext: CallContext, state: State, strings: String*): CheckInstance = {
    val checkInstance = insert(isBug, span, callContext, state)
    if(strings.length >= 1) checkInstance.string1 = strings(0)
    if(strings.length >= 2) checkInstance.string2 = strings(1)
    if(strings.length >= 3) checkInstance.string3 = strings(2)
    if(strings.length >= 4) checkInstance.string4 = strings(3)
    checkInstance
  }

  // Filter out bugs
  def filter(filterFunction: (CheckInstance, CheckInstance) => Boolean): Unit = {
    bugList = bugList.filter(bugInstance => {
      !notBugList.exists(notBugInstance => filterFunction(bugInstance, notBugInstance))
    })
  }

  // Group bugs
  def group(hashFunction: CheckInstance => Int): Unit = {
    groupedBugList = new MHashMap[Int, ListBuffer[CheckInstance]]()
    for(checkInstance <- bugList) {
      val key = hashFunction(checkInstance)
      val value_BugList = groupedBugList.get(key) match {
        case Some(value) => value
        case None =>
          val newBugList = new ListBuffer[CheckInstance]()
          groupedBugList.put(key, newBugList)
          newBugList
      }
      value_BugList.append(checkInstance)
    }
  }
}
