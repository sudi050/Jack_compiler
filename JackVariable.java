package compiler;

public class JackVariable
{
	String name, type, kind;
	int index;
	
	JackVariable(String name, String type, String kind, int index)
	{
		this.name = new String(name);
		this.type = new String(type);
		this.index = index;
		if(kind.equals("var"))
			this.kind = new String("local");
		else
			this.kind = new String(kind);
	}
	
	int getIndex()
	{
		return this.index;
	}
	
	String getName()
	{
		return this.name;
	}
	
	String getType()
	{
		return this.type;
	}
	
	String getKind()
	{
		return this.kind;
	}
}