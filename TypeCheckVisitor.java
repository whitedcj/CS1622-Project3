import syntaxtree.*;
import visitor.Visitor;

public class TypeCheckVisitor implements Visitor {

  private SymbolTable root;
  private SymbolTable cur;
  
  public TypeCheckVisitor(SymbolTable table){
	this.root = table;
	this.cur = root;
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
	
	if(cur == null)
		return null;
	
	//Check base class, if necessary
	String name = cur.getName();
	temp = cur.getParent();
	
	if(temp == null)
		return null;
	
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

  // Hashtable m;
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
	  
    n.i1.accept(this);
    n.i2.accept(this);
    n.s.accept(this);
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
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
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
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
  }

  // Type t;
  // Identifier i;
  public void visit(VarDecl n) {
    n.t.accept(this);
    n.i.accept(this);
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public void visit(MethodDecl n) { 
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
    
    prev = cur;
  }

  // Type t;
  // Identifier i;
  public void visit(Formal n) {
    n.t.accept(this);
    n.i.accept(this);
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

  // StatementList sl;
  public void visit(Block n) {
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
  }

  // Exp e;
  // Statement s1,s2;
  public void visit(If n) {
    n.e.accept(this);
    n.s1.accept(this);
    n.s2.accept(this);
  }

  // Exp e;
  // Statement s;
  public void visit(While n) {
    n.e.accept(this);
    n.s.accept(this);
  }

  // Exp e;
  public void visit(Print n) {
    n.e.accept(this);
  }
  
  // Identifier i;
  // Exp e;
  public void visit(Assign n) {
    if(getTableEntry(n.i.s) == null){
	    System.err.println("Use of undefined identifier " + n.i.s);
    }
	    
    n.i.accept(this);
    n.e.accept(this);
  }

  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {
    if(getTableEntry(n.i.s) == null){
	    System.err.println("Use of undefined identifier " + n.i.s);
    }
	  
    n.i.accept(this);
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(And n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(LessThan n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(Plus n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(Minus n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(Times n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(ArrayLookup n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e;
  public void visit(ArrayLength n) {
    n.e.accept(this);
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public void visit(Call n) {
	boolean found = false;
	
	if(n.e instanceof IdentifierExp){
		String id = ((IdentifierExp)n.e).s;
		SymbolTableEntry entry = this.getTableEntry(id);

		if(entry != null){
			SymbolTable prev = cur;
			cur = this.getTableByName(entry.defType);
			
			//Check if identifier for call is in current scope
			entry = this.getTableEntry(n.i.s);
			
			if(cur != null && entry != null)
			{
				found = true;	
			}
			
			cur = prev;		
		}
		
	}
	else if(n.e instanceof NewObject){
		SymbolTable prev = cur;
		String defType = (((NewObject)n.e).i.s);
		cur = this.getTableByName(defType);
		
		//Look up method id (n.i.s) in current symbol table
		SymbolTableEntry ste = this.getTableEntry(n.i.s);
		if(ste != null)
			found = true;
		
		cur = prev;
	}
	else if(getTableEntry(n.i.s) != null){
		found = true;
	}
	
	if(!found){
		System.err.println("Use of undefined identifier " + n.i.s);
	}
	    
    n.e.accept(this);
    n.i.accept(this);
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.elementAt(i).accept(this);
    }
    
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
	if(n instanceof IdentifierExp){
		String id = ((IdentifierExp)n).s;
		SymbolTableEntry entry = this.getTableEntry(id);
		
		if(entry == null){
			System.err.println("Use of undefined identifier " + id);
		}
	}
  }

  public void visit(This n) {
  }

  // Exp e;
  public void visit(NewArray n) {
    n.e.accept(this);
  }

  // Identifier i;
  public void visit(NewObject n) {
  }

  // Exp e;
  public void visit(Not n) {
    n.e.accept(this);
  }

  // String s;
  public void visit(Identifier n) {
  }
}
