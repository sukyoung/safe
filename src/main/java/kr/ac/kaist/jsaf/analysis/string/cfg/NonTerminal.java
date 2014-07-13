/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.string.cfg;

public class NonTerminal extends Token {
	public NonTerminal(){
		super();
	}
	
	public NonTerminal(String s){
		super(s);
	}
	
	@Override public boolean equals(Object that){
		if(!(that instanceof NonTerminal)) return false;
		return super.equals(that);
	}
}
