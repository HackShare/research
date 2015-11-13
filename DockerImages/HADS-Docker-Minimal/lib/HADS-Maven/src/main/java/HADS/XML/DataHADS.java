// TO DO:
//         check all numbers to make sure they are non-negative
//         check that cost-bound <= upper-bound
//         check that min-change <= max-change
//         check that all server ratios are non-negative

package HADS.XML;

import static HADS.Generator.Statics.*;

import java.io.*;
import java.util.*;

public class DataHADS {                         // immutable, hooray!
   private String fileName = null;
   private ParseHADS hadsParser = null;

   private String BATCH_PREFIX = null;
   private String OUTPUT_PREFIX = null;
   private String GENOUT_PREFIX = null;
   private String RESULTS_PREFIX = null;
   private String MERGED_PREFIX = null;
   private String STATS_PREFIX = null;
   private String NUM_RES_TYPES = null;
   private String GEN_DELAY = null;
   private String NUM_TRANS = null;
   private String DONATION_INTERVAL = null;
   private String FLIP_DECINC_INTERVAL = null;
   private String NUM_SERVERS = null;
   private String ALL_DECS_OR_INCS = null;
   private String GENERATOR = null;

   private String[] UPPER_BOUND = null;
   private String[] COST_BOUND = null;
   private String[] COST_PCT = null;
   private String[] DONATION_MIN = null;
   private String[] DONATION_MAX = null;

   private String[][] MIN_CHANGE = null;
   private String[][] MAX_CHANGE = null;
   private String[][] DEC_RATIO = null;
   private String[][] INC_RATIO = null;

   private int numServers = 0;
   private int numResourceTypes = 0;
   private int numTransactions = 0;
   private int generateDelay = 0;
   private int donationInterval = 0;
   private int flipDecIncInterval = 0;

   private boolean allDecsOrIncs = false;

   private int[] initialCostBound = null;
   private int[] upperCostBound = null;
   private int[] percentCostBound = null;
   private int[] donationMin = null;
   private int[] donationMax = null;

   private int[][] minChange = null;
   private int[][] maxChange = null;
   private int[][] decreaseRatio = null;
   private int[][] increaseRatio = null;

   private int[] ratioNums = null;

   private List<String> servers = null;
   private List<Integer> ratios = null;

