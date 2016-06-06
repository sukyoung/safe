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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain._

case class Operator(utils: Utils) { //TODO
  def ToUInt32(v: Value): AbsNumber = utils.absNumber.Bot

  /* unary operator */
  /* void */
  def uVoid(value: Value): Value = utils.ValueBot
  /* + */
  def uopPlus(value: Value): Value = utils.ValueBot

  /* - */
  def uopMinus(value: Value): Value = utils.ValueBot

  /* - */
  def uopMinusBetter(h: Heap, value: Value): Value = utils.ValueBot

  /* ~ */
  def uopBitNeg(value: Value): Value = utils.ValueBot

  /* ! */
  def uopNeg(value: Value): Value = utils.ValueBot

  /* binary operator */
  /* | */
  def bopBitOr(left: Value, right: Value): Value = utils.ValueBot

  /* & */
  def bopBitAnd(left: Value, right: Value): Value = utils.ValueBot

  /* ^ */
  def bopBitXor(left: Value, right: Value): Value = utils.ValueBot

  /* << */
  def bopLShift(left: Value, right: Value): Value = utils.ValueBot

  /* >> */
  def bopRShift(left: Value, right: Value): Value = utils.ValueBot

  /* >>> */
  def bopURShift(left: Value, right: Value): Value = utils.ValueBot

  /* + */
  def bopPlus(left: Value, right: Value): Value = utils.ValueBot

  /* - */
  def bopMinus(left: Value, right: Value): Value = utils.ValueBot

  /* * */
  def bopMul(left: Value, right: Value): Value = utils.ValueBot

  /* / */
  def bopDiv(left: Value, right: Value): Value = utils.ValueBot

  /* % */
  def bopMod(left: Value, right: Value): Value = utils.ValueBot

  /* == */
  def bopEqBetter(h: Heap, left: Value, right: Value): Value = utils.ValueBot

  def bopEq(left: Value, right: Value): Value = utils.ValueBot

  /* != */
  def bopNeq(left: Value, right: Value): Value = utils.ValueBot

  /* === */
  def bopSEq(left: Value, right: Value): Value = utils.ValueBot

  /* !== */
  def bopSNeq(left: Value, right: Value): Value = utils.ValueBot

  /* < */
  def bopLess(left: Value, right: Value): Value = utils.ValueBot

  /* > */
  def bopGreater(left: Value, right: Value): Value = utils.ValueBot

  /* <= */
  def bopLessEq(left: Value, right: Value): Value = utils.ValueBot

  /* >= */
  def bopGreaterEq(left: Value, right: Value): Value = utils.ValueBot
}
