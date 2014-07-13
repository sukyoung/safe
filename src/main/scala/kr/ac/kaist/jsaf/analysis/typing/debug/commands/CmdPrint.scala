/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug.commands

import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsole
import kr.ac.kaist.jsaf.analysis.typing.domain.DomainPrinter
import kr.ac.kaist.jsaf.analysis.cfg.{CFGInst, LEntry, Block}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.analysis.typing.ControlPoint
import scala.collection.mutable.{HashMap => MHashMap, HashSet => MHashSet, Stack => MStack}

class CmdPrint extends Command {
  override val name = "print"
  override val info: String = "Print out various information."

  override def help(): Unit = {
    System.out.println("usage: print allstate ({keyword})")
    System.out.println("       print state ({keyword})")
    System.out.println("       print loc {LocName} ({keyword})")
    System.out.println("       print fid {functionID}")
    System.out.println("       print worklist")
    System.out.println("       print ipsucc")
    System.out.println("       print trace")
    System.out.println("       print (cmd|command)")
  }

  def grep(key: String, txt: String): String = {
    val list = txt.split("\n")
    list.foldLeft("")((txt, line) => {
      if (line.contains(key)) txt + line + "\n"
      else txt
    })
  }

  override def run(c: DebugConsole, args: Array[String]): Unit = {
    try {
      val subcmd = args(0)
      subcmd.toLowerCase match {
        case "allstate" => {
          val key =
            if (args.length > 1) Some(args(1))
            else None

          val inS = c.readTable(c.current)
          val heap_1 = DomainPrinter.printHeap(0, inS._1, c.getCFG, 3)
          val heap_2 = key match {
            case Some(k) => grep(k, heap_1)
            case None => heap_1
          }
          System.out.println(heap_2)
          System.out.println(DomainPrinter.printContext(0, inS._2))
        }
        case "state" => {
          val key =
            if (args.length > 1) Some(args(1))
            else None

          val inS = c.readTable(c.current)
          val heap_1 = DomainPrinter.printHeap(0, inS._1, c.getCFG)
          val heap_2 = key match {
            case Some(k) => grep(k, heap_1)
            case None => heap_1
          }
          System.out.println(heap_2)
          System.out.println(DomainPrinter.printContext(0, inS._2))
        }
        case "loc" if args.length > 1 => {
          val arg1 = args(1)
          val sloc = parseLocName(arg1)
          sloc match {
            case Some(loc) => {
              val key =
                if (args.length > 2) Some(args(2))
                else None

              val inS = c.readTable(c.current)
              val o = inS._1(loc)
              val name = DomainPrinter.printLoc(loc)
              val obj_1 = DomainPrinter.printObj(4+name.length, o)
              val obj_2 = key match {
                case Some(k) => grep(k, obj_1)
                case None => obj_1
              }
              System.out.println(name + " -> ")
              System.out.println(obj_2)
            }
            case None => {
              System.err.println("cannot find: "+arg1)
            }
          }
        }
        case "worklist" => {
          System.out.println("* Worklist set")
          System.out.print(c.getWorklist.toString)
        }
        case "ipsucc" => {
          System.out.println("* successor map")
          val succs = c.getSemantics.getIPSucc(c.current)

          System.out.println("- src: "+c.current.toString())
          succs match {
            case Some(m) => {
              m.foreach(f => {
                val cp_target = f._1
                val cp_context = f._2._1
                val cp_obj = f._2._2
                System.out.println("- dst: "+cp_target.toString()+", "+cp_context.toString)
              })
            }
            case None => System.out.println("- Nothing")
          }
        }
        case "trace" => {
          System.out.println("* Call-Context Trace")

          // predecessor map (reversed successor map)
          /*val ipPredMap = new MHashMap[ControlPoint, MHashSet[ControlPoint]]
          for(kv1 <- c.getSemantics.ipSuccMap) {
            for(kv2 <- kv1._2) {
              ipPredMap.getOrElseUpdate(kv2._1, new MHashSet).add(kv1._1)
            }
          }*/

          val traceStack = new MStack[(Int, ControlPoint, CFGInst)]()
          traceStack.push((0, c.current, c.getCFG.getFirstInst(c.current._1)))
          while (!traceStack.isEmpty) {
            val (currentLevel, (currentNode, currentCallContext), currentInst) = traceStack.pop

            // Instruction info
            val source: String = if (currentInst == null) "" else {
              val instSpanString = currentInst.getInfo match {
                case Some(info) => "(" + info.getSpan.getFileNameOnly + ":" + info.getSpan.getBegin.getLine + ":" + info.getSpan.getBegin.column() + ")"
                case None => ""
              }
              "[" + currentInst.getInstId + "] " + currentInst.toString() + " " + instSpanString
            }

            // Function info
            val funcId = currentNode._1
            /*val funcName = if (funcId == c.getCFG.getGlobalFId) "global function"
            else {
              var tempFuncName = c.getCFG.getFuncName(funcId)
              val index = tempFuncName.lastIndexOf("@")
              if (index != -1) tempFuncName = tempFuncName.substring(0, index)
              "function " + tempFuncName
            }
            val funcSpan = c.getCFG.getFuncInfo(funcId).getSpan()
            val funcSpanBegin = funcSpan.getBegin()
            val funcSpanEnd = funcSpan.getEnd()*/

            printf("  %d> ", currentLevel)
            for(i <- 0 until currentLevel) printf("  ")

            printf("%s" + /*" in %s(%s:%d:%d~%d:%d)," +*/ " (%s,%s)\n",
              source,
              //funcName, funcSpan.getFileNameOnly, funcSpanBegin.getLine, funcSpanBegin.column(), funcSpanEnd.getLine, funcSpanEnd.column(),
              currentNode, currentCallContext)

            // Follow up the trace (Call relation "1(callee) : n(caller)" is possible)
            val controlPointPredSet = c.getSemantics.ipPredMap.get((funcId, LEntry), currentCallContext)
            if (controlPointPredSet.isDefined) {
              for(controlPointPred <- controlPointPredSet.get) {
                traceStack.push((currentLevel + 1, controlPointPred, c.getCFG.getLastInst(controlPointPred._1)))
              }
            }
          }
        }
        case "cmd" | "command" => {
          val cp = c.current

          c.getCFG.getCmd(cp._1) match {
            case Block(insts) =>
              System.out.println("- Command")
              for (inst <- insts) {
                System.out.println("    [" + inst.getInstId + "] " + inst.toString)
              }
              System.out.println()
            case _ => System.out.println("- Nothing")
          }
        }
        case "fid" if args.length > 1 => {
          val arg1 = args(1)
          try {
            val fid = arg1.toInt
            val info = c.getCFG.getFuncInfo(fid)
            val name = c.getCFG.getFuncName(fid)
            val filename = info.getSpan.getBegin.getFileName
            val begin = info.getSpan.getBegin.getLine
            val end = info.getSpan.getEnd.getLine

            System.out.println("Function name: "+name)
            System.out.println("%d~%d @%s".format(begin, end, filename))
          } catch {
            case e: NumberFormatException => System.out.println("fid must be integer.")
            case e: NoSuchElementException => System.out.println("unknown fid: "+arg1)
          }
        }
        case _ => {
          System.err.println("Illegal arguments: "+subcmd)
        }
      }
    } catch {
      case e: ArrayIndexOutOfBoundsException => help()
    }
  }
}
