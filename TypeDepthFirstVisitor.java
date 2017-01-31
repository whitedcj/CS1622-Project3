

import java.util.ArrayList;

import syntaxtree.*;
import visitor.TypeVisitor;

public class TypeDepthFirstVisitor implements TypeVisitor {

  private SymbolTable root;
  private SymbolTable cur;
  private SymbolTable st;
  
  public TypeDepthFirstVisitor(SymbolTable s) {
	  this.root = s;
	  this.cur = s;
	  this.st = s;
  }
  // MainClass m;
  // ClassDeclList cl;
  public Type visit(Program n) {
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.elementAt(i).accept(this);
    }
    return null;
  }
  
  // Identifier i1,i2;
  // Statement s;
  public Type visit(MainClass n) {
    n.i1.accept(this);
    n.i2.accept(this);
    n.s.accept(this);
    return null;
  }
  
  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclSimple n) {
	  
	  SymbolTable prev = cur;
	  cur = cur.getElement(n.i.s).enclosedScope;
    n.i.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }
    cur = prev;
    return null;
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclExtends n) {
	  SymbolTable prev = cur;
	  cur = cur.getElement(n.i.s).enclosedScope;
	  
	  n.i.accept(this);
    n.j.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }
    
    cur = prev;
    return null;
  }

  // Type t;
  // Identifier i;
  public Type visit(VarDecl n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public Type visit(MethodDecl n) {
	  
	  SymbolTable prev = cur;	
		SymbolTableEntry ste = this.getTableEntry(n.i.s);
		
		cur = ste.enclosedScope;
		
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
    n.e.accept(this);
    //SHOULD THIS BE CUR = PREV
    cur = prev;
    //originally prev = cur
    return null;
  }

  // Type t;
  // Identifier i;
  public Type visit(Formal n) {
    
    n.i.accept(this);
    return n.t.accept(this);
  }

  public Type visit(IntArrayType n) {
    return new IntArrayType();
  }

  public Type visit(BooleanType n) {
    return new BooleanType();
  }

  public Type visit(IntegerType n) {
    return new IntegerType();
  }

  // String s;
  public Type visit(IdentifierType n) {
    return new IdentifierType(n.s);
  }

  // StatementList sl;
  public Type visit(Block n) {
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    return null;
  }

  // Exp e;
  // Statement s1,s2;
  public Type visit(If n) {
    if (!(n.e.accept(this) instanceof BooleanType)) {
      System.out.println("Non-boolean expression used as the condition of if statement at line %d, character %d");
    }
    n.s1.accept(this);
    n.s2.accept(this);
    return null;
  }

  // Exp e;
  // Statement s;
  public Type visit(While n) {
    if (!(n.e.accept(this) instanceof BooleanType)) {
      System.out.println("Non-boolean expression used as the condition of while statement at line %d, character %d");
    }
    n.s.accept(this);
    return null;
  }

  // Exp e;
  public Type visit(Print n) {
    n.e.accept(this);
    //should i add one attempt to print a non int value check
    return null;
  }
  
  // Identifier i;  
  // Exp e;
  public Type visit(Assign n) {
    SymbolTable prev = cur;
	Type t1 = n.i.accept(this);
    Type t2 = n.e.accept(this);
    cur = prev;
    
	
    if (t1 instanceof IdentifierType) {
    	//find out what type it is
    	SymbolTableEntry entry = this.getTableEntry(n.i.s);
    	
    	
    	if (entry.defType.compareTo("int") == 0) {
    		t1 = new IntegerType();
    	} else if (entry.defType.compareTo("int[]") == 0) {
    		t1 = new IntArrayType();
    	} else if (entry.defType.compareTo("boolean") == 0) {
    		t1 = new BooleanType();
    	}else {
    		t1 = new IdentifierType(entry.returnType);
    	}
    	
    	if (n.i.s.compareTo("this") == 0) {
            System.out.println("Invalid l-value, \"this\" is a keyword, at line %d, character %d");
          }
          
          
            if (entry.entryType.compareTo("class") == 0) {
              System.out.println("Invalid l-value, " + n.i.s + " is a class, at line %d, character %d");
            } else if(entry.entryType.compareTo("function") == 0) {
              System.out.println("Invalid l-value, " + n.i.s + " is a function, at line %d, character %d");
            }
          
    	
    }
    if (t2 instanceof IdentifierType) {
    	//find out what type it is
    	SymbolTableEntry entry = null;
    	Boolean reset = true;
    	if (n.e instanceof IdentifierExp) {
    		entry = this.getTableEntry(((IdentifierExp)n.e).s);
    		if (entry.entryType.compareTo("class") == 0) {
		        System.out.println("Invalid r-value: " + ((IdentifierExp)n.e).s + " is a class, at line %d, character %d");
		      }
		      if (entry.entryType.compareTo("function") == 0) {
		        System.out.println("Invalid r-value: " + ((IdentifierExp)n.e).s + " is a function, at line %d, character %d");
		      }
    	} else if (n.e instanceof NewObject) {
    		entry = this.getTableEntry(((NewObject)n.e).i.s);
    	} else if (n.e instanceof This) {
    		entry = this.getTableEntry(((IdentifierType)t2).s);
    	} else if (n.e instanceof Call) {
    		reset = false;
    	}
    	if (reset == true) {
	    	if (entry.defType.compareTo("int") == 0) {
	    		t2 = new IntegerType();
	    	} else if (entry.defType.compareTo("int[]") == 0) {
	    		t2 = new IntArrayType();
	    	} else if (entry.defType.compareTo("boolean") == 0) {
	    		t2 = new BooleanType();
	    	}else {
	    		t2 = new IdentifierType(entry.returnType);
	    	}
    	}
    	
    }
    if (t1.getClass().getName().compareTo(t2.getClass().getName()) != 0) { 
    	System.out.println("Type mismatch during assignment at line %d, character %d");
    }   
    

    cur = prev;
    
    return t2;
  }

  // Identifier i;
  // Exp e1,e2;
  public Type visit(ArrayAssign n) {
    Type t1 = n.i.accept(this);
    Type t2 = n.e1.accept(this);
    Type t3 = n.e2.accept(this);
    

    if (!(t3 instanceof IntegerType || t3 instanceof IdentifierType)) { 
        System.out.println("Type mismatch during assignment at line %d, character %d");
    }   
    if (t3 instanceof IdentifierType) {
        
      if (st.getElement(((IdentifierExp)n.e2).s) != null) {
    	  SymbolTableEntry ste = st.getElement(((IdentifierExp)n.e2).s);
          if (ste.entryType.compareTo("class") == 0) {//maybe deftype
            System.out.println("Invalid r-value: " + n.i.s + " is a class, at line %d, character %d");
          }
          if (ste.entryType.compareTo("function") == 0) {
            System.out.println("Invalid r-value: " + n.i.s + " is a function, at line %d, character %d");
          }
      }
      

      if (n.i.s.compareTo("this") == 0) {
        System.out.println("Invalid l-value, \"this\" is a keyword, at line %d, character %d");
      }
      if (st.getElement(n.i.s) != null) {
        SymbolTableEntry ste = st.getElement(n.i.s);
        if (ste.entryType.compareTo("class") == 0) {
          System.out.println("Invalid l-value, " + n.i.s + " is a class, at line %d, character %d");
        } else if(ste.entryType.compareTo("function") == 0) {
          System.out.println("Invalid l-value, " + n.i.s + " is a function, at line %d, character %d");
        }
      } 

    }
    return t2;
  }

  // Exp e1,e2;
  public Type visit(And n) {
    Type t1 = n.e1.accept(this);
    Type t2 = n.e2.accept(this);

    if (!(t1 instanceof BooleanType) && !(t1 instanceof IdentifierType)) {
      System.out.println("Attempt to use boolean operator && on non-boolean operands at line %d, character %d");
    }
    if (!(t2 instanceof BooleanType) && !(t2 instanceof IdentifierType)) {
      System.out.println("Attempt to use boolean operator && on non-boolean operands at line %d, character %d");
    }
    if (t1 instanceof IdentifierType) {
        //check the table
      if (st.contains(((IdentifierExp)n.e1).s)) {
        if (st.getElement(((IdentifierExp)n.e1).s).defType.compareTo("boolean") != 0) {
          System.out.println("Attempt to use boolean operator && on non-boolean operands at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e1).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for && operator, at line %d, character %d");
        }
      }
    
    } 
    if (t2 instanceof IdentifierType) {
      if (st.contains(((IdentifierExp)n.e2).s)) {
        if (st.getElement(((IdentifierExp)n.e2).s).defType.compareTo("boolean") != 0) {
          System.out.println("Attempt to use boolean operator && on non-boolean operands at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e2).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for " + ((IdentifierExp)n.e2).s + " operator, at line %d, character %d");
        }
      }
    }

    
    return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(LessThan n) {
    Type t1 = n.e1.accept(this);
    Type t2 = n.e2.accept(this);
    
    
    if (!(t1 instanceof IntegerType) && !(t1 instanceof IdentifierType)) {
            System.out.println("Non-integer operand for operator < at line %d, character %d");
    }
    if (!(t2 instanceof IntegerType) && !(t2 instanceof IdentifierType)) {
          System.out.println("Non-integer operand for operator < at line %d, character %d");
    }
    if (t1 instanceof IdentifierType) {
        //check the table
      if (st.contains(((IdentifierExp)n.e1).s)) {
        if (st.getElement(((IdentifierExp)n.e1).s).defType.compareTo("int") != 0) {
          System.out.println("Non-integer operand for operator < at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e1).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for < operator, at line %d, character %d");
        }
      }
    
    } 
    if (t2 instanceof IdentifierType) {
      if (st.contains(((IdentifierExp)n.e2).s)) {
        if (st.getElement(((IdentifierExp)n.e2).s).defType.compareTo("int") != 0) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e1).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for < operator, at line %d, character %d");
        }
      }
    }

    return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(Plus n) {
       Type t1 = n.e1.accept(this);
    Type t2 = n.e2.accept(this);
    
    
    if (!(t1 instanceof IntegerType) && !(t1 instanceof IdentifierType)) {
            System.out.println("Non-integer operand for operator %c at line %d, character %d");
    }
    if (!(t2 instanceof IntegerType) && !(t2 instanceof IdentifierType)) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
    }
    if (t1 instanceof IdentifierType) {
        //check the table
      if (st.contains(((IdentifierExp)n.e1).s)) {
        if (st.getElement(((IdentifierExp)n.e1).s).defType.compareTo("int") != 0) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e1).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for " + ((IdentifierExp)n.e1).s + " operator, at line %d, character %d");
        }
      }
    
    } 
    if (t2 instanceof IdentifierType) {
      if (st.contains(((IdentifierExp)n.e2).s)) {
        if (st.getElement(((IdentifierExp)n.e2).s).defType.compareTo("int") != 0) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e1).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for " + ((IdentifierExp)n.e1).s + " operator, at line %d, character %d");
        }
      }
    }

    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Minus n) {
   Type t1 = n.e1.accept(this);
    Type t2 = n.e2.accept(this);
    
    
    if (!(t1 instanceof IntegerType) && !(t1 instanceof IdentifierType)) {
            System.out.println("Non-integer operand for operator %c at line %d, character %d");
    }
    if (!(t2 instanceof IntegerType) && !(t2 instanceof IdentifierType)) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
    }
    if (t1 instanceof IdentifierType) {
        //check the table
      if (st.contains(((IdentifierExp)n.e1).s)) {
        if (st.getElement(((IdentifierExp)n.e1).s).defType.compareTo("int") != 0) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e1).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for " + ((IdentifierExp)n.e1).s + " operator, at line %d, character %d");
        }
      }
    
    } 
    if (t2 instanceof IdentifierType) {
      if (st.contains(((IdentifierExp)n.e2).s)) {
        if (st.getElement(((IdentifierExp)n.e2).s).defType.compareTo("int") != 0) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e1).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for " + ((IdentifierExp)n.e1).s + " operator, at line %d, character %d");
        }
      }
    }

    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Times n) {
        Type t1 = n.e1.accept(this);
    Type t2 = n.e2.accept(this);
    
    
    if (!(t1 instanceof IntegerType) && !(t1 instanceof IdentifierType)) {
            System.out.println("Non-integer operand for operator %c at line %d, character %d");
    }
    if (!(t2 instanceof IntegerType) && !(t2 instanceof IdentifierType)) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
    }
    if (t1 instanceof IdentifierType) {
        //check the table
      if (st.contains(((IdentifierExp)n.e1).s)) {
        if (st.getElement(((IdentifierExp)n.e1).s).defType.compareTo("int") != 0) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e1).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for " + ((IdentifierExp)n.e1).s + " operator, at line %d, character %d");
        }
      }
    
    } 
    if (t2 instanceof IdentifierType) {
      if (st.contains(((IdentifierExp)n.e2).s)) {
        if (st.getElement(((IdentifierExp)n.e2).s).defType.compareTo("int") != 0) {
          System.out.println("Non-integer operand for operator %c at line %d, character %d");
        }
        if (st.getElement(((IdentifierExp)n.e1).s).entryType.compareTo("variable") != 0)
        {
          System.out.println("Invalid operands for " + ((IdentifierExp)n.e1).s + " operator, at line %d, character %d");
        }
      }
    }

    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(ArrayLookup n) {
    n.e1.accept(this);
    n.e2.accept(this);
    return new IntegerType();
  }

  // Exp e;
  public Type visit(ArrayLength n) {
    n.e.accept(this);
    return new IntegerType();
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public Type visit(Call n) {
	SymbolTable prev = cur;
	Type ret = null;
	if (n.e instanceof IdentifierExp) {
		String id = ((IdentifierExp)n.e).s;
		SymbolTableEntry entry = this.getTableEntry(id);
			
		if(entry != null){
			//SymbolTable prev = cur;
			cur = this.getTableByName(entry.defType);
				
			if(cur != null){
				SymbolTableEntry ste = this.getTableEntry(n.i.s);
				if (ste != null) {
			    	
			    	if (ste.entryType.compareTo("function") != 0) {
			    		System.out.println("Attempt to call a non-method at line %d, character %d");
			    	}
			    	
			    	if (ste.returnType.compareTo("int") == 0) {
			    		ret = new IntegerType();
			    	} else if (ste.returnType.compareTo("int[]") == 0) {
			    		ret = new IntArrayType();
			    	} else if (ste.returnType.compareTo("boolean") == 0) {
			    		ret = new BooleanType();
			    	}else {
			    		ret = new IdentifierType(ste.returnType);
			    	}
			    } 
			} 
			
		}
		
		
	} else if (n.e instanceof NewObject) {
    	//SymbolTable prev = cur;
    	String id = (((NewObject)n.e).i.s);
    	cur = this.getTableByName(id);
		SymbolTableEntry entry = this.getTableEntry(n.i.s);
		//this is the entry i want	
		
		if(entry != null){
			
			if (entry.entryType.compareTo("function") != 0) {
	    		System.out.println("Attempt to call a non-method at line %d, character %d");
	    	}
	    	
	    	if (entry.returnType.compareTo("int") == 0) {
	    		ret = new IntegerType();
	    	} else if (entry.returnType.compareTo("int[]") == 0) {
	    		ret = new IntArrayType();
	    	} else if (entry.returnType.compareTo("boolean") == 0) {
	    		ret = new BooleanType();
	    	}else {
	    		ret = new IdentifierType(entry.returnType);
	    	}
	    } else {
	    	System.out.println("it's null in call");
	    }
			
    } else if(n.e instanceof This) {
    	SymbolTableEntry entry = this.getTableEntry(n.i.s);
		//this is the entry i want	
		
		if(entry != null){
			
			if (entry.entryType.compareTo("function") != 0) {
	    		System.out.println("Attempt to call a non-method at line %d, character %d");
	    	}
	    	
	    	if (entry.returnType.compareTo("int") == 0) {
	    		ret = new IntegerType();
	    	} else if (entry.returnType.compareTo("int[]") == 0) {
	    		ret = new IntArrayType();
	    	} else if (entry.returnType.compareTo("boolean") == 0) {
	    		ret = new BooleanType();
	    	}else {
	    		ret = new IdentifierType(entry.returnType);
	    	}
	    } else {
	    	System.out.println("it's null in call");
	    }
    } else {
    	System.out.println("neither");
    	System.out.println("");
    }
	
	n.i.accept(this);
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.elementAt(i).accept(this);
    }
    
    cur = prev;
    return ret;
  }

  // int i;
  public Type visit(IntegerLiteral n) {
    return new IntegerType();
  }

  public Type visit(True n) {
    return new BooleanType();
  }

  public Type visit(False n) {
    return new BooleanType();
  }

  // String s;
  public Type visit(IdentifierExp n) {
    
	SymbolTableEntry ste = this.getTableEntry(n.s);
    if (ste != null) {
      if (ste.defType.compareTo("int") == 0) {
        return new IntegerType();
      } else if (ste.defType.compareTo("boolean") == 0) {
        return new BooleanType();
      } else if (ste.defType.compareTo("int[]") == 0) {
        return new IntArrayType();
      } else {
        return new IdentifierType(n.s);
      }
    } else {
    	//do i need stuff like this
    	//System.out.println(n.s + cur);
    	System.out.println("Symbol table doesn't have the identifier " + n.s);
    	return null;
    }
  }

  public Type visit(This n) {
    SymbolTable st = cur.getParent();
    if (st.getElement(cur.getName()).entryType.compareTo("function") == 0) {
    	return new IdentifierType(st.getName());
    } else if (st.getElement(cur.getName()).entryType.compareTo("class") == 0) {
    	return new IdentifierType(cur.getName());
    } else {
    	System.out.println("shit");
    }
    
    return null;
  }

  // Exp e;
  public Type visit(NewArray n) {
    return new IntArrayType();
  }

  // Identifier i;
  public Type visit(NewObject n) {
    return new IdentifierType(n.i.s);
  }

  // Exp e;
  public Type visit(Not n) {
    return n.e.accept(this);
  }

  // String s;
  public Type visit(Identifier n) {
    return new IdentifierType(n.s);
  }
  
