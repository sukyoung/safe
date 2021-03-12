/**
 * *****************************************************************************
 * Copyright (c) 2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

import scala.annotation.tailrec
import scala.annotation.unchecked.{ uncheckedVariance => uV }
import scala.collection.generic._
import scala.collection.immutable._
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.immutable.ParHashMap
import scala.collection.{ CustomParallelizable, Iterator }
import HashMap.{ HashTrieMap, HashMapCollision1, HashMap1 }

/**
 * This class implements immutable maps using a hash trie.
 *
 *  '''Note:''' The builder of this hash map may return specialized representations for small maps.
 *
 *  @tparam A      the type of the keys contained in this hash map.
 *  @tparam B      the type of the values associated with the keys.
 *
 *  @author  Martin Odersky
 *  @author  Tiark Rompf
 *  @version 2.8
 *  @since   2.3
 *  @see [[http://docs.scala-lang.org/overviews/collections/concrete-immutable-collection-classes.html#hash-tries "Scala's Collection Library overview"]]
 *  section on `Hash Tries` for more information.
 *  @define Coll `immutable.HashMap`
 *  @define coll immutable hash map
 *  @define mayNotTerminateInf
 *  @define willNotTerminateInf
 */
@SerialVersionUID(2L)
sealed class HashMap[A, +B] extends AbstractMap[A, B]
    with Map[A, B]
    with MapLike[A, B, HashMap[A, B]]
    with Serializable
    with CustomParallelizable[(A, B), ParHashMap[A, B]] {
  import HashMap.{ nullToEmpty, bufferSize }

  override def size: Int = 0

  override def empty = HashMap.empty[A, B]

  def iterator: Iterator[(A, B)] = Iterator.empty

  override def foreach[U](f: ((A, B)) => U): Unit = ()

  def get(key: A): Option[B] =
    get0(key, computeHash(key), 0)

  override final def contains(key: A): Boolean =
    contains0(key, computeHash(key), 0)

  override def updated[B1 >: B](key: A, value: B1): HashMap[A, B1] =
    updated0(key, computeHash(key), 0, value, null, null)

  override def +[B1 >: B](kv: (A, B1)): HashMap[A, B1] =
    updated0(kv._1, computeHash(kv._1), 0, kv._2, kv, null)

  override def +[B1 >: B](elem1: (A, B1), elem2: (A, B1), elems: (A, B1)*): HashMap[A, B1] =
    this + elem1 + elem2 ++ elems

  def -(key: A): HashMap[A, B] =
    removed0(key, computeHash(key), 0)

  override def tail: HashMap[A, B] = this - head._1

  override def filter(p: ((A, B)) => Boolean) = {
    val buffer = new Array[HashMap[A, B]](bufferSize(size))
    nullToEmpty(filter0(p, false, 0, buffer, 0))
  }

  override def filterNot(p: ((A, B)) => Boolean) = {
    val buffer = new Array[HashMap[A, B]](bufferSize(size))
    nullToEmpty(filter0(p, true, 0, buffer, 0))
  }

  protected def filter0(p: ((A, B)) => Boolean, negate: Boolean, level: Int, buffer: Array[HashMap[A, B @uV]], offset0: Int): HashMap[A, B] = null

  protected def elemHashCode(key: A) = key.##

  protected final def improve(hcode: Int) = {
    var h: Int = hcode + ~(hcode << 9)
    h = h ^ (h >>> 14)
    h = h + (h << 4)
    h ^ (h >>> 10)
  }

  def computeHash(key: A) = improve(elemHashCode(key))

  import HashMap.{ Merger, MergeFunction, liftMerger }
  type PartialOrder[B1] = (B1, B1) => Boolean
  type OptionPartialOrder[B1] = (Option[B1], Option[B1]) => Boolean

  def get0(key: A, hash: Int, level: Int): Option[B] = None
  protected def contains0(key: A, hash: Int, level: Int): Boolean = false
  def updated0[B1 >: B](key: A, hash: Int, level: Int, value: B1, kv: (A, B1), merger: Merger[A, B1]): HashMap[A, B1] =
    new HashMap.HashMap1(key, hash, value, kv)
  def updatedOption0[B1 >: B](key: A, hash: Int, level: Int, value: Option[B1], mergef: (Option[B1], Option[B1]) => B1): HashMap[A, B1] = {
    val nkv = (key, mergef(None, value))
    new HashMap.HashMap1(nkv._1, hash, nkv._2, nkv)
  }

  protected def removed0(key: A, hash: Int, level: Int): HashMap[A, B] = this

  protected def writeReplace(): AnyRef = new HashMap.SerializationProxy(this)

  def split: Seq[HashMap[A, B]] = Seq(this)

  /**
   * Creates a new map which is the merge of this and the argument hash map.
   *
   *  Uses the specified collision resolution function if two keys are the same.
   *  The collision resolution function will always take the first argument from
   *  `this` hash map and the second from `that`.
   *
   *  The `merged` method is on average more performant than doing a traversal and reconstructing a
   *  new immutable hash map from scratch, or `++`.
   *
   *  @tparam B1      the value type of the other hash map
   *  @param that     the other hash map
   *  @param mergef   the merge function or null if the first key-value pair is to be picked
   */
  def merged[B1 >: B](that: HashMap[A, B1])(mergef: MergeFunction[A, B1]): HashMap[A, B1] = merge0(that, 0, liftMerger(mergef))
  protected def merge0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = that

  /**
   * Creates a new map which is the merge of this and the argument hash map with
   * an idempotent collision resolution.
   *
   *  Uses the specified collision resolution function if two keys are the same.
   *  The collision resolution function will always take the first argument from
   *  `this` hash map and the second from `that`.
   *
   *  @tparam B1      the value type of the other hash map
   *  @param that     the other hash map
   *  @param mergef   the merge function
   */
  def mergeWithIdem[B1 >: B](
    that: HashMap[A, B1]
  )(mergef: (Option[B1], Option[B1]) => B1): HashMap[A, B1] = {
    if (this eq that) this
    else mergeWithIdem0(that, 0, mergef)
  }
  protected def mergeWithIdem0[B1 >: B](
    that: HashMap[A, B1], level: Int, mergef: (Option[B1], Option[B1]) => B1
  ): HashMap[A, B1] = {
    if (this eq that) this
    else (that /: that.keySet) {
      case (m, k) =>
        m.updatedOption0(k, computeHash(k), level, None, (l, r) => mergef(r, l))
    }
  }

  /**
   * Creates a new map which is the union of this and the argument hash map with
   * an idempotent collision resolution.
   *
   *  Uses the specified collision resolution function if two keys are the same.
   *  The collision resolution function will always take the first argument from
   *  `this` hash map and the second from `that`.
   *
   *  @tparam B1      the value type of the other hash map
   *  @param that     the other hash map
   *  @param mergef   the merge function
   */
  def unionWithIdem[B1 >: B](
    that: HashMap[A, B1]
  )(mergef: (B1, B1) => B1): HashMap[A, B1] = {
    if (this eq that) this
    else unionWithIdem0(that, 0, liftMerger[A, B1] {
      case ((k, l), (_, r)) => k -> mergef(l, r)
    })
  }
  protected def unionWithIdem0[B1 >: B](
    that: HashMap[A, B1], level: Int, merger: Merger[A, B1]
  ): HashMap[A, B1] = that

  /**
   * Creates a new map which is the intersection of this and the argument hash map with
   * an idempotent collision resolution.
   *
   *  Uses the specified collision resolution function if two keys are the same.
   *  The collision resolution function will always take the first argument from
   *  `this` hash map and the second from `that`.
   *
   *  @tparam B1      the value type of the other hash map
   *  @param that     the other hash map
   *  @param mergef   the merge function
   */
  def intersectWithIdem[B1 >: B](
    that: HashMap[A, B1]
  )(mergef: (B1, B1) => B1): HashMap[A, B1] = {
    if (this eq that) this
    else intersectWithIdem0(that, 0, liftMerger[A, B1] {
      case ((k, l), (_, r)) => k -> mergef(l, r)
    })
  }
  protected def intersectWithIdem0[B1 >: B](
    that: HashMap[A, B1], level: Int, merger: Merger[A, B1]
  ): HashMap[A, B1] = this

  /**
   * Checks whether this is a subset of the argument hash map based on a given relation.
   *
   *  @tparam B1      the value type of the other hash map
   *  @param that     the other hash map
   *  @param order    the compare function
   */
  def compareOptionWithPartialOrder[B1 >: B](
    that: HashMap[A, B1]
  )(order: OptionPartialOrder[B1]): Boolean = {
    if (this eq that) true
    else compareOptionWithPartialOrder0(that, 0, order)
  }
  protected def compareOptionWithPartialOrder0[B1 >: B](
    that: HashMap[A, B1], level: Int, order: OptionPartialOrder[B1]
  ): Boolean = {
    if (this eq that) true
    else that.keySet.forall(k => order(None, that.get0(k, computeHash(k), level)))
  }

  /**
   * Checks whether this is a subset of the argument hash map based on a given relation.
   *
   *  @tparam B1      the value type of the other hash map
   *  @param that     the other hash map
   *  @param order    the compare function
   */
  def compareWithPartialOrder[B1 >: B](
    that: HashMap[A, B1]
  )(order: PartialOrder[B1]): Boolean = {
    if (this eq that) true
    else compareWithPartialOrder0(that, 0, order)
  }
  protected def compareWithPartialOrder0[B1 >: B](
    that: HashMap[A, B1], level: Int, order: PartialOrder[B1]
  ): Boolean = true
}

