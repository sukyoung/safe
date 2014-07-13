/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.string.automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.ac.kaist.jsaf.analysis.string.cfg.CFG;
import kr.ac.kaist.jsaf.analysis.string.cfg.Production;
import kr.ac.kaist.jsaf.analysis.string.cfg.Terminal;

/**
 * 부합 분석에서 상태값을 나타내는 데 쓰이는 선형 오토마타이다.
 * @author JiminP
 */
public class Automata {
	protected State start;
	protected Set<State> ends;
	protected Map<State, Map<Object, Set<State>>> transition;
	public static final Automata.Epsilon epsilon = new Automata.Epsilon();
	
	/**
	 * 주어진 시작점과 끝점들을 가지는 새 오토마타를 생성한다.
	 * @param start	시작 상태
	 * @param ends	끝 상태들의 모임
	 */
	public Automata(State start, Set<State> ends){
		this.transition = new HashMap<State, Map<Object, Set<State>>>();
		this.start = this.addState(start);
		this.ends = this.addStates(ends);
	}
	
	/**
	 * 주어진 시작점을 가지는 새 오토마타를 생성한다.
	 * @param start	시작 상태
	 */
	public Automata(State start){
		this(start, new HashSet<State>());
	}

	/**
	 * 빈 오토마타를 생성한다.
	 */
	public Automata(){
		this((State) null);
		this.start = this.addState();
	}
	
	/**
	 * 주어진 토큰으로부터 새 오토마타를 생성한다.
	 * @param tokens	오토마타가 인식할 토큰들의 나열
	 */
	public Automata(Object[] tokens){
		this();
		
		State currentState, nextState;
		currentState = this.start;
		
		for(int i=0;i<tokens.length;i++){
			nextState = this.addState();
			this.addEdge(currentState, nextState, tokens[i]);
			currentState = nextState;
		}
		
		this.ends.add(currentState);
	}
	
	/**
	 * 주어진 토큰으로부터 새 오토마타를 생성한다.
	 * @param <E>
	 * @param tokens	오토마타가 인식할 토큰들의 나열
	 */
	public <E> Automata(List<E> tokens){
		this((Object[]) tokens.toArray());
	}
	
	public Automata(String input) {
		this(Terminal.getTerminals(input));
	}
	
	/**
	 * 주어진 오토마타로부터 새 오토마타를 생성한다.
	 * @param automata	복사할 오토마타
	 */
	public Automata(Automata automata){
		this();
		
		Map<State, State> stateMap = new HashMap<State, State>();
		Set<State> searchSet = new HashSet<State>(), nextSearchSet;
		State tfrom, tto;
		
		stateMap.put(automata.start, this.start);
		searchSet.add(automata.start);
		
		while(!searchSet.isEmpty()){
			nextSearchSet = new HashSet<State>();
			for(State from: searchSet){
				assert(stateMap.containsKey(from));
				tfrom = stateMap.get(from);
				for(Map.Entry<Object, Set<State>> edges: automata.transition.get(from).entrySet()){
					for(State to: edges.getValue()){
						boolean searched = true;
						
						tto = null;
						if(!stateMap.containsKey(to)){
							searched = false;
							stateMap.put(to, tto = this.addState());
							
							if(automata.ends.contains(to)) this.ends.add(tto);
						}else tto = stateMap.get(to);
						
						assert(tto != null);
						this.addEdge(tfrom, tto, edges.getKey());
						
						if(!searched) nextSearchSet.add(to);
					}
				}
			}
			searchSet = nextSearchSet;
		}
	}
	
	@Override public Automata clone(){
		return new Automata(this);
	}
	
	public Set<Object> getSigma(){
		Set<Object> ret = new HashSet<Object>();
		for(Map.Entry<State, Map<Object, Set<State>>> entry: this.transition.entrySet()){
			ret.addAll(entry.getValue().keySet());
		}
		return ret;
	}
	
