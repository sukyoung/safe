/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.clone_detector.vgen

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Collection
import java.util.Iterator
import java.util.List
import java.util.Vector
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.Span
import java.util.regex.Pattern

class VectorGenerator(file: String, minT: Int, sliding_stride: Int, vec_dir: String, isJS: Boolean) {
  def this(file: String, minT: Int, sliding_stride: Int, vec_dir: String) = this(file, minT, sliding_stride, vec_dir, true)
  
  var minTokens = minT
  var stride = sliding_stride
  var vector_dir = vec_dir
  
  // Generate vectors from a file
  val pgm = Parser.parseFileConvertExn(new File(file), true)
  /* for debugging
   *
  System.out.println(file)
  System.out.println("Ok")
  System.out.println(pgm.serialize())
   */
  new JSAstVectorGenerator(pgm, minTokens).doit
  //System.out.println(pgm.getInfo.getSpan.getCharVector.toString + "Characteristic Vector")

  // Merge vectors
  val serializedTree = new Vector[ASTNode]
  new JSAstSerializer(pgm, serializedTree, minTokens).doit
  val mergedVectors = new Vector[Span]
  var itr = serializedTree.iterator
  while (itr.hasNext) {
    val node = itr.next
    if (!node.getInfo.getSpan.getCharVector.isMergeable) {
      itr.remove
    }
  }
  
  /* for debugging
   *
  for (i <- 0 to serializedTree.size-1)
      System.out.println("Serialized Tree: " + serializedTree.elementAt(i).getInfo.getSpan.getCharVector.toString + "\t" + serializedTree.elementAt(i).getInfo.getSpan.getBegin.getLine + " " + serializedTree.elementAt(i).getInfo.getSpan.getEnd.getLine + " " + serializedTree.elementAt(i).getInfo.getSpan.getCharVector.isMergeable)
   */
      
  if (! serializedTree.isEmpty) {
    if (stride != 0) {
	    var step = 0
	    var front = 0
	    var back = front + 1
	    do {
	      var span = serializedTree.elementAt(front).getInfo.getSpan
	      if (serializedTree.size > 1)
	        for (i <- front+1 to back)
	          span = new Span(span, serializedTree.elementAt(back).getInfo.getSpan)
	      while (back != serializedTree.size-1 && !span.getCharVector.containsEnoughTokens(minTokens)) {
	        back += 1
	        span = new Span(span, serializedTree.elementAt(back).getInfo.getSpan)
	      }
	      if (step % stride == 0) {
	        if (! mergedVectors.isEmpty) {
	          val prev = mergedVectors.elementAt(mergedVectors.size-1)
	          if (prev.begin.getLine != span.begin.getLine || prev.end.getLine != span.end.getLine || prev.getCharVector.getNumOfTokens < span.getCharVector.getNumOfTokens)
	            mergedVectors.add(span)
	        } else mergedVectors.add(span)
	      }
	      front += 1
	      step += 1
	    } while (front < serializedTree.size)
    } else {
      for (i <- 0 to serializedTree.size-1)
        mergedVectors.add(serializedTree.elementAt(i).getInfo.getSpan)
    }
      
    /* for debugging
     *
    for (i <- 0 to mergedVectors.size-1)
      System.out.println("Merged vector: " + mergedVectors.elementAt(i).cvec.toString + "\t" + mergedVectors.elementAt(i).getBegin.getLine + " " + mergedVectors.elementAt(i).getEnd.getLine)
     */
  }

  // Remove smaller vectors of the same location
  var mv_itr = mergedVectors.iterator
  var cur = mv_itr.next
  while (mv_itr.hasNext) {
    val node = mv_itr.next
    if (cur.getFileName.equals(node.getFileName) && 
        cur.getBegin.getLine == node.getBegin.getLine && 
        cur.getEnd.getLine == node.getEnd.getLine) mv_itr.remove
    else cur = node
  }
  
  // Write vectors to a file
  val filename = vector_dir + "/vdb_" + minTokens + "_" + stride
  val fstream = new FileWriter(filename, true)
  val out = new BufferedWriter(fstream)
  val js_pattern = Pattern.compile(".[p|s|x]{0,1}htm[l]{0,1}(.[0-9]+_[0-9]+.js)")
  for (i <- 0 to mergedVectors.size-1) {
    val s = mergedVectors.elementAt(i)
    val node_kind = s.getCharVector.getNodeKind
    val sFileName = if (isJS) s.getFileName else {
      val matcher = js_pattern.matcher(s.getFileName)
      val target = if (matcher.find) matcher.group(1) else ""
      s.getFileName.replace(target, "")
    }
    out.write("# FILE:" + sFileName + ", LINE:" + s.getBegin.getLine + ", OFFSET:" + s.getEnd.getLine + ", NODE_KIND:" + node_kind + ", CONTEXT_KIND:0, NEIGHBOR_KIND:0, NUM_NODE:" + s.getCharVector.getNumOfTokens + ", NUM_DECL:0, NUM_STMT:0, NUM_EXPR:0,")
    out.newLine
    out.write(s.getCharVector.toString)
    out.newLine
  }
  out.close

  def getMergedVectors = mergedVectors
}
