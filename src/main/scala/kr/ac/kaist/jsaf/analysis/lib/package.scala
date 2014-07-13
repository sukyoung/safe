/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis
import kr.ac.kaist.jsaf.analysis.typing.domain.{LocSet, LocSetBot}

package object lib {
  /**
   * Code for |> operator.
   */
  case class ToPipe[A](a: A) {
    def |>[B](f: A => B) = f(a)
  }
  implicit def convert[A](s: A) = ToPipe(s)

  def getSomeDefault[T,R](map: Map[T, R], n: T, default: R): R = {
    map.get(n) match {
      case Some(s) => s
      case None => default
    }
  }

  def getSet[T,R](map: Map[T, Set[R]], n: T) = {
    getSomeDefault(map,n,Set[R]())
  }

  def getLocSet[T](map: Map[T, LocSet], n: T) = {
    getSomeDefault(map,n,LocSetBot)
  }
}