	/**
	 * 오토마타가 가지고 있는 상태들의 집합을 반환한다.
	 * @return	오토마타의 상태들의 집합
	 */
	public Set<State> getStates(){
		Set<State> r = new HashSet<State>();
		for(Map.Entry<State, Map<Object, Set<State>>> entry: this.transition.entrySet()){
			r.add(entry.getKey());
		}
		return r;
	}
	
	/**
	 * 오토마타가 가지고 있는 간선들을 반환한다.
	 * @return 오토마타의 간선들
	 */
	public Map<State, Map<Object, Set<State>>> getTransition(){
		return transition;
	}
	/**
	 * @return 오토마타의 시작 상태
	 */
	public State getStart(){
		return this.start;
	}
	
	/**
	 * @return 오토마타의 끝 상태들의 집합
	 */
	public Set<State> getEnds(){
		return this.ends;
	}
	
	/**
	 * 오토마타에 새 상태를 추가한다.
	 * @param	새로 추가할 상태
	 * @return	새로 추가된 상태
	 */
	public State addState(State state){
		if(state == null) return null;
		if(this.transition.containsKey(state)) return state;
		this.transition.put(state, new HashMap<Object, Set<State>>());
		return state;
	}

	/**
	 * 오토마타에 새 상태를 추가한다.
	 * @return	새로 추가된 상태
	 */
	public State addState(){
		return addState(new State());
	}
	
	/**
	 * 오토마타에 새 상태들을 추가한다.
	 * @param	새로 추가할 상태들
	 * @return	새로 추가된 상태들
	 */
	public Set<State> addStates(Set<State> states){
		for(State state: states) addState(state);
		return states;
	}

	/**
	 * 오토마타에 새 상태들을 추가한다.
	 * @param	새로 추가할 상태들
	 * @return	새로 추가된 상태들
	 */
	public List<State> addStates(List<State> states){
		for(State state: states) addState(state);
		return states;
	}
	
	/**
	 * 오토마타에 새 간선을 추가한다.
	 * @param from	시작 상태
	 * @param to	끝 상태
	 * @param value	간선 값
	 * @return	자기 자신
	 */
	public Automata addEdge(State from, State to, Object value){
		Map<Object, Set<State>> edges = this.transition.get(from);
		if(!edges.containsKey(value)) edges.put(value, new HashSet<State>());
		edges.get(value).add(to);
		return this;
	}
	
	/**
	 * 오토마타에 새 간선들을 추가한다.
	 * 경고: this.transition.putAll을 쓰면 안 된다.
	 * @param edges	추가할 간선들
	 * @return	자기 자신
	 */
	public Automata addEdges(Map<State, Map<Object, Set<State>>> edges){
		State from;
		Object value;
		for(Map.Entry<State, Map<Object, Set<State>>> entry: edges.entrySet()){
			from = entry.getKey();
			this.addState(from);
			for(Map.Entry<Object, Set<State>> entryEdges: entry.getValue().entrySet()){
				value = entryEdges.getKey();
				for(State state: entryEdges.getValue()) addEdge(from, state, value);
			}
		}
		return this;
	}
	
	/**
	 * 오토마타에 새 간선들을 추가한다. (addEdges와 같음)
	 * @param edges 추가할 간선들
	 * @return	자기 자신
	 * @see	addEdges
	 */
	public Automata addTransitions(Map<State, Map<Object, Set<State>>> edges){
		return this.addEdges(edges);
	}
	
	/**
	 * 오토마타의 도달 불가능한 상태를 제거한다.
	 * @return 자기 자신
	 */
	public Automata removeUnreachables(){
		Set<State> nextSearch, searching = new HashSet<State>();
		Set<State> unreachable = this.getStates();
		
		searching.add(this.start);
		
		while(!searching.isEmpty()){
			nextSearch = new HashSet<State>();
			for(State state: searching){
				unreachable.remove(state);
				
				for(Map.Entry<Object, Set<State>> edges: this.transition.get(state).entrySet()){
					for(State nextState: edges.getValue()){
						if(unreachable.contains(nextState)) nextSearch.add(nextState);
					}
				}
			}
			searching = nextSearch;
		}
		
		for(State state: unreachable){
			this.transition.remove(state);
		}
		return this;
	}
	
