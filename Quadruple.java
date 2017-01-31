
public class Quadruple {
	public String op;
	public String a1;
	public String a2;
	public SymbolTableEntry arg1;	
	public SymbolTableEntry arg2;
	public SymbolTableEntry res;
	public String result;	//not sure if this should be an entry or a string. it could be a temporary or a symbol table entry so idk.
	public int label;
	
	public Quadruple() {
		
	}
	
	public Quadruple(String op, String a1, String a2, String res, int label) {
		this.op = op;
		this.a1 = a1;
		this.a2 = a2;
		this.result = res;
		this.label = label;
	}
	
	public void setSTE(int num, SymbolTableEntry s) {
		if (num == 1) arg1 = s;
		if (num == 2) arg2 = s;
		if (num == 0) res = s;
	}
	
	public String toString() {
		String result = Integer.toString(this.label) + ": ";
		
		switch (this.op) {
			
			case "&&":
			case "<":
			case "*":
			case "-":
			case "+": 
				result += this.result + " := " + this.a1 + this.op + this.a2;
				break;
			case "ARRAYLOOKUP":
				result += this.result + " := " + this.a1 + "[" + this.a2 + "]";
				break;
			case "length":
				result += this.result + " := length " + this.a1;
				break;
			case "CALL":
				result += this.result + " := call " + this.a1 + ", " + this.a2;
				break;
			case "NEWARRAY":
				result += this.result + " := new int" + ", " + this.a1;
				break;
			case "NEW":
				result += this.result + " := new " + this.a1;
				break;
			case "NOT":
				result += this.result + " := !" + this.a1;
				break;
			case "RETURN":
				result += "return " + this.a1;
				break;
			case "IFFALSE":
				result += "iffalse " + this.a1 + " goto " + this.a2;
				break;
			case "PARAM":
				result += "param " + this.a1;
				break;
			case "COPY":
				result += this.result + " := " + this.a1;
				break;
			case "ARRAYASSIGN":
				result += this.result + "[" + this.a1 + "] := " + this.a2;
				break;
			case "GOTO":
				result += "goto " + this.a1;
				break;





			default:
				result = "UNKNOWN OP: " + this.op;
		}
		
		return result;
	}
}
