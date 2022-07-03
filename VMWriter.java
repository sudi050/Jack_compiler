package compiler;

public class VMWriter
{
	String VMOutput;
	
	VMWriter()
	{
		VMOutput = "";
	}
	
	String Push(String segment, int index)
	{
		if(segment=="field")
			segment="this";
		VMOutput = "push " + segment + " " + index;
		return VMOutput;
	}
	
	String Pop(String segment, int index)
	{
		if(segment=="field")
			segment="this";
		VMOutput = "pop " + segment + " " + index;
		return VMOutput;
	}
	
	String Arithmetic(String com)
	{
		VMOutput = com;
		return VMOutput;
	}
	
	String Label(String label)
	{
		VMOutput = "label " + label;
		return VMOutput;
	}
	
	String Goto(String label)
	{
		VMOutput = "goto " + label;
		return VMOutput;
	}
	
	String If(String label)
	{
		VMOutput = "if-goto " + label;
		return VMOutput;
	}
	
	String Call(String funcName, int nArgs)
	{
		VMOutput = "call " + funcName + " " + nArgs;
		return VMOutput;
	}
	
	String Function(String name, int nArgs)
	{
		VMOutput = "function " + name + " " + nArgs;
		return VMOutput;
	}
	
	String Return()
	{
		VMOutput = "return";
		return VMOutput;
	}
}