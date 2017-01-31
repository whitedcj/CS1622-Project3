import java.util.ArrayList;

public class SymbolTableEntry {

	public String entryType; //variable, function, class
	public String defType;	//int, boolean, or a class name
	public String returnType;
	public String baseClass;
	public ArrayList<String> params;
	public SymbolTable enclosedScope;

	public SymbolTableEntry() {
		params = new ArrayList<String>();
	}

	public SymbolTableEntry(String e, String d, String r, ArrayList<String> p, String b) {
		this.entryType = e;
		this.defType = d;
		this.returnType = r;
		this.params = p;
		this.baseClass = b;
	}

	public void addScope(SymbolTable st) {
		enclosedScope = st;
	}

	public String display(String name, String tabs)
	{
		StringBuilder b = new StringBuilder();
		b.append("entryType:" + entryType + ", defType:" + defType + ", returnType:" + returnType + ", baseClass:" + baseClass);
		
		if(params != null)
		{
			for(String s: params)
			{
				b.append(" Formal param: " + s);
			}
		}
		if(enclosedScope != null)
			b.append("\n\n" + enclosedScope.print(tabs));
		
		return b.toString();
	}
}