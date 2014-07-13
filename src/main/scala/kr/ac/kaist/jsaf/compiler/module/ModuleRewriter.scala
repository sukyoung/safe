/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.compiler.module

import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, _}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.compiler.module._
import java.util.HashMap

class ModuleRewriter(program: Program) extends Walker {
  /*
   * Entry point
   */
  def doit(): Program = {
    val env: Env = (new ModEnvWalker(program)).doit
    val fds: List[SourceElement] = (new ModFunWalker(env, Nil, program)).doit
    val vds: List[SourceElement] = (new ModVarWalker(env, Nil, program)).doit
    val inst: List[SourceElement] = (new ModInstWalker(env, Nil, program)).doit
    //val init: List[SourceElement] = (new ModInitWalker(env, Nil, program)).doit
    val stmt: List[SourceElement] = (new ModStmtWalker(env, Nil, program)).doit
    SProgram(program.getInfo,
             STopLevel(Nil, Nil,
                       List(SSourceElements(program.getInfo, fds ::: vds ::: inst ::: stmt, false))))
             //STopLevel(Nil, Nil, fds ::: vds ::: inst ::: init ::: stmt))
  }
}
