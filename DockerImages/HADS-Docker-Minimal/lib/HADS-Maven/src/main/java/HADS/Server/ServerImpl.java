package HADS.Server;

import static HADS.Generator.Statics.*;
import static HADS.Server.Constants.*;

import HADS.XML.DataHADS;
import HADS.Generator.TransactionRequest;
import HADS.Server.StorageConnector;

import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.net.*;
import java.util.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerImpl extends UnicastRemoteObject
    implements Server, Serializable {

  private int pid;
  private StorageConnector sc;
  private Parameters parms;
  private CurrentResources current;
  private int numResourceTypes;
  public final ParentTransQueue parentq;
  public final BlockingQueue<Transaction> childq0;
  public final List<String> ServerTable, FullServerTable;
  private int numServers;
  public final ResultsQueue resultsList;
  private String MyHostName;
  private int MyHostNameIndex;
  private Server ServerObj;
  public final ImageList Images;
  private String resultsFileName;
  private int numTempCompletes = 0;
  private int numTempCompletesMe = 0;
  private int numTempCompletesFirst = 0;
  private int numTempCompletesMeFirst = 0;
  private int numTempAnnounces = 0;
  private int numTempCompensates = 0;

  /* *****************Server Threads **************************************/

  public final PermanentProcessor perm_proc;
  public final TemporaryProcessor temp_proc;

  /* ***************** The constructor **********************************/

  public ServerImpl(
      int pid, int[] initialCB, int[] upperCB, int[] pctCB, String filename,
       String hostname, int hostnameIndex, int numServers, List<String> servers
            ) throws RemoteException {
    this.pid = pid;
    this.numServers = numServers;
    numTempCompletes = 0;
    numTempCompletesMe = 0;
    numTempCompletesFirst = 0;
    numTempCompletesMeFirst = 0;
    numTempAnnounces = 0;
    numTempCompensates = 0;

    resultsFileName = filename;
    MyHostName=hostname;
    MyHostNameIndex=hostnameIndex;
    if (!MyHostName.equals(servers.get(hostnameIndex))) {
      throw new IllegalArgumentException(
        "MyHostName=" + MyHostName
        + "!= servers.get(" + hostnameIndex + ")=" 
        + servers.get(hostnameIndex)
      );
    }
    ArrayList<String> tempServerTable=new ArrayList<String>();
    //Add all host names except local
    // Moved from just after constructor called in main method
    for(int i=0; i<servers.size(); i++) {
      if (!MyHostName.equals(servers.get(i))) {
        tempServerTable.add(servers.get(i));
      }
    }
    // FullServerTable has all servers including this one, MyHostName.
    // ServerTable has all servers except this one, MyHostName.
    this.FullServerTable = Collections.unmodifiableList(servers);
    this.ServerTable = Collections.unmodifiableList(tempServerTable);
    this.numServers = this.ServerTable.size() + 1;
    System.out.println("ServI in constructor: number of servers = "
       + this.numServers);

    parms = new Parameters(initialCB, upperCB, pctCB, numServers);
    current = new CurrentResources(parms);
    this.numResourceTypes = parms.getNumResourceTypes();
    System.out.println("ServI parameters = " + parms);
    System.out.println("ServI current = " + current);

    System.out.println("ServI  values = " + arrayToString(current.getValue())
       + "\n max_val = " + arrayToString(current.getMaxValue())
       + "\n pctCB=CBpercent = " + arrayToString(parms.getPercentCostBound())
       + "\n initialCB = " + arrayToString(parms.getInitialCostBound())
       + "\n upperCB = " + arrayToString(parms.getUpperCostBound())
       + "\n initCB = " + arrayToString(parms.getServerInitialCostBound())
       + "\n CostBound = " + arrayToString(current.getServerCostBound())
       + "\n thisNodeAlloc = " + arrayToString(current.getServerWork())
    );

    //sc = StorageConnector.getStorage();

    Images=new ImageList(200);

    parentq=new ParentTransQueue();

    childq0=new LinkedBlockingQueue<Transaction>();
    perm_proc=new PermanentProcessor(this, current, pid);
    temp_proc=new TemporaryProcessor(this, current);
    perm_proc.start();
    temp_proc.start();
    resultsList=new ResultsQueue(200);
	System.out.println(perm_proc.toString());
  } //end constructor

  public ResultsQueue SendResults() { return resultsList; }

  public void writeResultsFile() {
    System.out.println("ServI writeResultsFile: current = " + current);
    try {
      PrintWriter resultsFile = new PrintWriter(
                    new BufferedWriter(
                      new FileWriter(
                        new File(resultsFileName)
                      )
                    )
                  );
      for(int i=0;i<resultsList.size();++i) {
        ResultRecord rec = resultsList.get(i);
        System.out.println("ServI rec = " + rec);
        resultsFile.println(rec);
      }

      for(int i = 0; i < numResourceTypes; i++)
      {
      		System.out.println("TYPE: " + i + " END CB: " + sc.get(i));
      }

      resultsFile.close();
    
    } catch(IOException io) {
     System.out.println("ServI Error accessing Results file" + io);
    }
  }

  public void seeIfDone(int lastTransactionNum) {
     int counter = 0;
     System.out.println("ServI Waiting for transaction number "
        + lastTransactionNum + " to complete, current is "
        + perm_proc.getTransactionExpected());
     // Added a counter so this loop will eventually end.
     while (perm_proc.getTransactionExpected() < lastTransactionNum
        && counter < SERVI_COUNTER_MAX) {
          counter++;
          try { Thread.sleep(SERVI_SLEEPER); }
          catch (Exception e) { }
     }
     System.out.println("ServI Transaction number "
        + lastTransactionNum + " complete" + ", counter=" + counter
        + ", current is " + perm_proc.getTransactionExpected());
     System.out.println("ServerI number of times Perm Proc did work = "
        + perm_proc.getNumTimesDidWork());
     System.out.println("ServerI number of violations Perm Proc detected = "
        + perm_proc.getNumViolations());
     System.out.println("ServerI number of completes = "
        + numTempCompletes);
     System.out.println("ServerI number of completes me = "
        + numTempCompletesMe);
     System.out.println("ServerI number of completes first = "
        + numTempCompletesFirst);
     System.out.println("ServerI number of announces = "
        + numTempAnnounces);
     System.out.println("ServerI number of compensates = "
        + numTempCompensates);
     System.out.println("ServerI size childq0 = " + childq0.size());
     System.out.println("ServI Waiting 10 more seconds...");
     try { Thread.sleep(SERVI_SLEEPER); } catch (Exception e) { }
     System.out.println("ServI ...waited 10 more seconds");
  }

  public void terminate() {
  // Start a thread that will exit the VM in 10 seconds.
    System.out.println("ServI This server exiting in 10 seconds.");
    new Thread(
      new Runnable() {
        public void run() {
          try { Thread.sleep(SERVI_SLEEPER); }
          catch (Exception e) { }
          System.out.println("ServI This server exiting now.");
    new File("/tmp/commit" + pid + "-" + MyHostName + ".log").delete();
    new File("/tmp/undo"   + pid + "-" + MyHostName + ".log").delete();
    new File("/tmp/redo"   + pid + "-" + MyHostName + ".log").delete();
          System.exit(0);
        }
      }
    ).start();
  }

  public String getMyHN() { return MyHostName; }
  public StorageConnector getMySC() { return sc; }

  public void ServerExchangeOut(String toHostName, Transaction t) {
    String ServerObjName;
    ServerObjName = "//" + toHostName + "/" + toHostName + "S" + pid;
    try {
      ServerObj=(Server)Naming.lookup(ServerObjName);
      t = t.newFromServer(MyHostName);
if (SERVI_BASIC_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeOut to " + toHostName
    + ", transactionID=" + t.getID());
}
      ServerObj.ServerExchangeIn(MyHostName, MyHostNameIndex, t);
    } catch(Exception e) {
      System.out.println("ServI Exception occured: " + e);
      e.printStackTrace();
    }
  }

  // Remotely Accesibe method. Other Servers can call this method
  public void ServerExchangeIn(
      String FromHostName, int FromHostNameIndex, Transaction trans) {
    //Server to Server Child Transaction Arrivals (3001-4000)
if (SERVI_BASIC_DEBUG_OUTPUT) {
    System.out.println("ServI ServerExchangeIn from " + FromHostName
      + " [" + FromHostNameIndex + "], trans=" + trans);
}
    if (trans.getTransCode() == TEMP_INCREASE ||                     // 3001;
        trans.getTransCode() == TEMP_DECREASE ||                     // 3002;
        trans.getTransCode() == TEMP_MIXTURE ||
        trans.getTransCode() == TEMP_COMPLETE ||                     // 3003;
        trans.getTransCode() == TEMP_ANNOUNCE ||
        trans.getTransCode() == TEMP_COMPENSATE ||
        trans.getTransCode() == GLOBAL_COMPLETE ||                   // 3004;
        trans.getTransCode() == SELECTED_FOR_GLOBAL) {               // 3005;

      if (trans.getTransCode() == TEMP_DECREASE
          || trans.getTransCode() == TEMP_MIXTURE
          || trans.getTransCode() == TEMP_INCREASE) {
        trans = trans.newTransState(NEW_ARRIVAL);
if (SERVI_CQUEUE_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn: before put, transactionID="
    + trans.getID() + "\n childq0=" + childq0);
}
        try {
          childq0.put(trans);
        } catch (InterruptedException e) {
          System.out.println(
            "ServI ServerExchangeIn: interrupted (not supposed to happen)"
          );
          Thread.currentThread().interrupt();
        }

if (SERVI_CQUEUE_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn: after put, transactionID="
    + trans.getID() + "\n childq0=" + childq0);
}
      } else if (trans.getTransCode()==TEMP_ANNOUNCE) {
        numTempAnnounces++;
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn: TEMP_ANNOUNCE received"
    + " transactionID=" + trans.getID()
    + " from=" + FromHostName
    + " parent=" + trans.getInitiatingServer()
  );
}
        if (temp_proc.RemoveChild(trans) == null) {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn TEMP_ANNOUNCE:"
    + " transactionID=" + trans.getID() + " could NOT remove my child");
}
        } else {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn TEMP_ANNOUNCE:"
    + " transactionID=" + trans.getID() + " my child REMOVED");
}
        }
      } else if (trans.getTransCode()==TEMP_COMPENSATE) {
        numTempCompensates++;
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn TEMP_COMPENSATE received"
    + " transactionID=" + trans.getID()
    + " from=" + FromHostName
    + " parent=" + trans.getInitiatingServer()
  );
}
        temp_proc.compensateCB(trans);
      } else if (trans.getTransCode()==TEMP_COMPLETE) {
        numTempCompletes++;
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn: TEMP_COMPLETE received"
    + " transactionID=" + trans.getID()
    + " from=" + FromHostName
    + " parent=" + trans.getInitiatingServer()
  );
}
        if(trans.getInitiatingServer().equals(MyHostName)) {
          // Then this server is the coordinator
          numTempCompletesMe++;
if (SERVI_BASIC_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: TEMP_COMPLETE received by initiating server"
    + " who is me (" + MyHostName + ")"
  );
}
          ImageRecord transImage = trans.getOutImage();
          ImageRecord new_image = ImageRecord.newOne(
            transImage.getCB(),                         // cost bound
            transImage.getChange(),                     // change
            transImage.getTransaction_id(),             // transaction ID
            FromHostName,                               // who did temp
            FromHostNameIndex,                          // who did temp index
            System.currentTimeMillis(),                 // temp time completed
            transImage.getViolation()                   // violation
          );
          if (transImage.getViolation()) {
            throw new RuntimeException(
              "ServI TEMP_COMPLETE: transImage.getViolation() is true"
            );
          }
          if (trans.getID() != transImage.getTransaction_id()) {
            throw new RuntimeException(
             "ServI TEMP_COMPLETE: trans.getID != transImage.getTransaction_id"
            );
          }
          trans = trans.newOutImage(new_image);
          trans = trans.newWhoDidTemp(FromHostName, FromHostNameIndex);

          if(Images.addIfNotThere(new_image) == -1) {
            numTempCompletesFirst++;
if (ALWAYS_PRINT_DEBUG_OUTPUT) {
System.out.println("ServI TEMP_COMPLETE: age=" + age()
+ " transactionID=" + trans.getID()
+ " server=" + FromHostName + "=" + trans.getFromServer()
+ " did TEMP first, parent=" + trans.getInitiatingServer()
+ "\n transaction= " + trans
+ "\n current= " + current
+ "\n new_image=" + new_image);
}
if (MINIMAL_PRINT_DEBUG_OUTPUT) {
System.out.println(
    "Serv imgRec id=" + new_image.getTransaction_id()
  + ", wdT=" + new_image.getWhoDidTemp()
  + ", CB=" + arrayToString(new_image.getCB())
  + ", change=" + arrayToString(new_image.getChange())
);
}
            //coordinator telling all childprocessing re the trans to stop
            AnnounceTempComplete(trans);
          } else {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn: TEMP_COMPLETE received"
    + " transactionID=" + trans.getID()
    + " from=" + FromHostName
    + " was not first, parent=" + trans.getInitiatingServer()
  );
}
            // send CB compensate message