/**
 * $factoryInfo
 *  @define Coll `immutable.HashMap`
 *  @define coll immutable hash map
 *
 *  @author  Tiark Rompf
 *  @since   2.3
 */
object HashMap extends ImmutableMapFactory[HashMap] {

  abstract class Merger[A, B] {
    def apply(kv1: (A, B), kv2: (A, B)): (A, B)
    def invert: Merger[A, B]
  }

  type MergeFunction[A1, B1] = ((A1, B1), (A1, B1)) => (A1, B1)
  type PartialOrder[A1, B1] = ((A1, B1), (A1, B1)) => Boolean

  def liftMerger[A1, B1](mergef: MergeFunction[A1, B1]): Merger[A1, B1] =
    if (mergef == null) defaultMerger.asInstanceOf[Merger[A1, B1]] else liftMerger0(mergef)

  val defaultMerger: Merger[Any, Any] = liftMerger0((a, b) => a)

  def liftMerger0[A1, B1](mergef: MergeFunction[A1, B1]): Merger[A1, B1] = new Merger[A1, B1] {
    self =>
    def apply(kv1: (A1, B1), kv2: (A1, B1)): (A1, B1) = mergef(kv1, kv2)
    val invert: Merger[A1, B1] = new Merger[A1, B1] {
      def apply(kv1: (A1, B1), kv2: (A1, B1)): (A1, B1) = mergef(kv2, kv1)
      def invert: Merger[A1, B1] = self
    }
  }

  /** $mapCanBuildFromInfo */
  implicit def canBuildFrom[A, B]: CanBuildFrom[Coll, (A, B), HashMap[A, B]] = new MapCanBuildFrom[A, B]
  def empty[A, B]: HashMap[A, B] = EmptyHashMap.asInstanceOf[HashMap[A, B]]

  object EmptyHashMap extends HashMap[Any, Nothing] {
    override def head: (Any, Nothing) = throw new NoSuchElementException("Empty Map")
    override def tail: HashMap[Any, Nothing] = throw new NoSuchElementException("Empty Map")
  }

  // utility method to create a HashTrieMap from two leaf HashMaps (HashMap1 or HashMapCollision1) with non-colliding hash code)
  def makeHashTrieMap[A, B](hash0: Int, elem0: HashMap[A, B], hash1: Int, elem1: HashMap[A, B], level: Int, size: Int): HashTrieMap[A, B] = {
    val index0 = (hash0 >>> level) & 0x1f
    val index1 = (hash1 >>> level) & 0x1f
    if (index0 != index1) {
      val bitmap = (1 << index0) | (1 << index1)
      val elems = new Array[HashMap[A, B]](2)
      if (index0 < index1) {
        elems(0) = elem0
        elems(1) = elem1
      } else {
        elems(0) = elem1
        elems(1) = elem0
      }
      new HashTrieMap[A, B](bitmap, elems, size)
    } else {
      val elems = new Array[HashMap[A, B]](1)
      val bitmap = (1 << index0)
      elems(0) = makeHashTrieMap(hash0, elem0, hash1, elem1, level + 5, size)
      new HashTrieMap[A, B](bitmap, elems, size)
    }
  }

  @deprecatedInheritance("This class will be made final in a future release.", "2.12.2")
  class HashMap1[A, +B](val key: A, val hash: Int, val value: (B @uV), var kv: (A, B @uV)) extends HashMap[A, B] {
    override def size = 1

