package compiler;

import java.util.ArrayList;
import java.util.HashSet;

public class JackTokenizer 
{
	ArrayList<String> jacklines;
	ArrayList<String> TokenizerOutput;
	
	HashSet<String> kwd = new HashSet<String>();
	HashSet<String> sym = new HashSet<String>();
	
	{
		kwd.add("class");kwd.add("constructor");kwd.add("function");kwd.add("method");
		kwd.add("static");kwd.add("field");kwd.add("var");kwd.add("char");kwd.add("int");
		kwd.add("boolean");kwd.add("void");kwd.add("true");kwd.add("false");kwd.add("null");
		kwd.add("this");kwd.add("let");kwd.add("do");kwd.add("if");kwd.add("else");kwd.add("while");
		kwd.add("return");
		
		sym.add("[");sym.add("]");sym.add("{");sym.add("}");sym.add("(");sym.add(")");
		sym.add(".");sym.add(",");sym.add(";");sym.add("+");sym.add("-");sym.add("*");
		sym.add("/");sym.add("&");sym.add("|");sym.add("<");sym.add(">");sym.add("=");
		sym.add("~");
	}
	
	JackTokenizer(ArrayList<String> x)
	{
		jacklines = new ArrayList<String>(x);
		TokenizerOutput = new ArrayList<String>();
	}

	ArrayList<String> getJackTokenizerOutput() 
	{
		ArrayList<String> toks = new ArrayList<String>();
		for(int i = 0; i < jacklines.size(); i++)
		 {
			toks = gettokens(jacklines.get(i));
			 for(int j = 0; j < toks.size(); j++)
			 {
				 if(TokenWithType(toks.get((j))) != "")
					TokenizerOutput.add(TokenWithType(toks.get(j)));
			 }
		 }
		return TokenizerOutput;
	}
	
	//Returns a jack line as tokens
	ArrayList<String> gettokens(String n)
	{
		ArrayList<String> toks = new ArrayList<String>();
		ArrayList<String> jacktoks = new ArrayList<String>();
		if(n.charAt(0) == '/')
			return toks;
		n = n.strip();
		String strings[] = n.split("\"");
		for(int i = 0; i < strings.length; i++) 
		{
			String var = strings[i];
			if(i != 1) 
			{
				for(int j = 0; j < var.split(" ").length; j++) 
				{
					jacktoks.add(var.split(" ")[j]);
				}
			}
			else
				jacktoks.add("\"" + strings[i] + "\"");
		}
		int x = 0;
		int y = 0;
		for(int i = 0; i < jacktoks.size(); i++)
		{
			String s = jacktoks.get(i);
			x = 1;
			if(s.charAt(0) == '"')
			{
				toks.add(s);
				continue;
			}
			for(int j = 0; j < s.length(); j++)
			{
				char ch = s.charAt(j);
				boolean val = isalpha(Character.toString(ch));
				if(val)
				{
					if(isalpha(s))
					{
						toks.add(s);
						break;
					}
				}
				else
				{
					if(x == 1)
					{
						x = 0;
						toks.add(s.substring(0, j));
						toks.add(s.substring(j, j+1));
						y = j+1;
					}
					else
					{
						toks.add(s.substring(y, j));
						toks.add(s.substring(j, j+1));
						y = j+1;
					}
				}
			}
		}
		return toks;
	}
	
	//Checks if parameter is alphabets or not
	boolean isalpha(String n)
	{
		String specialCharactersString = "!@#$%&*()'+,-./:;<=>?[]^_`{|}";
        for (int i=0; i < n.length() ; i++)
        {
            char ch = n.charAt(i);
            if(specialCharactersString.contains(Character.toString(ch)))
            	return false;
            else if(i == n.length()-1)     
                return true;
            else
            	continue;
        }
        return false;
	}
	
	//Checks if parameter is numerical
	boolean isdigit(String n)
	{
		String specialCharactersString = "0123456789";
        for (int i=0; i < n.length() ; i++)
        {
            char ch = n.charAt(i);
            if(specialCharactersString.contains(Character.toString(ch)))
            	continue;
            else
            	return false;
        }
        return true;
	}
	
	//return token with token type
	String TokenWithType(String n) 
	{
		if(kwd.contains(n)) 
			return getKeywordOutput(n);
		else if(n == "") 
			return "";
		else if(sym.contains(n)) 
			return getSymbolOutput(n);
		else if(isdigit(n)) 
			return getIntegerConstantOutput(n);
		else if(n.startsWith("\"") && n.endsWith("\"")){
			String x = n.substring(1, n.length()-1);
			return getStringConstantOutput(x);
		}
		else
			return getIdentifierOutput(n);
	}
	
	String getKeywordOutput(String n)
	{
		return "<keyword> " + n + " </keyword>";
	}
	
	String getSymbolOutput(String n)
	{
		return "<symbol> " + n + " </symbol>";
	}
	
	String getIntegerConstantOutput(String n)
	{
		return "<integerConstant> " + n + " </integerConstant>";
	}
	
	String getStringConstantOutput(String n)
	{
		return "<stringConstant> " + n + " </stringConstant>";
	}
	
	String getIdentifierOutput(String n)
	{
		return "<identifier> " + n + " </identifier>";
	}
}
