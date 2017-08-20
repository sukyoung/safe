/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 * *
 * Use is subject to license terms.
 * *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.clone_detector.vgen

import java.io.{ BufferedWriter, File, FileWriter }
import java.util
import java.util.regex.Pattern

import kr.ac.kaist.safe
import kr.ac.kaist.safe.nodes.ast.{ ASTNode, Program, SourceElements, TopLevel }
import kr.ac.kaist.safe.parser.{ JSFromHTML, Parser }
import kr.ac.kaist.safe.util.Span

class VectorGenerator(file: String, minT: Int, slidingStride: Int, vecDir: String, isJS: Boolean) {
  def this(file: String, minT: Int, slidingStride: Int, vecDir: String) = this(file, minT, slidingStride, vecDir, true)

  val minTokens: Int = minT
  val stride: Int = slidingStride
  val vectorDir: String = vecDir

  // Generate vectors from a file
  val pgm: Program =
    if (file.endsWith(".js")) {
      val (program, _) = Parser.fileToAST(List(file)).get
      program
    } else {
      val (ses, _) = JSFromHTML.parseScripts(file).get
      wrapSourceElementsAsProgram(ses)
    }

  /* for debugging
   *
  System.out.println(file)
  System.out.println("Ok")
  System.out.println(pgm.serialize())
   */
  new JSAstVectorGenerator(pgm, minTokens).doit
  // println(pgm.info.span.cvec.toString + "Characteristic Vector")

  // Merge vectors
  val serializedTree = new util.Vector[ASTNode]
  new JSAstSerializer(pgm, serializedTree, minTokens).doit()
  val mergedVectors = new util.Vector[Span]

  var itr: util.Iterator[ASTNode] = serializedTree.iterator
  while (itr.hasNext) {
    val node = itr.next
    if (!node.info.span.cvec.isMergeable) {
      itr.remove()
    }
  }

  /* for debugging
   *
  for (i <- 0 until serializedTree.size)
    println("Serialized Tree: " + serializedTree.elementAt(i).info.span.cvec.toString + "\t" + serializedTree.elementAt(i).info.span.begin.line + " " + serializedTree.elementAt(i).info.span.end.line + " " + serializedTree.elementAt(i).info.span.cvec.isMergeable)
   */

  if (!serializedTree.isEmpty) {
    if (stride != 0) {
      var step = 0
      var front = 0
      var back = front + 1
      do {
        var span = serializedTree.elementAt(front).info.span
        if (serializedTree.size > 1)
          for (i <- front + 1 to back) {
            span = Span.merge(span, serializedTree.elementAt(back).info.span)
            span.cvec.merge(serializedTree.elementAt(back).info.span.cvec.getVector)
          }
        while (back != serializedTree.size - 1 && !span.cvec.containsEnoughTokens(minTokens)) {
          back += 1
          span = Span.merge(span, serializedTree.elementAt(back).info.span)
          span.cvec.merge(serializedTree.elementAt(back).info.span.cvec.getVector)
        }
        if (step % stride == 0) {
          if (!mergedVectors.isEmpty) {
            val prev = mergedVectors.elementAt(mergedVectors.size - 1)
            if (prev.begin.line != span.begin.line || prev.end.line != span.end.line || prev.cvec.getNumOfTokens < span.cvec.getNumOfTokens)
              mergedVectors.add(span)
          } else mergedVectors.add(span)
        }
        front += 1
        step += 1
      } while (front < serializedTree.size)
    } else {
      for (i <- 0 until serializedTree.size)
        mergedVectors.add(serializedTree.elementAt(i).info.span)
    }

    /* for debugging
     *
    for (i <- 0 until mergedVectors.size)
      System.out.println("Merged vector: " + mergedVectors.elementAt(i).cvec.toString + "\t" + mergedVectors.elementAt(i).begin.line + " " + mergedVectors.elementAt(i).end.line)
     */
  }

  // Remove smaller vectors of the same location
  var mvItr: util.Iterator[Span] = mergedVectors.iterator
  if (mvItr.hasNext) {
    var cur = mvItr.next
    while (mvItr.hasNext) {
      val node = mvItr.next
      if (cur.fileName.equals(node.fileName) &&
        cur.begin.line == node.begin.line &&
        cur.end.line == node.end.line) mvItr.remove()
      else cur = node
    }
  }

  // Write vectors to a file
  val filename: String = vectorDir + File.separator + "vdb_" + minTokens + "_" + stride
  val fstream = new FileWriter(filename, true)
  val out = new BufferedWriter(fstream)
  val jsPattern: Pattern = Pattern.compile(".[p|s|x]{0,1}htm[l]{0,1}(.[0-9]+_[0-9]+.js)")
  for (i <- 0 until mergedVectors.size) {
    val s = mergedVectors.elementAt(i)
    val nodeKind = s.cvec.getNodeKind
    val sFileName = if (isJS) s.fileName else {
      val matcher = jsPattern.matcher(s.fileName)
      val target = if (matcher.find) matcher.group(1) else ""
      s.fileName.replace(target, "")
    }
    out.write("# FILE:" + sFileName + ", LINE:" + s.begin.line + ", OFFSET:" + s.end.line + ", NODE_KIND:" + nodeKind + ", CONTEXT_KIND:0, NEIGHBOR_KIND:0, NUM_NODE:" + s.cvec.getNumOfTokens + ", NUM_DECL:0, NUM_STMT:0, NUM_EXPR:0,")
    out.newLine()
    out.write(s.cvec.toString)
    out.newLine()
  }
  out.close()

  def wrapSourceElementsAsProgram(ses: SourceElements): Program = {
    new Program(ses.info, TopLevel(ses.info, Nil, Nil, List(ses)))
  }

  def getMergedVectors: util.Vector[Span] = mergedVectors
}
