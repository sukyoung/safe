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

package kr.ac.kaist.safe.analyzer.models.builtin

import kr.ac.kaist.safe.analyzer.{ TypeConversionHelper, Helper }
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.SystemAddr

// 15.11 Error Objects
object BuiltinError extends FuncModel(
  name = "Error",
  // 15.11.1 Error(...)
  code = BasicCode(
    1,
    BuiltinErrorHelper.construct("Error", BuiltinErrorProto.loc)
  ),
  // 15.11.2 new Error(...)
  construct = Some(BasicCode(
    1,
    BuiltinErrorHelper.construct("Error", BuiltinErrorProto.loc)
  )),
  // 15.11.3.1 Error.prototype
  protoModel = Some((BuiltinErrorProto, F, F, F)),
  props = List(
    InternalProp(IPrototype, BuiltinFunctionProto)
  )
)

// 15.11.4. Properties of the Error Prototype Object
object BuiltinErrorProto extends ObjModel(
  name = "Error.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    // 15.11.4.2 Error.prototype.name
    NormalProp("name", PrimModel("Error"), T, F, T),
    // 15.11.4.3 Error.prototype.message
    NormalProp("message", PrimModel(""), T, F, T),

    // 15.11.4.4 Error.prototype.toString()
    NormalProp("toString", FuncModel(
      name = "Error.prototype.toString",
      code = BasicCode(argLen = 0, BuiltinErrorHelper.toString)
    ), T, F, T)
  )
)

private object BuiltinErrorHelper {
  // 15.11.1.1, 15.11.2.1
  def construct(errorName: String, protoLoc: Loc)(args: AbsValue, st: State): (State, State, AbsValue) = {
    val message = Helper.propLoad(args, Set(AbsString("0")), st.heap)
    val defaultError = AbsObjectUtil.Empty
      .update(IClass, InternalValueUtil(AbsString(errorName)))
      .update(IPrototype, InternalValueUtil(protoLoc))
      .update(IExtensible, InternalValueUtil(AbsBool.True))

    val undefObject =
      if (message.pvalue.undefval </ AbsUndef.Bot) defaultError
      else AbsObjectUtil.Bot

    val notUndefObject =
      if (message </ AbsUndef.Top) {
        val msg = TypeConversionHelper.ToString(message)
        defaultError.update("message", AbsDataProp(msg))
      } else AbsObjectUtil.Bot

    val errorObj = undefObject + notUndefObject

    val errorAddr = SystemAddr(errorName + "<instance>")
    val st1 = st.oldify(errorAddr)
    val errorLoc = Loc(errorAddr, Recent)
    val h2 = st1.heap.update(errorLoc, errorObj)

    (State(h2, st1.context), State.Bot, AbsValue(errorLoc))
  }

  def toString(args: AbsValue, st: State): (State, State, AbsValue) = {
    val thisBinding = st.context.thisBinding
    // 2. If Type(O) is not Object, throw a TypeError exception.
    val excSet =
      if (thisBinding.isBottom) ExcSetEmpty + TypeError
      else ExcSetEmpty

    // 3. - 10.
    val result = thisBinding.foldLeft(AbsString.Bot)((res, loc) => {
      val O = st.heap.get(loc)
      val name3 = O.Get("name", st.heap)
      val nameUndef =
        if (name3.pvalue.undefval </ AbsUndef.Top) AbsString("Error")
        else AbsString.Bot
      val nameNotUndef =
        if (name3 </ AbsUndef.Top) TypeConversionHelper.ToString(name3)
        else AbsString.Bot
      val name4 = nameUndef + nameNotUndef
      val msg5 = O.Get("message", st.heap)
      val msgUndef =
        if (msg5.pvalue.undefval </ AbsUndef.Top) AbsString("")
        else AbsString.Bot
      val msgNotUndef =
        if (msg5 </ AbsUndef.Top) TypeConversionHelper.ToString(msg5)
        else AbsString.Bot
      val msg6 = msgUndef + msgNotUndef

      val emptyString = AbsString("")
      val res8 =
        if (AbsBool.True <= (name4 === emptyString)) msg6
        else AbsString.Bot
      val res9 =
        if ((AbsBool.False <= (name4 === emptyString))
          && (AbsBool.True <= (msg6 === emptyString))) name4
        else AbsString.Bot
      val res10 =
        if ((AbsBool.False <= (name4 === emptyString))
          && (AbsBool.False <= (msg6 === emptyString))) name4.concat(AbsString(": ")).concat(msg6)
        else AbsString.Bot
      res + (res8 + res9 + res10)
    })
    (st, st.raiseException(excSet), result)
  }
}

////////////////////////////////////////////////////////////////////////////////
// Native Errors
////////////////////////////////////////////////////////////////////////////////

// 15.11.6.1 EvalError
object BuiltinEvalError extends FuncModel(
  name = "EvalError",
  // @function
  code = BasicCode(
    1,
    BuiltinErrorHelper.construct("EvalError", BuiltinEvalErrorProto.loc)
  ),
  props = List(
    NormalProp("name", PrimModel("EvalError"), T, F, T)
  ),
  // @construct
  construct = Some(BasicCode(
    1,
    BuiltinErrorHelper.construct("EvalError", BuiltinEvalErrorProto.loc)
  )),
  protoModel = Some((BuiltinEvalErrorProto, F, F, F))
)

object BuiltinEvalErrorProto extends ObjModel(
  name = "EvalError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("EvalError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// RangeError
object BuiltinRangeError extends FuncModel(
  name = "RangeError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("RangeError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinRangeErrorProto, F, F, F))
)

object BuiltinRangeErrorProto extends ObjModel(
  name = "RangeError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("RangeError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// ReferenceError
object BuiltinReferenceError extends FuncModel(
  name = "ReferenceError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("ReferenceError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinReferenceErrorProto, F, F, F))
)

object BuiltinReferenceErrorProto extends ObjModel(
  name = "ReferenceError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("ReferenceError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// SyntaxError
object BuiltinSyntaxError extends FuncModel(
  name = "SyntaxError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("SyntaxError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinSyntaxErrorProto, F, F, F))
)

object BuiltinSyntaxErrorProto extends ObjModel(
  name = "SyntaxError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("SyntaxError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// TypeError
object BuiltinTypeError extends FuncModel(
  name = "TypeError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("TypeError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinTypeErrorProto, F, F, F))
)

object BuiltinTypeErrorProto extends ObjModel(
  name = "TypeError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("TypeError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// URIError
object BuiltinURIError extends FuncModel(
  name = "URIError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("URIError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinURIErrorProto, F, F, F))
)

object BuiltinURIErrorProto extends ObjModel(
  name = "URIError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("URIError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)
