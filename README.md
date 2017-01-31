Vikram Patwardhan
Cody Whited
CS1622
Project 3

Included files:

minijava.flex
minijavaparser.cup
Project3Driver.java
Quadruple.java
SymbolTable.java
SymbolTableEntry.java
TypeCheckVisitor.java
TypeDepthFirstVisitor.java
BuildSymbolTableVisitor.java
IRVisitor.java

1) Generate the lexer class 

java -jar jflex-1.6.0.jar minijava.flex

2) Generate the parser and sym classes

java -jar java-cup-11a.jar -interface -parser Parser minijavaparser.cup

3) Compile the java files using the provided makefile

4) Run Project3Driver

java Project3Driver input_filename


The examples used to test this program were the sample minijava programs found at the textbook resources website: http://www.cambridge.org/resources/052182060X/

None of our error messages include the line and character number as in the examples. We could not figure out how to access this information after the parsing was done, and since our TypeCheckVisitor and TypeDepthFirstVisitor were run as separate passes after the symbol table was entirely constructed, we had no way to store the line number without modifying the given abstract syntax tree code.
