/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

import scala.collection.immutable

/**
 * By David Chase (from Sun Microsystems)
 */

/**
 * Magic numbers chosen to be:
 * <ul>
 * <li> prime </li>
 * <li> 16 set bits </li>
 * <li> good for 32-bit LFSR (irreducible in GF(2^32) if 2^32 added) </li>
 * <li> positive </li>
 * <li> inverses modulo 0x7fffffffL, 0x80000000L, 0x100000000L also prime
 * </li>
 * </ul>
 */
object MagicNumbers {
  val A = 0x56dfa013
  val B = 0x5c1e0d2f
  val C = 0x3aab43c9
  val D = 0x03a19fe9
  val E = 0x0c3679d3
  val F = 0x6cc54e71
  val G = 0x258f951d
  val H = 0x21ed7817
  val I = 0x1bb6c345
  val J = 0x689b8d8d
  val K = 0x48d51cb7
  val L = 0x46ad91cd
  val M = 0x52db2a55
  val N = 0x1095aef5
  val O = 0x1196e735
  val P = 0x164dd83d
  val Q = 0x78ba8693
  val R = 0x45b701af
  val S = 0x2f0e039f
  val T = 0x547a88bb
  val U = 0x403bf571
  val V = 0x06b5ea1b
  val W = 0x0f5e60ab
  val X = 0x799988b3
  val Y = 0x2e7a950b
  val Z = 0x72438977
  val a = 0x2b5d129d
  val b = 0x34d4d32b
  val c = 0x674f07c1
  val d = 0x56ab29a5
  val e = 0x0d370ce7
  val f = 0x3702d675
  val g = 0x1b495e99
  val h = 0x02bd6759
  val i = 0x7c722593
  val j = 0x1af228d7
  val k = 0x38ae921f
  val l = 0x323e4ad3
  val m = 0x53d41ae9
  val n = 0x3f4e82c9
  val o = 0x62772b45
  val p = 0x3594fc0d
  val q = 0x308cff29
  val r = 0x6167259d
  val s = 0x568d9669
  val t = 0x15ae06af
  val u = 0x207986ef
  val v = 0x5d5a0e2d
  val w = 0x8f738ad
  val x = 0x56a88f8d
  val y = 0x1317529f
  val z = 0x1244f6db

