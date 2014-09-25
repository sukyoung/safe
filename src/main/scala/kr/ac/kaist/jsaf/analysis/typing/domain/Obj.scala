/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import kr.ac.kaist.jsaf.analysis.cfg.InternalError
import kr.ac.kaist.jsaf.analysis.lib.ObjTreeMap
import kr.ac.kaist.jsaf.analysis.typing.{NotYetImplemented, Config}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

import scala.collection.mutable

class Obj(_map: ObjMap) {
  private val map = _map

  /* partial order */
  def <= (that: Obj): Boolean = {
    if (this.map eq that.map) true
    else {
      Obj.cacheLookup(this.map, that.map) match {
        case Some(b) => b
        case None =>
          val rtn = {
            val this_contained =
              if (Config.preAnalysis) {
                !map.exists(this_kv => {
                  val that_pva = that.lookup(this_kv._1)
                  if (this_kv._1 == "@class") false
                  else (this_kv._2._1 </ that_pva._1) || (this_kv._2._2 </ that_pva._2)
                })
              }
              else {
                this.map.submapOf(that.map)
              }

            if (!this_contained) false
            else {
              val that_only = that.map.entryDiff(this.map)
              if (Config.preAnalysis) {
                !that_only.exists(that_kv => {
                  val this_pva = this.lookup(that_kv._1)
                  if (that_kv._1 == "@class") false
                  else (this_pva._1 </ that_kv._2._1) || (this_pva._2 </ that_kv._2._2)
                })
              }
              else {
                !that_only.exists(that_kv => {
                  val this_pva = this.lookup(that_kv._1)
                  (this_pva._1 </ that_kv._2._1) || (this_pva._2 </ that_kv._2._2)
                })
              }
            }
          }

          Obj.cacheUpdate(this.map, that.map, rtn)
          rtn
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
    else if (this eq Obj.bottom) that
    else if (that eq Obj.bottom) this
    else if (this <= that) that
    else if (that <= this) this
    else {
      // choose smaller size map for iteration (x is the smaller one)
      val (x, y) = 
        if (this.map.size <= that.map.size) (this, that)
        else (that, this)

      val number = x.map(Str_default_number)._1 + y.map(Str_default_number)._1
      val other = x.map(Str_default_other)._1 + y.map(Str_default_other)._1

      // add entries of x to y
      val map1 = x.map.foldLeft(y.map)((m, kv) => {
        val y_pva = y.lookup(kv._1)
        val pva = y_pva._1 + kv._2._1
        val abs = y_pva._2 + kv._2._2
        val key = AbsString.alpha(kv._1)
        if (!(kv._1.take(1) == "@") && AbsentTop <= abs) {
          if (key <= NumStr && pva <= number) m - kv._1
          else if (key <= OtherStr && pva <= other) m - kv._1
          else m.updated(kv._1, (pva, abs))
        } else {
          m.updated(kv._1, (pva, abs))
        }
      })

      // adjust entries existing in only y
      val y_only = y.map.entryDiff(x.map)
      val map2 =  
        if (y_only.size == 0) map1 
        else {
          val x_number = x.map(Str_default_number)._1
          val x_other = x.map(Str_default_other)._1

          // if x's @default has no value, just tag entries as possibly absent.  
          if ((x_number eq PropValueBot) && (x_other eq PropValueBot)) {
            y_only.foldLeft(map1)((m, kv) => {
              val k = kv._1
              val ak = AbsString.alpha(kv._1)
              if (k.take(1) == "@") m
              else if (ak <= NumStr && kv._2._1 <= number) m - kv._1
              else if (ak <= OtherStr && kv._2._1 <= other) m - kv._1
              else if (AbsentTop <= kv._2._2) m
              else m.absentTop(kv._1)
            })
          }
          
          // if x's @default has values, join them.
          else {
            y_only.foldLeft(map1)((m, kv) => {
              val k = kv._1
              if (k.take(1) == "@") m
              else if (AbsString.alpha(k) <= NumStr) {
                val pva = kv._2._1 + x_number
                if (pva <= number) m - k
                else m.updated(k, (pva, AbsentTop))
              } else {
                val pva = kv._2._1 + x_other
                if (pva <= other) m - k
                else m.updated(k, (pva, AbsentTop))
              }
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
      val map1 = that.map.foldLeft(this.map)((m, kv) => {
          val this_pva = this.lookup(kv._1)
          if(m.contains(kv._1))
            m + (kv._1 -> (this_pva._1 <> kv._2._1, this_pva._2 <> kv._2._2))
          else m - kv._1
        })
      val map2 = this.map.foldLeft(map1)((m, kv) => {
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
          if (this.map(Str_default_number)._1._1._1 </ ValueBot) BoolTop
          else if (this.map(Str_default_other)._1._1._1 </ ValueBot) BoolTop
          else {
            val pset = map.keySet.exists(x => !(x.take(1) == "@"))
            if (pset) BoolTop
            else BoolFalse
          }
        case AbsMulti =>
          if (s.isAllNums) {
            if (this.map(Str_default_number)._1._1._1 </ ValueBot) BoolTop
            else {
              val pset = map.keySet.exists(x => !(x.take(1) == "@") && AbsString.alpha(x) <= NumStr)
              if (pset) BoolTop
              else BoolFalse
            }
          } else {
            if (this.map(Str_default_other)._1._1._1 </ ValueBot) BoolTop
            else {
              val pset = map.keySet.exists(x => !(x.take(1) == "@") && AbsString.alpha(x) <= OtherStr)
              if (pset) BoolTop
              else BoolFalse
            }
          }
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
          if (AbsentTop </ ox._2 || x.take(1) == "@") BoolTrue
          else BoolTop
        else // ox._1 <= PropValueBot
          if (x.take(1) == "@") BoolFalse
          else
            if (AbsString.alpha(x) <= NumStr)
              if (map(Str_default_number)._1._1._1 </ ValueBot) BoolTop
              else BoolFalse
            else // AbsString.alpha(x) <= OtherStr
              if (map(Str_default_other)._1._1._1 </ ValueBot) BoolTop
              else BoolFalse
      case None =>
        if (x.take(1) == "@") BoolFalse
        else
          if (AbsString.alpha(x) <= NumStr)
            if (map(Str_default_number)._1._1._1 </ ValueBot) BoolTop
            else BoolFalse
          else // AbsString.alpha(x) <= OtherStr
            if (map(Str_default_other)._1._1._1 </ ValueBot) BoolTop
            else BoolFalse
    }
  }

  /* domain: explicit membership without effects of @default */
  def dom(x: String): Boolean = map.contains(x)

  def apply(s: AbsString): PropValue = {
    s.gamma match {
      case Some(xs) =>
        xs.foldLeft[PropValue](PropValueBot)((r, x) => r + this(x))
      case _ => s.getAbsCase match {
        case AbsMulti =>
          if (s.isAllNums) {
            // ignore internal properties
            val pset = map.keySet.filter(x => !(x.take(1) == "@") && AbsString.alpha(x) <= NumStr)
            val propv1 = pset.foldLeft[PropValue](PropValueBot)((_pv, x) => _pv + this(x))
            val propv2 = this.map(Str_default_number)._1
            propv1 + propv2
          } else {
            // ignore internal properties
            val pset = map.keySet.filter(x => !(x.take(1) == "@") && AbsString.alpha(x) <= OtherStr)
            val propv1 = pset.foldLeft[PropValue](PropValueBot)((_pv, x) => _pv + this(x))
            val propv3 = this.map(Str_default_other)._1
            propv1 + propv3
          }
        case AbsTop =>
          val pset = map.keySet.filter(x => !(x.take(1) == "@"))
          val propv1 = pset.foldLeft[PropValue](PropValueBot)((_pv, x) => _pv + this(x))
          val propv2 = this.map(Str_default_number)._1
          val propv3 = this.map(Str_default_other)._1
          propv1 + propv2 + propv3
        case _ => PropValueBot
      }
    }
  }

  def apply(s: String): PropValue = {
    map.get(s) match {
      case Some(pva) => pva._1
      case None =>
        if (s.take(1) == "@")
          PropValueBot
        else if (AbsString.alpha(s) <= NumStr)
          map(Str_default_number)._1
        else   //AbsString.alpha(x) <= OtherStr
          map(Str_default_other)._1
    }
  }

  /* lookup */
  private def lookup(x: String): (PropValue, Absent) = {
    map.get(x) match {
      case Some(pva) => pva
      case None =>
        if (x.take(1) == "@")
          (PropValueBot, AbsentBot)
        else if (AbsString.alpha(x) <= NumStr)
          map(Str_default_number)
        else   //AbsString.alpha(x) <= OtherStr
          map(Str_default_other)
    }
  }

  /* update */
  def update(s: AbsString, propv:PropValue): Obj = {
    if (this.isBottom) Obj.bottom
    else if (Config.preAnalysis) {
      s.gamma match {
        case Some(xs) => // weak update
          xs.foldLeft(this)((r, x) => r + update(x, propv))
        case _ => s.getAbsCase match {
          case AbsMulti =>
            if (s.isAllNums) {
              val number = map(Str_default_number)._1 + propv
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !(x.take(1) == "@") && AbsString.alpha(x) <= NumStr && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => {
                val pva = propv + m(x)._1
                val abs = m(x)._2
                if (AbsentTop <= abs && pva <= number) m - x
                else m + (x -> (pva, abs))
              })
              Obj(map1 + (Str_default_number -> (number, AbsentTop)))
            } else {
              val other = map(Str_default_other)._1 + propv
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !(x.take(1) == "@") && AbsString.alpha(x) <= OtherStr && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => {
                val pva = propv + m(x)._1
                val abs = m(x)._2
                if (AbsentTop <= abs && pva <= other) m - x
                else m + (x -> (pva, abs))
              })
              Obj(map1 + (Str_default_other -> (other, AbsentTop)))
            }
            case AbsTop =>
              val number = map(Str_default_number)._1 + propv
              val other = map(Str_default_other)._1 + propv
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !(x.take(1) == "@") && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => {
                val ax = AbsString.alpha(x)
                val pva = propv + m(x)._1
                val abs = m(x)._2
                if (AbsentTop <= abs && ax <= NumStr && pva <= number) m - x
                else if (AbsentTop <= abs && ax <= OtherStr && pva <= other) m - x
                else m + (x -> (pva, abs))
              })
              Obj(map1 + (Str_default_number -> (number, AbsentTop),
                  Str_default_other -> (other, AbsentTop)))
            case AbsBot => Obj.bottom
            case _ => throw new InternalError("impossible case.") 
        }
      }
    } else {
      s.gamma match {
        case Some(xs) =>
         if (xs.size == 1) // strong update
           Obj(map.updated(xs.head, (propv, AbsentBot)))
          else // weak update
           xs.foldLeft(this)((r, x) => r + update(x, propv))
        case _ =>
          s.getAbsCase match {
          case AbsMulti =>
            if (s.isAllNums) {
              val number = map(Str_default_number)._1 + propv
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !(x.take(1) == "@") && AbsString.alpha(x) <= NumStr && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => {
                val pva = propv + m(x)._1
                val abs = m(x)._2
                if (AbsentTop <= abs && pva <= number) m - x
                else m + (x -> (pva, abs))
              })
              Obj(map1 + (Str_default_number -> (number, AbsentTop)))
            } else {
              val other = map(Str_default_other)._1 + propv
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !(x.take(1) == "@") && AbsString.alpha(x) <= OtherStr && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => {
                val pva = propv + m(x)._1
                val abs = m(x)._2
                if (AbsentTop <= abs && pva <= other) m - x
                else m + (x -> (pva, abs))
              })
              Obj(map1 + (Str_default_other -> (other, AbsentTop)))
            }
            case AbsTop =>
              val number = map(Str_default_number)._1 + propv
              val other = map(Str_default_other)._1 + propv
              // ignore internal or non-writable properties
              val pset = map.keySet.filter(x => !(x.take(1) == "@") && BoolTrue <= map(x)._1._1._2)
              // weak update
              val map1 = pset.foldLeft(map)((m, x) => {
                val ax = AbsString.alpha(x)
                val pva = propv + m(x)._1
                val abs = m(x)._2
                if (AbsentTop <= abs && ax <= NumStr && pva <= number) m - x
                else if (AbsentTop <= abs && ax <= OtherStr && pva <= other) m - x
                else m + (x -> (pva, abs))
              })
              Obj(map1 + (Str_default_number -> (number, AbsentTop),
                          Str_default_other -> (other, AbsentTop)))
            case AbsBot => Obj.bottom
            case _ => throw new InternalError("impossible case.") 
        }
      }
    }
  }

  // absent value is set to AbsentBot because it is strong update.
  def update(x: String, propv: PropValue, exist: Boolean = false): Obj = {
    if (this.isBottom) Obj.bottom
    else if (Config.preAnalysis) {
      // weak update
      map.get(x) match {
        case None =>
          val ax = AbsString.alpha(x)
          val number = map(Str_default_number)._1
          val other = map(Str_default_other)._1
          if (ax <= NumStr && propv <= number) this
          else if (ax <= OtherStr && propv <= other) this
          else if (exist) Obj(map.updated(x, (propv, AbsentBot)))
          else Obj(map.updated(x, (propv, AbsentTop)))
        case Some(pva) =>
          Obj(map.updated(x, (pva._1 + propv, pva._2)))
      }
    } else {
      if (x.startsWith("@default"))
        Obj(map.updated(x, (propv, AbsentTop)))
      else
        Obj(map.updated(x, (propv, AbsentBot)))
    }
  }

  /* remove property: abstract string */
  def - (s: AbsString): Obj = {
    if (this.isBottom) Obj.bottom
    else s.getSingle match {
      case Some(x) =>
        Obj(map - x)
      case _ =>
        val number = map(Str_default_number)._1
        val other = map(Str_default_other)._1
        s.getAbsCase match {
        case AbsMulti =>
          s.gamma match {
            case Some(vs) =>
              Obj(vs.foldLeft(map)((r, x) => {
                val ax = AbsString.alpha(x)
                val pv = r.get(x)
                pv match {
                    case Some(pvp) =>
                        if (ax <= NumStr && pvp._1 <= number) r - x
                        else if (ax <= OtherStr && pvp._1 <= other) r - x
                        else r.absentTop(x)
                    case None => r
                }
              }))
            case None =>
              if (s.isAllNums) {
                // ignore internal properties
                val pset = map.keySet.filter(x => !(x.take(1) == "@") && AbsString.alpha(x) <= NumStr && BoolTrue <= map(x)._1._1._4)
                Obj(pset.foldLeft(map)((m, x) => {
                  if (m(x)._1 <= number) m - x
                  else m.absentTop(x)
                }))
              } else {
                // ignore internal properties
                val pset = map.keySet.filter(x => !(x.take(1) == "@") && AbsString.alpha(x) <= OtherStr && BoolTrue <= map(x)._1._1._4)
                Obj(pset.foldLeft(map)((m, x) => {
                  if (m(x)._1 <= other) m - x
                  else m.absentTop(x)
                }))
              }
          }
        case AbsTop => // weak update
          // ignore internal properties
          val pset = map.keySet.filter(x => !(x.take(1) == "@") && BoolTrue <= map(x)._1._1._4)
          Obj(pset.foldLeft(map)((m, x) => {
            val pv = m(x)._1
            val ax = AbsString.alpha(x)
            if (ax <= NumStr && pv <= number) m - x
            else if (ax <= OtherStr && pv <= other) m - x
            else m.absentTop(x)
          }))
        case AbsBot => Obj.bottom
        case _ => throw new InternalError("impossible case.")
      }
    }
  }

  /* remove property: concrete string */
  def - (x: String): Obj = {
    if (this.isBottom) Obj.bottom
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
    map.keySet.filter(x => !(x.take(1) == "@"))
  }

  /* get own property names including internal properties */
  def getAllProps: Set[String] = {
    map.keySet
  }

  /* check whether this object is bottom object */
  def isBottom: Boolean = {
    // Physical equality suffices because object bottom occurs only with explicit Obj.bottom.
    // Note that concretization of object bottom is empty set of concrete objects,
    // which is not derivable from normal execution.
    this eq Obj.bottom
  }
    
  /* for temporal pre-analysis result, make all the properties absentTop. */
  def absentTop() = {
    Obj(this.map.map((kv) => kv._1 ->(kv._2._1, AbsentTop)))
  }
  def absentTop(s: String) = {
    Obj(this.map.absentTop(s))
  }
  def absentTop(s: AbsString) = {
    s.gamma match {
      case Some(vs) =>
        Obj(vs.foldLeft(this.map)((m, s) => m.absentTop(s)))
      case None => throw new NotYetImplemented
    }
  }

  /* to make old locations after preanalysis */
  def oldify(): Obj = {
    Obj(map.map(data => {
      val (s, old_o) = data
      val new_o =
        PropValue(ObjectValue(Value(old_o._1._1._1._1, oldifyLoc(old_o._1._1._1._2)),
          old_o._1._1._2, old_o._1._1._3, old_o._1._1._4),
          old_o._1._3)
      s -> (new_o, old_o._2)
    }))
  }

  // internal locations exist the only one in the heap
  private def oldifyLoc(locSet: LocSet): LocSet = {
    locSet.foldLeft(locSet)((lset, loc) => if(locToAddr(loc) < 0) lset else lset + addrToLoc(locToAddr(loc), Old))
  }

  override def equals(that: Any) = {
    that match {
      case o: Obj => this.map eq o.map
      case _ => false
    }
  }

  def size: Int = this.map.size
}

object Obj {
  val ObjMapBot: ObjMap = ObjTreeMap.Empty

  def apply(v: ObjMap): Obj = new Obj(v)

  val bottom: Obj = Obj(ObjMapBot.
    updated(Str_default_number, (PropValueBot, AbsentBot)).
    updated(Str_default_other, (PropValueBot, AbsentBot)))

  val empty: Obj = Obj(ObjMapBot.
    updated(Str_default_number, (PropValueBot, AbsentTop)).
    updated(Str_default_other, (PropValueBot, AbsentTop)))

  val cache: mutable.WeakHashMap[Pair[ObjMap,ObjMap], Boolean] = mutable.WeakHashMap()
  def cacheUpdate(l: ObjMap, r: ObjMap, b: Boolean) = cache += ((l,r) -> b)
  def cacheLookup(l: ObjMap, r: ObjMap): Option[Boolean] = cache.get((l,r))
}
