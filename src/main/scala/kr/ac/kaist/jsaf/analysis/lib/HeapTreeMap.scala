/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.lib

import kr.ac.kaist.jsaf.analysis.typing.domain.{Loc, Obj}

/**
 * Immutable tree map data structure from Loc keys to Obj values.
 * This implementation is based on on int-binary-map.sml in SML/NJ.
 */
class HeapTreeMap private(private val k: HeapTreeMap.Key, 
                          private val v: HeapTreeMap.Data,
                          val count: Int,
                          private val l: HeapTreeMap,
                          private val r: HeapTreeMap) {
  def + (kv: (HeapTreeMap.Key, HeapTreeMap.Data)): HeapTreeMap = {
    this.updated(kv._1, kv._2)
  }
  
  def + (kv1: (HeapTreeMap.Key, HeapTreeMap.Data),
         kv2: (HeapTreeMap.Key, HeapTreeMap.Data)): HeapTreeMap = {
    this + kv1 + kv2
  }

  def updated(k: HeapTreeMap.Key, v: HeapTreeMap.Data): HeapTreeMap = {
    if (this eq HeapTreeMap.Empty) new HeapTreeMap(k, v, 1, HeapTreeMap.Empty, HeapTreeMap.Empty)
    else {
      val c = k - this.k
      if (c < 0) {
        val l_updated = this.l.updated(k, v)
        if (l_updated eq this.l) this
        else HeapTreeMap.rebalance(this.k, this.v, l_updated, this.r)
      }
      else if (c == 0) {
        if (v.map eq this.v.map) this
        else new HeapTreeMap(this.k, v, this.count, this.l, this.r)
      }
      else {
        val r_updated = this.r.updated(k, v)
        if (r_updated eq this.r) this
        else HeapTreeMap.rebalance(this.k, this.v, this.l, r_updated)
      }
    }
  }

  def weakUpdated(k: HeapTreeMap.Key, v: HeapTreeMap.Data): HeapTreeMap = {
    if (this eq HeapTreeMap.Empty) new HeapTreeMap(k, v, 1, HeapTreeMap.Empty, HeapTreeMap.Empty)
    else {
      val c = k - this.k
      if (c < 0) {
        val l_result = this.l.weakUpdated(k, v)
        if (l_result eq this.l) this
        else HeapTreeMap.rebalance(this.k, this.v, l_result, this.r)
      }
      else if (c == 0) {
        if (v.map eq this.v.map) this
        else if (v.map.size == this.v.map.size) {
          if (v <= this.v) this
          else if (this.v <= v) {
            new HeapTreeMap(this.k, v, this.count, this.l, this.r)
          }
          else {
            new HeapTreeMap(this.k, this.v + v, this.count, this.l, this.r)
          }
        } 
        else { 
          new HeapTreeMap(this.k, this.v + v, this.count, this.l, this.r)
        }
      }
      else {
        val r_result = this.r.weakUpdated(k, v)
        if (r_result eq this.r) this
        else HeapTreeMap.rebalance(this.k, this.v, this.l, r_result)
      }
    }
  }

  def subsLoc(l_r: HeapTreeMap.Key, l_o: HeapTreeMap.Key): HeapTreeMap = {
    if (this eq HeapTreeMap.Empty) HeapTreeMap.Empty
    else {
      val l = this.l.subsLoc(l_r, l_o)
      val v = this.v.subsLoc(l_r, l_o)
      val r = this.r.subsLoc(l_r, l_o)

      if ((l eq this.l) && (v.map eq this.v.map) && (r eq this.r)) {
        this
      } else {
        new HeapTreeMap(this.k, v, this.count, l, r)
      }
    }
  }

  def - (k: HeapTreeMap.Key): HeapTreeMap = {
    if (this eq HeapTreeMap.Empty) this
    else {
      val c = k - this.k
      if (c < 0) HeapTreeMap.rebalance(this.k, this.v, this.l - k, this.r)
      else if (c > 0) HeapTreeMap.rebalance(this.k, this.v, this.l, this.r - k)
      else HeapTreeMap.reconstruct(this.l, this.r)
    }
  }
  
  def apply(k: HeapTreeMap.Key): HeapTreeMap.Data = {
    if (this eq HeapTreeMap.Empty) throw new NoSuchElementException("key not found: " + k)
    else {
      val c = k - this.k
      if (c < 0) this.l(k)
      else if (c == 0) this.v
      else this.r(k)
    }
  }
  
  def get(k: HeapTreeMap.Key): Option[HeapTreeMap.Data] = {
    if (this eq HeapTreeMap.Empty) None
    else {
      val c = k - this.k
      if (c < 0) this.l.get(k)
      else if (c == 0) Some(this.v)
      else this.r.get(k)
    }
  }

  def contains(k: HeapTreeMap.Key): Boolean = {
    if (this eq HeapTreeMap.Empty) false
    else {
      val c = k - this.k
      if (c < 0) this.l.contains(k)
      else if (c == 0) true
      else this.r.contains(k)
    }
  }
  
  def isEmpty: Boolean = {
    this.count == 0
  }
  
  def submapOf(that: HeapTreeMap): Boolean = {
    if (this eq that) true
    else if (this.count > that.count) false
    else this._submapOf(that)
  }

  private def _submapOf(that: HeapTreeMap): Boolean = {
    if (this eq that) true
    else if (this eq HeapTreeMap.Empty) true
    else if (that eq HeapTreeMap.Empty) false
    else {
      val c = this.k - that.k
      if (c == 0) {
        this.l._submapOf(that.l) && 
        this.r._submapOf(that.r) && 
        this.v <= that.v
      } else if (c < 0) {
        (new HeapTreeMap(this.k, this.v, 0, this.l, HeapTreeMap.Empty))._submapOf(that.l) &&
        this.r._submapOf(that)
      } else {
        (new HeapTreeMap(this.k, this.v, 0, HeapTreeMap.Empty, this.r))._submapOf(that.r) &&
        this.l._submapOf(that)
      }
    }
  }
  
  def size: Int = this.count
  
  def foldLeft[B](z: B)(op: (B, (HeapTreeMap.Key, HeapTreeMap.Data)) => B): B = {
    if (this eq HeapTreeMap.Empty) z
    else this.r.foldLeft[B](op(this.l.foldLeft[B](z)(op), (this.k, this.v)))(op)
  }
  
  def foreach(f: ((HeapTreeMap.Key, HeapTreeMap.Data)) => Unit): Unit = {
    if (this eq HeapTreeMap.Empty) Unit
    else {
      this.l.foreach(f)
      f((this.k, this.v))
      this.r.foreach(f)
    }
  }
  
  def exists(p: ((HeapTreeMap.Key, HeapTreeMap.Data)) => Boolean): Boolean = {
    if (this eq HeapTreeMap.Empty) false
    else p((this.k, this.v)) || this.l.exists(p) || this.r.exists(p) 
  }

  def filter(p: ((HeapTreeMap.Key, HeapTreeMap.Data)) => Boolean): HeapTreeMap = {
    if (this eq HeapTreeMap.Empty) this
    else {
      val _l = this.l.filter(p)
      val cond = p((this.k, this.v)) 
      val _r = r.filter(p)
      
      if (cond) {
        if ((_l eq this.l) && (_r eq this.r)) this
        else HeapTreeMap.concat3(_l, this.k, this.v, _r)
      } else {
        HeapTreeMap.concat(_l, _r)
      }
    }
  }

  def map(f: ((HeapTreeMap.Key, HeapTreeMap.Data)) => (HeapTreeMap.Key, HeapTreeMap.Data)): HeapTreeMap = {
    this.foldLeft[HeapTreeMap](HeapTreeMap.Empty)((res, kv) => {
      res + f(kv)
    })
  }
  
  def keySet: Set[HeapTreeMap.Key] = {
    this.foldLeft[Set[HeapTreeMap.Key]](Set())((set, kv) => {
      set + kv._1
    })
  }
  
  def toMap: Map[HeapTreeMap.Key, HeapTreeMap.Data] = {
    this.foldLeft[Map[HeapTreeMap.Key, HeapTreeMap.Data]](Map())((map, kv) => map + kv)
  }
  
  def toSeq: Seq[(HeapTreeMap.Key, HeapTreeMap.Data)] = {
    this.toMap.toSeq  
  }
  
  override def toString: String = {
    val sb = new StringBuilder("HeapTreeMap(")
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


object HeapTreeMap {
  type Key = Loc
  type Data = Obj
  
  val Empty = new HeapTreeMap(0, null, 0, null, null)
  
  def fromMap(map: Map[HeapTreeMap.Key, HeapTreeMap.Data]): HeapTreeMap = {
    map.foldLeft[HeapTreeMap](HeapTreeMap.Empty)((objmap, kv) => objmap + kv)
  }
  
  private def rebalance(k: Key, v: Data, l: HeapTreeMap, r: HeapTreeMap): HeapTreeMap = {
    if (l eq Empty) {
      if (r eq Empty) {
        // l == Empty, r == Empty
        new HeapTreeMap(k, v, 1, Empty, Empty)
      } else if (r.l eq Empty) {
        if (r.r eq Empty) {
          // l == Empty, r.l == Empty, r.r == Empty
          new HeapTreeMap(k, v, 2, Empty, r)
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
          new HeapTreeMap(k, v, 2, l, Empty)
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
        new HeapTreeMap(k, v, l.count + r.count + 1, l, r)
      }
    }
  }
  
  private def singleL(a: Key, v: Data, x: HeapTreeMap, r: HeapTreeMap): HeapTreeMap = {
    val rl = r.l
    val count1 = x.count + rl.count + 1
    val set1 = new HeapTreeMap(a, v, count1, x, rl)

    val rr = r.r
    new HeapTreeMap(r.k, r.v, count1 + rr.count + 1, set1, rr)
  }

  private def singleR(b: Key, v: Data, r: HeapTreeMap, z: HeapTreeMap): HeapTreeMap = {
    val rr = r.r
    val count1 = rr.count + z.count + 1
    val set1 = new HeapTreeMap(b, v, count1, rr, z)

    val rl = r.l
    new HeapTreeMap(r.k, r.v, rl.count + count1 + 1, rl, set1)
  }

  private def doubleL(a: Key, v: Data, w: HeapTreeMap, r: HeapTreeMap): HeapTreeMap = {
    val rl = r.l

    val rll = rl.l
    val count1 = w.count + rll.count + 1
    val set1 = new HeapTreeMap(a, v, count1, w, rll)

    val rlr = rl.r
    val rr = r.r
    val count2 = rlr.count + rr.count + 1
    val set2 = new HeapTreeMap(r.k, r.v, count2, rlr, rr)

    new HeapTreeMap(rl.k, rl.v, count1 + count2 + 1, set1, set2)
  }

  private def doubleR(c: Key, v: Data, r: HeapTreeMap, z: HeapTreeMap): HeapTreeMap = {
    val rl = r.l

    val rr = r.r
    val rrl = rr.l
    val count1 = rl.count + rrl.count + 1
    val set1 = new HeapTreeMap(r.k, r.v, count1, rl, rrl)

    val rrr = rr.r
    val count2 = rrr.count + z.count + 1
    val set2 = new HeapTreeMap(c, v, count2, rrr, z)

    new HeapTreeMap(rr.k, rr.v, count1 + count2 + 1, set1, set2)
  }
  
  private def reconstruct(l: HeapTreeMap, r: HeapTreeMap): HeapTreeMap = {
    if (l eq Empty) r
    else if (r eq Empty) l
    else {
      val m = min(r)
      rebalance(m.k, m.v, l, deleteMin(r))
    }
  }

  private def min(t: HeapTreeMap): HeapTreeMap = {
    if (t eq Empty) throw new NoSuchElementException()
    else if (t.l eq Empty) t
    else min(t.l)
  }

  private def deleteMin(t: HeapTreeMap): HeapTreeMap = {
    if (t eq Empty) t
    else if (t.l eq Empty) t.r
    else rebalance(t.k, t.v, deleteMin(t.l), t.r)
  }
  
  private def concat(t1: HeapTreeMap, t2: HeapTreeMap): HeapTreeMap = {
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
  
  private def concat3(l: HeapTreeMap, k: Key, v: Data, r: HeapTreeMap): HeapTreeMap = {
    if (l eq Empty) r.updated(k, v)
    else if (r eq Empty) l.updated(k, v)
    else {
      if (l.count*3 < r.count) rebalance(r.k, r.v, concat3(l, k, v, r.l), r.r)
      else if (r.count*3 < l.count) rebalance(l.k, l.v, l.l, concat3(l.r, k, v, r))
      else new HeapTreeMap(k, v, l.count + r.count + 1, l, r);
    }
  }
}
