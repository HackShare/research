package HADS.Server;

import static HADS.Generator.Statics.*;

import HADS.Generator.TransactionRequest;

// To enforce singleton, make constructor private and add a factory
// method
//   public static CurrentResources newInstance(Parameters parms)
// that checks a static boolean flag and calls the private constructor
// if the flag is false, then sets the flag to true.  To be sure, the
// newInstance method should be synchronized on the Class object.
// For mutable CurrentResources and whose constructor has a parameter,
// I should use Listing 16.4 on page 347 of Brian Goetz, Java Concurrency,
// for thread-safe singleton (it uses synchronized)
class CurrentResources {                       // singleton (unenforced)
  private final int numResourceTypes;
  private final int numServers;
  private final int[] percentCostBound;
  private final int[] serverCostBound;               // was CostBound
  private final int[] maxValue;                      // was max_val
  private final int[] value;
  private final int[] serverWork; // was thisNodeAlloc, work done by this server
  private final int[] totalWork; // work done by all servers, sum of abs(change)
  private final int[] tempWork; // work done by this TempP, sum of abs(change)
  // At doCommit/commitCompleted time, look at whoDidTemp and increment
  // ptempWork[whoDidTemp][0...resourceTypes-1]
  private final int[][] ptempWork;

  public CurrentResources(Parameters parms) {
    this.numResourceTypes = parms.getNumResourceTypes();   // cached copy
    this.numServers = parms.getNumServers();               // cached copy
    this.percentCostBound = parms.getPercentCostBound();   // cached copy
    this.maxValue = parms.getUpperCostBound(); // this and next 2 return copy
    this.value = parms.getInitialCostBound();
    this.serverCostBound = parms.getServerInitialCostBound();
    this.serverWork = new int[numResourceTypes];
    this.totalWork = new int[numResourceTypes];
    this.tempWork = new int[numResourceTypes];
    this.ptempWork = new int[this.numServers][this.numResourceTypes];
    for (int i = 0; i < this.numResourceTypes; i++) {
      this.serverWork[i] = 0;
      this.totalWork[i] = 0;
      this.tempWork[i] = 0;
      for (int j = 0; j < this.numServers; j++) {
        this.ptempWork[j][i] = 0;
      }
    }
  }

  public int getNumResourceTypes() { return numResourceTypes; }

// These are not thread safe even if synchronized.
//public int getMaxValue(int i) { return maxValue[i]; }
//public int getValue(int i) { return value[i]; }
//public int getServerCostBound(int i) { return serverCostBound[i]; }
//public int getServerWork(int i) { return serverWork[i]; }
//public int getTotalWork(int i) { return totalWork[i]; }

  public synchronized int[] getMaxValue() { return arrayCopyOf(maxValue); }
  public synchronized int[] getValue() { return arrayCopyOf(value); }
  public synchronized int[] getServerCostBound() {
    return arrayCopyOf(serverCostBound);
  }
  public synchronized int[] getServerWork() { return arrayCopyOf(serverWork); }
  public synchronized int[] getTotalWork() { return arrayCopyOf(totalWork); }
  public synchronized int[] getTempWork() { return arrayCopyOf(tempWork); }
  public synchronized int[][] getPtempWork() { return arrayCopyOf(ptempWork); }

  // Work done by this server.
  // Called from PermP commitCompleted if not a donation.
  public synchronized void adjustServerWork(int[] amount) {
    if (amount.length != numResourceTypes) {
      throw new IllegalArgumentException(
        "CurrentResources.adjustServerWork amount.length != numResourceTypes"
      );
    }
    for (int i = 0; i < serverWork.length; i++) {
      // It takes as much work to unload as load supplies on a truck.
      // It takes as much work to type in release as acquire an airline seat.
      // So take the absolute value.
      serverWork[i] += Math.abs(amount[i]);
    }
  }

  public synchronized boolean sCBwouldGoBelowZero(int[] change) {
    int newValue;
    for (int i = 0; i < numResourceTypes; i++) {
      newValue = serverCostBound[i] + change[i];
      if (newValue < 0) {
        return true;
      }
    }
    return false;
  }

  public synchronized boolean sCBwouldGoAboveMax(int[] change) {
    int newValue;
    for (int i = 0; i < numResourceTypes; i++) {
      newValue = serverCostBound[i] + change[i];
      if (newValue > maxValue[i]) {
        return true;
      }
    }
    return false;
  }

