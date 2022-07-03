package compiler;

import java.util.ArrayList;

public class JackCompilationEngine 
{
	ArrayList<String> TokenizerOutput;
	int CurrentToken;
	String Token;
	ArrayList<String> CompEngOutput;
	String ClassName;
	SymbolTable SymTab;
	int ifnum, whilenum;
	VMWriter VM;
	String JackOps;
	ArrayList<String> osClasses;
	
	final String ifTrue = "ifTrue";
	final String ifFalse = "ifFalse";
	final String ifEnd = "ifEnd";
	final String whileExpression = "whileExpression";
	final String whileEnd = "whileEnd";
	
	
	JackCompilationEngine(ArrayList<String> x)
	{
		TokenizerOutput = new ArrayList<String>(x);
		CompEngOutput = new ArrayList<String>();
		CurrentToken = 0;
		Token = "";
		ClassName = "";
		SymTab = new SymbolTable();
		ifnum = 0;
		whilenum = 0;
		VM = new VMWriter();
		JackOps = "+-/*<>|=&";
		osClasses = new ArrayList<String>();
		osClasses.add("Memory");
		osClasses.add("String");
		osClasses.add("Math");
		osClasses.add("Array");
		osClasses.add("Output");
		osClasses.add("Screen");
		osClasses.add("Keyboard");
		osClasses.add("Sys");
	}
	
	ArrayList<String> getCompilationEngineOutput()
	{
		compile();
		return CompEngOutput;
	}
	
	void compile()
	{
		tokenizeradvance();
		ClassName = new String(Token);
		tokenizeradvance();
		while(CurrentToken < TokenizerOutput.size())
		{
			tokenizeradvance();
			if(getTokenType().equals("symbol"))
			{
				if(Token.equals("}"))
					break;
			}
			if(getTokenType().equals("keyword"))
			{
				switch(Token)
				{
					case "static":
					case "field":
						compileClassVarDec();
						break;
					case "constructor":
					case "function":
					case "method":
						compileSubroutineDec();
						break;
				}
			}
		}
	}
	
	void compileClassVarDec()
	{
		String tokname, toktype, tokkind;
		tokkind = new String(Token);
		tokenizeradvance();
		toktype = new String(Token);
		tokenizeradvance();
		tokname = new String(Token);
		SymTab.newVariable(tokname, toktype, tokkind);
		while(nextToken().equals(","))
		{
			tokenizeradvance();
			tokenizeradvance();
			tokname = new String(Token);
			SymTab.newVariable(tokname, toktype, tokkind);
		}
		tokenizeradvance();
	}
	
	void compileSubroutineDec() 
	{
		SymTab.newSubroutine();
		ifnum = 0;
		whilenum = 0;
		String subroutinetype = new String(Token);
		boolean isMethod = false;
		if(subroutinetype.equals("method"))
			isMethod = true;
		else
			isMethod = false;
		tokenizeradvance();
		tokenizeradvance();
		String subroutinename = new String(Token);
		tokenizeradvance();
		compileParameterList(isMethod);
		tokenizeradvance();
		compileSubroutineBody();
		CompEngOutput.add(VM.Function(ClassName + "." + subroutinename, SymTab.VariableCount("local")));
		switch(subroutinetype)
		{
			case "constructor":
				CompEngOutput.add(VM.Push("constant", SymTab.VariableCount("field")));
				CompEngOutput.add(VM.Call("Memory.alloc", 1));
				CompEngOutput.add(VM.Pop("pointer", 0));
				break;
			case "function":
				break;
			case "method":
				CompEngOutput.add(VM.Push("argument", 0));
				CompEngOutput.add(VM.Pop("pointer", 0));
				break;
		}
		compileStatements();
		tokenizeradvance();
	}

	
	void compileParameterList(boolean isMethod)
	{
		String name, type;
		if(isMethod)
			SymTab.newVariable("this", " ", "argument");
		while(CurrentToken < TokenizerOutput.size())
		{
			if(nextToken().equals(")"))
				break;
			tokenizeradvance();
			type = new String(Token);
			tokenizeradvance();
			name = new String(Token);
			SymTab.newVariable(name, type, "argument");
			if(nextToken().equals(","))
				tokenizeradvance();
		}
	}
	
