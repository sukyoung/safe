/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import kr.ac.kaist.jsaf.analysis.cfg.InternalError
import kr.ac.kaist.jsaf.analysis.typing.Config
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

case class Obj(map: ObjMap) {
  /* partial order */
  def <= (that: Obj): Boolean = {
    if (this.map eq that.map) true
    else {
      val this_contained =
        if (Config.preAnalysis) {
          !(map.exists(this_kv => {
            val that_pva = that(this_kv._1)
            if (this_kv._1 == "@class") false
            else (this_kv._2._1 </ that_pva._1) || (this_kv._2._2 </ that_pva._2)
          }))
        }
        else {
          this.map.submapOf(that.map)
        }
          
      if (!this_contained) false
      else {
        val that_only = that.map.entryDiff(this.map)
        if (Config.preAnalysis) {
          !(that_only.exists(that_kv => {
            val this_pva = this(that_kv._1)
            if (that_kv._1 == "@class") false
            else (this_pva._1 </ that_kv._2._1) || (this_pva._2 </ that_kv._2._2)
          }))
        }
        else {
          !(that_only.exists(that_kv => {
            val this_pva = this(that_kv._1)
            (this_pva._1 </ that_kv._2._1) || (this_pva._2 </ that_kv._2._2)
          }))
        }
      }
    }
  }

  /* not a partial order */
  def </ (that: Obj): Boolean = {
    !(this <= that)
  }

  /* join */
  def + (that: Obj): Obj = {
    if (this.map eq that.map) this
    else if (this eq ObjBot) that
    else if (that eq ObjBot) this
    else {
      // choose smaller size map for iteration (x is the smaller one)
      val (x, y) = 
        if (this.map.size <= that.map.size) (this, that)
        else (that, this)

      // add entries of x to y
      val map1 = x.map.foldLeft(y.map)((m, kv) => {
        val y_pva = y(kv._1)
        m.updated(kv._1, (y_pva._1 + kv._2._1, y_pva._2 + kv._2._2))
      })

      // adjust entries existing in only y
      val y_only = y.map.entryDiff(x.map)
      val map2 =  
        if (y_only.size == 0) map1 
        else {
          val x_number = x.map("@default_number")._1
          val x_other = x.map("@default_other")._1

          // if x's @default has no value, just tag entries as possibly absent.  
          if ((x_number eq PropValueBot) && (x_other eq PropValueBot)) {
            y_only.foldLeft(map1)((m, kv) => {
              val k = kv._1
              if (kv._2._2 == AbsentTop) m
              else if (k.length > 0 && k.charAt(0) == '@') m
              else m.updated(kv._1, (kv._2._1, AbsentTop))
            })
          }
          
          // if x's @default has values, join them.
          else {
            y_only.foldLeft(map1)((m, kv) => {
              val k = kv._1
              if (k.length > 0 && k.charAt(0) == '@')
                m
              else if (AbsString.alpha(k) <= NumStr)
                m.updated(k, (kv._2._1 + x_number, AbsentTop))
              else   //AbsString.alpha(x) <= OtherStr
                m.updated(k, (kv._2._1 + x_other, AbsentTop))
            })              
          }
        }

      // return final result
      Obj(map2)
    }
  }

  /* meet */
  def <> (that: Obj): Obj = {
    if (this.map eq that.map) this
    else {
      val map1 = that.map.foldLeft(this.map)(
        (m, kv) => {
          val this_pva = this(kv._1)
          if(m.contains(kv._1))
            m + (kv._1 -> (this_pva._1 <> kv._2._1, this_pva._2 <> kv._2._2))
          else m - kv._1
        })
      val map2 = this.map.foldLeft(map1)(
        (m, kv) => {
          if (that.map.contains(kv._1)) m
          else m - kv._1
        })
      Obj(map2)
    }
  }

  def restrict(s: Set[String]) = {
    Obj(map.filter((kv) => kv._1.take(8).equals("@default") || s.contains(kv._1)))
  }

  def restrict_(s: Set[String]) = {
    Obj(map.filter((kv) => s.contains(kv._1)))
  }

  /* domain in: abstract string */
  def domIn(s: AbsString): AbsBool = {
    if (this.isBottom) BoolBot
    else s.gamma match {
      case Some(x) => domIn(x)
      case _ => s.getAbsCase match {
        case AbsTop =>
          if (this("@default_number")._1._1._1 </ ValueBot)
            BoolTop
          else if (this("@default_other")._1._1._1 </ ValueBot)
            BoolTop
          else {
            val pset = map.keySet.exists(x => !x.take(1).equals("@"))
            if (pset)
              BoolTop
            else
              BoolFalse
          }
        case AbsMulti =>
          if (s.isAllNums) {
            if (this("@default_number")._1._1._1 </ ValueBot)
              BoolTop
            else {
              val pset = map.keySet.exists(x => !x.take(1).equals("@") && AbsString.alpha(x) <= NumStr)
              if (pset)
                BoolTop
              else
                BoolFalse
            }
          } else {
            if (this("@default_other")._1._1._1 </ ValueBot)
              BoolTop
            else {
              val pset = map.keySet.exists(x => !x.take(1).equals("@") && AbsString.alpha(x) <= OtherStr)
              if (pset)
                BoolTop
              else
                BoolFalse
            }
          }
        /* TODO: exception? bottom? */
        case _ => BoolBot
      }
    }
  }
  def domIn(xs: Set[String]): AbsBool = xs.foldLeft[AbsBool](BoolBot)((r, x) => r + domIn(x))

  /* domain in: concrete string */
  def domIn(x: String): AbsBool = {
    if (this.isBottom) BoolBot
    else map.get(x) match {
      case Some(ox) =>
        if (ox._1 </ PropValueBot)
          if (AbsentTop </ ox._2)
            BoolTrue
          else
            BoolTop
        else // ox._1 <= PropValueBot
          if (x.take(1).equals("@"))
            BoolFalse
          else
            if (AbsString.alpha(x) <= NumStr)
              if (map("@default_number")._1._1._1 </ ValueBot)
                BoolTop
              else
                BoolFalse
            else // AbsString.alpha(x) <= OtherStr
              if (map("@default_other")._1._1._1 </ ValueBot)
                BoolTop
              else
                BoolFalse
      case None =>
        if (x.take(1).equals("@"))
            BoolFalse
        else
          if (AbsString.alpha(x) <= NumStr)
            if (map("@default_number")._1._1._1 </ ValueBot)
              BoolTop
            else
              BoolFalse
          else // AbsString.alpha(x) <= OtherStr
            if (map("@default_other")._1._1._1 </ ValueBot)
              BoolTop
            else
              BoolFalse
    }
  }

  /* domain: explicit membership without effects of @default */
  def dom(x: String): Boolean = map.contains(x)

  /* lookup */
  // by abstract property name
  def apply(s: AbsString): (PropValue, Absent) = {
    s.gamma match {
      case Some(x) =>
        this(x)
      case _ => s.getAbsCase match {
        case AbsMulti =>
          if (s.isAllNums) {
            // ignore internal properties
            val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= NumStr)
            val (propv1, abs1) = pset.foldLeft[(PropValue, Absent)]((PropValueBot, AbsentBot))(
              (_pa, x) => {
                val (propv, abs) = this(x)
                (_pa._1 + propv, _pa._2 + abs)})
            val (propv2, abs2) = this("@default_number")
            (propv1 + propv2, AbsentTop)
          } else {
            // ignore internal properties
            val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= OtherStr)
            val (propv1, abs1) = pset.foldLeft[(PropValue, Absent)]((PropValueBot, AbsentBot))(
              (_pa, x) => {
                val (propv, abs) = this(x)
                (_pa._1 + propv, _pa._2 + abs)})
            val (propv3, abs3) = this("@default_other")
            (propv1 + propv3, AbsentTop)
          }
        case AbsTop =>
          val pset = map.keySet.filter(x => !x.take(1).equals("@"))
          val (propv1, abs1) = pset.foldLeft[(PropValue, Absent)]((PropValueBot, AbsentBot))(
            (_pa, x) => {
              val (propv, abs) = this(x)
              (_pa._1 + propv, _pa._2 + abs)})
          val (propv2, abs2) = this("@default_number")
          val (propv3, abs3) = this("@default_other")
          (propv1 + propv2 + propv3, AbsentTop)
        case _ => (PropValueBot, AbsentBot)
      }
    }
  }

  /* lookup */
  // by concrete property name
  def apply(x: String): (PropValue, Absent) = {
    map.get(x) match {
      case Some(pva) => pva
      case None =>
        if (x.length > 0 && x.charAt(0) == '@')
          (PropValueBot, AbsentBot)
        else if (AbsString.alpha(x) <= NumStr)
          map("@default_number")
        else   //AbsString.alpha(x) <= OtherStr
          map("@default_other")
    }
  }

  def apply(xs: Set[String]): (PropValue, Absent) = {
    xs.foldLeft[(PropValue, Absent)]((PropValueBot, AbsentBot))((r, x) => {val (p, v) = this(x); (r._1 + p, r._2 + v)})
  }

  /* update */
  def update(s: AbsString, propv:PropValue): Obj = {
    if (this.isBottom) ObjBot
    else if (Config.preAnalysis) {
      s.gamma match {
        case Some(x) => // weak update
          // TODO: Wrong implementation (AbsentTop flags)
          update(x, propv, AbsentBot)
        case _ => s.getAbsCase match {
          case AbsMulti =>
            if (s.isAllNums) {
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= NumStr && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => m + (x -> (propv + m(x)._1, m(x)._2)))
              Obj(map1 + ("@default_number" -> ((propv + map1("@default_number")._1), AbsentTop)))
            } else {
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= OtherStr && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => m + (x -> (propv + m(x)._1, m(x)._2)))
              Obj(map1 + ("@default_other" -> ((propv + map1("@default_other")._1), AbsentTop)))
            }
            case AbsTop =>
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !x.take(1).equals("@") && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => m + (x -> (propv + m(x)._1, m(x)._2)))
              Obj(map1 + ("@default_number" -> ((propv + map1("@default_number")._1), AbsentTop),
                  "@default_other" -> ((propv + map1("@default_other")._1), AbsentTop)))
            case AbsBot => ObjBot
            case _ => throw new InternalError("impossible case.") 
        }
      }
    } else {
      s.gamma match {
        case Some(xs) => // strong update
          // TODO: Wrong implementation (AbsentTop flags)
          Obj(xs.foldLeft(map)((r, x) => r + (x -> (propv, AbsentBot))))
        case _ => s.getAbsCase match {
          case AbsMulti =>
            if (s.isAllNums) {
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= NumStr && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => m + (x -> (propv + m(x)._1, m(x)._2)))
              Obj(map1 + ("@default_number" -> ((propv + map1("@default_number")._1), AbsentTop)))
            } else {
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= OtherStr && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => m + (x -> (propv + m(x)._1, m(x)._2)))
              Obj(map1 + ("@default_other" -> ((propv + map1("@default_other")._1), AbsentTop)))
            }
            case AbsTop =>
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !x.take(1).equals("@") && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => m + (x -> (propv + m(x)._1, m(x)._2)))
              Obj(map1 + ("@default_number" -> ((propv + map1("@default_number")._1), AbsentTop),
                          "@default_other" -> ((propv + map1("@default_other")._1), AbsentTop)))
            case AbsBot => ObjBot
            case _ => throw new InternalError("impossible case.") 
        }
      }
    }
  }

  // absent value is always set to AbsentBot because it is strong update.
  // absent value is depending on a parameter when updates occur in the assert
  def update(x: String, propv: PropValue, abs: Absent = AbsentBot): Obj = {
    if (this.isBottom) ObjBot
    else if (Config.preAnalysis) {
      // weak update
      map.get(x) match {
        case None => Obj(map.updated(x, (propv, abs)))
        case Some(pva) => Obj(map.updated(x, (pva._1 + propv, pva._2 + abs)))
      }
    } else {
      Obj(map.updated(x, (propv, abs)))
    }
  }

  def update(xs: Set[String], propv: PropValue, abs: Absent): Obj = {
    xs.foldLeft(this)((r, x) => r + update(x, propv, abs))
  }

  /* weak update */
  /*
  def weakupdate(s: AbsString, propv:PropValue): Obj = {
    val locSet = propv._1._1._2.foldLeft(LocSetBot)((lset:LocSet, loc:Loc) => (lset ++ LocSet(loc._1, Recent)) ++ LocSet(loc._1, Old))
    val ov = ObjectValue(Value(locSet), BoolBot, BoolBot, BoolBot)
    val newPropv = propv + PropValue(ov)
    s match {
      case NumStrSingle(x) =>// weak update
        weakupdate(x, newPropv)
      case OtherStrSingle(x) => // weak update
        weakupdate(x, newPropv)
      case NumStr =>
        // ignore internal properties
        val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= NumStr)
        // weak update
        val map1 = pset.foldLeft(map)((m, x) => m + (x -> (newPropv + m(x)._1, m(x)._2)))
        Obj(map1 + ("@default_number" -> ((newPropv + map1("@default_number")._1), AbsentTop)))
      case OtherStr =>
        // ignore internal properties
        val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= OtherStr)
        // weak update
        val map1 = pset.foldLeft(map)((m, x) => m + (x -> (newPropv + m(x)._1, m(x)._2)))
        Obj(map1 + ("@default_other" -> ((newPropv + map1("@default_other")._1), AbsentTop)))
      case StrTop =>
        // ignore internal properties
        val pset = map.keySet.filter(x => !x.take(1).equals("@"))
        // weak update
        val map1 = pset.foldLeft(map)((m, x) => m + (x -> (newPropv + m(x)._1, m(x)._2)))
        Obj(map1 + ("@default_number" -> ((newPropv + map1("@default_number")._1), AbsentTop),
            "@default_other" -> ((newPropv + map1("@default_other")._1), AbsentTop)))
      /* TODO: exception? bottom? */
      case _ => this
    }
  }

  // absent value is always set to AbsentBot because it is strong update.
  // absent value is depending on a parameter when updates occur in the assert
  def weakupdate(x: String, propv: PropValue, abs: Absent = AbsentTop): Obj = {
    if(map.contains(x))
      Obj(map + (x -> (map(x)._1 + propv, map(x)._2 + abs)))
    else
      Obj(map + (x -> (propv, abs)))
  }
  */

  /* remove property: abstract string */
  def - (s: AbsString): Obj = {
    if (this.isBottom) ObjBot
    else s.getSingle match {
      case Some(x) =>
        Obj(map - x)
      case _ => s.getAbsCase match {
        case AbsMulti =>
          s.gamma match {
            case Some(vs) =>
              // TODO: Wrong implementation (AbsentTop flags)
              Obj(vs.foldLeft(map)((r, x) => r - x))
            case None =>
              if (s.isAllNums) {
                // ignore internal properties
                val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= NumStr && BoolTrue <= map(x)._1._1._4)
                Obj(pset.foldLeft(map)((m, x) => m + (x -> (m(x)._1, AbsentTop))))
              } else {
                // ignore internal properties
                val pset = map.keySet.filter(x => !x.take(1).equals("@") && AbsString.alpha(x) <= OtherStr && BoolTrue <= map(x)._1._1._4)
                Obj(pset.foldLeft(map)((m, x) => m + (x -> (m(x)._1, AbsentTop))))
              }
          }
        case AbsTop => // weak update
          // ignore internal properties
          val pset = map.keySet.filter(x => !x.take(1).equals("@") && BoolTrue <= map(x)._1._1._4)
          Obj(pset.foldLeft(map)((m, x) => m + (x -> (m(x)._1, AbsentTop))))
        case AbsBot => ObjBot
        case _ => throw new InternalError("impossible case.")
      }
    }
  }

  /* remove property: concrete string */
  def - (x: String): Obj = {
    if (this.isBottom) ObjBot
    else Obj(map - x)
  }

  /* substitute l_r by l_o */
  def subsLoc(l_r: Loc, l_o: Loc): Obj = {
    Obj(map.subsLoc(l_r, l_o))
  }

  /* weakly substitute l_r by l_o, this is keep l_r together */
  def weakSubsLoc(l_r: Loc, l_o: Loc): Obj = {
    Obj(map.weakSubsLoc(l_r, l_o))
  }
  
  /* get own property names without internal properties */
  def getProps: Set[String] = {
    map.keySet.filter(x => !x.take(1).equals("@"))
  }

  /* check whether this object is bottom object */
  def isBottom: Boolean = {
    // Physical equality suffices because object bottom occurs only with explicit ObjBot.
    // Note that concretization of object bottom is empty set of concrete objects,
    // which is not derivable from normal execution.
    this eq ObjBot  
  }
    
  /* for temporal pre-analysis result, make all the properties absentTop. */
  def absentTop() = {
    Obj(this.map.map((kv) => (kv._1 -> (kv._2._1, AbsentTop))))
  }

  /* to make old locations after preanalysis */
  def oldify(): Obj = {
    Obj(map.foldLeft(ObjMapBot)((objMap, data) => {
      val (s, old_o) = data
      val new_o =
        PropValue(ObjectValue(Value(old_o._1._1._1._1, oldifyLoc(old_o._1._1._1._2)),
                              old_o._1._1._2, old_o._1._1._3, old_o._1._1._4),
                  Value(old_o._1._2._1, oldifyLoc(old_o._1._2._2)),
                  old_o._1._3)
      objMap + (s -> (new_o, old_o._2))
    }))
  }

  // internal locations exist the only one in the heap
  private def oldifyLoc(locSet: LocSet): LocSet = {
    locSet.foldLeft(locSet)((lset, loc) => if(locToAddr(loc).toInt < 0) lset else lset + addrToLoc(locToAddr(loc), Old))
  }
}
