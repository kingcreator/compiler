import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class compiler {
	static List<String> terminal = new ArrayList<String>();
	static List<String> nonTerminal = new ArrayList<String>();
	static List<String> grammarList = new ArrayList<String>();
	static Map<String,Set<String>> firstSetTerminal = new LinkedHashMap<>();
	static Map<String,Set<String>> firstSet = new LinkedHashMap<>();
	static Map<String,Set<String>> followSet = new LinkedHashMap<>();
	static Map<String,Set<String>> followList = new LinkedHashMap<>();
	static String startSymbol;
	static Set<String> stateSet = new LinkedHashSet<String>();
	static List<Set<List<String>>> stateList = new ArrayList<Set<List<String>>>();
	static Map<Integer, Map<String, Integer> > SLR = new LinkedHashMap<>();
	static List<ProductionGrammar> production = new ArrayList<ProductionGrammar>();
	static List<ProductionGrammar> slrProduction = new ArrayList<ProductionGrammar>();
	static Map<Integer, List<SLRtable>> slrTable = new LinkedHashMap<Integer, List<SLRtable>>();


	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		

		readFromBuffer("GrammarList.txt", grammarList);
		readFromBuffer("nonTerminalList.txt", nonTerminal);
		readFromBuffer("terminalList.txt", terminal);

	
		setProduction(grammarList);
		/*
		for (int j = 0; j <production.size(); j ++)
		{
			System.out.print("Number: " +j);
			System.out.print("LeftSide: "+ production.get(j).getLeftSide());
			for (int k = 0; k <production.get(j).getRightSide().size(); k++)
			System.out.print("RightSide: "+ production.get(j).getRightSide().get(k));
			System.out.print("\n");
		} */
		
		buildTheFirst();
		printToTextFile(firstSet, "First");
		/*
		for (String name: firstSet.keySet())
		{
            String key = name.toString();
            String value = firstSet.get(name).toString();  
            System.out.println(key + " :: " + value);  
		} */
		// Start Symbol for the Program
		startSymbol = production.get(0).getLeftSide();
		buildTheFollow();
		printToTextFile(followSet,"Follow");

		/*for (String name: followSet.keySet())
		{
            String key = name.toString();
            String value = followSet.get(name).toString();  
            System.out.println("Key: " +key + " Terminal:" + value);  
		} 
		*/
		
		
		//create new SLR production add state 0
		
		
		for (int i = 0; i < grammarList.size() ; i ++)
		{
           // System.out.println(grammarList.get(i));  

		}
		createSLRproduction();
		//createSLR();
		createSLRmap();
		for(int i = 0; i < stateList.size(); i++)
		{
			System.out.println("--------------------------State " + i+ "----------------------------------");
			System.out.println(stateList.get(i));
			
		}
		for (Integer entry: SLR.keySet())
		{
			List<SLRtable> listSLRtable = new ArrayList<SLRtable>();
            String key = entry.toString();
            System.out.println("----------------------------State " +key +"--------------------------------");  
            Map<String,Integer> tempMap = new  LinkedHashMap<String,Integer> ();
            tempMap = SLR.get(entry);
            for(String S : tempMap.keySet())
            {
            	String command;
            	int tempInt;
            	String key1 = S.toString();
                String value = tempMap.get(key1).toString();  
               // System.out.println("Symbol: " +key1);
                if(nonTerminal.contains(key1))
                {
                	command ="Command G";
                }
                else 
                {
                	command = "Command S"  ;
                }
               // System.out.println("Number: " +value);
              //  System.out.println(" ");
                tempInt = Integer.parseInt(value);
                SLRtable table = new SLRtable(key1,command,tempInt);
                listSLRtable.add(table);
            }
            int tempKey = Integer.parseInt(key);
            slrTable.put(tempKey, listSLRtable);
		} 
		
		
		for (int j = 0; j <slrProduction.size(); j ++)
		{
			System.out.print("Number: " +j);
			System.out.print(slrProduction.get(j).getLeftSide() + "::");
			for (int k = 0; k <slrProduction.get(j).getRightSide().size(); k++)
			System.out.print(slrProduction.get(j).getRightSide().get(k) + " ");
			System.out.print("\n");
		}
		for(int i = 0; i < stateList.size(); i++)
		{
			List<SLRtable> listSLRtable = new ArrayList<SLRtable>();
			listSLRtable = slrTable.get(i);
			System.out.println("--------------------------State " + i+ "----------------------------------");
			for (List s : stateList.get(i))
			{
				int index = s.indexOf(".");
				if(index == s.size()-1)
				{
					int productionNumber = -1;
					Set<String> S1 = new LinkedHashSet<String>(s);
					for(int k = 0; k < slrProduction.size();k++)
					{
						Set<String> S2 = new LinkedHashSet<String>();
						S2.add(slrProduction.get(k).getLeftSide());
						S2.addAll(slrProduction.get(k).getRightSide());
						if(S1.equals(S2))
						{
							productionNumber = k;
						}
					}

					System.out.println("what going on?"+s);

					//this is for P'
					if(slrProduction.get(0).getLeftSide().equals(s.get(0)))
					{
		                SLRtable table = new SLRtable("$","Command accept");
						listSLRtable.add(table);

					}
					else
					{
						
						for(String stringItem : followSet.get(s.get(0)))
						{
							System.out.println(stringItem);
				            SLRtable table = new SLRtable(stringItem,"Command R", productionNumber );
							listSLRtable.add(table);
						}
						//Iterator<String> it = followSet.get(s.get(0)).iterator();
						//while(it.hasNext()){
							//SLRtable table = new SLRtable(it.next(),"Command R", );
					        //System.out.println(it.next());
					}
					
					
				}
				
			}
            slrTable.put(i, listSLRtable);

			
		}
		writeState();
		writeSLR();
		
	}
	public static void createSLRmap()
	{
		Set<List<String>> stateSet = new LinkedHashSet<List<String>>();
		
		
		//make the list of item what will go
		Set<String> tempSet = new LinkedHashSet<String>();
		for( int j = 0; j < slrProduction.size() ; j ++)
		{
			List<String> newList = new ArrayList<String>();
			newList.add(slrProduction.get(j).getLeftSide());
			newList.addAll(slrProduction.get(j).getRightSide());
			stateSet.add(newList);
			//index for the .
			int indexTemp = slrProduction.get(j).getRightSide().indexOf(".");
			{
				if ( indexTemp != slrProduction.get(j).getRightSide().size()-1 )
				{
					tempSet.add(slrProduction.get(j).getRightSide().get(indexTemp+1));
				}
			}
		}
		// initial State

		Set<List<String>> initialSet = new LinkedHashSet<List<String>>();
		recursionSLRstate(initialSet,slrProduction.get(0).getLeftSide());
		stateList.add(initialSet);
		//stateList.add(stateSet);
		int i = 0;	
		while (i < stateList.size())
		{
			//working set
			Set<List<String>> currentWorkingSet = new LinkedHashSet<List<String>>();
			//Map <String, Set<List<String>>> returnMap = new LinkedHashMap <String, Set<List<String>>>();
			
			currentWorkingSet = stateList.get(i);
			
			SLR.put(i,slrRecurrsion(currentWorkingSet));		
			i++;	
		}
	}
	
	public static Map<String,Integer> slrRecurrsion (Set<List<String>> currentWorkingSet)
	{
		Map <String, Integer> stateMap = new LinkedHashMap <String,Integer>();
		//Set<List<String>> innerSet = new LinkedHashSet <List<String>>();
		List<String> tempListForMove = new ArrayList <String>();
		//create the List for possible moving terminal and nonTerminal
		for(List<String> s : currentWorkingSet)
		{
			//access each element of the list
			int indexOfPeriod = s.indexOf(".");
			if ( indexOfPeriod < s.size()-1)
			{
				if(!tempListForMove.contains(s.get(indexOfPeriod+1)))
				{
					tempListForMove.add(s.get(indexOfPeriod+1));
				}
			}			
		}
		//make the set in to List of because Set filter out duplicate and List
		
		for (int i = 0 ; i < tempListForMove.size(); i ++)
		{
			Set<List<String>> tempSet = new LinkedHashSet<List<String>>();
			
			for(List<String> s : currentWorkingSet)
			{				
				int indexOfPeriod = s.indexOf(".");
				if( (s.size()-1 - indexOfPeriod) > 0 && s.get(indexOfPeriod+1).equals(tempListForMove.get(i)))
				{
					List<String> tempList = new ArrayList<String> ();
					for (String tempString : s)
					{
						tempList.add(tempString);
					}
					//swap the value of period and next value
					tempList.set(indexOfPeriod, tempList.get(indexOfPeriod+1));
					tempList.set(indexOfPeriod +1, ".");
					tempSet.add(tempList);
					//if after the Period is a Nontermial
					if((s.size()-1 - indexOfPeriod) > 1 && nonTerminal.contains(s.get(indexOfPeriod +2)))
					{		
						recursionSLRstate(tempSet, s.get(indexOfPeriod+2));
					}										
				}			
			}
			
			if(stateList.contains(tempSet))
			{
				int tempValue = stateList.indexOf(tempSet);
				stateMap.put(tempListForMove.get(i), tempValue);
			}
			else
			{
				stateList.add(tempSet);
				stateMap.put(tempListForMove.get(i),stateList.size()-1);
			}
			

		}		
 		return stateMap;
	}
	//this recreate more state if the . before the nonTerminal
	public static void  recursionSLRstate (Set<List<String>> tempSet, String CurrentNonTerminal)
	{
		for(int i = 0; i < slrProduction.size(); i ++)
		{
			if (slrProduction.get(i).getLeftSide().equals(CurrentNonTerminal))
			{
				List<String> productionList = new ArrayList<String>();
				productionList.add(slrProduction.get(i).getLeftSide());
				productionList.addAll(slrProduction.get(i).getRightSide());
				if(tempSet.contains(productionList))
				{
					continue;
				}
				tempSet.add(productionList);
				if(slrProduction.get(i).getRightSide().indexOf(".") == slrProduction.get(i).getRightSide().size()-1)
				{
					continue;
				}
				else if(nonTerminal.contains(slrProduction.get(i).getRightSide().get(1)))
					{
						recursionSLRstate (tempSet,slrProduction.get(i).getRightSide().get(1));
					}
			}
		}
		
	}
	/*
	public static void createSLR()
	{
		int i = 0;
		do 
		{
			if (i == 0)
			{
				//set up state 0 look like this ( p . l )first one is the left side
				Set<List<String>> stateSet = new LinkedHashSet<List<String>>();
				
				
				//make the list of item what will go
				Set<String> tempSet = new LinkedHashSet<String>();
				for( int j = 0; j < slrProduction.size() ; j ++)
				{
					List<String> newList = new ArrayList<String>();
					newList.add(slrProduction.get(j).getLeftSide());
					newList.addAll(slrProduction.get(j).getRightSide());
					stateSet.add(newList);
					//index for the .
					int indexTemp = slrProduction.get(j).getRightSide().indexOf(".");
					{
						if ( indexTemp != slrProduction.get(j).getRightSide().size()-1 )
						{
							tempSet.add(slrProduction.get(j).getRightSide().get(indexTemp+1));
						}
					}
				}
				SLR.put(stateSet, null);
				//create those state with the list of Terminal and Nonterminal we got					
				System.out.println("////////");
				System.out.println(tempSet.size());
				Map<String,Set<List<String>>> innerMap = new LinkedHashMap<>();			
				for (String s : tempSet) 
				{
					Set<List<String>> innerStateSet = new LinkedHashSet<List<String>>();
					for (List<String> L : stateSet) 
					{
						int index = L.indexOf(".") ;
						if (index != L.size()-1)
						{
							if (L.get(index+1).equals(s))
							{
								List<String> tempList = new ArrayList<String>();
								for(int t = 0 ; t < L.size(); t ++)
								{
									tempList.add(t, L.get(t));
								}
								tempList.set(index, L.get(index +1));
								tempList.set(index+1,".");
								innerStateSet.add(tempList);	
							}
						}
					}
					innerMap.put(s, innerStateSet);
					System.out.println("-------------------------1--------------------");

					for (List<String> K : innerStateSet)
					{
						System.out.println(K);
					}
					SLR.put(innerStateSet, null);
				}
				//modify the intial state

				
				SLR.put(stateSet, innerMap);

				
				
				
				System.out.println(SLR.size());

			}
			else
			{
				
			}
			i ++;
		}
		while (i < 2);
	}
	*/
	public static void createSLRproduction()
	{
		List<String> tempList = new ArrayList<String>();
		String intial;
		tempList.add(".");
		tempList.add(production.get(0).getLeftSide());
		intial = production.get(0).getLeftSide();
		intial = intial.concat("\'");
        ProductionGrammar pg = new ProductionGrammar(intial, tempList);
		slrProduction.add(pg);
		for (int i = 0 ; i < production.size(); i ++)
		{		
			if(!production.get(i).getRightSide().contains("empty"))
				{	
					List<String> tempList1 = new ArrayList<String>();
					tempList1.add(".");
					for(int j =0;j < production.get(i).getRightSide().size();j++)
					{
						tempList1.add(production.get(i).getRightSide().get(j));
					} 
		            ProductionGrammar pg1 = new ProductionGrammar(production.get(i).getLeftSide(), tempList1);
		            slrProduction.add(pg1);
				}
			else
			{
				List<String> tempList1 = new ArrayList<String>();
				tempList1.add(".");
				for(int j =0;j < production.get(i).getRightSide().size();j++)
				{
					if(!production.get(i).getRightSide().get(j).contains("empty"))
					{
					tempList1.add(production.get(i).getRightSide().get(j));
					}
					else
					{
						continue;
					}
				} 
	            ProductionGrammar pg1 = new ProductionGrammar(production.get(i).getLeftSide(), tempList1);
	            slrProduction.add(pg1);
				
			}
		}
		
		for (int i = 0 ; i < slrProduction.size(); i ++)
		{			
			List<String> test;
			test = slrProduction.get(i).getRightSide();
            //System.out.println("Left side:" + slrProduction.get(i).getLeftSide() + " :: ");  
			for(int j =0;j < test.size();j++){
			  // System.out.println(test.get(j));
			} 
		}
	}
	public static void buildTheFollow()
	{
		//create empty set
		for(int i = 0; i < nonTerminal.size(); i ++)
		{
			Set<String> emptySet = new LinkedHashSet<String>();
			followSet.put(nonTerminal.get(i),emptySet);
			Set<String> emptySet1 = new LinkedHashSet<String>();
			followList.put(nonTerminal.get(i),emptySet1);
		}
		
		buildSetFollow(production);
		//buildFollow();
		for(int j = 0; j < nonTerminal.size(); j++)
		{
			String currentNonTerminal = nonTerminal.get(j);
			Set<String> duplicate = new LinkedHashSet<String>();
			buildfollowRecursive(currentNonTerminal, currentNonTerminal,duplicate);
		}
		//System.out.println("----------Final Product------------------");

		for (String name: followSet.keySet())
		{
         String key = name.toString();
         String value = followSet.get(name).toString();  
        // System.out.println(key + " :: " + value);  
		} 
		
		
		/*for(int i = 0; i <nonTerminal.size(); i ++)
		{
			String nonTm = nonTerminal.get(i);
			Set<String> originalSetFollow = new LinkedHashSet<String>();
			System.out.println("This is for: " +nonTerminal.get(i));

			buildSetFollow(nonTerminal.get(i),startSymbol, originalSetFollow, production);
			System.out.println(originalSetFollow.size());
			followSet.put(nonTm,originalSetFollow);
		}*/
	}
	//merge The FollowSet and the FollowList together
	
	public static void buildfollowRecursive(String currentPointedNTM, String originalNTM, Set<String>duplicate )
	{
		Set<String> tempSet = new LinkedHashSet<String>();
		tempSet = followList.get(currentPointedNTM);
		if (!tempSet.isEmpty())
		{
			for(String value : tempSet)
			{
				if(duplicate.contains(value))
				{
					continue;
				}
				else
				{
					Set<String> inputSet = new LinkedHashSet<String>();
					inputSet = followSet.get(originalNTM);
					inputSet.addAll(followSet.get(value));
					followSet.put(originalNTM,inputSet);
					duplicate.add(value);
					buildfollowRecursive(value,originalNTM,duplicate);
				}
			}
		}
		else
		{

			Set<String> inputSet = new LinkedHashSet<String>();
			inputSet = followSet.get(originalNTM);
			inputSet.addAll(followSet.get(currentPointedNTM));
			followSet.put(originalNTM,inputSet);
		}
	}
	
	public static void buildFollow()
	{
		System.out.println("-------------------------------");

		for (String name: followSet.keySet())
		{
         String key = name.toString();
         String value = followSet.get(name).toString();  
        // System.out.println(key + " :: " + value);  
		} 
		System.out.println("-------------------------------");
		for (String name: followList.keySet())
		{
         String key = name.toString();
         String value = followList.get(name).toString();  
        // System.out.println(key + " :: " + value);  
		} 
		
		
		
	}
	public static void buildSetFollow (List <ProductionGrammar> tempProduction)
	{
		//add start symbol for the P in our grammar
		Set<String> tempSet = new LinkedHashSet<String>();
		tempSet = followSet.get(startSymbol);
		tempSet.add("$");
		followSet.put(startSymbol,tempSet);
		//Go through the production right side for each element on the right side
		for(int i = 0 ; i < tempProduction.size(); i ++)
		{

			for(int j = 0; j< tempProduction.get(i).getRightSide().size(); j ++)
			{
				//check if it was nonterminal and last character. if it does than add the follow of the left side production
				if(nonTerminal.contains(tempProduction.get(i).getRightSide().get(j)) && j== tempProduction.get(i).getRightSide().size()-1)
				{
					//System.out.println("This is for Nontermial:"+ tempProduction.get(i).getRightSide().get(j));
					Set<String> newSet = new LinkedHashSet<String>();					
					newSet.addAll(followList.get(tempProduction.get(i).getRightSide().get(j)));
					newSet.add(tempProduction.get(i).getLeftSide());
					//System.out.println("Follow "+tempProduction.get(i).getLeftSide());
					followList.put(tempProduction.get(i).getRightSide().get(j), newSet);
					
				}
				else
				{
					if(nonTerminal.contains(tempProduction.get(i).getRightSide().get(j)))
					{
						//System.out.println("This is for Nontermial 2:"+ tempProduction.get(i).getRightSide().get(j));
						boolean allNTMempty = true;
						for(int k = j+1; k < tempProduction.get(i).getRightSide().size(); k ++ )
						{
							//if the next one is terminal
							//System.out.println(">>>>>>>>>>>>>>>>>:"+ tempProduction.get(i).getRightSide().get(k));

							if(terminal.contains(tempProduction.get(i).getRightSide().get(k)))
							{
								//System.out.println("This is for Terminal 2:"+ tempProduction.get(i).getRightSide().get(k));
								Set<String> newSet = new LinkedHashSet<String>();
								newSet = followSet.get(tempProduction.get(i).getRightSide().get(j));
								newSet.add(tempProduction.get(i).getRightSide().get(k));							
								followSet.put(tempProduction.get(i).getRightSide().get(j), newSet);
								allNTMempty = false;
								break;
							}
							//
							else
							{
								//System.out.println("This is for Nontermial Inside:"+ tempProduction.get(i).getRightSide().get(k));

								Set<String> newSet = new LinkedHashSet<String>();
								newSet = followSet.get(tempProduction.get(i).getRightSide().get(j));
								Set<String> tempFirst = new LinkedHashSet<String>();
								tempFirst= firstSet.get(tempProduction.get(i).getRightSide().get(k));
								if(tempFirst.contains("empty"))
								{
									tempFirst.remove("empty");
									newSet.addAll(tempFirst);
									followSet.put(tempProduction.get(i).getRightSide().get(j), newSet);
									continue;
								}
								else
								{
									newSet.addAll(tempFirst);
									followSet.put(tempProduction.get(i).getRightSide().get(j), newSet);
									allNTMempty = false;
									break;
								}
							
							}			
						}	
						//when all is empty
						if (allNTMempty)
						{
							Set<String> newSet = new LinkedHashSet<String>();					
							newSet.addAll(followList.get(tempProduction.get(i).getRightSide().get(j)));
							newSet.add(tempProduction.get(i).getLeftSide());
							//System.out.println("Follow "+tempProduction.get(i).getLeftSide());
							followList.put(tempProduction.get(i).getRightSide().get(j), newSet);
						}
					}	
				}
			}
		}
	}

	//Go to each of the nonTerminal List
	public static void buildTheFirst ()
	{
		for (int i = 0; i < nonTerminal.size(); i ++)
		{
			String nonTM = nonTerminal.get(i);
			Set<String> orginalSet = new LinkedHashSet<String>();
			buildSetFirst(nonTerminal.get(i),orginalSet,production);
			firstSet.put(nonTM,orginalSet);
			//orginalSet.clear();
			
		}
	}
	// The actual code to build the first
	public static Set<String> buildSetFirst (String currentNTM, Set<String> originalSet,List<ProductionGrammar> tempProduction )
	{
		for(int i = 0; i < tempProduction.size();i ++)
		{
			//check if the currentNTM = leftside to point to the right production
			if(tempProduction.get(i).getLeftSide().equals(currentNTM))
			{
				//First rule if it is a terminal or empty add it to the set;
				//Check if first character on the production is equal to terminal or Empty
				if(tempProduction.get(i).getRightSide().get(0).equals("empty") || terminal.contains(tempProduction.get(i).getRightSide().get(0)))
					{
					 originalSet.add(tempProduction.get(i).getRightSide().get(0));
					}
				//If it is nonterminal
				else
				{
					// if it pointed to the same such as L :: L D
					if (tempProduction.get(i).getRightSide().get(0).equals(currentNTM))
					{
						//if it pointed to the same make a new production and remove the same
						List<ProductionGrammar> product = new ArrayList<ProductionGrammar>();
						for(int k = 0 ; k < tempProduction.size();k ++)
						{
				            ProductionGrammar temppg = new ProductionGrammar(tempProduction.get(k).getLeftSide(), tempProduction.get(k).getRightSide());
							product.add(temppg);
						}
						product.remove(i);
						
						//make a new empty set to check for empty
						Set<String> tempSet = new HashSet<String>();
						// check if it return empty and size is 1 if it does move the CurrentTM to the next value
						if (buildSetFirst(currentNTM,tempSet, product).size() == 1 && buildSetFirst(currentNTM,tempSet, product).contains("empty"))
						{
							//System.out.print((buildSet(tempProduction.get(i).getRightSide().get(1),originalSet, tempProduction)[0]);
							originalSet.addAll(buildSetFirst(tempProduction.get(i).getRightSide().get(1),originalSet, tempProduction));						
						}
					}	
			//move on to the next value
			else
					{
						originalSet.addAll(buildSetFirst(tempProduction.get(i).getRightSide().get(0),originalSet, tempProduction));						
					}
				}	
			}
		}
		return originalSet;
	}
	
	
	// parse the production put in the list object....... 
	public static void setProduction ( List<String> grammar)
	{
		
		for(int i = 0 ; i < grammar.size(); i ++)
		{
			String[] productionSides = grammar.get(i).split("::");
			String tempString = productionSides[1].trim();
			String leftSide = productionSides[0].trim();
			String[] tempArray = tempString.split(" ");
			List<String> tempList =  new ArrayList<String>(Arrays.asList(tempArray));
            ProductionGrammar pg = new ProductionGrammar(leftSide, tempList);
			production.add(pg);	
		}
	}
	
	// set up the map for termimal
	public static void setValueInMap (List <String> TempList, Map<String, Set<String>> tempMap)
	{
		for (String temp : TempList) {
			   Set<String> tempset = new HashSet<String>();
			   tempset.add(temp);
			   tempMap.put(temp,tempset);
			}
	}
	
	//store the files.txt to a list
	public static void readFromBuffer( String fileName ,  List<String> theList) 
	{
		BufferedReader reader = null;
		try {
		    File file = new File(fileName);
		    reader = new BufferedReader(new FileReader(file));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	theList.add(line);	
		    	}
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
    }
