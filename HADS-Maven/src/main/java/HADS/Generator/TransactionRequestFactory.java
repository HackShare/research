package HADS.Generator;

import static HADS.Generator.Statics.*;

import HADS.Generator.Parameters;
import HADS.Generator.TransactionRequest;

import java.util.Random;

public class TransactionRequestFactory { // singleton
  private final Parameters parms;
  private final Random rand;
  private final boolean allDecsOrIncs;

  public TransactionRequestFactory(
        Parameters parms,
        Random rand,
        boolean allDecsOrIncs
  ) {
    if (parms == null || rand == null) {
      throw new IllegalArgumentException(
        "TransactionRequestFactory: parms == null || rand == null"
      );
    } else {
      this.parms = parms;
      this.rand = rand;
      this.allDecsOrIncs = allDecsOrIncs;
    }
  }

  public TransactionRequest generateOne(int whichOne, String serverSentTo,
      int serverIndex, boolean donation) {
    return generateOne(whichOne, serverSentTo, serverIndex, donation, false);
  }

// need another parameter, boolean donation
  public TransactionRequest generateOne(int whichOne, String serverSentTo,
      int serverIndex, boolean donation, boolean flipDecInc) {
    int numResourceTypes = parms.getNumResourceTypes();
    int[] changeBy = new int[numResourceTypes];
    boolean isIncrease = true;  // false means decrease
    TransactionRequest tr = null;
    for (int i = 0; i < numResourceTypes; i++) {
        //range of changes for each trans. max change to min change
        int min_change, max_change;
        if (donation) {
           min_change = parms.getDonationMin(i);
           max_change = parms.getDonationMax(i);
        } else {
           min_change = parms.getMinChange(serverIndex, i);
           max_change = parms.getMaxChange(serverIndex, i);
        }
        int decrease_ratio = parms.getDecRatio(serverIndex, i);
        int increase_ratio = parms.getIncRatio(serverIndex, i);
        if (flipDecInc) {
          int temp = decrease_ratio;
          decrease_ratio = increase_ratio;
          increase_ratio = temp;
        }
// Generate a non-negative random integer, change_by, uniformly from
// [min_change, max_change] where the [] means both inclusive
        int change_by = min_change
          + (int)(rand.nextDouble()*(max_change + 1 - min_change));
        //Generate possibilites for transaction type
// Generate a non-negative random integer, type, that is < increase_ratio
// a percent of the time equal to
//                increase_ratio/(increase_ratio+decrease_ratio)
// and >= increase_ratio a percent of the time equal to
//                decrease_ratio/(increase_ratio+decrease_ratio)
        int type =
            (int)(rand.nextDouble()*(increase_ratio + decrease_ratio));
        if (donation) {
          isIncrease = true;
        } else if (i == 0 || !allDecsOrIncs) {
          isIncrease = (type < increase_ratio);
        }
        if (isIncrease) {
          changeBy[i] = change_by;
        } else {
          changeBy[i] = -change_by;
        }
    }
// If a donation, must be an increase
    if (whichOne < 0) {
      throw new IllegalArgumentException(
        "TransactionRequest: whichOne = " + whichOne + " is < 0"
      );
    } else if (serverSentTo == null || serverSentTo.length() == 0) {
      throw new IllegalArgumentException(
        "TransactionRequest: String serverSentTo is null or has zero length."
      );
    } else if (changeBy.length <= 0) {
      throw new IllegalArgumentException(
        "TransactionRequest: changeBy.length = " + changeBy.length + " is <= 0"
      );
    } else if (allZeros(changeBy)) {
      throw new IllegalArgumentException(
        "TransactionRequest: changeBy is all zeros"
      );
// If a donation, must be an increase
    } else if (donation && anyNegative(changeBy)) {
      throw new IllegalArgumentException(
        "TransactionRequest: changeBy is not all >=0"
      );
    } else {
      tr = new TransactionRequest(whichOne, serverSentTo, changeBy, donation);
    }
    return tr;
  }

  public static void main(String[] args) {
    Random rand = new Random(1234567890L);
    int min_change = 3;
    int max_change = 9;
    int decrease_ratio = 10;
    int increase_ratio = 5;
    int initial_cost_bound = 200;
    int upper_cost_bound = 400;
    int donation_min = 4;
    int donation_max = 16;
    Parameters parms = new Parameters(
  new int[] {initial_cost_bound, initial_cost_bound*2, initial_cost_bound/2},
  new int[] {upper_cost_bound, upper_cost_bound*2, upper_cost_bound/2},
  new int[] {donation_min, donation_min*2, donation_min/2},
  new int[] {donation_max, donation_max*2, donation_max/2},
  new int[][] { {min_change, min_change, min_change},
                {2*min_change, 2*min_change, 2*min_change} },
  new int[][] { {max_change, max_change, max_change},
                {2*max_change, 2*max_change, 2*max_change} },
  new int[][] { {decrease_ratio, decrease_ratio, decrease_ratio},
                {increase_ratio, increase_ratio, increase_ratio} },
  new int[][] { {increase_ratio, increase_ratio, increase_ratio},
                {decrease_ratio, decrease_ratio, decrease_ratio} }
    );
    TransactionRequestFactory trf
      = new TransactionRequestFactory(parms, rand, false);
    TransactionRequest tr;
    for (int i = 0; i < 10; i++) {
      tr = trf.generateOne(i, "localhostA", 0, 1==i%3);
      System.out.println("tr=" + tr);
    }
    trf = new TransactionRequestFactory(parms, rand, true);
    for (int i = 0; i < 10; i++) {
      tr = trf.generateOne(i, "localhostB", 1, 1==i%3);
      System.out.println("tr=" + tr);
    }
  }

}