	protected Automata toDFA(){
		Automata automata = new Automata();
		
		Set<State> startClosure = epsilonClosure(this.start);
		Map<Set<State>, State> equivNode = new HashMap<Set<State>, State>();
		equivNode.put(startClosure, automata.start);
		
		Set<Set<State>> toSearch = new HashSet<Set<State>>(), nextSearch;
		toSearch.add(startClosure);
		
		while(!toSearch.isEmpty()){
			nextSearch = new HashSet<Set<State>>();
			for(Set<State> thisNode: toSearch){
				boolean containsEnd = false;
				for(State state: this.ends) if(thisNode.contains(state)){containsEnd = true; break;}
				if(containsEnd) automata.ends.add(equivNode.get(thisNode));
				
				for(Map.Entry<Object, Set<State>> edge: getEdges(thisNode).entrySet()){
					Object edgeValue = edge.getKey();
					if(Automata.isEpsilon(edgeValue)) continue;
					
					Set<State> edgeDst = epsilonClosure(edge.getValue());
					if(!equivNode.containsKey(edgeDst)){
						equivNode.put(edgeDst, automata.addState());
						nextSearch.add(edgeDst);
					}
					automata.addEdge(equivNode.get(thisNode), equivNode.get(edgeDst), edgeValue);
				}
			}
			toSearch = nextSearch;
		}
		
		return automata;
	}
	
	/**
	 * 오토마타를 minimal DFA로 변환한다.
	 * @return minimize된 DFA
	 */
	public Automata minimalDFA(){
		Automata dfa = this.removeUnreachables().toDFA();
		
		if(dfa.ends.isEmpty()) return new Automata();
		
		// 파티션 생성
		Set<Object> sigma = dfa.getSigma();
		Set<Set<State>> P = new HashSet<Set<State>>();
		Set<Set<State>> W = new HashSet<Set<State>>();
		
		Set<State> QF = new HashSet<State>();
		for(State state: dfa.getStates()) if(!dfa.ends.contains(state)) QF.add(state);
		
		P.add(dfa.ends);
		if(!QF.isEmpty()) P.add(QF);
		W.add(dfa.ends);
		
		while(!W.isEmpty()){
			Set<State> A = W.iterator().next(); W.remove(A);
			for(Object c: sigma){
				Set<State> X = new HashSet<State>();
				for(State state: A) X.addAll(dfa.reverseSearch(state, c));
				
				Set<Set<State>> pRemove = new HashSet<Set<State>>();
				Set<Set<State>> pAdd = new HashSet<Set<State>>();
				
				for(Set<State> Y: P){
					Set<State> Y_X = new HashSet<State>(), XY = new HashSet<State>();
					for(State y: Y){
						if(X.contains(y)) XY.add(y);
						else Y_X.add(y);
					}
					if(XY.isEmpty()) continue;
					pRemove.add(Y);
					if(!Y_X.isEmpty()) pAdd.add(Y_X);
					pAdd.add(XY);
					if(W.contains(Y)){
						W.remove(Y); W.add(Y_X); W.add(XY);
					}else{
						W.add(XY.size() <= Y_X.size() ? XY : Y_X);
					}
				}
				
				P.removeAll(pRemove);
				P.addAll(pAdd);
			}
		}
		
		// 오토마타 생성
		Automata mdfa = new Automata();
		
		Map<Set<State>, State> equivSet = new HashMap<Set<State>, State>();
		Map<State, Set<State>> stateToPart = new HashMap<State, Set<State>>();
		
		for(Set<State> p: P){
			boolean isStart = false, isEnd = false;
			for(State state: p){
				if(state.equals(dfa.start)) isStart = true;
				if(!isEnd && dfa.ends.contains(state)) isEnd = true;
				stateToPart.put(state, p);
			}
			if(isStart) equivSet.put(p, mdfa.start);
			else equivSet.put(p, mdfa.addState());
			
			if(isEnd) mdfa.ends.add(equivSet.get(p));
		}
		
		for(Set<State> p: P){
			State state = p.iterator().next();
			for(Map.Entry<Object, Set<State>> edge: dfa.transition.get(state).entrySet()){
				Set<State> dst = stateToPart.get(edge.getValue().iterator().next());
				mdfa.addEdge(equivSet.get(p), equivSet.get(dst), edge.getKey());
			}
		}
		
		return mdfa;
	}
	
