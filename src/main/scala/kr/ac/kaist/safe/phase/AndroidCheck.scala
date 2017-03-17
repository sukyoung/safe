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

package kr.ac.kaist.safe.phase

import scala.io.Source
import scala.util.{ Try, Failure }
import kr.ac.kaist.safe.{ CommandObj, CmdASTRewrite }
import kr.ac.kaist.safe.Safe
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.android_checker._
import kr.ac.kaist.safe.errors.error.NoFileError
import kr.ac.kaist.safe.util._
import spray.json._
import spray.json.DefaultJsonProtocol._

// Type check an Android hybrid application
case object AndroidCheck extends PhaseObj[Unit, AndroidCheckConfig, Unit] {
  val name: String = "androidChecker"
  val help: String =
    "Checks types of Android hybrid applications"

  def apply(
    unit: Unit,
    safeConfig: SafeConfig,
    config: AndroidCheckConfig
  ): Try[Unit] = safeConfig.fileNames match {
    case Nil => Failure(NoFileError("androidCheck"))
    case _ =>
      var startTime = System.currentTimeMillis

      // Preprocess the Info. from HybriDoid 
      implicit object hybriDroidFormat extends JsonFormat[HybriDroid] {
        def write(obj: HybriDroid): JsValue = {
          val map1 = JsObject(obj.js2bridge.mapValues {
            (m: Map[String, List[String]]) =>
              JsObject(m.mapValues { (l: List[String]) => JsArray(l.map(JsString(_)).toVector) })
          })
          val map2 = JsObject(obj.classes.mapValues {
            (l: List[HybridMethod]) => JsArray(l.map(hybridMethodFormat.write).toVector)
          })
          JsObject(("js2bridge", map1), ("classes", map2))
        }
        def read(json: JsValue): HybriDroid = json match {
          case JsObject(fields) =>
            (fields.get("js2bridge"), fields.get("classes")) match {
              case (Some(JsObject(js1)), Some(JsObject(js2))) =>
                val map1: Map[String, Map[String, List[String]]] = js1.mapValues {
                  case JsObject(m) =>
                    m.mapValues {
                      case JsArray(vectors) =>
                        vectors.toList.map {
                          case JsString(str) => str
                          case _ => deserializationError("HybriDroid expected")
                        }
                      case _ => deserializationError("HybriDroid expected")
                    }
                  case _ => deserializationError("HybriDroid expected")
                }
                val map2: Map[String, List[HybridMethod]] = js2.mapValues {
                  case JsArray(vectors) => vectors.toList.map(hybridMethodFormat.read)
                  case _ => deserializationError("HybriDroid expected")
                }
                new HybriDroid(map1, map2)
              case _ => deserializationError("HybriDroid expected")
            }
          case _ => deserializationError("HybriDroid expected")
        }
      }

      implicit object hybridMethodFormat extends JsonFormat[HybridMethod] {
        def write(obj: HybridMethod): JsValue =
          JsObject(
            ("name", JsString(obj.name)),
            ("params", JsArray(obj.params.map(JsString(_)).toVector)),
            ("result", JsString(obj.result))
          )
        def read(json: JsValue): HybridMethod = json match {
          case JsObject(fields) =>
            (fields.get("name"), fields.get("params"), fields.get("result")) match {
              case (Some(JsString(name)), Some(JsArray(vectors)), Some(JsString(result))) =>
                new HybridMethod(
                  name,
                  vectors.toList.map {
                    case JsString(str) => str
                    case _ => deserializationError("HybridMethod expected")
                  },
                  result
                )
              case _ => deserializationError("HybridMethod expected")
            }
          case _ => deserializationError("HybridMethod expected")
        }
      }

      val fileName = safeConfig.fileNames.head
      val hybridroid = Source.fromFile(fileName)("UTF-8").mkString.parseJson.convertTo[HybriDroid]
      var jvClasses = AndroidUtil.init
      hybridroid.classes.foreach {
        case (name, mths) => {
          val cls = new AndroidUtil.JvClass(name)
          mths.foreach(hm => cls.addJvMethod(hm.name, hm.params, hm.result))
          jvClasses = AndroidUtil.addClass(name, cls, jvClasses)
        }
      }
      var preprocessTime = System.currentTimeMillis - startTime
      var checkTime = 0.toLong

      val parser = new ArgParser(safeConfig.command, safeConfig)
      hybridroid.js2bridge.foreach {
        case (name, bridge) => {
          startTime = System.currentTimeMillis
          CmdToIR(List("-silent", name), false).map {
            case ir: IRRoot =>
              val compileTime = System.currentTimeMillis
              preprocessTime += compileTime - startTime
              new AndroidTypeSystem(name, ir, bridge, jvClasses, config.debug).check
              checkTime += System.currentTimeMillis() - compileTime
            case _ =>
          }
        }
      }

      println(s"Preprocess time: $preprocessTime (ms)")
      Try(println(s"Type-check time: $checkTime (ms)"))
  }

  def defaultConfig: AndroidCheckConfig = AndroidCheckConfig()
  val options: List[PhaseOption[AndroidCheckConfig]] = List(
    ("debug", BoolOption(c => c.debug = true),
      "messages during compilation are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the results of type checking will be written to the outfile.")
  )
}

// Bridge information from HybriDroid
case class HybriDroid(
  val js2bridge: Map[String, Map[String, List[String]]],
  val classes: Map[String, List[HybridMethod]]
) extends DefaultJsonProtocol

// Bridge method
case class HybridMethod(
  val name: String,
  val params: List[String],
  val result: String
) extends DefaultJsonProtocol

case object CmdToIR extends CommandObj("toIR", CmdASTRewrite >> Compile)

// AndroidCheck phase config
case class AndroidCheckConfig(
  var debug: Boolean = false,
  var outFile: Option[String] = None
) extends Config