	void compileSubroutineBody()
	{
		tokenizeradvance();
		while(nextToken().equals("var"))
			compileVarDec();
	}
	
	void compileVarDec()
	{
		tokenizeradvance();
		String type, name;
		tokenizeradvance();
		type = new String(Token);
		tokenizeradvance();
		name = new String(Token);;
		SymTab.newVariable(name, type, "var");
		while(nextToken().equals(","))
		{
			tokenizeradvance();
			tokenizeradvance();
			name = new String(Token);
			SymTab.newVariable(name, type, "var");
		}
		tokenizeradvance();
	}
	
	void compileStatements()
	{
		while(CurrentToken < TokenizerOutput.size())
		{
			if(nextToken().equals("}"))
				break;
			compileStatement();
		}
	}
	
	void compileStatement()
	{
		switch(nextToken())
		{
			case "let":
				compileLetStatement();
				break;
			case "while":
				compileWhileStatement();
				break;
			case "do":
				compileDoStatement();
				break;
			case "return":
				compileReturnStatement();
				break;
			case "if":
				compileIfStatement();
				break;	
		}
	}
	
	void compileLetStatement()
	{
		tokenizeradvance();
		tokenizeradvance();
		String varName = new String(Token);
		String varKind = new String(SymTab.getVariableKind(varName));
		int varIndex = SymTab.getVariableIndex(varName);
		if(nextToken().equals("["))
		{
			tokenizeradvance();
			compileExpression();
			tokenizeradvance();
			if(varKind.equals("field"))
				CompEngOutput.add(VM.Push("this", varIndex));
			else
				CompEngOutput.add(VM.Push(varKind, varIndex));
			CompEngOutput.add(VM.Arithmetic("add"));
			tokenizeradvance();
			compileExpression();
			CompEngOutput.add(VM.Pop("this", 0));
			CompEngOutput.add(VM.Pop("pointer", 1));
			CompEngOutput.add(VM.Push("temp", 0));
			CompEngOutput.add(VM.Pop("that", 0));
		}
		else
		{
			tokenizeradvance();
			compileExpression();
			if(varKind.equals("field"))
				CompEngOutput.add(VM.Pop("this", varIndex));
			else
				CompEngOutput.add(VM.Pop(varKind, varIndex));
		}
		tokenizeradvance();
	}
	
	void compileWhileStatement()
	{
		tokenizeradvance();
		int currentWhilenum = whilenum;
		whilenum++;
		CompEngOutput.add(VM.Label(whileExpression + currentWhilenum));
		tokenizeradvance();
		compileExpression();
		tokenizeradvance();
		CompEngOutput.add(VM.Arithmetic("not"));
		CompEngOutput.add(VM.If(whileEnd + currentWhilenum));
		tokenizeradvance();
		compileStatements();
		tokenizeradvance();
		CompEngOutput.add(VM.Goto(whileExpression + currentWhilenum));
		CompEngOutput.add(VM.Label(whileEnd + currentWhilenum));
	}
	
	void compileDoStatement()
	{
		tokenizeradvance();
		compileSubroutineCall();
		tokenizeradvance();
		CompEngOutput.add(VM.Pop("temp", 0));
	}
	