//print to the Text file
 public static void printToTextFile(Map<String,Set<String>> mapPrintout, String nameoftheText)
 {
	 try {
		 // put the path for folder you want to create
		 String path = "C:\\Users\\Duc Le\\Desktop\\Compiler\\" ;
		 path = path.concat(nameoftheText);
		 path = path.concat(".txt");
		 File file = new File(path);

			// if file doesnt exists, then create it
		 if (!file.exists()) {
		    file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (String name: mapPrintout.keySet())
			{
	         String key = name.toString();
	         String value = mapPrintout.get(name).toString();  
	         bw.write(key + " :: " + value);  
	         bw.newLine();
			} 
			bw.close();
	} catch (IOException  e) {
		e.printStackTrace();
	}

 }
 public static void writeState()
 {
	 try {
		 // put the path for folder you want to create
		 String path = "C:\\Users\\Duc Le\\Desktop\\Compiler\\" ;
		 path = path.concat("state");
		 path = path.concat(".txt");
		 File file = new File(path);

			// if file doesnt exists, then create it
		 if (!file.exists()) {
		    file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < stateList.size(); i ++)
			{
				bw.write("----------------------------State "+i+"----------------------------");
		        bw.newLine();
		        for( List s : stateList.get(i))
		        {
		        	for(int j = 0; j < s.size() ; j ++)
		        	{
		        		if(j == 0)
		        		{
				        	bw.write(s.get(j).toString());
				        	bw.write(" :: ");
		        		}
		        		else
		        		{
		        			bw.write(s.get(j).toString());
		        			bw.write(" ");
		        		}
		        		
		        	}
		        	bw.newLine();
		        }
				bw.newLine();
							
			} 			
			bw.close();
	} catch (IOException  e) {
		e.printStackTrace();
	}
 }
 public static void writeSLR()
 {
	 try {
		 // put the path for folder you want to create
		 String path = "C:\\Users\\Duc Le\\Desktop\\Compiler\\" ;
		 path = path.concat("SLRtable");
		 path = path.concat(".txt");
		 File file = new File(path);

			// if file doesnt exists, then create it
		 if (!file.exists()) {
		    file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int state: slrTable.keySet())
			{
				bw.write("----------------------------State "+state+"----------------------------");
		        bw.newLine();
				List<SLRtable> tempList = new ArrayList<SLRtable>();
				tempList =slrTable.get(state);
				for(int i = 0; i < tempList.size();i ++)
				{
					bw.write("Symbol "+tempList.get(i).getSymbol());
			        bw.newLine();
					bw.write(tempList.get(i).getCommand());
			        bw.newLine();
					if(tempList.get(i).getState() != -1)
					bw.write("Number "+tempList.get(i).getState());
			        bw.newLine();	
			        bw.newLine();				

				}				
			} 			
			bw.close();
	} catch (IOException  e) {
		e.printStackTrace();
	}

 }
}