    def getKey = key
    def getHash = hash
    def computeHashFor(k: A) = computeHash(k)

    override def get0(key: A, hash: Int, level: Int): Option[B] =
      if (hash == this.hash && key == this.key) Some(value) else None

    override protected def contains0(key: A, hash: Int, level: Int): Boolean =
      hash == this.hash && key == this.key
    override def updated0[B1 >: B](key: A, hash: Int, level: Int, value: B1, kv: (A, B1), merger: Merger[A, B1]): HashMap[A, B1] =
      if (hash == this.hash && key == this.key) {
        if (merger eq null) {
          if (this.value.asInstanceOf[AnyRef] eq value.asInstanceOf[AnyRef]) this
          else new HashMap1(key, hash, value, kv)
        } else {
          val nkv = merger(this.ensurePair, if (kv != null) kv else (key, value))
          new HashMap1(nkv._1, hash, nkv._2, nkv)
        }
      } else {
        if (hash != this.hash) {
          // they have different hashes, but may collide at this level - find a level at which they don't
          val that = new HashMap1[A, B1](key, hash, value, kv)
          makeHashTrieMap[A, B1](this.hash, this, hash, that, level, 2)
        } else {
          // 32-bit hash collision (rare, but not impossible)
          new HashMapCollision1(hash, ListMap.empty.updated(this.key, this.value).updated(key, value))
        }
      }

    override def updatedOption0[B1 >: B](key: A, hash: Int, level: Int, value: Option[B1], mergef: (Option[B1], Option[B1]) => B1): HashMap[A, B1] = {
      if (hash == this.hash && key == this.key) {
        val nkv = (key -> mergef(Some(this.value), value))
        new HashMap1(nkv._1, hash, nkv._2, nkv)
      } else if (hash != this.hash) {
        val nkv = (key -> mergef(None, value))
        val that = new HashMap1[A, B1](nkv._1, hash, nkv._2, nkv)
        makeHashTrieMap[A, B1](this.hash, this, hash, that, level, 2)
      } else {
        new HashMapCollision1(hash, ListMap.empty.updated(this.key, this.value).updated(key, mergef(None, value)))
      }
    }

    override def removed0(key: A, hash: Int, level: Int): HashMap[A, B] =
      if (hash == this.hash && key == this.key) HashMap.empty[A, B] else this

    override protected def filter0(p: ((A, B)) => Boolean, negate: Boolean, level: Int, buffer: Array[HashMap[A, B @uV]], offset0: Int): HashMap[A, B] =
      if (negate ^ p(ensurePair)) this else null

    override def iterator: Iterator[(A, B)] = Iterator(ensurePair)
    override def foreach[U](f: ((A, B)) => U): Unit = f(ensurePair)
    // this method may be called multiple times in a multithreaded environment, but that's ok
    def ensurePair: (A, B) = if (kv ne null) kv else { kv = (key, value); kv }
    protected override def merge0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = {
      that.updated0(key, hash, level, value, kv, merger.invert)
    }
    protected override def mergeWithIdem0[B1 >: B](that: HashMap[A, B1], level: Int, mergef: (Option[B1], Option[B1]) => B1): HashMap[A, B1] = {
      if (this eq that) this
      else (that /: (that.keySet + key)) {
        case (m, k) =>
          val v = if (k == key) Some(value) else None
          m.updatedOption0(k, computeHash(k), level, v, (l, r) => mergef(r, l))
      }
    }
    protected override def unionWithIdem0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = {
      that.updated0(key, hash, level, value, kv, merger.invert)
    }
    protected override def intersectWithIdem0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = that.get(key) match {
      case Some(value) =>
        val nkv = merger(this.ensurePair, (key, value))
        new HashMap1(nkv._1, hash, nkv._2, nkv)
      case None => this
    }
    protected override def compareOptionWithPartialOrder0[B1 >: B](that: HashMap[A, B1], level: Int, order: OptionPartialOrder[B1]): Boolean = {
      if (this eq that) true
      else (that.keySet + key).forall(k => {
        val v1 = if (k == key) Some(value) else None
        order(v1, that.get0(k, computeHash(k), level))
      })
    }
    protected override def compareWithPartialOrder0[B1 >: B](that: HashMap[A, B1], level: Int, order: PartialOrder[B1]): Boolean = that.get(key) match {
      case Some(thatValue) => order(value, thatValue)
      case None => false
    }
  }

