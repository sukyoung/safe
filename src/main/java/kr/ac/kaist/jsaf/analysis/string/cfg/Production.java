/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.string.cfg;

import java.util.List;

/**
 * Production rule을 나타내는 클래스
 * @author Peltro
 */
public class Production {
	protected final NonTerminal from; protected final List<Token> to;
	public Production(NonTerminal from_, List<Token> to_){
		from = from_;
		to = to_;
	}
	
	public final NonTerminal getFrom(){
		return this.from;
	}
	
	public final List<Token> getTo(){
		return this.to;
	}
	
	@Override public int hashCode(){
		return this.from.hashCode() ^ this.to.hashCode();
	}
	
	@Override public String toString(){
		return "("+this.from.toString()+","+this.to.toString()+")";
	}
	
	@Override public boolean equals(Object that){
		if(!(that instanceof Production)) return false;
		Production pthat = (Production) that;
		
		if(pthat.getFrom() == null){
			if(this.getFrom() != null) return false;
		}else if(!pthat.getFrom().equals(this.getFrom())) return false;
		
		if(pthat.getTo() == null){
			if(this.getTo() != null) return false;
		}else if(!pthat.getTo().equals(this.getTo())) return false;
		
		return true;
	}
}