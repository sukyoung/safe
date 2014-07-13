/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.lib

/**
 * Immutable integer tree set data structure.
 * This implementation is based on on int-binary-set.sml in SML/NJ.
 */
class IntTreeSet private(private val x: IntTreeSet.Key, 
                         private val count: Int,
                         private val l: IntTreeSet,
                         private val r: IntTreeSet) {
  /**
   * Adds an element <code>x</code> to this set.
   * @param x - element to add
   * @return new set with the element <code>x</code> added
   */
  def + (x: IntTreeSet.Key): IntTreeSet = {
    if (this eq IntTreeSet.Empty) new IntTreeSet(x, 1, IntTreeSet.Empty, IntTreeSet.Empty)
    else {
      val tx = this.x
      val c = IntTreeSet.compareKey(x, tx)
      if (c < 0) IntTreeSet.rebalance(tx, this.l + x, this.r)
      else if (c == 0) this
      else IntTreeSet.rebalance(tx, this.l, this.r + x)
    }
  }
  
  /**
   * Removes element <code>x</code> from this set.
   * If this set does not contain <code>x</code>, it is returned unchanged.
   * @param x - element to remove
   * @return new set with element <code>x</code> removed
   */
  def - (x: IntTreeSet.Key): IntTreeSet = {
    if (this eq IntTreeSet.Empty) this
    else {
      val tx = this.x
      val c = IntTreeSet.compareKey(x, tx)
      if (c < 0) IntTreeSet.rebalance(tx, this.l - x, this.r)
      else if (c > 0) IntTreeSet.rebalance(tx, this.l, this.r - x)
      else IntTreeSet.reconstruct(this.l, this.r)
    }
  }

  /**
   * Tests whether this set contains given element <code>x</code>.
   * @param x - element to test presence
   * @return <code>true</code> if this set contains <code>x</code>
   */
  def apply(x: IntTreeSet.Key): Boolean = {
    if (this eq IntTreeSet.Empty) false
    else {
      val c = IntTreeSet.compareKey(x, this.x)
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
  def contains(x: IntTreeSet.Key): Boolean = {
    if (this eq IntTreeSet.Empty) false
    else {
      val c = IntTreeSet.compareKey(x, this.x)
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
  def isEmpty: Boolean = this eq IntTreeSet.Empty

  /**
   * Returns the smallest element in this set.
   * If this set is empty, NoSuchElementException is thrown.
   * @return the smallest element
   * @throws NoSuchElementException this exception is thrown when this set is empty.
   */
  def head: IntTreeSet.Key = {
    if (this eq IntTreeSet.Empty) throw new NoSuchElementException()
    else if (this.l eq IntTreeSet.Empty) this.x
    else this.l.head
  }
  
  /**
   * Computes the union of this and that sets.
   * @param that the set to add to this set
   * @return new set containing all elements from this and that sets
   */
  def ++ (that: IntTreeSet): IntTreeSet = {
    if (this eq that) this
    else if (that eq IntTreeSet.Empty) this
    else if (this eq IntTreeSet.Empty) that
    else {
      val this_x = this.x
      val result = 
        IntTreeSet.concat3(
          IntTreeSet.unionHigh(this.l, IntTreeSet.trimHigh(this_x, that), this_x),
          this_x,
          IntTreeSet.unionLow(this.r, IntTreeSet.trimLow(this_x, that), this_x))
      if (result.count == this.count) this
      else if (result.count == that.count) that
      else result
    }
  }
  
  def ++ (that: Set[IntTreeSet.Key]): IntTreeSet = {
    this ++ IntTreeSet.fromSet(that)
  }
  
  
  /**
   * Computes set difference of this and that sets (this - that).
   * @param that the set of elements to remove from this set
   * @return new set containing elements of this that are not part of that set
   */
  def -- (that: IntTreeSet): IntTreeSet = {
    if (this eq that) IntTreeSet.Empty
    else if (this eq IntTreeSet.Empty) this
    else if (that eq IntTreeSet.Empty) this
    else {
      val that_x = that.x;
      val l2 = IntTreeSet.splitLT(this, that_x)
      val r2 = IntTreeSet.splitGT(this, that_x)
      IntTreeSet.concat(l2 -- that.l, r2 -- that.r)
    }
  }
 
  /**
   * Computes intersection of this and that two sets.
   * @param that the set to apply intersect with this set
   * @return new set containing elements common to both this and that set
   */
  def intersect(that: IntTreeSet): IntTreeSet = {
    if (this eq that) this
    else if (this eq IntTreeSet.Empty) this
    else if (that eq IntTreeSet.Empty) that
    else {
      val that_x = that.x
      val l2 = IntTreeSet.splitLT(this, that_x)
      val r2 = IntTreeSet.splitGT(this, that_x)
      if (this.contains(that_x)) {
        IntTreeSet.concat3(l2.intersect(that.l),
                           that_x,
                           r2.intersect(that.r))
      } else {
        IntTreeSet.concat(l2.intersect(that.l), 
                          r2.intersect(that.r))
      }
    }
  }
 
  /**
   * Tests subset relation of this and that sets (this <= that).
   * @param that the set to test
   * @return true if this is subset of that set
   */
  def subsetOf(that: IntTreeSet): Boolean = {
    if (this eq that) true
    else if (this.count > that.count) false
    else this._subsetOf(that)
  }
  
  private def _subsetOf(that: IntTreeSet): Boolean = {
    if (this eq IntTreeSet.Empty) true
    else if (that eq IntTreeSet.Empty) false
    else {
      val c = IntTreeSet.compareKey(this.x, that.x)
      if (c == 0) {
        this.l._subsetOf(that.l) && 
        this.r._subsetOf(that.r) 
      } else if (c < 0) {
        (new IntTreeSet(this.x, 0, this.l, IntTreeSet.Empty))._subsetOf(that.l) &&
        this.r._subsetOf(that)
      } else {
        (new IntTreeSet(this.x, 0, IntTreeSet.Empty, this.r))._subsetOf(that.r) &&
        this.l._subsetOf(that)
      }
    }
  }

  def foldLeft[B](z: B)(op: (B, IntTreeSet.Key) => B): B = {
    if (this eq IntTreeSet.Empty) z
    else this.r.foldLeft[B](op(this.l.foldLeft[B](z)(op), this.x))(op)
  }
  
  def foreach(f: (IntTreeSet.Key) => Unit): Unit = {
    if (this eq IntTreeSet.Empty) Unit
    else {
      this.l.foreach(f)
      f(this.x)
      this.r.foreach(f)
    }
  }
  
  def exists(p: (IntTreeSet.Key) => Boolean): Boolean = {
    if (this eq IntTreeSet.Empty) false
    else p(this.x) || this.l.exists(p) || this.r.exists(p) 
  }

  def filter(p: (IntTreeSet.Key) => Boolean): IntTreeSet = {
    if (this eq IntTreeSet.Empty) this
    else {
      val _l = this.l.filter(p)
      val cond = p(this.x) 
      val _r = r.filter(p)
      if (cond) {
        if ((_l eq this.l) && (_r eq this.r)) this
        else IntTreeSet.concat3(_l, this.x, _r)
      } else {
        IntTreeSet.concat(_l, _r)
      }
    }
  }

  def toSet: Set[IntTreeSet.Key] = {
    this.foldLeft[Set[IntTreeSet.Key]](Set())((set, k) => set + k)
  }
  
  def toSeq: Seq[IntTreeSet.Key] = {
    this.toSet.toSeq  
  }
  
  override def toString: String = {
    val sb = new StringBuilder("IntTreeSet(")
    var first = true
    this.foreach((x) => {
      if (first) first = false else sb.append(", ")
      sb.append(x.toString)
    })
    sb.append(")")
    sb.toString
  }
  
  override def hashCode: Int = {
    this.foldLeft[Int](0)((result, x) => {
      result + x
    })
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case that: IntTreeSet =>
        (this eq that) || (this.count == that.count && this._subsetOf(that))
      case _ => false
    }
  }
}


object IntTreeSet {
  type Key = Int
 
  val Empty = new IntTreeSet(0, 0, null, null)
  
  def fromSet(set: Set[Key]): IntTreeSet = {
    set.foldLeft[IntTreeSet](Empty)((locset, k) => locset + k)
  }

  def apply(v: Key): IntTreeSet = new IntTreeSet(v, 1, Empty, Empty)
  
  private def compareKey(a: Key, b: Key): Int = {
    if (a < b) -1
    else if (a > b) 1
    else 0
  }

  private def rebalance(v: Key, l: IntTreeSet, r: IntTreeSet): IntTreeSet = {
    if (l eq Empty) {
      if (r eq Empty) {
        // l == Empty, r == Empty
        new IntTreeSet(v, 1, Empty, Empty)
      } else if (r.l eq Empty) {
        if (r.r eq Empty) {
          // l == Empty, r.l == Empty, r.r == Empty
          new IntTreeSet(v, 2, Empty, r)
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
          new IntTreeSet(v, 2, l, Empty)
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
        new IntTreeSet(v, l.count + r.count + 1, l, r)
      }
    }
  }

  private def singleL(a: Key, x: IntTreeSet, r: IntTreeSet): IntTreeSet = {
    val rl = r.l
    val count1 = x.count + rl.count + 1
    val set1 = new IntTreeSet(a, count1, x, rl)

    val rr = r.r
    new IntTreeSet(r.x, count1 + rr.count + 1, set1, rr)
  }

  private def singleR(b: Key, r: IntTreeSet, z: IntTreeSet): IntTreeSet = {
    val rr = r.r
    val count1 = rr.count + z.count + 1
    val set1 = new IntTreeSet(b, count1, rr, z)

    val rl = r.l
    new IntTreeSet(r.x, rl.count + count1 + 1, rl, set1)
  }

  private def doubleL(a: Key, w: IntTreeSet, r: IntTreeSet): IntTreeSet = {
    val rl = r.l

    val rll = rl.l
    val count1 = w.count + rll.count + 1
    val set1 = new IntTreeSet(a, count1, w, rll)

    val rlr = rl.r
    val rr = r.r
    val count2 = rlr.count + rr.count + 1
    val set2 = new IntTreeSet(r.x, count2, rlr, rr)

    new IntTreeSet(rl.x, count1 + count2 + 1, set1, set2)
  }

  private def doubleR(c: Key, r: IntTreeSet, z: IntTreeSet): IntTreeSet = {
    val rl = r.l

    val rr = r.r
    val rrl = rr.l
    val count1 = rl.count + rrl.count + 1
    val set1 = new IntTreeSet(r.x, count1, rl, rrl)

    val rrr = rr.r
    val count2 = rrr.count + z.count + 1
    val set2 = new IntTreeSet(c, count2, rrr, z)

    new IntTreeSet(rr.x, count1 + count2 + 1, set1, set2)
  }
  
  
  private def reconstruct(l: IntTreeSet, r: IntTreeSet): IntTreeSet = {
    if (l eq Empty) r
    else if (r eq Empty) l
    else rebalance(min(r), l, deleteMin(r))
  }

  private def min(t: IntTreeSet): Key = {
    if (t eq Empty) throw new NoSuchElementException()
    else if (t.l eq Empty) t.x
    else min(t.l)
  }

  private def deleteMin(t: IntTreeSet): IntTreeSet = {
    if (t eq Empty) t
    else if (t.l eq Empty) t.r
    else rebalance(t.x, deleteMin(t.l), t.r)
  }
  
  private def concat(t1: IntTreeSet, t2: IntTreeSet): IntTreeSet = {
    if (t1 eq Empty) t2 
    else if (t2 eq Empty) t1
    else {
      if (t1.count*3 < t2.count) rebalance(t2.x, concat(t1, t2.l), t2.r) 
      else if (t2.count*3 < t1.count) rebalance(t1.x, t1.l, concat(t1.r, t2))
      else rebalance(min(t2), t1, deleteMin(t2))
    }
  }
  
  private def concat3(l: IntTreeSet, v: Key, r: IntTreeSet): IntTreeSet = {
    if (l eq Empty) r + v
    else if (r eq Empty) l + v
    else {
      if (l.count*3 < r.count) rebalance(r.x, concat3(l, v, r.l), r.r)
      else if (r.count*3 < l.count) rebalance(l.x, l.l, concat3(l.r, v, r))
      else new IntTreeSet(v, l.count + r.count + 1, l, r);
    }
  }
  
  private def unionHigh(t1: IntTreeSet, t2: IntTreeSet, hi: Key): IntTreeSet = {
    if (t2 eq Empty) t1
    else if (t1 eq Empty) concat3(t2.l, t2.x, splitLT(t2.r, hi))
    else {
      val t1x = t1.x;
      concat3(unionHigh(t1.l, trimHigh(t1x, t2), t1x), 
              t1x,
              unionBD(t1.r, trim(t1x, hi, t2), t1x, hi))
    }
  }
  
  private def unionLow(t1: IntTreeSet, t2: IntTreeSet, lo: Key): IntTreeSet = {
    if (t2 eq Empty) t1
    else if (t1 eq Empty) concat3(splitGT(t2.l, lo), t2.x, t2.r)
    else {
      val t1x = t1.x
      concat3(unionBD(t1.l, trim(lo, t1x, t2), lo, t1x),
              t1x,
              unionLow(t1.r, trimLow(t1x, t2), t1x))
    }
  }
  
  private def unionBD(t1: IntTreeSet, t2: IntTreeSet, lo: Key, hi: Key): IntTreeSet = {
    if (t2 eq Empty) t1
    else if (t1 eq Empty) concat3(splitGT(t2.l, lo), t2.x, splitLT(t2.r, hi))
    else {
      val t1x = t1.x
      concat3(unionBD(t1.l, trim(lo, t1x, t2), lo, t1x),
              t1x,
              unionBD(t1.r, trim(t1x, hi, t2), t1x, hi))
    }
  }
  
  private def splitGT(t: IntTreeSet, x: Key): IntTreeSet = {
    if (t eq Empty) t
    else {
      val tx = t.x
      val c = compareKey(tx, x)
      if (c < 0) splitGT(t.r, x)
      else if (c > 0) concat3(splitGT(t.l, x), tx, t.r)
      else t.r
    }
  }
  
  private def splitLT(t: IntTreeSet, x: Key): IntTreeSet = {
    if (t eq Empty) t
    else {
      val tx = t.x
      val c = compareKey(tx, x)
      if (c < 0) concat3(t.l, tx, splitLT(t.r, x))
      else if (c > 0) splitLT(t.l, x)
      else t.l
    }
  }

  private def trim(lo: Key, hi: Key, s: IntTreeSet): IntTreeSet = {
    if (s eq Empty) s
    else {
      if (compareKey(s.x, lo) > 0) {
        if (compareKey(s.x, hi) < 0) s
        else trim(lo, hi, s.l)
      } else {
        trim(lo, hi, s.r)
      }
    }
  }
  
  private def trimHigh(hi: Key, t: IntTreeSet): IntTreeSet = {
    if (t eq Empty) t
    else {
      if (compareKey(t.x, hi) < 0) t
      else trimHigh(hi, t.l)
    }
  }
  
  private def trimLow(lo: Key, t: IntTreeSet): IntTreeSet = {
    if (t eq Empty) t
    else {
      if (compareKey(t.x, lo) > 0) return t
      else trimLow(lo, t.r)
    }
  }
}
