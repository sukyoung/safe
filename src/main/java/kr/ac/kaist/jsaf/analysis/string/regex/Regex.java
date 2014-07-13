/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.string.regex;
import kr.ac.kaist.jsaf.analysis.string.automata.Automata;
import kr.ac.kaist.jsaf.analysis.string.automata.State;
import kr.ac.kaist.jsaf.analysis.string.cfg.Token;

import java.util.HashSet;

/**
 * 
 * @author Peltro
 *
 * Token 클래스를 vocabulary로 하는 정규 표현식을 다루는 클래스.
 * Regex 클래스의 static 메서드를 이용해 정규 표현식을 만듭니다.
 */
public abstract class Regex {
	//constants
	public static final int EMPTY = 0;
	public static final int EPSILON = 1;
	public static final int SYMBOL = 2;
	public static final int UNION = 3;
	public static final int CONCAT = 4;
	public static final int CLOSURE = 5;
	
	protected int type;
	
	// Instances of this class can only be created by static method calls.
	public static Regex empty(){
		return new Empty();
	}
	public static Regex epsilon(){
		return new Epsilon();
	}
	public static Regex symbol(Token symbol){
		return new Symbol(symbol);
	}
	public static Regex union(Regex r1, Regex r2){
		return new Union(r1, r2);
	}
	public static Regex concat(Regex r1, Regex r2){
		return new Concat(r1, r2);
	}
	public static Regex closure(Regex r){
		return new Closure(r);
	}
	public int type(){
		return type;
	}
	
	/**
	 * Thompson's Algorithm으로 이 regex와 동등한 e-NFA를 만듭니다.
	 * @return 이 regex와 동등한 e-NFA. 반환되는 e-NFA의 시작 상태/끝 상태는 1개씩
	 */
	public Automata toAutomata(){
		State start = new State();
		State accept = new State();
		HashSet<State> acceptSet = new HashSet<State>();
		acceptSet.add(accept);
		Automata A = new Automata(start, acceptSet);
		switch(type){
		case EMPTY:
			//do nothing
			break;
		case EPSILON:
			A.addEdge(start, accept, Automata.epsilon);
			break;
		case SYMBOL:
			A.addEdge(start, accept, ((Symbol)this).symbol());
			break;
		case UNION:
			Automata l = ((Union)this).left().toAutomata();
			Automata r = ((Union)this).right().toAutomata();
			A.addStates(l.getStates());
			A.addEdges(l.getTransition());
			A.addStates(r.getStates());
			A.addEdges(r.getTransition());
			A.addEdge(start, l.getStart(), Automata.epsilon);
			A.addEdge(start, r.getStart(), Automata.epsilon);
			A.addEdge(l.getEnds().iterator().next(), accept, Automata.epsilon);
			A.addEdge(r.getEnds().iterator().next(), accept, Automata.epsilon);
			break;
		case CONCAT:
			l = ((Union)this).left().toAutomata();
			r = ((Union)this).right().toAutomata();
			A = new Automata(l.getStart(), r.getEnds());
			A.addStates(l.getStates());
			A.addEdges(l.getTransition());
			A.addStates(r.getStates());
			A.addEdges(r.getTransition());
			A.addEdge(l.getEnds().iterator().next(), r.getStart(), Automata.epsilon);
			break;
		case CLOSURE:
			l = ((Union)this).left().toAutomata();
			A.addStates(l.getStates());
			A.addEdges(l.getTransition());
			A.addEdge(start, accept, Automata.epsilon);
			A.addEdge(start, l.getStart(), Automata.epsilon);
			State lfinal = l.getEnds().iterator().next();
			A.addEdge(lfinal, l.getStart(), Automata.epsilon);
			A.addEdge(lfinal, accept, Automata.epsilon);
			break;
		}
		return A;
	}
}

class Empty extends Regex{
	public Empty(){
		type = EMPTY;
	}
	public String toString(){
		return "∅";
	}
}

class Epsilon extends Regex{
	public Epsilon(){
		type = EPSILON;
	}
	public String toString(){
		return "ε";
	}
}

class Symbol extends Regex{
	private Token symbol;
	public Symbol(Token symbol){
		type = SYMBOL;
		this.symbol = symbol;
	}
	public Token symbol(){
		return symbol;
	}
	public String toString(){
		return symbol.toString();
	}
}

class Union extends Regex{
	private Regex left;
	private Regex right;
	public Union(Regex left, Regex right){
		type = UNION;
		this.left = left;
		this.right = right;
	}
	public Regex left(){
		return left;
	}
	public Regex right(){
		return right;
	}
	public String toString(){
		return "("+left.toString()+"|"+right.toString()+")";
	}
}

class Concat extends Regex{
	private Regex left;
	private Regex right;
	public Concat(Regex left, Regex right){
		type = CONCAT;
		this.left = left;
		this.right = right;
	}
	public Regex left(){
		return left;
	}
	public Regex right(){
		return right;
	}
	public String toString(){
		return "("+left.toString()+"."+right.toString()+")";
	}
}

class Closure extends Regex{
	private Regex inside;
	public Closure(Regex inside){
		type = CLOSURE;
		this.inside = inside;
	}
	public Regex inside(){
		return inside;
	}
	public String toString(){
		return "("+inside.toString()+")*";
	}
}