   public DataHADS(String fileName) throws IOException {
     if (fileName == null || fileName.length() == 0) {
        throw new IllegalArgumentException(
           "DataHADS: fileName is null or empty string"
        );
     }
     this.fileName = fileName;
     // Create a new instance of the Hads XML Parser that takes in
     // the XML file to be parsed
     hadsParser = new ParseHADS(new File(fileName));

     // Use parsed data from the Hads XML file to fill in the constants
     BATCH_PREFIX = hadsParser.getNodeByName("batch-prefix");
     OUTPUT_PREFIX = hadsParser.getNodeByName("output-prefix");
     GENOUT_PREFIX = hadsParser.getNodeByName("genout-prefix");
     RESULTS_PREFIX = hadsParser.getNodeByName("results-prefix");
     MERGED_PREFIX = hadsParser.getNodeByName("merged-prefix");
     STATS_PREFIX = hadsParser.getNodeByName("stats-prefix");
     NUM_RES_TYPES = hadsParser.getNodeByName("num-res-types");
     GEN_DELAY = hadsParser.getNodeByName("gen-delay");
     NUM_TRANS = hadsParser.getNodeByName("num-trans");
     DONATION_INTERVAL = hadsParser.getNodeByName("donation-interval");
     FLIP_DECINC_INTERVAL = hadsParser.getNodeByName("flip-decinc-interval");
     NUM_SERVERS = hadsParser.getNumOfNodes("server");
     ALL_DECS_OR_INCS = hadsParser.getNodeByName("all-decs-or-incs");
     GENERATOR = hadsParser.getNodeByName("generator");

     numServers = Integer.parseInt(NUM_SERVERS);
     numResourceTypes = Integer.parseInt(NUM_RES_TYPES);
     numTransactions = Integer.parseInt(NUM_TRANS);
     generateDelay = Integer.parseInt(GEN_DELAY);
     donationInterval = Integer.parseInt(DONATION_INTERVAL);
     flipDecIncInterval = Integer.parseInt(FLIP_DECINC_INTERVAL);

     allDecsOrIncs = Boolean.valueOf(ALL_DECS_OR_INCS).booleanValue();

     UPPER_BOUND = new String[numResourceTypes];
     COST_BOUND = new String[numResourceTypes];
     COST_PCT = new String[numResourceTypes];
     DONATION_MIN = new String[numResourceTypes];
     DONATION_MAX = new String[numResourceTypes];

     MIN_CHANGE = new String[numServers][numResourceTypes];
     MAX_CHANGE = new String[numServers][numResourceTypes];
     DEC_RATIO = new String[numServers][numResourceTypes];
     INC_RATIO = new String[numServers][numResourceTypes];

     for (int i = 0; i < numResourceTypes; i++) {
       UPPER_BOUND[i] = hadsParser.getNodeByName("upper-bound-" + i);
       COST_BOUND[i] = hadsParser.getNodeByName("cost-bound-" + i);
       COST_PCT[i] = hadsParser.getNodeByName("cost-pct-" + i);
       DONATION_MIN[i] = hadsParser.getNodeByName("donation-min-" + i);
       DONATION_MAX[i] = hadsParser.getNodeByName("donation-max-" + i);
     }

     initialCostBound = new int[numResourceTypes];
     upperCostBound = new int[numResourceTypes];
     percentCostBound = new int[numResourceTypes];
     donationMin = new int[numResourceTypes];
     donationMax = new int[numResourceTypes];

     minChange = new int[numServers][numResourceTypes];
     maxChange = new int[numServers][numResourceTypes];
     decreaseRatio = new int[numServers][numResourceTypes];
     increaseRatio = new int[numServers][numResourceTypes];

      for (int i = 0; i < numResourceTypes; i++) {
        upperCostBound[i] = Integer.parseInt(UPPER_BOUND[i]);
        initialCostBound[i] = Integer.parseInt(COST_BOUND[i]);
        percentCostBound[i] = Integer.parseInt(COST_PCT[i]);
        donationMin[i] = Integer.parseInt(DONATION_MIN[i]);
        donationMax[i] = Integer.parseInt(DONATION_MAX[i]);
      }

     // Create temporary lists for servers and ratios
     ArrayList<String> tempServers = new ArrayList<String>();
     ArrayList<Integer> tempRatios = new ArrayList<Integer>();
     // Go through the server nodes and collect server names and ratios
     for (int i = 0; i < numServers; i++) {
       // Get current server and ratio
       String tempServer =
          hadsParser.getSubNodeByName("server", "server-name", i);
       int tempRatio = Integer.parseInt(
          hadsParser.getSubNodeByName("server", "server-ratio", i)
       );

       for (int j = 0; j < numResourceTypes; j++) {
         MIN_CHANGE[i][j] =
           hadsParser.getSubNodeByName("server", "min-change-" + j, i);
         MAX_CHANGE[i][j] =
           hadsParser.getSubNodeByName("server", "max-change-" + j, i);
         DEC_RATIO[i][j] =
           hadsParser.getSubNodeByName("server", "dec-ratio-" + j, i);
         INC_RATIO[i][j] =
           hadsParser.getSubNodeByName("server", "inc-ratio-" + j, i);
         minChange[i][j] = Integer.parseInt(MIN_CHANGE[i][j]);
         maxChange[i][j] = Integer.parseInt(MAX_CHANGE[i][j]);
         decreaseRatio[i][j] = Integer.parseInt(DEC_RATIO[i][j]);
         increaseRatio[i][j] = Integer.parseInt(INC_RATIO[i][j]);
       }

       // Store current server and ratio in temporary arrays
       tempServers.add(tempServer);
       tempRatios.add(tempRatio);
     }
     // Create unmodifiable lists (constant lists) from temporary lists
     servers = Collections.unmodifiableList(tempServers);
     ratios = Collections.unmodifiableList(tempRatios);

     ratioNums = new int[numServers];
     for (int i = 0; i < numServers; i++) {
       ratioNums[i] = ratios.get(i);
     }
   }

   public String getFileName() { return fileName; }

   public String getBATCH_PREFIX() { return BATCH_PREFIX; }
   public String getOUTPUT_PREFIX() { return OUTPUT_PREFIX; }
   public String getGENOUT_PREFIX() { return GENOUT_PREFIX; }
   public String getRESULTS_PREFIX() { return RESULTS_PREFIX; }
   public String getMERGED_PREFIX() { return MERGED_PREFIX; }
   public String getSTATS_PREFIX() { return STATS_PREFIX; }
   public String getGENERATOR() { return GENERATOR; }

   public int getNumServers() { return numServers; }
   public int getNumResourceTypes() { return numResourceTypes; }
   public int getNumTransactions() { return numTransactions; }
   public int getGenerateDelay() { return generateDelay; }
   public int getDonationInterval() { return donationInterval; }
   public int getFlipDecIncInterval() { return flipDecIncInterval; }

   public boolean getAllDecsOrIncs() { return allDecsOrIncs; }

   public int[] getInitialCostBound() { return arrayCopyOf(initialCostBound); }
   public int[] getUpperCostBound() { return arrayCopyOf(upperCostBound); }
   public int[] getPercentCostBound() { return arrayCopyOf(percentCostBound); }
   public int[] getDonationMin() { return arrayCopyOf(donationMin); }
   public int[] getDonationMax() { return arrayCopyOf(donationMax); }

   public int[][] getMinChange() { return arrayCopyOf(minChange); }
   public int[][] getMaxChange() { return arrayCopyOf(maxChange); }
   public int[][] getDecreaseRatio() { return arrayCopyOf(decreaseRatio); }
   public int[][] getIncreaseRatio() { return arrayCopyOf(increaseRatio); }

