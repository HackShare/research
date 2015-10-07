package HADS.Server;

import static HADS.Generator.Statics.*;

import java.io.*;
import java.util.*;


// Effectively immutable because all setX method, renamed newX,
// return a new object.

public class ResultRecord implements Serializable {
  private int result_id;
  private boolean donation;
  private long ResponseTime;
  private long TimeIn;
  private long TimeOut;
  private long TempTimeOut;
  private boolean TempProcessed=false;
  private String whoDidTemp = "Unknown";
  private int whoDidTempIndex = -1;
  private boolean UndoRequired=false;
  private boolean Violation=false;
  private int[] oldValue = null, change = null, newValue = null;

  public ResultRecord(Transaction t) {
    result_id=t.getID();
    donation = t.isDonation();
    ResponseTime=0;
    TimeIn=t.getTimeStamp();
    TimeOut=0;
    //change this to temptime in transaction - we need to adjust transaction
    TempTimeOut=0;
    TempProcessed=false;
    whoDidTemp = t.getWhoDidTemp();
    whoDidTempIndex = t.getWhoDidTempIndex();
    UndoRequired=false;
    Violation=t.getViolation();
    change = t.getTransChange();
    oldValue = aZeroFilledArray(change.length);
    newValue = aZeroFilledArray(change.length);
  }

  private ResultRecord() { }

  public static ResultRecord duplicate(ResultRecord r) {
    ResultRecord dupl = new ResultRecord();
   // all are primitive or immutable types except oldValue, change, newValue
    dupl.result_id = r.result_id;
    dupl.donation = r.donation;
    dupl.ResponseTime = r.ResponseTime;
    dupl.TimeIn = r.TimeIn;
    dupl.TimeOut = r.TimeOut;
    dupl.TempTimeOut = r.TempTimeOut;
    dupl.TempProcessed = r.TempProcessed;
    dupl.whoDidTemp = r.whoDidTemp;
    dupl.whoDidTempIndex = r.whoDidTempIndex;
    dupl.UndoRequired = r.UndoRequired;
    dupl.Violation = r.Violation;
    dupl.oldValue = arrayCopyOf(r.oldValue);
    dupl.change = arrayCopyOf(r.change);
    dupl.newValue = arrayCopyOf(r.newValue);
    return dupl;
  }

  public int returnId() { return result_id; }
  public long returnIn() { return TimeIn; }
  public long returnOut() { return TimeOut; }
  public String getWhoDidTemp() { return whoDidTemp; }
  public int getWhoDidTempIndex() { return whoDidTempIndex; }
  public boolean isDonation() { return donation; }

  public ResultRecord newUndoRequired() {
    ResultRecord result = duplicate(this);
    result.UndoRequired = true;
    return result;
  }

  public ResultRecord newTempTimeOut(long time, String who, int index) {
    ResultRecord result = duplicate(this);
    result.TempTimeOut = time;
    result.whoDidTemp = who;
    result.whoDidTempIndex = index;
    result.TempProcessed = true;
    return result;
  }

  public ResultRecord newOut(long TimeOut) {
    ResultRecord result = duplicate(this);
    result.TimeOut = TimeOut;
    result.ResponseTime = result.TimeOut - result.TimeIn;
    return result;
  }

  public ResultRecord newValues(int[] oV, int[] nV) {
    ResultRecord result = duplicate(this);
    result.oldValue = arrayCopyOf(oV);
    result.newValue = arrayCopyOf(nV);
    return result;
  }

  public boolean equals(Object otherObject) {
    if (this == otherObject) return true;
    if (otherObject == null) return false;
    if (getClass() != otherObject.getClass()) return false;
    ResultRecord rec = (ResultRecord) otherObject;
    return result_id == rec.result_id;
  }

  public String dump() {
    StringBuffer oldValues = new StringBuffer();
    StringBuffer changes = new StringBuffer();
    StringBuffer newValues = new StringBuffer();
    oldValues.append('{'); changes.append('{'); newValues.append('{');
    for (int i =0; i < oldValue.length; i++) {
       oldValues.append(" " + oldValue[i]);
       changes.append(" " + change[i]);
       newValues.append(" " + newValue[i]);
    }
    oldValues.append(' '); changes.append(' '); newValues.append(' ');
    oldValues.append('}'); changes.append('}'); newValues.append('}');
    return "ResultRecord: result_id=" + result_id
      + ", " + (donation?"DONATION":"normal")
      + ", TimeIn=" + TimeIn
      + ", TimeOut=" + TimeOut
      + ", ResponseTime=" + ResponseTime
      + ", TempProcessed=" + (TempProcessed ? "TEMP" : "no-t")
      + ", TempTimeOut=" + TempTimeOut
      + ", whoDidTemp=" + whoDidTemp
      + ", whoDidTempIndex=" + whoDidTempIndex
      + ", UndoRequired=" + (UndoRequired ? "UNDO" : "no-u")
      + ", Violation=" + (Violation ? "VIOLATE" : "   no-v")
      + ", oldValue=" + oldValues
      + ", change=" + changes
      + ", newValue=" + newValues;
  }

  public String toString() {
    String S;
    StringBuffer oldValues = new StringBuffer();
    StringBuffer changes = new StringBuffer();
    StringBuffer newValues = new StringBuffer();
    oldValues.append('{'); changes.append('{'); newValues.append('{');
    for (int i =0; i < oldValue.length; i++) {
       oldValues.append(" " + oldValue[i]);
       changes.append(" " + change[i]);
       newValues.append(" " + newValue[i]);
    }
    oldValues.append(' '); changes.append(' '); newValues.append(' ');
    oldValues.append('}'); changes.append('}'); newValues.append('}');
    S =
              rightJustify(result_id, 10)
      + " " + rightJustify((donation ? "DNTN" : "norm"), 6)
      + " " + rightJustify(TimeIn, 15)
      + " " + rightJustify(TimeOut, 15)
      + " " + rightJustify(ResponseTime, 10)
      + " " + rightJustify((TempProcessed ? "TEMP" : "no-t"), 6)
      + " " + rightJustify(TempTimeOut, 15)
      + " " + rightJustify(whoDidTemp, 15)
      + " " + rightJustify(whoDidTempIndex, 5)
      + " " + rightJustify((UndoRequired ? "UNDO" : "no-u"), 6)
      + " " + rightJustify((Violation ? "VIOLATE" : "   no-v"), 7)
      + " " + oldValues
      + " " + changes
      + " " + newValues
      + "  ";
    return S;
  }

}//end class