	protected Map<Object, Set<State>> getEdges(Set<State> states){
		Map<Object, Set<State>> ret = new HashMap<Object, Set<State>>();
		for(State state: states){
			for(Map.Entry<Object, Set<State>> edges: this.transition.get(state).entrySet()){
				if(ret.containsKey(edges.getKey())){
					ret.get(edges.getKey()).addAll(edges.getValue());
				}else{
					// 참고: 절대로 그냥 넣으면 안 된다. (레퍼런스)
					ret.put(edges.getKey(), new HashSet<State>());
					ret.get(edges.getKey()).addAll(edges.getValue());
				}
			}
		}
		return ret;
	}
	
	protected Set<State> epsilonClosure(State state){
		Set<State> x = new HashSet<State>(); x.add(state);
		return epsilonClosure(x);
	}
	
	protected Set<State> epsilonClosure(Set<State> states){
		Set<State> search, nextSearch, closure, epsilon, temp;
		search = states;
		closure = new HashSet<State>();
		
		while(!search.isEmpty()){
			nextSearch = new HashSet<State>();
			for(State s: search){
				closure.add(s);
				temp = this.transition.get(s).get(Automata.epsilon);
				if(temp != null){
					epsilon = new HashSet<State>();
					for(State state: temp)
						if(!closure.contains(state)) epsilon.add(state);
					nextSearch.addAll(epsilon);
				}
			}
			search = nextSearch;
		}
		
		return closure;
	}
	
	protected Set<State> reachable(Set<State> begins, Object token){
		Set<State> ret = new HashSet<State>();
		for(State state: epsilonClosure(begins)){
			Set<State> destinations = this.transition.get(state).get(token);
			if(destinations != null) ret.addAll(destinations);
		}
		return epsilonClosure(ret);
	}
	
	/**
	 * 오토마타가 주어진 입력값을 인식하는지의 여부를 판별한다.
	 * @param input	입력값
	 * @return	이 오타마타가 입력값을 인식하는지의 여부
	 */
	public boolean accept(Object[] input){
		Set<State> currentStates;
		currentStates = epsilonClosure(this.start);
		
		for(int i=0;i<input.length;i++){
			currentStates = reachable(currentStates, input[i]);
		}
		
		for(State state: currentStates){
			if(this.ends.contains(state)) return true;
		}
		return false;
	}

	/**
	 * 오토마타가 주어진 입력값을 인식하는지의 여부를 판별한다.
	 * @param input	입력값
	 * @return	이 오타마타가 입력값을 인식하는지의 여부
	 */
	public boolean accept(List<Object> input){
		return accept((Object[]) input.toArray());
	}
	
	/**
	 * 오토마타에서 epsilon cycle들을 제거한다.
	 * @deprecated
	 * @return 자기 자신
	 */
	public Automata removeCycle(){
		for(Map.Entry<State, Map<Object, Set<State>>> stateInfo: this.transition.entrySet()){
			Map<Object, Set<State>> edges = stateInfo.getValue();
			Set<State> states = edges.get(Automata.epsilon);
			states.remove(stateInfo.getKey());
			
			if(states.isEmpty()){
				edges.remove(Automata.epsilon);
			}
		}
		return this;
	}
	
	/**
	 * 오토마타 합집합 연산을 수행한다.
	 * @param a
	 * @param b
	 * @return	두 오토마타의 합집합
	 */
	public static Automata union(Automata a, Automata b){
		Automata r = new Automata();
		
		r.addTransitions(a.transition).addTransitions(b.transition);
		r.addEdge(r.start, a.start, Automata.epsilon);
		r.addEdge(r.start, b.start, Automata.epsilon);
		r.ends.addAll(a.ends);
		r.ends.addAll(b.ends);
		
		return r.removeUnreachables();
	}

