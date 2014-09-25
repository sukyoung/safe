/*******************************************************************************
    Copyright (c) 2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import java.io._
import java.net._
import java.nio.charset.CodingErrorAction
import java.util.{ List => JList, HashMap }
import java.lang.{ Integer => JInteger }
import scala.io.Codec
import scala.io._
import edu.rice.cs.plt.tuple.{ Option => JOption }
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes.Program
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.useful.{ Useful, Triple, Pair }
import kr.ac.kaist.jsaf.analysis.typing.Config
import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHelper
import kr.ac.kaist.jsaf.Shell
import java.util.ArrayList

object DOMStatistics {

  private var apiset = Set[String]()
  private var usedset = Set[String]()
  private var outputFile : File = null

  def setInputFile(filename : String) = {
    val input = new File(filename)
    val inputsource = Source.fromFile(input)
    inputsource.getLines.foreach(s => {
      val splitted = s.split(",");
      apiset += splitted(0) + "." + splitted(1)

    })
  }
  
  def setOutputFile(filename : String) =
    outputFile = new File(filename) 
  

  def addAPI(name : String) = {
     usedset += name    
  }

  def printResult() = {
    val writer = new BufferedWriter(new FileWriter(outputFile))
    var nonmodeled = Set[String]()
    var modeled = Set[String]()
    usedset.foreach(s => {
       if(apiset(s))
         modeled += s
       else
         nonmodeled += s
    })
    writer.write("API usage\n")
    writer.write("--------------------\n")
    writer.write("Modeled APIs : " + modeled.size + "\\" + apiset.size +"\n")
    writer.write("Non-modeled APIs : " + nonmodeled.size + "\n")
    writer.write("Total : " + usedset.size + "\n")
    writer.write("Modeling Coverage : " + modeled.size + "\\" + usedset.size + "(" +((modeled.size.toDouble / usedset.size.toDouble) *100) + "%) \n")
    writer.write("--------------------\n")
    writer.newLine    
    writer.write("List of modeld APIs\n")
    writer.write("--------------------\n")
    modeled.foreach(s => 
      writer.write(s + "\n")
    )
    writer.newLine    
    writer.write("List of non-modeld APIs\n")
    writer.write("--------------------\n")
    nonmodeled.foreach(s => 
      writer.write(s + "\n")
    )
    writer.flush()
    writer.close()
  }
}
