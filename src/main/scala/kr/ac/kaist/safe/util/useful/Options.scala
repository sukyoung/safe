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

package kr.ac.kaist.safe.util.useful

import _root_.edu.rice.cs.plt.tuple.{ Option => JavaOption }

object Options {
  /* Transforms a Java option to a Scala option */
  implicit def toOption[T](opt: JavaOption[T]): Option[T] =
    if (opt.isNone) None
    else Some(opt.unwrap)

  implicit def toJavaOption[T](op: Option[T]): JavaOption[T] = op match {
    case Some(wrap) => some(wrap)
    case None => none()
  }

  def some[T](wrapped: T): JavaOption[T] = JavaOption.some(wrapped)

  def none[T](): JavaOption[T] = JavaOption.none()
}

object JavaSome {
  def apply[T](wrapped: T): JavaOption[T] = JavaOption.some(wrapped)

  def unapply[T](opt: JavaOption[T]) =
    if (opt.isNone) None
    else Some(opt.unwrap)
}

object JavaNone {
  def apply[T](): JavaOption[T] = JavaOption.none()

  def unapply[T](opt: JavaOption[T]) =
    if (opt.isNone) Some()
    else None
}
