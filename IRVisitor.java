import syntaxtree.*;
import visitor.Visitor;

import java.util.*;

public class IRVisitor implements Visitor {

	public ArrayList<Quadruple> IR;
	private SymbolTable st;
	private int tempnum;				//for t0, t1, etc. this is used if the "result" of the quadruple isn't in the symbol table or is impossible to know at that time
	private int labelnum;				//when creating each quadruple, increment this
	
	public IRVisitor() {
	
	}
	
	public IRVisitor(SymbolTable st) {
		IR = new ArrayList<Quadruple>();
		this.st = st;
		this.tempnum = 0;
		this.labelnum = 0;
	}
	
	public ArrayList<Quadruple> getIR(){
		return IR;
	}
	
	// MainClass m;
	// ClassDeclList cl;
	public void visit(Program n) {
	    n.m.accept(this);
	    for ( int i = 0; i < n.cl.size(); i++ ) {
	        n.cl.elementAt(i).accept(this);
	    }
	}
	
	// Identifier i1,i2;
	// Statement s;
	public void visit(MainClass n) {
	    n.s.accept(this);
	}
	
	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclSimple n) {
	    for ( int i = 0; i < n.vl.size(); i++ ) {
	        n.vl.elementAt(i).accept(this);
	    }
	    for ( int i = 0; i < n.ml.size(); i++ ) {
	        n.ml.elementAt(i).accept(this);
	    }
	}
	
	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclExtends n) {
	    for ( int i = 0; i < n.vl.size(); i++ ) {
	        n.vl.elementAt(i).accept(this);
	    }
	    for ( int i = 0; i < n.ml.size(); i++ ) {
	        n.ml.elementAt(i).accept(this);
	    }
	}
	

	
	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public void visit(MethodDecl n) {
		String op = "RETURN";
		String a1;
		SymbolTableEntry arg1 = null;	
		int label;
		
		n.t.accept(this);
	    n.i.accept(this);
	    for ( int i = 0; i < n.fl.size(); i++ ) {
	        n.fl.elementAt(i).accept(this);
	    }
	    for ( int i = 0; i < n.vl.size(); i++ ) {
	        n.vl.elementAt(i).accept(this);
	    }
	    for ( int i = 0; i < n.sl.size(); i++ ) {
	        n.sl.elementAt(i).accept(this);
	    }
	    
	    if (n.e instanceof IntegerLiteral) {
	    	a1 = Integer.toString(((IntegerLiteral)n.e).i);
	    } else if (n.e instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e).s;
			arg1 = st.getElement(a1);
	    } else if (n.e instanceof True) {
	    	a1 = "1";
	    } else if (n.e instanceof False) {
	    	a1 = "0";
	    } else {
	    	n.e.accept(this);
		    a1 = "t" + (tempnum - 1);
	    }
	    
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, "", "", label);
		if (arg1 != null) q.setSTE(1,  arg1);
		IR.add(q);
	}
	
	// StatementList sl;
	public void visit(Block n) {		
	    for ( int i = 0; i < n.sl.size(); i++ ) {
	        n.sl.elementAt(i).accept(this);
	    }
	}
	
	// Exp e;
	// Statement s1,s2;
	public void visit(If n) {			
		String op = "IFFALSE";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2;
		SymbolTableEntry res;
		String result;
		int label;
		
		if (n.e instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e).s;
			arg1 = st.getElement(a1);
	    } else if (n.e instanceof True) {
	    	a1 = "1";
	    } else if (n.e instanceof False) {
	    	a1 = "0";
	    } else {
	    	n.e.accept(this);
	    	a1 = "t" + (tempnum - 1);
	    }
		
		
		
		label = labelnum++;		//label of the iffalse line
		Quadruple q = new Quadruple(op, a1, "", "", label);
		if (arg1 != null) q.setSTE(1, arg1);
		
		Quadruple fake = new Quadruple("FAKE", "FAKE", "FAKE", "FAKE", -1);
		IR.add(fake);
		
		n.s1.accept(this);
		
		//now a goto
		label = labelnum++;
		Quadruple q1 = new Quadruple("GOTO", "", "", "", label);
		
		//this label goes in the iffalse line
		q.a2 = Integer.toString(labelnum);
		//now find fake and replace it
		int index = IR.indexOf(fake);
		IR.add(index, q);
		IR.remove(fake);
		
		//add the fake one as a placeholder for q1
		IR.add(fake);
		
	    n.s2.accept(this);
	    
	    //now here is the value that the second goto needs
	    q1.a1 = Integer.toString(labelnum);	    
		//now find fake and replace it
		int index2 = IR.indexOf(fake);
		IR.add(index2, q1);
		IR.remove(fake);
	    
	}

	// Exp e;
	// Statement s;
	public void visit(While n) {		
		String op = "IFFALSE";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2;
		SymbolTableEntry res;
		String result;
		int label;
		
		if (n.e instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e).s;
			arg1 = st.getElement(a1);
	    } else if (n.e instanceof True) {
	    	a1 = "1";
	    } else if (n.e instanceof False) {
	    	a1 = "0";
	    } else {
	    	n.e.accept(this);
	    	a1 = "t" + (tempnum - 1);
	    }
	    
		label = labelnum++;		//label of the iffalse line
		Quadruple q = new Quadruple(op, a1, "", "", label);
		if (arg1 != null) q.setSTE(1, arg1);
		
		Quadruple fake = new Quadruple("FAKE", "FAKE", "FAKE", "FAKE", -1);
		IR.add(fake);
		
		n.s.accept(this);
		
		//now a goto
		Quadruple q1 = new Quadruple("GOTO", Integer.toString(label), "", "", labelnum++);
		IR.add(q1);
		
		q.a2 = Integer.toString(labelnum);
		int index = IR.indexOf(fake);
		IR.add(index, q);
		IR.remove(fake);
	    
	}

	// Exp e;
	public void visit(Print n) {		
		String op = "PARAM";
		String result;
		String a1;
		SymbolTableEntry arg1 = null;	
		int label;
		
	    if (n.e instanceof IntegerLiteral) {
	    	a1 = Integer.toString(((IntegerLiteral)n.e).i);
	    } else if (n.e instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e).s;
			arg1 = st.getElement(a1);
	    } else {
	    	n.e.accept(this);
		    a1 = "t" + (tempnum - 1);
	    }
	    
	    label = labelnum++;
	    Quadruple qp = new Quadruple(op, "System.out.println", "", "", label);
	    IR.add(qp);

		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, "", "", label);
		if (arg1 != null) q.setSTE(1,  arg1);
		IR.add(q);
	    
		result = "t" + tempnum;
		tempnum++;

	    label = labelnum++;
		Quadruple call = new Quadruple("CALL", "System.out.println", "2", result, label);
		IR.add(call);

	}	
	
	// Identifier i;
	// Exp e;
	public void visit(Assign n) {		    
		String op = "COPY";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2;
		SymbolTableEntry res;
		String result;
		int label;
		
		result = n.i.s;
		res = st.getElement(n.i.s);
		
		if (n.e instanceof IntegerLiteral) {
			a1 = Integer.toString(((IntegerLiteral)n.e).i);
		} else if (n.e instanceof IdentifierExp) { 
			a1 = ((IdentifierExp)n.e).s;
			arg1 = st.getElement(a1);
		} else if (n.e instanceof True) {
			a1 = "1";
		} else if (n.e instanceof False) {
			a1 = "0";
		} else {
			n.e.accept(this);
			a1 = "t" + (tempnum - 1);
		}
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, "", result, label);
		q.setSTE(0, res);
		if (arg1 != null) q.setSTE(1,  arg1);
		IR.add(q);
	}
	
	// Identifier i;
    //  Exp e1,e2;
	public void visit(ArrayAssign n) {			
		String op = "ARRAYASSIGN";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2 = null;
		SymbolTableEntry res;
		String result;
		int label;
		  
		result = n.i.s;
		res = st.getElement(n.i.s);
		
		if (n.e1 instanceof IntegerLiteral) {
			a1 = Integer.toString(((IntegerLiteral)n.e1).i);
		} else if (n.e1 instanceof IdentifierExp) {
			a1 = ((IdentifierExp)n.e1).s;
			arg1 = st.getElement(a1);
		} else {
			n.e1.accept(this);
			a1 = "t" + (tempnum - 1);
		}
		
		if (n.e2 instanceof IntegerLiteral) {
			a2 = Integer.toString(((IntegerLiteral)n.e2).i);
		} else if (n.e2 instanceof IdentifierExp) {
			a2 = ((IdentifierExp)n.e2).s;
			arg2 = st.getElement(a2);
		} else {
			n.e2.accept(this);
			a2 = "t" + (tempnum - 1);
		}
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, a2, result, label);
		q.setSTE(0, res);
		if (arg1 != null) q.setSTE(1, arg1);
		if (arg2 != null) q.setSTE(2, arg2);
		IR.add(q);
	}

	// Exp e1,e2;
	public void visit(And n) {		
		String op = "&&";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2 = null;
		String result;
		int label;
		
		if (n.e1 instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e1).s;
			arg1 = st.getElement(a1);
	    } else if (n.e1 instanceof True) {
	    	a1 = "1";					
	    } else if (n.e1 instanceof False) {
	    	a1 = "0";
	    } else {
	    	n.e1.accept(this);
	    	a1 = "t" + (tempnum - 1);
	    }
	    
		if (n.e2 instanceof IdentifierExp) {
	    	a2 = ((IdentifierExp)n.e2).s;
			arg2 = st.getElement(a2);
	    } else if (n.e2 instanceof True) {
	    	a2 = "1";
	    } else if (n.e1 instanceof False) {
	    	a2 = "0";
	    } else {
	    	n.e2.accept(this);
	    	a2 = "t" + (tempnum - 1);
	    }
		
		result = "t" + tempnum;
		tempnum++;
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, a2, result, label);
		if (arg1 != null) q.setSTE(1, arg1);
		if (arg2 != null) q.setSTE(2, arg2);
		IR.add(q);
			    
	}
	
	// Exp e1,e2;
	public void visit(LessThan n) {
		String op = "<";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2 = null;
		String result;
		int label;
		
		if (n.e1 instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e1).s;
			arg1 = st.getElement(a1);
	    } else if (n.e1 instanceof IntegerLiteral) {
			a1 = Integer.toString(((IntegerLiteral)n.e1).i);
	    } else {
	    	n.e1.accept(this);
	    	a1 = "t" + (tempnum - 1);
	    }
	    
		if (n.e2 instanceof IdentifierExp) {
	    	a2 = ((IdentifierExp)n.e2).s;
			arg2 = st.getElement(a2);
	    } else if (n.e2 instanceof IntegerLiteral) {
			a2 = Integer.toString(((IntegerLiteral)n.e2).i);
	    } else {
	    	n.e2.accept(this);
	    	a2 = "t" + (tempnum - 1);
	    }
		
		result = "t" + tempnum;
		tempnum++;
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, a2, result, label);
		if (arg1 != null) q.setSTE(1, arg1);
		if (arg2 != null) q.setSTE(2, arg2);
		IR.add(q);
	}
	
	// Exp e1,e2;
	public void visit(Plus n) {
		String op = "+";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2 = null;
		String result;
		int label;
		
		if (n.e1 instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e1).s;
			arg1 = st.getElement(a1);
	    } else if (n.e1 instanceof IntegerLiteral) {
			a1 = Integer.toString(((IntegerLiteral)n.e1).i);
	    } else {
	    	n.e1.accept(this);
	    	a1 = "t" + (tempnum - 1);
	    }
	    
		if (n.e2 instanceof IdentifierExp) {
	    	a2 = ((IdentifierExp)n.e2).s;
			arg2 = st.getElement(a2);
	    } else if (n.e2 instanceof IntegerLiteral) {
			a2 = Integer.toString(((IntegerLiteral)n.e2).i);
	    } else {
	    	n.e2.accept(this);
	    	a2 = "t" + (tempnum - 1);
	    }
		
		result = "t" + tempnum;
		tempnum++;
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, a2, result, label);
		if (arg1 != null) q.setSTE(1, arg1);
		if (arg2 != null) q.setSTE(2, arg2);
		IR.add(q);
	}

	// Exp e1,e2;
	public void visit(Minus n) {
		String op = "-";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2 = null;
		String result;
		int label;
		
		if (n.e1 instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e1).s;
			arg1 = st.getElement(a1);
	    } else if (n.e1 instanceof IntegerLiteral) {
			a1 = Integer.toString(((IntegerLiteral)n.e1).i);
	    } else {
	    	n.e1.accept(this);
	    	a1 = "t" + (tempnum - 1);
	    }
	    
		if (n.e2 instanceof IdentifierExp) {
	    	a2 = ((IdentifierExp)n.e2).s;
			arg2 = st.getElement(a2);
	    } else if (n.e2 instanceof IntegerLiteral) {
			a2 = Integer.toString(((IntegerLiteral)n.e2).i);
	    } else {
	    	n.e2.accept(this);
	    	a2 = "t" + (tempnum - 1);
	    }
		
		result = "t" + tempnum;
		tempnum++;
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, a2, result, label);
		if (arg1 != null) q.setSTE(1, arg1);
		if (arg2 != null) q.setSTE(2, arg2);
		IR.add(q);
	}

	// Exp e1,e2;
	public void visit(Times n) {
		String op = "*";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2 = null;
		String result;
		int label;
		
		if (n.e1 instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e1).s;
			arg1 = st.getElement(a1);
	    } else if (n.e1 instanceof IntegerLiteral) {
			a1 = Integer.toString(((IntegerLiteral)n.e1).i);
	    } else {
	    	n.e1.accept(this);
	    	a1 = "t" + (tempnum - 1);
	    }
	    
		if (n.e2 instanceof IdentifierExp) {
	    	a2 = ((IdentifierExp)n.e2).s;
			arg2 = st.getElement(a2);
	    } else if (n.e2 instanceof IntegerLiteral) {
			a2 = Integer.toString(((IntegerLiteral)n.e2).i);
	    } else {
	    	n.e2.accept(this);
	    	a2 = "t" + (tempnum - 1);
	    }
		
		result = "t" + tempnum;
		tempnum++;
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, a2, result, label);
		if (arg1 != null) q.setSTE(1, arg1);
		if (arg2 != null) q.setSTE(2, arg2);
		IR.add(q);
	}
	
	// Exp e1,e2;
	public void visit(ArrayLookup n) {
		String op = "ARRAYLOOKUP";
		String a1;
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2 = null;
		String result;
		int label;
		
		if (n.e1 instanceof IdentifierExp) {
			a1 = ((IdentifierExp)n.e1).s;
			arg1 = st.getElement(a1);
		} else {
			n.e1.accept(this);
			a1 = "t" + (tempnum - 1);
		}
		
		if (n.e2 instanceof IntegerLiteral) {
			a2 = Integer.toString(((IntegerLiteral)n.e2).i);
		} else if (n.e2 instanceof IdentifierExp) {
			a2 = ((IdentifierExp)n.e2).s;
			arg2 = st.getElement(a2);
		} else {
			n.e2.accept(this);
			a2 = "t" + (tempnum - 1);
		}
		
		result = "t" + tempnum;
		tempnum++;
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, a2, result, label);
		if (arg1 != null) q.setSTE(1, arg1);
		if (arg2 != null) q.setSTE(2, arg2);
		IR.add(q);
	}

	// Exp e;
	public void visit(ArrayLength n) {		
		String op = "length";
		String a1;
		SymbolTableEntry arg1 = null;	
		String result;
		int label;
		
		if (n.e instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e).s;
			arg1 = st.getElement(a1);
		} else {
	    	n.e.accept(this);
	    	a1 = "t" + (tempnum - 1);
		}
		
		result = "t" + tempnum;
		tempnum++;
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, "", result, label);
		if (arg1 != null) q.setSTE(1, arg1);
		IR.add(q);
	    
	}

	  // Exp e;
	  // Identifier i;
	  // ExpList el;
	  public void visit(Call n) {
		String op = "CALL";
		String a1 = "";
		String a2;
		SymbolTableEntry arg1 = null;	
		SymbolTableEntry arg2 = null;
		String result;
		int label;
		

		if (n.e instanceof IdentifierExp) {
			a1 = ((IdentifierExp)n.e).s;
			arg1 = st.getElement(a1);
		} else if (n.e instanceof This) {
			a1 = "this";
		} else if (n.e instanceof NewObject) {
			n.e.accept(this);
			a1 = "t" + (tempnum - 1);
		} else {
			System.err.println("THE LHS OF THE DOT IN A CALL WAS NOT AN IDENTIFIER");
		}

		label = labelnum++;
		Quadruple q = new Quadruple("PARAM", a1, "", "", label);
		if (arg1 != null) q.setSTE(1, arg1);
		IR.add(q);

	    for ( int i = 0; i < n.el.size(); i++ ) {
	        
	    	if (n.el.elementAt(i) instanceof IntegerLiteral) {
	    		a1 = Integer.toString(((IntegerLiteral)n.el.elementAt(i)).i);
	    	} else if (n.el.elementAt(i) instanceof IdentifierExp) {
				a1 = ((IdentifierExp)n.el.elementAt(i)).s;
				arg1 = st.getElement(a1);
	    	} else if (n.el.elementAt(i) instanceof True) {
	    		a1 = "1";
	    	} else if (n.el.elementAt(i) instanceof False) {
	    		a1 = "0";
	    	} else {
	    	    n.el.elementAt(i).accept(this);
	    	    a1 = "t" + (tempnum - 1);
	    	}
	        label = labelnum++;
	        Quadruple q1 = new Quadruple("PARAM", a1, "", "", label);
	        IR.add(q1);
	        
	    }

	    result = "t" + tempnum;
	    tempnum++;

	    label = labelnum++;
    	Quadruple call = new Quadruple("CALL", n.i.s, Integer.toString(n.el.size() + 1), result, label);
    	IR.add(call);

	  }



	  // Exp e;
	  public void visit(NewArray n) {
	    //x = new TYPE, SIZE
	    String op = "NEWARRAY";
		String a1;
		SymbolTableEntry arg1 = null;	
		String result;
		int label;
		
		if (n.e instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e).s;
			arg1 = st.getElement(a1);
		} else if (n.e instanceof IntegerLiteral) {
			a1 = Integer.toString(((IntegerLiteral)n.e).i);
		} else {
	    	n.e.accept(this);
	    	a1 = "t" + (tempnum - 1);
		}
		
		result = "t" + tempnum;
		tempnum++;
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, "", result, label);
		if (arg1 != null) q.setSTE(1, arg1);
		IR.add(q);
	    
	  }

	  // Identifier i;
	  public void visit(NewObject n) {
	    String result;
	    int label;

	    result = "t" + tempnum;
	    tempnum++;

	    label = labelnum++;
	    Quadruple q = new Quadruple("NEW", n.i.s, "", result, label);
	    IR.add(q);
	    
	  }

	// Exp e;
	public void visit(Not n) {
		String op = "NOT";
		String a1;
		SymbolTableEntry arg1 = null;	
		String result;
		int label;
		
		if (n.e instanceof IdentifierExp) {
	    	a1 = ((IdentifierExp)n.e).s;
			arg1 = st.getElement(a1);
		} else if (n.e instanceof True) {
			a1 = "1";
		} else if (n.e instanceof False) {
			a1 = "0";
		} else {
	    	n.e.accept(this);
	    	a1 = "t" + (tempnum - 1);
		}
		
		result = "t" + tempnum;
		tempnum++;
		
		label = labelnum++;
		Quadruple q = new Quadruple(op, a1, "", result, label);
		if (arg1 != null) q.setSTE(1, arg1);
		IR.add(q);
	}

	// String s;
	public void visit(Identifier n) {
	}
	
	// int i;
	public void visit(IntegerLiteral n) {
	}

	public void visit(True n) {
	}

	public void visit(False n) {
	}

	// String s;
	public void visit(IdentifierExp n) {
	}

	public void visit(This n) {
	}
	  
	// Type t;
	// Identifier i;
	public void visit(Formal n) {
	}
		
	public void visit(IntArrayType n) {
	}

	public void visit(BooleanType n) {
	}

	public void visit(IntegerType n) {
	}

	// String s;
	public void visit(IdentifierType n) {
	}
	
	// Type t;
	// Identifier i;
	public void visit(VarDecl n) {
	}
}
