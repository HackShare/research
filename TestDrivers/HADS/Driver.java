import HADS.Server.*;

import java.io.*;
import java.util.*;

class Driver 
{
   public static void main(String args[]) throws IOException 
   {
	System.out.println("Starting driver...");
	StorageConnector sc = new HazelcastStorage();

	Integer k = 0;
	String v = "Hello World!";
	System.out.println("Driver: " + sc.put(k,v));

	System.out.println("Driver: " + sc.get(k));

	System.out.println("DONE");
   }
}