if (MINIMAL_PRINT_DEBUG_OUTPUT) {
System.out.println(
    "Serv sndCmp id=" + trans.getID()
  + ", to=" + FromHostName
  + ", change=" + arrayToString(trans.getTransChange())
);
}
            trans = trans.newTransCode(TEMP_COMPENSATE);
            ServerExchangeOut(FromHostName, trans);
          }
          if(!trans.getFromServer().equals(MyHostName)) {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: TEMP_COMPLETE received by initiating server"
    + " from " + trans.getFromServer() + " who is NOT me ("
    + MyHostName + ")"
    + " transactionID=" + trans.getID()
  );
}
          //find the transaction in whichever child queue here and remove it
            if (temp_proc.RemoveChild(trans) == null) {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn TEMP_COMPLETE:"
    + " transactionID=" + trans.getID() + " could NOT remove my child");
}
            } else {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn TEMP_COMPLETE:"
    + " transactionID=" + trans.getID() + " my child REMOVED");
}
            }
          } else {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: TEMP_COMPLETE received by initiating server"
    + " from " + trans.getFromServer() + " who is me ("
    + MyHostName + ")"
    + " transactionID=" + trans.getID()
  );
}
          }
        } else {
          // you are not coordinator, so some other coordinating server
          // is telling you to stop
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: TEMP_COMPLETE Received by "
    + MyHostName + ", who is NOT"
    + " the initiating server (" + trans.getInitiatingServer() + ")"
    + " transactionID=" + trans.getID()
  );
}
          if (temp_proc.RemoveChild(trans) == null) {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn TEMP_COMPLETE:"
    + " transactionID=" + trans.getID() + " could NOT remove my child");
}
          } else {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI ServerExchangeIn TEMP_COMPLETE:"
    + " transactionID=" + trans.getID() + " my child REMOVED");
}
          }
        }
      }// end if code==TEMP_COMPLETE
      else if (trans.getTransCode() == GLOBAL_COMPLETE) {
if (SERVI_BASIC_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: GLOBAL_COMPLETE received,"
    + " transactionID=" + trans.getID()
  );
}
          current.adjustPtempWork(
            trans.getWhoDidTempIndex(), trans.getTransChange()
          );
          temp_proc.GlobalProcessingComplete(trans);
          // Call CurrentResources method to update ptempWork[][]
          // now that trans has whoDidTemp inside it.
      } else if(trans.getTransCode()==SELECTED_FOR_GLOBAL){
if (SERVI_BASIC_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: SELECTED_FOR_GLOBAL received,"
    + " transactionID=" + trans.getID()
  );
}
        try {
        temp_proc.SelectedForGlobalProcessing(trans);
        } catch (InterruptedException e) {
          System.out.println(
            "ServI ServerExchangeIn: interrupted (not supposed to happen)"
          );
          Thread.currentThread().interrupt();
        }
      }
    }//end if code is between 3000 and 4000
    else if (trans.getTransCode() == CANCOMMIT ||                //4001;
             trans.getTransCode() == YES ||                      //4002;
             trans.getTransCode() == NO ||                       //4003;
             trans.getTransCode() == ABORT ||                    //4004;
             trans.getTransCode() == COMMIT ||                   //4005;
             trans.getTransCode() == COMMITDONE) {               //4006;

      switch(trans.getTransCode()) {
        case CANCOMMIT:
if (SERVI_BASIC_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: CANCOMMIT received,"
    + " transactionID=" + trans.getID()
  );
}
          perm_proc.processCanCommit(trans);
          break;
        case YES: case NO:
if (SERVI_BASIC_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: YES/NO received,"
    + " transactionID=" + trans.getID()
  );
}
          perm_proc.ReceiveVote(trans);
          break;
        case ABORT:
