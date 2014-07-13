/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.lib

import kr.ac.kaist.jsaf.analysis.typing.domain._

/**
 * Immutable Loc tree set data structure.
 * This implementation is based on on int-binary-set.sml in SML/NJ.
 */
class LocTreeSet private(private val x: LocTreeSet.Key, 
                         private val count: Int,
                         private val l: LocTreeSet,
                         private val r: LocTreeSet) {
  /**
   * Adds an element <code>x</code> to this set.
   * @param x - element to add
   * @return new set with the element <code>x</code> added
   */
  def + (x: LocTreeSet.Key): LocTreeSet = {
    if (this eq LocTreeSet.Empty) new LocTreeSet(x, 1, LocTreeSet.Empty, LocTreeSet.Empty)
    else {
      val tx = this.x
      val c = x - tx
      if (c < 0) LocTreeSet.rebalance(tx, this.l + x, this.r)
      else if (c == 0) this
      else LocTreeSet.rebalance(tx, this.l, this.r + x)
    }
  }

  /**
   * Removes element <code>x</code> from this set.
   * If this set does not contain <code>x</code>, it is returned unchanged.
   * @param x - element to remove
   * @return new set with element <code>x</code> removed
   */
  def - (x: LocTreeSet.Key): LocTreeSet = {
    if (this eq LocTreeSet.Empty) this
    else {
      val tx = this.x
      val c = x - tx
      if (c < 0) LocTreeSet.rebalance(tx, this.l - x, this.r)
      else if (c > 0) LocTreeSet.rebalance(tx, this.l, this.r - x)
      else LocTreeSet.reconstruct(this.l, this.r)
    }
  }

  /**
   * Tests whether this set contains given element <code>x</code>.
   * @param x - element to test presence
   * @return <code>true</code> if this set contains <code>x</code>
   */
  def apply(x: LocTreeSet.Key): Boolean = {
    if (this eq LocTreeSet.Empty) false
    else {
      val c = x - this.x
      if (c < 0) this.l(x)
      else if (c == 0) true
      else this.r(x)
    }
  }
  
  /**
   * Tests whether this set contains given element <code>x</code>.
   * @param x - element to test presence
   * @return <code>true</code> if this set contains <code>x</code>
   */
  def contains(x: LocTreeSet.Key): Boolean = {
    if (this eq LocTreeSet.Empty) false
    else {
      val c = x - this.x
      if (c < 0) this.l.contains(x)
      else if (c == 0) true
      else this.r.contains(x)
    }
  }
  
  /**
   * Returns the number of elements in this set (cardinality).
   * @return the size of this set
   */
  def size: Int = this.count

  /**
   * Tests whether this set is empty.
   * @return <code>true</code> if this set is an empty set
   */
  def isEmpty: Boolean = this eq LocTreeSet.Empty

  /**
   * Returns the smallest element in this set.
   * If this set is empty, NoSuchElementException is thrown.
   * @return the smallest element
   * @throws NoSuchElementException this exception is thrown when this set is empty.
   */
  def head: LocTreeSet.Key = {
    if (this eq LocTreeSet.Empty) throw new NoSuchElementException()
    else if (this.l eq LocTreeSet.Empty) this.x
    else this.l.head
  }
  
  /**
   * Computes the union of this and that sets.
   * @param that the set to add to this set
   * @return new set containing all elements from this and that sets
   */
  def ++ (that: LocTreeSet): LocTreeSet = {
    if (this eq that) this
    else if (that eq LocTreeSet.Empty) this
    else if (this eq LocTreeSet.Empty) that
    else {
      val this_x = this.x
      val result = 
        LocTreeSet.concat3(
          LocTreeSet.unionHigh(this.l, LocTreeSet.trimHigh(this_x, that), this_x),
          this_x,
          LocTreeSet.unionLow(this.r, LocTreeSet.trimLow(this_x, that), this_x))
      if (result.count == this.count) this
      else if (result.count == that.count) that
      else result
    }
  }
  
  def ++ (that: Set[LocTreeSet.Key]): LocTreeSet = {
    this ++ LocTreeSet.fromSet(that)
  }
  
  
  /**
   * Computes set difference of this and that sets (this - that).
   * @param that the set of elements to remove from this set
   * @return new set containing elements of this that are not part of that set
   */
  def -- (that: LocTreeSet): LocTreeSet = {
    if (this eq that) LocTreeSet.Empty
    else if (this eq LocTreeSet.Empty) this
    else if (that eq LocTreeSet.Empty) this
    else {
      val that_x = that.x;
      val l2 = LocTreeSet.splitLT(this, that_x)
      val r2 = LocTreeSet.splitGT(this, that_x)
      LocTreeSet.concat(l2 -- that.l, r2 -- that.r)
    }
  }
 
  /**
   * Computes intersection of this and that two sets.
   * @param that the set to apply intersect with this set
   * @return new set containing elements common to both this and that set
   */
  def intersect(that: LocTreeSet): LocTreeSet = {
    if (this eq that) this
    else if (this eq LocTreeSet.Empty) this
    else if (that eq LocTreeSet.Empty) that
    else {
      val that_x = that.x
      val l2 = LocTreeSet.splitLT(this, that_x)
      val r2 = LocTreeSet.splitGT(this, that_x)
      if (this.contains(that_x)) {
        LocTreeSet.concat3(l2.intersect(that.l),
                           that_x,
                           r2.intersect(that.r))
      } else {
        LocTreeSet.concat(l2.intersect(that.l), 
                          r2.intersect(that.r))
      }
    }
  }

  /**
   * Tests subset relation of this and that sets (this <= that).
   * @param that the set to test
   * @return true if this is subset of that set
   */
  def subsetOf(that: LocTreeSet): Boolean = {
    if (this eq that) true
    else if (this.count > that.count) false
    else this._subsetOf(that)
  }
  
  private def _subsetOf(that: LocTreeSet): Boolean = {
    if (this eq LocTreeSet.Empty) true
    else if (that eq LocTreeSet.Empty) false
    else {
      val c = this.x - that.x
      if (c == 0) {
        this.l._subsetOf(that.l) && 
        this.r._subsetOf(that.r) 
      } else if (c < 0) {
        (new LocTreeSet(this.x, 0, this.l, LocTreeSet.Empty))._subsetOf(that.l) &&
        this.r._subsetOf(that)
      } else {
        (new LocTreeSet(this.x, 0, LocTreeSet.Empty, this.r))._subsetOf(that.r) &&
        this.l._subsetOf(that)
      }
    }
  }

  def foldLeft[B](z: B)(op: (B, LocTreeSet.Key) => B): B = {
    if (this eq LocTreeSet.Empty) z
    else this.r.foldLeft[B](op(this.l.foldLeft[B](z)(op), this.x))(op)
  }
  
  def foreach(f: (LocTreeSet.Key) => Unit): Unit = {
    if (this eq LocTreeSet.Empty) Unit
    else {
      this.l.foreach(f)
      f(this.x)
      this.r.foreach(f)
    }
  }
  
  def exists(p: (LocTreeSet.Key) => Boolean): Boolean = {
    if (this eq LocTreeSet.Empty) false
    else p(this.x) || this.l.exists(p) || this.r.exists(p) 
  }

  def filter(p: (LocTreeSet.Key) => Boolean): LocTreeSet = {
    if (this eq LocTreeSet.Empty) this
    else {
      val _l = this.l.filter(p)
      val cond = p(this.x) 
      val _r = r.filter(p)
      if (cond) {
        if ((_l eq this.l) && (_r eq this.r)) this
        else LocTreeSet.concat3(_l, this.x, _r)
      } else {
        LocTreeSet.concat(_l, _r)
      }
    }
  }

  def compare(that: LocTreeSet): Int = {
    if (this eq that) 0
    else {
      // larger sets are treated as smaller.
      // this approach decreases iteration counts in TAJS context-sensitivity.
      val size_cmp = that.size - this.size
      if (size_cmp != 0) size_cmp
      else LocTreeSet.cmp(LocTreeSet.stackLeft(this, Nil), LocTreeSet.stackLeft(that, Nil))
    }
  }
  
  def toSet: Set[LocTreeSet.Key] = {
    this.foldLeft[Set[LocTreeSet.Key]](Set())((set, k) => set + k)
  }
  
  def toSeq: Seq[LocTreeSet.Key] = {
    this.toSet.toSeq  
  }
  
  override def toString: String = {
    val sb = new StringBuilder("LocTreeSet(")
    var first = true
    this.foreach((x) => {
      if (first) first = false else sb.append(", ")
      sb.append(x.toString)
    })
    sb.append(")")
    sb.toString
  }

  override def hashCode: Int = {
    this.foldLeft[Int](0)((result, loc) => {
      result + loc//(loc._1 * 2 + loc._2)
    })
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case that: LocTreeSet =>
        (this eq that) || (this.count == that.count && this._subsetOf(that))
      case _ => false
    }
  }
}


