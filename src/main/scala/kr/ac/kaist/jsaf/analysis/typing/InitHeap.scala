/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolTrue => T, BoolFalse => F}
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, LEntry, InternalError}
import kr.ac.kaist.jsaf.{ShellParameters, Shell}
import kr.ac.kaist.jsaf.compiler.Predefined
import kr.ac.kaist.jsaf.analysis.typing.models.ModelManager

class InitHeap(cfg: CFG) {
  private var initPureLocalObj: Obj = null
  private var initHeap: Heap = HeapBot
  def getInitHeapPre() = {
    val initCP = ((cfg.getGlobalFId, LEntry), CallContext.globalCallContext)
    initHeap.update(cfg.getPureLocal(initCP), initPureLocalObj)
  }
  def getInitHeap() = {
    initHeap.update(SinglePureLocalLoc, initPureLocalObj)
  }

  def setInitHeap(newheap : Heap) = {
    initHeap = newheap
  }

  def initialize(): Unit = {
    // predefined global values for test mode
    val global0 = Config.testMode match {
      case false => ObjEmpty
      case true =>
        Config.testModeProp.foldLeft(ObjEmpty)((obj, kv) =>
          obj.update(AbsString.alpha(kv._1), PropValue(ObjectValue(kv._2, F, F, F))))
    }
    // predefined global values for library mode
    val global1 = Config.libMode match {
      case false => global0
      case true =>
        Config.libModeProp.foldLeft(global0)((obj, kv) =>
          obj.update(AbsString.alpha(kv._1), PropValue(ObjectValue(kv._2, F, F, F))))
    }

    // Set up global pure local object.
    // return statement is not allowed in global code.
    val globalPureLocal = Helper.NewPureLocal(Value(NullTop), GlobalSingleton) - "@return"
    initPureLocalObj = globalPureLocal

    val map1 = HeapMapBot.
      updated(GlobalLoc, global1).
      updated(SinglePureLocalLoc, globalPureLocal).
      updated(CollapsedLoc, ObjEmpty).
      updated(JSONObjTopLoc, JSONObjTop)

    // top object for library mode
    val map2 = Config.libMode match {
      case false => map1
      case true => map1.updated(LibModeObjTopLoc, LibModeObjTop)
    }
    /* add model objects to heap */
    initHeap = ModelManager.initialize(cfg, Heap(map2))
  }

  /**
   * Checks global variables added by the modeling against framework's predefined global list.
   * Hoister uses the list to ignore re-declaration of modeled variables.
   * If modeled variables are not included in the list, program will be terminated.
   * If listed variables are not modeled, only warning will be given.
   */
  def checkPredefined(): Unit = {
    // list of variables not yet implemented in the model.
    // these variables will be ignored in the checking.
    val notYetImplemented = Set[String]()

    val predef = if (Shell.pred != null) Shell.pred.all.toSet
                 else (new Predefined(new ShellParameters())).all.toSet
    var global = initHeap(GlobalLoc).getProps

    // ignore test mode variables
    if (Config.testMode) {
      global = global -- Config.testModeProp.keySet
    }

    // check that all modeled variables are include in the list
    val globalOnly = global -- predef
    if (globalOnly.size != 0 && !Config.domMode) {
      System.out.println("The following names are defined in the initial heap of analyzer");
      System.out.println("but not in the list of predefined names:")
      System.out.print("    ")
      globalOnly.foreach(x => System.out.print(x + " "))
      System.out.println()
      System.out.println("Update predefined names at jsaf.compiler.Predefind")
      throw new InternalError("Predefined names mismatch.")
    }

    // check that all listed variables are implemented in the model.
    val predefOnly = predef -- global -- notYetImplemented
    if (predefOnly.size != 0) {
      System.out.println("The following names are defined in the list of predefined names");
      System.out.println("but not in the initial heap of analyzer:")
      System.out.print("    ")
      predefOnly.foreach(x => System.out.print(x + " "))
      System.out.println()
      System.out.println("Update notYetImplemented at jsaf.typing.models.BuiltinModel")
      throw new InternalError("Predefined names mismatch.")
    }

    // check that notYetImplmented list is up-to-date.
    val implemented = notYetImplemented & global
    if (implemented.size != 0) {
      System.out.println("The following names are declared as not yet implemented in the model");
      System.out.println("but present in the initial heap of analyzer:")
      System.out.print("    ")
      implemented.foreach(x => System.out.print(x + " "))
      System.out.println()
      System.out.println("Update notYetImplemented at jsaf.typing.models.BuiltinModel")
      throw new InternalError("Implemented model status mismatch.")
    }
  }
}
