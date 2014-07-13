/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.lib

import kr.ac.kaist.jsaf.analysis.typing.domain._

/**
 * Immutable tree map data structure from String keys to (PropValue, Absent) values.
 * This implementation is based on on int-binary-map.sml in SML/NJ.
 */
class ObjTreeMap private(private val k: ObjTreeMap.Key, 
                          private val v: ObjTreeMap.Data,
                          private val count: Int,
                          private val l: ObjTreeMap,
                          private val r: ObjTreeMap) {
  def + (kv: (ObjTreeMap.Key, ObjTreeMap.Data)): ObjTreeMap = {
    this.updated(kv._1, kv._2)
  }
  
  def + (kv1: (ObjTreeMap.Key, ObjTreeMap.Data),
         kv2: (ObjTreeMap.Key, ObjTreeMap.Data)): ObjTreeMap = {
    this + kv1 + kv2
  }

  def updated(k: ObjTreeMap.Key, v: ObjTreeMap.Data): ObjTreeMap = {
    if (this eq ObjTreeMap.Empty) new ObjTreeMap(k, v, 1, ObjTreeMap.Empty, ObjTreeMap.Empty)
    else {
      val c = k.compareTo(this.k)
      if (c < 0) {
        val l_updated = this.l.updated(k, v)
        if (l_updated eq this.l) this
        else ObjTreeMap.rebalance(this.k, this.v, l_updated, this.r)
      }
      else if (c == 0) {
        if ((v._1 eq this.v._1) || (v._1 == this.v._1)) {
          if (v._2 == this.v._2) this
          else new ObjTreeMap(this.k, (this.v._1, v._2), this.count, this.l, this.r)
        }
        else new ObjTreeMap(this.k, v, this.count, this.l, this.r)
      }
      else {
        val r_updated = this.r.updated(k, v)
        if (r_updated eq this.r) this
        else ObjTreeMap.rebalance(this.k, this.v, this.l, r_updated)
      }
    }
  }

  def subsLoc(l_r: Loc, l_o: Loc): ObjTreeMap = {
    if (this eq ObjTreeMap.Empty) ObjTreeMap.Empty
    else {
      val l = this.l.subsLoc(l_r, l_o)
      val r = this.r.subsLoc(l_r, l_o)

      val propv = this.v._1
      val ov = propv._1

      val v1 = ov._1
      val v1_new = v1.subsLoc(l_r, l_o)
      val ov_new = 
        if (v1 eq v1_new) ov
        else ObjectValue(v1_new, ov._2, ov._3, ov._4)

      val v2 = propv._2
      val v2_new = v2.subsLoc(l_r, l_o)

      val propv_new =
        if ((ov eq ov_new) && (v2 eq v2_new)) propv
        else PropValue(ov_new, v2_new, propv._3)
      
      if ((l eq this.l) && (r eq this.r) && (propv eq propv_new)) this
      else {
        new ObjTreeMap(this.k, (propv_new, this.v._2), this.count, l, r)
      }
    }
  }

  def weakSubsLoc(l_r: Loc, l_o: Loc): ObjTreeMap = {
    if (this eq ObjTreeMap.Empty) ObjTreeMap.Empty
    else {
      val l = this.l.weakSubsLoc(l_r, l_o)
      val r = this.r.weakSubsLoc(l_r, l_o)

      val propv = this.v._1
      val ov = propv._1

      val v1 = ov._1
      val v1_new = v1.weakSubsLoc(l_r, l_o)
      val ov_new = 
        if (v1 eq v1_new) ov
        else ObjectValue(v1_new, ov._2, ov._3, ov._4)

      val v2 = propv._2
      val v2_new = v2.weakSubsLoc(l_r, l_o)

      val propv_new =
        if ((ov eq ov_new) && (v2 eq v2_new)) propv
        else PropValue(ov_new, v2_new, propv._3)
      
      if ((l eq this.l) && (r eq this.r) && (propv eq propv_new)) this
      else {
        new ObjTreeMap(this.k, (propv_new, this.v._2), this.count, l, r)
      }
    }
  }

  def - (k: ObjTreeMap.Key): ObjTreeMap = {
    if (this eq ObjTreeMap.Empty) this
    else {
      val c = k.compareTo(this.k)
      if (c < 0) ObjTreeMap.rebalance(this.k, this.v, this.l - k, this.r)
      else if (c > 0) ObjTreeMap.rebalance(this.k, this.v, this.l, this.r - k)
      else ObjTreeMap.reconstruct(this.l, this.r)
    }
  }
  
  def apply(k: ObjTreeMap.Key): ObjTreeMap.Data = {
    if (this eq ObjTreeMap.Empty) throw new NoSuchElementException("key not found: " + k)
    else {
      val c = k.compareTo(this.k)
      if (c < 0) this.l(k)
      else if (c == 0) this.v
      else this.r(k)
    }
  }
  
  def get(k: ObjTreeMap.Key): Option[ObjTreeMap.Data] = {
    if (this eq ObjTreeMap.Empty) None
    else {
      val c = k.compareTo(this.k)
      if (c < 0) this.l.get(k)
      else if (c == 0) Some(this.v)
      else this.r.get(k)
    }
  }

  def contains(k: ObjTreeMap.Key): Boolean = {
    if (this eq ObjTreeMap.Empty) false
    else {
      val c = k.compareTo(this.k)
      if (c < 0) this.l.contains(k)
      else if (c == 0) true
      else this.r.contains(k)
    }
  }
  
  // Tests if this map's keys and values are contained in that map.
  // That map's default values are used for keys existing only in this map.
  // Note that keys existing only in that map are ignored.
  // (In other words, submapOf is not complete Object join operator.) 
  def submapOf(that: ObjTreeMap): Boolean = {
    if (this eq that) true
    else {
      val that_number = that("@default_number")
      val that_other = that("@default_other")
        
      def submap(x: ObjTreeMap, y: ObjTreeMap): Boolean = {
        if (x eq y) true
        else if (x eq ObjTreeMap.Empty) true
        else if (y eq ObjTreeMap.Empty) {
          !x.exists(kv => {
            val xk = kv._1
            val xv = kv._2
            if (xk.length > 0 && xk.charAt(0) == '@') true
            else if (AbsString.alpha(xk) <= NumStr) {
              xv._1 </ that_number._1 || xv._2 </ that_number._2 
            }
            else {
              xv._1 </ that_other._1 || xv._2 </ that_other._2 
            }
          })
        }
        else {
          val c = x.k.compareTo(y.k)
          if (c == 0) {
            x.v._1 <= y.v._1 &&
            x.v._2 <= y.v._2 &&
            submap(x.l, y.l) && 
            submap(x.r, y.r)
          } else if (c < 0) {
            submap(new ObjTreeMap(x.k, x.v, 0, x.l, ObjTreeMap.Empty), y.l) &&
            submap(x.r, y)
          } else {
            submap(new ObjTreeMap(x.k, x.v, 0, ObjTreeMap.Empty, x.r), y.r) &&
            submap(x.l, y)
          }
        }
      }

      if ((that_number._1 eq PropValueBot) && (that_other._1 eq PropValueBot) && 
          this.count > that.count) {
        false 
      } else {
        submap(this, that)
      }
    }
  }

  def entryDiff(that: ObjTreeMap): ObjTreeMap = {
    if (this eq that) ObjTreeMap.Empty
    else if (this eq ObjTreeMap.Empty) this
    else if (that eq ObjTreeMap.Empty) this
    else {
      val that_k = that.k;
      val l2 = ObjTreeMap.splitLT(this, that_k)
      val r2 = ObjTreeMap.splitGT(this, that_k)
      ObjTreeMap.concat(l2.entryDiff(that.l), r2.entryDiff(that.r))
    }
  }

  def size: Int = this.count
  
  def foldLeft[B](z: B)(op: (B, (ObjTreeMap.Key, ObjTreeMap.Data)) => B): B = {
    if (this eq ObjTreeMap.Empty) z
    else this.r.foldLeft[B](op(this.l.foldLeft[B](z)(op), (this.k, this.v)))(op)
  }
  
  def foreach(f: ((ObjTreeMap.Key, ObjTreeMap.Data)) => Unit): Unit = {
    if (this eq ObjTreeMap.Empty) Unit
    else {
      this.l.foreach(f)
      f((this.k, this.v))
      this.r.foreach(f)
    }
  }
  
  def exists(p: ((ObjTreeMap.Key, ObjTreeMap.Data)) => Boolean): Boolean = {
    if (this eq ObjTreeMap.Empty) false
    else p((this.k, this.v)) || this.l.exists(p) || this.r.exists(p) 
  }

  def filter(p: ((ObjTreeMap.Key, ObjTreeMap.Data)) => Boolean): ObjTreeMap = {
    if (this eq ObjTreeMap.Empty) this
    else {
      val _l = this.l.filter(p)
      val cond = p((this.k, this.v)) 
      val _r = r.filter(p)
      
      if (cond) {
        if ((_l eq this.l) && (_r eq this.r)) this
        else ObjTreeMap.concat3(_l, this.k, this.v, _r)
      } else {
        ObjTreeMap.concat(_l, _r)
      }
    }
  }
  
  def map(f: ((ObjTreeMap.Key, ObjTreeMap.Data)) => (ObjTreeMap.Key, ObjTreeMap.Data)): ObjTreeMap = {
    this.foldLeft[ObjTreeMap](ObjTreeMap.Empty)((res, kv) => {
      res + f(kv)
    })
  }
  
  def keySet: Set[ObjTreeMap.Key] = {
    this.foldLeft[Set[ObjTreeMap.Key]](Set())((set, kv) => {
      set + kv._1
    })
  }
  
  def toMap: Map[ObjTreeMap.Key, ObjTreeMap.Data] = {
    this.foldLeft[Map[ObjTreeMap.Key, ObjTreeMap.Data]](Map())((map, kv) => map + kv)
  }
  
  def toSeq: Seq[(ObjTreeMap.Key, ObjTreeMap.Data)] = {
    this.toMap.toSeq  
  }
  
  override def toString: String = {
    val sb = new StringBuilder("ObjTreeMap(")
    var first = true
    this.foreach((x) => {
      if (first) first = false else sb.append(", ")
      sb.append(x._1.toString)
      sb.append(" -> ")
      sb.append(x._2.toString)
    })
    sb.append(")")
    sb.toString
  }
}


