package HADS.Server;

import static HADS.Generator.Statics.*;
import static HADS.Server.Constants.*;

import java.io.*;
import java.util.*;

public class TemporaryProcessor extends Thread {
  private ServerImpl serv_imp;
  private ImageRecord OutImage;

  private CurrentResources current;
  private int numResourceTypes;

  private volatile boolean frozen; // is `volatile' good enough?

  public TemporaryProcessor(ServerImpl s, CurrentResources current) {
    super();
    this.current = current;
    this.numResourceTypes = current.getNumResourceTypes();
    serv_imp = s;
    this.frozen = false;
    OutImage = ImageRecord.newDefaults(current.getServerCostBound());
    try { this.setPriority(Thread.MIN_PRIORITY); }
    catch(Exception e) {
      System.out.println
        ("TempP Exception while setting Processor Priority: " + e);
    }
  }


// Semaphore frozenSync = 1;

  public boolean getFrozen() { return this.frozen; }

  public void freeze() {
    if (this.frozen) {
      throw new RuntimeException("TemporaryProcessor freeze: already frozen");
    } else {
      this.frozen = true;
// Suspend manageQ0: down(frozenSync);
// Send a freeze reply server exchange out to sender of freeze
// that contains this server's current cost bound and work done
// where work done is sum of absolute values of temp
// transactions (temp compensates subtract absolute value).
    }
  }

  public void unfreeze() {
    if (!this.frozen) {
      throw new RuntimeException("TemporaryProcessor unfreeze: not frozen");
    } else {
      this.frozen = false;
// Wait on a queue for an unfreeze message that contains
// new cost bound for this server.
// Send an unfreeze reply server exchange out.
// Resume manageQ0: up(frozenSync);
    }
  }

  public void run() {
      // artificially slowing TempP down for experimentation
      // try { Thread.sleep(10000); } catch (InterruptedException e) { }
    while(true) {
      // artificially slowing TempP down for experimentation
      // try { Thread.sleep(500); } catch (InterruptedException e) { }
      try {
// Suspend if frozen: down(frozenSync);
// Will be resumed with an unfreeze and up(frozenSync);
        manageQ0();
// Announce OK to freeze manageQ0: up(frozenSync);
      } catch (InterruptedException e) {
        System.out.println(
          "TempP run: interrupted (not supposed to happen)"
        );
        Thread.currentThread().interrupt();
      } // try catch
    } // while (true)
  } // run

  public void manageQ0() throws InterruptedException {
    Transaction trans;
if (TEMPP_CQUEUE_DEBUG_OUTPUT) {
    int howMany = serv_imp.childq0.size();
  if (howMany > 0) {
    System.out.println("TempP manageQ0: howMany=" + howMany);
  }
}
if (TEMPP_CQUEUE_DEBUG_OUTPUT) {
  String cQ = serv_imp.childq0.toString();
  System.out.println("TempP manageQ0: before take, childq0=" + cQ);
}
    trans = (Transaction) serv_imp.childq0.take();
if (TEMPP_CQUEUE_DEBUG_OUTPUT) {
  System.out.println("TempP manageQ0: after take, childq0="
    + serv_imp.childq0 + "\n removed trans=" + trans);
}
    if (trans != null) { // trans == null is not supposed to happen
      if (CBsatisfied(trans)) {
          doTempProcessing(trans);
      } else {
          // blocked by cost
          BlockedByCost(trans);
      }
    } else {
      throw new RuntimeException("TempP manageQ0: null trans after take");
    }
  }// end method Manage queue0

  public void BlockedByCost(Transaction t) throws InterruptedException {
    t = t.newTransState(COST_BOUND_NOT_SATISFIED);
if (TEMPP_BASIC_DEBUG_OUTPUT) {
    System.out.println("TempP BlockedByCost: transactionID=" + t.getID());
}
  }

