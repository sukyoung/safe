/*******************************************************************************
    Copyright (c) 2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.ts

import scala.collection.mutable.{ HashMap => MHashMap, Map => MMap, ListBuffer => MListBuffer }
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{ NodeFactory => NF, Span, SourceLocRats , ASTIO}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import java.util.{List => JList}

object TSTypeMap extends Walker {
  ////////////////////////////////////////////////////////////////////////////////
  // Database
  ////////////////////////////////////////////////////////////////////////////////
  var db: Program = null
  var vardb: MMap[String, Type] = MHashMap[String, Type]()

  ////////////////////////////////////////////////////////////////////////////////
  // Path
  ////////////////////////////////////////////////////////////////////////////////
  var modPath: List[String] = List[String]()

  ////////////////////////////////////////////////////////////////////////////////
  // TS Nodes
  ////////////////////////////////////////////////////////////////////////////////
  var intfMap: MMap[String, IntfDecl] = MHashMap[String, IntfDecl]()
  var callMap: MMap[String, List[CallSig]] = MHashMap[String, List[CallSig]]()
  var idxMap: MMap[String, IndexSig] = MHashMap[String, IndexSig]()
  var objMap: MMap[String, ObjectType] = MHashMap[String, ObjectType]()
  var clsMap: MMap[String, AmbClsDecl] = MHashMap[String, AmbClsDecl]()
  var enumMap: MMap[String, AmbEnumDecl] = MHashMap[String, AmbEnumDecl]()
  var globalFunMap: MMap[String, List[Type]] = MHashMap[String, List[Type]]()
  var intfFunMap: MMap[String, List[Type]] = MHashMap[String, List[Type]]()
  var arrayMap: MMap[String, ArrayType] = MHashMap[String, ArrayType]()

  ////////////////////////////////////////////////////////////////////////////////
  // Function Argument Size
  ////////////////////////////////////////////////////////////////////////////////
  def initAll() = {
    // Initialize variables
    db = null
    vardb = MHashMap[String, Type]()
    modPath = List[String]()

    intfMap = MHashMap[String, IntfDecl]()
    callMap = MHashMap[String, List[CallSig]]()
    idxMap = MHashMap[String, IndexSig]()
    objMap = MHashMap[String, ObjectType]()
    clsMap = MHashMap[String, AmbClsDecl]()
    enumMap = MHashMap[String, AmbEnumDecl]()
    globalFunMap = MHashMap[String, List[Type]]()
    intfFunMap = MHashMap[String, List[Type]]()
    arrayMap = MHashMap[String, ArrayType]()
  }

  def setLibrary(library: String): Unit = {
    // Reset
    initAll()
    // Read libraries (databases)
    var result = ASTIO.readJavaAst(library)
    if (!result.isNone) db = result.unwrap

    // Collect some nodes
    walkTS()
  }

  def toPath(_modPath : List[String], name : String): String = {
    var x:String = null
    var result = ""
    for (x <- _modPath.reverse)
      result += x + "."
    result += name
    result
  }

  def walkTS(): Unit = {
    object tsWalker extends Walker {
      override def walkUnit(node: Any): Unit = {
        var isMod = false
        node match {
          case node@SAmbVarDecl(info, id, ty) => 
            ty match {
              case Some(t) => {
                val name: String = id.getText
                t match {
                  case SFunctionType(_, _, _, _) => {
                    globalFunMap.get(toPath(modPath, name)) match {
                      case Some(funList) =>
                        globalFunMap.put(toPath(modPath, name), funList:+t)
                      case None =>
                        globalFunMap.put(toPath(modPath, name), List[Type](t))
                    }
                  }
                  case t@SArrayType(_, _) => {
                    arrayMap.put(toPath(modPath, name), t)
                  }
                  case t@SObjectType(_, _) =>{
                    objMap.put(toPath(modPath, name), t)
                  }
                  case _ => vardb.put(toPath(modPath, name), t)
                }
              }
              case None =>
            }
          case node@SAmbFunDecl(info, id, sig) =>
            val name:String = id.getText
            globalFunMap.get(toPath(modPath, name)) match {
              case Some(funList) =>
                globalFunMap.put(toPath(modPath, name), funList:+sig)
              case None =>
                globalFunMap.put(toPath(modPath, name), List[Type](sig))
            }
          case node@SIntfDecl(info, id, tps, ext, ty) => {
            val intfPath: String = toPath(modPath, id.getText)
            intfMap.put(intfPath, node)
            toList(ty.getMembers).foreach(mem => mem match {
              case SPropertySig (info, prop, opt, typ) =>{
                val name: String = prop match {
                  case SPropId(info,id) => id.getText
                }
                typ match {
                  case Some(t) => t match {
                    case SFunctionType(_, _, _, _) =>{
                      intfFunMap.get(intfPath+"."+name) match {
                        case Some(funList) =>
                          intfFunMap.put(intfPath+"."+name, funList:+t)
                        case None =>
                          intfFunMap.put(intfPath+"."+name, List[Type](t))
                      }
                    }
                    case t@SArrayType(_, _) => {
                      arrayMap.put(intfPath+"."+name, t)
                    }
                    case t@SObjectType(_, _) =>{
                      objMap.put(intfPath+"."+name, t)
                    }
                    case _ =>
                  }
                  case None =>
                }
              }
              case SMethodSig(info, prop, opt, sig) => {
                val name: String = prop match {
                  case SPropId(info,id) => id.getText
                }
                intfFunMap.get(intfPath+"."+name) match {
                  case Some(funList) =>
                    intfFunMap.put(intfPath+"."+name, funList:+sig)
                  case None =>
                    intfFunMap.put(intfPath+"."+name, List[Type](sig))
                }
              }
              case mem@SCallSig(info, tparams, params, typ) =>{
                callMap.get(intfPath) match {
                  case Some(callList) =>
                    callMap.put(intfPath, callList:+mem)
                  case None =>
                    callMap.put(intfPath, List[CallSig](mem))
                }
              }
              case mem@SIndexSig(info, id, annot, num) =>{
                idxMap.put(intfPath, mem)
              }
              case _ =>
            })
          }
          case node@SAmbClsDecl(info, id, tps, ext, imp, elts) =>
            clsMap.put(toPath(modPath, id.getText), node)
          case node@SAmbEnumDecl(info, id, mem) =>
            enumMap.put(toPath(modPath, id.getText), node)
          case node@SAmbModDecl(info, path, mem) =>
            var name = toList(path.getNames).map(n => n.getText).mkString(".")
            modPath ::= name
            isMod = true
          case _ =>
        }
        super.walkUnit(node)
        if (isMod)
          modPath = modPath.tail
      }
    }
    tsWalker.walkUnit(db)
  }
}
