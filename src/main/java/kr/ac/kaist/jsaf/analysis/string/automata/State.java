/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.string.automata;

/**
 * 오토마타의 한 상태를 나타내는 클래스.
 * @author JiminP
 */
public class State {
	private static long gid = 0;
	private long id;
	
	/**
	 * 새 상태를 생성한다.
	 */
	public State(){
		this.id = gid++;
	}
	
	/**
	 * 기존의 존재하는 상태를 복사한다.
	 * @param state
	 */
	public State(State state){
		this.id = state.id;
	}
	
	/**
	 * 상태의 고유한 ID를 반환한다.
	 * @return	상태의 ID값
	 */
	public long getID(){
		return this.id;
	}
	
	@Override public int hashCode(){
		return (int) this.id;
	}
	
	@Override public boolean equals(Object that){
		return (that instanceof State) && this.id == ((State) that).id;
	}
	
	@Override public String toString(){
		return "[State " + this.id + "]";
	}
}