  public void doTempProcessing(Transaction t) throws InterruptedException {
    /*
     * .do the actual temporary processing and adjust CB etc.
     * .When Finished call the method TempProcessingComplete
     * t.setTransState(3);
     */
    //Temporary processing
if (TEMPP_BASIC_DEBUG_OUTPUT) {
    System.out.println("TempP doTempProcessing: transactionID=" + t.getID());
}
    ImageRecord outputImage = ImageRecord.newDuplicate(OutImage);
    outputImage = outputImage.newTransaction_id(t.getID());
    int[] old_CB = current.getServerCostBound();
    int[] new_CB = null;
    int[] change = t.getTransChange();
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("TempP doTempProcessing: transactionID=" + t.getID()
    + ", CB=" + arrayToString(current.getServerCostBound())
    + ", tempWork=" + arrayToString(current.getTempWork())
    + ", change =" + arrayToString(change));
}
    int code = t.getTransCode();
    if (code==TEMP_INCREASE || code==TEMP_DECREASE || code==TEMP_MIXTURE) {
      // changes should not cause value to exceed max_val or go below zero
      if(current.changeCB(change) != null) {
        outputImage = outputImage.newViolation(true);
        // This should not happen because manageQ0() already called
        // CBsatisfied(t) and got true before we got here.
        throw new RuntimeException(
          "TempP doTempProcessing: transactionID=" + t.getID()
          + ", change cannot be done because it VIOLATES but already checked?"
        );
      } else {
        new_CB = current.getServerCostBound();
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("TempP doTempProcessing: transactionID=" + t.getID()
    + ", change ok, new_CB=" + arrayToString(new_CB)
    + ", tempWork=" + arrayToString(current.getTempWork())
    + ", change =" + arrayToString(change));
}
        outputImage = outputImage.newCB(new_CB);
        outputImage = outputImage.newChange(change);
        OutImage = ImageRecord.newDuplicate(outputImage);
        //save this output image for access by the next child done
        //set the outImage of the transaction to be sent to coordinator
        t = t.newOutImage(outputImage);
if (MINIMAL_PRINT_DEBUG_OUTPUT) {
System.out.println(
    "Temp doTemp id=" + t.getID()
  + ", to=" + t.getInitiatingServer()
  + ", oCB=" + arrayToString(old_CB)
  + ", change=" + arrayToString(change)
  + ", nCB=" + arrayToString(new_CB)
);
}
        //this should be done only if not VIOLATES, right?
        TempProcessingComplete(t);
      }
    } else {
      throw new RuntimeException("TempP doTempProcessing: illegal trans code");
    }//end if code = temp_increase/decrease/mixture
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP doTempProcessing: outputImage= " + outputImage);
}
  }

  //These transactions simply sit around waiting to go into state 5.
  //Nothing else is done here.
  //Called when TempProcessing is complete.
  public void TempProcessingComplete(Transaction t)
        throws InterruptedException {
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP TempProcessingComplete: t= " + t);
}
    Transaction trans;
    Transaction transcopy;
    trans = t.newFromServer(serv_imp.getMyHN());
    trans = trans.newTransState(TEMP_PROCESSING_COMPLETE);
    transcopy = trans.newTransCode(TEMP_COMPLETE);
    serv_imp.ServerExchangeOut(t.getInitiatingServer(), transcopy);
  }

  // Used for removing a transaction from whichever queue it's in
  public Transaction RemoveChild(Transaction t) {
    Transaction result = t;
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP RemoveChild: t= " + t);
}
if (TEMPP_CQUEUE_DEBUG_OUTPUT) {
  System.out.println("TempP RemoveChild: before remove,"
    + " childq0=" + serv_imp.childq0 + "\n removing t=" + t);
}
    if (!serv_imp.childq0.remove(t)) {
      result = null;
      /* Nothing to do here; the transaction is not in queue 0 */
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP RemoveChild: not in queue 0");
}
    } else {
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP RemoveChild: in queue 0");
}
if (TEMPP_CQUEUE_DEBUG_OUTPUT) {
  String cQ = serv_imp.childq0.toString();
  System.out.println("TempP RemoveChild: after remove, transactionID="
    + t.getID() + "\n childq0=" + cQ);
}
    }
    return result;
  }

  public void SelectedForGlobalProcessing(Transaction t)
        throws InterruptedException {
    /*
     This Method is called for transactions which have been selected
     by the permanent processor for global or permanent processing.
     If the transaction is still under going temporary processing it is
     searched for and deleted from whichever child queue it is on.
     If however it is in state 4, Temporary Processing Completed,
     it is removed from queue 4 and placed on queue 5, indicating that it is
     currently undergoing permanent processing.
    */
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP SelectedForGlobalProcessing: t= " + t);
}
    if (RemoveChild(t) == null) {
      //This means the transaction is not in queue 0
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println
    ("TempP SelectedForGlobalProcessing: not on child queue 0");
}
    }
  }

  public void GlobalProcessingComplete(Transaction trans) {
  // SelectedForGlobalProcessing(t) should have already removed t from queue
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP GlobalProcessingComplete: trans= " + trans);
}
////////////////  What about this??? ////////////////////////////
    if (trans.getViolation()) {
      undoRequired(trans);
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP GlobalProcessingComplete: UNDOREQUIRED,"
    + " transactionID=" + trans.getID());
}
    } else {
      OutImage = OutImage.newCB(current.getServerCostBound());
    }
