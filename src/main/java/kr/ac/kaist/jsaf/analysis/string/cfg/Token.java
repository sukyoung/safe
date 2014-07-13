/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.string.cfg;

public class Token {
	protected final String value;
	
	public Token(String s){
		this.value = s;
	}
	
	public Token(){
		this(null);
	}
	
	@Override public int hashCode(){
		if(this.value == null) return 0;
		return this.value.hashCode();
	}
	
	@Override public boolean equals(Object that){
		if(!(that instanceof Token)) return false;
		Token tThat = (Token) that;
		if(this.value == null) return tThat.value == null;
		return this.value.equals(tThat.value);
	}
	
	@Override public String toString(){
		return "["+this.getClass().getSimpleName()+" "+(this.value == null? "": this.value)+"]";
	}
}
