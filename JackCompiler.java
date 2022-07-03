package compiler;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class JackCompiler 
{

	public static void main(String[] args) 
	{
		Scanner in = new Scanner(System.in);
		String dir;
		System.out.println("Enter the file directory: ");
		dir = in.next();
		in.close();
		try
		{
			File Directory = new File(dir);
			for(File f:Directory.listFiles())
			{
				String filename = f.getName();
				System.out.println(f);
				if(filename.endsWith(".jack"))
				{
					File file = new File(dir + "\\" + filename.replace(".jack", ".vm"));
					PrintWriter output = new PrintWriter(file);
					Scanner input = new Scanner(f);
					ArrayList<String> jackcoms = new ArrayList<String>();
					while(input.hasNext())
					{
						String jackline = input.nextLine();
						boolean ws = true;
						if(jackline.strip().isEmpty())
							ws = true;
						else
							ws = false;
						if(ws)
							continue;
						else
						{
							jackline = jackline.strip();
							if(!(jackline.charAt(0) == '/' || jackline.charAt(0) == '*'))
								jackcoms.add(jackline);
						}
					}
					ArrayList<String> TokenizerOutput = new ArrayList<String>();
					TokenizerOutput = Tokenizer(jackcoms);
			//		for(int i = 0; i < TokenizerOutput.size(); i++)
			//			System.out.println(TokenizerOutput.get(i));
					ArrayList<String> CompEngOutput = new ArrayList<String>();
					CompEngOutput = CompilationEngine(TokenizerOutput);
		//			for(int i = 0; i < CompEngOutput.size(); i++)
		//				System.out.println(CompEngOutput.get(i));
					for(int i = 0; i < CompEngOutput.size(); i++)
						output.println(CompEngOutput.get(i));
					output.close();
					input.close();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> Tokenizer(ArrayList<String> jackcoms)
	{
		ArrayList<String> TokenizerOutput = new ArrayList<String>();
		JackTokenizer jtok = new JackTokenizer(jackcoms);
		TokenizerOutput = jtok.getJackTokenizerOutput();
		return TokenizerOutput;
	}
	
	public static ArrayList<String> CompilationEngine(ArrayList<String> TokenizerOutput)
	{
		ArrayList<String> CompEngOutput = new ArrayList<String>();
		JackCompilationEngine jcomeng = new JackCompilationEngine(TokenizerOutput);
		CompEngOutput = jcomeng.getCompilationEngineOutput();
		return CompEngOutput;
	}
}
