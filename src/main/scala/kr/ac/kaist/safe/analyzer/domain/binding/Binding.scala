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

package kr.ac.kaist.safe.analyzer.domain

// concrete binding type
abstract class Binding

// mutable binding
case class MBinding(value: Value) extends Binding

// immutable binding
case class IBinding(valueOpt: Option[Value]) extends Binding