object ObjTreeMap {
  type Key = String
  type Data = (PropValue, Absent)
  
  val Empty = new ObjTreeMap(null, null, 0, null, null)
  
  def fromMap(map: Map[ObjTreeMap.Key, ObjTreeMap.Data]): ObjTreeMap = {
    map.foldLeft[ObjTreeMap](ObjTreeMap.Empty)((objmap, kv) => objmap + kv)
  }
  
  private def rebalance(k: Key, v: Data, l: ObjTreeMap, r: ObjTreeMap): ObjTreeMap = {
    if (l eq Empty) {
      if (r eq Empty) {
        // l == Empty, r == Empty
        new ObjTreeMap(k, v, 1, Empty, Empty)
      } else if (r.l eq Empty) {
        if (r.r eq Empty) {
          // l == Empty, r.l == Empty, r.r == Empty
          new ObjTreeMap(k, v, 2, Empty, r)
        } else {
          // l == Empty, r.l == Empty, r.r != Empty
          singleL(k, v, l, r)
        }
      } else if (r.r eq Empty) {
        // l == Empty, r.l != Empty, r.r == Empty
        doubleL(k, v, l, r)
      } else {
        // l == Empty, r.l != Empty, r.r != Empty
        if (r.l.count < r.r.count) {
          singleL(k, v, l, r)
        } else {
          doubleL(k, v, l, r)
        }
      }
    } else if (r eq Empty) {
      if (l.l eq Empty) {
        if (l.r eq Empty) {
          // l.l == Empty, l.r == Empty, r == Empty
          new ObjTreeMap(k, v, 2, l, Empty)
        } else {
          // l.l == Empty, l.r != Empty, r == Empty
          doubleR(k, v, l, r)
        }
      } else if (l.r eq Empty) {
        // l.l != Empty, l.r == Empty, r == Empty
        singleR(k, v, l, r)
      } else {
        // l.l != Empty, l.r != Empty, r == Empty
        if (l.l.count > l.r.count) {
          singleR(k, v, l, r)
        } else {
          doubleR(k, v, l, r)
        }
      }
    } else {
      // l != Empty, r != Empty
      if (r.count >= l.count*3) {
        val rln = r.l.count;
        val rrn = r.r.count;
        if (rln < rrn) {
          singleL(k, v, l, r)
        } else {
          doubleL(k, v, l, r)
        }
      } else if (l.count >= r.count*3) {
        val lln = l.l.count;
        val lrn = l.r.count;
        if (lrn < lln) {
          singleR(k, v, l, r)
        } else {
          doubleR(k, v, l, r)
        }
      } else {
        new ObjTreeMap(k, v, l.count + r.count + 1, l, r)
      }
    }
  }
  