	void compileIfStatement()
	{
		tokenizeradvance();
		int currentIfnum = ifnum;
		ifnum++;
		tokenizeradvance();
		compileExpression();
		tokenizeradvance();
		CompEngOutput.add(VM.If(ifTrue + currentIfnum));
		CompEngOutput.add(VM.Goto(ifFalse + currentIfnum));
		CompEngOutput.add(VM.Label(ifTrue + currentIfnum));
		tokenizeradvance();
		compileStatements();
		tokenizeradvance();
		if(nextToken().equals("else"))
		{
			CompEngOutput.add(VM.Goto(ifEnd + currentIfnum));
			CompEngOutput.add(VM.Label(ifFalse + currentIfnum));
			tokenizeradvance();
			tokenizeradvance();
			compileStatements();
			tokenizeradvance();
			CompEngOutput.add(VM.Label(ifEnd + currentIfnum));
		}
		else
			CompEngOutput.add(VM.Label(ifFalse + currentIfnum));
	}
	
	void compileReturnStatement()
	{
		tokenizeradvance();
		
		if(nextToken().equals(";"))
			CompEngOutput.add(VM.Push("constant", 0));
		else
			compileExpression();
		tokenizeradvance();
		CompEngOutput.add(VM.Return());
	}
	
	void compileExpression()
	{
		String op;
		compileTerm();
		while(JackOps.contains(nextToken()))
		{
			tokenizeradvance();
			op = new String(Token);
			compileTerm();
			CompEngOutput.add(VM.Arithmetic(getOperator(op)));
		}
	}
	
	void compileSubroutineCall()
	{
		tokenizeradvance();
		String subroutineName = new String(Token);
		int nArgs = 0;
		String beforePeek = new String(Token);
		if(nextToken().equals("."))
		{
			if(!(osClasses.contains(subroutineName)))
			{
				subroutineName = SymTab.getVariableType(beforePeek);
				if(subroutineName == null)
					subroutineName = beforePeek;
				else
				{
					String subroutineKind = SymTab.getVariableKind(beforePeek);
					int subroutineIndex = SymTab.getVariableIndex(beforePeek);
			
					if(subroutineKind.equals("field"))
						CompEngOutput.add(VM.Push("this", subroutineIndex));
					else
						CompEngOutput.add(VM.Push(subroutineKind, subroutineIndex));
					nArgs++;
				}
			} 
			tokenizeradvance();
			tokenizeradvance();
			subroutineName += "." + new String(Token);
		} 
		else
		{
			CompEngOutput.add(VM.Push("pointer", 0));
			nArgs++;
			subroutineName = ClassName + "." + beforePeek;
		}
		tokenizeradvance();
		nArgs += compileExpressionList();
		tokenizeradvance();
		CompEngOutput.add(VM.Call(subroutineName, nArgs));
	}
	
