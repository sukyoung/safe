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

package kr.ac.kaist.safe.compiler

import kr.ac.kaist.safe.Config
import kr.ac.kaist.safe.errors.{ StaticError, StaticErrors }
import kr.ac.kaist.safe.nodes.{ IRRoot, Program }
import kr.ac.kaist.safe.util.NodeUtil

object Compiler {
  def compile(config: Config): (IRRoot, Int, List[StaticError]) = {
    val (program, return_code, errors) = astRewrite(config)
    val translator = new Translator(program)
    val ir = translator.doit.asInstanceOf[IRRoot]
    val errs = errors ::: translator.getErrors
    (
      ir,
      StaticErrors.reportErrors(NodeUtil.getFileName(program), errs),
      errs
    )
  }

  def astRewrite(config: Config): (Program, Int, List[StaticError]) = {
    var program: Program = Parser.fileToAST(config.FileNames)
    program = (new Hoister(program).doit).asInstanceOf[Program]
    val disambiguator = new Disambiguator(program, config)
    program = (disambiguator.doit).asInstanceOf[Program]
    var errors: List[StaticError] = disambiguator.getErrors
    val withRewriter: WithRewriter = new WithRewriter(program, false)
    program = withRewriter.doit.asInstanceOf[Program]
    errors :::= withRewriter.getErrors
    (
      program,
      StaticErrors.reportErrors(NodeUtil.getFileName(program), errors),
      errors
    )
  }
}