  class HashMapCollision1[A, +B](val hash: Int, val kvs: ListMap[A, B @uV])
      extends HashMap[A, B @uV] {
    // assert(kvs.size > 1)

    override def size = kvs.size

    override def get0(key: A, hash: Int, level: Int): Option[B] =
      if (hash == this.hash) kvs.get(key) else None

    override protected def contains0(key: A, hash: Int, level: Int): Boolean =
      hash == this.hash && kvs.contains(key)

    override def updated0[B1 >: B](key: A, hash: Int, level: Int, value: B1, kv: (A, B1), merger: Merger[A, B1]): HashMap[A, B1] =
      if (hash == this.hash) {
        if ((merger eq null) || !kvs.contains(key)) new HashMapCollision1(hash, kvs.updated(key, value))
        else new HashMapCollision1(hash, kvs + merger((key, kvs(key)), kv))
      } else {
        val that = new HashMap1(key, hash, value, kv)
        makeHashTrieMap(this.hash, this, hash, that, level, size + 1)
      }

    override def updatedOption0[B1 >: B](key: A, hash: Int, level: Int, value: Option[B1], mergef: (Option[B1], Option[B1]) => B1): HashMap[A, B1] = {
      if (hash == this.hash) {
        new HashMapCollision1(hash, kvs.updated(key, mergef(kvs.get(key), value)))
      } else {
        val nkv = (key, mergef(None, value))
        val thatM = new HashMap1(nkv._1, hash, nkv._2, nkv)
        makeHashTrieMap(this.hash, this, hash, thatM, level, size + 1)
      }
    }

    override def removed0(key: A, hash: Int, level: Int): HashMap[A, B] =
      if (hash == this.hash) {
        val kvs1 = kvs - key
        kvs1.size match {
          case 0 =>
            HashMap.empty[A, B]
          case 1 =>
            val kv = kvs1.head
            new HashMap1(kv._1, hash, kv._2, kv)
          case x if x == kvs.size =>
            this
          case _ =>
            new HashMapCollision1(hash, kvs1)
        }
      } else this

    override protected def filter0(p: ((A, B)) => Boolean, negate: Boolean, level: Int, buffer: Array[HashMap[A, B @uV]], offset0: Int): HashMap[A, B] = {
      val kvs1 = if (negate) kvs.filterNot(p) else kvs.filter(p)
      kvs1.size match {
        case 0 =>
          null
        case 1 =>
          val kv @ (k, v) = kvs1.head
          new HashMap1(k, hash, v, kv)
        case x if x == kvs.size =>
          this
        case _ =>
          new HashMapCollision1(hash, kvs1)
      }
    }

    override def iterator: Iterator[(A, B)] = kvs.iterator
    override def foreach[U](f: ((A, B)) => U): Unit = kvs.foreach(f)
    override def split: Seq[HashMap[A, B]] = {
      val (x, y) = kvs.splitAt(kvs.size / 2)
      def newhm(lm: ListMap[A, B @uV]) = new HashMapCollision1(hash, lm)
      List(newhm(x), newhm(y))
    }
    protected override def merge0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = {
      // this can be made more efficient by passing the entire ListMap at once
      var m = that
      for (p <- kvs) m = m.updated0(p._1, this.hash, level, p._2, p, merger.invert)
      m
    }
    protected override def mergeWithIdem0[B1 >: B](that: HashMap[A, B1], level: Int, mergef: (Option[B1], Option[B1]) => B1): HashMap[A, B1] = {
      if (this eq that) this
      else (that /: (that.keySet ++ kvs.keySet)) {
        case (m, k) =>
          m.updatedOption0(k, computeHash(k), level, kvs.get(k), (l, r) => mergef(r, l))
      }
    }
    protected override def unionWithIdem0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = {
      var m = that
      for (p <- kvs) m = m.updated0(p._1, this.hash, level, p._2, p, merger.invert)
      m
    }
    protected override def intersectWithIdem0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = {
      var m: HashMap[A, B1] = this
      for (p <- kvs) that.get(p._1) match {
        case Some(v) =>
          val nkv = merger(p, (p._1, v))
          m = m.updated0(nkv._1, hash, level, nkv._2, nkv, merger)
        case None =>
      }
      m
    }
    protected override def compareOptionWithPartialOrder0[B1 >: B](that: HashMap[A, B1], level: Int, order: OptionPartialOrder[B1]): Boolean = {
      if (this eq that) true
      else (that.keySet ++ kvs.keySet).forall(k => {
        order(kvs.get(k), that.get0(k, computeHash(k), level))
      })
    }
    protected override def compareWithPartialOrder0[B1 >: B](that: HashMap[A, B1], level: Int, order: PartialOrder[B1]): Boolean = {
      var res = true
      var it = kvs.iterator
      while (res && it.hasNext) {
        val (k, v1) = it.next()
        res = that.get(k) match {
          case Some(v2) => order(v1, v2)
          case None => false
        }
      }
      res
    }
  }

