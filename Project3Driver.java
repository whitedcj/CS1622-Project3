import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import syntaxtree.*;
import visitor.*;
import java_cup.runtime.Symbol;


public class Project3Driver {

	public static void main(String[] args) throws FileNotFoundException {
		Reader reader;
		
		if (args.length < 1) {
			System.out.println("Pass the filename as the first command line argument.");
		} else {
			
			Symbol parse_tree = null;
			
			reader = new FileReader(args[0]);
			Lexer lex = new Lexer(reader);
			Parser p = new Parser(lex);
			
			try {
				parse_tree = p.parse();
				Program program = (Program)parse_tree.value;
				
				//Build symbol table and check for multiply defined identifiers
				BuildSymbolTableVisitor stv = new BuildSymbolTableVisitor();
				program.accept(stv);
				SymbolTable table = stv.getTable();
				//System.out.println(table);
				
				//Use symbol table for checking undefined identifiers
				TypeCheckVisitor tcv = new TypeCheckVisitor(table);
				program.accept(tcv);
				
				TypeDepthFirstVisitor tdfv = new TypeDepthFirstVisitor(table);
				program.accept(tdfv);
				
				IRVisitor irv = new IRVisitor(table);
				program.accept(irv);
				ArrayList<Quadruple> ir = irv.getIR();
				for(Quadruple q: ir){
					System.out.println(q);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
