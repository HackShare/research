package HADS.Generator;

import static HADS.Generator.Statics.*;

class CurrentResources {
  private final int numResourceTypes;
  private final int[] currentCostBound;
  private final int[] upperCostBound;
  private final int[] totalWork;         // excludes processing donations
  private final int[] totalDonations;

  public CurrentResources(Parameters parms) {
    this.numResourceTypes = parms.getNumResourceTypes();
    this.currentCostBound = parms.getInitialCostBound();
    this.upperCostBound = parms.getUpperCostBound();
    this.totalWork = new int[this.numResourceTypes];
    this.totalDonations = new int[this.numResourceTypes];
    for (int i = 0; i < this.numResourceTypes; i++) {
      totalWork[i] = 0;
      totalDonations[i] = 0;
    }
  }

  public int getCurrentCostBound(int i) { return currentCostBound[i]; }
  // I think currentCostBound here corresponds to value in the Server
  // CurrentResources class.  Ugh!  Also currentCostBound here is
  // initialized from initialCostBound in the Parameter class here.
  public int[] getCurrentCostBound() { return arrayCopyOf(currentCostBound); }

  public int getTotalWork(int i) { return totalWork[i]; }
  public int[] getTotalWork() { return arrayCopyOf(totalWork); }
  public int[] getTotalDonations() { return arrayCopyOf(totalDonations); }

  private boolean changeCurrentCostBound(int i, int change, boolean abort) {
    int newValue = currentCostBound[i] + change;
    if (newValue > upperCostBound[i] || newValue < 0) {
      if (abort) {
        throw new IllegalArgumentException(
          "CurrentResources.changeCurrentCostBound: change is too big"
          + ", currentCostBound[" + i + "]=" + currentCostBound[i]
          + ", upperCostBound[" + i + "]=" + upperCostBound[i]
          + ", change=" + change + ", newValue=" + newValue
        );
      } else {
        return false;
      }
    } else {
      currentCostBound[i] = newValue;
      totalWork[i] += Math.abs(change);
    }
    return true;
  }

  public void makeDonation(TransactionRequest tr) {
    int change, newValue;
    for (int i = 0; i < numResourceTypes; i++) {
      change = tr.getChangeBy(i);
      if (change < 0) {
        throw new RuntimeException(
  "CurrentResources makeDonation: change[" + i + "]=" + change + " is < 0"
        );
      } else {
        change = tr.getChangeBy(i);
        upperCostBound[i] = upperCostBound[i] + change;
        currentCostBound[i] = currentCostBound[i] + change;
        totalDonations[i] += change;
      }
    }
  }

  public boolean exceedsUpperBound(TransactionRequest tr) {
    int change, newValue;
    for (int i = 0; i < numResourceTypes; i++) {
      change = tr.getChangeBy(i);
      newValue = currentCostBound[i] + change;
      if (newValue > upperCostBound[i]) {
        return true;
      }
    }
    return false;
  }

  public boolean goesBelowZero(TransactionRequest tr) {
    int change, newValue;
    for (int i = 0; i < numResourceTypes; i++) {
      change = tr.getChangeBy(i);
      newValue = currentCostBound[i] + change;
      if (newValue < 0) {
        return true;
      }
    }
    return false;
  }

  public boolean isAcceptable(TransactionRequest tr) {
    int change, newValue;
    for (int i = 0; i < numResourceTypes; i++) {
      change = tr.getChangeBy(i);
      newValue = currentCostBound[i] + change;
      if (newValue > upperCostBound[i] || newValue < 0) {
        return false;
      }
    }
    return true;
  }

  public void perform(TransactionRequest tr, boolean abort) {
    for (int i = 0; i < numResourceTypes; i++) {
      changeCurrentCostBound(i, tr.getChangeBy(i), abort);
    }
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append('{');
    b.append('C');
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(currentCostBound[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append('{');
    b.append('U');
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(upperCostBound[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append('{');
    b.append('W');
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(totalWork[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append('{');
    b.append('D');
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(totalDonations[i], 5));
    }
    b.append(' ');
    b.append('}');
    return b.toString();
  }

}
