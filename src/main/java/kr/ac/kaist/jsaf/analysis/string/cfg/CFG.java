/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.string.cfg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Context Free Grammar
 * @author Peltro
 *
 */
public class CFG {
	//private Set<NonTerminal> N;
	//private Set<Terminal> T;
	private Set<Production> P;
	private NonTerminal S;
	
	/**
	 * 주어진 P, S를 이용해 CFG를 초기화한다.
	 * @param P_
	 * @param S_
	 */
	public CFG(
			Set<Production> P_,
			NonTerminal S_){
		//P = new HashSet<Production>(P_);
		P = P_;
		S = S_;
	}
	
	/**
	 * 빈 CFG를 만든다.
	 */
	public CFG(){
		P = new HashSet<Production>();
		S = null;
	}
	
	/**
	 * production rule을 추가한다.
	 * @param newprod
	 */
	public void addProd(Production newprod){
		P.add(newprod);
	}
	/**
	 * 화살표 왼쪽 오른쪽을 각각 받아 production rule을 추가한다.
	 * @param from
	 * @param to
	 */
	public void addProd(NonTerminal from, List<Token> to){
		P.add(new Production(from, to));
	}
	/**
	 * production rule이 P 안에 있다면 제거한다.
	 * @param prod
	 * @return 인자로 받은 production rule이 P 안에 있었을 경우 true
	 */
	public boolean removeProd(Production prod){
		return P.remove(prod);
	}
	/**
	 * 화살표 왼쪽 오른쪽을 각각 받아 해당 production rule을 제거한다.
	 * @param from
	 * @param to
	 * @return 인자로 받은 production rule이 P 안에 있었을 경우 true
	 */
	public boolean removeProd(NonTerminal from, List<Token> to){
		return P.remove(new Production(from, to));
	}
	
	/**
	 * P를 반환
	 * @return P
	 */
	public Set<Production> getP(){
		return P;
	}
	/**
	 * S를 반환
	 * @return S
	 */
	public NonTerminal getS(){
		return S;
	}
	/**
	 * S를 주어진 인자로 변경
	 * @param S_
	 */
	public void setS(NonTerminal S_){
		S = S_;
	}
	/**
	 * P를 주어진 인자로 변경
	 * @param P_
	 */
	public void setP(Set<Production> P_){
		//P = new HashSet<Production>(P_);
		P = P_;
	}
}