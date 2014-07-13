/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
*******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import _root_.java.io.BufferedWriter
import _root_.java.io.IOException
import _root_.java.util.HashMap
import _root_.java.util.{List => JList}

import kr.ac.kaist.jsaf.exceptions.JSAFError.error
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeFactory => NF, NodeUtil => NU}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._

object TSUtil {
  /*
   * 3.7.2.2 Parameter List
   * A signature’s parameter list consists of zero or more required parameters,
   * followed by zero or more optional parameters, finally followed by an optional
   * rest parameter.
   */
  def checkUnique(ids: List[Id]) = ids.toSet.size == ids.size

  def isArrayType(t: Type) = t match {
    case _:ArrayType => true
    case _ => false
  }

  def checkParams(writer: BufferedWriter, ps: JList[Param]) = {
    var seenOptional: Boolean = false
    var seenRest: Boolean = false
    val params = toList(ps)
    // Parameter names must be unique.
    // A compile-time error occurs if two or more parameters have the same name.
    checkUnique(params.map(_.getName))
    params.foreach(p => if (p.getTyp.isSome) { // optional parameter
                          seenOptional = true
                          if (seenRest)
                            NU.log(writer, NU.getSpan(p),
                                   "Rest parameters should come after optional parameters.")
                          else if (p.isRest) {
                            // A type annotation for a rest parameter must denote an array type.
                            if (p.getTyp.isSome && !isArrayType(p.getTyp.unwrap))
                              NU.log(writer, NU.getSpan(p),
                                    "Rest parameter should have an array type.");
                            seenRest = true
                          } else { // required parameter
                            if (seenOptional || seenRest)
                              NU.log(writer, NU.getSpan(p),
                                     "Optional parameters and rest parameters should come after required parameters.");
            }
        })
  }

  /*
A parameter is permitted to include a public or private modifier only if it occurs in the parameter list of a ConstructorImplementation (section 8.3.1).

When a parameter type annotation specifies a string literal type, the containing signature is a specialized signature (section 3.7.2.4). Specialized signatures are not permitted in conjunction with a function body, i.e. the FunctionExpression, FunctionImplementation, MemberFunctionImplementation, and ConstructorImplementation grammar productions do not permit parameters with string literal types.

A parameter can be marked optional by following its name with a question mark (?) or by including an initializer. The form that includes an initializer is permitted only in conjunction with a function body, i.e. only in a FunctionExpression, FunctionImplementation, MemberFunctionImplementation, or ConstructorImplementation grammar production.   
   */
}
