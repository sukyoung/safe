/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

class Pipe[A](a: A) {
  def |>[B](f: A => B) = f(a)
}

object Pipe {
  def apply[A](v: A) = new Pipe(v)
}

object PipeOps {
  implicit def toPipe[A](a: A) = Pipe(a)
}
