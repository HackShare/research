package HADS.Server;

import java.util.*;
import java.io.*;

import java.util.concurrent.PriorityBlockingQueue;

/*queue for holding transactions*/
public class ParentTransQueue {
  private PriorityBlockingQueue<Transaction> theQ = null;
  private int transactionIdExpected = 0;
  private boolean locked = false;
  private boolean coordinating = false;

  public ParentTransQueue() {
    this.theQ = new PriorityBlockingQueue<Transaction>(200);
    this.transactionIdExpected = 0;
    this.locked = false;
    this.coordinating = false;
  }

  public synchronized int getTransactionIdExpected() {
    return this.transactionIdExpected;
  }

  public synchronized void incTransactionIdExpected() {
    this.transactionIdExpected++;
    notifyAll();
  }

  public synchronized boolean isLocked() {
    return this.locked;
  }

  private synchronized void lockIfNotCoordinating() {  // ended up not using
    try {
      while (this.locked || this.coordinating) {
        wait();
      }
    } catch (InterruptedException e) {
      System.out.println("ParentTransQueue lockIfNotCoordinating:"
        + " interrupted from wait (not supposed to happen)"
      );
      Thread.currentThread().interrupt();
    }
    this.locked = true;
  }

  public synchronized boolean tryLockIfNotCoordinating() {
    if (this.locked || this.coordinating) {
      return false;
    }
    this.locked = true;
    return true;
  }

  public synchronized void unlock() {
    this.locked = false;
    notifyAll();
  }

  public synchronized boolean isCoordinating() {
    return this.coordinating;
  }

  public synchronized void unCoordinating() {
    this.coordinating = false;
    notifyAll();
  }

/*
  Adds Transactions in order of timestamp: The transactions are ordered from
                                ^^^^^^^^^ changed from timestamp to ID
  the least at the botton of the queue to the greatest at the top. i.e. the
  newest transactions are at the top. Therefore we would process from the
  bottom first
*/

  public synchronized void addTransaction(Transaction t) {
    if (t == null) {
      throw new IllegalArgumentException(
        "ParentTransQueue addTransaction: null argument passed"
      );
    }
    theQ.put(t);
    notifyAll();
  }

  /*
    This method removes and returns the top most transaction, that is, the
    oldest transaction on the queue, if its ID matches transactionIdExpected.
  */
  public synchronized Transaction waitForTopMatched() {
    try {
      while (theQ.size() == 0
          || theQ.peek().getID() != this.transactionIdExpected
          || locked || coordinating) {
        wait();
      }
    } catch (InterruptedException e) {
      System.out.println("ParentTransQueue waitForTopMatched:"
        + " interrupted from wait (not supposed to happen)"
      );
      Thread.currentThread().interrupt();
      return null;
    }
    try {
      Transaction result = theQ.take();
      this.coordinating = true;
      // you would think to set locked too but it is not in the original code
//    this.locked = true;
      return result;
    } catch (InterruptedException e) {
      System.out.println("ParentTransQueue waitForTopMatched:"
        + " interrupted from take (not supposed to happen)"
      );
      Thread.currentThread().interrupt();
      return null;
    }
  }

  public synchronized String toString() {
    if (theQ.size() == 0) {
      return "TransQueue: empty";
    }
    Object[] a = theQ.toArray();
    Arrays.sort(a);
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < a.length; i++) {
      sb.append(" " + a[i].toString());
    }
    return "TransQueue:" + sb.toString();
  }
}
