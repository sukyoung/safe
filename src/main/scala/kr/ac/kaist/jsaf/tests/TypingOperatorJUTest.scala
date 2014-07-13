/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
package kr.ac.kaist.jsaf.tests

import junit.framework.Test
import junit.framework.TestSuite
import junit.framework.TestCase
import junit.framework.Assert._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.{AddressManager, Operator}
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.ShellParameters
import kr.ac.kaist.jsaf.compiler.Predefined

// class definition for eclipse JUnit runner 
class TypingOperatorJUTest

object TypingOperatorJUTest {
  Shell.pred = new Predefined(new ShellParameters())

  val joinCases:List[(String, List[Any], List[Any], List[Any], Boolean)] = List(
      ("{} + {} = {}", List(), List(), List(), true),
      ("{1} + {2} = {1, 2}", List(1), List(2), List(1, 2), true),
      ("{UndefTop} + {true} = {UndefTop, true}", List(UndefTop), List(true), List(UndefTop, true), true),
      ("{NullTop} + {} = {NullTop}", List(NullTop), List(), List(NullTop), true),
      ("{true} + {false} = {true, false}", List(true), List(false), List(true, false), true),
      ("{false} + {NullTop} = {false, NullTop}", List(false), List(NullTop), List(false, NullTop), true),
      ("{-1} + {true} = {-1, true}", List(-1), List(true), List(-1, true), true),
      ("{-3} + {0.2} = {-3, 0.2}", List(-3), List(0.2), List(-3, 0.2), true),
      ("{1, 2} + {false} = {1, 2, false}", List(1, 2), List(false), List(1, 2, false), true),
      ("{1, \"s\"} + {false} = {1, \"s\", false}", List(1, "s"), List(false), List(1, "s", false), true),
      ("{\"1\", \"0\"} + {1} = {\"1\", \"0\", 1}", List("1", "0"), List(1), List("1", "0", 1), true),
      ("{NaN} + {true} = {NaN, true}", List(NaN), List(true), List(NaN, true), true),
      ("{PosInf} + {NullTop} = {PosInf, NullTop}", List(PosInf), List(NullTop), List(PosInf, NullTop), true),
      ("{UndefTop} + {NegInf} = {UndefTop, NegInf}", List(UndefTop), List(NegInf), List(UndefTop, NegInf), true),
      ("{\"foo\"} + {\"1\"} = {\"foo\", \"1\"}", List("foo"), List("1"), List("foo", "1"), true),
      ("{UndefTop} + {\"str\"} = {UndefTop, \"str\"}", List(UndefTop), List("str"), List(UndefTop, "str"), true),
      ("{NaN} + {-1} = {NaN, -1}", List(NaN), List(-1), List(NaN, -1), true),
      ("{PosInf} + {NegInf} = {PosInf, NegInf}", List(PosInf), List(NegInf), List(PosInf, NegInf), true),
      ("{NaN} + {NegInf} = {NaN, NegInf}", List(NaN), List(NegInf), List(NaN, NegInf), true),
      ("{NaN} + {PosInf} = {NaN, PosInf}", List(NaN), List(PosInf), List(NaN, PosInf), true),
      ("{1, 2} + {-3, 4.3} = {1, 2, -3, 4.3}", List(1, 2), List(-3, 4.3), List(1, 2, -3, 4.3), true),
      ("{UndefTop, NullTop, true} + {false} = {UndefTop, NullTop, true, false}", List(UndefTop, NullTop, true), List(false), List(UndefTop, NullTop, true, false), true),
      ("{UndefTop, NullTop, true, false} + {NaN, 1, \"1\", \"str\"} = {UndefTop, NullTop, true, false, NaN, 1, \"1\", \"str\"}", List(UndefTop, NullTop, true, false), List(NaN, 1, "1", "str"), List(UndefTop, NullTop, true, false, NaN, 1, "1", "str"), true),
      ("{\"-1\"} + {\"3.5\"} = {\"-1\", \"3.5\"}", List("-1"), List("3.5"), List("-1", "3.5"), true),
      ("{\"1\"} + {} = {\"1\"}", List("1"), List(), List("1"), true),
      ("{\"-1\"} + {} = {\"-1\"}", List("-1"), List(), List("-1"), true),
      ("{UndefTop} + {} = {UndefTop}", List(UndefTop), List(), List(UndefTop), true),
      ("{NullTop} + {} = {NullTop}", List(NullTop), List(), List(NullTop), true),
      ("{true} + {} = {true}", List(true), List(), List(true), true)
      // TODO
      )
  val binCases:List[TypeOperator] = List(
      BinBitOr("{NaN} | {1, 2} = {1, 2}", List(NaN), List(1,2), List(1, 2), true),
      BinBitOr("{-1, 3.2} | {NaN} = {-1, 3.2}", List(-1, 3.2), List(NaN), List(-1, 3), true),
      BinBitOr("{1} | {2} = {3}", List(1), List(2), List(3), true),
      BinBitOr("{1} | {-1} = {-1}", List(1), List(-1), List(-1), true),
      BinBitOr("{1} | {1, 2} = {1, 3}", List(1), List(1, 2), List(1, 3), true),
      BinBitOr("{1} | {-1, 2.1} = {-1, 3}", List(1), List(-1, 2.1), List(-1, 3), true),
      BinBitOr("{-1} | {-2} = {-1}", List(-1), List(-2), List(-1), true),
      BinBitOr("{-1} | {1} = {-1}", List(-1), List(1), List(-1), true),
      BinBitOr("{-1} | {1, 2} >= {-1, -1}", List(-1), List(1, 2), List(-1, -1), false),
      BinBitOr("{-2} | {1, 2} = {-1, -2}", List(-2), List(1, 2), List(-1, -2), true),
      BinBitOr("{2.1} | {1, 2} = {3, 2}", List(2.1), List(1, 2), List(3, 2), true),
      BinBitOr("{-1} | {-1, 3.1} >= {-1, -1}", List(-1), List(-1, 3.1), List(-1, -1), false),
      BinBitOr("{7.1} | {-1, 3.1} = {-1, 7}", List(7.1), List(-1, 3.1), List(-1, 7), true),
      BinBitOr("{1, 2} | {5} = {5, 7}", List(1, 2), List(5), List(5, 7), true),
      BinBitOr("{1, 2} | {-3} = {-3, -1}", List(1, 2), List(-3), List(-3, -1), true),
      BinBitOr("{1, 2} | {5.1} = {5, 7}", List(1, 2), List(5.1), List(5, 7), true),
      BinBitOr("{1, 2} | {5, 6} = {5, 7, 7, 6}", List(1, 2), List(5, 6), List(5, 7, 7, 6), true),
      BinBitOr("{1, 2} | {-4, 7.5} = {-3, 7, -2, 7}", List(1, 2), List(-4, 7.5), List(-3, 7, -2, 7), true),
      BinBitOr("{-3, -2} | {2} >= {-1, -2}", List(-3, -2), List(2), List(-1, -2), false),
      BinBitOr("{-3, 2.5} | {2} = {-1, 2}", List(-3, 2.5), List(2), List(-1, 2), true),
      BinBitOr("{-3, -2} | {-1, 1} >= {-1, -3, -1, -1}", List(-3, -2), List(-1, 1), List(-1, -3, -1, -1), false),
      BinBitOr("{-3, 2.5} | {-1, 1} = {-1, -3, -1, 3}", List(-3, 2.5), List(-1, 1), List(-1, -3, -1, 3), true),
      BinBitOr("{PosInf} | {NegInf} = {0}", List(PosInf), List(NegInf), List(0), true),
      BinBitOr("{PosInf} | {1} = {1}", List(PosInf), List(1), List(1), true),
      BinBitOr("{PosInf} | {3.5} = {3}", List(PosInf), List(3.5), List(3), true),
      BinBitOr("{1} | {NegInf} = {1}", List(1), List(NegInf), List(1), true),
      BinBitOr("{-1} | {NegInf} = {-1}", List(-1), List(NegInf), List(-1), true),
      BinBitOr("{PosInf, NegInf} | {1} = {1}", List(PosInf, NegInf), List(1), List(1), true),

      BinBitAnd("{1} & {2} = {0}", List(1), List(2), List(0), true),
      BinBitAnd("{1} & {-1} = {1}", List(1), List(-1), List(1), true),
      BinBitAnd("{1} & {2, 1} = {0, 1}", List(1), List(2, 1), List(0, 1), true),
      BinBitAnd("{1} & {-2, 3} = {0, 1}", List(1), List(-2, 3), List(0, 1), true),
      BinBitAnd("{1} & {-2, 3.4} = {0, 1}", List(1), List(-2, 3.4), List(0, 1), true),
      BinBitAnd("{3} & {-4, 3.2} = {0, 3}", List(3), List(-4, 3.2), List(0, 3), true),
      BinBitAnd("{-2} & {3} = {2}", List(-2), List(3), List(2), true),
      BinBitAnd("{-2} & {-3} = {-4}", List(-2), List(-3), List(-4), true),
      BinBitAnd("{-2} & {3, 5} = {2, 4}", List(-2), List(3, 5), List(2, 4), true),
      BinBitAnd("{-2} & {-3, -6} >= {-4, -6}", List(-2), List(-3, -6), List(-4, -6), false),
      BinBitAnd("{-2} & {-3, 6.5} = {-4, 6}", List(-2), List(-3, 6.5), List(-4, 6), true),
      BinBitAnd("{-2} & {-3, 3} = {-4, 2}", List(-2), List(-3, 5), List(-4, 2), true),
      BinBitAnd("{6, 7} & {1} = {0, 1}", List(6, 7), List(1), List(0, 1), true),
      BinBitAnd("{6, 7} & {-5} = {2, 3}", List(6, 7), List(-5), List(2, 3), true),
      BinBitAnd("{3.4, -6} & {1} >= {1, 0}", List(3.4, -6), List(1), List(1, 0), false),
      BinBitAnd("{3.4, -6} & {-1} = {3, -6}", List(3.4, -6), List(-1), List(3, -6), true),
      BinBitAnd("{3.4, -10} & {5.5} >= {1, 4}", List(3.4, -10), List(5.5), List(1, 4), false),
      BinBitAnd("{3.4, -6} & {1, 2} >= {1, 2, 0, 2}", List(3.4, -6), List(1, 2), List(1, 2, 0, 2), false),
      BinBitAnd("{3.4, -6} & {3.4, -6} = {3, 2, 2, -6}", List(3.4, -6), List(3.4, -6), List(3, 2, 2, -6), true),
      BinBitAnd("{PosInf} & {NegInf} = {0}", List(PosInf), List(NegInf), List(0), true),
      BinBitAnd("{PosInf} & {1} = {0}", List(PosInf), List(1), List(0), true),
      BinBitAnd("{PosInf} & {1, 2} = {0}", List(PosInf), List(1, 2), List(0), true),
      BinBitAnd("{1} & {NegInf} = {0}", List(1), List(NegInf), List(0), true),
      BinBitAnd("{2.5, -3} & {NegInf} = {0}", List(2.5, -3), List(NegInf), List(0), true),
      BinBitAnd("{1} & {PosInf, NegInf} = {0}", List(1), List(PosInf, NegInf), List(0), true),
      BinBitAnd("{NaN} & {-3} = {0}", List(NaN), List(-3), List(0), true),

      BinBitXor("{3} ^ {2} = {1}", List(3), List(2), List(1), true),
      BinBitXor("{3} ^ {1, 2} = {2, 1}", List(3), List(1, 2), List(2, 1), true),
      BinBitXor("{3} ^ {-1} = {-4}", List(3), List(-1), List(-4), true),
      BinBitXor("{-3} ^ {-3, 3.6} = {0, -2}", List(-3), List(-3, 3.6), List(0, -2), true),
      BinBitXor("{-3} ^ {-1} = {2}", List(-3), List(-1), List(2), true),
      BinBitXor("{-3} ^ {1, 2} = {-4, -1}", List(-3), List(1, 2), List(-4, -1), true),
      BinBitXor("{-3} ^ {-1, 0.4} = {2, -3}", List(-3), List(-1, 0.4), List(2, -3), true),
      BinBitXor("{3, 7} ^ {2} = {1, 5}", List(3, 7), List(2), List(1, 5), true),
      BinBitXor("{3, 7} ^ {2, 6} = {1, 5, 5, 1}", List(3, 7), List(2, 6), List(1, 5, 5, 1), true),
      BinBitXor("{3, 7} ^ {-4} = {-1, -5}", List(3, 7), List(-4), List(-1, -5), true),
      BinBitXor("{3, 7} ^ {-6, 1.3} = {-7, 2, -3, 6}", List(3, 7), List(-6, 1.3), List(-7, 2, -3, 6), true),
      BinBitXor("{0.2, -4} ^ {3} = {3, -1}", List(0.2, -4), List(3), List(3, -1), true),
      BinBitXor("{0.2, -4} ^ {3, 6} = {3, -1, 6, -6}", List(0.2, -4), List(3, 6), List(3, -1, 6, -6), true),
      BinBitXor("{0.2, -4} ^ {0.5} = {0, -4}", List(0.2, -4), List(0.5), List(0, -4), true),
      BinBitXor("{0.2, -4} ^ {0.5, 3.6} = {0, -4, 3, -1}", List(0.2, -4), List(0.5, 3.6), List(0, -4, 3, -1), true),
      BinBitXor("{PosInf} ^ {3} = {3}", List(PosInf), List(3), List(3), true),
      BinBitXor("{PosInf} ^ {NegInf} = {0}", List(PosInf), List(NegInf), List(0), true),
      BinBitXor("{PosInf} ^ {-3} = {-3}", List(PosInf), List(-3), List(-3), true),
      BinBitXor("{NegInf} ^ {3.5} = {3}", List(NegInf), List(3.5), List(3), true),
      BinBitXor("{2, 4} ^ {NegInf} = {2, 4}", List(2, 4), List(NegInf), List(2, 4), true),
      BinBitXor("{PosInf, NegInf} ^ {3, 2} = {3, 2}", List(PosInf, NegInf), List(3, 2), List(3, 2), true),

      BinLShift("{3} << {1} = {6}", List(3), List(1), List(6), true),
      BinLShift("{3} << {1.2} = {6}", List(3), List(1.2), List(6), true),
      BinLShift("{3} << {-1.2} = {6}", List(3), List(-1.2), List(-2147483648), true),
      BinLShift("{-3} << {1} = {-6}", List(-3), List(1), List(-6), true),
      BinLShift("{-1} << {-3} = {-536870912}", List(-1), List(-3), List(-536870912), true),
      BinLShift("{-1} << {-3, 2} >= {-536870912, -4}", List(-1), List(-3, 2), List(-536870912, -4), false),
      BinLShift("{4} << {29} = {-2147483648}", List(4), List(29), List(-2147483648), true),
      BinLShift("{-4} << {30} = {0}", List(-4), List(30), List(0), true),
      BinLShift("{4} << {29, 28} = {-2147483648, 1073741824}", List(4), List(29, 28), List(-2147483648, 1073741824), true),
      BinLShift("{4, 2} << {-2} = {0, -2147483648}", List(4, 2), List(-2), List(0, -2147483648), true),

      BinRShift("{256} >> {3} = {32}", List(256), List(3), List(32), true),
      BinRShift("{256} >> {-32} = {256}", List(256), List(-32), List(256), true),
      BinRShift("{256} >> {-31} = {128}", List(256), List(-31), List(128), true),
      BinRShift("{256} >> {-33} = {0}", List(256), List(-33), List(0), true),
      BinRShift("{-256} >> {-3} = {-1}", List(-256), List(-3), List(-1), true),
      BinRShift("{-256} >> {3} = {-32}", List(-256), List(3), List(-32), true),
      BinRShift("{-256} >> {-0.5} = {-256}", List(-256), List(-0.5), List(-256), true),
      BinRShift("{-256} >> {17, 31} >= {-1, -1}", List(-256), List(17, 31), List(-1, -1), false),
      BinRShift("{343.4} >> {2} = {85}", List(343.4), List(2), List(85), true),
      BinRShift("{256, 34} >> {2} = {64, 8}", List(256, 34), List(2), List(64, 8), true),
      BinRShift("{34, -34} >> {2} = {64, -9}", List(34, -34), List(2), List(64, -9), true),

      BinURShift("{32} >>> {2} = {8}", List(32), List(2), List(8), true),
      BinURShift("{-32} >>> {2} = {1073741816}", List(-32), List(2), List(1073741816), true),
      BinURShift("{-32} >>> {-1} = {1}", List(-32), List(-1), List(1), true),
      BinURShift("{-32} >>> {30} = {3}", List(-32), List(30), List(3), true),
      BinURShift("{-32} >>> {-1, 30} >= {1, 3}", List(-32), List(-1, 30), List(1, 3), false),
      BinURShift("{564} >>> {30} = {0}", List(564), List(30), List(0), true),
      BinURShift("{564} >>> {-30} = {141}", List(564), List(-30), List(141), true),
      BinURShift("{-12345} >>> {31} = {1}", List(-12345), List(31), List(1), true),
      BinURShift("{564, -32} >>> {30} = {0, 3}", List(564, -32), List(30), List(0, 3), true),
      BinURShift("{34, 78} >>> {2} = {8, 19}", List(34, 78), List(2), List(8, 19), true),
      BinURShift("{-65, -90} >>> {-2} >= {3, 3}", List(-65, -90), List(-2), List(3, 3), false),
      BinURShift("{-65, -90} >>> {4} = {268435451, 268435450}", List(-65, -90), List(4), List(268435451, 268435450), true),

      BinPlus("{NaN} + {2} = {NaN}", List(NaN), List(2), List(NaN), true),
      BinPlus("{PosInf, NegInf} + {NaN} = {NaN}", List(PosInf, NegInf), List(NaN), List(NaN), true),
      BinPlus("{PosInf} + {NegInf} = {NaN}", List(PosInf), List(NegInf), List(NaN), true),
      BinPlus("{NegInf} + {PosInf} = {NaN}", List(NegInf), List(PosInf), List(NaN), true),
      BinPlus("{NegInf} + {NegInf} = {NegInf}", List(NegInf), List(NegInf), List(NegInf), true),
      BinPlus("{PosInf} + {PosInf} = {PosInf}", List(PosInf), List(PosInf), List(PosInf), true),
      BinPlus("{PosInf, NegInf} + {PosInf, NegInf} = {NaN, PosInf, NegInf}", List(PosInf, NegInf), List(PosInf, NegInf), List(NaN, PosInf, NegInf), true),
      BinPlus("{2} + {PosInf, NegInf} = {PosInf, NegInf}", List(2), List(PosInf, NegInf), List(PosInf, NegInf), true),
      BinPlus("{PosInf, NegInf} + {-3.1} = {PosInf, NegInf}", List(PosInf, NegInf), List(-3.1), List(PosInf, NegInf), true),
      BinPlus("{PosInf} + {2} = {PosInf}", List(PosInf), List(2), List(PosInf), true),
      BinPlus("{-4.3} + {PosInf} = {PosInf}", List(-4.3), List(PosInf), List(PosInf), true),
      BinPlus("{NegInf} + {100} = {NegInf}", List(NegInf), List(100), List(NegInf), true),
      BinPlus("{3} + {NegInf} = {NegInf}", List(3), List(NegInf), List(NegInf), true),
      BinPlus("{1} + {2} = {3}", List(1), List(2), List(3), true),
      BinPlus("{1} + {-4} = {-3}", List(1), List(-4), List(-3), true),
      BinPlus("{1} + {3, 4} = {4, 5}", List(1), List(3, 4), List(4, 5), true),
      BinPlus("{1, 2} + {4} = {5, 6}", List(1, 2), List(4), List(5, 6), true),
      BinPlus("{-1} + {-4} = {-5}", List(-1), List(-4), List(-5), true),
      BinPlus("{-1} + {3.4, -2} >= {2.4, -3}", List(-1), List(3.4, -2), List(2.4, -3), false),
      BinPlus("{-1} + {0, 2} = {-1, 1}", List(-1), List(0, 2), List(-1, 1), true),
      BinPlus("{-1} + {3} = {2}", List(-1), List(3), List(2), true),
      BinPlus("{3.5} + {0.5} = {4}", List(3.5), List(0.5), List(4), true),
      BinPlus("{3.5} + {0.5, 1.5} >= {4, 5}", List(3.5), List(0.5, 1.5), List(4, 5), false),
      BinPlus("{3.5, 6.5} + {0.5} >= {3, 6}", List(3.5, 6.5), List(0.5), List(4, 7), false),
      BinPlus("{2, 4} + {-5} >= {-3, -1}", List(2, 4), List(-5), List(-3, -1), false),
      BinPlus("{1.2, 4} + {-1} = {0.2, 3}", List(1.2, 4), List(-1), List(0.2, 3), true),

      BinPlus("{} + {} = {}", List(), List(), List(), true),
      BinPlus("{\"s\"} + {PosInf} = {\"sInfinity\"}", List("s"), List(PosInf), List("sInfinity"), true),
      BinPlus("{\"s\"} + {NegInf} = {\"s-Infinity\"}", List("s"), List(NegInf), List("s-Infinity"), true),
      BinPlus("{NaN} + {\"s\"} = {\"NaNs\"}", List(NaN), List("s"), List("NaNs"), true),
      BinPlus("{\"A\"} + {\"B\"} = {\"AB\"}", List("A"), List("B"), List("AB"), true),
      BinPlus("{\"1\"} + {3} = {\"13\"}", List("1"), List(3), List("13"), true),
      BinPlus("{\"0\"} + {1} = {\"01\"}", List("0"), List(1), List("01"), true),
      BinPlus("{\"2\"} + {\"\"} = {\"2\"}", List("2"), List(""), List("2"), true),
      BinPlus("{\"2\"} + {-3} = {\"2-3\"}", List("2"), List(-3), List("2-3"), true),
      BinPlus("{\"2\"} + {1.2} = {\"21.2\"}", List("2"), List(1.2), List("21.2"), true),
      BinPlus("{\"-1\"} + {\"\"} = {\"-1\"}", List("-1"), List(""), List("-1"), true),
      BinPlus("{\"-1\"} + {1} = {\"-11\"}", List("-1"), List(1), List("-11"), true),
      BinPlus("{\"-1\"} + {\"\", 2} = {\"-1\", \"-12\"}", List("-1"), List("", 2), List("-1", "-12"), true),
      BinPlus("{\"3.2\", \"\"} + {0} = {\"3.20\", \"0\"}", List("3.2", ""), List(0), List("3.20", "0"), false),
      BinPlus("{\"3.2\", \"\"} + {-1} = {\"3.2-1\", \"-1\"}", List("3.2", ""), List(-1), List("3.2-1", "-1"), true),
      BinPlus("{\"a\", 1} + {\"b\", 2} >= {\"ab\", \"a2\", \"1b\", 3}", List("a", 1), List("b", 2), List("ab", "1b", "a2", 3), false),
      BinPlus("{true, 1} + {\"a\", 2} = {\"1a\", \"truea\", 3, 3}", List(true, 1), List("a", 2), List("1a", "truea", 3, 3), true),
      BinPlus("{false, 1} + {\"a\", 2} = {\"1a\", \"falsea\", 2, 3}", List(false, 1), List("a", 2), List("1a", "falsea", 2, 3), true),
      BinPlus("{null, \"1\"} + {\"1\", \"\"} = {\"null1\", \"11\", \"null\", \"1\"}", List(NullTop, "1"), List("1", ""), List("null1", "11", "null", "1"), true),
      BinPlus("{null, \"1\"} + {\"1\", \"2\"} = {\"null1\", \"11\", \"null2\", \"12\"}", List(NullTop, "1"), List("1", "2"), List("null1", "11", "null2", "12"), true),
      BinPlus("{Undef, 1} + {\"str\", true} = {\"undefinedstr\", NaN, \"1str\", 2}", List(UndefTop, 1), List("str", true), List("undefinedstr", NaN, "1str", 2), true),
      BinPlus("{\"1\", -2} + {1, \"4\"} = {\"11\", \"14\", -1, \"-24\"}", List("1", -2), List(1, "4"), List("11", "14", -1, "-24"), true),
      BinPlus("{\"\"} + {1, 2} = {\"1\", \"2\"}", List(""), List(1, 2), List("1", "2"), true),
      BinPlus("{\"\"} + {-1, 3.2} = {\"-1\", \"3.2\"}", List(""), List(-1, 3.2), List("-1", "3.2"), true),
      BinPlus("{\"\"} + {1, -1} = {\"1\", \"-1\"}", List(""), List(1, -1), List("1", "-1"), true),

      BinMinus("{} - {} = {}", List(), List(), List(), true),
      BinMinus("{} - {1} = {}", List(), List(1), List(), true),
      BinMinus("{-1} - {} = {}", List(-1), List(), List(), true),
      BinMinus("{NaN} - {3} = {NaN}", List(NaN), List(3), List(NaN), true),
      BinMinus("{-2} - {NaN} = {NaN}", List(-2), List(NaN), List(NaN), true),
      BinMinus("{PosInf} - {NegInf} = {PosInf}", List(PosInf), List(NegInf), List(PosInf), true),
      BinMinus("{NegInf} - {PosInf} = {NegInf}", List(NegInf), List(PosInf), List(NegInf), true),
      BinMinus("{PosInf} - {PosInf} = {NaN}", List(PosInf), List(PosInf), List(NaN), true),
      BinMinus("{NegInf} - {NegInf} = {NaN}", List(NegInf), List(NegInf), List(NaN), true),
      BinMinus("{PosInf} - {3} = {PosInf}", List(PosInf), List(3), List(PosInf), true),
      BinMinus("{NegInf} - {-2} = {NegInf}", List(NegInf), List(-2), List(NegInf), true),
      BinMinus("{2} - {PosInf} = {NegInf}", List(2), List(PosInf), List(NegInf), true),
      BinMinus("{1} - {NegInf} = {PosInf}", List(1), List(NegInf), List(PosInf), true),
      BinMinus("{3} - {2} = {1}", List(3), List(2), List(1), true),
      BinMinus("{3} - {5} = {-2}", List(3), List(5), List(-2), true),
      BinMinus("{3} - {-5} = {8}", List(3), List(-5), List(8), true),
      BinMinus("{5} - {3.5} = {1.5}", List(5), List(3.5), List(1.5), true),
      BinMinus("{3} - {-5, 3.5} = {8, 0.5}", List(3), List(-5, 3.5), List(8, 0.5), true),
      BinMinus("{-2} - {1} = {-3}", List(-2), List(1), List(-3), true),
      BinMinus("{-2} - {1, 3} >= {-3, -5}", List(-2), List(1, 3), List(-3, -5), false),
      BinMinus("{5.2} - {2} = {3.2}", List(5.2), List(2), List(3.2), true),
      BinMinus("{-2} - {-5} = {3}", List(-2), List(-5), List(3), true),
      BinMinus("{-2} - {-5, -1} = {3, -1}", List(-2), List(-5, -1), List(3, -1), true),
      BinMinus("{-2} - {-2, 1} = {0, -3}", List(-2), List(-2, -1), List(0, -3), true),
      BinMinus("{2, 3} - {3} = {-1, 0}", List(2, 3), List(3), List(-1, 0), true),
      BinMinus("{2, 3} - {-2} = {4, 5}", List(2, 3), List(-2), List(4, 5), true),
      BinMinus("{2, 3} - {-2.5} = {4.5, 5.5}", List(2, 3), List(-2.5), List(4.5, 5.5), true),
      BinMinus("{2, 3} - {-1, -3} >= {3, 4, 4, 6}", List(2, 3), List(-1, -3), List(3, 4, 4, 6), false),
      BinMinus("{2, 3} - {-2.5, -1} = {4.5, 5.5, 3, 4}", List(2, 3), List(-2.5, -1), List(4.5, 5.5, 3, 4), true),
      BinMinus("{2, 3} - {2.5} = {0.5, -0.5}", List(2, 3), List(2.5), List(0.5, -0.5), true),
      BinMinus("{2, 3} - {2, 5} = {0, 1, -3, -2}", List(2, 3), List(2, 5), List(0, 1, -3, -2), true),
      BinMinus("{-1, 4.2} - {3} = {-4, 1.2}", List(-1, 4.2), List(3), List(-4, 1.2), true),
      BinMinus("{3.5, 1.5} - {0.5} >= {3, 1}", List(3.5, 1.5), List(0.5), List(3, 1), false),
      BinMinus("{2.1, -4} - {-1} >= {3.1, -3}", List(2.1, -4), List(-1), List(3.1, -3), false),
      BinMinus("{-2, 2.5} - {3, 2} = {-5, -0.5, -4, 0.5}", List(-2, 2.5), List(3, 2), List(-5, -0.5, -4, 0.5), true),
      BinMinus("{-1, -5} - {-2, -1} = {1, 0, -3, -4}", List(-1, -5), List(-2, -1), List(1, 0, -3, -4), true),

      BinMul("{NaN} * {3} = {NaN}", List(NaN), List(3), List(NaN), true),
      BinMul("{2} * {NaN} = {NaN}", List(2), List(NaN), List(NaN), true),
      BinMul("{0} * {PosInf} = {NaN}", List(0), List(PosInf), List(NaN), true),
      BinMul("{0} * {NegIng} = {NaN}", List(0), List(NegInf), List(NaN), true),
      BinMul("{0} * {PosInf, NegInf} = {NaN}", List(0), List(PosInf, NegInf), List(NaN), true),
      BinMul("{PosInf} * {PosInf} = {PosInf}", List(PosInf), List(PosInf), List(PosInf), true),
      BinMul("{PosIng} * {NegInf} = {NegInf}", List(PosInf), List(NegInf), List(NegInf), true),
      BinMul("{NegInf} * {NegInf} = {PosInf}", List(NegInf), List(NegInf), List(PosInf), true),
      BinMul("{NegInf} * {PosInf} = {NegInf}", List(NegInf), List(PosInf), List(NegInf), true),
      BinMul("{PosInf, NegInf} * {PosInf} = {PosInf, NegInf}", List(PosInf, NegInf), List(PosInf), List(PosInf, NegInf), true),
      BinMul("{0, 2} * {PosInf} = {NaN, PosInf}", List(0, 2), List(PosInf), List(NaN, PosInf), true),
      BinMul("{PosInf} * {0, 2} = {NaN, PosInf}", List(PosInf), List(0, 2), List(NaN, PosInf), true),
      BinMul("{NegInf} * {2, 3} >= {NegInf}", List(NegInf), List(2, 3), List(NegInf), false),
      BinMul("{1E9} * {5} = {5E9}", List(1E9), List(5), List(5E9), true),
      BinMul("{PosInf} / {-1, 3.5} = {PosInf, NegInf}", List(PosInf), List(-1, 3.5), List(PosInf, NegInf), true),
      BinMul("{-1, 3.5} / {PosInf} = {PosInf, NegInf}", List(-1, 3.5), List(PosInf), List(PosInf, NegInf), true),
      BinMul("{NegInf} / {-1, 3.5} = {PosInf, NegInf}", List(NegInf), List(-1, 3.5), List(PosInf, NegInf), true),
      BinMul("{-1, 3.5} / {NegInf} = {PosInf, NegInf}", List(-1, 3.5), List(NegInf), List(PosInf, NegInf), true),

      BinMul("{0} * {-2, 3.4, 1} = {0}", List(0), List(-2, 3.4, 1), List(0), true),
      BinMul("{-1, 4} * {0} = {0}", List(-1, 4), List(0), List(0), true),
      BinMul("{2} * {3} = {6}", List(2), List(3), List(6), true),
      BinMul("{2} * {1, 3} = {2, 6}", List(2), List(1, 3), List(2, 6), true),
      BinMul("{2} * {-1} = {-2}", List(2), List(-1), List(-2), true),
      BinMul("{2} * {3.2, -4} = {6.4, -6}", List(2), List(3.2, -4), List(6.4, -6), true),
      BinMul("{0, 2} * {2} = {0, 4}", List(0, 2), List(2), List(0, 4), true),
      BinMul("{0, 2} * {2, 5} = {0, 4, 10}", List(0, 2), List(2, 5), List(0, 4, 10), true),
      BinMul("{0, 2} * {-1} = {0, -2}", List(0, 2), List(-1), List(0, -2), true),
      BinMul("{0, 2} * {-1, 2.5} = {0, -2, 5}", List(0, 2), List(-1, 2.5), List(0, -2, 5), true),
      BinMul("{2.5} * {2} = {5}", List(2.5), List(2), List(5), true),
      BinMul("{2.5} * {2, 3} = {5, 7.5}", List(2.5), List(2, 3), List(5, 7.5), true),
      BinMul("{-2} * {-4} = {8}", List(-2), List(-4), List(8), true),
      BinMul("{-2} * {-4, 2.5} = {8, -5}", List(-2), List(-4, 2.5), List(8, -5), true),
      BinMul("{2.5, 1.5} * {2} >= {5, 3}", List(2.5, 1.5), List(2), List(5, 3), false),
      BinMul("{2.5 -2} * {1, 2} = {2.5, -2, 5, -4}", List(2.5, -2), List(1, 2), List(2.5, -2, 5, -4), true),
      BinMul("{-2, -4} * {-3} >= {6, 12}", List(-2, -4), List(-3), List(6, 12), false),
      BinMul("{-2, 2.5} * {-2, -1} = {2, 2.5, -4, 5}", List(-2, 2.5), List(-2, -1), List(2, 2.5, -4, 5), true),

      BinDiv("{} / {} = {}", List(), List(), List(), true),
      BinDiv("{NaN} / {3} = {NaN}", List(NaN), List(3), List(NaN), true),
      BinDiv("{3} / {NaN} = {NaN}", List(3), List(NaN), List(NaN), true),
      BinDiv("{PosInf} / {NegInf} = {NaN}", List(PosInf), List(NegInf), List(NaN), true),
      BinDiv("{NegInf} / {NegInf} = {NaN}", List(NegInf), List(NegInf), List(NaN), true),
      BinDiv("{PosInf} / {NegInf, PosInf} = {NaN}", List(PosInf), List(NegInf, PosInf), List(NaN), true),
      BinDiv("{PosInf} / {0} = {PosInf}", List(PosInf), List(0), List(PosInf), true),
      BinDiv("{NegInf} / {0} = {NegInf}", List(NegInf), List(0), List(NegInf), true),
      BinDiv("{PosInf, NegInf} / {0} = {PosInf, NegInf}", List(PosInf, NegInf), List(0), List(PosInf, NegInf), true),
      BinDiv("{PosInf} / {2, 3} = {PosInf}", List(PosInf), List(2, 3), List(PosInf), true),
      BinDiv("{PosInf} / {-1} = {NegInf}", List(PosInf), List(-1), List(NegInf), true),
      BinDiv("{PosInf} / {0.5} = {PosInf}", List(PosInf), List(0.5), List(PosInf), true),
      BinDiv("{PosInf} / {-1, 0.5} = {NegInf, PosInf}", List(PosInf), List(-1, 0.5), List(NegInf, PosInf), true),
      BinDiv("{NegInf} / {2, 3} = {NegInf}", List(NegInf), List(2, 3), List(NegInf), true),
      BinDiv("{NegInf} / {-1} = {PosInf}", List(NegInf), List(-1), List(PosInf), true),
      BinDiv("{NegInf} / {0.5} = {NegInf}", List(NegInf), List(0.5), List(NegInf), true),
      BinDiv("{NegInf} / {-1, 0.5} = {PosInf, NegInf}", List(NegInf), List(-1, 0.5), List(PosInf, NegInf), true),
      BinDiv("{PosInf, NegInf} / {-3} = {PosInf, NegInf}", List(PosInf, NegInf), List(-3), List(PosInf, NegInf), true),
      BinDiv("{PosInf, NegInf} / {-3, 0.5} = {PosInf, NegInf}", List(PosInf, NegInf), List(-3, 0.5), List(PosInf, NegInf), true),      
      BinDiv("{3} / {PosInf} = {0}", List(3), List(PosInf), List(0), true),
      BinDiv("{-2, 0.5} / {NegInf} = {0}", List(-2, 0.5), List(NegInf), List(0), true),
      BinDiv("{1, 2} / {PosInf, NegInf} = {0}", List(1, 2), List(PosInf, NegInf), List(0), true),

      BinDiv("{0} / {0} = {NaN}", List(0), List(0), List(NaN), true),
      BinDiv("{0} / {-3} = {0}", List(0), List(-3), List(0), true),
      BinDiv("{0} / {PosInf} = {0}", List(0), List(PosInf), List(0), true),
      BinDiv("{0} / {-3, 2} = {0}", List(0), List(-3, 2), List(0), true),
      BinDiv("{3} / {0} = {PosInf}", List(3), List(0), List(PosInf), true),
      BinDiv("{1, 2} / {0} >= {PosInf}", List(1, 2), List(0), List(PosInf), false),
      BinDiv("{0, 2} / {0} = {NaN, PosInf}", List(1, 2), List(0), List(NaN, PosInf), true),
      BinDiv("{0.5} / {0} = {PosInf}", List(0.5), List(0), List(PosInf), true),
      BinDiv("{-1} / {0} = {NegInf}", List(-1), List(0), List(NegInf), true),
      BinDiv("{-1, 0.5} / {0} = {PosInf, NegInf}", List(-1, 0.5), List(0), List(PosInf, NegInf), true),
      BinDiv("{3} / {2} = {1.5}", List(3), List(2), List(1.5), true),
      BinDiv("{4} / {2} = {2}", List(4), List(2), List(2), true),
      BinDiv("{3} / {2, 3} = {1.5, 1}", List(3), List(2, 3), List(1.5, 1), true),
      BinDiv("{3} / {0, 3} = {PosInf, 1}", List(3), List(0, 3), List(PosInf, 1), true),
      BinDiv("{3} / {1.5} = {2}", List(3), List(1.5), List(2), true),
      BinDiv("{3} / {-1} = {-3}", List(3), List(-1), List(-3), true),
      BinDiv("{3} / {1.5, -1} = {2, -3}", List(3), List(1.5, -1), List(2, -3), true),
      BinDiv("{-2} / {2} = {-1}", List(-2), List(2), List(-1), true),
      BinDiv("{-2} / {2, 0} = {-1, NegInf}", List(-2), List(2, 0), List(-1, NegInf), true),
      BinDiv("{-2} / {-1} = {2}", List(-2), List(-1), List(2), true),
      BinDiv("{-4} / {0.8} = {-5}", List(-4), List(0.8), List(-5), true),
      BinDiv("{-4} / {-1, 0.8} = {4, -5}", List(-4), List(-1, 0.8), List(4, -5), true),
      BinDiv("{-2, -4} / {2} = {-1, -2}", List(-2, -4), List(2), List(-1, -2), true),
      BinDiv("{-2, 0.8} / {2, 1} >= {-1, 0.4, 0.8, -2}", List(-2, 0.8), List(2, 1), List(-1, 0.4, 0.8, -2), false),
      BinDiv("{-1, 0.4} / {2, 0} = {-0.5, PosInf, NegInf, 0.2}", List(-1, 0.4), List(2, 0), List(-0.5, PosInf, NegInf, 0.2), true),
      BinDiv("{-2, 0.4} / {-1} = {2, -0.4}", List(-2, 0.4), List(-1), List(2, -0.4), true),
      BinDiv("{-2, 0.8} / {-2, 0.4} = {1, -0.4, 2, -5}", List(-2, 0.8), List(-2, 0.4), List(-2, 0.4, 2, -5), true),

      BinMod("{} % {} = {}", List(), List(), List(), true),
      BinMod("{NaN} % {3} = {NaN}", List(NaN), List(3), List(NaN), true),
      BinMod("{3} % {NaN} = {NaN}", List(3), List(NaN), List(NaN), true),
      BinMod("{PosInf} % {3} = {NaN}", List(PosInf), List(3), List(NaN), true),
      BinMod("{NegInf} % {2} = {NaN}", List(NegInf), List(2), List(NaN), true),
      BinMod("{PosInf, NegInf} % {2, -3} = {NaN}", List(PosInf, NegInf), List(2, -3), List(NaN), true),
      BinMod("{2} % {0} = {NaN}", List(2), List(0), List(NaN), true),
      BinMod("{0} % {PosInf} = {0}", List(0), List(PosInf), List(0), true),
      BinMod("{0} % {-1} = {0}", List(0), List(-1), List(0), true),
      BinMod("{0} % {2, 3} = {0}", List(0), List(2, 3), List(0), true),
      BinMod("{3} % {PosInf} = {3}", List(3), List(PosInf), List(3), true),
      BinMod("{-1, 0.5} % {NegInf} = {-1, 0.5}", List(-1, 0.5), List(NegInf), List(-1, 0.5), true),
      BinMod("{-2} % {PosInf, NegInf} = {-2}", List(-2), List(PosInf, NegInf), List(-2), true),
      BinMod("{3} % {2} = {1}", List(3), List(2), List(1), true),
      BinMod("{3} % {1, 0} = {1, NaN}", List(3), List(1, 0), List(1, NaN), true),
      BinMod("{3} % {-2} = {1}", List(3), List(-2), List(1), true),
      BinMod("{3} % {-2, 0.4} = {1, 0.2}", List(3), List(-2, 0.4), List(1, 0.2), true),
      BinMod("{1, 0} % {1} >= {0, 0}", List(1, 0), List(1), List(0, 0), false),
      BinMod("{-1} % {-2} = {-1}", List(-1), List(-2), List(-1), true),
      BinMod("{-2} % {-1} = {0}", List(-2), List(-1), List(0), true),
      BinMod("{-1} % {-1, -2} = {0, -1}", List(-1), List(-1, -2), List(0, -1), true),
      BinMod("{3.5} % {-0.5} = {0}", List(3.5), List(-0.5), List(0), true),
      BinMod("{-2, 2.5} % {3} = {-2, 2.5}", List(-2, 2.5), List(3), List(-2, 2.5), true),
      BinMod("{-2, 3.2} % {0.4} >= {0}", List(-2, 3.2), List(0.4), List(0), false),
      BinMod("{-2, -4} % {0.8} = {-0.4, 0}", List(-2, -4), List(0.8), List(-0.4, 0), true),

      BinEq("{} == {} = {}", List(), List(), List(), true),
      BinEq("{UndefTop} == {UndefTop} = {true}", List(UndefTop), List(UndefTop), List(true), true),
      BinEq("{NullTop} == {NullTop} = {true}", List(NullTop), List(NullTop), List(true), true),
      BinEq("{NaN} == {3, -1} = {false}", List(NaN), List(3, -1), List(false), true),
      BinEq("{PosInf} == {NaN} = {false}", List(PosInf), List(NaN), List(false), true),
      BinEq("{PosInf} == {PosInf} = {true}", List(PosInf), List(PosInf), List(true), true),
      BinEq("{NegInf} == {NegInf} = {true}", List(NegInf), List(NegInf), List(true), true),
      BinEq("{PosInf} == {NegInf} = {false}", List(PosInf), List(NegInf), List(false), true),
      BinEq("{NegInf} == {PosInf} = {false}", List(NegInf), List(PosInf), List(false), true),
      BinEq("{PosInf, NegInf} == {PosInf, NegInf} = {true, false}", List(PosInf, NegInf), List(PosInf, NegInf), List(true, false), true),
      BinEq("{PosInf, NegInf} == {NegInf} = {true, false}", List(PosInf, NegInf), List(NegInf), List(true, false), true),
      BinEq("{PosInf, NegInf} == {PosInf} = {true, false}", List(PosInf, NegInf), List(PosInf), List(true, false), true),
      BinEq("{PosInf} == {PosInf, NegInf} = {true, false}", List(PosInf), List(PosInf, NegInf), List(true, false), true),
      BinEq("{NegInf} == {PosInf, NegInf} = {true, false}", List(NegInf), List(PosInf, NegInf), List(true, false), true),
      BinEq("{1} == {1} = {true}", List(1), List(1), List(true), true),
      BinEq("{-2} == {-2} = {true}", List(-2), List(-2), List(true), true),
      BinEq("{1, 2} == {2} = {false, true}", List(1, 2), List(2), List(false, true), true),
      BinEq("{-1, -2} == {-1} = {true, false}", List(-1, -2), List(-1), List(true, false), true),
      BinEq("{2} == {1, 2} = {false, true}", List(2), List(1, 2), List(false, true), true),
      BinEq("{-1} == {-1, -2} = {true, false}", List(-1), List(-1, -2), List(true, false), true),
      BinEq("{-2} == {1, 2} = {false, false}", List(-2), List(1, 2), List(false), true),
      BinEq("{1, 2} == {2, 3} = {true, false}", List(1, 2), List(2, 3), List(true, false), true),
      BinEq("{-1, -2} == {-4, -2} = {true, false}", List(-1, -2), List(-4, -2), List(true, false), true),
      BinEq("{-1, 3} == {0.5} = {false, false}", List(-1, 3), List(0.5), List(false), false),
      BinEq("{\"1\"} == {\"1\"} = {true}", List("1"), List("1"), List(true), true),
      BinEq("{\"-1\"} == {\"-1\"} = {true}", List("-1"), List("-1"), List(true), true),
      BinEq("{\"1\"} == {\"2\"} = {false}", List("1"), List("2"), List(false), true),
      BinEq("{\"-1\"} == {\"-2\"} = {false}", List("-2"), List("-1"), List(false), true),
      BinEq("{\"1\"} == {\"1\", \"2\"} = {true, false}", List("1"), List("1", "2"), List(true, false), true),
      BinEq("{\"-1\"} == {\"-2\", \"-1\"} = {true, false}", List("-1"), List("-2", "-1"), List(false, true), true),
      BinEq("{\"1\", \"2\"} == {\"2\", \"3\"} = {true, false}", List("1", "2"), List("2", "3"), List(true, false), true),
      BinEq("{\"-1\", \"-3\"} == {\"-3\", \"0.4\"} = {true, false}", List("-1", "-3"), List("-3", "0.4"), List(true, false), true),
      BinEq("{true} == {\"1\"} = {true}", List(true), List("1"), List(true), true),
      BinEq("{false} == {\"0\"} = {true}", List(false), List("0"), List(true), true),
      BinEq("{\"true\", true} == {\"true\"} = {true, false}", List("true", true), List("true"), List(true, false), true),
      BinEq("{1, true} == {\"true\"} = {false}", List(1, true), List("true"), List(false), true),      
      BinEq("{true} == {ture} = {true}", List(true), List(true), List(true), true),
      BinEq("{true} == {false} = {false}", List(true), List(false), List(false), true),
      BinEq("{false} == {true} = {false}", List(false), List(true), List(false), true),
      BinEq("{false} == {false} = {true}", List(false), List(false), List(true), true),
      BinEq("{true, false} == {true} = {true, false}", List(true, false), List(true), List(true, false), true),
      BinEq("{true, false} == {false} = {true, false}", List(true, false), List(false), List(true, false), true),
      BinEq("{true} == {true, false} = {true, false}", List(true), List(true, false), List(true, false), true),
      BinEq("{false} == {true, false} = {true, false}", List(false), List(true, false), List(true, false), true),
      BinEq("{UndefTop} == {NullTop} = {true}", List(UndefTop), List(NullTop), List(true), true),
      BinEq("{NullTop} == {UndefTop} = {true}", List(NullTop), List(UndefTop), List(true), true),
      BinEq("{NaN} == {\"NaN\"} = {false}", List(NaN), List("NaN"), List(false), true),
      BinEq("{1} == {\"1\"} = {true}", List(1), List("1"), List(true), true),
      BinEq("{\"1\"} == {1} = {true}", List("1"), List(1), List(true), true),
      BinEq("{-1} == {\"-1\"} = {true}", List(-1), List("-1"), List(true), true),
      BinEq("{\"-1\"} == {-1} = {true}", List("-1"), List(-1), List(true), true),
      BinEq("{-2} == {-1} = {false}", List(-2), List(-1), List(false), true),
      BinEq("{1} == {\"1\", \"2\"} = {true, false}", List(1), List("1", "2"), List(true, false), true),
      BinEq("{\"1\", \"2\"} == {1} = {true, false}", List("1", "2"), List(1), List(true, false), true),
      BinEq("{-2} == {\"1\"} = {false}", List(-2), List("1"), List(false), true),
      BinEq("{\"1\"} == {-2} = {false}", List("1"), List(-2), List(false), true),
      BinEq("{1, 2} == {\"1\", \"3\"} = {true, false}", List(1, 2), List("1", "3"), List(true, false), true),
      BinEq("{\"1\", \"3\"} == {1, 2} = {true, false}", List("1", "3"), List(1, 2), List(true, false), true),
      BinEq("{-1, -2} == {\"-1\", \"-3\"} = {true, false}", List(-1, -2), List("-1", "-3"), List(true, false), true),
      BinEq("{\"-1\", \"-3\"} == {-1, -2} = {true, false}", List("-1", "-3"), List(-1, -2), List(true, false), true),
      BinEq("{true} == {1} = {true}", List(true), List(1), List(true), true),
      BinEq("{false} == {0} = {true}", List(false), List(0), List(true), true),
      BinEq("{1} == {true} = {true}", List(1), List(true), List(true), true),
      BinEq("{0} == {false} = {true}", List(0), List(false), List(true), true),
      BinEq("{false} == {1} = {false}", List(false), List(1), List(false), true),
      BinEq("{1} == {false} = {false}", List(1), List(false), List(false), true),
      BinEq("{3} == {true} = {false}", List(3), List(true), List(false), true),
      BinEq("{true} == {3} = {false}", List(true), List(3), List(false), true),

      BinNeq("{} != {} = {}", List(), List(), List(), true),
      BinNeq("{UndefTop} != {UndefTop} = {false}", List(UndefTop), List(UndefTop), List(false), true),
      BinNeq("{NullTop} != {NullTop} = {false}", List(NullTop), List(NullTop), List(false), true),
      BinNeq("{NaN} != {3, -1} = {true}", List(NaN), List(3, -1), List(true), true),
      BinNeq("{PosInf} != {NaN} = {true}", List(PosInf), List(NaN), List(true), true),
      BinNeq("{PosInf} != {PosInf} = {false}", List(PosInf), List(PosInf), List(false), true),
      BinNeq("{NegInf} != {NegInf} = {false}", List(NegInf), List(NegInf), List(false), true),
      BinNeq("{PosInf} != {NegInf} = {true}", List(PosInf), List(NegInf), List(true), true),
      BinNeq("{NegInf} != {PosInf} = {true}", List(NegInf), List(PosInf), List(true), true),
      BinNeq("{PosInf, NegInf} != {PosInf, NegInf} = {true, false}", List(PosInf, NegInf), List(PosInf, NegInf), List(true, false), true),
      BinNeq("{PosInf, NegInf} != {NegInf} = {true, false}", List(PosInf, NegInf), List(NegInf), List(true, false), true),
      BinNeq("{PosInf, NegInf} != {PosInf} = {true, false}", List(PosInf, NegInf), List(PosInf), List(true, false), true),
      BinNeq("{PosInf} != {PosInf, NegInf} = {true, false}", List(PosInf), List(PosInf, NegInf), List(true, false), true),
      BinNeq("{NegInf} != {PosInf, NegInf} = {true, false}", List(NegInf), List(PosInf, NegInf), List(true, false), true),
      BinNeq("{1} != {1} = {false}", List(1), List(1), List(false), true),
      BinNeq("{-2} != {-2} = {false}", List(-2), List(-2), List(false), true),
      BinNeq("{1, 2} != {2} = {true, false}", List(1, 2), List(2), List(true, false), true),
      BinNeq("{-1, -2} != {-1} = {false, true}", List(-1, -2), List(-1), List(false, true), true),
      BinNeq("{2} != {1, 2} = {true, false}", List(2), List(1, 2), List(true, false), true),
      BinNeq("{-1} != {-1, -2} = {false, true}", List(-1), List(-1, -2), List(false, true), true),
      BinNeq("{-2} != {1, 2} = {true, true}", List(-2), List(1, 2), List(true), true),
      BinNeq("{1, 2} != {2, 3} = {false, true}", List(1, 2), List(2, 3), List(false, true), true),
      BinNeq("{-1, -2} != {-4, -2} = {false, true}", List(-1, -2), List(-4, -2), List(false, true), true),
      BinNeq("{-1, 3} != {0.5} >= {true, true}", List(-1, 3), List(0.5), List(true), false),
      BinNeq("{\"1\"} != {\"1\"} = {false}", List("1"), List("1"), List(false), true),
      BinNeq("{\"-1\"} != {\"-1\"} = {false}", List("-1"), List("-1"), List(false), true),
      BinNeq("{\"1\"} != {\"2\"} = {true}", List("1"), List("2"), List(true), true),
      BinNeq("{\"-1\"} != {\"-2\"} = {true}", List("-2"), List("-1"), List(true), true),
      BinNeq("{\"1\"} != {\"1\", \"2\"} = {false, true}", List("1"), List("1", "2"), List(false, true), true),
      BinNeq("{\"-1\"} != {\"-2\", \"-1\"} = {false, true}", List("-1"), List("-2", "-1"), List(true, false), true),
      BinNeq("{\"1\", \"2\"} != {\"2\", \"3\"} = {false, true}", List("1", "2"), List("2", "3"), List(false, true), true),
      BinNeq("{\"-1\", \"-3\"} != {\"-3\", \"0.4\"} = {false, true}", List("-1", "-3"), List("-3", "0.4"), List(false, true), true),      
      BinNeq("{false} != {false} = {false}", List(false), List(false), List(false), true),
      BinNeq("{false} != {true} = {true}", List(false), List(true), List(true), true),
      BinNeq("{true} != {false} = {true}", List(true), List(false), List(true), true),
      BinNeq("{true} != {true} = {false}", List(true), List(true), List(false), true),
      BinNeq("{false, true} != {false} = {false, true}", List(false, true), List(false), List(false, true), true),
      BinNeq("{false, true} != {true} = {false, true}", List(false, true), List(true), List(false, true), true),
      BinNeq("{false} != {false, true} = {false, true}", List(false), List(false, true), List(false, true), true),
      BinNeq("{true} != {false, true} = {false, true}", List(true), List(false, true), List(false, true), true),
      BinNeq("{UndefTop} != {NullTop} = {false}", List(UndefTop), List(NullTop), List(false), true),
      BinNeq("{NullTop} != {UndefTop} = {false}", List(NullTop), List(UndefTop), List(false), true),
      BinNeq("{NaN} != {\"NaN\"} = {true}", List(NaN), List("NaN"), List(true), true),
      BinNeq("{1} != {\"1\"} = {false}", List(1), List("1"), List(false), true),
      BinNeq("{\"1\"} != {1} = {false}", List("1"), List(1), List(false), true),
      BinNeq("{-1} != {\"-1\"} = {false}", List(-1), List("-1"), List(false), true),
      BinNeq("{\"-1\"} != {-1} = {false}", List("-1"), List(-1), List(false), true),
      BinNeq("{-2} != {-1} = {true}", List(-2), List(-1), List(true), true),
      BinNeq("{1} != {\"1\", \"2\"} = {false, true}", List(1), List("1", "2"), List(false, true), true),
      BinNeq("{\"1\", \"2\"} != {1} = {false, true}", List("1", "2"), List(1), List(false, true), true),
      BinNeq("{-2} != {\"1\"} = {true}", List(-2), List("1"), List(true), true),
      BinNeq("{\"1\"} != {-2} = {true}", List("1"), List(-2), List(true), true),
      BinNeq("{1, 2} != {\"1\", \"3\"} = {false, true}", List(1, 2), List("1", "3"), List(false, true), true),
      BinNeq("{\"1\", \"3\"} != {1, 2} = {false, true}", List("1", "3"), List(1, 2), List(true, false), true),
      BinNeq("{-1, -2} != {\"-1\", \"-3\"} = {false, true}", List(-1, -2), List("-1", "-3"), List(false, true), true),
      BinNeq("{\"-1\", \"-3\"} != {-1, -2} = {false, true}", List("-1", "-3"), List(-1, -2), List(false, true), true),
      BinNeq("{true} != {1} = {false}", List(true), List(1), List(false), true),
      BinNeq("{false} != {0} = {false}", List(false), List(0), List(false), true),
      BinNeq("{1} != {true} = {false}", List(1), List(true), List(false), true),
      BinNeq("{0} != {false} = {false}", List(0), List(false), List(false), true),
      BinNeq("{false} != {1} = {true}", List(false), List(1), List(true), true),
      BinNeq("{1} != {false} = {true}", List(1), List(false), List(true), true),
      BinNeq("{3} != {true} = {true}", List(3), List(true), List(true), true),
      BinNeq("{true} != {3} = {true}", List(true), List(3), List(true), true),

      BinSEq("{} === {} = {}", List(), List(), List(), true),
      BinSEq("{UndefTop} === {UndefTop} = {true}", List(UndefTop), List(UndefTop), List(true), true),
      BinSEq("{NullTop} === {NullTop} = {true}", List(NullTop), List(NullTop), List(true), true),
      BinSEq("{} === {1, -1} = {}", List(), List(1, -1), List(), true),
      BinSEq("{1, -1} === {} = {}", List(1, -1), List(), List(), true),
      BinSEq("{NaN} === {1} = {false}", List(NaN), List(1), List(false), true),
      BinSEq("{2} === {NaN} = {false}", List(2), List(NaN), List(false), true),
      BinSEq("{PosInf} === {NegInf} = {false}", List(PosInf), List(NegInf), List(false), true),
      BinSEq("{PosInf, NegInf} === {PosInf} = {true, false}", List(PosInf, NegInf), List(PosInf), List(true, false), true),
      BinSEq("{1} === {1} = {true}", List(1), List(1), List(true), true),
      BinSEq("{-2} === {-2} = {true}", List(-2), List(-2), List(true), true),
      BinSEq("{1, 2} === {2} = {false, true}", List(1, 2), List(2), List(false, true), true),
      BinSEq("{-1, -2} === {-1} = {true, false}", List(-1, -2), List(-1), List(true, false), true),
      BinSEq("{2} === {1, 2} = {false, true}", List(2), List(1, 2), List(false, true), true),
      BinSEq("{-1} == {-1, -2} = {true, false}", List(-1), List(-1, -2), List(true, false), true),
      BinSEq("{-2} === {1, 2} = {false, false}", List(-2), List(1, 2), List(false), true),
      BinSEq("{1, 2} === {2, 3} = {true, false}", List(1, 2), List(2, 3), List(true, false), true),
      BinSEq("{-1, -2} === {-4, -2} = {true, false}", List(-1, -2), List(-4, -2), List(true, false), true),
      BinSEq("{-1, 3} === {0.5} >= {false, false}", List(-1, 3), List(0.5), List(false), false),
      BinSEq("{1} === {\"1\"} = {false}", List(1), List("1"), List(false), true),
      BinSEq("{\"1\"} === {1} = {false}", List("1"), List(1), List(false), true),
      BinSEq("{\"1\"} === {\"1\"} = {true}", List("1"), List("1"), List(true), true),
      BinSEq("{\"-1\"} === {\"-1\"} = {true}", List("-1"), List("-1"), List(true), true),
      BinSEq("{\"1\"} === {\"2\"} = {false}", List("1"), List("2"), List(false), true),
      BinSEq("{\"-1\"} === {\"-2\"} = {false}", List("-2"), List("-1"), List(false), true),
      BinSEq("{\"1\"} === {\"1\", \"2\"} = {true, false}", List("1"), List("1", "2"), List(true, false), true),
      BinSEq("{\"-1\"} === {\"-2\", \"-1\"} = {true, false}", List("-1"), List("-2", "-1"), List(false, true), true),
      BinSEq("{\"1\", \"2\"} === {\"2\", \"3\"} = {true, false}", List("1", "2"), List("2", "3"), List(true, false), true),
      BinSEq("{\"-1\", \"-3\"} === {\"-3\", \"0.4\"} = {true, false}", List("-1", "-3"), List("-3", "0.4"), List(true, false), true),
      BinSEq("{\"s\"} === {\"s\"} = {true}", List("s"), List("s"), List(true), true),
      BinSEq("{\"Ta\"} === {\"ta\"} = {false}", List("Ta"), List("ta"), List(false), true),
      BinSEq("{true} === {\"1\"} = {false}", List(true), List("1"), List(false), true),
      BinSEq("{false} === {\"0\"} = {false}", List(false), List("0"), List(false), true),
      BinSEq("{true} === {1} = {false}", List(true), List(1), List(false), true),
      BinSEq("{false} === {0} = {false}", List(false), List(0), List(false), true),
      BinSEq("{true} === {true} = {true}", List(true), List(true), List(true), true),

      BinSNeq("{} !== {} = {}", List(), List(), List(), true),
      BinSNeq("{UndefTop} !== {UndefTop} = {false}", List(UndefTop), List(UndefTop), List(false), true),
      BinSNeq("{NullTop} !== {NullTop} = {false}", List(NullTop), List(NullTop), List(false), true),
      BinSNeq("{} !== {1, -1} = {}", List(), List(1, -1), List(), true),
      BinSNeq("{1, -1} !== {} = {}", List(1, -1), List(), List(), true),
      BinSNeq("{NaN} !== {1} = {true}", List(NaN), List(1), List(true), true),
      BinSNeq("{2} !== {NaN} = {true}", List(2), List(NaN), List(true), true),
      BinSNeq("{PosInf} !== {NegInf} = {true}", List(PosInf), List(NegInf), List(true), true),
      BinSNeq("{PosInf, NegInf} !== {PosInf} = {false, true}", List(PosInf, NegInf), List(PosInf), List(false, true), true),
      BinSNeq("{1} !== {1} = {false}", List(1), List(1), List(false), true),
      BinSNeq("{-2} !== {-2} = {false}", List(-2), List(-2), List(false), true),
      BinSNeq("{1, 2} !== {2} = {true, false}", List(1, 2), List(2), List(true, false), true),
      BinSNeq("{-1, -2} !== {-1} = {false, true}", List(-1, -2), List(-1), List(false, true), true),
      BinSNeq("{2} !== {1, 2} = {true, false}", List(2), List(1, 2), List(true, false), true),
      BinSNeq("{-1} !== {-1, -2} = {false, true}", List(-1), List(-1, -2), List(false, true), true),
      BinSNeq("{-2} !== {1, 2} = {true, true}", List(-2), List(1, 2), List(true), true),
      BinSNeq("{1, 2} !== {2, 3} = {false, true}", List(1, 2), List(2, 3), List(false, true), true),
      BinSNeq("{-1, -2} !== {-4, -2} = {false, true}", List(-1, -2), List(-4, -2), List(false, true), true),
      BinSNeq("{-1, 3} !== {0.5} >= {true, true}", List(-1, 3), List(0.5), List(true), false),
      BinSNeq("{1} !== {\"1\"} = {true}", List(1), List("1"), List(true), true),
      BinSNeq("{\"1\"} !== {1} = {true}", List("1"), List(1), List(true), true),
      BinSNeq("{\"1\"} !== {\"1\"} = {false}", List("1"), List("1"), List(false), true),
      BinSNeq("{\"-1\"} !== {\"-1\"} = {false}", List("-1"), List("-1"), List(false), true),
      BinSNeq("{\"1\"} !== {\"2\"} = {true}", List("1"), List("2"), List(true), true),
      BinSNeq("{\"-1\"} !== {\"-2\"} = {true}", List("-2"), List("-1"), List(true), true),
      BinSNeq("{\"1\"} !== {\"1\", \"2\"} = {false, true}", List("1"), List("1", "2"), List(false, true), true),
      BinSNeq("{\"-1\"} !== {\"-2\", \"-1\"} = {false, true}", List("-1"), List("-2", "-1"), List(true, false), true),
      BinSNeq("{\"1\", \"2\"} !== {\"2\", \"3\"} = {false, true}", List("1", "2"), List("2", "3"), List(false, true), true),
      BinSNeq("{\"-1\", \"-3\"} !== {\"-3\", \"0.4\"} = {false, true}", List("-1", "-3"), List("-3", "0.4"), List(false, true), true),
      BinSNeq("{\"s\"} !== {\"s\"} = {false}", List("s"), List("s"), List(false), true),
      BinSNeq("{\"Ta\"} !== {\"ta\"} = {true}", List("Ta"), List("ta"), List(true), true),
      BinSNeq("{true} !== {\"1\"} = {true}", List(true), List("1"), List(true), true),
      BinSNeq("{false} !== {\"0\"} = {true}", List(false), List("0"), List(true), true),
      BinSNeq("{true} !== {1} = {true}", List(true), List(1), List(true), true),
      BinSNeq("{false} !== {0} = {true}", List(false), List(0), List(true), true),
      BinSNeq("{true} !== {true} = {false}", List(true), List(true), List(false), true),

      BinLess("{2, \"-3\"} < {NaN, \"5\"} = {true, false}", List(2, "-3"), List(NaN, "5"), List(true, false), true),
      BinLess("{} < {} = {}", List(), List(), List(), true),
      BinLess("{false} < {true} = {true}", List(false), List(true), List(true), true),
      BinLess("{true} < {false} = {false}", List(true), List(false), List(false), true),
      BinLess("{false} < {true, false} = {true, false}", List(false), List(true, false), List(true, false), true),
      BinLess("{true} < {true, false} >= {false}", List(true), List(true, false), List(false), false),
      BinLess("{true, false} < {true} = {false, true}", List(true, false), List(true), List(true, false), true),
      BinLess("{true, false} < {false} = {false}", List(true, false), List(false), List(false), true),
      BinLess("{null} < {true} = {true}", List(NullTop), List(true), List(true), true),
      BinLess("{false} < {null} = {false}", List(false), List(NullTop), List(false), true),
      BinLess("{NaN} < {3} = {false}", List(NaN), List(3), List(false), true),
      BinLess("{2} < {NaN} = {false}", List(2), List(NaN), List(false), true),
      BinLess("{PosInf} < {PosInf} = {false}", List(PosInf), List(PosInf), List(false), true),
      BinLess("{NegInf} < {NegInf} = {false}", List(NegInf), List(NegInf), List(false), true),
      BinLess("{PosInf, NegInf} < {1} = {true, false}", List(PosInf, NegInf), List(1), List(true, false), true),
      BinLess("{3} < {PosInf, NegInf} = {true, false}", List(3), List(PosInf, NegInf), List(true, false), true),
      BinLess("{PosInf} < {NegInf} = {false}", List(PosInf), List(NegInf), List(false), true),
      BinLess("{NegInf} < {PosInf} = {true}", List(NegInf), List(PosInf), List(true), true),
      BinLess("{1} < {2} = {true}", List(1), List(2), List(true), true),
      BinLess("{2} < {1} = {false}", List(2), List(1), List(false), true),
      BinLess("{1} < {1.5} = {true}", List(1), List(1.5), List(true), true),
      BinLess("{1} < {-1} = {false}", List(1), List(-1), List(false), true),
      BinLess("{-3} < {1} = {true}", List(-3), List(1), List(true), true),
      BinLess("{3.4} < {1} = {false}", List(3.4), List(1), List(false), true),
      BinLess("{-2} < {-1} = {true}", List(-2), List(-1), List(true), true),
      BinLess("{-1} < {-2} = {false}", List(-1), List(-2), List(false), true),
      BinLess("{1, 2} < {2, 3} = {true, false}", List(1, 2), List(2, 3), List(true, false), true),
      BinLess("{-2, -3} < {-5, -2} = {true, false}", List(-2, -3), List(-5, -2), List(true, false), true),
      BinLess("{-2, \"-5\"} < {\"-3\", -2} = {true, false}", List(-2, "-5"), List("-3", -2), List(true, false), true),
      BinLess("{\"1\"} < {\"2\"} = {true}", List("1"), List("2"), List(true), true),
      BinLess("{\"1\"} < {\"1\"} = {false}", List("1"), List("1"), List(false), true),
      BinLess("{\"1\"} < {\"1d\"} = {true}", List("1"), List("1d"), List(true), true),
      BinLess("{\"1\"} < {\"-1\"} = {false}", List("1"), List("-1"), List(false), true),
      BinLess("{\"\"} < {\"1\"} = {true}", List(""), List("1"), List(true), true),
      BinLess("{\"s\"} < {\"1\"} = {false}", List("s"), List("1"), List(false), true),
      BinLess("{\"s\"} < {\"t\"} = {true}", List("s"), List("t"), List(true), true),
      BinLess("{\"s\"} < {\"s1\"} = {true}", List("s"), List("s1"), List(true), true),
      BinLess("{\"s\"} < {\"d\"} = {false}", List("s"), List("d"), List(false), true),
      BinLess("{\"1\", \"2\"} < {\"12\", \"23\"} = {true, false}", List("1", "2"), List("12", "23"), List(true, false), true),
      BinLess("{\"s\", \"d\"} < {\"sd\"} = {ture, false}", List("s", "d"), List("sd"), List(true, false), true),
      BinLess("{\"-5\"} < {\"-3\", -2} = {true, false}", List("-5"), List("-3", -2), List(true, false), true),
      BinLess("{3} < {\"5\", NaN} = {true, false}", List(3), List("5", NaN), List(true, false), true),
      BinLess("{-2, \"-5\"} < {\"-3\", NaN} = {false}", List(-2, "-5"), List("-3", NaN), List(false), false),

      BinGreater("{} > {} = {}", List(), List(), List(), true),
      BinGreater("{2} > {1} = {true}", List(2), List(1), List(true), true),
      BinGreater("{2} > {4} = {false}", List(2), List(4), List(false), true),
      BinGreater("{2} > {1, 4} = {true, false}", List(2), List(1, 4), List(true, false), true),
      BinGreater("{2} > {-1} = {true}", List(2), List(-1), List(true), true),
      BinGreater("{2} > {3.5} = {false}", List(2), List(3.5), List(false), true),
      BinGreater("{2} > {-1, 3.5} = {true, false}", List(2), List(-1, 3.5), List(true, false), true),
      BinGreater("{-3} > {1} = {false}", List(-3), List(1), List(false), true),
      BinGreater("{4.3} > {1} = {true}", List(4.3), List(1), List(true), true),
      BinGreater("{-3} > {0, 1} = {false}", List(-3), List(0, 1), List(false), true),
      BinGreater("{4.5} > {0, 5} = {ture, false}", List(4.5), List(0, 5), List(true, false), true),
      BinGreater("{-2} > {4.5} = {false}", List(-2), List(4.5), List(false), true),
      BinGreater("{3.2} > {-1} = {true}", List(3.2), List(-1), List(true), true),
      BinGreater("{-3} > {-2, -5} = {true, false}", List(-3), List(-2, -5), List(true, false), true),
      BinGreater("{1, 3} > {2} = {true, false}", List(1, 3), List(2), List(true, false), true),
      BinGreater("{1, 2} > {3.5} >= {false}", List(1, 2), List(3.5), List(false), false),
      BinGreater("{1, 2} > {1.5} = {true, false}", List(1, 2), List(1.5), List(true, false), true),
      BinGreater("{1, 2} > {-1, 3.5} = {true, false}", List(1, 2), List(-1, 3.5), List(true, false), true),
      BinGreater("{-2, -5} > {2} >= {false}", List(-2, -5), List(2), List(false), false),
      BinGreater("{-1, 3.5} > {2} = {true, false}", List(-1, 3.5), List(2), List(true, false), true),
      BinGreater("{-2, -3} > {-4} >= {true}", List(-2, -3), List(-4), List(true), false),
      BinGreater("{-2, -3} > {0, 6} >= {false}", List(-2, -3), List(0, 6), List(false), false),
      BinGreater("{-2, 5.5} > {0, 6} = {true, false}", List(-2, 5.5), List(0, 6), List(true, false), true),
      BinGreater("{-3, 2.5} > {1.5, -2} = {true, false}", List(-3, 2.5), List(1.5, -2), List(true, false), true),
      BinGreater("{-2, \"-3\"} > {\"-5\", -2} = {true, false}", List(-2, "-3"), List("-5", -2), List(true, false), true),
      BinGreater("{false} > {true} = {false}", List(false), List(true), List(false), true),
      BinGreater("{true} > {false} = {true}", List(true), List(false), List(true), true),
      BinGreater("{false} > {true, false} = {false}", List(false), List(true, false), List(false), true),
      BinGreater("{true, false} > {true} >= {false}", List(true, false), List(true), List(false), false),
      BinGreater("{true, false} > {false} = {true, false}", List(true, false), List(false), List(true, false), true),
      BinGreater("{null} > {true} = {false}", List(NullTop), List(true), List(false), true),
      BinGreater("{false} > {null} = {false}", List(false), List(NullTop), List(false), true),
      BinGreater("{NaN} > {3} = {false}", List(NaN), List(3), List(false), true),
      BinGreater("{2} > {NaN} = {false}", List(2), List(NaN), List(false), true),
      BinGreater("{PosInf} > {PosInf} = {false}", List(PosInf), List(PosInf), List(false), true),
      BinGreater("{NegInf} > {NegInf} = {false}", List(NegInf), List(NegInf), List(false), true),
      BinGreater("{PosInf, NegInf} > {1} = {true, false}", List(PosInf, NegInf), List(1), List(true, false), true),
      BinGreater("{3} > {PosInf, NegInf} = {true, false}", List(3), List(PosInf, NegInf), List(true, false), true),
      BinGreater("{PosInf} > {NegInf} = {true}", List(PosInf), List(NegInf), List(true), true),
      BinGreater("{NegInf} > {PosInf} = {false}", List(NegInf), List(PosInf), List(false), true),
      BinGreater("{-2, \"-3\"} > {-4, \"-1\"} >= {true, false}", List(-2, "-3"), List(-4, "-1"), List(true, false), true),
      BinGreater("{} > {} = {}", List(), List(), List(), true),
      BinGreater("{\"-5\"} > {\"-3\", -2} = {true, false}", List("-5"), List("-3", -2), List(true, false), true),
      BinGreater("{-2} > {\"-3\", NaN} = {true, false}", List(-2), List("-3", NaN), List(true, false), true),
      BinGreater("{3} > {\"5\", NaN} = {false}", List(3), List("5", NaN), List(false), false),
      
      BinLessEq("{} <= {} = {}", List(), List(), List(), true),
      BinLessEq("{} <= {1} = {}", List(), List(1), List(), true),
      BinLessEq("{1} <= {} = {}", List(1), List(), List(), true),
      BinLessEq("{true} <= {true} = {true}", List(true), List(true), List(true), true),
      BinLessEq("{false} <= {true, false} = {true}", List(false), List(true, false), List(true), true),
      BinLessEq("{null} <= {0, 1} = {true}", List(NullTop), List(0, 1), List(true), true),
      BinLessEq("{null} <= {-1, -4} = {true, false}", List(NullTop), List(-1, -4), List(true, false), true),
      BinLessEq("{UndefTop} <= {0, 1} = {false}", List(UndefTop), List(0, 1), List(false), true),
      BinLessEq("{PosInf} <= {PosInf} = {true}", List(PosInf), List(PosInf), List(true), true),
      BinLessEq("{NegInf} <= {PosInf} = {true}", List(NegInf), List(PosInf), List(true), true),
      BinLessEq("{PosInf} <= {NegInf} = {false}", List(PosInf), List(NegInf), List(false), true),
      BinLessEq("{NegInf} <= {NegInf} = {true}", List(NegInf), List(NegInf), List(true), true),
      BinLessEq("{NegInf} <= {PosInf, NegInf} = {true}", List(NegInf), List(PosInf, NegInf), List(true), true),
      BinLessEq("{PosInf} <= {PosInf, NegInf} = {true, false}", List(PosInf), List(PosInf, NegInf), List(true, false), true),
      BinLessEq("{PosInf, NegInf} <= {PosInf} = {true}", List(PosInf, NegInf), List(PosInf), List(true), true),
      BinLessEq("{PosInf, NegInf} <= {NegInf} = {true, false}", List(PosInf, NegInf), List(NegInf), List(true, false), true),
      BinLessEq("{1} <= {1} = {true}", List(1), List(1), List(true), true),
      BinLessEq("{1} <= {0, 1} = {true, false}", List(1), List(0, 1), List(true, false), true),
      BinLessEq("{1} <= {-1} = {false}", List(1), List(-1), List(false), true),
      BinLessEq("{1} <= {2.3} = {true}", List(1), List(2.3), List(true), true),
      BinLessEq("{1} <= {-1, 2.3} = {true, false}", List(1), List(-1, 2.3), List(true, false), true),
      BinLessEq("{-1} <= {-1} = {true}", List(-1), List(-1), List(true), true),
      BinLessEq("{-1} <= {1} = {true}", List(-1), List(1), List(true), true),
      BinLessEq("{-1} <= {0, 1} = {true}", List(-1), List(0, 1), List(true), true),
      BinLessEq("{-1} <= {-3} = {false}", List(-1), List(-3), List(false), true),
      BinLessEq("{-1} <= {2.5, -3} = {true, false}", List(-1), List(2.5, -3), List(true, false), true),
      BinLessEq("{2.5} <= {3} = {true}", List(2.5), List(3), List(true), true),
      BinLessEq("{2.5} <= {3.5} = {true}", List(2.5), List(3.5), List(true), true),
      BinLessEq("{2.5} <= {2, 3} = {true, false}", List(2.5), List(2, 3), List(true, false), true),
      BinLessEq("{2.5} <= {-1, 3.5} = {true, false}", List(2.5), List(-1, 3.5), List(true, false), true),
      BinLessEq("{0, 1} <= {0} = {true, false}", List(0, 1), List(0), List(true, false), true),
      BinLessEq("{0, 1} <= {-1} = {false}", List(0, 1), List(-1), List(false), true),
      BinLessEq("{0, 1} <= {0, 3} = {true, false}", List(0, 1), List(0, 3), List(true, false), true),
      BinLessEq("{0, 1} <= {-2, 3.5} = {true, false}", List(0, 1), List(-2, 3.5), List(true, false), true),
      BinLessEq("{-1, 2.5} <= {1} = {true, false}", List(-1, 2.5), List(1), List(true, false), true),
      BinLessEq("{-1, 2.5} <= {-1} = {true, false}", List(-1, 2.5), List(-1), List(true, false), true),
      BinLessEq("{-1, 2.5} <= {0, 1} = {true, false}", List(-1, 2.5), List(0, 1), List(true, false), true),
      BinLessEq("{-1, 2.5} <= {-3, 2.5} = {true, false}", List(-1, 2.5), List(-3, 2.5), List(true, false), true),
      BinLessEq("{-1, \"2\"} <= {\"3\", 2.5} = {true, false}", List(-1, "2"), List("3", 2.5), List(true, false), true),
      BinLessEq("{\"s\"} <= {\"s\"} = {true}", List("s"), List("s"), List(true), true),
      BinLessEq("{\"s\"} <= {\"str\"} = {true}", List("s"), List("str"), List(true), true),
      BinLessEq("{\"a\"} <= {\"b\"} = {true}", List("a"), List("b"), List(true), true),
      BinLessEq("{\"1\"} <= {\"2\"} = {true}", List("1"), List("2"), List(true), true),
      BinLessEq("{\"0\"} <= {\"-3\"} = {false}", List("0"), List("-3"), List(false), true),
      BinLessEq("{\"0\", \"1\"} <= {\"0\", \"3\"} = {true, false}", List("0", "1"), List("0", "3"), List(true, false), true),
      BinLessEq("{\"0\", \"1\"} <= {\"-1\"} = {false}", List("0", "1"), List("-1"), List(false), false),
      BinLessEq("{\"-1\", \"2.5\"} <= {\"-1\"} = {true, false}", List("-1", "2.5"), List("-1"), List(true, false), true),
      BinLessEq("{\"-5\"} <= {\"-3\", -2} = {true, false}", List("-5"), List("-3", -2), List(true, false), true),
      BinLessEq("{-2} <= {\"-3\", NaN} = {false}", List(-2), List("-3", NaN), List(false), false),
      BinLessEq("{3} <= {\"5\", NaN} = {false}", List(3), List("5", NaN), List(false), false),
      
      BinGreaterEq("{} >= {} = {}", List(), List(), List(), true),
      BinGreaterEq("{NaN} >= {2} = {false}", List(NaN), List(2), List(false), true),
      BinGreaterEq("{PosInf} >= {NegInf} = {true}", List(PosInf), List(NegInf), List(true), true),
      BinGreaterEq("{PosInf} >= {PosInf} = {true}", List(PosInf), List(PosInf), List(true), true),
      BinGreaterEq("{NegInf} >= {PosInf} = {false}", List(NegInf), List(PosInf), List(false), true),
      BinGreaterEq("{NegInf} >= {NegInf} = {true}", List(NegInf), List(NegInf), List(true), true),
      BinGreaterEq("{PosInf, NegInf} >= {NegInf} = {true}", List(PosInf, NegInf), List(NegInf), List(true), true),
      BinGreaterEq("{PosInf} >= {PosInf, NegInf} = {true}", List(PosInf), List(PosInf, NegInf), List(true), true),
      BinGreaterEq("{NegInf} >= {PosInf, NegInf} = {true, false}", List(NegInf), List(PosInf, NegInf), List(true, false), true),
      BinGreaterEq("{1} >= {1} = {true}", List(1), List(1), List(true), true),
      BinGreaterEq("{3} >= {5} = {false}", List(3), List(5), List(false), true),
      BinGreaterEq("{1} >= {-1} = {true}", List(1), List(-1), List(true), true),
      BinGreaterEq("{1} >= {1, 3} = {true, false}", List(1), List(1, 3), List(true, false), true),
      BinGreaterEq("{1} >= {-1, 3.5} = {true, false}", List(1), List(-1, 3.5), List(true, false), true),
      BinGreaterEq("{-1} >= {0} = {false}", List(-1), List(0), List(false), true),
      BinGreaterEq("{3.5} >= {0} = {true}", List(3.5), List(0), List(true), true),
      BinGreaterEq("{-1} >= {-2} = {true}", List(-1), List(-2), List(true), true),
      BinGreaterEq("{-5} >= {-2} = {false}", List(-5), List(-2), List(false), true),
      BinGreaterEq("{-1} >= {1, 2} = {false}", List(-1), List(1, 2), List(false), true),
      BinGreaterEq("{3.5} >= {3, 4} = {true, false}", List(3.5), List(3, 4), List(true, false), true),
      BinGreaterEq("{-1} >= {-2, 2.5} = {ture, false}", List(-1), List(-2, 2.5), List(true, false), true),
      BinGreaterEq("{0, 1} >= {0} = {true}", List(0, 1), List(0), List(true), true),
      BinGreaterEq("{0, 1} >= {-2} = {true}", List(0, 1), List(-2), List(true), true),
      BinGreaterEq("{0, 1} >= {0.5} = {true, false}", List(0, 1), List(0.5), List(true, false), true),
      BinGreaterEq("{0, 1} >= {0, 3} = {true, false}", List(0, 1), List(0, 3), List(true, false), true),
      BinGreaterEq("{0, 1} >= {-2, 0.5} = {true, false}", List(0, 1), List(-2, 0.5), List(true, false), true),
      BinGreaterEq("{\"0\", 1} >= {\"-2\", 0.5} = {true, false}", List("0", 1), List("-2", 0.5), List(true, false), true),
      BinGreaterEq("{-2, 3.5} >= {1} = {true, false}", List(-2, 3.5), List(1), List(true, false), true),
      BinGreaterEq("{-2, 3.5} >= {0, 1} = {true, false}", List(-2, 3.5), List(0, 1), List(true, false), true),
      BinGreaterEq("{-2, 3.5} >= {-2} >= {true}", List(-2, 3.5), List(-2), List(true), false),
      BinGreaterEq("{-2, 3.5} >= {-3, 4.2} = {true, false}", List(-2, 3.5), List(-3, 4.2), List(true, false), true),
      BinLessEq("{\"-5\"} >= {\"-3\", -2} = {true, false}", List("-5"), List("-3", -2), List(true, false), true),
      BinLessEq("{-2} >= {\"-3\", NaN} = {true, false}", List(-2), List("-3", NaN), List(true, false), true),
      BinLessEq("{3} >= {\"5\", NaN} = {false}", List(3), List("5", NaN), List(false), false)
      )
  val unaCases:List[TypeOperator] = List (
      UnaVoid("void {1} = {\"undefined\"}", List(1), List(UndefTop), true),
      UnaVoid("void {null} = {\"undefined\"}", List(NullTop), List(UndefTop), true),
      UnaVoid("void {null, PosInf} = {\"undefined\"}", List(NullTop, PosInf), List(UndefTop), true),

      UnaPlus("+{null} = {0}", List(NullTop), List(0), true),
      UnaPlus("+{true, 1} = {1}", List(true, 1), List(1), true),

      UnaMinus("-{NaN} = {NaN}", List(NaN), List(NaN), true),
      UnaMinus("-{0} = {0}", List(0), List(0), true),
      UnaMinus("-{1} = {-1}", List(1), List(-1), true),
      UnaMinus("-{-3.2} = {3.2}", List(-3.2), List(3.2), true),
      UnaMinus("-{-3} = {3}", List(-3), List(3), true),
      UnaMinus("-{1, 3} = {-3, -1}", List(1,3), List(-1,-3), true),
      UnaMinus("-{-1, 2.1} = {1, -2.1}", List(-1, 2.1), List(1, -2.1), true),
      UnaMinus("-{PosInf} = {NegInf}", List(PosInf), List(NegInf), true),
      UnaMinus("-{NegInf} = {PosInf}", List(NegInf), List(PosInf), true),
      UnaMinus("-{\"str\", null} = {NaN, 0}", List("str", NullTop), List(NaN, 0), true),

      UnaBitNeg("~{32} = {-33}", List(32), List(-33), true),
      UnaBitNeg("~{3.1} = {-4}", List(3.1), List(-4), true),
      UnaBitNeg("~{3, 10} = {-4, -11}", List(3, 10), List(-4, -11), true),
      UnaBitNeg("~{-3, 0.5} = {2, -1}", List(-3, 0.5), List(2, -1), true),
      UnaBitNeg("~{1, -1} = {-2, 0}", List(1, -1), List(-2, 0), true),

      UnaNeg("!{true} = {false}", List(true), List(false), true),
      UnaNeg("!{false} = {true}", List(false), List(true), true),
      UnaNeg("!{true, false = {false, true}}", List(true, false), List(false, true), true)
      )

