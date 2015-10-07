package HADS.Server;

import static HADS.Generator.Statics.*;

class Parameters {                                      // immutable, hooray!
  private final int numServers;
  private final int numResourceTypes;
  private final int[] initialCostBound;              // was initialCB
  private final int[] upperCostBound;
  private final int[] percentCostBound;              // was pctCB
  private final int[] serverInitialCostBound;        // was initCB

  public Parameters(
    int[] initialCostBound,
    int[] upperCostBound,
    int[] percentCostBound,
    int numServers
  ) {
    this.numResourceTypes = initialCostBound.length;
    if (initialCostBound.length !=  percentCostBound.length) {
      throw new IllegalArgumentException(
        "Parameters: initialCostBound.length !=  percentCostBound.length"
      );
    }
    if (numServers <= 0) {
      throw new IllegalArgumentException(
        "Parameters: numServers <= 0"
      );
    }
    this.numServers = numServers;
    if (!allPositive(initialCostBound)) {
      throw new IllegalArgumentException(
        "Parameters: initialCostBound not all positive"
      );
    }
    if (!allPositive(upperCostBound)) {
      throw new IllegalArgumentException(
        "Parameters: upperCostBound not all positive"
      );
    }
//  if (!allHundredOrMore(percentCostBound)) {
//    throw new IllegalArgumentException(
//      "Parameters: percentCostBound not all >= 100"
//    );
//  }
//  I want to allow experiments where percentCostBound < 100.
    int threshhold = 10;
    if (!allThreshholdOrMore(percentCostBound, threshhold)) {
      throw new IllegalArgumentException(
        "Parameters: percentCostBound not all >= " + threshhold
      );
    }
    this.initialCostBound = arrayCopyOf(initialCostBound);
    this.upperCostBound = arrayCopyOf(upperCostBound);
    this.percentCostBound = arrayCopyOf(percentCostBound);
    this.serverInitialCostBound = new int[numResourceTypes];
    for (int i = 0; i < numResourceTypes; i++) {
      this.serverInitialCostBound[i] = (int)
        Math.round(
          (((this.percentCostBound[i]/100.0)*this.initialCostBound[i])
            /
          ((double) this.numServers))
        );
    }
  }

  public int getNumServers() { return numServers; }
  public int getNumResourceTypes() { return numResourceTypes; }

  public int getInitialCostBound(int i) { return initialCostBound[i]; }
  public int getUpperCostBound(int i) { return upperCostBound[i]; }
  public int getPercentCostBound(int i) { return percentCostBound[i]; }
  public int getServerInitialCostBound(int i) {
    return serverInitialCostBound[i];
  }

  public int[] getInitialCostBound() { return arrayCopyOf(initialCostBound); }
  public int[] getUpperCostBound() { return arrayCopyOf(upperCostBound); }
  public int[] getPercentCostBound() { return arrayCopyOf(percentCostBound); }
  public int[] getServerInitialCostBound() {
    return arrayCopyOf(serverInitialCostBound);
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("numServers=" + numServers + " ");
    b.append('{');
    b.append('I');
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(initialCostBound[i], 5));
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
    b.append('%');
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(percentCostBound[i], 5));
    }
    b.append(' ');
    b.append('}');
    b.append(' ');
    b.append('{');
    b.append('S');
    for (int i = 0; i < numResourceTypes; i++) {
      b.append(rightJustify(serverInitialCostBound[i], 5));
    }
    b.append(' ');
    b.append('}');
    return b.toString();
  }

}
