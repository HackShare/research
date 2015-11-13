package HADS.Generator;

import static HADS.Generator.Statics.*;

import java.io.Serializable;

public class TransactionRequest implements Serializable { // immutable, hooray!
  private final int numResourceTypes;
  private final int whichOne;
  private final String serverSentTo;
  private final int[] changeBy;
  private final boolean donation;

  public TransactionRequest(int whichOne, String serverSentTo,
      int[] changeBy) {
    this(whichOne, serverSentTo, changeBy, false);
  }

  public TransactionRequest(int whichOne, String serverSentTo,
      int[] changeBy, boolean donation) {
    if (whichOne < 0) {
      throw new IllegalArgumentException("TransactionRequest: whichOne = " + whichOne + " is < 0");
    } else if (serverSentTo == null || serverSentTo.length() == 0) {
      throw new IllegalArgumentException("TransactionRequest: String serverSentTo is null or has zero length.");
    } else if (changeBy.length <= 0) {
      throw new IllegalArgumentException("TransactionRequest: changeBy.length = " + changeBy.length + " is <= 0");
    } else if (allZeros(changeBy)) {
      throw new IllegalArgumentException("TransactionRequest: changeBy is all zeros");
    } else if (donation && anyNegative(changeBy)) {
      throw new IllegalArgumentException("TransactionRequest: donation and a changeBy is negative");
    } else {
      this.numResourceTypes = changeBy.length;
      this.whichOne = whichOne;
      this.serverSentTo = new String(serverSentTo);
      this.changeBy = arrayCopyOf(changeBy);
      this.donation = donation;
    }
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append(rightJustify(whichOne, 10));
    b.append(' ');
    b.append('{');
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(changeBy[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append(serverSentTo);
    b.append(' ');
    b.append( donation?'D':'n' );
    return b.toString();
  }

  public int getNumResourceTypes() { return numResourceTypes; }
  public int getWhichOne() { return whichOne; }
  public String getServerSentTo() { return serverSentTo; }
  public int getChangeBy(int i) { return changeBy[i]; }
  public int[] getChangeBy() { return arrayCopyOf(changeBy); }
  public boolean isDonation() { return donation; }

  public boolean isDecrease() {
    for (int i = 0; i < numResourceTypes; i++) {
      // none is zero by this point, so use > instead of >=
      // Did I mean `not all are' instead of `none is'?
      if (changeBy[i] > 0) return false;
    }
    return true;
  }

  public boolean isIncrease() {
    for (int i = 0; i < numResourceTypes; i++) {
      // none is zero by this point, so use < instead of <=
      // Did I mean `not all are' instead of `none is'?
      if (changeBy[i] < 0) return false;
    }
    return true;
  }

  public static void main(String[] args) {
    TransactionRequest tr = new TransactionRequest(123, "TheServer", new int[] {10, -20, 30});
    System.out.println(tr);
    if (tr.isDonation()) {
      throw new RuntimeException("is not a donation");
    }
    if (tr.getNumResourceTypes() != 3) {
      throw new RuntimeException("num resources is not 3");
    }
    if (tr.getWhichOne() != 123) {
      throw new RuntimeException("whichOne is not 123");
    }
    if (!tr.getServerSentTo().equals("TheServer")) {
      throw new RuntimeException("server is not 'TheServer'");
    }
    if (tr.isIncrease()) {
      throw new RuntimeException("is not an increase");
    }
    if (tr.isDecrease()) {
      throw new RuntimeException("is not a decrease");
    }
    if (tr.getChangeBy(0) != 10) {
      throw new RuntimeException("is not 10");
    }
    if (tr.getChangeBy(1) != -20) {
      throw new RuntimeException("is not -20");
    }
    if (tr.getChangeBy(2) != 30) {
      throw new RuntimeException("is not 30");
    }
    tr = new TransactionRequest(1, "TheServer", new int[] {10, 20, 30}, true);
    System.out.println(tr);
    if (!tr.isIncrease()) {
      throw new RuntimeException("is an increase");
    }
    if (!tr.isDonation()) {
      throw new RuntimeException("is a donation");
    }
    tr = new TransactionRequest(2, "TheServer", new int[] {-1, -2, -3});
    System.out.println(tr);
    if (!tr.isDecrease()) {
      throw new RuntimeException("is a decrease");
    }
    tr = new TransactionRequest(3, "TheOtherServer", new int[] {-111});
    System.out.println(tr);
    tr = new TransactionRequest(4, "Server", new int[] {222});
    System.out.println(tr);
  }

}
