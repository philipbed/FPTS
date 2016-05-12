package fpts.data;
import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fpts.iterator.Iterator;

public class Portfolio implements Serializable{
	
	/**
	 * This variable can prevent problems with loading old, incompatible versions of portfolio.
	 * Honestly, it's not super neccesary, but I don't like my IDE complaining at me.
	 * If we ever make large scale variable changes, increment this number by 1.
	 */
	private static final long serialVersionUID = 1L;

	public static Portfolio currentPortfolio;
	
	private ArrayList<Transaction> transHistory;
	private ArrayList<Holding> holdings;
	private ArrayList<WatchedEquity> watchlist;
	private String userID;
	private String password;
	private final static transient String key = "randomizes"; //Must be 10 unique letters.
	private boolean hasChanged;
	
	
	/**
	 * Constructor for Portfolio. Creates a new Portfolio with no holdings or transactions
	 * @param uid The userID of this portfolio.
	 * @param pwd The unencrypted password for this portfolio. Encrypts in constructor.
	 */
	public Portfolio(String uid, String pwd){
		userID = uid;
		password = pwd;
		encryptPwd();
		watchlist = new ArrayList<WatchedEquity>();

		hasChanged = false;
		holdings = new ArrayList<Holding>();
		transHistory = new ArrayList<Transaction>();
	}

	/**
	 * Constructor for Portfolio. Creates a new Portfolio with Holdings and Transactions imported from a csv file.
	 * @param uid The userID of this portfolio.
	 * @param pwd The unencrypted password for this portfolio. Encrypts in constructor.
	 * @param filename The csv file containing portfolio info.
	 */
	public Portfolio(String uid, String pwd, String filename){
		//TODO Fix and update for R2
		userID = uid;
		password = pwd;
		encryptPwd();
		holdings = new ArrayList<Holding>();
		transHistory = new ArrayList<Transaction>();
		watchlist = new ArrayList<WatchedEquity>();

		importFile(filename);


	}

	/**
	 * Method for accessing the portfolio's holdings
	 * @return The portfolio's holdings
	 */
	public ArrayList<Holding> getHoldings()
	{
		return holdings;
	}

    /**
     * Method for accessing the portfolio's equities
     * @return The portfolio's equities
     */
    public ArrayList<Equity> getEquities(){
        ArrayList<Equity> equities = new ArrayList<>();

        for(Holding h : holdings){
            if(h instanceof Equity && ((Equity) h).getEquityInfo()!=null){
                equities.add((Equity) h);
            }
        }
        return equities;
    }

	/**
	 * Method for accessing MarketAverage Stocks
	 * @return The portfolio's MarketAverage stocks
     */
	public ArrayList<MarketAverage> getMarketStocks() {
		ArrayList<MarketAverage> marketAvgs = new ArrayList<>();
		for (Holding h : holdings) {
			if (h instanceof MarketAverage) {
				marketAvgs.add((MarketAverage) h);
			}
		}
		return marketAvgs;
	}

	/**
	 * This is the method for adding and executing transactions.
	 * @param hgained The holding (if any) gained by this transaction.
	 * @param hlost The holding (if any) lost by this transaction.
	 * @param datetime The date the transaction was performed.
	 * @return Returns true if transaction was done succesfully.
	 */
	public boolean addTransaction(Holding hgained, Holding hlost, String datetime){
		hasChanged = true;
		Transaction t = new Transaction(hgained, hlost, datetime);
		if(!subHolding(hlost)){
			return false;
		}
		transHistory.add(t);
		addHolding(hgained);
		return true;
	}
	
	/**
	 * This method simply creates a transaction and adds it to the history.
	 * IT DOES NOT EXECUTE IT.
	 * If you want to execute this transaction, use addTransaction instead.
	 * @param hgained The holding (if any) gained by this transaction.
	 * @param hlost The holding (if any) lost by this transaction.
	 * @param datetime The date the transaction was performed.
	 */
	public void addToTransactionHistory(Holding hgained, Holding hlost, String datetime){
		hasChanged = true;
		Transaction t = new Transaction(hgained, hlost, datetime);
		transHistory.add(t);
	}
	
	/**
	 * Import method for holdings, which changes the state of the Portfolio to modified
	 * 
	 */
	public void importHolding(Holding h){
		hasChanged = true;
		addHolding(h);
	}
	
