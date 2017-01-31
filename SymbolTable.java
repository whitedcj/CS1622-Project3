import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class SymbolTable
{
	private String name;
	private SymbolTable parent;
	private Hashtable<String, SymbolTableEntry> table;

	public SymbolTable() {
		name = "root";
		parent = null;
		table = new Hashtable<String, SymbolTableEntry>();
	}
	
	public SymbolTable(String name) {
		this.name = name;
		parent = null;
		table = new Hashtable<String, SymbolTableEntry>();
	}

	public void addElement(String id, SymbolTableEntry ste) {
		table.put(id, ste);
	}

	public void addElement(String id, String e, String d, String r, ArrayList<String> p, String b) {
		SymbolTableEntry se = new SymbolTableEntry(e, d, r, p, b);
		table.put(id, se);
	}
	
	public SymbolTableEntry getElement(String id) {
		return table.get(id);
	}
	
	public void setParent(SymbolTable s){
		this.parent = s;
	}
	
	public SymbolTable getParent(){
		return parent;
	}
	
	public void setName(String n){
		this.name = n;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean contains(String key){
		return table.containsKey(key);
	}
	
	public Set<String> getKeys(){
		return this.table.keySet();
	}
	
	public String toString()
	{
		return print("");
	}

	public String print(String tabs)
	{
		StringBuilder b = new StringBuilder();
		b.append(tabs + name + "'s table \n");
		for(String s: table.keySet())
		{
			b.append(tabs + "\t" + s + ": " + table.get(s).display(s, "\t\t" + tabs) + "\n");
		}
		
		return b.toString();
	}
}