if (SERVI_BASIC_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: ABORT received,"
    + " transactionID=" + trans.getID()
  );
}
          perm_proc.AbortTrans(trans);
          break;
        case COMMIT:
if (SERVI_BASIC_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: COMMIT received,"
    + " transactionID=" + trans.getID()
  );
}
          perm_proc.DoCommit(trans);
          break;
        case COMMITDONE:
if (SERVI_BASIC_DEBUG_OUTPUT) {
  System.out.println(
    "ServI ServerExchangeIn: COMMITDONE received,"
    + " transactionID=" + trans.getID()
  );
}
          perm_proc.commitCompleted(trans);
          break;
        default:
          throw new RuntimeException
  ("ServI ServerExchangeIn: Invalid Transaction Code for Permanent Processing,"
    + " transactionID=" + trans.getID());
      }//end switch
    }//end if
    else {
      throw new RuntimeException(
        "ServI ServerExchangeIn: unknown transaction code received,"
        + " transactionID=" + trans.getID()
      );
    }
  }//end method

  //Method called by clients requesting a Server to process its request
  public void RequestProcessor(TransactionRequest tr) {
    int[] change = tr.getChangeBy();
    int id = tr.getWhichOne();
    int code;
    if (tr.isDonation()) {
      code = DONATION;
    } else if (tr.isIncrease()) {
      code = INCREASE;
    } else if (tr.isDecrease()) {
      code = DECREASE;
    } else {
      code = MIXTURE;
    }
    ResultRecord new_rec;
    int tc=0;
    try {
      long timestamp = System.currentTimeMillis();
      Transaction trans = new Transaction(id, code, MyHostName, tr, timestamp);
	if (SERVI_BASIC_DEBUG_OUTPUT) {
	  System.out.println("ServI RequestProcessor: tr=" + tr
	    + "\n and trans=" + trans);
	}
      	//add transaction to the parent queue;
	if (SERVI_PQUEUE_DEBUG_OUTPUT) {
	  System.out.println("ServI RequestProcessor: before add, transactionID="
	    + trans.getID() + "\n parentq=" + parentq);
	}

      	parentq.addTransaction(trans);

	if (SERVI_PQUEUE_DEBUG_OUTPUT) {
	  System.out.println("ServI RequestProcessor: after add, transactionID="
	    + trans.getID() + "\n parentq=" + parentq);
	}
      	new_rec=new ResultRecord(trans);
      	resultsList.add(new_rec);
      	//set the transaction code for temporary processing
      	if (code == DONATION)       {  tc = TEMP_DONATION; }
      	else if (code == INCREASE)  {  tc = TEMP_INCREASE; }
      	else if (code == DECREASE)  {  tc = TEMP_DECREASE; }
      	else if (code == MIXTURE)   {  tc = TEMP_MIXTURE;  }
      	Transaction t = trans.newTransCode(tc);
      	if (!t.isDonation()) {
	if (SERVI_CQUEUE_DEBUG_OUTPUT) {
	  System.out.println("ServI RequestProcessor: before put, childq0=" + childq0
	    + "\n adding t=" + t);
	}
	// If a donation, do not put in childq0
        childq0.put(t);
	if (SERVI_CQUEUE_DEBUG_OUTPUT) {
	  System.out.println("ServI RequestProcessor: after put, transactionID="
	    + t.getID() + "\n childq0=" + childq0);
	}
	// If a donation, do not send to other servers
      for (int i = 0; ServerTable.size() > i; i++)  // all but this server
        ServerExchangeOut( ServerTable.get(i), t);
      } // end if (!donation)
    } catch(Exception e) { System.out.println("ServI " + e); }
  }//end method RequestProcessor(...)

  public void AnnounceTempComplete(Transaction trans) {
    trans = trans.newTransCode(TEMP_ANNOUNCE);
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI AnnounceTempComplete: trans=" + trans);
}
    for(int i=0;i<ServerTable.size();i++) {  // all but this server, InitServer
      //don't send to server that completed TempProcessing
      if (getCohort(i).equals(trans.getFromServer())) {
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI AnnounceTempComplete: transactionID="
    + trans.getID() + ", skipping " + getCohort(i));
}
        continue;
      }