object LocTreeSet {
  type Key = Loc
 
  val Empty = new LocTreeSet(0, 0, null, null)
  
  def fromSet(set: Set[Key]): LocTreeSet = {
    set.foldLeft[LocTreeSet](Empty)((locset, k) => locset + k)
  }

  def apply(v: Key): LocTreeSet = new LocTreeSet(v, 1, Empty, Empty)

  private def rebalance(v: Key, l: LocTreeSet, r: LocTreeSet): LocTreeSet = {
    if (l eq Empty) {
      if (r eq Empty) {
        // l == Empty, r == Empty
        new LocTreeSet(v, 1, Empty, Empty)
      } else if (r.l eq Empty) {
        if (r.r eq Empty) {
          // l == Empty, r.l == Empty, r.r == Empty
          new LocTreeSet(v, 2, Empty, r)
        } else {
          // l == Empty, r.l == Empty, r.r != Empty
          singleL(v, l, r)
        }
      } else if (r.r eq Empty) {
        // l == Empty, r.l != Empty, r.r == Empty
        doubleL(v, l, r)
      } else {
        // l == Empty, r.l != Empty, r.r != Empty
        if (r.l.count < r.r.count) {
          singleL(v, l, r)
        } else {
          doubleL(v, l, r)
        }
      }
    } else if (r eq Empty) {
      if (l.l eq Empty) {
        if (l.r eq Empty) {
          // l.l == Empty, l.r == Empty, r == Empty
          new LocTreeSet(v, 2, l, Empty)
        } else {
          // l.l == Empty, l.r != Empty, r == Empty
          doubleR(v, l, r)
        }
      } else if (l.r eq Empty) {
        // l.l != Empty, l.r == Empty, r == Empty
        singleR(v, l, r)
      } else {
        // l.l != Empty, l.r != Empty, r == Empty
        if (l.l.count > l.r.count) {
          singleR(v, l, r)
        } else {
          doubleR(v, l, r)
        }
      }
    } else {
      // l != Empty, r != Empty
      if (r.count >= l.count*3) {
        val rln = r.l.count;
        val rrn = r.r.count;
        if (rln < rrn) {
          singleL(v, l, r)
        } else {
          doubleL(v, l, r)
        }
      } else if (l.count >= r.count*3) {
        val lln = l.l.count;
        val lrn = l.r.count;
        if (lrn < lln) {
          singleR(v, l, r)
        } else {
          doubleR(v, l, r)
        }
      } else {
        new LocTreeSet(v, l.count + r.count + 1, l, r)
      }
    }
  }