  private def singleL(a: Key, v: Data, x: ObjTreeMap, r: ObjTreeMap): ObjTreeMap = {
    val rl = r.l
    val count1 = x.count + rl.count + 1
    val set1 = new ObjTreeMap(a, v, count1, x, rl)

    val rr = r.r
    new ObjTreeMap(r.k, r.v, count1 + rr.count + 1, set1, rr)
  }

  private def singleR(b: Key, v: Data, r: ObjTreeMap, z: ObjTreeMap): ObjTreeMap = {
    val rr = r.r
    val count1 = rr.count + z.count + 1
    val set1 = new ObjTreeMap(b, v, count1, rr, z)

    val rl = r.l
    new ObjTreeMap(r.k, r.v, rl.count + count1 + 1, rl, set1)
  }

  private def doubleL(a: Key, v: Data, w: ObjTreeMap, r: ObjTreeMap): ObjTreeMap = {
    val rl = r.l

    val rll = rl.l
    val count1 = w.count + rll.count + 1
    val set1 = new ObjTreeMap(a, v, count1, w, rll)

    val rlr = rl.r
    val rr = r.r
    val count2 = rlr.count + rr.count + 1
    val set2 = new ObjTreeMap(r.k, r.v, count2, rlr, rr)

    new ObjTreeMap(rl.k, rl.v, count1 + count2 + 1, set1, set2)
  }

