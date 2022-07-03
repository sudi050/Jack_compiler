package compiler;

import java.util.ArrayList;

public class SymbolTable
{
	int StaticIndex, FieldIndex, LocalIndex, ArgumentIndex;
	boolean isSubroutine;
	ArrayList<JackVariable> ClassScope = new ArrayList<JackVariable>();
	ArrayList<JackVariable> SubroutineScope = new ArrayList<JackVariable>();
	
	SymbolTable()
	{
		StaticIndex = 0;
		FieldIndex = 0;
		LocalIndex = 0;
		ArgumentIndex = 0;
		isSubroutine = false;
	}
	
	void newSubroutine()
	{
		SubroutineScope.clear();
		isSubroutine = true;
		LocalIndex = 0;
		ArgumentIndex = 0;
	}
	
	void newVariable(String name, String type, String kind)
	{
		JackVariable var = new JackVariable(name, type, kind, getVariableKindIndex(kind));
		if(isSubroutine)
			SubroutineScope.add(var);
		else
			ClassScope.add(var);
	}
	
	int getVariableKindIndex(String kind)
	{
		int index;
		switch(kind)
		{
			case "var":
				index = LocalIndex;
				LocalIndex++;
				break;
			case "field":
				index = FieldIndex;
				FieldIndex++;
				break;
			case "argument":
				index = ArgumentIndex;
				ArgumentIndex++;
				break;
			case "static":
				index = StaticIndex;
				StaticIndex++;
				break;
			default:
				index = -1;
		}
		return index;
	}
	
	int VariableCount(String kind) 
	{
			switch(kind) 
			{
				case "argument":
					return ArgumentIndex;
				case "field":
					return FieldIndex;
				case "static":
					return StaticIndex;
				case "local":
					return LocalIndex;
				default:
					return -1;
			}
	}
	 
	String getVariableType(String varname)
	{
		 for(int i = 0; i < SubroutineScope.size(); i++)
		 {
			 if(SubroutineScope.get(i).name.equals(varname))
				 return SubroutineScope.get(i).getType();
		 }
		 for(int i = 0; i < ClassScope.size(); i++)
		 {
			 if(ClassScope.get(i).name.equals(varname))
				 return ClassScope.get(i).getType();
		 }
		 return null;
	}
	 
	String getVariableKind(String varname)
	{
		 for(int i = 0; i < SubroutineScope.size(); i++)
		 {
			 if(SubroutineScope.get(i).name.equals(varname))
				 return SubroutineScope.get(i).getKind();
		 }
		 for(int i = 0; i < ClassScope.size(); i++)
		 {
			 if(ClassScope.get(i).name.equals(varname))
				 return ClassScope.get(i).getKind();
		 }
		 return null;
	}
	 
	int getVariableIndex(String varname)
	{
		 for(int i = 0; i < SubroutineScope.size(); i++)
		 {
			 if(SubroutineScope.get(i).name.equals(varname))
				 return SubroutineScope.get(i).getIndex();
		 }
		 for(int i = 0; i < ClassScope.size(); i++)
		 {
			 if(ClassScope.get(i).name.equals(varname))
				 return ClassScope.get(i).getIndex();
		 }
		 return -1;
	}
}