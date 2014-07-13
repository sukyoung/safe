/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.lib

import kr.ac.kaist.jsaf.analysis.typing.OrderEntry
import kr.ac.kaist.jsaf.analysis.cfg._

/**
 * Immutable tree set data structure for Worklist implementation.
 * This implementation is based on on int-binary-set.sml in SML/NJ.
 */
class WorkTreeSet private(private val x: WorkTreeSet.Key, 
                          private val count: Int,
                          private val l: WorkTreeSet,
                          private val r: WorkTreeSet) {
  /**
   * Adds an element <code>x</code> to this set.
   * @param x - element to add
   * @return new set with the element <code>x</code> added
   */
  def + (x: WorkTreeSet.Key): WorkTreeSet = {
    if (this eq WorkTreeSet.Empty) new WorkTreeSet(x, 1, WorkTreeSet.Empty, WorkTreeSet.Empty)
    else {
      val tx = this.x
      val c = WorkTreeSet.compareKey(x, tx)
      if (c < 0) WorkTreeSet.rebalance(tx, this.l + x, this.r)
      else if (c == 0) this
      else WorkTreeSet.rebalance(tx, this.l, this.r + x)
    }
  }
  
  /**
   * Removes element <code>x</code> from this set.
   * If this set does not contain <code>x</code>, it is returned unchanged.
   * @param x - element to remove
   * @return new set with element <code>x</code> removed
   */
  def - (x: WorkTreeSet.Key): WorkTreeSet = {
    if (this eq WorkTreeSet.Empty) this
    else {
      val tx = this.x
      val c = WorkTreeSet.compareKey(x, tx)
      if (c < 0) WorkTreeSet.rebalance(tx, this.l - x, this.r)
      else if (c > 0) WorkTreeSet.rebalance(tx, this.l, this.r - x)
      else WorkTreeSet.reconstruct(this.l, this.r)
    }
  }

  /**
   * Tests whether this set contains given element <code>x</code>.
   * @param x - element to test presence
   * @return <code>true</code> if this set contains <code>x</code>
   */
  def apply(x: WorkTreeSet.Key): Boolean = {
    if (this eq WorkTreeSet.Empty) false
    else {
      val c = WorkTreeSet.compareKey(x, this.x)
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
  def contains(x: WorkTreeSet.Key): Boolean = {
    if (this eq WorkTreeSet.Empty) false
    else {
      val c = WorkTreeSet.compareKey(x, this.x)
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
  def isEmpty: Boolean = this eq WorkTreeSet.Empty

  /**
   * Returns the smallest element in this set.
   * If this set is empty, NoSuchElementException is thrown.
   * @return the smallest element
   * @throws NoSuchElementException this exception is thrown when this set is empty.
   */
  def head: WorkTreeSet.Key = {
    if (this eq WorkTreeSet.Empty) throw new NoSuchElementException()
    else if (this.l eq WorkTreeSet.Empty) this.x
    else this.l.head
  }
  
  /**
   * Removes the smallest element in this set.
   * If this set is empty, UnsupportedOperationException is thrown.
   * @return set with the smallest element removed
   * @throws NoSuchElementException this exception is thrown when this set is empty.
   */
  def tail: WorkTreeSet = {
    if (this eq WorkTreeSet.Empty) throw new UnsupportedOperationException("empty.tail")
    else if (this.l eq WorkTreeSet.Empty) this.r
    else WorkTreeSet.rebalance(this.x, this.l.tail, this.r)
  }

  /**
   * Returns head and tail of this set.
   * If this set is empty, NoSuchElementException is thrown.
   * @return pair of head and tail
   * @throws NoSuchElementException this exception is thrown when this set is empty.
   */
  def headAndTail: (WorkTreeSet.Key, WorkTreeSet) = {
    var head: WorkTreeSet.Key = null
    def deleteMin(t: WorkTreeSet): WorkTreeSet = { 
      if (t eq WorkTreeSet.Empty) throw new NoSuchElementException()
      else if (t.l eq WorkTreeSet.Empty) {
        head = t.x
        t.r 
      }
      else WorkTreeSet.rebalance(t.x, deleteMin(t.l), t.r)
    }
    
    val tail = deleteMin(this)
    (head, tail)
  }
  
  /**
   * Computes the union of this and that sets.
   * @param that the set to add to this set
   * @return new set containing all elements from this and that sets
   */
  def ++ (that: WorkTreeSet): WorkTreeSet = {
    if (this eq that) this
    else if (that eq WorkTreeSet.Empty) this
    else if (this eq WorkTreeSet.Empty) that
    else {
      val this_x = this.x
      val result = 
        WorkTreeSet.concat3(
          WorkTreeSet.unionHigh(this.l, WorkTreeSet.trimHigh(this_x, that), this_x),
          this_x,
          WorkTreeSet.unionLow(this.r, WorkTreeSet.trimLow(this_x, that), this_x))
      if (result.count == this.count) this
      else if (result.count == that.count) that
      else result
    }
  }
  
  def ++ (that: Set[WorkTreeSet.Key]): WorkTreeSet = {
    this ++ WorkTreeSet.fromSet(that)
  }
  
  
  /**
   * Computes set difference of this and that sets (this - that).
   * @param that the set of elements to remove from this set
   * @return new set containing elements of this that are not part of that set
   */
  def -- (that: WorkTreeSet): WorkTreeSet = {
    if (this eq that) WorkTreeSet.Empty
    else if (this eq WorkTreeSet.Empty) this
    else if (that eq WorkTreeSet.Empty) this
    else {
      val that_x = that.x;
      val l2 = WorkTreeSet.splitLT(this, that_x)
      val r2 = WorkTreeSet.splitGT(this, that_x)
      WorkTreeSet.concat(l2 -- that.l, r2 -- that.r)
    }
  }
 
  /**
   * Computes intersection of this and that two sets.
   * @param that the set to apply intersect with this set
   * @return new set containing elements common to both this and that set
   */
  def intersect(that: WorkTreeSet): WorkTreeSet = {
    if (this eq that) this
    else if (this eq WorkTreeSet.Empty) this
    else if (that eq WorkTreeSet.Empty) that
    else {
      val that_x = that.x
      val l2 = WorkTreeSet.splitLT(this, that_x)
      val r2 = WorkTreeSet.splitGT(this, that_x)
      if (this.contains(that_x)) {
        WorkTreeSet.concat3(l2.intersect(that.l),
                           that_x,
                           r2.intersect(that.r))
      } else {
        WorkTreeSet.concat(l2.intersect(that.l), 
                          r2.intersect(that.r))
      }
    }
  }
 
  /**
   * Tests subset relation of this and that sets (this <= that).
   * @param that the set to test
   * @return true if this is subset of that set
   */
  def subsetOf(that: WorkTreeSet): Boolean = {
    if (this eq that) true
    else if (this.count > that.count) false
    else this._subsetOf(that)
  }
  
  private def _subsetOf(that: WorkTreeSet): Boolean = {
    if (this eq WorkTreeSet.Empty) true
    else if (that eq WorkTreeSet.Empty) false
    else {
      val c = WorkTreeSet.compareKey(this.x, that.x)
      if (c == 0) {
        this.l._subsetOf(that.l) && 
        this.r._subsetOf(that.r) 
      } else if (c < 0) {
        (new WorkTreeSet(this.x, 0, this.l, WorkTreeSet.Empty))._subsetOf(that.l) &&
        this.r._subsetOf(that)
      } else {
        (new WorkTreeSet(this.x, 0, WorkTreeSet.Empty, this.r))._subsetOf(that.r) &&
        this.l._subsetOf(that)
      }
    }
  }

  def foldLeft[B](z: B)(op: (B, WorkTreeSet.Key) => B): B = {
    if (this eq WorkTreeSet.Empty) z
    else this.r.foldLeft[B](op(this.l.foldLeft[B](z)(op), this.x))(op)
  }
  
  def foreach(f: (WorkTreeSet.Key) => Unit): Unit = {
    if (this eq WorkTreeSet.Empty) Unit
    else {
      this.l.foreach(f)
      f(this.x)
      this.r.foreach(f)
    }
  }
  
  def exists(p: (WorkTreeSet.Key) => Boolean): Boolean = {
    if (this eq WorkTreeSet.Empty) false
    else p(this.x) || this.l.exists(p) || this.r.exists(p) 
  }

  def filter(p: (WorkTreeSet.Key) => Boolean): WorkTreeSet = {
    if (this eq WorkTreeSet.Empty) this
    else {
      val _l = this.l.filter(p)
      val cond = p(this.x) 
      val _r = r.filter(p)
      if (cond) {
        if ((_l eq this.l) && (_r eq this.r)) this
        else WorkTreeSet.concat3(_l, this.x, _r)
      } else {
        WorkTreeSet.concat(_l, _r)
      }
    }
  }

  def toSet: Set[WorkTreeSet.Key] = {
    this.foldLeft[Set[WorkTreeSet.Key]](Set())((set, k) => set + k)
  }
  
  def toSeq: Seq[WorkTreeSet.Key] = {
    this.toSet.toSeq  
  }
  
  override def toString: String = {
    val sb = new StringBuilder("WorkTreeSet(")
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
      result + x.hashCode
    })
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case that: WorkTreeSet =>
        (this eq that) || (this.count == that.count && this._subsetOf(that))
      case _ => false
    }
  }
}


object WorkTreeSet {
  type Key = OrderEntry
 
  val Empty = new WorkTreeSet(null, 0, null, null)
  
  def fromSet(set: Set[Key]): WorkTreeSet = {
    set.foldLeft[WorkTreeSet](Empty)((locset, k) => locset + k)
  }

  def apply(v: Key): WorkTreeSet = new WorkTreeSet(v, 1, Empty, Empty)
  
  private def compareKey(a: Key, b: Key): Int = {
    val index_a = a._1
    val index_b = b._1
    
    // 1. compare node's order
    val index_cmp = index_a - index_b
    if (index_cmp != 0) return index_cmp
    
    val cp_a = a._2
    val cp_b = b._2

    val node_a = cp_a._1
    val node_b = cp_b._1

    val fid_a = node_a._1
    val fid_b = node_b._1

    // 2. compare node's FunctionId
    // Note: node comparison is necessary because node doesn't have order if not reachable in DDG. 
    val fid_cmp = fid_a - fid_b
    if (fid_cmp != 0) return fid_cmp
    
    val label_a = node_a._2 match {
      case LEntry => -3
      case LExit => -2
      case LExitExc => -1
      case LBlock(n) => n
    }
    val label_b = node_b._2 match {
      case LEntry => -3
      case LExit => -2
      case LExitExc => -1
      case LBlock(n) => n
    }

    // 3. compare node's Label
    val label_cmp = label_a - label_b
    if (label_cmp != 0) return label_cmp
    
    val cc_a = cp_a._2
    val cc_b = cp_b._2
    
    // 4. compare calling context
    return cc_a.compare(cc_b)
  }

  private def rebalance(v: Key, l: WorkTreeSet, r: WorkTreeSet): WorkTreeSet = {
    if (l eq Empty) {
      if (r eq Empty) {
        // l == Empty, r == Empty
        new WorkTreeSet(v, 1, Empty, Empty)
      } else if (r.l eq Empty) {
        if (r.r eq Empty) {
          // l == Empty, r.l == Empty, r.r == Empty
          new WorkTreeSet(v, 2, Empty, r)
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
          new WorkTreeSet(v, 2, l, Empty)
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
        new WorkTreeSet(v, l.count + r.count + 1, l, r)
      }
    }
  }

  private def singleL(a: Key, x: WorkTreeSet, r: WorkTreeSet): WorkTreeSet = {
    val rl = r.l
    val count1 = x.count + rl.count + 1
    val set1 = new WorkTreeSet(a, count1, x, rl)

    val rr = r.r
    new WorkTreeSet(r.x, count1 + rr.count + 1, set1, rr)
  }

  private def singleR(b: Key, r: WorkTreeSet, z: WorkTreeSet): WorkTreeSet = {
    val rr = r.r
    val count1 = rr.count + z.count + 1
    val set1 = new WorkTreeSet(b, count1, rr, z)

    val rl = r.l
    new WorkTreeSet(r.x, rl.count + count1 + 1, rl, set1)
  }

  private def doubleL(a: Key, w: WorkTreeSet, r: WorkTreeSet): WorkTreeSet = {
    val rl = r.l

    val rll = rl.l
    val count1 = w.count + rll.count + 1
    val set1 = new WorkTreeSet(a, count1, w, rll)

    val rlr = rl.r
    val rr = r.r
    val count2 = rlr.count + rr.count + 1
    val set2 = new WorkTreeSet(r.x, count2, rlr, rr)

    new WorkTreeSet(rl.x, count1 + count2 + 1, set1, set2)
  }

  private def doubleR(c: Key, r: WorkTreeSet, z: WorkTreeSet): WorkTreeSet = {
    val rl = r.l

    val rr = r.r
    val rrl = rr.l
    val count1 = rl.count + rrl.count + 1
    val set1 = new WorkTreeSet(r.x, count1, rl, rrl)

    val rrr = rr.r
    val count2 = rrr.count + z.count + 1
    val set2 = new WorkTreeSet(c, count2, rrr, z)

    new WorkTreeSet(rr.x, count1 + count2 + 1, set1, set2)
  }
  
  
  private def reconstruct(l: WorkTreeSet, r: WorkTreeSet): WorkTreeSet = {
    if (l eq Empty) r
    else if (r eq Empty) l
    else rebalance(min(r), l, deleteMin(r))
  }

  private def min(t: WorkTreeSet): Key = {
    if (t eq Empty) throw new NoSuchElementException()
    else if (t.l eq Empty) t.x
    else min(t.l)
  }

  private def deleteMin(t: WorkTreeSet): WorkTreeSet = {
    if (t eq Empty) t
    else if (t.l eq Empty) t.r
    else rebalance(t.x, deleteMin(t.l), t.r)
  }
  
  private def concat(t1: WorkTreeSet, t2: WorkTreeSet): WorkTreeSet = {
    if (t1 eq Empty) t2 
    else if (t2 eq Empty) t1
    else {
      if (t1.count*3 < t2.count) rebalance(t2.x, concat(t1, t2.l), t2.r) 
      else if (t2.count*3 < t1.count) rebalance(t1.x, t1.l, concat(t1.r, t2))
      else rebalance(min(t2), t1, deleteMin(t2))
    }
  }
  
  private def concat3(l: WorkTreeSet, v: Key, r: WorkTreeSet): WorkTreeSet = {
    if (l eq Empty) r + v
    else if (r eq Empty) l + v
    else {
      if (l.count*3 < r.count) rebalance(r.x, concat3(l, v, r.l), r.r)
      else if (r.count*3 < l.count) rebalance(l.x, l.l, concat3(l.r, v, r))
      else new WorkTreeSet(v, l.count + r.count + 1, l, r);
    }
  }
  
  private def unionHigh(t1: WorkTreeSet, t2: WorkTreeSet, hi: Key): WorkTreeSet = {
    if (t2 eq Empty) t1
    else if (t1 eq Empty) concat3(t2.l, t2.x, splitLT(t2.r, hi))
    else {
      val t1x = t1.x;
      concat3(unionHigh(t1.l, trimHigh(t1x, t2), t1x), 
              t1x,
              unionBD(t1.r, trim(t1x, hi, t2), t1x, hi))
    }
  }
  
  private def unionLow(t1: WorkTreeSet, t2: WorkTreeSet, lo: Key): WorkTreeSet = {
    if (t2 eq Empty) t1
    else if (t1 eq Empty) concat3(splitGT(t2.l, lo), t2.x, t2.r)
    else {
      val t1x = t1.x
      concat3(unionBD(t1.l, trim(lo, t1x, t2), lo, t1x),
              t1x,
              unionLow(t1.r, trimLow(t1x, t2), t1x))
    }
  }
  
  private def unionBD(t1: WorkTreeSet, t2: WorkTreeSet, lo: Key, hi: Key): WorkTreeSet = {
    if (t2 eq Empty) t1
    else if (t1 eq Empty) concat3(splitGT(t2.l, lo), t2.x, splitLT(t2.r, hi))
    else {
      val t1x = t1.x
      concat3(unionBD(t1.l, trim(lo, t1x, t2), lo, t1x),
              t1x,
              unionBD(t1.r, trim(t1x, hi, t2), t1x, hi))
    }
  }
  
  private def splitGT(t: WorkTreeSet, x: Key): WorkTreeSet = {
    if (t eq Empty) t
    else {
      val tx = t.x
      val c = compareKey(tx, x)
      if (c < 0) splitGT(t.r, x)
      else if (c > 0) concat3(splitGT(t.l, x), tx, t.r)
      else t.r
    }
  }
  
  private def splitLT(t: WorkTreeSet, x: Key): WorkTreeSet = {
    if (t eq Empty) t
    else {
      val tx = t.x
      val c = compareKey(tx, x)
      if (c < 0) concat3(t.l, tx, splitLT(t.r, x))
      else if (c > 0) splitLT(t.l, x)
      else t.l
    }
  }

  private def trim(lo: Key, hi: Key, s: WorkTreeSet): WorkTreeSet = {
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
  
  private def trimHigh(hi: Key, t: WorkTreeSet): WorkTreeSet = {
    if (t eq Empty) t
    else {
      if (compareKey(t.x, hi) < 0) t
      else trimHigh(hi, t.l)
    }
  }
  
  private def trimLow(lo: Key, t: WorkTreeSet): WorkTreeSet = {
    if (t eq Empty) t
    else {
      if (compareKey(t.x, lo) > 0) return t
      else trimLow(lo, t.r)
    }
  }
}
