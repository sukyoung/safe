/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.visualization

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.Typing
import kr.ac.kaist.jsaf.analysis.cfg._
import java.util.{HashMap=>JHashMap}
import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import java.io.File
import java.io.IOException
import kr.ac.kaist.jsaf.useful.Useful
import edu.rice.cs.plt.tuple.Option
import scala.io._
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer



class Visualization(typing:Typing, in:String, out:Option[String], inCFG:CFG = null) {
  var outpath:String = "";
  var filename = ""
  var resultpath = ""
  val cfg = if (inCFG != null) inCFG else typing.cfg

  def run(fromCFG: Boolean) {
    filename = (new File(in)).getName.split(".js")(0)
    out.isSome match {
      case true =>
        outpath = out.unwrap
        resultpath = out.unwrap + "/result"
      case false =>
        outpath = filename
        resultpath = filename + "/result"
    }
    
    var f = new File(resultpath)
    if (!f.exists) {
      f.mkdirs
    } else {
      System.out.println(resultpath + " exist. overrite it.");
      f.listFiles.foldLeft[String]("")((s, f) => {
        f.delete
        s + f.getName() + ","
      })
	}
    drawGraph(resultpath, !fromCFG)
    if (fromCFG) {
      dumpSourceInfo
      dumpFuncInfo
      runScript
    }
  }
  
  def drawGraph(path:String, onlyGraph:Boolean) {
    if (onlyGraph) {
      FunCFGWriter.write(cfg, cfg.getFunctionIds.toList, cfg.getNodes, path, "dot")
    } else {
      FunCFGWriter.write(cfg, getFuncsId, typing.programNodes, path, "dot")
      CGWriter.write(cfg, getCallgraph, path+"/callgraph.dot", path+"/callgraph.svg", "dot")
    }
  }
  
  
  def dumpSourceInfo() {
    try {
      var contents = new StringBuilder
      //Source.fromFile(fileMap.get(in).split("::")(0)).getLines.foreach((line) => {
      //  contents.append("\"").append(line.replaceAll("\"", "\\\\\"").replaceAll("\t", "        ")).append("\\n\" +\n")
      //})
      contents.append("\"").append("\";")
      
      val sb = new StringBuilder
      sb.append("var filename = \"").append(in).append("\";\n\n")
      sb.append("var file_contents = ").append(contents.toString).append(";\n\n")
      sb.append("var fid2name = ").append(getFuncsName).append(";\n\n")
      sb.append("var ir = ").append(dumpIR).append(";\n\n")
      val pair = Useful.filenameToBufferedWriter(resultpath+"/data.js")
      val fw = pair.first
      val writer = pair.second
      System.out.println("writing in a " + resultpath+"/data.js" + " file")
      writer.write(sb.toString)
      writer.close
      fw.close
    } catch {
      case e:IOException =>
        throw new IOException("IOException " + e + "while writing " + resultpath);
    }
  }
  
  def dumpFuncInfo() {
    val it = getFuncsId().iterator
    while(it.hasNext) {
      dumpHeap(it.next.toInt)		// write heap dump to result/f#.js
    }
  }
  
  def dumpHeap(fid:Int) {
    val pair = Useful.filenameToBufferedWriter(resultpath+"/f"+fid+".js")
    val fw = pair.first
    val bw = pair.second
    val sb = new StringBuilder
    var names:List[String] = List()
    
    System.out.println("writing in a " + resultpath+"/f"+fid+".js" + " file");
    bw.write("var fid = " + fid + ";\n\n");
    for (node <- cfg.getNodes) {
      node match {
        case (n, _) if n==fid =>
          var callStates:List[JValue] = List()
          val nodeStr = node match {
	  	    case (_, LBlock(id)) =>  "Block"+id
	  	    case (_, LEntry) =>  "Entry"+fid
	  	    case (_, LExit) =>  "Exit"+fid
	  	    case (_, LExitExc) =>  "ExitExc"+fid
	  	  }
          names ::= nodeStr

          typing.inTable.get(node) match {
            case None =>
              if (!(typing.fset_builtin.contains(node._1))) {
                bw.write("var " + nodeStr + " = " + compact(render(("name"->nodeStr)~("state"->"Bottom (cc:ALL)"))) + ";\n\n")
              }
            case Some(map) =>
              map.foreach(kv => {
                var csMap:MHashMap[String, JValue] = MHashMap()
                val state = kv._2

                // normal state
                if (state != StateBot) {
                  csMap.update("heap",DomainPrinter.printHeap(state._1, cfg))
                  csMap.update("context",DomainPrinter.printContext(state._2))
                } else {
                  csMap.update("state", "Bot")
                }
                csMap.update("cc", kv._1.toString)
                callStates ::= csMap.toSeq.sortBy(_._1)
              })
            case _ =>
          }

          if (!callStates.isEmpty)
            bw.write("var " + nodeStr + " = " + pretty(render(("name"->nodeStr)~("state"->callStates))) + ";\n\n")
        case _ =>
      }
    }
    bw.write("var names = " + compact(render(names)) + ";\n\n")
    bw.close
    fw.close
  }
  
