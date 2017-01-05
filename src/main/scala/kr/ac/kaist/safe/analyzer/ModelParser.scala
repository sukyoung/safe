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

// Rename Success and Failure to avoid name conflicts with ParseResult
import kr.ac.kaist.safe.errors.error.ModelParseError
import kr.ac.kaist.safe.analyzer.domain._
import scala.collection.immutable.HashMap
import scala.util.{ Try, Success => Succ, Failure => Fail }
import scala.util.parsing.combinator._

// Argument parser by using Scala RegexParsers.
object ModelParser extends RegexParsers with JavaTokenParsers {
  def apply(model: String): ParseResult[Heap] = parse(jsHeap, model)

  // repeat rules
  def emptyList[T]: Parser[List[T]] = success(Nil)
  def repsepE[T](p: => Parser[T], sep: String): Parser[List[T]] =
    p ~! (("," ~> repsepE(p, sep)) | emptyList) ^^ { case x ~ xs => x :: xs } | emptyList

  // primitive parser
  lazy val num: Parser[Double] = floatingPointNumber ^^ { _.toDouble }
  lazy val int: Parser[Int] = wholeNumber ^^ { _.toInt }
  lazy val str: Parser[String] = stringLiteral
  lazy val t: Parser[Boolean] = "true" ^^^ { true }
  lazy val f: Parser[Boolean] = "false" ^^^ { false }
  lazy val bool: Parser[Boolean] = t | f
  lazy val T: Parser[Boolean] = "T" ^^^ { true }
  lazy val F: Parser[Boolean] = "F" ^^^ { false }
  lazy val shortBool: Parser[Boolean] = T | F

  // JavaScript primitive value
  lazy val jsNum: Parser[Num] = num ^^ { Num(_) }
  lazy val jsStr: Parser[Str] = str ^^ { Str(_) }
  lazy val jsNull: Parser[Null] = "null" ^^^ { Null }
  lazy val jsBool: Parser[Bool] = bool ^^ { Bool(_) }
  lazy val jsShortBool: Parser[Bool] = shortBool ^^ { Bool(_) }
  lazy val jsShortBoolE: Parser[Bool] =
    jsShortBool | "" ~> failure("illegal start of boolean(T/F)")
  lazy val jsUndef: Parser[Undef] = "undefined" ^^^ { Undef }
  lazy val jsPValue: Parser[PValue] = jsNum | jsStr | jsNull | jsBool | jsUndef

  // JavaScript value
  lazy val jsLoc: Parser[Loc] = "#" ~> "[0-9a-zA-Z.<>]+".r ^^ { SystemLoc(_, Recent) }
  lazy val jsValue: Parser[Value] = jsPValue | jsLoc
  lazy val jsValueE: Parser[Value] = jsValue | failure("illegal start of value")

  // JavaScript data property
  lazy val jsDataProp: Parser[DataProp] = "<" ~> (
    jsValueE ~
    ("," ~> jsShortBoolE) ~
    ("," ~> jsShortBoolE) ~
    ("," ~> jsShortBoolE)
  ) <~ ">" ^^ {
      case value ~ writable ~ enumerable ~ configurable =>
        DataProp(value, writable, enumerable, configurable)
    }

  // JavaScript internal property
  lazy val jsIPrototype = "[[Prototype]]" ^^^ { IPrototype }
  lazy val jsIClass = "[[Class]]" ^^^ { IClass }
  lazy val jsIExtensible = "[[Extensible]]" ^^^ { IExtensible }
  lazy val jsIPrimitiveValue = "[[PrimitiveValue]]" ^^^ { IPrimitiveValue }
  lazy val jsICall = "[[Call]]" ^^^ { ICall }
  lazy val jsIConstruct = "[[Construct]]" ^^^ { IConstruct }
  lazy val jsIScope = "[[Scope]]" ^^^ { IScope }
  lazy val jsIHasInstance = "[[HasInstance]]" ^^^ { IHasInstance }
  lazy val jsIName: Parser[IName] = {
    jsIPrototype | jsIClass | jsIExtensible | jsIPrimitiveValue |
      jsICall | jsIConstruct | jsIScope | jsIHasInstance
  }
  lazy val jsFId: Parser[FId] = "fun(" ~> int <~ ")" ^^ { FId(_) }
  lazy val jsIValue: Parser[IValue] = jsValue | jsFId
  lazy val jsIValueE: Parser[IValue] =
    jsIValue | "" ~> failure("illegal start of IValue")

  // JavaScript object
  type PMap = Map[String, DataProp]
  type IMap = Map[IName, IValue]
  def jsObjMapTuple: Parser[(PMap, IMap)] = {
    lazy val empty: Parser[(PMap, IMap)] = success((HashMap(), HashMap()))
    lazy val jsMember = (str <~ ":") ~! jsDataProp ^^ { case n ~ d => (n, d) }
    lazy val jsIMember = (jsIName <~ ":") ~! jsIValueE ^^ { case n ~ v => (n, v) }
    lazy val next = ("," ~> jsObjMapTuple) | empty
    jsMember ~! next ^^
      { case (name, dp) ~ ((pmap, imap)) => (pmap + (name -> dp), imap) } |
      jsIMember ~! next ^^
      { case (iname, iv) ~ ((pmap, imap)) => (pmap, imap + (iname -> iv)) } |
      empty
  }
  lazy val jsObject: Parser[Object] = "{" ~> jsObjMapTuple <~ "}" ^^ {
    case (pmap, imap) => Object(pmap, imap)
  }

  // JavaScript Heap
  lazy val jsHeap: Parser[Heap] = "{" ~> (
    repsepE((jsLoc <~ ":") ~! jsObject, ",")
  ) <~ "}" ^^ {
      case lst => {
        val map = lst.foldLeft(HashMap[Loc, Object]()) {
          case (map, loc ~ obj) => {
            map + (loc -> obj)
          }
        }
        Heap(map)
      }
    }
}