  def suite(): Test = {
    // Initialize AddressManager
    AddressManager.reset()

    val suite = new TestSuite("Typing Operator Test")
    val suiteJoin = new TestSuite("Join")
    val suiteBin = new TestSuite("Binary Operators")
    val suiteUna = new TestSuite("Unary Operators")
    for(joinCase <-joinCases) {
      suiteJoin.addTest(new JoinTest(joinCase._1, joinCase._2, joinCase._3, joinCase._4, joinCase._5, "testJoin"))
    }
    for(binCase <-binCases) {
      binCase match {
        case BinBitOr(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testBitOr"))
        case BinBitAnd(name, lhs, rhs, expec, equal) =>
         suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testBitAnd"))
        case BinBitXor(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testBitXor"))
        case BinLShift(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testLShift"))
        case BinRShift(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testRShift"))
        case BinURShift(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testURShift"))
        case BinPlus(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testPlus"))
        case BinMinus(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testMinus"))
        case BinMul(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testMul"))
        case BinDiv(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testDiv"))
        case BinMod(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testMod"))
        case BinEq(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testEq"))
        case BinNeq(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testNeq"))
        case BinSEq(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testSEq"))
        case BinSNeq(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testSNeq"))
        case BinLess(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testLess"))
        case BinGreater(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testGreater"))
        case BinLessEq(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testLessEq"))
        case BinGreaterEq(name, lhs, rhs, expec, equal) =>
          suiteBin.addTest(new BinTest(name, lhs, rhs, expec, equal, "testGreaterEq"))
      }
    }
    for(unaCase <-unaCases) {
      unaCase match {
        case UnaVoid(name, oprnd, expec, equal) =>
          suiteUna.addTest(new UnaTest(name, oprnd, expec, equal, "testVoid"))
        case UnaPlus(name, oprnd, expec, equal) =>
          suiteUna.addTest(new UnaTest(name, oprnd, expec, equal, "testPlus"))
        case UnaMinus(name, oprnd, expec, equal) =>
          suiteUna.addTest(new UnaTest(name, oprnd, expec, equal, "testMinus"))
        case UnaBitNeg(name, oprnd, expec, equal) =>
          suiteUna.addTest(new UnaTest(name, oprnd, expec, equal, "testBitNeg"))
        case UnaNeg(name, oprnd, expec, equal) =>
          suiteUna.addTest(new UnaTest(name, oprnd, expec, equal, "testNeg"))
      }
    }
    suite.addTest(suiteJoin)
    suite.addTest(suiteBin)
    suite.addTest(suiteUna)
    suite
  }
}

class OperatorTestCase(func:String) extends TestCase(func) {
  // alpha function : concs -> abs
  def toValue(in:List[Any]):Value = {
    var v:Value = ValueBot
    for(i <-in) {
      v = i match {
        case u:AbsUndef if u.isTop =>   v + Value(AbsUndef.alpha)
        case n:AbsNumber => n.getAbsCase match {
          case AbsSingle if !(n.getSingle.isDefined && AbsNumber.isNum(n)) => v + Value(n)
          case _ => v
        }
        case n:Int =>			v + Value(AbsNumber.alpha(n))
        case d:Number =>		v + Value(AbsNumber.alpha(d.doubleValue))
        case s:String =>		v + Value(AbsString.alpha(s))
        case b:Boolean =>		v + Value(AbsBool.alpha(b))
        case n:AbsNull if n.isTop =>	v + Value(AbsNull.alpha)
      }
    }
    v
  }
}

class JoinTest(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean, func:String) extends OperatorTestCase(func) {
  var _left:Value = ValueBot
  var _right:Value = ValueBot
  var _expec:Value = ValueBot
  def joinTest() {}
  override def getName = name
  override def setUp() = {
    _left = super.toValue(lhs)
    _right = super.toValue(rhs)
    _expec = super.toValue(expec)
  }  
  def testJoin = {
    assertTrue(_expec <= (_left + _right))
    if (equal) assertTrue((_left + _right) <= _expec)
  }
}

class BinTest(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean, func:String) extends OperatorTestCase(func) {
  var leftVal:Value = ValueBot
  var rightVal:Value = ValueBot
  var expecVal:Value = ValueBot

  def binTest() {}
  override def getName = name

  override def setUp = {
    leftVal = super.toValue(lhs)
    rightVal = super.toValue(rhs)
    expecVal = super.toValue(expec)
  }
  
  def testBitOr = {
    assertTrue(expecVal <= Operator.bopBitOr(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopBitOr(leftVal, rightVal) <= expecVal)
  }
  def testBitAnd = {
    assertTrue(expecVal <= Operator.bopBitAnd(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopBitAnd(leftVal, rightVal) <= expecVal)
  }
  def testBitXor = {
    assertTrue(expecVal <= Operator.bopBitXor(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopBitXor(leftVal, rightVal) <= expecVal)
  }
  def testLShift = {
    assertTrue(expecVal <= Operator.bopLShift(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopLShift(leftVal, rightVal) <= expecVal)
  }
  def testRShift = {
    assertTrue(expecVal <= Operator.bopRShift(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopRShift(leftVal, rightVal) <= expecVal)
  }
  def testURShift = {
    assertTrue(expecVal <= Operator.bopURShift(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopURShift(leftVal, rightVal) <= expecVal)
  }
  def testPlus = {
    assertTrue(expecVal <= Operator.bopPlus(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopPlus(leftVal, rightVal) <= expecVal)
  }
  def testMinus = {
    assertTrue(expecVal <= Operator.bopMinus(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopMinus(leftVal, rightVal) <= expecVal)
  }
  def testMul = {
    assertTrue(expecVal <= Operator.bopMul(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopMul(leftVal, rightVal) <= expecVal)
  }
  def testDiv = {
    assertTrue(expecVal <= Operator.bopDiv(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopDiv(leftVal, rightVal) <= expecVal)
  }
  def testMod = {
    assertTrue(expecVal <= Operator.bopMod(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopMod(leftVal, rightVal) <= expecVal)
  }
  def testEq = {
    assertTrue(expecVal <= Operator.bopEq(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopEq(leftVal, rightVal) <= expecVal)
  }
  def testNeq = {
    assertTrue(expecVal <= Operator.bopNeq(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopNeq(leftVal, rightVal) <= expecVal)
  }
  def testSEq = {
    assertTrue(expecVal <= Operator.bopSEq(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopSEq(leftVal, rightVal) <= expecVal)
  }
  def testSNeq = {
    assertTrue(expecVal <= Operator.bopSNeq(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopSNeq(leftVal, rightVal) <= expecVal)
  }
  def testLess = {
    assertTrue(expecVal <= Operator.bopLess(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopLess(leftVal, rightVal) <= expecVal)
  }
  def testGreater = {
    assertTrue(expecVal <= Operator.bopGreater(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopGreater(leftVal, rightVal) <= expecVal)
  }
  def testLessEq = {
    assertTrue(expecVal <= Operator.bopLessEq(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopLessEq(leftVal, rightVal) <= expecVal)
  }
  def testGreaterEq = {
    assertTrue(expecVal <= Operator.bopGreaterEq(leftVal, rightVal))
    if (equal) assertTrue(Operator.bopGreaterEq(leftVal, rightVal) <= expecVal)
  }
}

class UnaTest(name:String, oprnd:List[Any], expec:List[Any], equal:Boolean, func:String) extends OperatorTestCase(func) {
  var oprndVal:Value = ValueBot
  var expecVal:Value = ValueBot
  
  def unaTest() {}
  override def getName = name

  override def setUp = {
    oprndVal = super.toValue(oprnd)
    expecVal = super.toValue(expec)
  }
  def testVoid = {
    assertTrue(expecVal <= Operator.uVoid(oprndVal))
    if (equal) assertTrue(Operator.uVoid(oprndVal) <= expecVal)
  }
  def testPlus = {
    assertTrue(expecVal <= Operator.uopPlus(oprndVal))
    if (equal) assertTrue(Operator.uopPlus(oprndVal) <= expecVal)
  }
  def testMinus = {
    assertTrue(expecVal <= Operator.uopMinus(oprndVal))
    if (equal) assertTrue(Operator.uopMinus(oprndVal) <= expecVal)
  }
  def testBitNeg = {
    assertTrue(expecVal <= Operator.uopBitNeg(oprndVal))
    if (equal) assertTrue(Operator.uopBitNeg(oprndVal) <= expecVal)
  }
  def testNeg = {
    assertTrue(expecVal <= Operator.uopNeg(oprndVal))
    if (equal) assertTrue(Operator.uopNeg(oprndVal) <= expecVal)
  }
}

abstract class TypeOperator
/* Binary */
case class BinBitOr(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinBitAnd(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinBitXor(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinLShift(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinRShift(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinURShift(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinPlus(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinMinus(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinMul(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinDiv(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinMod(name:String, lhs:List[Any], rhs:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class BinEq(name:String, lhs:List[Any], rhs:List[Any], expec:List[Boolean], equal:Boolean) extends TypeOperator
case class BinNeq(name:String, lhs:List[Any], rhs:List[Any], expec:List[Boolean], equal:Boolean) extends TypeOperator
case class BinSEq(name:String, lhs:List[Any], rhs:List[Any], expec:List[Boolean], equal:Boolean) extends TypeOperator
case class BinSNeq(name:String, lhs:List[Any], rhs:List[Any], expec:List[Boolean], equal:Boolean) extends TypeOperator
case class BinLess(name:String, lhs:List[Any], rhs:List[Any], expec:List[Boolean], equal:Boolean) extends TypeOperator
case class BinGreater(name:String, lhs:List[Any], rhs:List[Any], expec:List[Boolean], equal:Boolean) extends TypeOperator
case class BinLessEq(name:String, lhs:List[Any], rhs:List[Any], expec:List[Boolean], equal:Boolean) extends TypeOperator
case class BinGreaterEq(name:String, lhs:List[Any], rhs:List[Any], expec:List[Boolean], equal:Boolean) extends TypeOperator
/* Unary */
case class UnaVoid(name:String, oprn:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
//case class UnaTypeof(name:String, oprn:List[Any], expec:List[String], equal:Boolean) extends TypeOperator
case class UnaPlus(name:String, oprn:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class UnaMinus(name:String, oprn:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class UnaBitNeg(name:String, oprn:List[Any], expec:List[Any], equal:Boolean) extends TypeOperator
case class UnaNeg(name:String, oprn:List[Any], expec:List[Boolean], equal:Boolean) extends TypeOperator
