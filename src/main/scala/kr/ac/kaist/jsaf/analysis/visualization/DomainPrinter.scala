/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.visualization

import kr.ac.kaist.jsaf.analysis.typing.Config
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object DomainPrinter {
  def printHeap(heap: Heap, cfg: CFG): JValue = {
    val printer = new DomainPrinter(Config.verbose)
    printer.toJHeap(heap, cfg)
  }

  def printContext(ctx: Context): JValue = {
    val printer = new DomainPrinter(Config.verbose)
    printer.toJContext(ctx)
  }
  
  def printValue(value: Value): JValue = {
    val printer = new DomainPrinter(Config.verbose)
    printer.toJValue(value)
  }

  def printLoc(l: Loc): JValue = {
    val printer = new DomainPrinter(Config.verbose)
    printer.toJLoc(l)
  }
}

private class DomainPrinter(verbose_lv: Int) {
  def toJHeap(heap: Heap, cfg: CFG):JValue  = {
    var locList:List[JValue] = List()
    for ((loc, obj) <- heap.map.toSeq.sortBy(_._1)) {
      // for non-verbose mode, locations for built-in are skipped.
      if ( verbose_lv == 3
        || (locToAddr(loc) >= locToAddr(CollapsedLoc) && !cfg.isHtmlAddr(locToAddr(loc)))
        || (verbose_lv == 1 && cfg.isHtmlAddr(locToAddr(loc)))
        || (verbose_lv == 2 && locToAddr(loc) < locToAddr(CollapsedLoc))) {
        if (verbose_lv >= 2 || locToAddr(loc) != locToAddr(GlobalLoc)) {
          locList ::= ("name"->toJLoc(loc))~("obj"->toJObj(obj, true))
        } else {
          locList ::= ("name"->toJLoc(loc))~("obj"->toJObj(obj, false))
        }
      }
    }
    locList
  }

  def toJContext(ctx: Context): JValue = {
    if (Config.verbose >= 2) {
      if (ctx._4 == null) {
        //("_1"->toJLocSet(ctx._1))~("_2"->toJLocSet(ctx._2))~("_3"->toJAddrSet(ctx._3))~("_4"->"TOP")
        ("_3"->toJAddrSet(ctx._3))~("_4"->"TOP")
      } else {
        //("_1"->toJLocSet(ctx._1))~("_2"->toJLocSet(ctx._2))~("_3"->toJAddrSet(ctx._3))~("_4"->toJAddrSet(ctx._4))
        ("_3"->toJAddrSet(ctx._3))~("_4"->toJAddrSet(ctx._4))
      }
    } else {
      //("_1"->toJLocSet(ctx._1))~("_2"->toJLocSet(ctx._2))
      ("_1"->"Bot")~("_2"->"Bot")
    }
  }
  

  def toJObj(obj: Obj, verbose: Boolean): JValue = {
    val map = obj.map
    //var objMap:MHashMap[String, JValue] = MHashMap()
    var propList:List[JValue] = List()
    
    for ((prop, (pv,abs)) <- map.toSeq.sortBy(_._1)) {
      val show = verbose match {
        case true => true
        case false => Config.testMode match {
          case true =>
            !Config.globalVerboseProp(prop) &&
            !Config.testModeProp.contains(prop)
          case false =>
            !Config.globalVerboseProp(prop)
        }
      }

      if (show) {
        var propValueMap:MHashMap[String, JValue] = MHashMap()
        propValueMap.update("propValue", toJPropValue(pv))
        propValueMap.update("absent", !abs.isBot)
        propList ::= ("name"->prop) ~ ("value"->propValueMap.toSeq.sortBy(_._1))
      }
    }
    propList
  }

  def toJPropValue(pv: PropValue): JValue = {
    var propValueMap:MHashMap[String, JValue] = MHashMap()

    val ov = pv._1
    if (ov != ObjectValueBot) {
      propValueMap.update("objValue", toJObjValue(ov))
    } else {
      propValueMap.update("objValue", "Bot")
    }

    val v = pv._2
    if (v != ValueBot) {
      propValueMap.update("value", toJValue(v))
    } else {
      propValueMap.update("value", "Bot")
    }

    val fun = pv._3
    if (fun != FunSetBot) {
      propValueMap.update("fid", toJFunSet(fun))
    } else {
      propValueMap.update("fid", "Bot")
    }
   propValueMap.toSeq.sortBy(_._1)
  }
  
  def toJObjValue(ov:ObjectValue): JValue = {
    var objValueMap:MHashMap[String, JValue] = MHashMap()
    objValueMap.update("value", toJValue(ov._1))
    objValueMap.update("writable", toJBool(ov._2))
    objValueMap.update("enumerable", toJBool(ov._3))
    objValueMap.update("configurable", toJBool(ov._4))
    objValueMap.toSeq.sortBy(_._1)
  }

  def toJValue(v: Value): JValue = {
    var valueMap:MHashMap[String, JValue] = MHashMap()
    
    if (v._1 != PValueBot) {
      valueMap.update("pvalue", v._1.toString)
    } else {
      valueMap.update("pvalue", "Bot")
    }
    
    if (v._2 != LocSetBot) {
      valueMap.update("locs", toJLocSet(v._2))
    } else {
      valueMap.update("locs", "Bot")
    }
    
    valueMap.toSeq.sortBy(_._1)
  }
  
  def toJLoc(loc: Loc): JValue = {    
    val str = isRecentLoc(loc) match {
      case true => "#"
      case false => "##"
    }
    //("str",str+locName(loc))~("addr",loc._1)
    str+locName(loc)
  }

  def toJAddrSet(set: AddrSet): JValue = {
    var addrList:List[JValue] = List()
    for (addr <- set.toSeq.sorted) {
      addrList ::= toJLoc(addrToLoc(addr, Recent))
    }
    addrList
  }

  def toJLocSet(set: LocSet): JValue = {
    var locList:List[JValue] = List()
    for (loc <- set.toSeq.sorted) {
      locList ::= toJLoc(loc)
    }
    locList
  }
  
  def toJFunSet(set: FunSet): JValue = {
    var funList:List[String] = List()
    for (fid <- set.toSeq.sorted) {
      funList ::= fid.toString
    }
    funList
  }

  def toJBool(b: AbsBool): String = {
    b.getPair match {
      case (AbsTop, _) => "T"
      case (AbsBot, _) => "B"
      case (AbsSingle, Some(v)) => if (v) "t" else "f"
      case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
    }
  }
}