//Searches entire symbol table tree to find 
  private SymbolTable getTableByName(String n){
	  return getTableByName(root, n);
  }
  
  private SymbolTable getTableByName(SymbolTable current, String n){
	  if(current.getName().compareTo(n) == 0){
		  return current;
	  }
	  
	  for(String key: current.getKeys()){
		  if(current.getElement(key).enclosedScope != null){
			  SymbolTable returned = getTableByName(current.getElement(key).enclosedScope, n);
			  if(returned != null)
				  return returned;
		  }
	  }
	  
	  return null;
  }
  
//Checks if an identifier is valid in the current scope
  public SymbolTableEntry getTableEntry(String identifier){
	SymbolTable temp = cur;
	
	//Search current symbol table, and all parents
	while(temp != null){
		if(temp.contains(identifier))
			return temp.getElement(identifier);
		temp = temp.getParent();
	}
	
	//Check base class, if necessary
	String name = cur.getName();
	temp = cur.getParent();
	
	if(temp == null)
		return null;
	
	//when would something's parent not be a class (or root)
	while(temp.getElement(name).entryType.compareTo("class") != 0)
	{
		name = temp.getName();
		temp = temp.getParent();
	}
	
	String baseClassName = "";
	if(temp != null)
		baseClassName = temp.getElement(name).baseClass;
	
	while(baseClassName.compareTo("") != 0){
		SymbolTable baseClassTable = getTableByName(baseClassName);
		if(baseClassTable.contains(identifier))
			return baseClassTable.getElement(identifier);
		
		baseClassName = baseClassTable.getParent().getElement(baseClassName).baseClass;
	}
		
	return null;
  }

}
