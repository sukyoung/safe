/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug.commands
import scala.collection.mutable.HashMap
import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsole
import kr.ac.kaist.jsaf.analysis.typing.domain.DomainPrinter
import kr.ac.kaist.jsaf.analysis.typing.Semantics
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

class CmdPrintResult extends Command {
  override val name = "result"
  override val info: String = "Print out various information."

  override def help(): Unit = {
    System.out.println("usage: result allstate")
    System.out.println("       result allexcstate")
    System.out.println("       result state")
    System.out.println("       result excstate")
    System.out.println("       result loc {LocName}")
    System.out.println("       result excloc {LocName}")
  }

  override def run(c: DebugConsole, args: Array[String]): Unit = {
    val sem = new Semantics(c.getCFG, c.getWorklist, Shell.params.opt_LocClone)
    try {
      val subcmd = args(0)
      subcmd.toLowerCase match {
        case "allstate" => {
          val inS = c.readTable(c.current)
          val cmd = c.getCFG.getCmd(c.current._1)

          val (outS, _) = sem.C(c.current, cmd, inS)
          System.out.println(DomainPrinter.printHeap(0, outS._1, c.getCFG, 3))
          System.out.println(DomainPrinter.printContext(0, outS._2))
        }
        case "allexcstate" => {
          val inS = c.readTable(c.current)
          val cmd = c.getCFG.getCmd(c.current._1)

          val (_, outES) = sem.C(c.current, cmd, inS)
          System.out.println(DomainPrinter.printHeap(0, outES._1, c.getCFG, 3))
          System.out.println(DomainPrinter.printContext(0, outES._2))
        }
        case "state" => {
          val inS = c.readTable(c.current)
          val cmd = c.getCFG.getCmd(c.current._1)

          val (outS, _) = sem.C(c.current, cmd, inS)
          System.out.println(DomainPrinter.printHeap(0, outS._1, c.getCFG))
          System.out.println(DomainPrinter.printContext(0, outS._2))
        }
        case "excstate" => {
          val inS = c.readTable(c.current)
          val cmd = c.getCFG.getCmd(c.current._1)

          val (_, outES) = sem.C(c.current, cmd, inS)

          System.out.println(DomainPrinter.printHeap(0, outES._1, c.getCFG))
          System.out.println(DomainPrinter.printContext(0, outES._2))
        }
        case "loc" if args.length > 1 => {
          val arg1 = args(1)
          val sloc = parseLocName(arg1)
          sloc match {
            case Some(loc) => {
              val inS = c.readTable(c.current)
              val cmd = c.getCFG.getCmd(c.current._1)

              val (outS, _) = sem.C(c.current, cmd, inS)
              val o = outS._1(loc)
              val name = DomainPrinter.printLoc(loc)
              System.out.println(name + " -> ")
              System.out.println(DomainPrinter.printObj(4+name.length, o))
            }
            case None => {
              System.err.println("cannot find: "+arg1)
            }
          }
        }
        case "excloc" if args.length > 1 => {
          val arg1 = args(1)
          val sloc = parseLocName(arg1)
          sloc match {
            case Some(loc) => {
              val inS = c.readTable(c.current)
              val cmd = c.getCFG.getCmd(c.current._1)

              val (_, outES) = sem.C(c.current, cmd, inS)
              val o = outES._1(loc)
              val name = DomainPrinter.printLoc(loc)
              System.out.println(name + " -> ")
              System.out.println(DomainPrinter.printObj(4+name.length, o))
            }
            case None => {
              System.err.println("cannot find: "+arg1)
            }
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