  val array = Array( // 96*13=1248
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, a,
    b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, M, K,
    e, a, s, g, z, F, O, p, G, b, E, U, v, W, C, i, d, n, R, o, Z, P, y, k, w, j, r,
    D, c, A, N, Y, q, H, Q, m, T, L, u, S, I, B, l, V, X, x, h, f, t, J, a, D, Z, X,
    y, G, S, K, T, l, h, w, N, e, c, U, u, A, v, s, C, V, Q, i, q, r, z, W, j, f, p,
    B, n, E, g, J, M, I, k, t, m, O, F, b, o, H, x, P, d, L, Y, R, s, L, i, t, p, E,
    Z, q, m, a, G, j, x, A, l, C, F, H, M, g, T, u, Y, W, n, V, O, f, k, c, e, I, U,
    J, d, y, r, N, R, D, B, v, h, o, P, b, S, K, Q, w, z, X, O, d, G, e, j, L, b, c,
    o, R, H, x, w, y, U, I, i, N, D, u, h, r, X, k, F, S, z, a, M, s, V, T, v, f, C,
    q, W, E, Y, A, g, J, Z, n, p, P, B, t, l, m, Q, K, I, W, U, P, R, E, M, f, e, D,
    z, y, b, K, X, r, T, B, c, L, j, a, v, V, d, p, t, A, C, H, h, u, s, m, i, x, Z,
    J, l, O, n, k, Q, w, Y, N, F, o, G, q, g, S, b, z, h, t, Y, s, S, E, X, k, M, A,
    R, j, o, v, U, i, H, O, B, n, p, r, g, Z, G, x, w, P, C, I, K, d, Q, f, T, l, L,
    y, F, c, e, W, J, a, V, q, m, u, N, D, t, P, p, V, g, M, c, z, L, J, m, G, K, h,
    C, j, F, I, o, Y, U, a, T, i, Q, w, S, v, x, u, k, W, O, R, s, D, b, e, Z, N, l,
    X, E, A, n, f, q, r, H, y, d, B, Y, y, i, N, k, R, p, Q, l, t, M, x, q, I, J, U,
    E, f, b, s, L, F, d, v, O, a, X, u, Z, C, h, e, j, D, H, g, w, K, B, P, W, o, m,
    n, r, A, c, z, T, G, S, V, X, H, M, h, x, y, A, C, n, B, f, q, G, g, j, e, i, K,
    P, F, m, w, V, Z, E, c, S, t, b, Y, a, L, o, u, U, Q, v, R, d, I, s, D, p, O, k,
    r, W, N, J, l, T, z, e, v, j, r, B, R, b, u, h, P, S, g, A, z, V, w, L, W, C, y,
    x, t, o, a, H, k, N, I, U, f, m, F, Y, l, i, q, p, D, J, X, O, Z, T, G, d, K, E,
    n, M, Q, s, c, A, S, m, N, d, Y, c, R, V, Z, b, I, B, x, n, o, j, i, f, X, W, O,
    k, w, s, p, M, q, G, r, U, e, z, H, T, u, L, J, y, g, E, C, h, v, D, t, a, Q, l,
    F, K, P, N, r, k, a, s, b, C, S, y, D, l, M, z, m, U, x, E, e, n, d, j, t, L, i,
    T, G, c, o, q, F, Z, v, X, B, Y, P, A, h, p, K, J, V, R, I, O, u, Q, w, f, g, H,
    W, d, g, i, K, Z, B, k, T, J, N, b, q, Q, e, D, j, R, u, A, Y, y, E, H, o, X, t,
    V, F, p, U, m, h, x, P, f, s, G, z, W, r, v, n, l, O, w, I, C, S, L, M, c, a, V,
    f, b, m, q, g, I, U, O, W, u, Q, M, s, x, h, i, z, r, E, l, G, a, p, R, C, c, N,
    A, d, t, y, n, F, j, T, o, B, H, e, Y, P, k, X, Z, w, v, K, D, J, L, S, h, o, x,
    w, W, B, A, F, m, r, c, s, I, S, a, G, y, v, U, g, q, N, n, t, f, Z, K, e, j, u,
    l, E, d, J, i, Q, k, P, D, V, X, p, L, M, H, z, R, O, b, T, Y, C, I, c, l, K, H,
    d, C, B, a, p, A, e, W, q, O, n, x, i, u, V, v, X, Z, G, Y, k, b, Q, M, w, j, h,
    S, f, L, F, y, D, g, s, R, U, m, o, P, N, t, T, J, E, z, r, K, w, Z, t, Y, A, U,
    c, g, P, J, b, S, l, j, q, R, h, O, H, x, N, y, i, L, M, C, n, Q, E, p, o, V, W,
    d, r, I, m, k, z, D, a, B, e, X, F, T, G, u, s, f, v, H, s, i, z, p, W, Z, L, D,
    K, A, Q, T, h, P, x, B, F, I, d, g, R, f, n, V, N, a, E, k, j, l, m, q, r, u, Y,
    M, S, v, w, o, O, J, X, G, e, U, c, y, b, C, t, a, u, A, l, Q, s, e, j, X, v, F,
    T, b, Y, q, m, i, S, w, R, J, M, t, k, B, U, C, K, I, H, N, g, O, E, x, L, n, W,
    f, h, d, r, Z, V, p, G, o, z, P, D, y, c, m, n, q, G, v, W, I, E, l, w, C, Y, e,
    c, t, M, g, o, j, s, Q, K, O, N, u, p, z, h, x, F, J, R, H, D, i, T, Z, r, P, a,
    V, k, y, b, f, S, B, X, A, L, d, U, e, C, J, z, f, H, U, W, t, k, I, h, v, Q, X,
    O, q, i, F, a, o, V, p, M, d, Z, A, T, b, G, x, m, c, u, y, E, g, P, s, Y, B, j,
    l, n, r, K, N, L, D, R, S, w, z, G, p, N, d, I, j, C, s, r, D, A, c, J, x, Q, B,
    m, X, f, q, K, g, i, y, b, U, O, T, R, k, n, a, v, H, w, e, l, Z, S, P, t, W, h,
    V, E, L, M, F, Y, u, o, f, Y, i, S, k, v, p, y, P, z, I, T, L, m, r, q, W, E, e,
    H, s, B, u, O, a, K, t, R, Z, x, J, l, Q, w, F, d, b, c, o, G, n, X, D, V, M, h,
    j, C, g, A, U, N
  )

  def a(ii: Int): Int = {
    val i = ii & 0x7fffffff // i is now positive
    if (i < array.length) {
      // Tossing a bone to the compiler...
      array(i)
    } else array(i % array.length)
  }

  def uniformHash(serial: Int, f2: Int, t2: Int): Int = {
    var t0: Long = (0xffffffffL & serial.asInstanceOf[Long]) * f2 + t2
    var t1: Long = (t0 >>> 31)
    t0 = (0x7fffffffL & t0) + t1 /* perhaps 34 bits */
    t1 = t0 >>> 31
    t0 = (0x7fffffffL & t0) + t1 /* perhaps 32 bits */
    var tt: Int = t0.asInstanceOf[Int]
    tt = tt - ((tt >> 31) & 0x7fffffff) /* fixes all but 0xffffffff */
    tt = tt - ((tt >> 31) & 0x7fffffff) /* fixes 0x80000000 */
    tt /* Can also return 0x7fffffff -- not an actual problem */
  }
}