  @deprecatedInheritance("This class will be made final in a future release.", "2.12.2")
  class HashTrieMap[A, +B](
      val bitmap: Int,
      val elems: Array[HashMap[A, B @uV]],
      val size0: Int
  ) extends HashMap[A, B @uV] {
    // assert(Integer.bitCount(bitmap) == elems.length)
    // assert(elems.length > 1 || (elems.length == 1 && elems(0).isInstanceOf[HashTrieMap[_,_]]))

    override def size = size0

    override def get0(key: A, hash: Int, level: Int): Option[B] = {
      // Note: this code is duplicated with `contains0`
      val index = (hash >>> level) & 0x1f
      if (bitmap == -1) {
        elems(index).get0(key, hash, level + 5)
      } else {
        val mask = (1 << index)
        if ((bitmap & mask) != 0) {
          val offset = Integer.bitCount(bitmap & (mask - 1))
          elems(offset).get0(key, hash, level + 5)
        } else {
          None
        }
      }
    }

    override protected def contains0(key: A, hash: Int, level: Int): Boolean = {
      // Note: this code is duplicated from `get0`
      val index = (hash >>> level) & 0x1f
      if (bitmap == -1) {
        elems(index).contains0(key, hash, level + 5)
      } else {
        val mask = (1 << index)
        if ((bitmap & mask) != 0) {
          val offset = Integer.bitCount(bitmap & (mask - 1))
          elems(offset).contains0(key, hash, level + 5)
        } else {
          false
        }
      }
    }

    override def updated0[B1 >: B](key: A, hash: Int, level: Int, value: B1, kv: (A, B1), merger: Merger[A, B1]): HashMap[A, B1] = {
      val index = (hash >>> level) & 0x1f
      val mask = (1 << index)
      val offset = Integer.bitCount(bitmap & (mask - 1))
      if ((bitmap & mask) != 0) {
        val sub = elems(offset)
        val subNew = sub.updated0(key, hash, level + 5, value, kv, merger)
        if (subNew eq sub) this else {
          val elemsNew = new Array[HashMap[A, B1]](elems.length)
          Array.copy(elems, 0, elemsNew, 0, elems.length)
          elemsNew(offset) = subNew
          new HashTrieMap(bitmap, elemsNew, size + (subNew.size - sub.size))
        }
      } else {
        val elemsNew = new Array[HashMap[A, B1]](elems.length + 1)
        Array.copy(elems, 0, elemsNew, 0, offset)
        elemsNew(offset) = new HashMap1(key, hash, value, kv)
        Array.copy(elems, offset, elemsNew, offset + 1, elems.length - offset)
        new HashTrieMap(bitmap | mask, elemsNew, size + 1)
      }
    }

    override def updatedOption0[B1 >: B](key: A, hash: Int, level: Int, value: Option[B1], mergef: (Option[B1], Option[B1]) => B1): HashMap[A, B1] = {
      val index = (hash >>> level) & 0x1f
      val mask = (1 << index)
      val offset = Integer.bitCount(bitmap & (mask - 1))
      if ((bitmap & mask) != 0) {
        val sub = elems(offset)
        val subNew = sub.updatedOption0(key, hash, level + 5, value, mergef)
        if (subNew eq sub) this else {
          val elemsNew = new Array[HashMap[A, B1]](elems.length)
          Array.copy(elems, 0, elemsNew, 0, elems.length)
          elemsNew(offset) = subNew
          new HashTrieMap(bitmap, elemsNew, size + (subNew.size - sub.size))
        }
      } else {
        val elemsNew = new Array[HashMap[A, B1]](elems.length + 1)
        Array.copy(elems, 0, elemsNew, 0, offset)
        val nkv = (key -> mergef(None, value))
        elemsNew(offset) = new HashMap1(nkv._1, hash, nkv._2, nkv)
        Array.copy(elems, offset, elemsNew, offset + 1, elems.length - offset)
        new HashTrieMap(bitmap | mask, elemsNew, size + 1)
      }
    }

    override def removed0(key: A, hash: Int, level: Int): HashMap[A, B] = {
      val index = (hash >>> level) & 0x1f
      val mask = (1 << index)
      val offset = Integer.bitCount(bitmap & (mask - 1))
      if ((bitmap & mask) != 0) {
        val sub = elems(offset)
        val subNew = sub.removed0(key, hash, level + 5)
        if (subNew eq sub) this
        else if (subNew.isEmpty) {
          val bitmapNew = bitmap ^ mask
          if (bitmapNew != 0) {
            val elemsNew = new Array[HashMap[A, B]](elems.length - 1)
            Array.copy(elems, 0, elemsNew, 0, offset)
            Array.copy(elems, offset + 1, elemsNew, offset, elems.length - offset - 1)
            val sizeNew = size - sub.size
            if (elemsNew.length == 1 && !elemsNew(0).isInstanceOf[HashTrieMap[_, _]])
              elemsNew(0)
            else
              new HashTrieMap(bitmapNew, elemsNew, sizeNew)
          } else
            HashMap.empty[A, B]
        } else if (elems.length == 1 && !subNew.isInstanceOf[HashTrieMap[_, _]]) {
          subNew
        } else {
          val elemsNew = new Array[HashMap[A, B]](elems.length)
          Array.copy(elems, 0, elemsNew, 0, elems.length)
          elemsNew(offset) = subNew
          val sizeNew = size + (subNew.size - sub.size)
          new HashTrieMap(bitmap, elemsNew, sizeNew)
        }
      } else {
        this
      }
    }

    override protected def filter0(p: ((A, B)) => Boolean, negate: Boolean, level: Int, buffer: Array[HashMap[A, B @uV]], offset0: Int): HashMap[A, B] = {
      // current offset
      var offset = offset0
      // result size
      var rs = 0
      // bitmap for kept elems
      var kept = 0
      // loop over all elements
      var i = 0
      while (i < elems.length) {
        val result = elems(i).filter0(p, negate, level + 5, buffer, offset)
        if (result ne null) {
          buffer(offset) = result
          offset += 1
          // add the result size
          rs += result.size
          // mark the bit i as kept
          kept |= (1 << i)
        }
        i += 1
      }
      if (offset == offset0) {
        // empty
        null
      } else if (rs == size0) {
        // unchanged
        this
      } else if (offset == offset0 + 1 && !buffer(offset0).isInstanceOf[HashTrieMap[A, B]]) {
        // leaf
        buffer(offset0)
      } else {
        // we have to return a HashTrieMap
        val length = offset - offset0
        val elems1 = new Array[HashMap[A, B]](length)
        System.arraycopy(buffer, offset0, elems1, 0, length)
        val bitmap1 = if (length == elems.length) {
          // we can reuse the original bitmap
          bitmap
        } else {
          // calculate new bitmap by keeping just bits in the kept bitmask
          keepBits(bitmap, kept)
        }
        new HashTrieMap(bitmap1, elems1, rs)
      }
    }

    override def iterator: Iterator[(A, B)] = new TrieIterator[(A, B)](elems.asInstanceOf[Array[Iterable[(A, B)]]]) {
      final override def getElem(cc: AnyRef): (A, B) = cc.asInstanceOf[HashMap1[A, B]].ensurePair
    }

    override def foreach[U](f: ((A, B)) => U): Unit = {
      var i = 0
      while (i < elems.length) {
        elems(i).foreach(f)
        i += 1
      }
    }

    def posOf(n: Int, bm: Int) = {
      var left = n
      var i = -1
      var b = bm
      while (left >= 0) {
        i += 1
        if ((b & 1) != 0) left -= 1
        b = b >>> 1
      }
      i
    }

    override def split: Seq[HashMap[A, B]] = if (size == 1) Seq(this) else {
      val nodesize = Integer.bitCount(bitmap)
      if (nodesize > 1) {
        val splitpoint = nodesize / 2
        val bitsplitpoint = posOf(nodesize / 2, bitmap)
        val bm1 = bitmap & (-1 << bitsplitpoint)
        val bm2 = bitmap & (-1 >>> (32 - bitsplitpoint))

        val (e1, e2) = elems.splitAt(splitpoint)
        val hm1 = new HashTrieMap(bm1, e1, e1.foldLeft(0)(_ + _.size))
        val hm2 = new HashTrieMap(bm2, e2, e2.foldLeft(0)(_ + _.size))

        List(hm1, hm2)
      } else elems(0).split
    }

    protected override def merge0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = that match {
      case hm: HashMap1[_, _] =>
        this.updated0(hm.key, hm.hash, level, hm.value.asInstanceOf[B1], hm.kv, merger)
      case hm: HashTrieMap[_, _] =>
        val that = hm.asInstanceOf[HashTrieMap[A, B1]]
        val thiselems = this.elems
        val thatelems = that.elems
        var thisbm = this.bitmap
        var thatbm = that.bitmap

        // determine the necessary size for the array
        val subcount = Integer.bitCount(thisbm | thatbm)

        // construct a new array of appropriate size
        val merged = new Array[HashMap[A, B1]](subcount)

        // run through both bitmaps and add elements to it
        var i = 0
        var thisi = 0
        var thati = 0
        var totalelems = 0
        while (i < subcount) {
          val thislsb = thisbm ^ (thisbm & (thisbm - 1))
          val thatlsb = thatbm ^ (thatbm & (thatbm - 1))

          // collision
          if (thislsb == thatlsb) {
            val m = thiselems(thisi).merge0(thatelems(thati), level + 5, merger)
            totalelems += m.size
            merged(i) = m
            thisbm = thisbm & ~thislsb
            thatbm = thatbm & ~thatlsb
            thati += 1
            thisi += 1
          } else {
            // condition below is due to 2 things:
            // 1) no unsigned int compare on JVM
            // 2) 0 (no lsb) should always be greater in comparison
            if (unsignedCompare(thislsb - 1, thatlsb - 1)) {
              val m = thiselems(thisi)
              totalelems += m.size
              merged(i) = m
              thisbm = thisbm & ~thislsb
              thisi += 1
            } else {
              val m = thatelems(thati)
              totalelems += m.size
              merged(i) = m
              thatbm = thatbm & ~thatlsb
              thati += 1
            }
          }
          i += 1
        }

        new HashTrieMap[A, B1](this.bitmap | that.bitmap, merged, totalelems)
      case hm: HashMapCollision1[_, _] => that.merge0(this, level, merger.invert)
      case hm: HashMap[_, _] => this
      case _ => sys.error("section supposed to be unreachable.")
    }

    protected override def mergeWithIdem0[B1 >: B](that: HashMap[A, B1], level: Int, mergef: (Option[B1], Option[B1]) => B1): HashMap[A, B1] = that match {
      case hm: HashTrieMap[_, _] =>
        val that = hm.asInstanceOf[HashTrieMap[A, B1]]
        val thiselems = this.elems
        val thatelems = that.elems
        var thisbm = this.bitmap
        var thatbm = that.bitmap

        // determine the necessary size for the array
        val subcount = Integer.bitCount(thisbm | thatbm)

        // construct a new array of appropriate size
        val merged = new Array[HashMap[A, B1]](subcount)

        // run through both bitmaps and add elements to it
        var i = 0
        var thisi = 0
        var thati = 0
        var totalelems = 0
        while (i < subcount) {
          val thislsb = thisbm ^ (thisbm & (thisbm - 1))
          val thatlsb = thatbm ^ (thatbm & (thatbm - 1))

          // collision
          if (thislsb == thatlsb) {
            val m = thiselems(thisi).mergeWithIdem0(thatelems(thati), level + 5, mergef)
            totalelems += m.size
            merged(i) = m
            thisbm = thisbm & ~thislsb
            thatbm = thatbm & ~thatlsb
            thati += 1
            thisi += 1
          } else {
            // condition below is due to 2 things:
            // 1) no unsigned int compare on JVM
            // 2) 0 (no lsb) should always be greater in comparison
            if (unsignedCompare(thislsb - 1, thatlsb - 1)) {
              val m = thiselems(thisi).mergeWithIdem0(EmptyHashMap.asInstanceOf[HashMap[A, B1]], level + 5, mergef)
              totalelems += m.size
              merged(i) = m
              thisbm = thisbm & ~thislsb
              thisi += 1
            } else {
              val m = EmptyHashMap.asInstanceOf[HashMap[A, B1]].mergeWithIdem0(thatelems(thati), level + 5, mergef)
              totalelems += m.size
              merged(i) = m
              thatbm = thatbm & ~thatlsb
              thati += 1
            }
          }
          i += 1
        }

        new HashTrieMap[A, B1](this.bitmap | that.bitmap, merged, totalelems)
      case _ => that.mergeWithIdem0(this, level, (l, r) => mergef(r, l))
    }

    protected override def unionWithIdem0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = that match {
      case hm: HashMap1[_, _] =>
        this.updated0(hm.key, hm.hash, level, hm.value.asInstanceOf[B1], hm.kv, merger)
      case hm: HashTrieMap[_, _] => if (this eq that) this else {
        val that = hm.asInstanceOf[HashTrieMap[A, B1]]
        val thiselems = this.elems
        val thatelems = that.elems
        var thisbm = this.bitmap
        var thatbm = that.bitmap

        // determine the necessary size for the array
        val subcount = Integer.bitCount(thisbm | thatbm)

        // construct a new array of appropriate size
        val merged = new Array[HashMap[A, B1]](subcount)

        // run through both bitmaps and add elements to it
        var i = 0
        var thisi = 0
        var thati = 0
        var totalelems = 0
        while (i < subcount) {
          val thislsb = thisbm ^ (thisbm & (thisbm - 1))
          val thatlsb = thatbm ^ (thatbm & (thatbm - 1))

          // collision
          if (thislsb == thatlsb) {
            val m = thiselems(thisi).unionWithIdem0(thatelems(thati), level + 5, merger)
            totalelems += m.size
            merged(i) = m
            thisbm = thisbm & ~thislsb
            thatbm = thatbm & ~thatlsb
            thati += 1
            thisi += 1
          } else {
            // condition below is due to 2 things:
            // 1) no unsigned int compare on JVM
            // 2) 0 (no lsb) should always be greater in comparison
            if (unsignedCompare(thislsb - 1, thatlsb - 1)) {
              val m = thiselems(thisi)
              totalelems += m.size
              merged(i) = m
              thisbm = thisbm & ~thislsb
              thisi += 1
            } else {
              val m = thatelems(thati)
              totalelems += m.size
              merged(i) = m
              thatbm = thatbm & ~thatlsb
              thati += 1
            }
          }
          i += 1
        }

        new HashTrieMap[A, B1](this.bitmap | that.bitmap, merged, totalelems)
      }
      case hm: HashMapCollision1[_, _] => that.unionWithIdem0(this, level, merger.invert)
      case hm: HashMap[_, _] => this
      case _ => sys.error("section supposed to be unreachable.")
    }

    protected override def intersectWithIdem0[B1 >: B](that: HashMap[A, B1], level: Int, merger: Merger[A, B1]): HashMap[A, B1] = that match {
      case hm: HashTrieMap[_, _] => if (this eq that) this else {
        val that = hm.asInstanceOf[HashTrieMap[A, B1]]
        val thiselems = this.elems
        val thatelems = that.elems
        var thisbm = this.bitmap
        var thatbm = that.bitmap

        // determine the necessary size for the array
        val subcount = Integer.bitCount(thisbm & thatbm)

        // construct a new array of appropriate size
        val merged = new Array[HashMap[A, B1]](subcount)

        // run through both bitmaps and add elements to it
        var i = 0
        var thisi = 0
        var thati = 0
        var totalelems = 0
        while (i < subcount) {
          val thislsb = thisbm ^ (thisbm & (thisbm - 1))
          val thatlsb = thatbm ^ (thatbm & (thatbm - 1))

          // collision
          if (thislsb == thatlsb) {
            val m = thiselems(thisi).intersectWithIdem0(thatelems(thati), level + 5, merger)
            totalelems += m.size
            merged(i) = m
            thisbm = thisbm & ~thislsb
            thatbm = thatbm & ~thatlsb
            thati += 1
            thisi += 1
          } else {
            // condition below is due to 2 things:
            // 1) no unsigned int compare on JVM
            // 2) 0 (no lsb) should always be greater in comparison
            if (unsignedCompare(thislsb - 1, thatlsb - 1)) {
              thisbm = thisbm & ~thislsb
              thisi += 1
            } else {
              thatbm = thatbm & ~thatlsb
              thati += 1
            }
          }
          i += 1
        }

        new HashTrieMap[A, B1](this.bitmap & that.bitmap, merged, totalelems)
      }
      case hm: HashMap[_, _] => that.intersectWithIdem0(this, level, merger.invert)
      case _ => sys.error("section supposed to be unreachable.")
    }

    protected override def compareOptionWithPartialOrder0[B1 >: B](that: HashMap[A, B1], level: Int, order: OptionPartialOrder[B1]): Boolean = that match {
      case hm: HashTrieMap[_, _] =>
        val that = hm.asInstanceOf[HashTrieMap[A, B1]]
        val thiselems = this.elems
        val thatelems = that.elems
        var thisbm = this.bitmap
        var thatbm = that.bitmap

        // determine the necessary size for the array
        val subcount = Integer.bitCount(thisbm | thatbm)

        // construct a new array of appropriate size
        var res = true

        // run through both bitmaps and add elements to it
        var i = 0
        var thisi = 0
        var thati = 0
        while (res && i < subcount) {
          val thislsb = thisbm ^ (thisbm & (thisbm - 1))
          val thatlsb = thatbm ^ (thatbm & (thatbm - 1))

          // collision
          if (thislsb == thatlsb) {
            res = thiselems(thisi).compareOptionWithPartialOrder0(thatelems(thati), level + 5, order)
            thisbm = thisbm & ~thislsb
            thatbm = thatbm & ~thatlsb
            thati += 1
            thisi += 1
          } else if (unsignedCompare(thislsb - 1, thatlsb - 1)) {
            res = thiselems(thisi).compareOptionWithPartialOrder0(EmptyHashMap.asInstanceOf[HashMap[A, B1]], level + 5, order)
            thisbm = thisbm & ~thislsb
            thisi += 1
          } else {
            res = EmptyHashMap.asInstanceOf[HashMap[A, B1]].compareOptionWithPartialOrder0(thatelems(thati), level + 5, order)
            thatbm = thatbm & ~thatlsb
            thati += 1
          }
          i += 1
        }

        res
      case _ => that.compareOptionWithPartialOrder0(this, level, (l, r) => order(r, l))
    }

    protected override def compareWithPartialOrder0[B1 >: B](that: HashMap[A, B1], level: Int, order: PartialOrder[B1]): Boolean = that match {
      case hm: HashTrieMap[_, _] => if (this eq that) true else {
        val that = hm.asInstanceOf[HashTrieMap[A, B1]]
        val thiselems = this.elems
        val thatelems = that.elems
        var thisbm = this.bitmap
        var thatbm = that.bitmap

        // determine the necessary size for the array
        val subcount = Integer.bitCount(thisbm & thatbm)

        // result
        var res = true

        // run through both bitmaps and add elements to it
        var i = 0
        var thisi = 0
        var thati = 0
        var totalelems = 0
        while (res && i < subcount) {
          val thislsb = thisbm ^ (thisbm & (thisbm - 1))
          val thatlsb = thatbm ^ (thatbm & (thatbm - 1))

          // collision
          if (thislsb == thatlsb) {
            res = thiselems(thisi).compareWithPartialOrder0(thatelems(thati), level + 5, order)
            thisbm = thisbm & ~thislsb
            thatbm = thatbm & ~thatlsb
            thati += 1
            thisi += 1
          } else {
            // condition below is due to 2 things:
            // 1) no unsigned int compare on JVM
            // 2) 0 (no lsb) should always be greater in comparison
            if (unsignedCompare(thislsb - 1, thatlsb - 1)) {
              res = false
              thisbm = thisbm & ~thislsb
              thisi += 1
            } else {
              thatbm = thatbm & ~thatlsb
              thati += 1
            }
          }
          i += 1
        }

        res
      }
      case hm: HashMap[_, _] => false
      case _ => sys.error("section supposed to be unreachable.")
    }
  }