  private def doubleR(c: Key, v: Data, r: ObjTreeMap, z: ObjTreeMap): ObjTreeMap = {
    val rl = r.l

    val rr = r.r
    val rrl = rr.l
    val count1 = rl.count + rrl.count + 1
    val set1 = new ObjTreeMap(r.k, r.v, count1, rl, rrl)

    val rrr = rr.r
    val count2 = rrr.count + z.count + 1
    val set2 = new ObjTreeMap(c, v, count2, rrr, z)

    new ObjTreeMap(rr.k, rr.v, count1 + count2 + 1, set1, set2)
  }
  
  private def reconstruct(l: ObjTreeMap, r: ObjTreeMap): ObjTreeMap = {
    if (l eq Empty) r
    else if (r eq Empty) l
    else {
      val m = min(r)
      rebalance(m.k, m.v, l, deleteMin(r))
    }
  }

  private def min(t: ObjTreeMap): ObjTreeMap = {
    if (t eq Empty) throw new NoSuchElementException()
    else if (t.l eq Empty) t
    else min(t.l)
  }

  private def deleteMin(t: ObjTreeMap): ObjTreeMap = {
    if (t eq Empty) t
    else if (t.l eq Empty) t.r
    else rebalance(t.k, t.v, deleteMin(t.l), t.r)
  }
  
  private def concat(t1: ObjTreeMap, t2: ObjTreeMap): ObjTreeMap = {
    if (t1 eq Empty) t2 
    else if (t2 eq Empty) t1
    else {
      if (t1.count*3 < t2.count) rebalance(t2.k, t2.v, concat(t1, t2.l), t2.r) 
      else if (t2.count*3 < t1.count) rebalance(t1.k, t1.v, t1.l, concat(t1.r, t2))
      else {
        val m = min(t2)
        rebalance(m.k, m.v, t1, deleteMin(t2))
      }
    }
  }
  
  private def concat3(l: ObjTreeMap, k: Key, v: Data, r: ObjTreeMap): ObjTreeMap = {
    if (l eq Empty) r.updated(k, v)
    else if (r eq Empty) l.updated(k, v)
    else {
      if (l.count*3 < r.count) rebalance(r.k, r.v, concat3(l, k, v, r.l), r.r)
      else if (r.count*3 < l.count) rebalance(l.k, l.v, l.l, concat3(l.r, k, v, r))
      else new ObjTreeMap(k, v, l.count + r.count + 1, l, r);
    }
  }

  private def splitGT(t: ObjTreeMap, k: Key): ObjTreeMap = {
    if (t eq Empty) t
    else {
      val tk = t.k
      val c = tk.compareTo(k)
      if (c < 0) splitGT(t.r, k)
      else if (c > 0) concat3(splitGT(t.l, k), tk, t.v, t.r)
      else t.r
    }
  }
  
  private def splitLT(t: ObjTreeMap, k: Key): ObjTreeMap = {
    if (t eq Empty) t
    else {
      val tk = t.k
      val c = tk.compareTo(k)
      if (c < 0) concat3(t.l, tk, t.v, splitLT(t.r, k))
      else if (c > 0) splitLT(t.l, k)
      else t.l
    }
  }
}
