package HADS.Generator;

import static HADS.Generator.Statics.*;

import HADS.XML.DataHADS;

import java.io.*;
import java.util.*;

class Statistics {

   public static void main(String[] args) throws IOException {
      final int NUM_RANGES = 4;
      String line = null;
      StringTokenizer tokens = null;
      int transactionID = 0;
      long timeIn = 0;
      long pessimisticTimeOut = 0;
      int PRT = 0;
      boolean doneOptimistically = false;
      boolean donation = false;
      long optimisticTimeOut = 0;
      String whoDidOptimistic = null;
      int whoDidOptimisticIndex = -1;
      String serverTransSentTo = null;
      boolean undone = false;
      boolean violation = false;
      int ORT = 0;
      int numTransactions = 0;
      int numOptimistic = 0;
      int numDonations = 0;
      int numUndone = 0;
      int numViolations = 0;
      int numZeroORTs = 0;
      int numNegativeORTs = 0;
      int violateTundoneT = 0;
      int violateTundoneF = 0;
      int violateFundoneT = 0;
      int violateFundoneF = 0;
      int[] current = null, change = null, next = null;
      int[] sumDonations = null;
      float ratioRT = 0;
      double sumRatios = 0, sumPRTs = 0, sumORTs = 0;
      float maximumRatio = 0, maximumPRT = 0, maximumORT = 0;
      float minimumRatio = Float.MAX_VALUE,
        minimumPRT = Float.MAX_VALUE, minimumORT = Float.MAX_VALUE;
      float averageRatio = 0, averagePRT = 0, averageORT = 0;
      float fractionOptAll = 0;
      float fractionUndoneAll = 0;
      float fractionUndoneOpt = 0;
      int[] range = new int[NUM_RANGES];

      System.out.print("Statistics args: ");
      for (int i = 0; i < args.length; i++) {
         System.out.print(args[i]);
         if (i != args.length-1) System.out.print(", ");
      }
      if (args.length > 0) System.out.println();

      final String XML_FILE_NAME = args[0];
      final int pid = Integer.parseInt(args[1]);
      final String INPUT_FILE_NAME = args[2];
      final DataHADS dataHADS = new DataHADS(XML_FILE_NAME);

      final int numResourceTypes = dataHADS.getNumResourceTypes();
      final int numServers = dataHADS.getNumServers();
      List<String> servers = dataHADS.getServers();
      int[] numDoneOptimistically = new int[numServers];
      int[] numSentTo = new int[numServers];
      int[] numSentToViolations = new int[numServers];
      int[] numSentToDonations = new int[numServers];
      int[] numTempDoneByParent = new int[numServers];
      int[][] tempWorkDone = new int[numServers][numResourceTypes];
      int[][] tempWorkDoneViolations = new int[numServers][numResourceTypes];
      int[][] tempWorkDoneUndone = new int[numServers][numResourceTypes];
      int[][] permWorkDone = new int[numServers][numResourceTypes];
      sumDonations = new int[numResourceTypes];

      for (int i = 0; i < numServers; i++) {
         numDoneOptimistically[i] = 0;
         numSentTo[i] = 0;
         numSentToViolations[i] = 0;
         numSentToDonations[i] = 0;
         numTempDoneByParent[i] = 0;
         for (int j = 0; j < numResourceTypes; j++) {
            tempWorkDone[i][j] = 0;
            tempWorkDoneViolations[i][j] = 0;
            tempWorkDoneUndone[i][j] = 0;
            permWorkDone[i][j] = 0;
         }
      }
      for (int i = 0; i < numResourceTypes; i++) {
         sumDonations[i] = 0;
      }

      System.out.println("XML file name = " + XML_FILE_NAME);
      System.out.println("pid = " + pid);
      System.out.println("input file name = " + INPUT_FILE_NAME);
      System.out.println("numResourceTypes = " + numResourceTypes);
      System.out.println("dataHADS = " + dataHADS);

      current = new int[numResourceTypes];
      change = new int[numResourceTypes];
      next = new int[numResourceTypes];
      for (int i = 0; i < range.length; i++) range[i] = 0;
      try {
         BufferedReader in = new BufferedReader(
           new FileReader(
              new File(INPUT_FILE_NAME)
           )
         );
         while (true) {
            line = in.readLine();
            if (line == null) break;
            else if (line.equals("")) break;
            tokens = new StringTokenizer(line);
/* Example lines from the sorted merged batch files:

        22   norm   1248127773063   1248127773099         36   TEMP   1248127773070 dallas.rowan.edu     1   no-u    no-v { 180 284 49 } { -6 -21 -6 } { 174 263 43 }  //austin.rowan.edu/austin.rowan.eduS11287
        23   DNTN   1248127773356   1248127773392         36   no-t               0         Unknown    -1   no-u    no-v { 174 263 43 } { 11 53 6 } { 185 316 49 }  //austin.rowan.edu/austin.rowan.eduS11287
        68   norm   1248127782249   1248127782302         53   no-t               0         Unknown    -1   no-u VIOLATE { 187 58 1 } { 6 -16 -4 } { 187 58 1 }  //dallas.rowan.edu/dallas.rowan.eduS11287
        69   norm   1248127782402   1248127782437         35   TEMP   1248127782403 houston.rowan.edu     0   UNDO VIOLATE { 187 58 1 } { -5 10 -5 } { 187 58 1 }  //houston.rowan.edu/houston.rowan.eduS11287

*/
            transactionID = Integer.parseInt(tokens.nextToken());
            donation = tokens.nextToken().equals("DNTN");
            if (donation) numDonations++;
            timeIn = Long.parseLong(tokens.nextToken());
            pessimisticTimeOut = Long.parseLong(tokens.nextToken());
            PRT = Integer.parseInt(tokens.nextToken());
            if (PRT != (int) (pessimisticTimeOut - timeIn)) {
               System.out.println(transactionID +
                  ": PRT != timeIn - pessimisticTimeOut");
            }
            sumPRTs += PRT;
            if (PRT < minimumPRT) minimumPRT = PRT;
            if (PRT > maximumPRT) maximumPRT = PRT;
            doneOptimistically = tokens.nextToken().equals("TEMP");
            optimisticTimeOut = Long.parseLong(tokens.nextToken());
            whoDidOptimistic = tokens.nextToken();
            whoDidOptimisticIndex = Integer.parseInt(tokens.nextToken());

            for (int i = 0; i < numServers; i++) {
               if (whoDidOptimistic.contains(servers.get(i))) {
                  numDoneOptimistically[i]++;
                  if (whoDidOptimisticIndex != i) {
                    throw new RuntimeException(
                       "Statistics: whoDidOptimisticIndex="
                       + whoDidOptimisticIndex + " != " + i
                    );
                  }
                  break;
               }
            }

            undone = tokens.nextToken().equals("UNDO");
            violation = tokens.nextToken().equals("VIOLATE");
            if (violation) numViolations++;
            if (violation && undone) violateTundoneT++;
            if (violation && !undone) violateTundoneF++;
            if (!violation && undone) violateFundoneT++;
            if (!violation && !undone) violateFundoneF++;
            // The following three are now arrays of length numResourceTypes
            // enclosed in curly braces.
            // { 200 400 100 } { 9 9 4 } { 209 409 104 }
            String curlyBrace = tokens.nextToken();
            for (int i = 0; i < numResourceTypes; i++) {
               current[i] = Integer.parseInt(tokens.nextToken());
            }
            curlyBrace = tokens.nextToken();
            curlyBrace = tokens.nextToken();
            for (int i = 0; i < numResourceTypes; i++) {
               change[i] = Integer.parseInt(tokens.nextToken());
            }
            if (donation) {
               for (int i = 0; i < numResourceTypes; i++) {
                  if (change[i] < 0) {
                     throw new RuntimeException(
                        "Statistics: donation change[" + i + "]="
                        + change[i] + " is < 0"
                     );
                  }
                  sumDonations[i] += change[i];
               }
            }
            curlyBrace = tokens.nextToken();
            curlyBrace = tokens.nextToken();
            for (int i = 0; i < numResourceTypes; i++) {
               next[i] = Integer.parseInt(tokens.nextToken());
            }
            curlyBrace = tokens.nextToken();
            serverTransSentTo = tokens.nextToken();

            for (int i = 0; i < numServers; i++) {
               if (serverTransSentTo.contains(servers.get(i))) {
                  numSentTo[i]++;
                  if (donation) {
                    numSentToDonations[i]++;
                  } else if (violation) {
                    numSentToViolations[i]++;
                  } else {
                    for (int j = 0; j < numResourceTypes; j++) {
                      permWorkDone[i][j] += Math.abs(change[j]);
                    }
                  }
                  if (doneOptimistically
                       && serverTransSentTo.contains(whoDidOptimistic)) {
                     numTempDoneByParent[i]++;
                  }
                  break;
               }
            }

            boolean mismatch = false;
            for (int i = 0; i < numResourceTypes; i++) {
               if (current[i] + change[i] != next[i] && current[i] != next[i]) {
                  mismatch = true;
                  break;
               }
            }
            if (mismatch) {
               System.out.println("mismatch: transactionID=" + transactionID
                  + ": current=" + arrayToString(current)
                  + ", change=" + arrayToString(change)
                  + " != next=" + arrayToString(next)
               );
            }
            numTransactions++;
            if (doneOptimistically) {
               if (donation) {
                 throw new RuntimeException(
                   "donation and done optimistically both true incorrectly"
                 );
               }
               for (int j = 0; j < numResourceTypes; j++) {
                 tempWorkDone[whoDidOptimisticIndex][j] += Math.abs(change[j]);
                 if (violation) {
                   tempWorkDoneViolations[whoDidOptimisticIndex][j]
                     += Math.abs(change[j]);
                   if (undone) {
                     tempWorkDoneUndone[whoDidOptimisticIndex][j]
                       += Math.abs(change[j]);
                   }
                 }
               }
            }
            if (doneOptimistically) {
               numOptimistic++;
               ORT = (int) (optimisticTimeOut - timeIn);
               if (ORT < 0) {
                  numNegativeORTs++;
//                System.out.println(transactionID + ": ORT < 0");
               } else if (ORT == 0) {
                  numZeroORTs++;
//                System.out.println(transactionID + ": ORT == 0");
               }
               sumORTs += ORT;
               if (ORT < minimumORT) minimumORT = ORT;
               if (ORT > maximumORT) maximumORT = ORT;
               ratioRT = (float)PRT/(float)ORT;
               sumRatios += ratioRT;
               if (ratioRT > maximumRatio) maximumRatio = ratioRT;
               if (ratioRT < 1.0f) {
                  System.out.println(transactionID + ": ratioRT < 1.0f");
               }
               else if (ratioRT < 5.0f) range[0]++;
               else if (ratioRT < 10.0f) range[1]++;
               else if (ratioRT < 15.0f) range[2]++;
               else range[3]++;
               if (undone) numUndone++;
            } else ORT = 0;
         }
         if (numOptimistic == 0) {
            System.out.println(transactionID + ": numOptimistic == 0");
         }
         averageRatio = (float)sumRatios/(float)numOptimistic;
         averagePRT =     (float)sumPRTs/(float)numOptimistic;
         averageORT =     (float)sumORTs/(float)numOptimistic;
         fractionOptAll = (float)numOptimistic/(float)numTransactions;
         fractionUndoneAll = (float)numUndone/(float)numTransactions;
         fractionUndoneOpt = (float)numUndone/(float)numOptimistic;
         System.out.println("number of transactions           "
            + numTransactions);
         System.out.println("number of donations              "
            + numDonations);
         System.out.println("sum of donations                 "
            + arrayToString(sumDonations));
         System.out.println("done optimistically              "
            + numOptimistic);
         System.out.println("undone                           "
            + numUndone);
         System.out.println("violations                       "
            + numViolations);
         System.out.println("done optimistically fraction all "
            + fractionOptAll);
         System.out.println("undone fraction optimistic       "
            + fractionUndoneOpt);
         System.out.println("undone fraction all              "
            + fractionUndoneAll);
         System.out.println("average PRT/ORT ratio            "
            + averageRatio);
         System.out.println("maximum PRT/ORT ratio            "
            + maximumRatio);
         System.out.println("number of  1.0 <= ratio <  5.0  " + range[0]);
         System.out.println("number of  5.0 <= ratio < 10.0  " + range[1]);
         System.out.println("number of 10.0 <= ratio < 15.0  " + range[2]);
         System.out.println("number of 15.0 <= ratio         " + range[3]);
         System.out.println("number of zero ORTs=" + numZeroORTs
              + ", number of negative ORTs=" + numNegativeORTs);
         System.out.println("avgPRT=" + averagePRT + ", minPRT=" + minimumPRT
           + ", maxPRT=" + maximumPRT);
         System.out.println("avgORT=" + averageORT + ", minORT=" + minimumORT
           + ", maxORT=" + maximumORT);
         System.out.println("vTuT=" + violateTundoneT
            + ", vTuF=" + violateTundoneF
            + ", vFuT=" + violateFundoneT
            + ", vFuF=" + violateFundoneF);
         for (int i = 0; i < numServers; i++) {
           System.out.println(servers.get(i)
              + " received " + numSentTo[i] + " transactions,"
              + "\n of which " + numSentToViolations[i] + " were violations"
              + ", of which " + numSentToDonations[i] + " were donations"
              + "\n did " + numTempDoneByParent[i]
              + " of them (ones it is parent of) optimistically,"
              + "\n did " + numDoneOptimistically[i]
              + " transactions optimistically overall"
              + "\n did this much work optimistically "
              + arrayToString(tempWorkDone[i])
              + "\n of which work " + arrayToString(tempWorkDoneViolations[i])
                 + " were violations"
              + ", and " + arrayToString(tempWorkDoneUndone[i])
                 + " were violations undone"
              + "\n did this much work pessimistically (excluding donations) "
              + arrayToString(permWorkDone[i])
           );
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         System.out.println("transactionID=" + transactionID);
      }
      System.exit(0);
   }
}