	/**
	 * Searches for EquityInfos that match the criteria provided.
	 * Note that not all parameters need to be filled out, they can be parsed as empty strings.
	 * @param name - EquityInfo's name
	 * @param tickerSymbol - EquityInfo's ticker symbol
	 * @param marketIndex - MarketAverage on which EquityInfo is stored
     * @return Returns the results.
     */
	public ArrayList<Equity> getMatchingEquities(String name, String tickerSymbol, String marketIndex){
		ArrayList<Equity> equities = new ArrayList<Equity>();
		for(int i = 0; i < holdings.size(); i++){
			if(holdings.get(i) instanceof Equity){
				equities.add((Equity) holdings.get(i));
			}
		}
		ArrayList<Equity> results = new ArrayList<Equity>();
		for (Equity equity : equities) {
			if (equity.isMatch(tickerSymbol, name, marketIndex)){
				results.add(equity);
			}
		}
		return results;
	}
	
	/**
	 * An import method for a cash account.
	 * @param name The name of the account.
	 * @param amount The amount of money in the account.
	 * @param date The date the account was created.
	 */
	public void importCashAccount(double amount, String name, String date){
		hasChanged = true;
		CashAccount ca = new CashAccount(amount, name, date);
		importHolding(ca);
	}
	
	/**
	 * An import method for an equity.
	 * @param eqinfo The Equity's information.
	 * @param aprice The aquisition price of the equity.
	 * @param adatetime The date the equity was aquired.
	 * @param numshares The number of shares of the equity.
	 * @param cashacc If the equity was aquired using cash from a cash account.
	 */
	public void importEquity(EquityInfo eqinfo, double aprice, String adatetime, double numshares, boolean cashacc){
		hasChanged = true;
		Equity equity = new Equity(eqinfo, aprice, adatetime, numshares, cashacc);
		importHolding(equity);
	}
	
	/**
	 * Method used to get the User's ID
	 * @return Returns the userID of this portfolio.
	 */
	public String getUserID(){
		return userID;
	}
	
	/**
	 * A helper method for adding holdings to our internal holdings list.
	 * @param holding The holding to be added.
	 */
	public void addHolding(Holding holding){
		hasChanged = true;
		boolean duplicate = false;
		for(Holding hld:holdings){
			if(holding.getName().compareTo(hld.getName())==0){
				hld.add(holding);
				duplicate = true;
			}
		}
		if(!duplicate){
			holdings.add(holding);
		}
	}

    /**
     * A method for removing holdings from the portfolio's holdings
     * @param holding the holding to be removed
     * @return true if the holding was removed
     */
    public boolean removeHolding(Holding holding){
        return holdings.remove(holding);
    }
	
