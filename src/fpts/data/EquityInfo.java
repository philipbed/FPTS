package fpts.data;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import fpts.iterator.Container;
import fpts.iterator.Iterator;

/**
 * This class is a way to hold the information from the list of equities the system persists. This object represents
 * a type of equity. Think of it as the actual corporation itself.
 * @author Ryan, Philip
 *
 */
public class EquityInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private static EquityInfoList equities;
	private static ArrayList<String> nasdaqEqs;
	public static boolean initialized;
	private static double percentageLoaded = -1;
	public static DoubleProperty percentageLoadedProperty = new SimpleDoubleProperty(percentageLoaded);
	private static equitiesUpdater updateThread;			

	/**
	 * This method updates the values of the equity info objects in the static equities array list.
	 */
	public static void updateEquities(){
		percentageLoaded = 0;
		percentageLoadedProperty.setValue(percentageLoaded);
		ArrayList<EquityInfo> infos = new ArrayList<EquityInfo>();
		int index = 0;
		while(index < equities.size()){
			for(int si = 0/*Small Index*/; si <= 100 && index < equities.size(); si++){
				infos.add(equities.get(index));
				index++;
			}
			ArrayList<Double> newValues = getSymValues(infos);
			for(int esi = 0/*Extra Small Index*/; esi < infos.size(); esi++){
				infos.get(esi).updateValue(newValues.get(esi));
			}
			infos = new ArrayList<EquityInfo>();
			percentageLoaded = index/828.0;
			percentageLoadedProperty.setValue(percentageLoaded);
		}
		percentageLoaded = 1;
		percentageLoadedProperty.setValue(percentageLoaded);
		initialized = true;

		//Portfolio.currentPortfolio.updateWatchlist();
	}

	/**
	 * This method instantiates the static equities arraylist object. Must be called before equities is used.
	 * It also populates the nasdaqEqs array list.
	 * It does this inside a seperate thread, so that immense loading times do not affect the running of the program.
	 */
	public static void instantiateEquities(){
		if(!initialized){
			//Anonymous thread class
			new Thread(){
				@Override
				public void run(){
					//Populate the list of nasdaq stocks
					percentageLoaded = 0;
					percentageLoadedProperty.setValue(percentageLoaded);
					URL nasList;
					BufferedReader nasIn;
					try{
						nasdaqEqs = new ArrayList<String>();
						nasList = new URL("ftp://ftp.nasdaqtrader.com/SymbolDirectory/nasdaqlisted.txt");
						nasIn = new BufferedReader(new InputStreamReader(nasList.openStream()));
						nasIn.readLine();
						String inputLine;
						while ((inputLine = nasIn.readLine()) != null){
							nasdaqEqs.add(inputLine.substring(0, inputLine.indexOf("|")));
						}
						nasIn.close();
					}catch(Exception e){
						e.printStackTrace();
					}

					//Do initialization
					equities = (new EquityInfo("","",0,null)).new EquityInfoList();
					String line;
					String symbol = null;
					String name;
					double value;
					EquityInfo nei;
					ArrayList<String> markAvgs = new ArrayList<String>();
					try{
						BufferedReader pbuffer = new BufferedReader(new FileReader("formattedEquities.csv"));
						line = "";
						while((line = pbuffer.readLine()) != null){
							markAvgs = new ArrayList<String>();
							symbol = line.substring(0, line.indexOf(","));
							line = line.substring(line.indexOf(",") + 1);
							value = 0;
							if(line.indexOf("\",") >= 0){
								name = line.substring(1, line.indexOf("\","));
								line = line.substring(line.indexOf("\",") + 2);
								while(line.indexOf(",") >= 0){
									markAvgs.add(line.substring(0, line.indexOf(",")));
									line = line.substring(line.indexOf(",") + 1);
								}
								markAvgs.add(line);
								if(nasdaqEqs.contains(symbol)){
									markAvgs.add("NASDAQ100");
								}
								nei = new EquityInfo(symbol, name, value, markAvgs);
							}else{
								name = line.substring(1, line.length() - 1);
								if(nasdaqEqs.contains(symbol)){
									markAvgs.add("NASDAQ100");
								}
								nei = new EquityInfo(symbol, name, value, markAvgs);
							}
							equities.add(nei);
							percentageLoaded += 1/828.0;
							percentageLoadedProperty.setValue(percentageLoaded);
						}
						percentageLoaded = 1;
						percentageLoadedProperty.setValue(percentageLoaded);
						pbuffer.close();
						//System.out.println("called");
						updateEquities();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	/**
	 * This method is a helper for instantiateEquitites and updateEquitites. It parses for stock values from a yahoo url.
	 * @param infos The ticker symbol of the equities who's value we're parsing for.
	 * @return Returns a double that is the value of the stock tickSym. Returns 0 if invalid symbol.
	 * @throws IOException Thrown if we have trouble using br.readLine();
	 */
	private static ArrayList<Double> getSymValues(ArrayList<EquityInfo> infos){
		try{
			String url = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(";
			for(EquityInfo ei: infos){
				url += "%22" + ei.getTS() + "%22,";
			}
			url = url.substring(0, url.length() - 1);
			url += ")&env=store://datatables.org/alltableswithkeys";
			URL yahoo = new URL(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(yahoo.openStream()));
			br.readLine();
			String infoLine = br.readLine();
			ArrayList<Double> values = new ArrayList<Double>();
			for(int i = 0; i < infos.size(); i++){
				try{
					String valS = "";
					if(infoLine.indexOf("<Ask>") >= 0){
						valS = infoLine.substring(infoLine.indexOf("<Ask>") + 5, infoLine.indexOf("</Ask>"));
						infoLine = infoLine.substring(infoLine.indexOf("</quote>") + 8);
					}else if(infoLine.indexOf("<Bid>") >= 0){
						valS = infoLine.substring(infoLine.indexOf("<Bid>") + 5, infoLine.indexOf("</Bid>"));
						infoLine = infoLine.substring(infoLine.indexOf("</quote>") + 8);
					}else if(infoLine.indexOf("<BookValue>") >= 0){
						valS = infoLine.substring(infoLine.indexOf("<BookValue>") + 11, infoLine.indexOf("</BookValue>"));
						infoLine = infoLine.substring(infoLine.indexOf("</quote>") + 8);
					}else{
						System.out.println(infos.get(i));
					}
					values.add(Double.parseDouble(valS));
				}catch(StringIndexOutOfBoundsException sioobe){sioobe.printStackTrace();}
			}
			br.close();
			return values;
		}catch(Exception e){
			return null;
		}
	}

	/**
	 * This method is responsible for starting the thread that updates equity values every time period.
	 * It is also responsible for updating that time period. To update, simply call with the new time interval.
	 * @param timeIntervalMinutes The number of minutes in between updating the equity info values.
	 */
	public static void startUpdateThread(double timeIntervalMinutes){
		if(updateThread == null){
			updateThread = (new EquityInfo("","",0,new ArrayList<String>())).new equitiesUpdater(timeIntervalMinutes); //Private internal classes and static methods don't play well together.
			updateThread.start();
		}else{
			updateThread.setTimeInterval(timeIntervalMinutes);
			updateThread.interrupt(/*Wake Me Up Inside (The while loop)*/);
		}
	}
	
	/**
	 * A getter for an iterator over the EquityInfoList object that keeps track of all of our EquityInfos.
	 * @return Returns an iterator over equities.
	 */
	public static Iterator<EquityInfo> getEquityInfoIterator(){
		return equities.getIterator();
	}
	
	/**
	 * Searches for EquityInfos that match the criteria provided.
	 * Note that not all parameters need to be filled out, they can be parsed as empty strings.
	 * @param name - EquityInfo's name
	 * @param tickerSymbol - EquityInfo's ticker symbol
	 * @param marketIndex - Market on which EquityInfo is stored
     * @return Returns the results.
     */
	public static ArrayList<EquityInfo> getMatchingInfo(String name, String tickerSymbol, String marketIndex){
		ArrayList<EquityInfo> results = new ArrayList<>();
		Iterator<EquityInfo> itr = equities.getIterator();
		EquityInfo equity;
		while(itr.hasNext()){
			equity = itr.next();
			if (equity.isMatch(tickerSymbol, name, marketIndex)){
				if(equity.startsWith(tickerSymbol, name, marketIndex)){
					results.add(0, equity);
				}else{
					results.add(equity);
				}
			}
		}
		return results;
	}
	
	private String equityName;
	private String tickerSymbol;
	private ArrayList<String> marketIndices;
	private double value;

	/**
	 * A constructor for the constants in this EquityInfo. Should never be called outside of EquityInfo, hence the private scope.
	 * Instead of creating  your own, you should be getting EquityInfo objects from the static arraylist in this class called equities.
	 * @param markDex The marketIndices of this equity info.
	 * @param name The name of the equity defined by this equity info.
	 * @param tickSym The ticker symbol of the equity defined by this equity info.
	 */
	private EquityInfo(String tickSym, String name, double v, ArrayList<String> markDex){
		equityName = name;
		tickerSymbol = tickSym;
		value = v;
		marketIndices = markDex;
		}
	
	/**
	 * A method to determine if this equityInfo is a possible match for the information provided.
	 * This method is not case sensitive.
	 * @param tickSym The ticker symbol (or fragment of) this equity to see if matches. If you don't have one, make it an empty string or null.
	 * @param name The name (or fragment of) this equity info to see if matches. If you don't have one, make it an empty string or null.
	 * @param markDex The market index (or fragment of) this equity info to see if matches. If you don't have one, make it an empty or null.
	 * @return Returns true if all non-empty and non-null paramaters are matches for the ticker symbol, name, and a market index for this equity info.
	 */
	public boolean isMatch(String tickSym, String name, String markDex){
		if(tickSym != null){
			tickSym = tickSym.toLowerCase();
		}
		if(name != null){
			name = name.toLowerCase();
		}
		if(markDex != null){
			markDex = markDex.toLowerCase();
		}
		boolean isMatch = true;
		if(tickSym != null && !tickSym.equals("")){
			if(tickerSymbol.toLowerCase().indexOf(tickSym) < 0){
				isMatch = false;
			}
		}
		if(name != null && !name.equals("")){
			if(equityName.toLowerCase().indexOf(name) < 0){
				isMatch = false;
			}
		}
		if(markDex != null && !markDex.equals("")){
			boolean oneTrue = false; //Boolean for if at least one index matches
			for(int i = 0; i < marketIndices.size(); i++){
				if(marketIndices.get(i).toLowerCase().indexOf(markDex) >= 0){
					oneTrue = true;
				}
			}
			if(!oneTrue){
				isMatch = oneTrue;
			}
		}
		return isMatch;
	}
	
	/**
	 * A simple method to update the value of this equity.
	 * @param val The new value of this equity.
	 */
	public void updateValue(double val){
		value = val;
		//System.out.println("pls");
	}
	
	/**
	 * A helper for getMatchingInfo to see if this EquityInfo should be put on top of the list. 
	 * If one of the fields starts with one of the search terms, return true. 
	 * This method is not case sensitive.
	 * @param tickSym The ticker symbol (or fragment of) this equity to see if matches. If you don't have one, make it an empty string or null.
	 * @param name The name (or fragment of) this equity info to see if matches. If you don't have one, make it an empty string or null.
	 * @param markDex The market index (or fragment of) this equity info to see if matches. If you don't have one, make it an empty or null.
	 * @return Returns true if one of the fields starts with one of the search terms.
	 */
 	private boolean startsWith(String tickSym, String name, String markDex){
 		if(tickSym != null){
			tickSym = tickSym.toLowerCase();
		}
		if(name != null){
			name = name.toLowerCase();
		}
		if(markDex != null){
			markDex = markDex.toLowerCase();
		}
		boolean startsWith = false;
		if(tickSym != null && !tickSym.equals("")){
			if(tickerSymbol.toLowerCase().indexOf(tickSym) == 0){
				startsWith = true;
			}
		}
		if(name != null && !name.equals("")){
			if(equityName.toLowerCase().indexOf(name) == 0){
				startsWith = true;
			}
		}
		if(markDex != null && !markDex.equals("")){
			for(int i = 0; i < marketIndices.size(); i++){
				if(marketIndices.get(i).toLowerCase().indexOf(markDex) == 0){
					startsWith = true;
				}
			}
		}
		return startsWith;
	}
		
	/**
	 * A getter for the name of this equity info.
	 * @return Returns the name of this equity info.
	 */
	public String getName(){
		return equityName;
	}
	
	/**
	 * A getter for the ticker symbol of this equity info.
	 * @return Returns the ticker symbol of this equity info.
	 */
	public String getTS(){
		return tickerSymbol;
	}
	
	/**
	 * A getter for the value of this equity info.
	 * @return Returns the value of this equity info.
	 */
	public double getValue(){
		//This is neccessary. Static variables are not serialized, so the list is not saved. This is why we have to initialize the list ever time.
		//Because we need to re initialize the list every time we run, serialized equity infos are not pointers to the list's equity infos.
		try{
			return equities.get(equities.indexOf(this)).value;
		}catch(Exception e){
			return value;
		} 
	}

	/**
	 * A getter for the industry sector of this equity info.
	 * @return Returns the industry sector of this equity info.
	 */
	public ArrayList<String> getMarketIndices(){
		return marketIndices;
	}

	/**
	 * provides the table cell with the string for the ticker symbol
	 * @return - the String Property of the ticker symbol
     */
	public StringProperty tickerProperty(){
		StringProperty tickersym = new SimpleStringProperty(tickerSymbol);
		return tickersym;
	}

	/**
	 * provides the table cell with the string for the name of the equity
	 * @return - the String Property of the equity name
	 */
	public StringProperty nameProperty(){
		
		StringProperty name = new SimpleStringProperty(equityName);
		return name;
	}

	/**
	 * provides the table cell with the double for the equity value
	 * @return - the Double Property of the equity value
	 */
	public DoubleProperty valueProperty(){

		DoubleProperty val = new SimpleDoubleProperty(value);
		return val;
	}

	/**
	 * provides the table cell with the string for the market index
	 * @return - the String Property of the market index
	 */
	public StringProperty marketProperty(){
		
		ObservableList<String> market = FXCollections.observableArrayList();
		for(String s: marketIndices){
			market.add(s);
		}
		// remove the brackets from the market index list

		String s1 = removeBrackets(market.toString());
		StringProperty s2 = new SimpleStringProperty(s1);
		return s2;
		
		
	}
	
	/**
	 * A method to get this equityInfo as a string.
	 * @return Returns this object in the format [EquityInfo: (TickerSymbol=$tickSym) (Name=$name) (Value=$value) (MarketIndices=$markDex)]
	 */
	@Override
	public String toString(){
		return "[EquityInfo: (TickerSymbol=" + getTS() + ") (Name=" + getName() + ") (Value=" + getValue() + ") (MarketIndices=" + getMarketIndices() + ")]";
	}

	/**
	 * helper function to remove the trailing brackets from a string
	 * @param sp - the input string
	 * @return - the String without trialing brackets
     */
	private String removeBrackets(String sp){
		String newS = "";
		for (int i = 0; i < sp.length(); i++) {
			if(sp.charAt(i) == '[' || sp.charAt(i) == ']'){
				continue;
			}
			newS += sp.charAt(i);
		}

		return newS;
	}

	/**
	 * If the ticker symbol for two equity infos are the same, they are considered equal.
	 * @param obj The object to compare to this.
	 * @return Returns true if obj is an equity info and its ticker symbol matches this's ticker symbol.
	 */
	@Override
	public boolean equals(Object obj){
		return obj instanceof EquityInfo && ((EquityInfo) obj).tickerSymbol.equals(tickerSymbol);
	}
	
	/**
	 * This class is an internal class responsible for timing the updating the value of the equity infos in the static equities array list.
	 * The only thing that should be touching this is the startUpdateThread() method in EquityInfo.
	 * @author Ryan
	 */
	private class equitiesUpdater extends Thread {
		private int timeIntervalSeconds; 
		
		/**
		 * A constructor for an equitiesUpdater.
		 * @param timeIntervalMinutes The number of minutes in between updates.
		 */
		public equitiesUpdater(double timeIntervalMinutes){
			timeIntervalSeconds = (int) (timeIntervalMinutes * 60);
		}
		
		/**
		 * A method to set a new value for the time interval between equity info value updates.
		 */
		public void setTimeInterval(double timeIntervalMinutes){
			timeIntervalSeconds = (int) (timeIntervalMinutes * 60);
		}
		
		/**
		 * This is the main equity info value update loop.
		 * DO NOT CALL THIS METHOD DIRECTLY.
		 * Use (equitiesUpdater object name).start();
		 * If you call this directly, the new thread will not have started, and the program will be in an infinite loop.
		 */
		@Override
		public void run(){
			while(true){
				try{
					Thread.sleep(timeIntervalSeconds*1000);
					updateEquities();
					Portfolio.currentPortfolio.updateWatchlist();
				}catch(InterruptedException ie){
					//We catch it, but do nothing, because we want it to return to the start of the loop
				}catch(Exception e){
					e.printStackTrace(); //If this happens, something has genuinely gone wrong
				}
			}
		}
	}
	
	/**
	 * This class helps with encapsulation of EquityInfo data and more importantly, gives us something with which we can implement the iterator pattern.
	 * @author Ryan
	 */
	private class EquityInfoList implements Container<EquityInfo>{
		
		private ArrayList<EquityInfo> equities;

		public EquityInfoList(){
			equities = new ArrayList<EquityInfo>();
		}
		/**
		 * Inherited from our Iterator<E> interface.
		 * This method is used to let people get our equities without letting them edit the internal holding structure.
		 * @return Returns an EquityInfoListIterator that iterates over our equities variable.
		 */
		@Override
		public Iterator<EquityInfo> getIterator() {
			return new EquityInfoListIterator();
		}
		
		/**
		 * A method to add an EquityInfo to the internal list.
		 * @param ei The EquityInfo object to add.
		 */
		public void add(EquityInfo ei){
			equities.add(ei);
		}
		
		/**
		 * A method to get an EquityInfo object from the internal list.
		 * @param i The index of the EquityInfo object you want to get.
		 * @return Returns the EquityInfo object at index i.
		 */
		public EquityInfo get(int i){
			return equities.get(i);
		}
		
		/**
		 * A method to get the index of an EquityInfo object.
		 * @param ei The EquityInfo object to get the index of.
		 * @return Returns the index of ei.
		 */
		public int indexOf(EquityInfo ei){
			return equities.indexOf(ei);
		}
		
		/**
		 * A method to get the number of EquityInfo objects in the internal list.
		 * @return Returns the number of EquityInfo objects in the internal list.
		 */
		public int size(){
			return equities.size();
		}
		
		/**
		 * This is the internal iterator class. 
		 * This is used so that people can iterate over our internal list without being able to edit it.
		 * The objects it stores cannot be edited, and they don't have a reference to the internal list.
		 * @author Ryan
		 */
		private class EquityInfoListIterator implements Iterator<EquityInfo>{
			private int index = 0;
			
			/**
			 * One of two important iterator methods.
			 * This method tells you if you have iterated over the entire list or if there is a next EquityInfo in the list to move on to.
			 * @return Returns true if we have not reached the end of the list.
			 */
			@Override
			public boolean hasNext() {
				return index != equities.size() - 1;
			}
			
			/**
			 * The second important iterator methods.
			 * This method gets the next EquityInfo in the list.
			 * @return Returns an EquityInfo object if hasNext is true, or null is hasNext is false;
			 */
			@Override
			public EquityInfo next() {
				EquityInfo toReturn;
				try{
					toReturn = equities.get(index);
				}catch(IndexOutOfBoundsException ioobe){
					toReturn = null;
				}
				index++;
				return toReturn;
			}
		}

	}
}
