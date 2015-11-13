package HADS.Server;

import static HADS.Generator.Statics.*;

import java.io.*;

public class ImageRecord implements Serializable {    // immutable, hooray!
  private final int[] CostBound;
  private final int[] change;
  private final int transaction_id;
  private final String whoDidTemp;             // = "Unknown";
  private final int whoDidTempIndex;           // = -1;
  private final long TempTimeCompleted;        // = 0;
  // This field is whether or not TempP could do it, not PermP.
  private final boolean violation;             // = false;

  private ImageRecord() {
      this.CostBound = aZeroFilledArray(0);  // cost bound
      this.change = aZeroFilledArray(0);     // change
      this.transaction_id = 0;               // transaction ID
      this.whoDidTemp = "Unknown";           // who did temp
      this.whoDidTempIndex = -1;             // who did temp index
      this.TempTimeCompleted = 0;            // temp time completed
      this.violation = false;                // violation
  }

  private ImageRecord(
      int[] CB,
      int[] ch,
      int T_id,
      String wDT,
      int wDTI,
      long tTC,
      boolean v) {
    if (CB == null || ch == null || T_id < 0
        || wDT == null || wDTI < -1 || tTC < 0) {
      throw new IllegalArgumentException(
        "ImageRecord: CB == null || ch == null || T_id < 0"
        + " || wDT == null || tTC < 0"
      );
    } else {
      this.CostBound = arrayCopyOf(CB);
      this.change = arrayCopyOf(ch);
      this.transaction_id = T_id;
      this.whoDidTemp = wDT;
      this.whoDidTempIndex = wDTI;
      this.TempTimeCompleted = tTC;
      this.violation = v;
    }
  }

  public static ImageRecord newOne(
      int[] CB,
      int[] ch,
      int T_id,
      String wDT,
      int wDTI,
      long tTC,
      boolean v) {
    return new ImageRecord(CB, ch, T_id, wDT, wDTI, tTC, v);
  }

  public static ImageRecord newDuplicate(ImageRecord r) {
    return new ImageRecord(
// all are primitive or immutable types except CB, change
      r.CostBound, r.change, r.transaction_id, r.whoDidTemp,
      r.whoDidTempIndex, r.TempTimeCompleted, r.violation
    );
  }

  public ImageRecord duplicate() {
    return new ImageRecord(
      this.CostBound, this.change, this.transaction_id,
      this.whoDidTemp, this.whoDidTempIndex, this.TempTimeCompleted,
      this.violation
    );
  }

  public static ImageRecord newDefaults() { return new ImageRecord(); }

  public static ImageRecord newDefaults(int transaction_id) {
    return new ImageRecord(
      aZeroFilledArray(0),      // cost bound
      aZeroFilledArray(0),      // change
      transaction_id,           // transaction ID
      "Unknown",                // who did temp
      -1,                       // who did temp index
      0,                        // temp time completed
      false                     // violation
    );
  }

  public static ImageRecord newDefaults(int[] CB) {
    return new ImageRecord(
      CB,                       // cost bound
      aZeroFilledArray(0),      // change
      0,                        // transaction ID
      "Unknown",                // who did temp
      -1,                       // who did temp index
      0,                        // temp time completed
      false                     // violation
    );
  }

  public int[] getCB() { return arrayCopyOf(CostBound) ; }
  public int[] getChange() { return arrayCopyOf(change) ; }
  public int getTransaction_id() { return transaction_id; }
  public String getWhoDidTemp() { return whoDidTemp; }
  public int getWhoDidTempIndex() { return whoDidTempIndex; }
  public long getTempTimeCompleted() { return TempTimeCompleted; }
  public boolean getViolation() { return violation; }

  public ImageRecord newCB(int[] CB) {
    return new ImageRecord(
      CB,             this.change, this.transaction_id,
      this.whoDidTemp, this.whoDidTempIndex, this.TempTimeCompleted,
      this.violation
    );
  }

  public ImageRecord changeCB(int[] change) {
    int[] temp = getCB();
    for (int i = 0; i < change.length; i++) {
      temp[i] += change[i];
    }
    return this.newCB(temp);
  }

  public ImageRecord newChange(int[] c) {
    return new ImageRecord(
      this.CostBound, c,           this.transaction_id, this.whoDidTemp,
      this.whoDidTempIndex, this.TempTimeCompleted, this.violation
    );
  }

  public ImageRecord newTransaction_id(int id) {
    return new ImageRecord(
      this.CostBound, this.change, id, this.whoDidTemp,
      this.whoDidTempIndex, this.TempTimeCompleted, this.violation
    );
  }

  public ImageRecord newViolation(boolean v) {
    return new ImageRecord(
      this.CostBound, this.change, this.transaction_id, this.whoDidTemp,
      this.whoDidTempIndex, this.TempTimeCompleted, v
    );
  }

  public boolean equals(Object otherObject) {
    if (this == otherObject) return true;
    if (otherObject == null) return false;
    if (getClass() != otherObject.getClass()) return false;
    ImageRecord ir = (ImageRecord) otherObject;
    return transaction_id == ir.transaction_id;
  }

  public String toString() {
    StringBuffer changes = new StringBuffer();
    StringBuffer CostBounds = new StringBuffer();
    changes.append('{'); CostBounds.append('{');
    for (int i =0; i < change.length; i++) {
       changes.append(" " + change[i]);
       CostBounds.append(" " + CostBound[i]);
    }
    changes.append(' '); CostBounds.append(' ');
    changes.append('}'); CostBounds.append('}');
    String noYES = (violation ? "VIOLATE" : "   no-v");
    return "ImageRecord: transaction_id=" + transaction_id
      + ", change = " + changes + ", CostBound=" + CostBounds
      + ", TempTimeCompleted=" + TempTimeCompleted
      + ", violation=" + noYES + ", whoDidTemp=" + whoDidTemp
      + ", whoDidTempIndex=" + whoDidTempIndex;
  }
}