	/**
	 * A helper method for subtracting holdings from our internal holdings list.
	 * To be clear, holdings can only be subtracted if they have the same name.
	 * If two cash accounts are subtracted, their values will be subtracted.
	 * If two equities are subtracted, their num shares will be subtracted.
	 * @param h The holding to be subtracted.
	 * @return Returns false if subtracting would result in negative cash or shares.
	 */
	public boolean subHolding(Holding h){
		hasChanged = true;

		if(holdings.contains(h)){
			boolean able;
			able = holdings.get(holdings.indexOf(h)).subtract(h);
			if(able == false){
				return false;
			}
			if(holdings.get(holdings.indexOf(h)).getTotalValue() == 0){
				holdings.remove(holdings.indexOf(h));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * This is a simple method to remove all Equities of which we have 0 shares or less from our account.
	 */
	public void trimHoldings(){
		for(int i = 0; i < holdings.size(); i++){
			if(holdings.get(i).getTotalValue() <= 0 && holdings.get(i) instanceof Equity){
				holdings.remove(i);
			}
		}
	}
	
	/**
	 * Saves this object to a file in the portfolios folder.
	 * Name format is the userID.pfo
	 */
	public void save(){
		try{
	         FileOutputStream fileOut = new FileOutputStream("portfolios/" + userID + ".pfo");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(this);
	         out.close();
	         fileOut.close();
	      }catch(FileNotFoundException fnfe){
	    	  File f = new File("portfolios");
	    	  if (!f.exists()) {
	    		  f.mkdir();
	    	  }
	    	  save();
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }
	}
	
	/**
	 * Attempts to load a portfolio. The boolean returned is meant to tell the GUI to prompt user to re-enter credentials if failed.
	 * @param uid The userID of the portfolio we're attempting to load.
	 * @param pwd The password the user inputted to try and acces the portfolio.
	 * @return Returns true if loaded sucessfully, false if not. Will return false if passwords do not match.
	 */
	public static void load(String uid, String pwd) throws Exception{
		try{
			FileInputStream fileIn = new FileInputStream("portfolios/" + uid + ".pfo");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			currentPortfolio = (Portfolio) in.readObject();
			currentPortfolio.decryptPwd(); //ALWAYS REENCRYPT, its not hard
			if(!pwd.equals(currentPortfolio.password)){
				currentPortfolio.encryptPwd();//reencrypt
				currentPortfolio = null;
				in.close();
				fileIn.close();
				throw new Exception("Incorrect Password.");
			}
			currentPortfolio.encryptPwd();//reencrypt
			currentPortfolio.hasChanged = false;
			in.close();
			fileIn.close();
		}catch(FileNotFoundException fnfe){
			if (new File("portfolios").exists()) {
			   throw new Exception("Incorrect Username.");
			}
			throw new Exception("Portfolios folder not found.");
		}catch(Exception e){
			e.printStackTrace();
			currentPortfolio = null;
			throw e;
		}
	}
	
	/**
	 * This is the method to encrypt the password.
	 * To encrypt, we need a 10 letter key with no repeating characters.
	 * This method breaks the password down into characters, then takes the decimal representation of that character.
	 * The numbers in the decimal representation are then replaced by the letter at that numbers position in the key.
	 * For example, if we have the letter m, its decimal is 109. If the key is "algorithms", then 109 becomes las.
	 * Finally, all of the 3 letter representations of the char are concatenated for the final encrypted string.
	 * A final example, "password" becomes llgashllillillslllllrlaa
	 */
	private void encryptPwd(){
		String encrypted = "";
		for(int i = 0; i < password.length(); i++){
			char currentChar = password.charAt(i);
			String currentCharEncrypting;
			if(currentChar >= 100){ //This if adds a 0 to the front of the string if it's only 2 digits.
				currentCharEncrypting = "" + (int) currentChar;
			}else{
				currentCharEncrypting = "0" + (int) currentChar;
			}
			for(int replaceIndex = 0; replaceIndex < 10; replaceIndex++){ //Replace all numbers with appropriate letters.
				currentCharEncrypting = currentCharEncrypting.replaceAll("" + replaceIndex, key.substring(replaceIndex, replaceIndex+1));
			}
			encrypted += currentCharEncrypting;
		}
		password = encrypted;
	}
	
	/**
	 * This is the decryption method. It performs the encryption algorithm in reverse. 
	 * Please see the encryption method documentation for an overview of the algorithm.
	 */
	private void decryptPwd(){
		String decrypted = "";
		for(int i = 0; i < password.length(); i += 3){
			String currentCharDecrypting = password.substring(i, i + 3);
			for(int replaceIndex = 0; replaceIndex < 10; replaceIndex++){ //Replace letters with appropriate numbers.
				currentCharDecrypting = currentCharDecrypting.replaceAll(key.substring(replaceIndex, replaceIndex+1), "" + replaceIndex);
			}
			decrypted += (char) Integer.parseInt(currentCharDecrypting);
		}
		password = decrypted;
	}
	
	/**
	 * A method to see if this Portfolio has changed since being loaded.
	 * @return Returns true if the portfolio has changed since being loaded.
	 */
	public boolean hasChanged(){
		return hasChanged;
	}

	/**
	 * Method for accessing the Portfolio's cash accounts
	 * @return The Portfolio's cash accounts
     */
	public ArrayList<CashAccount> getCashAccounts(){
		ArrayList<CashAccount> accounts = new ArrayList<CashAccount>();
		for(int i = 0; i < holdings.size(); i++){
			if(holdings.get(i) instanceof CashAccount){
				accounts.add((CashAccount) holdings.get(i));
			}
		}
		return accounts;
	}

	/**
	 * Method for accessing transaction history
	 * @return an arraylist of transactions
     */
	public ArrayList<Transaction> getTransactions(){return this.transHistory;}

	/**
	 * Method for adding Transactions to the transaction history
	 * @param trans - the transaction to be added.
     */
	public void addTransactionToHistory(Transaction trans){this.transHistory.add(trans);}

	/**
	 * Method for removing transactions from the Transaction history
	 * @param trans - the transaction to be added.
	 */
	public void removeTransactionFromHistory(Transaction trans){this.transHistory.remove(trans);}

/*********************WATCHLIST METHODS*************************/

	public ArrayList<WatchedEquity> getWatchlist(){return watchlist;}

	/**
	 * Adds WatchedEquity to watchlist
	 * @param equity - Equity to be watched
     */
	public void addToWatchlist(WatchedEquity equity){
		hasChanged = true;
		watchlist.add(equity);
	}

	/**
	 * Removes WatchedEquity from watchedlist
	 * @param equity - Equity to remove from watchlist
     */
	public void removeFromWatchlist(WatchedEquity equity){
		hasChanged = true;
		watchlist.remove(equity);
	}

	/**
	 * Updates all WatchedEquities in watchlist with new EquityInfo
	 */
	public void updateWatchlist(){
		for (int i = 0; i < watchlist.size() ; i++) {
			watchlist.get(i).updateEquityInfo();
		}
	}

/**********************EXPORT / IMPORT***************************/
	/**
	 * Exports portfolio in the user-defined filepath
	 * @param filepath - The filepath of the output file
     */
	public void export(String filepath){
		try{
			PrintWriter writer = new PrintWriter(filepath, "UTF-8");
			ArrayList<Equity> eqs = new ArrayList<Equity>();
			ArrayList<CashAccount> cas = new ArrayList<CashAccount>();
			for(int i = 0; i < holdings.size(); i++){
				Holding h = holdings.get(i);
				if(h instanceof Equity){
					eqs.add((Equity) h);
				}else if(h instanceof CashAccount){
					cas.add((CashAccount) h);
				}
			}
			String newLine = "";
			for(Equity e: eqs){
				newLine = "\"EQUITY\",";
				newLine += "\"" + e.getEquityInfo().getTS() + "\",";
				newLine += "\"" + e.getNumShares() + "\"";
				newLine += System.lineSeparator();
				writer.write(newLine);
			}
			for(CashAccount ca: cas){
				newLine = "\"CA\",";
				newLine += "\"" + ca.getName() + "\",";
				newLine += "\"" + ca.getTotalValue() + "\"";
				newLine += System.lineSeparator();
				writer.write(newLine);
			}
			for(WatchedEquity we: watchlist){
				newLine = "\"WATCHING\",";
				newLine += "\"" + we.getEquityInfo().getTS() + "\"";
				newLine += System.lineSeparator();
				writer.write(newLine);
			}
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		
		/*
		try {
			BufferedWriter filebuffer = new BufferedWriter(new FileWriter(filepath));
			ArrayList<Equity> equities = getEquities();
			ArrayList<MarketAverage> marketAvgs = getMarketStocks();
			ArrayList<CashAccount> cashAccounts = getCashAccounts();
			ArrayList<Transaction> transactions = getTransactions();

			for (Equity eq:equities){
				String fca;
				if (eq.getFromCashAccount()){
					fca = "Y";
				}else{
					fca  = "N";
				}
				filebuffer.write("\""+eq.getEquityInfo().getTS()+"\",\""+eq.getEquityInfo().getName()+"\",\""+eq.getShareValue()+"\",\""+eq.getDate()+"\",\""+eq.getNumShares()+"\",\""+fca+"\"");
				filebuffer.newLine();
			}
			filebuffer.write("-----------------\n");
			for (MarketAverage m:marketAvgs){
				String fca;
				if (m.getFromCashAccount()){
					fca = "Y";
				}else{
					fca  = "N";
				}
				filebuffer.write("\""+m.getName()+"\",\""+m.getShareValue()+"\",\""+m.getDate()+"\",\""+m.getNumShares()+"\",\""+fca+"\"");
				filebuffer.newLine();
			}
			filebuffer.write("-----------------\n");
			for(CashAccount ca: cashAccounts){
				filebuffer.write("\""+ca.getName()+"\",\""+ca.getDate()+"\",\""+ca.getTotalValue()+"\"");
				filebuffer.newLine();
			}
			filebuffer.write("-----------------\n");
			for (Transaction t: transactions){
				Holding holding;
				Holding cashAccount;
				String type = "";
				if (t.getLoss() instanceof CashAccount){
					holding = t.getGain();
					cashAccount = t.getLoss();
					if(holding instanceof Equity) {
						filebuffer.write("\""+cashAccount.getName()+"\",-,\""+cashAccount.getTotalValue()+"\",\""+cashAccount.getDate()+"\"");
						filebuffer.newLine();
						filebuffer.write("\""+((Equity) holding).getEquityInfo().getTS()+"\",E,+,\"" + ((Equity)holding).getShareValue()+"\",\"" + ((Equity)holding).getNumShares()+"\"");
						filebuffer.newLine();
					}
					else if (holding instanceof MarketAverage){
						filebuffer.write("\""+cashAccount.getName()+"\",-,\""+cashAccount.getTotalValue()+"\",\""+cashAccount.getDate()+"\"");
						filebuffer.newLine();
						filebuffer.write("\""+holding.getName()+"\",M,+,\""+ ((Equity)holding).getShareValue()+"\",\""+ ((Equity)holding).getNumShares()+"\"");
						filebuffer.newLine();
					}


				}else {
					holding = t.getLoss();
					cashAccount = t.getGain();
					if(holding instanceof Equity) {
						filebuffer.write("\""+cashAccount.getName()+"\",+,\""+cashAccount.getTotalValue()+"\",\""+cashAccount.getDate()+"\"");
						filebuffer.newLine();
						filebuffer.write("\""+((Equity) holding).getEquityInfo().getTS()+"\",E,-,\""+((Equity)holding).getShareValue()+"\",\""+((Equity)holding).getNumShares()+"\"");
						filebuffer.newLine();
					}
					else if (holding instanceof MarketAverage){
						filebuffer.write("\""+cashAccount.getName()+"\",+,\""+cashAccount.getTotalValue()+"\",\""+cashAccount.getDate()+"\"");
						filebuffer.newLine();
						filebuffer.write("\""+holding.getName()+"\",M,-,\""+((Equity)holding).getShareValue()+"\",\""+((Equity)holding).getNumShares()+"\"");
						filebuffer.newLine();
					}
				}

				filebuffer.newLine();

			}
			filebuffer.write("-----------------\n");
			filebuffer.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		*/
	}

	/**
	 * A method to populate holdings and watchlist from a file.
	 * @param filepath The path of the file to import from.
	 */
	public void importFile(String filepath){
		while(!EquityInfo.initialized){
			try{
				Thread.sleep(500);
			}catch(Exception e){e.printStackTrace();}
		}
		try {
			BufferedReader pbuffer = new BufferedReader(new FileReader(filepath));
			String newLine;
			DateFormat df = new SimpleDateFormat("mm/dd/yyyy @ hh:mm:ss a");
			String today = df.format(new Date());
			while((newLine = pbuffer.readLine()) != null){
				try{
				if(newLine.indexOf("EQUITY") >= 0){
					newLine = newLine.substring(newLine.indexOf(",") + 1);
					EquityInfo ei = EquityInfo.getMatchingInfo(null, newLine.substring(1, newLine.indexOf("\",")), null).get(0);
					newLine = newLine.substring(newLine.indexOf(",") + 1);
					Equity e = new Equity(ei, ei.getValue(), today, Double.parseDouble(newLine.substring(1, newLine.length() - 1)), false);
					this.addHolding(e);
				}
				if(newLine.indexOf("CA") >= 0){
					newLine = newLine.substring(newLine.indexOf(",") + 1);
					String name = newLine.substring(1, newLine.indexOf("\","));
					newLine = newLine.substring(newLine.indexOf(",") + 1);
					Double monies = Double.parseDouble(newLine.substring(1, newLine.length() - 1));
					CashAccount ca = new CashAccount(monies, name, today);
					this.addHolding(ca);
				}
				if(newLine.indexOf("WATCHING") >= 0){
					newLine = newLine.substring(newLine.indexOf(",") + 1);
					double[] t = new double[3];
					EquityInfo ei = EquityInfo.getMatchingInfo("", newLine.substring(1, newLine.length() - 1), "").get(0);
					WatchedEquity we = new WatchedEquity(ei, t);
					addToWatchlist(we);
				}
				}catch(Exception e){
					e.printStackTrace();			
					System.out.println("Don't worry, the above exception was caught.");
				}
			}
			pbuffer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (NumberFormatException nfe){
			nfe.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Don't worry, the above exception was caught.");
		}
	}
}
