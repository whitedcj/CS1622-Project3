JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
	Project3Driver.java \
	Lexer.java \
	Parser.java \
	sym.java \
	BuildSymbolTableVisitor.java \
	IRVisitor.java \
	Quadruple.java \
	SymbolTable.java \
	SymbolTableEntry.java \
	TypeCheckVisitor.java \
	TypeDepthFirstVisitor.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class