  private def singleL(a: Key, x: LocTreeSet, r: LocTreeSet): LocTreeSet = {
    val rl = r.l
    val count1 = x.count + rl.count + 1
    val set1 = new LocTreeSet(a, count1, x, rl)

    val rr = r.r
    new LocTreeSet(r.x, count1 + rr.count + 1, set1, rr)
  }

  private def singleR(b: Key, r: LocTreeSet, z: LocTreeSet): LocTreeSet = {
    val rr = r.r
    val count1 = rr.count + z.count + 1
    val set1 = new LocTreeSet(b, count1, rr, z)

    val rl = r.l
    new LocTreeSet(r.x, rl.count + count1 + 1, rl, set1)
  }

  private def doubleL(a: Key, w: LocTreeSet, r: LocTreeSet): LocTreeSet = {
    val rl = r.l

    val rll = rl.l
    val count1 = w.count + rll.count + 1
    val set1 = new LocTreeSet(a, count1, w, rll)

    val rlr = rl.r
    val rr = r.r
    val count2 = rlr.count + rr.count + 1
    val set2 = new LocTreeSet(r.x, count2, rlr, rr)

    new LocTreeSet(rl.x, count1 + count2 + 1, set1, set2)
  }

  private def doubleR(c: Key, r: LocTreeSet, z: LocTreeSet): LocTreeSet = {
    val rl = r.l

    val rr = r.r
    val rrl = rr.l
    val count1 = rl.count + rrl.count + 1
    val set1 = new LocTreeSet(r.x, count1, rl, rrl)

    val rrr = rr.r
    val count2 = rrr.count + z.count + 1
    val set2 = new LocTreeSet(c, count2, rrr, z)

    new LocTreeSet(rr.x, count1 + count2 + 1, set1, set2)
  }
  
  
  private def reconstruct(l: LocTreeSet, r: LocTreeSet): LocTreeSet = {
    if (l eq Empty) r
    else if (r eq Empty) l
    else rebalance(min(r), l, deleteMin(r))
  }

  private def min(t: LocTreeSet): Key = {
    if (t eq Empty) throw new NoSuchElementException()
    else if (t.l eq Empty) t.x
    else min(t.l)
  }

  private def deleteMin(t: LocTreeSet): LocTreeSet = {
    if (t eq Empty) t
    else if (t.l eq Empty) t.r
    else rebalance(t.x, deleteMin(t.l), t.r)
  }
  