  def dumpIR():String = {
    val infoInst = cfg.getNodes.foldLeft[MHashMap[String, List[Map[String,String]]]](MHashMap())((m, node) => {
      cfg.getCmd(node) match {
        case Block(insts) =>
          val nodeId = node._2 match {
            case LBlock(id) =>  id.toString
            case _ => "-1"
          }
          val inner = insts.foldLeft[MHashMap[String, List[Map[String,String]]]](MHashMap())((_m, inst) => {
            inst.getInfo match {
              case Some(info) =>
                var b = info.getSpan.getBegin.at.split(".js:")
                var e = info.getSpan.getEnd.at.split(".js:")
                if (b.length <= 1 || b.length <= 1){
                  b = info.getSpan.getBegin.at.split(":")
                  e = info.getSpan.getEnd.at.split(":")
                }
                val span = b(1).split("\\.")(0)+":"+b(1).split("\\.")(1)+"-"+e(1).split("\\.")(0)+":"+e(1).split("\\.")(1)
                val key = b(1).split("\\.")(0)
                val values:List[Map[String,String]] = _m.get(key) match {
                  case Some(list:List[Map[String,String]]) =>  list ::: List(Map("span"->span,"iid"->inst.getInstId.toString,"fid"->node._1.toString,"nodeid"->nodeId,"str"->inst.toString))
                  case None =>  List(Map("span"->span,"iid"->inst.getInstId.toString,"fid"->node._1.toString,"nodeid"->nodeId,"str"->inst.toString))
                }
                _m ++ MHashMap(key->values)
              case None => _m
            }
          })
          m ++ inner.map{ case (k,v) => k -> (v ++ m.getOrElse(k,List()))}
        case _ =>  m
      }
    })
    val filled = infoInst.toSeq.sortBy(_._1.toInt).foldLeft[MHashMap[String,List[Map[String,String]]]](MHashMap())((m, kv) =>{
      val buf:MHashMap[String, List[Map[String,String]]] = MHashMap()
      while (kv._1.toInt > m.size+buf.size) {
        buf.update((m.size+buf.size).toString,List(Map("span"->"Nothing")))
      }
      m ++ (MHashMap(kv._1->kv._2) ++ buf)
    })
    pretty(render(filled.toSeq.sortBy(_._1.toInt)))
  }
  
  def dumpInstLoc():String = {
    var m:MHashMap[String,Set[String]] = MHashMap()

    for (node <- cfg.getNodes) {
      cfg.getCmd(node) match {
            case Block(insts) =>
              for (inst <- insts) {
                inst.getInfo match {
                  case Some(info) =>
                    var line = info.getSpan.getBegin.getLine.toString
                    var str = inst.toString
                    m.get(line) match {
                      case Some(s) =>  s ++ str
                      case None =>  m += (line->Set(str))
                    }
                  case _ =>	;
                }
              }
            case _ =>	;
       }
    }
    pretty(render(m))
  }
  
  // without non-callee built-ins
  def getFuncsId():List[Int] = {
    val callgraph = getCallgraph
    callgraph.foldLeft[List[FunctionId]](List())((l, kv) => {
      l.contains(kv._1) match {
        case true =>
          kv._2.foldLeft[List[FunctionId]](List())((_l, fid) => {
            l.contains(fid) match {
              case true =>  _l
              case false => fid :: _l
            }
          }) ++ l
        case false =>
          kv._2.foldLeft[List[FunctionId]](List())((_l, fid) => {
            l.contains(fid) match {
              case true =>  _l
              case false => fid :: _l
            }
          }) ++ List(kv._1) ++ l
      }
    })
  }
  
  def getFuncsName():String = {
    val fids = getFuncsId
    val fidname:List[JValue] =
      cfg.getFunctionIds.foldLeft[List[JValue]](List())((l, fid) => {
        fids.contains(fid) match {
          case true =>  (("fid"->fid)~("name"->cfg.getFuncName(fid))) :: l
          case false =>  l
        }
      })
    pretty(render(fidname))
  }

  def getCallgraph():Map[FunctionId, Set[FunctionId]] = {
	val cg = typing.computeCallGraph
    cg.foldLeft[Map[FunctionId, Set[FunctionId]]](Map())((m, kv) => {
        val caller = cfg.findEnclosingNode(kv._1)._1
        m.get(caller) match {
          case Some(callees) => m + (caller -> (kv._2 ++ callees))
          case None =>	m + (caller -> kv._2)
        }
      })
  }

  def runScript() {
	try {
	  var osName = System.getProperty("os.name")
	  var p:Process = null
	  if (osName.indexOf("Windows") > -1) {
	    val cmdarray:Array[String] = Array("cmd.exe", "/y", "/c", "bash jsav "+outpath)
	    p = Runtime.getRuntime().exec(cmdarray)
	  } else {
	    val cmdarray = Array("/bin/sh", "-c", "jsav "+outpath)
	    p = Runtime.getRuntime().exec(cmdarray)
	  }
	  p.waitFor
	} catch {
	  case e:Exception =>  e.printStackTrace
	}
    
  }
}