  /**
   * Calculates the maximum buffer size given the maximum possible total size of the trie-based collection
   * @param size the maximum size of the collection to be generated
   * @return the maximum buffer size
   */
  @inline def bufferSize(size: Int): Int = (size + 6) min (32 * 7)

  /**
   * In many internal operations the empty map is represented as null for performance reasons. This method converts
   * null to the empty map for use in public methods
   */
  @inline def nullToEmpty[A, B](m: HashMap[A, B]): HashMap[A, B] = if (m eq null) empty[A, B] else m

  /**
   * Utility method to keep a subset of all bits in a given bitmap
   *
   * Example
   *    bitmap (binary): 00000001000000010000000100000001
   *    keep (binary):                               1010
   *    result (binary): 00000001000000000000000100000000
   *
   * @param bitmap the bitmap
   * @param keep a bitmask containing which bits to keep
   * @return the original bitmap with all bits where keep is not 1 set to 0
   */
  def keepBits(bitmap: Int, keep: Int): Int = {
    var result = 0
    var current = bitmap
    var kept = keep
    while (kept != 0) {
      // lowest remaining bit in current
      val lsb = current ^ (current & (current - 1))
      if ((kept & 1) != 0) {
        // mark bit in result bitmap
        result |= lsb
      }
      // clear lowest remaining one bit in abm
      current &= ~lsb
      // look at the next kept bit
      kept >>>= 1
    }
    result
  }