  private def concat(t1: LocTreeSet, t2: LocTreeSet): LocTreeSet = {
    if (t1 eq Empty) t2 
    else if (t2 eq Empty) t1
    else {
      if (t1.count*3 < t2.count) rebalance(t2.x, concat(t1, t2.l), t2.r) 
      else if (t2.count*3 < t1.count) rebalance(t1.x, t1.l, concat(t1.r, t2))
      else rebalance(min(t2), t1, deleteMin(t2))
    }
  }
  
  private def concat3(l: LocTreeSet, v: Key, r: LocTreeSet): LocTreeSet = {
    if (l eq Empty) r + v
    else if (r eq Empty) l + v
    else {
      if (l.count*3 < r.count) rebalance(r.x, concat3(l, v, r.l), r.r)
      else if (r.count*3 < l.count) rebalance(l.x, l.l, concat3(l.r, v, r))
      else new LocTreeSet(v, l.count + r.count + 1, l, r);
    }
  }
  
  private def unionHigh(t1: LocTreeSet, t2: LocTreeSet, hi: Key): LocTreeSet = {
    if (t2 eq Empty) t1
    else if (t1 eq Empty) concat3(t2.l, t2.x, splitLT(t2.r, hi))
    else {
      val t1x = t1.x;
      concat3(unionHigh(t1.l, trimHigh(t1x, t2), t1x), 
              t1x,
              unionBD(t1.r, trim(t1x, hi, t2), t1x, hi))
    }
  }
  
  private def unionLow(t1: LocTreeSet, t2: LocTreeSet, lo: Key): LocTreeSet = {
    if (t2 eq Empty) t1
    else if (t1 eq Empty) concat3(splitGT(t2.l, lo), t2.x, t2.r)
    else {
      val t1x = t1.x
      concat3(unionBD(t1.l, trim(lo, t1x, t2), lo, t1x),
              t1x,
              unionLow(t1.r, trimLow(t1x, t2), t1x))
    }
  }
  
  private def unionBD(t1: LocTreeSet, t2: LocTreeSet, lo: Key, hi: Key): LocTreeSet = {
    if (t2 eq Empty) t1
    else if (t1 eq Empty) concat3(splitGT(t2.l, lo), t2.x, splitLT(t2.r, hi))
    else {
      val t1x = t1.x
      concat3(unionBD(t1.l, trim(lo, t1x, t2), lo, t1x),
              t1x,
              unionBD(t1.r, trim(t1x, hi, t2), t1x, hi))
    }
  }
  
  private def splitGT(t: LocTreeSet, x: Key): LocTreeSet = {
    if (t eq Empty) t
    else {
      val tx = t.x
      val c = tx - x
      if (c < 0) splitGT(t.r, x)
      else if (c > 0) concat3(splitGT(t.l, x), tx, t.r)
      else t.r
    }
  }
  
  private def splitLT(t: LocTreeSet, x: Key): LocTreeSet = {
    if (t eq Empty) t
    else {
      val tx = t.x
      val c = tx - x
      if (c < 0) concat3(t.l, tx, splitLT(t.r, x))
      else if (c > 0) splitLT(t.l, x)
      else t.l
    }
  }

  private def trim(lo: Key, hi: Key, s: LocTreeSet): LocTreeSet = {
    if (s eq Empty) s
    else {
      if ((s.x - lo) > 0) {
        if ((s.x - hi) < 0) s
        else trim(lo, hi, s.l)
      } else {
        trim(lo, hi, s.r)
      }
    }
  }
  
  private def trimHigh(hi: Key, t: LocTreeSet): LocTreeSet = {
    if (t eq Empty) t
    else {
      if ((t.x - hi) < 0) t
      else trimHigh(hi, t.l)
    }
  }
  
  private def trimLow(lo: Key, t: LocTreeSet): LocTreeSet = {
    if (t eq Empty) t
    else {
      if ((t.x - lo) > 0) return t
      else trimLow(lo, t.r)
    }
  }

  private def cmp(stack1: List[LocTreeSet], stack2: List[LocTreeSet]): Int = {
    val (t1, rest1) = stackNext(stack1)
    val (t2, rest2) = stackNext(stack2)
    
    if (t1 eq Empty) {
      if (t2 eq Empty) 0
      else -1
    } else if (t2 eq Empty) {
      if (t1 eq Empty) 0
      else 1
    } else {
      val c = t1.x - t2.x
      if (c != 0) c
      else cmp(rest1, rest2)
    }
  }
  
  private def stackNext(stack: List[LocTreeSet]): (LocTreeSet, List[LocTreeSet]) = {
    stack match {
      case Nil => (Empty, Nil)
      case t :: rest => (t, stackLeft(t.r, rest))
    }
  }
  
  private def stackLeft(t: LocTreeSet, stack: List[LocTreeSet]): List[LocTreeSet] = {
    if (t eq Empty) stack
    else stackLeft(t.l, t :: stack)
  }
}