  public synchronized boolean sCBwouldViolate(int[] change) {
    int newValue;
    for (int i = 0; i < numResourceTypes; i++) {
      newValue = serverCostBound[i] + change[i];
      if (newValue < 0 || newValue > maxValue[i]) {
        return true;
      }
    }
    return false;
  }

  public synchronized int[] changeValue(int[] change, boolean donation) {
    if (change == null || change.length != numResourceTypes) {
      throw new IllegalArgumentException(
        "CurrentResources.changeValue argument null or wrong length"
      );
    }
    if (donation) {
//    System.out.println(
//      "CurrentResources changeValue donation"
//      + ": maxValue=" + arrayToString(maxValue)
//      + ", serverCostBound=" + arrayToString(serverCostBound)
//      + ", change=" + arrayToString(change)
//    );
      int addSCB, newSCB;
      for (int i = 0; i < numResourceTypes; i++) {
        maxValue[i] += change[i];
        addSCB = (int) Math.round((percentCostBound[i]*change[i]/100.0)
                                      *(1.0/numServers));
        newSCB = serverCostBound[i] + addSCB;
        serverCostBound[i] = newSCB;
        // later: replace 1/numServers with proportional to ptempWork
      }
//    System.out.println(
//      "CurrentResources changeValue donation"
//      + ": maxValue=" + arrayToString(maxValue)
//      + ", serverCostBound=" + arrayToString(serverCostBound)
//    );
    }
    // fill violations with -1 would go <0, 0 no violation, +1 would go >max
    int[] violations = new int[numResourceTypes];
    int[] newValue = new int[numResourceTypes];
    boolean noViolations = true;
    for (int i = 0; i < numResourceTypes; i++) {
      newValue[i] = value[i] + change[i];
      if (newValue[i] > maxValue[i]) {
        noViolations = false;
        violations[i] = +1;
      } else if (newValue[i] < 0) {
        noViolations = false;
        violations[i] = -1;
      } else {
        violations[i] = 0;
      }
    }
    if (noViolations) {
      for (int i = 0; i < numResourceTypes; i++) {
        value[i] = newValue[i];
        // adjustServerWork(int[] amount) above adds work done by this server
        // line below adds work done by all servers
        if (!donation) totalWork[i] += Math.abs(change[i]);
      }
      return null;  // no violations (array is all zeros, no need to return it)
    }
    return violations;  // report violation details
    // donation should be false here; check and throw exception
  }

  public boolean adjustPtempWork(int whoDidTemp, int change[]) {
    if (whoDidTemp >= 0) {
      for (int i = 0; i < numResourceTypes; i++) {
        ptempWork[whoDidTemp][i] += Math.abs(change[i]);
      }
      return true;
    } else {
      return false;
    }
  }

  // Done by temporary processor.
  public synchronized int[] changeCB(int[] change) {
    if (change == null || change.length != numResourceTypes) {
      throw new IllegalArgumentException(
        "CurrentResources.changeCB argument null or wrong length"
      );
    }
    // fill violations with -1 would go <0, 0 no violation, +1 would go >max
    int[] violations = new int[numResourceTypes];
    int[] newValue = new int[numResourceTypes];
    boolean noViolations = true;
    for (int i = 0; i < numResourceTypes; i++) {
      newValue[i] = serverCostBound[i] + change[i];
      if (newValue[i] > maxValue[i]) {
        noViolations = false;
        violations[i] = +1;
      } else if (newValue[i] < 0) {
        noViolations = false;
        violations[i] = -1;
      } else {
        violations[i] = 0;
      }
    }
    if (noViolations) {
      for (int i = 0; i < numResourceTypes; i++) {
        serverCostBound[i] = newValue[i];
        tempWork[i] += Math.abs(change[i]);
      }
      return null;  // no violations (array is all zeros, no need to return it)
    }
    return violations;  // report violation details
  }

  public synchronized int[] compensateWithinBoundsCB(int[] change) {
    return this.compensateWithinBoundsCB(change, false);
  }