	/**
	 * 오토마타 교집합 연산을 수행한다.
	 * @param a
	 * @param b
	 * @return	두 오토마타의 교집합
	 */
	public static Automata intersect(Automata a, Automata b){
		Map<Pair<State, State>, State> map = new HashMap<Pair<State, State>, State>();
		
		Automata r = new Automata();
		map.put(new Pair<State, State>(a.start, b.start), r.start);
		
		Pair<State, State> fromPair, toPair;
		State from, to;
		
		Object aObj, bObj;
		Set<State> aStates, bStates;
		
		for(Map.Entry<State, Map<Object, Set<State>>> aEntry: a.transition.entrySet()){
			for(Map.Entry<State, Map<Object, Set<State>>> bEntry: b.transition.entrySet()){
				fromPair = new Pair<State, State>(aEntry.getKey(), bEntry.getKey());
				from = map.get(fromPair);
				if(from == null){
					from = r.addState();
					map.put(fromPair, from);
					
					if(a.ends.contains(fromPair.car()) && b.ends.contains(fromPair.cdr())){
						r.ends.add(from);
					}
				}
				
				for(Map.Entry<Object, Set<State>> aEdges: aEntry.getValue().entrySet()){
					aObj = aEdges.getKey(); aStates = aEdges.getValue();
					for(Map.Entry<Object, Set<State>> bEdges: bEntry.getValue().entrySet()){
						bObj = bEdges.getKey(); bStates = bEdges.getValue();
						
						if(aObj.equals(bObj)){
							for(State aState: aStates) for(State bState: bStates){
								toPair = new Pair<State, State>(aState, bState);
								to = map.get(toPair);
								if(to == null){
									to = r.addState();
									map.put(toPair, to);
									
									if(a.ends.contains(toPair.car()) && b.ends.contains(toPair.cdr())){
										r.ends.add(to);
									}
								}
								
								r.addEdge(from, to, aObj);
							}
						}
						if(Automata.isEpsilon(aObj)){
							for(State aState: aStates){
								toPair = new Pair<State, State>(aState, fromPair.cdr());
								to = map.get(toPair);
								if(to == null){
									to = r.addState();
									map.put(toPair, to);
									
									if(a.ends.contains(toPair.car()) && b.ends.contains(toPair.cdr())){
										r.ends.add(to);
									}
								}
								
								r.addEdge(from, to, aObj);
							}
						}
						if(Automata.isEpsilon(bObj)){
							for(State bState: bStates){
								toPair = new Pair<State, State>(fromPair.car(), bState);
								to = map.get(toPair);
								if(to == null){
									to = r.addState();
									map.put(toPair, to);

									if(a.ends.contains(toPair.car()) && b.ends.contains(toPair.cdr())){
										r.ends.add(to);
									}
								}
								
								r.addEdge(from, to, bObj);
							}
						}
					}
				}
			}
		}
		return r.removeUnreachables();
	}
	
	/**
	 * 주어진 두 오토마타를 잇는다.
	 * @param that
	 * @return	두 오토마타를 연결한 새 오토마타
	 */
	public static Automata concat(Automata a, Automata b){
		Automata r = new Automata(a.start, b.ends);
		
		r.addTransitions(a.transition).addTransitions(b.transition);
		
		for(State end: a.ends){
			r.addEdge(end, b.start, Automata.epsilon);
		}
		
		return r.removeUnreachables();
	}
	
