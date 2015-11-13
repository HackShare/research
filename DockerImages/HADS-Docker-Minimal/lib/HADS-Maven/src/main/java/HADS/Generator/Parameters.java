package HADS.Generator;

import static HADS.Generator.Statics.*;

public class Parameters {             // immutable, hooray!
  private final int numServers;
  private final int numResourceTypes;
  private final int[] initialCostBound;
  private final int[] upperCostBound;
  private final int[] donationMin;
  private final int[] donationMax;
  private final int[][] minChange;
  private final int[][] maxChange;
  private final int[][] decRatio;
  private final int[][] incRatio;

  public Parameters(
    int[] initialCostBound,
    int[] upperCostBound,
    int[] donationMin,
    int[] donationMax,
    int[][] minChange,
    int[][] maxChange,
    int[][] decRatio,
    int[][] incRatio
      ) {
    this.numServers = minChange.length;
    if (this.numServers <= 0) {
      throw new IllegalArgumentException("Parameters: numServers = " + this.numServers + " is <= 0");
    }
    if (this.numServers !=  maxChange.length
     || this.numServers !=  decRatio.length
     || this.numServers !=  incRatio.length) {
      throw new IllegalArgumentException("Parameters: 2d arrays 1stD are not all the same length");
    }
    this.numResourceTypes = minChange[0].length;
    if (this.numResourceTypes <= 0) {
      throw new IllegalArgumentException("Parameters: numResourceTypes = " + this.numResourceTypes + " is <= 0");
    }
    if (this.numResourceTypes !=  initialCostBound.length
     || this.numResourceTypes !=  upperCostBound.length
     || this.numResourceTypes !=  donationMin.length
     || this.numResourceTypes !=  donationMax.length) {
      throw new IllegalArgumentException("Parameters: 1d arrays are not all the same length");
    }
    for (int i = 0; i < numServers; i++) {
      if (this.numResourceTypes != minChange[i].length
       || this.numResourceTypes != maxChange[i].length
       || this.numResourceTypes != decRatio[i].length
       || this.numResourceTypes != incRatio[i].length) {
      throw new IllegalArgumentException("Parameters: 2d arrays 2ndD are not all the same length");
      }
    }

    this.initialCostBound = arrayCopyOf(initialCostBound);
    this.upperCostBound = arrayCopyOf(upperCostBound);
    this.donationMin = arrayCopyOf(donationMin);
    this.donationMax = arrayCopyOf(donationMax);

    this.minChange = arrayCopyOf(minChange);
    this.maxChange = arrayCopyOf(maxChange);
    this.decRatio = arrayCopyOf(decRatio);
    this.incRatio = arrayCopyOf(incRatio);
  }

  public int getNumServers() { return numServers; }
  public int getNumResourceTypes() { return numResourceTypes; }
  public int getInitialCostBound(int i) { return initialCostBound[i]; }
  public int getUpperCostBound(int i) { return upperCostBound[i]; }
  public int getDonationMin(int i) { return donationMin[i]; }
  public int getDonationMax(int i) { return donationMax[i]; }

  public int[] getMinChange(int i) { return arrayCopyOf(minChange[i]); }
  public int[] getMaxChange(int i) { return arrayCopyOf(maxChange[i]); }
  public int[] getDecRatio(int i) { return arrayCopyOf(decRatio[i]); }
  public int[] getIncRatio(int i) { return arrayCopyOf(incRatio[i]); }

  public int getMinChange(int i, int j) { return minChange[i][j]; }
  public int getMaxChange(int i, int j) { return maxChange[i][j]; }
  public int getDecRatio(int i, int j) { return decRatio[i][j]; }
  public int getIncRatio(int i, int j) { return incRatio[i][j]; }

  public int[] getInitialCostBound() { return arrayCopyOf(initialCostBound); }
  public int[] getUpperCostBound() { return arrayCopyOf(upperCostBound); }
  public int[] getDonationMin() { return arrayCopyOf(donationMin); }
  public int[] getDonationMax() { return arrayCopyOf(donationMax); }

  public int[][] getMinChange() { return arrayCopyOf(minChange); }
  public int[][] getMaxChange() { return arrayCopyOf(maxChange); }
  public int[][] getDecRatio() { return arrayCopyOf(decRatio); }
  public int[][] getIncRatio() { return arrayCopyOf(incRatio); }

  public static void main(String[] args) {
    Parameters gp = new Parameters(
      new int[] {13, 14, 15},
      new int[] {16, 17, 18},
      new int[] {19, 20, 21},
      new int[] {22, 23, 24},
      new int[][] { {1, 2, 3}, {101, 102, 103} },
      new int[][] { {4, 5, 6}, {204, 205, 206} },
      new int[][] { {7, 8, 9}, {307, 308, 309} },
      new int[][] { {10, 11, 12}, {410, 411, 412} }
    );
    if (gp.getNumServers() != 2) {
      throw new RuntimeException("num servers is not 2");
    }
    if (gp.getNumResourceTypes() != 3) {
      throw new RuntimeException("num resources is not 3");
    }
    if (gp.getDonationMin(0) != 19) {
      throw new RuntimeException("is not 19");
    }
    if (gp.getDonationMax(2) != 24) {
      throw new RuntimeException("is not 19");
    }
    if (gp.getMinChange(0, 0) != 1) {
      throw new RuntimeException("is not 1");
    }
    if (gp.getMinChange(0, 1) != 2) {
      throw new RuntimeException("is not 2");
    }
    if (gp.getMinChange(0, 2) != 3) {
      throw new RuntimeException("is not 3");
    }
    if (gp.getMaxChange(0, 0) != 4) {
      throw new RuntimeException("is not 4");
    }
    if (gp.getMaxChange(0, 1) != 5) {
      throw new RuntimeException("is not 5");
    }
    if (gp.getMaxChange(0, 2) != 6) {
      throw new RuntimeException("is not 6");
    }
    if (gp.getDecRatio(0, 0) != 7) {
      throw new RuntimeException("is not 7");
    }
    if (gp.getDecRatio(0, 1) != 8) {
      throw new RuntimeException("is not 8");
    }
    if (gp.getDecRatio(0, 2) != 9) {
      throw new RuntimeException("is not 9");
    }
    if (gp.getIncRatio(0, 0) != 10) {
      throw new RuntimeException("is not 10");
    }
    if (gp.getIncRatio(0, 1) != 11) {
      throw new RuntimeException("is not 11");
    }
    if (gp.getIncRatio(0, 2) != 12) {
      throw new RuntimeException("is not 12");
    }
  }

}