  @SerialVersionUID(2L)
  class SerializationProxy[A, B](@transient var orig: HashMap[A, B]) extends Serializable {
    def writeObject(out: java.io.ObjectOutputStream) {
      val s = orig.size
      out.writeInt(s)
      for ((k, v) <- orig) {
        out.writeObject(k)
        out.writeObject(v)
      }
    }

    def readObject(in: java.io.ObjectInputStream) {
      orig = empty
      val s = in.readInt()
      for (i <- 0 until s) {
        val key = in.readObject().asInstanceOf[A]
        val value = in.readObject().asInstanceOf[B]
        orig = orig.updated(key, value)
      }
    }

    def readResolve(): AnyRef = orig
  }

  def unsignedCompare(i: Int, j: Int) = (i < j) ^ (i < 0) ^ (j < 0)
}

/**
 * Abandons any pretense of type safety for speed.  You can't say I
 *  didn't try: see r23934.
 */
abstract class TrieIterator[+T](elems: Array[Iterable[T]]) extends collection.Iterator[T] {
  outer =>

  def getElem(x: AnyRef): T

  def initDepth = 0
  def initArrayStack: Array[Array[Iterable[T @uV]]] = new Array[Array[Iterable[T]]](6)
  def initPosStack = new Array[Int](6)
  def initArrayD: Array[Iterable[T @uV]] = elems
  def initPosD = 0
  def initSubIter: Iterator[T] = null // to traverse collision nodes

