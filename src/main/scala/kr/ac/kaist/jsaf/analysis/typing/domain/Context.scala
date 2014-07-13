/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import kr.ac.kaist.jsaf.analysis.cfg.InternalError
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

case class Context(private val env: LocSet, private val thisBinding: LocSet, mayOld: AddrSet, mustOld: AddrSet) {
  /* tuple-like accessor */
  def _1: LocSet = throw new InternalError("Environment was moved from Context to PureLocal as property @env.")
  def _2: LocSet = throw new InternalError("This binding was moved from Context to PureLocal as property @this.")
  val _3 = mayOld
  val _4 = mustOld  // null mustOld value represents intersection domain bottom.

  /* partial order */
  def <= (that: Context) = {
    if (this eq that) true
    else {
      this.mayOld.subsetOf(that.mayOld) &&
      ( if (this.mustOld == null) true
        else if (that.mustOld == null) false
        else that.mustOld.subsetOf(this.mustOld)
      )
    }

  }

  /* not a partial order */
  def </ (that: Context) = {
    if (this eq that) false
    else {
      !this.mayOld.subsetOf(that.mayOld) ||
      ( if (this.mustOld == null) false
        else if (that.mustOld == null) true
        else !that.mustOld.subsetOf(this.mustOld)
      )
    }
  }

  /* bottom checking */
  def isBottom: Boolean = {
    this.mustOld == null && this.mayOld.isEmpty  
  }
  
  /* join */
  def + (that: Context) = {
    if (this eq that) this
    else if (this eq ContextBot) that
    else if (that eq ContextBot) this
    else {
      Context(LocSetBot, 
              LocSetBot,
              this.mayOld ++ that.mayOld,
              ( if (this.mustOld == null) that.mustOld
                else if (that.mustOld == null) this.mustOld
                else this.mustOld.intersect(that.mustOld)
              ))
    }
  }

  /* meet */
  def <> (that: Context) = {
    if (this eq that) this
    else {
      Context(LocSetBot,
              LocSetBot,
              this.mayOld.intersect(that.mayOld),
              ( if (this.mustOld == null) null
                else if (that.mustOld == null) null
                else this.mustOld ++ that.mustOld
              ))
    }
  }

  /* substitute l_r by l_o */
  def subsLoc(l_r: Loc, l_o: Loc): Context = {
    Context(LocSetBot, LocSetBot, mayOld + locToAddr(l_r), mustOld + locToAddr(l_r))
  }

  /* weakly substitute l_r by l_o, that is keep l_r together */
  def weakSubsLoc(l_r: Loc, l_o: Loc): Context = {
    Context(LocSetBot, LocSetBot, mayOld + locToAddr(l_r), mustOld)
  }

  def restrict(lp: LPSet) = {
    // TODO: Is this.mayOld OK?
    Context(LocSetBot, LocSetBot, this.mayOld, this.mustOld)
  }

  def restrict(l: LocSet) = {
    if (l.contains(ContextLoc))
      Context(LocSetBot, LocSetBot, this.mayOld, this.mustOld)
    else
      ContextBot
  }

  // TODO: This method is for pre-analysis.
  //       The change that env, this are moved to PureLocal should be properly applied.
  def oldify() : Context = {
    Context(oldifyLoc(env), oldifyLoc(thisBinding), mayOld, mustOld)
  }

  // internal locations exist the only one in the heap
  private def oldifyLoc(locSet: LocSet): LocSet = {
    locSet.foldLeft(locSet)((lset, loc) => if(locToAddr(loc).toInt < 0) lset else lset + addrToLoc(locToAddr(loc), Old))
  }
}