/////////////////////////////////////////////////////////////////
  }

  public boolean CBsatisfied(Transaction t) {
    if (current.sCBwouldViolate(t.getTransChange())) {
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP CBsatisfied: no, transactionID=" + t.getID());
}
      return false;
    } else {
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP CBsatisfied: yes, transactionID=" + t.getID());
}
      return true;
    }
  }

  public void undoRequired(Transaction t) {
if (TEMPP_BASIC_DEBUG_OUTPUT) {
  System.out.println("TempP undoRequired: transactionID=" + t.getID());
}
    OutImage = OutImage.changeCB(t.getTransChange());
  }

  public void compensateCB(Transaction t) {
    // server completed temporary processing, but not first so
    // compensate costbound
    int[] change = t.getTransChange();
    for (int i = 0; i < change.length; i++) { // reverse polarity to undo it
      change[i] = -change[i];
    }
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("TempP compensateCB: transactionID=" + t.getID()
    + ", before sCB=" + arrayToString(current.getServerCostBound())
    + ", tempWork=" + arrayToString(current.getTempWork())
    + "\n reversed polarity change=" + arrayToString(change));
}
    //add back to costbound, polarity already reversed
    int[] old_CB = current.getServerCostBound();
    if(current.compensateWithinBoundsCB(change) == null) {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("TempP compensateCB: change reversed WITHIN BOUNDS"
    + ", transactionID=" + t.getID()
    + ", tempWork=" + arrayToString(current.getTempWork())
    + ", new sCB=" + arrayToString(current.getServerCostBound()));
}
if (MINIMAL_PRINT_DEBUG_OUTPUT) {
System.out.println(
    "Temp compCB id=" + t.getID()
  + ", oCB=" + arrayToString(old_CB)
  + ", change=" + arrayToString(change)
  + ", nCB=" + arrayToString(current.getServerCostBound())
);
}
    } else {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("TempP compensateCB: change NOT completely reversible"
    + " within bounds, transactionID=" + t.getID());
}
if (MINIMAL_PRINT_DEBUG_OUTPUT) {
System.out.println(
    "Temp noCmCB id=" + t.getID()
  + ", oCB=" + arrayToString(old_CB)
  + ", change=" + arrayToString(change)
  + ", nCB=" + arrayToString(current.getServerCostBound())
);
}
    }
  }
}