   // Argument `i' is server number in the following four getX methods.
   public int[] getMinChange(int i) {
      return arrayCopyOf(minChange[i]);
   }
   public int[] getMaxChange(int i) {
      return arrayCopyOf(maxChange[i]);
   }
   public int[] getDecreaseRatio(int i) {
      return arrayCopyOf(decreaseRatio[i]);
   }
   public int[] getIncreaseRatio(int i) {
      return arrayCopyOf(increaseRatio[i]);
   }

   public List<String> getServers() { return servers; }
   public List<Integer> getRatios() { return ratios; }

   public int[] getRatioNums() { return arrayCopyOf(ratioNums); }

   public String toString() {
     String result =
          "\n fileName = " + fileName
        + "\n BATCH_PREFIX = " + BATCH_PREFIX
        + "\n OUTPUT_PREFIX = " + OUTPUT_PREFIX
        + "\n GENOUT_PREFIX = " + GENOUT_PREFIX
        + "\n RESULTS_PREFIX = " + RESULTS_PREFIX
        + "\n MERGED_PREFIX = " + MERGED_PREFIX
        + "\n STATS_PREFIX = " + STATS_PREFIX
        + "\n UPPER_BOUND = " + arrayToString(UPPER_BOUND)
        + "\n COST_BOUND = " + arrayToString(COST_BOUND)
        + "\n COST_PCT = " + arrayToString(COST_PCT)
        + "\n GEN_DELAY = " + GEN_DELAY
        + "\n NUM_TRANS = " + NUM_TRANS
        + "\n DONATION_INTERVAL = " + DONATION_INTERVAL
        + "\n DONATION_MIN = " + arrayToString(DONATION_MIN)
        + "\n DONATION_MAX = " + arrayToString(DONATION_MAX)
        + "\n FLIP_DECINC_INTERVAL = " + FLIP_DECINC_INTERVAL
        + "\n NUM_SERVERS = " + NUM_SERVERS
        + "\n NUM_RES_TYPES = " + NUM_RES_TYPES
        + "\n ALL_DECS_OR_INCS = " + ALL_DECS_OR_INCS
     ;
     for (int i = 0; i < servers.size(); i++) {
        result = result
           + "\n SERVER " + servers.get(i) + " has RATIO " + ratios.get(i)
           + "\n    MIN_CHANGE = " + arrayToString(MIN_CHANGE[i])
           + "\n    MAX_CHANGE = " + arrayToString(MAX_CHANGE[i])
           + "\n    DEC_RATIO = " + arrayToString(DEC_RATIO[i])
           + "\n    INC_RATIO = " + arrayToString(INC_RATIO[i])
        ;
     }
     result = result + "\n GENERATOR = " + GENERATOR;
     return result;
   }

   public static void main(String[] args) throws IOException {
      if (args.length == 0 || args[0] == null || args[0].length() == 0) {
         System.out.println("args is empty or args[0] is null or empty string");
      } else {
         DataHADS dh = new DataHADS(args[0]);
         System.out.println(args[0] + "=" + dh);

         System.out.println("fileName = " + dh.getFileName());

         System.out.println("batch prefix = " + dh.getBATCH_PREFIX());
         System.out.println("output prefix = " + dh.getOUTPUT_PREFIX());
         System.out.println("genout prefix = " + dh.getGENOUT_PREFIX());
         System.out.println("results prefix = " + dh.getRESULTS_PREFIX());
         System.out.println("merged prefix = " + dh.getMERGED_PREFIX());
         System.out.println("stats prefix = " + dh.getSTATS_PREFIX());
         System.out.println("generator = " + dh.getGENERATOR());

         System.out.println("num servers = " + dh.getNumServers());
         System.out.println("num res types = " + dh.getNumResourceTypes());
         System.out.println("num trans = " + dh.getNumTransactions());
         System.out.println("trans delay = " + dh.getGenerateDelay());
         System.out.println("donation interval = " + dh.getDonationInterval());
    System.out.println("flip decinc interval = " + dh.getFlipDecIncInterval());

         System.out.println("all decs or incs = " + dh.getAllDecsOrIncs());

         System.out.println("initial CB = "
            + arrayToString(dh.getInitialCostBound()));
         System.out.println("upper CB = "
            + arrayToString(dh.getUpperCostBound()));
         System.out.println("pct CB = "
            + arrayToString(dh.getPercentCostBound()));
         System.out.println("donation minimum = "
            + arrayToString(dh.getDonationMin()));
         System.out.println("donation maximum = "
            + arrayToString(dh.getDonationMax()));

         int numServers = dh.getNumServers();
         for (int i = 0; i < numServers; i++) {
            System.out.println("min change[" + i + "] = "
               + arrayToString(dh.getMinChange(i)));
            System.out.println("max change[" + i + "] = "
               + arrayToString(dh.getMaxChange(i)));
            System.out.println("decrease ratio[" + i + "] = "
               + arrayToString(dh.getDecreaseRatio(i)));
            System.out.println("increase ratio[" + i + "] = "
               + arrayToString(dh.getIncreaseRatio(i)));
         }

         System.out.println("servers = " + listToString(dh.getServers()));
         System.out.println("ratios = " + listToString(dh.getRatios()));

         System.out.println("server ratio nums = "
            + arrayToString(dh.getRatioNums()));
      }
   }

}