  public synchronized int[] compensateWithinBoundsCB(
      int[] change, boolean permP) {
    // permP is true when this method is called from PermP else false
    // permP turns off reducing tempWork[]
    if (change == null || change.length != numResourceTypes) {
      throw new IllegalArgumentException(
     "CurrentResources.compensateWithinBoundsCB argument null or wrong length"
      );
    }
    // fill boundsReached with -1 would go <0, 0 no violation, +1 would go >max
    int[] boundsReached = new int[numResourceTypes];
    int[] newValue = new int[numResourceTypes];
    boolean noBoundsReached = true;
    for (int i = 0; i < numResourceTypes; i++) {
      newValue[i] = serverCostBound[i] + change[i];
      if (newValue[i] > maxValue[i]) {
        noBoundsReached = false;
        boundsReached[i] = +1;
        // -= instead of += because reversing earlier TempP work done
        if (!permP) tempWork[i] -= (maxValue[i]-serverCostBound[i]);
        // Following line must come after previous line!
        serverCostBound[i] = maxValue[i];
      } else if (newValue[i] < 0) {
        noBoundsReached = false;
        boundsReached[i] = -1;
        // -= instead of += because reversing earlier TempP work done
        if (!permP) tempWork[i] -= serverCostBound[i];
        // Following line must come after previous line!
        serverCostBound[i] = 0;
      } else {
        // Do NOT set noBoundsReached to true!
        boundsReached[i] = 0;
        serverCostBound[i] = newValue[i];
        // -= instead of += because reversing earlier TempP work done
        if (!permP) tempWork[i] -= Math.abs(change[i]);
      }
    }
    if (noBoundsReached) {
      return null;  // no violations (array is all zeros, no need to return it)
    }
    return boundsReached;  // report violations details
  }

  // Was done by permanent processor.
  private synchronized void setCBtoPermAdjustedCB(boolean w_sub_i_is_1_over_n) {
    for (int i = 0; i < numResourceTypes; i++) {
      serverCostBound[i] = permAdjustedCB(i, w_sub_i_is_1_over_n);
    }
  }

  private int permAdjustedCB(int i, boolean w_sub_i_is_1_over_n) {
        int result;
        if (w_sub_i_is_1_over_n) {

          // The following calculation does not allow for an upper bound
          // (e.g. 400) to be > initial cost bound (e.g. 200).

          // w_i = 1/n, n is the number of servers
          // Cnew = (1 - RA/R) * Cinit
          // Cnew_i = w_i * Cnew
          // where RA is (serv_imp.max_value - serv_imp.getValue()),
          // R is serv_imp.max_value, and
          // serv_imp.getinitCB is Cinit/n
/*****************************************************************************
          result = (int) (
            (1.0 -
              (current.getMaxValue(i) - current.getValue(i)) // RA
                           /
              ((double) current.getMaxValue(i))       // R
            ) * parms.getServerInitialCostBound(i)
          );
*****************************************************************************/
          result = (int) Math.round(
             ((value[i] * percentCostBound[i])/100.0)
                           /
             ((double) numServers)
          );
        } else {

          // w_i = (RA_i+1)/(RA+n)
          // Cnew = (1 - RA/R) * Cinit
          // Cnew_i = w_i * Cnew
          // where RA is (serv_imp.max_value - serv_imp.getValue()),
          // R is serv_imp.max_value, and
          // serv_imp.getinitCB is Cinit/n
/*****************************************************************************
          result = (int) (
            ( (double) (current.getServerWork(i) + 1)) // RA_i+1
                 /
            (current.getMaxValue(i) - current.getValue(i) + numServers) // RA+n
             *
               (1.0 -
                 (current.getMaxValue(i) - current.getValue(i))  // RA
                           /
                 ((double) current.getMaxValue(i))  // R
               ) * parms.getServerInitialCostBound(i)       // we want Cinit not Cinit/n so
                 * ( (double) numServers )  // multiple by n here
          );
*****************************************************************************/
          result = (int) Math.round(
             ((value[i] * percentCostBound[i])/100.0)
                           *
            ( ((double) (serverWork[i] + 1)) // RA_i+1
                 /
            (totalWork[i] + numServers)) // RA+n
          );
        }
        if (result == 0 && value[i] >= 1) {
          result = 1;
        }
        return result;
  }

  public synchronized String toString() {
    StringBuffer b = new StringBuffer();
    b.append("{sCB");
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(serverCostBound[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append("{mxV");
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(maxValue[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append("{val");
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(value[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append("{seW");
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(serverWork[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append("{toW");
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(totalWork[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append("{teW");
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(tempWork[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append("{ptW");
    for (int j = 0; j < numServers; j++) {
      b.append(' ');
      b.append('{');
      for (int i = 0; i < numResourceTypes; i++) {
        b.append(rightJustify(ptempWork[j][i], 5));
      }
      b.append(' ');
      b.append('}');
    }
    b.append(' ');
    b.append('}');
    return b.toString();
  }

}
