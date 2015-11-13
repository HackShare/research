package HADS.Server;

import static HADS.Generator.Statics.*;
import static HADS.Server.Constants.*;

import HADS.Generator.TransactionRequest;

import java.util.*;
import java.io.*;

// Effectively immutable because all setX method, renamed newX,
// return a new object.

public class Transaction implements Serializable, Comparable<Transaction> {
  // Make all the fields `final' after recoding this class to be
  // immutable like ImageRecord is coded.
  private int transaction_id; // never changes (compile errors if `final')
  private long timestamp;
  private int transaction_code;
  private int transaction_state;
  private int transaction_change[];
  private int numResourceTypes; // never changes (compile errors if `final')
  private boolean violation;
  private ImageRecord outImage;
  private String InitiatingServer;
  private String FromServer;
  private String whoDidTemp;
  private int whoDidTempIndex;
  private boolean CBsat;
  private TransactionRequest transReq;  // an immutable object

  public Transaction(int transaction_id, int transaction_code,
      String InitiatingServer, TransactionRequest transReq,
      long timestamp) {
    if (transReq.isDonation() && transaction_code != DONATION) {
      throw new IllegalArgumentException(
        "Transaction: is a donation but transaction_code is "
        + numToName(transaction_code)
      );
    }
    this.transaction_id = transaction_id;
    this.timestamp = timestamp;
    this.transaction_code = transaction_code;
    this.transaction_state = 0;
    this.transaction_change = transReq.getChangeBy();
    this.numResourceTypes = transaction_change.length;
    this.violation = false;
    this.outImage = ImageRecord.newDefaults(transaction_id);
    this.InitiatingServer = InitiatingServer;
    this.FromServer = transReq.getServerSentTo();
    this.whoDidTemp = "Unknown";
    this.whoDidTempIndex = -1;
    this.CBsat = false;
    this.transReq = transReq;
  }

  private Transaction() { } // used by duplicate() below

  public static Transaction duplicate(Transaction t) {
    Transaction dupl = new Transaction();

   // all are primitive or immutable types except transaction_change, outImage
    dupl.transaction_id = t.transaction_id;
    dupl.timestamp = t.timestamp;
    dupl.transaction_code = t.transaction_code;
    dupl.transaction_state = t.transaction_state;
    dupl.transaction_change = t.getTransChange();  // creates copy
    dupl.numResourceTypes = t.numResourceTypes;
    dupl.violation = t.violation;
    dupl.outImage = ImageRecord.newDuplicate(t.getOutImage()); // creates copy
    dupl.InitiatingServer = t.InitiatingServer;
    dupl.FromServer = t.FromServer;
    dupl.whoDidTemp = t.whoDidTemp;
    dupl.whoDidTempIndex = t.whoDidTempIndex;
    dupl.CBsat = t.CBsat;
    dupl.transReq = t.transReq;                   // immutable object

    return dupl;
  }

/* ********************Accessing Data items******************/

  // TransactionRequest from generator
  public TransactionRequest getTR() { return this.transReq; }

  public boolean isDonation() { return this.transReq.isDonation(); }

  public int getID() { return transaction_id; }

  public long getTimeStamp() { return timestamp; }

  //Transaction Code
  public Transaction newTransCode(int code) {
    Transaction result = duplicate(this);
    result.transaction_code=code;
    return result;
  }

  public int getTransCode() { return transaction_code; }

  //Transaction State
  public Transaction newTransState(int state) {
    Transaction result = duplicate(this);
    result.transaction_state=state;
    return result;
  }

  public int getTransState() { return transaction_state; }

  //Transaction Change
  public Transaction newTransChange(int[] change) {
    Transaction result = duplicate(this);
    result.transaction_change=arrayCopyOf(change);
    return result;
  }

  public int[] getTransChange() { return arrayCopyOf(transaction_change); }

  public String getInitiatingServer() { return InitiatingServer; }

  //From Server
  public Transaction newFromServer(String ServerHN) {
    Transaction result = duplicate(this);
    result.FromServer=ServerHN;
    return result;
  }

  public String getFromServer() { return FromServer; }

  //Who did temporary processing?
  public Transaction newWhoDidTemp(String ServerHN, int index) {
    Transaction result = duplicate(this);
    result.whoDidTemp=ServerHN;
    result.whoDidTempIndex = index;
    return result;
  }

  public String getWhoDidTemp() { return whoDidTemp; }
  public int getWhoDidTempIndex() { return whoDidTempIndex; }

  public boolean getViolation() { return violation; }

  public Transaction newViolation(boolean violation) {
    Transaction result = duplicate(this);
    result.violation=violation;
    return result;
  }

  public boolean getCBsat() { return CBsat; }

  public Transaction newCBsat(boolean CBsat) {
    Transaction result = duplicate(this);
    result.CBsat=CBsat;
    return result;
  }

  public boolean equals(Object otherObject) {
    if (this == otherObject) return true;
    if (otherObject == null) return false;
    if (getClass() != otherObject.getClass()) return false;
    Transaction t = (Transaction) otherObject;
    return transaction_id == t.transaction_id;
  }

  public int compareTo(Transaction other) {
    if (other == null) {
      throw new NullPointerException(      // required by interface Comparable
        "Transaction compareTo: other is null"
      );
    }
    // if (this.equals(other)) return 0;  // consistent with equals
    if (transaction_id == other.transaction_id) return 0;
    else if (transaction_id < other.transaction_id) return -1;
    else /* if (transaction_id > other.transaction_id) */ return 1;
  }

  public ImageRecord getOutImage() {
    return ImageRecord.newDuplicate(outImage);  // makes a copy
  }

  public Transaction newOutImage(ImageRecord r) {
    Transaction result = duplicate(this);
    result.outImage = ImageRecord.newDuplicate(r);  // makes a copy
    return result;
  }

  public String toString() {
    return " Transaction: transaction_id=" + transaction_id +
           ", timestamp=" + timestamp +
           ", transaction_code=" + numToName(transaction_code) +
           ", transaction_state=" + numToName(transaction_state) +
           ", transaction_change=" + arrayToString(transaction_change) +
           ", InitiatingServer=" + InitiatingServer +
           ", whoDidTemp=" + whoDidTemp +
           ", whoDidTempIndex=" + whoDidTempIndex +
           ", violation=" + violation +
           ", FromServer=" + FromServer +
           ", CBsat=" + CBsat +
           ", transReq=" + transReq.toString();
  }
}
