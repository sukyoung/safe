/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.string.automata;

/**
 * 간단하게 쓸 수 있는 Pair 클래스
 * @author JiminP
 */
public class Pair<A, B> {
	protected final A a; protected final B b;
	public Pair(A a, B b){
		this.a = a;
		this.b = b;
	}
	
	public final A car(){
		return this.a;
	}
	
	public final B cdr(){
		return this.b;
	}
	
	@Override public int hashCode(){
		return this.a.hashCode() ^ this.b.hashCode();
	}
	
	@Override public String toString(){
		return "("+this.a.toString()+","+this.b.toString()+")";
	}
	
	@Override public boolean equals(Object that){
		if(!(that instanceof Pair<?, ?>)) return false;
		Pair<?, ?> pthat = (Pair<?, ?>) that;
		
		if(pthat.car() == null){
			if(this.car() != null) return false;
		}else if(!pthat.car().equals(this.car())) return false;
		
		if(pthat.cdr() == null){
			if(this.cdr() != null) return false;
		}else if(!pthat.cdr().equals(this.cdr())) return false;
		
		return true;
	}
}