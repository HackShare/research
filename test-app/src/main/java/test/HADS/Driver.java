import HADS.Server.StorageConnector;

import java.io.*;
import java.util.*;

public class Driver 
{
   public static void main(String args[]) throws IOException 
   {
	System.out.println("Starting driver...");
	StorageConnector sc = StorageConnector.getStorage();

	Integer k = 0;
	String v = "Hello World!";
	System.out.println("Driver: " + sc.put(k,v));

	System.out.println("Driver: " + sc.get(k));

	System.out.println("DONE");
   }
}