	void compileTerm()
	{
		tokenizeradvance();
		String nexttok = new String(getTokenType());
		tokenizerretreat();
		switch(nexttok)
		{
			case "identifier":
				String beforePeek = nextToken();				
				String varKind = SymTab.getVariableKind(beforePeek);
				int varIndex = SymTab.getVariableIndex(beforePeek);
				if(varKind == null)
					compileSubroutineCall();
				else
				{
					tokenizeradvance();
					beforePeek = new String(Token);
					if(nextToken().equals(".") || nextToken().equals("("))
					{
						String objectName = new String(beforePeek);
						int argsNum = 0;
						if(nextToken().equals("."))
						{
							if(!(osClasses.contains(objectName)))
							{
								objectName = SymTab.getVariableType(beforePeek);
								if(objectName == null)
									objectName = beforePeek;
								else
								{
									String objKind = SymTab.getVariableKind(beforePeek);
									int objIndex = SymTab.getVariableIndex(beforePeek);
									if(objKind.equals("identifier"))
										CompEngOutput.add(VM.Push("this", objIndex));
									else
										CompEngOutput.add(VM.Push(objKind, objIndex));
									argsNum++;
								}
							
							}
							tokenizeradvance();
							tokenizeradvance();
							objectName += "." + new String(Token);
						}
						else
						{
							CompEngOutput.add(VM.Push("pointer", 0));
							argsNum++;
							objectName = ClassName + "." + beforePeek;
						}
						tokenizeradvance();
						argsNum += compileExpressionList();
						tokenizeradvance();
						CompEngOutput.add(VM.Call(objectName, argsNum));
					}
					else if(nextToken().equals("["))
					{
						tokenizeradvance();
						compileExpression();
						tokenizeradvance();
						if(varKind.equals("field"))
							CompEngOutput.add(VM.Push("this", varIndex));
						else
							CompEngOutput.add(VM.Push(SymTab.getVariableKind(varKind), varIndex));
						CompEngOutput.add(VM.Arithmetic("add"));
						CompEngOutput.add(VM.Pop("pointer", 1));
						CompEngOutput.add(VM.Push("that", 0));
					}
					else
					{
						if(varKind.equals("identifier"))
							CompEngOutput.add(VM.Push("this", varIndex));
						else
							CompEngOutput.add(VM.Push(varKind, varIndex));
					}
				}
				break;
			
			case "integerConstant":
				tokenizeradvance();
				CompEngOutput.add(VM.Push("constant", Integer.parseInt(Token)));
				break;
			
			case "keyword":
				tokenizeradvance();
				switch(Token) 
				{
					case "true":
						CompEngOutput.add(VM.Push("constant", 0));
						CompEngOutput.add(VM.Arithmetic("not"));
						break;
					case "false":
					case "null":
						CompEngOutput.add(VM.Push("constant", 0));
						break;
					case "this":
						CompEngOutput.add(VM.Push("pointer", 0));;
						break;
				}
				break;
			case "stringConstant":
				tokenizeradvance();
				String theString = new String(Token.replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t").replace((char) 14, (char) 9));
				CompEngOutput.add(VM.Push("constant", theString.length()));
				CompEngOutput.add(VM.Call("String.new", 1));
				for(int i = 0; i < theString.length(); i++)
				{
					int asciiVal = theString.charAt(i);
					CompEngOutput.add(VM.Push("constant", asciiVal));
					CompEngOutput.add(VM.Call("String.appendChar", 2));
				}
				break;
			case "symbol":
				switch(nextToken())
				{
					case "(":
						tokenizeradvance();
						compileExpression();
						tokenizeradvance();
						break;
					case "-":
					case "~":
						tokenizeradvance();
						String unaryOp = new String(Token);
						compileTerm();
						if(unaryOp.equals("~"))
							CompEngOutput.add(VM.Arithmetic("not"));
						else if(unaryOp.equals("-"))
							CompEngOutput.add(VM.Arithmetic("neg"));
						break;
				}
				break;
			}
		}
	
	int compileExpressionList()
	{
		int expnum = 0;
		while(CurrentToken < TokenizerOutput.size())
		{
			if(nextToken().equals(")"))
				break;
			compileExpression();
			expnum++;
			if(nextToken().equals(","))
				tokenizeradvance();
		}
		return expnum;
	}
	
	void tokenizeradvance()
	{
		//if(CurrentToken < TokenizerOutput.size()-1)
		//{
			CurrentToken++;
			Token = TokenizerOutput.get(CurrentToken).split(" ")[1];
		//}
		//else
			//System.out.println("Exceeded Number of Tokens!!");
		
	}
	
	void tokenizerretreat()
	{
		CurrentToken--;
		Token = TokenizerOutput.get(CurrentToken).split(" ")[1];
	}
	
	String getTokenType()
	{
		String type = TokenizerOutput.get(CurrentToken).split(" ")[0];
		return type.substring(1, type.length()-1);
	}
	
	String nextToken()
	{
		tokenizeradvance();
		String nexttok = new String(Token);
		tokenizerretreat();
		return nexttok;
	}
	
	String getOperator(String op)
	{
		switch(op)
		{
			case "+":
				return "add";
			case "-":
				return "sub";
			case "*":
				return VM.Call("Math.multiply", 2);
			case "/":
				return VM.Call("Math.divide", 2);
			case "=":
				return "eq";
			case "<":
				return "lt";
			case ">":
				return "gt";
			case "|":
				return "or";
			case "&":
				return "and";
			case "~":
				return "not";
			default:
				return null;
		}
	}
}