if (TEMPP_COMPN_DEBUG_OUTPUT) {
  System.out.println("ServI AnnounceTempComplete: transactionID="
    + trans.getID() + ", sent to " + ServerTable.get(i));
}
       ServerExchangeOut(ServerTable.get(i),trans);
    }
  }

  public String getCohort(int index) {
    return ServerTable.get(index);
  }

  public static void main(String args[]) throws Exception {
    String ServerObjName, resultsFileName, myName;
    int pid, myNameIndex;
    try {

      // Create a new instance of the Hads XML Parser that takes in
      // the XML file to be parsed
      final String XML_FILE_NAME = args[0];
      pid = Integer.parseInt(args[1]);
      myName = args[2];
      DataHADS dataHADS = new DataHADS(XML_FILE_NAME);

      final int[] initialCB = dataHADS.getInitialCostBound();
      final int[] upperCB = dataHADS.getUpperCostBound();
      final int[] pctCB = dataHADS.getPercentCostBound();

      resultsFileName = dataHADS.getRESULTS_PREFIX()+pid+"-"+myName;
      int numServers = dataHADS.getNumServers();

      List<String> servers = dataHADS.getServers();
      myNameIndex = -1;
      for (int i = 0; i < numServers; i++) {
        if (myName.equals(servers.get(i))) {
          myNameIndex = i;
          break;
        }
      }
      if (myNameIndex == -1) {
        throw new RuntimeException(
          "cannot find myName=" + myName + " in server list"
        );
      }

      // first two args were initial cost bound and cost bound percent
      // third was local host name
      // rest were names of other servers
      System.out.println(
        "ServI Usage: java ServerImpl file.xml pid localHostName [was... "
        + "initialCB pctCB resultsFile server1 server2 ...]"
      );
      System.out.println("ServI in main: numServers = " + numServers);
      ServerImpl server_implementation =
         new ServerImpl(pid, initialCB, upperCB, pctCB, resultsFileName,
         myName, myNameIndex, numServers, servers);

      /*ServerObjName = "//" + server_implementation.getMyHN() + "/"
         + server_implementation.getMyHN() + "S" + pid;*/
      ServerObjName = "//" + server_implementation.getMyHN() + "/"
         + server_implementation.getMyHN() + "S" + pid;
      System.out.println("ServI: Binding to - " + ServerObjName);

      Naming.rebind(ServerObjName, server_implementation);
      System.out.println("ServI: Name Binding Successful!");
    } catch (Exception e) {
      System.out.println("ServI Exception occurred: " + e);
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("ServI Server is up and running");
  }
}
