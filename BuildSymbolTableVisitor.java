import java.util.ArrayList;

import syntaxtree.*;
import visitor.Visitor;

public class BuildSymbolTableVisitor implements Visitor {

  private SymbolTable root;
  private SymbolTable cur;

  public BuildSymbolTableVisitor() {
    root = new SymbolTable("root");
    cur = root;
  }
  
  public SymbolTable getTable(){
	  return root;
  }
  
  private String getType(Type t){
	  String type = "";
	  if(t instanceof IntArrayType){
		  type = "int[]";
	  }
	  else if(t instanceof BooleanType){
		  type = "boolean";
	  }
	  else if(t instanceof IntegerType){
		  type = "int";
	  }
	  else{
		  type = ((IdentifierType)t).s;
	  }
	  
	  return type;
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
    //create a symboltableentry to the highest scope
    //add it to cur
    SymbolTableEntry ste = new SymbolTableEntry("class", "", "", null, "");
    
    n.i1.accept(this);
    
    //Add formal parameters for main() to class symbol table
    ArrayList<String> parameters = new ArrayList<String>();
    parameters.add(n.i2.s);
    
    n.i2.accept(this);
    
    //Create symbol table for main
    SymbolTable m = new SymbolTable();
    SymbolTable prev = cur;
    cur = m;
    
    n.s.accept(this);

    cur.addElement("main", new SymbolTableEntry("function", "", "void", parameters, ""));
    cur.setParent(prev);
    cur.setName(n.i1.s);
    
    ste.addScope(cur);
    cur = prev;
    
	cur.addElement(n.i1.s, ste);
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    SymbolTableEntry ste = new SymbolTableEntry("class", "", "", null, "");

    n.i.accept(this);
    
    SymbolTable st = new SymbolTable();
    SymbolTable prev = cur;
    cur = st;
    cur.setName(n.i.s);
    //creat a symboltableentry to the highest scope
    //make a new empty symboltable, and have the things within this classdecl add to that
    //so set it to cur
    //have everything update cur
    //we manually update cur as it should be changed tos oemthing else
    
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }

    //add this symboltable to this guys entry and then add that entry to the overall highest scope

    cur.setParent(prev);
    ste.addScope(cur);
    cur = prev;

    cur.addElement(n.i.s, ste);
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
	SymbolTableEntry ste = new SymbolTableEntry("class", "", "", null, n.j.s);
	    
    n.i.accept(this);
    n.j.accept(this);

    SymbolTable st = new SymbolTable();
    SymbolTable prev = cur;
    cur = st;
    cur.setName(n.i.s);
    
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }

    cur.setParent(prev);
    ste.addScope(cur);
    cur = prev;

	cur.addElement(n.i.s, ste);
  }

  // Type t;
  // Identifier i;
  public void visit(VarDecl n) {
	//Check for multiply defined identifier
	String identifier = n.i.s;
	boolean foundId = false;
	  
	for(String key: cur.getKeys()){
		if(key.compareTo(identifier) == 0){
			foundId = true;
		}
	}
	
	if(!foundId){
		cur.addElement(identifier, new SymbolTableEntry("variable", getType(n.t), "", null, ""));
	}
	else{
		System.err.println("Multiply defined identifier " + identifier);
	}
	
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
	ArrayList<String> formalParams = new ArrayList<String>();
	
	SymbolTable st = new SymbolTable();
    SymbolTable prev = cur;
    cur = st;
    cur.setName(n.i.s);
    
    n.t.accept(this);
    n.i.accept(this);
    
    for ( int i = 0; i < n.fl.size(); i++ ) {
        n.fl.elementAt(i).accept(this);
        
        formalParams.add(n.fl.elementAt(i).i.s);
    }

	SymbolTableEntry ste = new SymbolTableEntry("function", "", getType(n.t), formalParams, "");
    
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    
    n.e.accept(this);

    cur.setParent(prev);
    ste.addScope(cur);
    cur = prev;

	cur.addElement(n.i.s, ste);
  }

  // Type t;
  // Identifier i;
  public void visit(Formal n) {
    cur.addElement(n.i.s, new SymbolTableEntry("variable", getType(n.t), "", null, ""));
	
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
    n.i.accept(this);
    n.e.accept(this);
  }

  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {
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
