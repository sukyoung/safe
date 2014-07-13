/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.concolic;

import java.util.*;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.Pair;
import com.microsoft.z3.*;

import kr.ac.kaist.jsaf.scala_src.useful.Options.*;

public final class Z3 {
    @SuppressWarnings("serial")
    class TestFailedException extends Exception
    {
        public TestFailedException()
        {
            super("Check FAILED");
        }
    };
	
	boolean debug = false;
	
	public HashMap<String, Integer> ConstraintSolver(Context ctx, List<ConstraintForm> conslist, Integer inum, Map<Integer, Pair<String, List<Pair<String, String>>>> objects, List<Pair<String, String>> thisObject) throws Z3Exception, TestFailedException 
	{
		if (debug) 
			System.out.println("ConstraintSolver");

		Map<String, IntExpr> exprMap = new HashMap<String, IntExpr>();
		Solver solver = ctx.mkSolver();
		while (!conslist.isEmpty()) {
			ConstraintForm constraint = conslist.remove(0);
			if (constraint.getJavaOp().isSome()) {
				String op = constraint.getJavaOp().unwrap();
				//String lhs = constraint.getLhs();
				String lhs = constraint.getLhs().getValue();
				exprMap.put(lhs, ctx.mkIntConst(lhs));
				if (constraint.getJavaRhs().isSome()) {
					ConstraintForm c = constraint.getJavaRhs().unwrap();
					//String rhs = c.getLhs();
					String rhs = c.getLhs().getValue();
					if (rhs.contains("s") || rhs.contains("i") || rhs.contains("this"))
						exprMap.put(rhs, ctx.mkIntConst(rhs));
					else
						exprMap.put(rhs, ctx.mkInt(Integer.parseInt(rhs)));
					
					switch (op.charAt(0)) {
						case '=' :
							if (op.length() > 1 && op.charAt(1) == '=') 
								solver.assert_(ctx.mkEq(exprMap.get(lhs), exprMap.get(rhs)));
							else {
								if (c.getJavaOp().isSome()) {
									if (c.getJavaRhs().isSome()) {
										//String v = c.getJavaRhs().unwrap().getLhs();	
										String v = c.getJavaRhs().unwrap().getLhs().getValue();	
										if (v.contains("s") || v.contains("i") || v.contains("this"))
											exprMap.put(v, ctx.mkIntConst(v));
										else
											exprMap.put(v, ctx.mkInt(Integer.parseInt(v)));
										
										String constraint_op = c.getJavaOp().unwrap(); 
										switch (constraint_op.charAt(0)) {
											case '+':
												solver.assert_(ctx.mkEq(exprMap.get(lhs), ctx.mkAdd(new ArithExpr[] { exprMap.get(rhs), exprMap.get(v)})));
												break;
											case '-':	
												solver.assert_(ctx.mkEq(exprMap.get(lhs), ctx.mkSub(new ArithExpr[] { exprMap.get(rhs), exprMap.get(v)})));
												break;
											case '*':
												solver.assert_(ctx.mkEq(exprMap.get(lhs), ctx.mkMul(new ArithExpr[] { exprMap.get(rhs), exprMap.get(v)})));
												break;
											case '/':
												solver.assert_(ctx.mkEq(exprMap.get(lhs), ctx.mkDiv((ArithExpr) exprMap.get(rhs), (ArithExpr) exprMap.get(v))));
												break;
											case '&':
												BitVecExpr x = ctx.mkInt2BV(1, exprMap.get(rhs));
												BitVecExpr y = ctx.mkInt2BV(1, exprMap.get(v));
												solver.assert_(ctx.mkEq(exprMap.get(lhs), ctx.mkBV2Int(ctx.mkBVAND(x, y), false)));
												//solver.assert_(ctx.mkEq(exprMap.get(lhs), ctx.mkBV2Int(ctx.mkBVAND(ctx.mkInt2BV(32, exprMap.get(rhs)), ctx.mkInt2BV(32, exprMap.get(v))), true)));
										
												break;
											/* Boolean Expression */
											case '!':
												solver.assert_((BoolExpr) ctx.mkITE(ctx.mkDistinct(exprMap.get(rhs), exprMap.get(v)), ctx.mkDistinct(exprMap.get(lhs), ctx.mkInt(0)), ctx.mkEq(exprMap.get(lhs), ctx.mkInt(0)))); 
												break;
											case '=':
												solver.assert_((BoolExpr) ctx.mkITE(ctx.mkEq(exprMap.get(rhs), exprMap.get(v)), ctx.mkDistinct(exprMap.get(lhs), ctx.mkInt(0)), ctx.mkEq(exprMap.get(lhs), ctx.mkInt(0)))); 
												//solver.assert_(ctx.mkEq(exprMap.get(lhs), ctx.mkEq(exprMap.get(rhs), exprMap.get(v))));
												break;
											case '>':
												BoolExpr condition = ((constraint_op.length() > 1 && constraint_op.charAt(1) == '=')? 
														ctx.mkGe(exprMap.get(rhs), exprMap.get(v)) : 
														ctx.mkGt(exprMap.get(rhs), exprMap.get(v))); 
												solver.assert_((BoolExpr) ctx.mkITE(condition, ctx.mkDistinct(exprMap.get(lhs), ctx.mkInt(0)), ctx.mkEq(exprMap.get(lhs), ctx.mkInt(0)))); 
												break;
											case '<':
												condition = ((constraint_op.length() > 1 && constraint_op.charAt(1) == '=')? 
														ctx.mkLe(exprMap.get(rhs), exprMap.get(v)) : 
														ctx.mkLt(exprMap.get(rhs), exprMap.get(v))); 
												solver.assert_((BoolExpr) ctx.mkITE(condition, ctx.mkDistinct(exprMap.get(lhs), ctx.mkInt(0)), ctx.mkEq(exprMap.get(lhs), ctx.mkInt(0)))); 
												break;
											default:
												System.out.println("Not yet supported");
												throw new TestFailedException();
										}
									}
									else {
										System.out.println("Wrong constraint form" + c);
										throw new TestFailedException();
									}
								}
								else
									solver.assert_(ctx.mkEq(exprMap.get(lhs), exprMap.get(rhs)));
								
							}
							break;
						case '<':
							if (op.length() > 1 && op.charAt(1) == '=')
								solver.assert_(ctx.mkLe(exprMap.get(lhs), exprMap.get(rhs)));
							else 
								solver.assert_(ctx.mkLt(exprMap.get(lhs), exprMap.get(rhs)));
							break;
						case '>':
							if (op.length() > 1 && op.charAt(1) == '=')
								solver.assert_(ctx.mkGe(exprMap.get(lhs), exprMap.get(rhs)));
							else 
								solver.assert_(ctx.mkGt(exprMap.get(lhs), exprMap.get(rhs)));
							break;
						case '!':
							if (op.length() > 1 && op.charAt(1) == '=')
								solver.assert_(ctx.mkDistinct(new Expr[] { exprMap.get(lhs), exprMap.get(rhs)}));
							else {
							   	System.out.println("Wrong constraint form" + op);   	   
								throw new TestFailedException();
							}
							break;
						default:
							System.out.println("Not yet supported");
							throw new TestFailedException();
					}		
				}
				else { 	
					System.out.println("Wrong constraint form" + constraint);
					throw new TestFailedException();
				}
			}
			else { 	
				System.out.println("Wrong constraint form" + constraint);
				throw new TestFailedException();
			}
		}
		
		Model model = null;
		if (Status.SATISFIABLE == solver.check()) {
			model = solver.getModel();
			if (debug) {
				System.out.println("Solver = " + solver);
				System.out.println("Model = " + model);
			}
			// Add this object to result
			HashMap<String, Integer> result = new HashMap<String, Integer>(); 
			if (exprMap.containsKey("this")) 
				result.put("this", Integer.parseInt(model.getConstInterp(exprMap.get("this")).toString()));
			for (int j=0; j<thisObject.size(); j++) 
				result.put("this."+thisObject.get(j).first(), Integer.parseInt(model.getConstInterp(exprMap.get("this."+thisObject.get(j).first())).toString()));

			for (int i=0; i<inum; i++) {
				if (exprMap.containsKey("i"+i)) 
					result.put("i"+i, Integer.parseInt(model.getConstInterp(exprMap.get("i"+i)).toString()));
				if (objects.containsKey(i)) {
					if (objects.get(i).first() == "Array") {
						String length = objects.get(i).second().get(0).first();
						for (int j=0; j<Integer.parseInt(length); j++)
							result.put("i"+i+"."+Integer.toString(j), Integer.parseInt(model.getConstInterp(exprMap.get("i"+i+"."+Integer.toString(j))).toString()));
					}
					else {
						List<Pair<String, String>> properties = objects.get(i).second();
						for (int j=0; j<properties.size(); j++) 
							result.put("i"+i+"."+properties.get(j).first(), Integer.parseInt(model.getConstInterp(exprMap.get("i"+i+"."+properties.get(j).first())).toString()));
					}
				}
			}
			return result;			
		}
		else {
			System.out.println("BUG, the constraints are satisfiable.");
			throw new TestFailedException();
		}
	}	

	public Option<HashMap<String, Integer>> solve(List<ConstraintForm> constraints, Integer inum, Option<Map<Integer, Pair<String, List<Pair<String, String>>>>> objects, List<Pair<String, String>> thisObject) {
		try {
			HashMap<String, String> cfg = new HashMap<String, String>();
			cfg.put("model", "true");
			Context ctx = new Context(cfg);
			if (!constraints.isEmpty())
				return Option.<HashMap<String, Integer>>some(this.ConstraintSolver(ctx, constraints, inum, objects.unwrap(), thisObject));
			else
				return Option.<HashMap<String, Integer>>none();
		} catch (Z3Exception ex) {
            System.out.println("TEST CASE FAILED: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(System.out);
			return Option.<HashMap<String, Integer>>none();
        } catch (Exception ex) {
            System.out.println("Unknown Exception: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(System.out);
			return Option.<HashMap<String, Integer>>none();
        }
	}
}
