package HADS.Generator;

import static HADS.Generator.Statics.*;

import HADS.XML.DataHADS;
import HADS.Server.*;
import HADS.Server.StorageConnector;

/*
  Class Name: TransGenerator
  Description: Generates transactions based on parameters entered
*/

import java.io.*;
import java.util.*;
import java.rmi.*;

public class TransGenerator {
  private File current_batch, results_file;
  private PrintWriter Batch, Results;
  private int numServers;
  private ArrayList<String> ServerObjNames;
  private ArrayList<Integer> Ratios;
  private int[] initial_cost_bound = null, upper_cost_bound = null;
  private int[] donationMin = null, donationMax = null;
  private int[][] max_change = null, min_change = null;
  private int[][] increase_ratio = null, decrease_ratio = null;
  private int numResourceTypes, number, ratioTotal, delay;
  private int donationInterval, flipDecIncInterval;
  private int pid;
  private boolean allDecsOrIncs;
  private Random randGen;
  private Parameters parms;
  private CurrentResources current;
  private TransactionRequestFactory trf = null;

  public TransGenerator(String[] args) {
//   randGen = new Random(System.currentTimeMillis());
//   randGen = new Random(1234567890L);
     randGen = new Random(9876543210L);

    System.out.println("Transaction Generator Started!!");
    try {

      // Create a new instance of the Hads XML Parser that takes in
      // the XML file to be parsed
      final String XML_FILE_NAME = args[0];
      pid = Integer.parseInt(args[1]);
      DataHADS dataHADS = new DataHADS(XML_FILE_NAME);

      numResourceTypes = dataHADS.getNumResourceTypes();
      numServers =       dataHADS.getNumServers();
      delay =            dataHADS.getGenerateDelay();
      number =           dataHADS.getNumTransactions();
      donationInterval = dataHADS.getDonationInterval();
      flipDecIncInterval = dataHADS.getFlipDecIncInterval();
      String generator = dataHADS.getGENERATOR();
      current_batch =    new File(dataHADS.getBATCH_PREFIX()+pid+"-"+generator);
      results_file =     new File(dataHADS.getMERGED_PREFIX()+pid+"-"+generator);

      // read command line arguments into parameters
      System.out.println(
         "Usage: java TransGenerator file.xml pid [was... "
         + "generationDelay minimumChange maximumChange numTransactions "
         + "decRatio incRatio batchFile resultsFile numServers "
         + "server1 ratio1 server2 ratio2 ...]"
      );

      initial_cost_bound = dataHADS.getInitialCostBound();
      upper_cost_bound = dataHADS.getUpperCostBound();
      donationMin = dataHADS.getDonationMin();
      donationMax = dataHADS.getDonationMax();
      allDecsOrIncs = dataHADS.getAllDecsOrIncs();

      min_change = dataHADS.getMinChange();
      max_change = dataHADS.getMaxChange();
      decrease_ratio = dataHADS.getDecreaseRatio();
      increase_ratio = dataHADS.getIncreaseRatio();

      parms = new Parameters(
         initial_cost_bound, upper_cost_bound,
         donationMin, donationMax,
         min_change, max_change,
         decrease_ratio, increase_ratio
      );
      current = new CurrentResources(parms);
      trf = new TransactionRequestFactory(parms, randGen, allDecsOrIncs);

      ServerObjNames = new ArrayList<String>();
      Ratios = new ArrayList<Integer>();
      List<String> servers = dataHADS.getServers();
      List<Integer> ratios = dataHADS.getRatios();
      ratioTotal = 0;
      for (int i = 0; i < numServers; i++) {
//      String serverName  = "//" + servers.get(i) + "/Server" + pid;
        String serverName  = "//" + servers.get(i) + "/"
          + servers.get(i) + "S" + pid;
        int serverRatio =            ratios.get(i);
        ServerObjNames.add(serverName);
        Ratios.add(serverRatio);
        ratioTotal = ratioTotal + serverRatio;
      }
    } catch (Exception e) {
      System.out.println("Exception occurred: " + e);
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println(
       "Transaction Generator command line arguments processed:"
       + "\n pid =                " + pid
       + "\n initial_cost_bound = " + arrayToString(initial_cost_bound)
       + "\n upper_cost_bound =   " + arrayToString(upper_cost_bound)
       + "\n donationInterval =   " + donationInterval
       + "\n donationMin =        " + arrayToString(donationMin)
       + "\n donationMax =        " + arrayToString(donationMax)
       + "\n flipDecIncInterval = " + flipDecIncInterval
       + "\n delay =              " + delay
       + "\n number =             " + number
       + "\n current_batch =      " + current_batch
       + "\n results_file =       " + results_file
       + "\n allDecsOrIncs =      " + allDecsOrIncs
       + "\n numServers =         " + numServers
       + "\n ratioTotal =         " + ratioTotal
       + "\n min_change =         " + arrayToString(min_change)
       + "\n max_change =         " + arrayToString(max_change)
       + "\n decrease_ratio =     " + arrayToString(decrease_ratio)
       + "\n increase_ratio =     " + arrayToString(increase_ratio)
    );
    for (int i = 0; i < numServers; i++) {
      System.out.println("server = " + ServerObjNames.get(i) +
        ", ratio = " + Ratios.get(i));
    }
  }

  /*
    Start Generation
    Starts the Transaction Generation Process
    making use of the information gathered from the interace
  */

  public void startGeneration() {
    int numDecrements = 0, numIncrements = 0, numGenerated = 0;
    int numDonations = 0;
    int violationsBelowZero = 0, violationsAboveMax = 0, violations = 0;
    int[] numSentServer = aZeroFilledArray(numServers);
    boolean donation = false, flipDecInc = false;
    System.out.println("Now Generating and Sending Transactions");
    System.out.println("Sleeping an average of " + delay
      + " milliseconds between transactions");
    System.out.println("On average, one of every " + donationInterval
      + " transaction is a donation of new resources");

    try{
      Batch=new PrintWriter(new BufferedWriter(new FileWriter(current_batch)));
	// need a parameter for average number of transactions between donations
	// like the average time between transactions
      for(int i = 0; i < number; ++i) 
      {
      	long thisTime = (long) (2*delay*randGen.nextDouble());
        Thread.sleep(thisTime);
        // Determine if a donation.  Zero donation interval means none ever.
        // Positive donation interval means the probability that this
        // transaction is a donation is 1/(donation interval).
        if (donationInterval <= 0) 
	{
        	donation = false;
        } 
	else 
	{
	        donation = (0L == ((long) (randGen.nextDouble()*donationInterval)));
        }
        // Determine if we have reached an interval where decrement and
        // increment ratios are switched for a while
        if (flipDecIncInterval <= 0) 
	{
        	flipDecInc = false;
        } 
	else 
	{
        	if ( (i > 0) && ((i % flipDecIncInterval) == 0)) 
		{
            		flipDecInc = !flipDecInc;
            		System.out.println("flipDecInc is now " + flipDecInc);
          	}
        }
        //Determining which server the transaction is to be sent to
	// Check this!  The `1 +' below might be wrong as it was
	// for the decrease and increase ratios in the transaction factory.
        int rand_val = 1 + (int)(randGen.nextDouble()*ratioTotal);
        int upper=0;
        int lower=1;
        int server_pos=0;
        for(int j = 0; j < numServers; j++) 
	{
        	upper += Ratios.get(j);
          	if(rand_val >= lower && rand_val <= upper) 
		{
            		server_pos=j;
            		break;
          	} 
		else 
			lower=1+upper;
        }
        numSentServer[server_pos]++;
        String init_server_name = ServerObjNames.get(server_pos);
        Server initializing_server = (Server) Naming.lookup(init_server_name);
        TransactionRequest tr = null;
        System.out.println("current = " + current.toString());
	// need to add donation argument
        tr = trf.generateOne(
          i, init_server_name, server_pos, donation, flipDecInc
        );
        System.out.print("Trying tr = " + tr.toString() + " .... ");
        if (donation) {
          current.makeDonation(tr);
          System.out.println("is a DONATION.");
        } else if (allDecsOrIncs) {
          if (current.exceedsUpperBound(tr)) {
            System.out.println("VIOLATION would EXCEED upper bound.");
            violationsAboveMax++;
          } else if(current.goesBelowZero(tr)) {
            System.out.println("VIOLATION would go BELOW zero.");
            violationsBelowZero++;
          } else {
            current.perform(tr, true);
            System.out.println("does not exceed upper bound or go below zero.");
          }
        } else {
          if (!current.isAcceptable(tr)) {
            System.out.println("VIOLATION.");
            violations++;
          } else {
            current.perform(tr, true);
            System.out.println("is acceptable.");
          }
        }
        initializing_server.RequestProcessor(tr);
        if (donation) {
          numDonations++;
          Batch.print("donation");
        } else if (allDecsOrIncs) {
          if (tr.isIncrease()) {
            numIncrements++;
            Batch.print("increase");
          } else if (tr.isDecrease()) {
            numDecrements++;
            Batch.print("decrease");
          } else {
            throw new RuntimeException(
        "transaction request must be all increases or all decreases"
            );
          }
        } else {
          Batch.print("mixture ");
        }
        Batch.println(tr.toString());
        System.out.println(thisTime + " ms, " + tr.toString());
        numGenerated++;
      }
      System.out.println("last current = " + current.toString());
      Batch.close();
      System.out.println("number donation = " + numDonations);
      if (allDecsOrIncs) {
        System.out.println("number decrements = " + numDecrements
           + ", number increments = " + numIncrements);
        System.out.println("violations below zero = " + violationsBelowZero
           + ", violations above max = " + violationsAboveMax);
      } else {
        System.out.println("violations  = " + violations);
      }
      System.out.println("number generated = " + numGenerated);
      for (int i = 0; i < numServers; i++) {
        String server_name  = ServerObjNames.get(i);
        int server_ratio = Ratios.get(i);
        System.out.println("Server " + server_name + " (ratio "
          + server_ratio + ") received " + numSentServer[i]
          + " transactions.");
      }
      System.out.println("All transactions generated; waiting "
         + number*delay + " milliseconds for servers to settle....");
      // KLUDGE!!! we need a termination criterion to wait for servers to finish
      Thread.sleep(number*delay);
    } catch(Exception e) {
      System.out.println("Exception occured generating transactions");
      System.out.println("Exception: " + e);
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Generation Complete");
  }
  
   public int getNumServers(){ return numServers; }

  /*
         getResults
         Collects all the results from all the servers
  */

  public void getResults() {

   String resultLine;

    System.out.println("Telling all servers to send their results,"
      + " write their own results files, and terminate.");
    try{
      for(int i=0;i<numServers;i++) {
        String server_name=(String)ServerObjNames.get(i);
        Server current_server = (Server)Naming.lookup(server_name);
        current_server.seeIfDone(number);
      }
    } catch(Exception e) {
      System.out.println("Exception occured seeing if server is done");
      System.out.println("Exception: " + e);
      e.printStackTrace();
      System.exit(1);
    }
    try{
      Results=new PrintWriter(new BufferedWriter(new FileWriter(results_file)));
      for(int i=0;i<numServers;i++) {
        String server_name=(String)ServerObjNames.get(i);
        Server current_server = (Server)Naming.lookup(server_name);
        ResultsQueue r=current_server.SendResults();
        current_server.writeResultsFile();
        for(int j=0;j<r.size();j++) {
          ResultRecord rec = r.get(j);
          resultLine=rec.toString();
          Results.println(resultLine+server_name);
        }
        current_server.terminate();
      }
      Results.close();
    } catch(Exception e) {
      System.out.println("Exception occured generating results");
      System.out.println("Exception: " + e);
      e.printStackTrace();
      System.exit(1);
  }
}

  public static void main (String args[]) {
    TransGenerator T= new TransGenerator(args);
    System.out.println("Transactions Being Generated Now!!");
    int wait = 0;
    try {
	System.out.println("GEN DELAY: " + StorageConnector.genDelay());
	System.out.println("SERVERS: " + T.getNumServers());
	wait = StorageConnector.genDelay() * T.getNumServers();
      	Thread.sleep(wait); // manually slow down the refresh
    } catch (InterruptedException ie) {
      		System.err.println("INTERRUPTED");
    }//wait around
    long millis = System.currentTimeMillis();
    T.startGeneration();
    long dur = System.currentTimeMillis() - millis;
    System.out.println("Runtime: " + dur);
    T.getResults();
    System.out.println("Generation program done.");
  }
}