  private[this] var depth = initDepth
  private[this] var arrayStack: Array[Array[Iterable[T @uV]]] = initArrayStack
  private[this] var posStack = initPosStack
  private[this] var arrayD: Array[Iterable[T @uV]] = initArrayD
  private[this] var posD = initPosD
  private[this] var subIter = initSubIter

  private[this] def getElems(x: Iterable[T]): Array[Iterable[T]] = (x match {
    case x: HashTrieMap[_, _] => x.elems
  }).asInstanceOf[Array[Iterable[T]]]

  private[this] def collisionToArray(x: Iterable[T]): Array[Iterable[T]] = (x match {
    case x: HashMapCollision1[_, _] => x.kvs.map((x: (Any, Any)) => HashMap(x)).toArray
  }).asInstanceOf[Array[Iterable[T]]]

  private[this]type SplitIterators = ((Iterator[T], Int), Iterator[T])

  private def isTrie(x: AnyRef) = x match {
    case _: HashTrieMap[_, _] => true
    case _ => false
  }
  private def isContainer(x: AnyRef) = x match {
    case _: HashMap1[_, _] => true
    case _ => false
  }

  final class DupIterator(xs: Array[Iterable[T]] @uV) extends TrieIterator[T](xs) {
    override def initDepth = outer.depth
    override def initArrayStack: Array[Array[Iterable[T @uV]]] = outer.arrayStack
    override def initPosStack = outer.posStack
    override def initArrayD: Array[Iterable[T @uV]] = outer.arrayD
    override def initPosD = outer.posD
    override def initSubIter = outer.subIter

    final override def getElem(x: AnyRef): T = outer.getElem(x)
  }

  def dupIterator: TrieIterator[T] = new DupIterator(elems)

  private[this] def newIterator(xs: Array[Iterable[T]]) = new TrieIterator(xs) {
    final override def getElem(x: AnyRef): T = outer.getElem(x)
  }

  private[this] def iteratorWithSize(arr: Array[Iterable[T]]): (Iterator[T], Int) =
    (newIterator(arr), (arr.map(_.size): Array[Int]).sum)

  private[this] def arrayToIterators(arr: Array[Iterable[T]]): SplitIterators = {
    val (fst, snd) = arr.splitAt(arr.length / 2)

    (iteratorWithSize(snd), newIterator(fst))
  }
  private[this] def splitArray(ad: Array[Iterable[T]]): SplitIterators =
    if (ad.length > 1) arrayToIterators(ad)
    else ad(0) match {
      case _: HashMapCollision1[_, _] =>
        arrayToIterators(collisionToArray(ad(0)))
      case _ =>
        splitArray(getElems(ad(0)))
    }

  def hasNext = (subIter ne null) || depth >= 0
  @throws[NoSuchElementException]
  def next(): T = {
    if (subIter ne null) {
      val el = subIter.next()
      if (!subIter.hasNext)
        subIter = null
      el
    } else
      next0(arrayD, posD)
  }

  @tailrec private[this] def next0(elems: Array[Iterable[T]], i: Int): T = {
    if (i == elems.length - 1) { // reached end of level, pop stack
      depth -= 1
      if (depth >= 0) {
        arrayD = arrayStack(depth)
        posD = posStack(depth)
        arrayStack(depth) = null
      } else {
        arrayD = null
        posD = 0
      }
    } else
      posD += 1

    val m = elems(i)

    // Note: this block is over twice as fast written this way as it is
    // as a pattern match.  Haven't started looking into why that is, but
    // it's pretty sad the pattern matcher is that much slower.
    if (isContainer(m))
      getElem(m) // push current pos onto stack and descend
    else if (isTrie(m)) {
      if (depth >= 0) {
        arrayStack(depth) = arrayD
        posStack(depth) = posD
      }
      depth += 1
      arrayD = getElems(m)
      posD = 0
      next0(getElems(m), 0)
    } else {
      subIter = m.iterator
      next()
    }
  }

  // assumption: contains 2 or more elements
  // splits this iterator into 2 iterators
  // returns the 1st iterator, its number of elements, and the second iterator
  def split: SplitIterators = {
    // 0) simple case: no elements have been iterated - simply divide arrayD
    if (arrayD != null && depth == 0 && posD == 0)
      return splitArray(arrayD)

    // otherwise, some elements have been iterated over
    // 1) collision case: if we have a subIter, we return subIter and elements after it
    if (subIter ne null) {
      val buff = ArrayBuffer.empty.++=(subIter)
      subIter = null
      ((buff.iterator, buff.length), this)
    } else {
      // otherwise find the topmost array stack element
      if (depth > 0) {
        // 2) topmost comes before (is not) arrayD
        //    steal a portion of top to create a new iterator
        if (posStack(0) == arrayStack(0).length - 1) {
          // 2a) only a single entry left on top
          // this means we have to modify this iterator - pop topmost
          val snd = Array[Iterable[T]](arrayStack(0).last)
          val szsnd = snd(0).size
          // modify this - pop
          depth -= 1
          1 until arrayStack.length foreach (i => arrayStack(i - 1) = arrayStack(i))
          arrayStack(arrayStack.length - 1) = Array[Iterable[T]](null)
          posStack = posStack.tail ++ Array[Int](0)
          // we know that `this` is not empty, since it had something on the arrayStack and arrayStack elements are always non-empty
          ((newIterator(snd), szsnd), this)
        } else {
          // 2b) more than a single entry left on top
          val (fst, snd) = arrayStack(0).splitAt(arrayStack(0).length - (arrayStack(0).length - posStack(0) + 1) / 2)
          arrayStack(0) = fst
          (iteratorWithSize(snd), this)
        }
      } else {
        // 3) no topmost element (arrayD is at the top)
        //    steal a portion of it and update this iterator
        if (posD == arrayD.length - 1) {
          // 3a) positioned at the last element of arrayD
          val m = arrayD(posD)
          arrayToIterators(
            if (isTrie(m)) getElems(m)
            else collisionToArray(m)
          )
        } else {
          // 3b) arrayD has more free elements
          val (fst, snd) = arrayD.splitAt(arrayD.length - (arrayD.length - posD + 1) / 2)
          arrayD = fst
          (iteratorWithSize(snd), this)
        }
      }
    }
  }
}
