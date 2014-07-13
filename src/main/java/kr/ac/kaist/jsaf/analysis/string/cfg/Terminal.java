/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.string.cfg;

import java.util.LinkedList;
import java.util.List;

public class Terminal extends Token {
	public Terminal(){
		super();
	}
	
	public Terminal(String s){
		super(s);
	}
	
	@Override public boolean equals(Object that){
		if(!(that instanceof Terminal)) return false;
		return super.equals(that);
	}
	
	public static List<Token> getTerminals(String input) {
		int length = input.length();
		List<Token> ret = new LinkedList<Token>();
		for(int i = 0; i < length; i++) {
			ret.add(new Terminal(Character.toString(input.charAt(i))));
		}
		
		return ret;
	}
}