	/**
	 * 주어진 두 오토마타가 같은 오토마타인지 확인한다.
	 * @param a
	 * @param b
	 * @return 두 오토마타가 같은지의 여부
	 */
	public static boolean equivalent(Automata a, Automata b){
		Automata da = a.minimalDFA(), db = b.minimalDFA();
		
		if(da.transition.size() != db.transition.size()) return false;
		if(da.ends.size() != db.ends.size()) return false;
		
		Map<State, State> map = new HashMap<State, State>();
		map.put(da.start, db.start);
		
		Set<State> openSet, nextSet;
		openSet = new HashSet<State>(); openSet.add(da.start);
		
		while(!openSet.isEmpty()){
			nextSet = new HashSet<State>();
			for(State aState: openSet){
				State bState = map.get(aState);
				Map<Object, Set<State>> aInfo = da.transition.get(aState);
				Map<Object, Set<State>> bInfo = db.transition.get(bState);
				
				if(aInfo.size() != bInfo.size()) return false;
				for(Map.Entry<Object, Set<State>> aEdges: aInfo.entrySet()){
					if(!bInfo.containsKey(aEdges.getKey())) return false;
					
					State aDst = aEdges.getValue().iterator().next();
					State bDst = bInfo.get(aEdges.getKey()).iterator().next();
					if(bDst == null) return false;
					
					if(map.containsKey(aDst)){
						if(!map.get(aDst).equals(bDst)) return false;
					}else{
						map.put(aDst, bDst);
						nextSet.add(aDst);
					}
				}
			}
			openSet = nextSet;
		}
		return true;
	}
	
	/**
	 * 주어진 Context Free Grammar에 따라 Automata에 간선을 추가한다.
	 * @param c Context Free Grammar
	 * @return 간선 추가 된 새 오토마타.
	 */
	public Automata addEdgeWithGrammar(CFG c) {
		Automata ret = new Automata(this);
		Map<State, Map<Object, Set<State>>> worklist = new HashMap<State, Map<Object, Set<State>>>();
		for(Map.Entry<State, Map<Object, Set<State>>> t: ret.transition.entrySet()) {
			if(!t.getValue().isEmpty()) worklist.put(t.getKey(), t.getValue());
		}
		for(Production p: c.getP()) {
			if(p.getTo().isEmpty()) {
				//epsilon production
				Set<State> states = ret.transition.keySet();
				for(State s: states) {
					Map<Object, Set<State>> v1 = ret.transition.get(s);
					
					if(v1.containsKey(p.getFrom())) {
						Set<State> v2 = v1.get(p.getFrom());
						if(!v2.contains(s)) {
							worklist.get(s).get(p.getFrom()).add(s);
						}
					}
					else {
						Map<Object, Set<State>> e = new HashMap<Object, Set<State>>();
						e.putAll(ret.transition.get(s));
						Set<State> newS = new HashSet<State>();
						newS.add(s);
						e.put(p.getFrom(), newS);
						worklist.put(s, e);
						ret.transition.put(s, e);
					}
				}
			}
		}

//		System.out.println(worklist);
		Set<State> keySet;
		while(!worklist.isEmpty()) {
			keySet = worklist.keySet();
			Object[] keyArray = keySet.toArray();
			State from = (State) keyArray[0];
			while(worklist.get(from) != null) {
				Map<State, Map<Object, Set<State>>> queue = new HashMap<State, Map<Object, Set<State>>>();
				for(Map.Entry<Object, Set<State>> e: worklist.get(from).entrySet()) {
//					System.out.println(ret.toString());
//					System.out.println(worklist);
//					System.out.println("Selected edge : " + from + "->" + e);
					Set<State> tos = e.getValue();
					for(Production p: c.getP()) {
//						System.out.println(p);
						if(p.getTo().contains(e.getKey())) {
							int front = p.getTo().indexOf(e.getKey());
							int back = p.getTo().size() - front - 1;
							Set<State> bin = new HashSet<State>();
							Set<State> currentF = new HashSet<State>();
							Set<State> currentB = new HashSet<State>();
							currentF.add(from);
							currentB.addAll(tos);
							for(int i = 0; i < front; i++) {
								for(State s: currentF) {
									bin.addAll(ret.reverseSearch(s, p.getTo().get(front-i-1)));
								}
								currentF.clear();
								currentF.addAll(bin);
								bin.clear();
							}
							for(int i = 0; i < back; i++) {
								for(State s: currentB) {
									for(Map.Entry<Object, Set<State>> e_: ret.transition.get(s).entrySet()) {
										if(e_.getKey().equals(p.getTo().get(front+i+1))) bin.addAll(e_.getValue());
									}
								}
								currentB.clear();
								currentB.addAll(bin);
								bin.clear();
							}
							
							for(State f: currentF) {
								assert(ret.transition.get(f) != null);
								if(ret.transition.get(f).containsKey(p.getFrom())) {
									for(State b: currentB) {
										if(!ret.transition.get(f).get(p.getFrom()).contains(b)) {
											ret.transition.get(f).get(p.getFrom()).add(b);
											if(queue.containsKey(f)) {
												if(queue.get(f).containsKey(p.getFrom())) {
													queue.get(f).get(p.getFrom()).add(b);
												}
												else {
													Set<State> in = new HashSet<State>();
													in.add(b);
													queue.get(f).put(p.getFrom(), in);
												}
											}
											else {
												Set<State> in = new HashSet<State>();
												in.add(b);
												Map<Object, Set<State>> inn = new HashMap<Object, Set<State>>();
												inn.put(p.getFrom(), in);
												queue.put(f, inn);
											}
										}
									}
								}
								else {
									Set<State> newS = new HashSet<State>();
									newS.addAll(currentB);
									ret.transition.get(f).put(p.getFrom(), newS);
									if(queue.containsKey(f)) {
										if(queue.get(f).containsKey(p.getFrom())) {
											queue.get(f).get(p.getFrom()).addAll(currentB);
										}
										else {
											Set<State> in = new HashSet<State>();
											in.addAll(currentB);
											queue.get(f).put(p.getFrom(), in);
										}
									}
									else {
										Set<State> in = new HashSet<State>();
										in.addAll(currentB);
										Map<Object, Set<State>> inn = new HashMap<Object, Set<State>>();
										inn.put(p.getFrom(), in);
										queue.put(f, inn);
									}
								}
							}
						}
					}
//					System.out.println(ret.toString());
				}
				worklist.remove(from);
				worklist.putAll(queue);
			}
			worklist.remove(from);
		}
		
//		System.out.println(ret);
		return ret.removeUnreachables();
	}
	
