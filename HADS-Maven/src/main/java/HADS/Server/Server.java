package HADS.Server;
import HADS.Generator.TransactionRequest;

import java.rmi.*;

public interface Server extends Remote {
  public void ServerExchangeIn(
      String FromHostName, int FromHostNameIndex, Transaction trans
                              ) throws RemoteException;
  public void RequestProcessor(TransactionRequest tr)
    throws RemoteException;
  public ResultsQueue SendResults()
    throws RemoteException;
  public void writeResultsFile()
    throws RemoteException;
  public void seeIfDone(int lastTransactionNum)
    throws RemoteException;
  public void terminate()
    throws RemoteException;
}
