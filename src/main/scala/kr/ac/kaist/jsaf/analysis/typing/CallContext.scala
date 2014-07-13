/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import scala.collection.immutable.HashSet
import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import scala.collection.mutable.{HashMap => MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain.Obj
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap

/* Interface */
object CallContext {
  var globalCallContext: CallContext = null
  
  def initialize = Config.contextSensitivityMode match {
    case Config.Context_Insensitive => globalCallContext = Insensitive.globalCallContext
    case Config.Context_OneCallsite => globalCallContext = OneCallsite.globalCallContext
    case Config.Context_OneObject => globalCallContext = OneObject.globalCallContext
    case Config.Context_OneObjectTAJS => globalCallContext = OneObjectTAJS.globalCallContext
    case Config.Context_OneCallsiteAndObject => globalCallContext = OneCallsiteAndObject.globalCallContext
    case Config.Context_OneCallsiteOrObject => globalCallContext = OneCallsiteOrObject.globalCallContext
    case Config.Context_KCallsite => globalCallContext = KCallsite.globalCallContext
    case Config.Context_KCallsiteAndObject => globalCallContext = KCallsiteAndObject.globalCallContext
    case Config.Context_KCallsiteAndIdentity => globalCallContext = KCallsiteAndIdentity.globalCallContext
    case Config.Context_CallsiteSet => globalCallContext = CallsiteSet.globalCallContext
    case Config.Context_Identity => globalCallContext = Identity.globalCallContext
    case Config.Context_Loop => globalCallContext = Loop.globalCallContext
  }
  
  def getModeName = Config.contextSensitivityMode match {
    case Config.Context_Insensitive => Insensitive.getModeName
    case Config.Context_OneCallsite => OneCallsite.getModeName
    case Config.Context_OneObject => OneObject.getModeName
    case Config.Context_OneObjectTAJS => OneObjectTAJS.getModeName
    case Config.Context_OneCallsiteAndObject => OneCallsiteAndObject.getModeName
    case Config.Context_OneCallsiteOrObject => OneCallsiteOrObject.getModeName
    case Config.Context_KCallsite => KCallsite.getModeName
    case Config.Context_KCallsiteAndObject => KCallsiteAndObject.getModeName
    case Config.Context_KCallsiteAndIdentity => KCallsiteAndIdentity.getModeName
    case Config.Context_CallsiteSet => CallsiteSet.getModeName
    case Config.Context_Identity => Identity.getModeName
    case Config.Context_Loop => Loop.getModeName
  } 

  ////////////////////////////////////////////////////////////////////////////////
  // Context sensitivity mode flags
  ////////////////////////////////////////////////////////////////////////////////
  type SensitivityFlagType =                    Int

  val _INSENSITIVE:                             SensitivityFlagType = 0x00000000
  val _1_CALLSITE:                              SensitivityFlagType = 0x00000001
  val _1_OBJECT:                                SensitivityFlagType = 0x00000002
  val _IDENTITY:                                SensitivityFlagType = 0x00000004
  val _MOST_SENSITIVE:                          SensitivityFlagType = 0xFFFFFFFF
}

abstract class CallContext {
  // cfg: control flow graph
  // fid: callee FunctionId
  // l: environment(scope) of callee (call instruction id can be used to separate the callsite)
  // lset: this value of callee
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset_this: LocSet, l2: Option[Address] = None): Set[(CallContext, Obj)] = 
    NewCallContext(h, cfg, fid, l, lset_this)
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset_this: LocSet): Set[(CallContext, Obj)]
  def compare(that: CallContext): Int
  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext
  def toString2(): String = {""}
  def newLoopContext(n: Node, init: Boolean, cond: AbsBool, old_cond: AbsBool = BoolBot, isbreak: Boolean = false, isout: Boolean = false, isreturn: Boolean = false): (CallContext,  Boolean) = (null, false)
}


////////////////////////////////////////////////////////////////////////////////
// Context-insensitive
////////////////////////////////////////////////////////////////////////////////
private object Insensitive {
  val globalCallContext = Insensitive(GlobalCallsite)
  def getModeName = "Insensitive"
}