	protected Set<State> reverseSearch(State s) {
		Set<State> ret = new HashSet<State>();
		for(Map.Entry<State, Map<Object, Set<State>>> t: this.transition.entrySet()) {
			for(Set<State> stateSet: t.getValue().values()) {
				if(stateSet.contains(s)) ret.add(t.getKey());
			}
		}
		
		return ret;
	}
	
	protected Set<State> reverseSearch(State s, Object o) {
		Set<State> ret = new HashSet<State>();
		for(Map.Entry<State, Map<Object, Set<State>>> t: this.transition.entrySet()) {
			if(t.getValue().containsKey(o)) {
				if(t.getValue().get(o).contains(s)) ret.add(t.getKey());
			}
		}
		
		return ret;
	}
	
	@Override public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Automata\n");

		for(Map.Entry<State, Map<Object, Set<State>>> stateInfo: this.transition.entrySet()){
			buffer.append('\t').append(stateInfo.getKey().toString());
			if(this.start == stateInfo.getKey()) buffer.append(" ^");
			if(this.ends.contains(stateInfo.getKey())) buffer.append(" $");
			buffer.append(":\n");
			for(Map.Entry<Object, Set<State>> edges: stateInfo.getValue().entrySet()){
				buffer.append("\t\t").append(edges.getKey()).append(" =>");
				for(State state: edges.getValue()) buffer.append(' ').append(state.toString());
				buffer.append('\n');
			}
		}
		
		return buffer.append("]").toString();
	}
	
	private static boolean isEpsilon(Object object){
		return Automata.epsilon.equals(object);
	}
	
	/**
	 * 오토마타에서 epsilon 간선을 나타낸다.
	 * @author JiminP
	 */
	private static final class Epsilon {
		public Epsilon(){};
		
		@Override public int hashCode(){
			return 0xE2E22E2E;
		}
		@Override public boolean equals(Object obj){
			return obj != null && obj instanceof Automata.Epsilon;
		}
		@Override public String toString(){
			return "<epsilon>";
		}
	}
}