private case class Insensitive(builtin: Address) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    val obj_new = Helper.NewPureLocal(Value(LocSet(l)), lset)
    val cc_new = 
      if (cfg.isUserFunction(fid)) {
        Insensitive.globalCallContext
      } else {
        // additional 1-callsite context-sensitivity for built-in calls.
//        Insensitive.globalCallContext
        Insensitive(locToAddr(l))
      }
    HashSet((cc_new, obj_new))
  }
  
  def compare(other: CallContext): Int = {
    other match {
      case that: Insensitive => 
        if (this.builtin < that.builtin) -1
        else if (this.builtin > that.builtin) 1
        else 0
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = this

  override def toString = builtin.toString
}


////////////////////////////////////////////////////////////////////////////////
// 1-callsite call context
////////////////////////////////////////////////////////////////////////////////
private object OneCallsite {
  val globalCallContext = OneCallsite(GlobalCallsite, GlobalCallsite)
  def getModeName = "1-callsite"
}

private case class OneCallsite(addr: Address, builtin: Address) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    val obj_new = Helper.NewPureLocal(Value(LocSet(l)), lset)
    val cc_new = 
      if (cfg.isUserFunction(fid)) {
        OneCallsite(locToAddr(l), GlobalCallsite)
      } else {
        // additional 1-callsite context-sensitivity for built-in calls.
        OneCallsite(this.addr, locToAddr(l))
      }
    HashSet((cc_new, obj_new))
  }
  
  def compare(other: CallContext): Int = {
    other match {
      case that: OneCallsite => 
        if (this.addr < that.addr) -1
        else if (this.addr > that.addr) 1
        else {
          if (this.builtin < that.builtin) -1
          else if (this.builtin > that.builtin) 1
          else 0
        }
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = {
    if ((flag & CallContext._1_CALLSITE) != 0) this
    else new OneCallsite(GlobalCallsite, builtin)
  }

  override def toString = "(" + addr.toString + "," + builtin.toString + ")"
}


////////////////////////////////////////////////////////////////////////////////
// 1-object call context
////////////////////////////////////////////////////////////////////////////////
private object OneObject {
  val globalCallContext = OneObject(GlobalLoc, GlobalCallsite)
  def getModeName = "1-object"
}

private case class OneObject(loc: Loc, builtin: Address) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    val env_new = LocSet(l)
    if (cfg.isUserFunction(fid)) {
      lset.foldLeft(HashSet[(CallContext, Obj)]())((result, l_this) => {
        val this_new = if (l_this == GlobalLoc) this.loc else l_this
        val cc_new = OneObject(this_new, GlobalCallsite)
        val obj_new = Helper.NewPureLocal(Value(env_new), LocSet(l_this))
        result + ((cc_new, obj_new))
      })
    } else {
      // additional 1-callsite context-sensitivity for built-in calls.
      val cc_new = OneObject(this.loc, locToAddr(l))
      val obj_new = Helper.NewPureLocal(Value(env_new), lset)
      HashSet((cc_new, obj_new))
    }
  }
  
  def compare(other: CallContext): Int = {
    other match {
      case that: OneObject => 
        val loc_cmp = this.loc - that.loc
        if (loc_cmp != 0) loc_cmp
        else {
          if (this.builtin < that.builtin) -1
          else if (this.builtin > that.builtin) 1
          else 0
        }
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = {
    if ((flag & CallContext._1_OBJECT) != 0) this
    else new OneObject(GlobalLoc, builtin)
  }

  override def toString = "(" + DomainPrinter.printLoc(loc) + "," + builtin.toString + ")"
}


////////////////////////////////////////////////////////////////////////////////
// TAJS-style 1-object call context
////////////////////////////////////////////////////////////////////////////////
private object OneObjectTAJS {
  val globalCallContext = OneObjectTAJS(GlobalSingleton, GlobalCallsite)
  def getModeName = "1-object (TAJS)"
}

private case class OneObjectTAJS(lset: LocSet, builtin: Address) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    val obj_new = Helper.NewPureLocal(Value(LocSet(l)), lset)
    val cc_new = 
      if (cfg.isUserFunction(fid)) {
        OneObjectTAJS(lset, GlobalCallsite)
      } else {
        // additional 1-callsite context-sensitivity for built-in calls.
        OneObjectTAJS(this.lset, locToAddr(l))
      }
    HashSet((cc_new, obj_new))
  }

  def compare(other: CallContext): Int = {
    other match {
      case that: OneObjectTAJS => 
        val lset_cmp = this.lset.compare(that.lset)
        if (lset_cmp != 0) lset_cmp
        else {
          if (this.builtin < that.builtin) -1
          else if (this.builtin > that.builtin) 1
          else 0
        }
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = {
    if ((flag & CallContext._1_OBJECT) != 0) this
    else new OneObjectTAJS(LocSet(GlobalLoc), builtin)
  }

  override def toString = "({" + DomainPrinter.printLocSet(lset) + "}," + builtin.toString + ")"
}


////////////////////////////////////////////////////////////////////////////////
// (1-callsite and 1-object) call context
////////////////////////////////////////////////////////////////////////////////
private object OneCallsiteAndObject {
  val globalCallContext = OneCallsiteAndObject(GlobalCallsite, GlobalLoc, GlobalCallsite)
  def getModeName = "1-callsite and 1-object"
}

private case class OneCallsiteAndObject(addr: Address, loc: Loc, builtin: Address) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    // 1-callsite and 1-object
    val env_new = LocSet(l)
    if (cfg.isUserFunction(fid)) {
      lset.foldLeft(HashSet[(CallContext, Obj)]())((result, l_this) => {
        val this_new = if (l_this == GlobalLoc) this.loc else l_this
        val cc_new = OneCallsiteAndObject(locToAddr(l), this_new, GlobalCallsite)
        val obj_new = Helper.NewPureLocal(Value(env_new), LocSet(l_this))
        result + ((cc_new, obj_new))
      })
    } else {
      val cc_new = OneCallsiteAndObject(this.addr, this.loc, locToAddr(l))
      val obj_new = Helper.NewPureLocal(Value(env_new), lset)
      HashSet((cc_new, obj_new))
    }
  }

  def compare(other: CallContext): Int = {
    other match {
      case that: OneCallsiteAndObject =>
        val addr_cmp = this.addr - that.addr
        if (addr_cmp != 0) return addr_cmp

        val loc_cmp = this.loc - that.loc
        if (loc_cmp != 0) return loc_cmp

        val builtin_cmp = this.builtin - that.builtin
        if (builtin_cmp != 0) return builtin_cmp

        return 0
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = {
    if ((flag & CallContext._1_CALLSITE) != 0 && (flag & CallContext._1_OBJECT) != 0) this
    else {
      var addr: Address = GlobalCallsite
      var loc: Loc = GlobalLoc
      if ((flag & CallContext._1_CALLSITE) != 0) addr = locToAddr(this.addr)
      if ((flag & CallContext._1_OBJECT) != 0) loc = this.loc
      new OneCallsiteAndObject(addr, loc, GlobalCallsite)
    }
  }

  override def toString = "(" + addr.toString + "," + DomainPrinter.printLoc(loc) + "," + builtin.toString + ")"

  override def toString2: String = {
    val callsiteValue = "env = #" + locName(addrToLoc(addr, Recent))
    val thisValue = "this = #" + locName(loc)
    val builtinValue = "built-in = #" + locName(addrToLoc(builtin, Recent))
    "(" + callsiteValue + ", " + thisValue + ")"
  }
}


////////////////////////////////////////////////////////////////////////////////
// (1-callsite or 1-object) call context
////////////////////////////////////////////////////////////////////////////////
private object OneCallsiteOrObject {
  val globalCallContext = OneCallsiteOrObject(GlobalCallsite, GlobalLoc, GlobalCallsite)
  def getModeName = "1-callsite or 1-object"
}

private case class OneCallsiteOrObject(addr: Address, loc: Loc, builtin: Address) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    // 1-callsite or 1-object
    val env_new = LocSet(l)
    if (cfg.isUserFunction(fid)) {
      lset.foldLeft(HashSet[(CallContext, Obj)]())((result, l_this) => {
        if (l_this == GlobalLoc) {
          // 1-callsite
          val cc_new = OneCallsiteOrObject(locToAddr(l), this.loc, GlobalCallsite)
          val obj_new = Helper.NewPureLocal(Value(env_new), lset)
          result + ((cc_new, obj_new))
        }
        else {
          // 1-object
          val cc_new = OneCallsiteOrObject(GlobalCallsite, l_this, GlobalCallsite)
          val obj_new = Helper.NewPureLocal(Value(env_new), LocSet(l_this))
          result + ((cc_new, obj_new))
        }
      })
    } else {
      val cc_new = OneCallsiteOrObject(this.addr, this.loc, locToAddr(l))
      val obj_new = Helper.NewPureLocal(Value(env_new), lset)
      HashSet((cc_new, obj_new))
    }
  }

  def compare(other: CallContext): Int = {
    other match {
      case that: OneCallsiteOrObject =>
        val addr_cmp = this.addr - that.addr
        if (addr_cmp != 0) return addr_cmp

        val loc_cmp = this.loc - that.loc
        if (loc_cmp != 0) return loc_cmp

        val builtin_cmp = this.builtin - that.builtin
        if (builtin_cmp != 0) return builtin_cmp

        return 0
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = {
    if ((flag & CallContext._1_CALLSITE) != 0 && (flag & CallContext._1_OBJECT) != 0) this
    else {
      var addr: Address = GlobalCallsite
      var loc: Loc = GlobalLoc
      if ((flag & CallContext._1_CALLSITE) != 0) addr = locToAddr(this.addr)
      if ((flag & CallContext._1_OBJECT) != 0) loc = this.loc
      new OneCallsiteAndObject(addr, loc, GlobalCallsite)
    }
  }

  override def toString = "(" + addr.toString + "," + DomainPrinter.printLoc(loc) + "," + builtin.toString + ")"
}


////////////////////////////////////////////////////////////////////////////////
// k-callsite call context
////////////////////////////////////////////////////////////////////////////////
private object KCallsite {
  val globalCallContext = KCallsite(List())
  def getModeName = Config.contextSensitivityDepth.toString + "-callsite"
    
  /**
   * Lexicographic ordering of two Address Lists.
   */
  def compareList(x: List[Address], y: List[Address]): Int = {
    (x, y) match {
      case (Nil, Nil) => 0
      case (Nil, _) => -1
      case (_, Nil) => 1
      case (x1 :: xs, y1 :: ys) =>
        val cmp = x1 - y1
        if (cmp != 0) cmp
        else compareList(xs, ys)
    }
  } 
}

private case class KCallsite(callsiteList: List[Address]) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    val k = 
      if (cfg.isUserFunction(fid)) {
        Config.contextSensitivityDepth
      } else {
        // additional depth for built-in calls.
        Config.contextSensitivityDepth + 1
      }
    val newCallsiteList = (locToAddr(l) :: this.callsiteList).take(k)
    val newPureLocal = Helper.NewPureLocal(Value(LocSet(l)), lset)
    HashSet((KCallsite(newCallsiteList), newPureLocal))  
  }
  
  def compare(other: CallContext): Int = {
    other match {
      case that: KCallsite => KCallsite.compareList(this.callsiteList, that.callsiteList)
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  // TODO: apply appropriate filtering if necessary
  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = this

  override def toString = callsiteList.toString
}


////////////////////////////////////////////////////////////////////////////////
// loop context
////////////////////////////////////////////////////////////////////////////////
private object Loop {
  val globalCallContext = Loop(List(), List())
  def getModeName = "loop-sensitivity enabled and " + Config.contextSensitivityDepth.toString + "-callsite"
    
  /**
   * Lexicographic ordering of two Address Lists.
   */
  def compareList(x: List[Address], y: List[Address]): Int = {
    (x, y) match {
      case (Nil, Nil) => 0
      case (Nil, _) => -1
      case (_, Nil) => 1
      case (x1 :: xs, y1 :: ys) =>
        val cmp = x1 - y1
        if (cmp != 0) cmp
        else compareList(xs, ys)
    }
  } 
  
  def compareLoop(x: List[(List[Address], Node, Int)], y: List[(List[Address], Node, Int)]): Int = {
    (x, y) match {
      case (Nil, Nil) => 0
      case (Nil, _) => -1
      case (_, Nil) => 1
      case (x1 :: xs, y1 :: ys) =>
        val cmp = x1._3 - y1._3
        val cc = compareList(x1._1, y1._1)
        if (cc != 0) cc
        else if(cmp != 0) cmp
        else compareLoop(xs, ys)
    }
  } 

}

private case class Loop(loopList : List[(List[Address], Node, Int)], callsiteList: List[Address]) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    val k = 
      if (cfg.isUserFunction(fid)) {
        Config.contextSensitivityDepth
      } else {
        // additional depth for built-in calls.
        Config.contextSensitivityDepth + 1
      }
    val newCallsiteList = this.loopList match {
      case i::li if i._3 == 0 && k == Config.contextSensitivityDepth => this.callsiteList 
      case i::li if i._3 == 0 && k == Config.contextSensitivityDepth + 1 => 
        (locToAddr(l):: this.callsiteList).take(k)
      case _ => (locToAddr(l):: this.callsiteList).take(k)
    }
    
    val newPureLocal = Helper.NewPureLocal(Value(LocSet(l)), lset)
    HashSet((Loop(this.loopList, newCallsiteList), newPureLocal))  
  }

  override def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet, l2: Option[Address] = None): Set[(CallContext, Obj)] = {
    val k = 
      if (cfg.isUserFunction(fid)) {
        Config.contextSensitivityDepth
      } else {
        // additional depth for built-in calls.
        Config.contextSensitivityDepth + 1
      }
    val newCallsiteList = this.loopList match {
      //case i::li if i._3 == 0 && k == Config.contextSensitivityDepth => this.callsiteList 
      //case i::li if i._3 == 0 && k == Config.contextSensitivityDepth + 1 => 
      //  ((if(l2.isEmpty)locToAddr(l) else l2.get) :: this.callsiteList).take(k)
      case _ => ((if(l2.isEmpty)locToAddr(l) else l2.get) :: this.callsiteList).take(k)
    }
    
    val newPureLocal = Helper.NewPureLocal(Value(LocSet(l)), lset)
    HashSet((Loop(this.loopList, newCallsiteList), newPureLocal))  
  }

  val max_loopcontext = 10;
  val max_iter = 500;
  override def newLoopContext(n: Node, init: Boolean, cond: AbsBool, old_cond: AbsBool = BoolBot, isbreak: Boolean = false, isout: Boolean = false, isreturn: Boolean = false): (CallContext, Boolean) = {
    if(isout) {
      cond match {
        case BoolFalse => 
          val (loopContext, abstraction) = this.loopList match {
            case i::l if i._2 != n || this.callsiteList != i._1 => (this.loopList, true)
            case i::l if i._2 == n && this.callsiteList == i._1 => (l, false)
          //  case Nil =>(this.loopList, false)
            case _ => throw new InternalError("No available loop context")
          }
          (Loop(loopContext, this.callsiteList), abstraction)
        case BoolTop => 
          val loopContext = this.loopList match {
            case i::l if i._2 != n || this.callsiteList != i._1 => this.loopList
            case i::l if i._2 == n && this.callsiteList == i._1 => l
          //  case Nil => this.loopList
            case _ => throw new InternalError("No available loop context")
          }
          (Loop(loopContext, this.callsiteList), true)
        case _ => (this, false)
      }

    }
    else if(isreturn) {
      cond match {
        case BoolTrue | BoolFalse => 
          val loopContext = this.loopList.span(l => l._1 == this.callsiteList)._2
          (Loop(loopContext, this.callsiteList), false)
        case BoolTop => 
          val loopContext = this.loopList.span(l => l._1 == this.callsiteList)._2
          (Loop(loopContext, this.callsiteList), true)
        case _ => throw new InternalError("No possible match for loop return context")
      }
    }

    else if(isbreak) {
      cond match {
        case BoolTrue => 
          val (loopContext, abstraction) = this.loopList match {
            case i::l if i._2 != n || this.callsiteList != i._1 => (this.loopList, true)
            case i::l if i._2 == n && this.callsiteList == i._1 => (l, false)
            //case Nil => (this.loopList, true)
            case _ => throw new InternalError("No available loop context")
          }
          (Loop(loopContext, this.callsiteList), abstraction)
        case BoolTop => 
          val loopContext = this.loopList match {
            case i::l if i._2 != n || this.callsiteList != i._1 => this.loopList
            case i::l if i._2 == n && this.callsiteList == i._1 => l
            //case Nil => this.loopList
            case _ => throw new InternalError("No available loop context")
          }
          (Loop(loopContext, this.callsiteList), true)
        case _ => throw new InternalError("No possible match for loop break context")
      }
    }
    else if(init) {
      cond match {
        case BoolTrue =>
          
          if(this.loopList.size >= max_loopcontext){
          /*  val loopContext = this.loopList match {
              case i::l => l
              case _ => throw new InternalError("No available loop context")
            }
            (Loop(loopContext, this.callsiteList), true)
          */
            (this, true)
          }
          else {
            val (loopContext, abstraction) = this.loopList match {
              case i::l if i._3 == 0 => (this.loopList, true)
              case _ => ((this.callsiteList, n, 1)::this.loopList, false)
            }

            (Loop(loopContext, this.callsiteList), abstraction)

            //if(!(this.loopList.isEmpty) && this.loopList.head._1 != this.callsiteList && this.loopList.head._2 == 0)
            //  (this, true)
            //else
              //(Loop((this.callsiteList, n, 1)::this.loopList, this.callsiteList), false)
          }
        case BoolTop => 
          if(this.loopList.size >= max_loopcontext){
          /*
          val loopContext = this.loopList match {
              case i::l => l
              case _ => throw new InternalError("No available loop context")
            }
            (Loop(loopContext, this.callsiteList), true)
          }*/
            (this, true)
          }
          //if(this.loopList.size >= max_loopcontext)
          //  (this, true)
          else {
            //if(!(this.loopList.isEmpty) && this.loopList.head._1 != this.callsiteList && this.loopList.head._2 == 0)
            //  (this, true)
            //if(!(this.loopList.isEmpty) && this.loopList.head._1 != this.callsiteList)
            //  (this, true)
            //else
            val (loopContext, abstraction) = this.loopList match {
              case i::l if i._3 == 0 => (this.loopList, true)
              case _ => ((this.callsiteList, n, 0)::this.loopList, false)
            }
            (Loop(loopContext, this.callsiteList), abstraction)
            //(Loop(this.loopList, this.callsiteList), abstraction)
              //(Loop((this.callsiteList, n, 0)::this.loopList, this.callsiteList), true)
          }
        case _ => 
          if(this.loopList.size >= max_loopcontext){
            /*val loopContext = this.loopList match {
              case i::l => l
              case _ => throw new InternalError("No available loop context")
            }
            (Loop(loopContext, this.callsiteList), true)
          }*/
            (this, true)
          }
          //if(this.loopList.size >= max_loopcontext)
          //  (this, true)
          else {
            val (loopContext, abstraction) = this.loopList match {
              case i::l if i._3 == 0 => (this.loopList, true)
              case _ => ((this.callsiteList, n, 1)::this.loopList, false)
            }

            (Loop(loopContext, this.callsiteList), abstraction)
          }  //(Loop((this.callsiteList, n, 1)::this.loopList, this.callsiteList), false)
      }
    }
    // loop iteration
    else {
      old_cond match {
        case BoolTop => (this, true)
        case _ => cond match {
          case BoolTrue =>
            val (loopContext1, num, abstraction)  = this.loopList match {
              case i::l if i._2 == n && this.callsiteList == i._1 => ((i._1, n, i._3+1)::l, i._3, false)
              case i::l if i._2 != n || this.callsiteList != i._1=> (this.loopList, i._3, true)
              //case Nil => (this.loopList, 0, true)
              case _ => throw new InternalError("No available loop context")
            }
            if(num >= max_iter) (this,true)
            //if(num >= max_iter) (Loop(loopContext2, this.callsiteList) ,true)
            //else if(num==0) (this, true)
            else (Loop(loopContext1, this.callsiteList), abstraction)
          case BoolFalse =>
            val (loopContext1, num, abstraction)  = this.loopList match {
              case i::l if i._2 == n && this.callsiteList == i._1 => ((i._1, n, i._3+1)::l, i._3, false)
              case i::l if i._2 != n || this.callsiteList != i._1 => (this.loopList, i._3, true)
              //case Nil => (this.loopList, 0, true)
              case _ => throw new InternalError("No available loop context")
            }
            (Loop(loopContext1, this.callsiteList), abstraction)
          case BoolTop =>
            (this, true)
          case _ => (this, true)
        }
      }
   }
 }

  
  def compare(other: CallContext): Int = {
    other match {
      case that: Loop => 
        val callcompare = Loop.compareList(this.callsiteList, that.callsiteList) 
        val loopcompare = Loop.compareLoop(this.loopList, that.loopList)
        if(callcompare == 0 && loopcompare == 0) 0
        else if(callcompare != 0) callcompare
        else loopcompare
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  // TODO: apply appropriate filtering if necessary
  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = this

  override def toString = "Loop : " + loopList.toString + ", Call: " + callsiteList.toString
}



////////////////////////////////////////////////////////////////////////////////
// k-callsite and 1-object call context
////////////////////////////////////////////////////////////////////////////////
private object KCallsiteAndObject {
  val globalCallContext = KCallsiteAndObject(List(), GlobalLoc)
  def getModeName = Config.contextSensitivityDepth.toString + "-callsite and 1-object"
}

private case class KCallsiteAndObject(callsiteList: List[Address], thisLoc: Loc) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    val k =
      if (cfg.isUserFunction(fid)) {
        Config.contextSensitivityDepth
      } else {
        // additional depth for built-in calls.
        Config.contextSensitivityDepth + 1
      }
    val newCallsiteList = (locToAddr(l) :: this.callsiteList).take(k)
    val newEnv = LocSet(l)

    lset.foldLeft(HashSet[(CallContext, Obj)]())((result, l_this) => {
      val newPureLocal = Helper.NewPureLocal(Value(newEnv), LocSet(l_this))
      val newThisLoc = if (l_this == GlobalLoc) this.thisLoc else l_this
      val newCallContext = KCallsiteAndObject(newCallsiteList, newThisLoc)
      result + ((newCallContext, newPureLocal))
    })
  }

  def compare(other: CallContext): Int = {
    other match {
      case that: KCallsiteAndObject =>
        val callsite_cmp = KCallsite.compareList(this.callsiteList, that.callsiteList)
        if (callsite_cmp != 0) callsite_cmp
        else this.thisLoc - that.thisLoc
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  // TODO: apply appropriate filtering if necessary
  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = this

  override def toString = "(" + callsiteList.toString + ", " + DomainPrinter.printLoc(thisLoc) + ")"
}


////////////////////////////////////////////////////////////////////////////////
// callsite-set call context
////////////////////////////////////////////////////////////////////////////////
private object CallsiteSet {
  val globalCallContext = CallsiteSet(LocSetBot)
  def getModeName = "callsite-set"
}

private case class CallsiteSet(callsiteSet: LocSet) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    val newPureLocal = Helper.NewPureLocal(Value(LocSet(l)), lset)
    val newCallsiteSet = this.callsiteSet + locToAddr(l)
    HashSet((CallsiteSet(newCallsiteSet), newPureLocal))  
  }
  
  def compare(other: CallContext): Int = {
    other match {
      case that: CallsiteSet => this.callsiteSet.compare(that.callsiteSet)
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  // TODO: apply appropriate filtering if necessary
  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = this

  override def toString = callsiteSet.toString
}

private object KCallsiteAndIdentity {
  val globalCallContext = KCallsiteAndIdentity(List(), Identity.globalIdentity)
  def getModeName = Config.contextSensitivityDepth.toString + "-callsite and identity"
}

private case class KCallsiteAndIdentity(callsiteList: List[Address], thisLoc: Loc) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    val k =
      if (cfg.isUserFunction(fid)) {
        Config.contextSensitivityDepth
      } else {
        // additional depth for built-in calls.
        Config.contextSensitivityDepth + 1
      }
    val newCallsiteList = (locToAddr(l) :: this.callsiteList).take(k)
    val newEnv = LocSet(l)

    val idmap = lset.foldLeft(HashMap[Int, LocSet]())((map, l) => {
      val id = Identity.makeIdentity(h, l)
      map.get(id) match {
        case Some(s) => map + (id -> (s + l))
        case None => map + (id -> LocSet(l))
      }
    })
    idmap.foldLeft(HashSet[(CallContext, Obj)]())((result, idlset) => {
      val id = idlset._1
      val lset_this = idlset._2

      val newPureLocal = Helper.NewPureLocal(Value(newEnv), lset_this)
      val newCallContext = KCallsiteAndIdentity(newCallsiteList, id)
      result + ((newCallContext, newPureLocal))
    })
  }

  def compare(other: CallContext): Int = {
    other match {
      case that: KCallsiteAndIdentity =>
        val callsite_cmp = KCallsite.compareList(this.callsiteList, that.callsiteList)
        if (callsite_cmp != 0) callsite_cmp
        else this.thisLoc - that.thisLoc
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  // TODO: apply appropriate filtering if necessary
  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = this

  override def toString = "(" + callsiteList.toString + ", " + DomainPrinter.printLoc(thisLoc) + ")"
}

private object Identity {
  val globalIdentity = 0
  val globalCallContext = Identity(GlobalCallsite, globalIdentity, GlobalCallsite)
  def getModeName = "1-callsite or identity"

  var identityID = 0
  def newIdentityID(): Int = {
    identityID += 1
    identityID
  }

  val IDMap: MHashMap[Set[List[String]], Int] = MHashMap()
  val hashcons: MHashMap[(List[String], String), List[String]] = MHashMap()
  def cons(p: String, prefix: List[String]) : List[String] = {
    hashcons.get((prefix, p)) match {
      case Some(s) => s
      case None =>
        val newlist = p::prefix
        hashcons += (prefix, p) -> newlist
        newlist
    }
  }

  def visibleProps(h: Heap, loc: Loc, prefix: List[String], visitedProps: Set[String], sets: Set[List[String]]): Set[List[String]] = {
    val o = h(loc)
    val currentProps = o.getProps
    val newProps = currentProps -- visitedProps
    val newVisitedProps = visitedProps ++ currentProps.filter(p => BoolTrue == o.domIn(p))

    val lset_proto = h(loc)("@proto")._1._1._1._2

    val restSets = lset_proto.foldLeft(sets)((sset, l_proto) => {
      visibleProps(h, l_proto, cons("@", prefix), newVisitedProps, sset)
    })

    newProps.foldLeft(restSets)((s, p) => {
      s + cons(p, prefix)
    })
  }

  def makeIdentity(h: Heap, loc: Loc): Int = {
    if (loc == GlobalLoc) 0
    val key = visibleProps(h, loc, List[String](""), HashSet[String](), HashSet[List[String]]())

//    System.out.println("* Identity")
//    key.foreach(s => System.out.println(s))
    val id = IDMap.get(key) match {
      case Some(i) => i
      case None =>
        val newID = newIdentityID()
        IDMap += (key -> newID)
        newID
    }
//    System.out.println(" IDValue: "+id)
    id
  }
}

private case class Identity(addr: Address, loc: Loc, builtin: Address) extends CallContext {
  def NewCallContext(h: Heap, cfg: CFG, fid: FunctionId, l: Loc, lset: LocSet): Set[(CallContext, Obj)] = {
    // 1-callsite or identity
    val env_new = LocSet(l)
    if (cfg.isUserFunction(fid)) {
      val id_lset = lset.filter(l => l != GlobalLoc)

      // identity
      val idmap = id_lset.foldLeft(HashMap[Int, LocSet]())((map, l) => {
        val id = Identity.makeIdentity(h, l)
        map.get(id) match {
          case Some(s) => map + (id -> (s + l))
          case None => map + (id -> LocSet(l))
        }
      })
      val cset_1 = idmap.foldLeft(HashSet[(CallContext, Obj)]())((result, idlset) => {
        val id = idlset._1
        val lset = idlset._2
        val cc_new = Identity(GlobalCallsite, id, GlobalCallsite)
        val obj_new = Helper.NewPureLocal(Value(env_new), lset)
        result + ((cc_new, obj_new))
      })

      // 1-callsite
      val cset_2 = lset.foldLeft(HashSet[(CallContext, Obj)]())((result, l_this) => {
        if (l_this == GlobalLoc) {
          val cc_new = Identity(locToAddr(l), this.loc, GlobalCallsite)
          val obj_new = Helper.NewPureLocal(Value(env_new), lset)
          result + ((cc_new, obj_new))
        }
        else result
      })

      cset_1 ++ cset_2
    } else {
      val cc_new = Identity(this.addr, this.loc, locToAddr(l))
      val obj_new = Helper.NewPureLocal(Value(env_new), lset)
      HashSet((cc_new, obj_new))
    }
  }

  def compare(other: CallContext): Int = {
    other match {
      case that: Identity =>
        val addr_cmp = this.addr - that.addr
        if (addr_cmp != 0) return addr_cmp

        val loc_cmp = this.loc - that.loc
        if (loc_cmp != 0) return loc_cmp

        val builtin_cmp = this.builtin - that.builtin
        if (builtin_cmp != 0) return builtin_cmp

        return 0
      case _ => throw new InternalError("compare must be called on same CallContext kinds")
    }
  }

  def filterSensitivity(flag: CallContext.SensitivityFlagType): CallContext = {
    if ((flag & CallContext._1_CALLSITE) != 0 && (flag & CallContext._IDENTITY) != 0) this
    else {
      var addr: Address = GlobalCallsite
      var loc: Loc = GlobalLoc
      if ((flag & CallContext._1_CALLSITE) != 0) addr = locToAddr(this.addr)
      if ((flag & CallContext._IDENTITY) != 0) loc = this.loc
      new Identity(addr, loc, GlobalCallsite)
    }
  }

  override def toString = "(" + addr.toString + "," + DomainPrinter.printLoc(loc) + "," + builtin.toString + ")"
}

