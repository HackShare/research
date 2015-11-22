import HADS.XML.DataHADS;

import java.io.*;
import java.util.*;

class RunHADS {
   public static final Runtime runtime = Runtime.getRuntime();
   public static final int SLEEP_TIME = 5000;

   public static String getHome() throws IOException {
      String[] arguments = { "/bin/bash", "-c", "echo $HOME" };
      Process p = runtime.exec(arguments);
      return (new BufferedReader(
         new InputStreamReader(p.getInputStream()))).readLine();
   }

   public static String getPid() throws IOException {
      String[] arguments = { "/bin/bash", "-c", "echo $$" };
      Process p = runtime.exec(arguments);
      return (new BufferedReader(
         new InputStreamReader(p.getInputStream()))).readLine();
     }

   public static String getProcessOutput(Process p) throws IOException {
     BufferedReader br;
     String line;
     StringBuffer output = new StringBuffer();
     br = new BufferedReader(new InputStreamReader(p.getInputStream()));
     output.append("stdout:");
     while ((line = br.readLine()) != null) {
          output.append(line);
          output.append('\n');
     }
     br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
     output.append("stderr:");
     while ((line = br.readLine()) != null) {
          output.append(line);
          output.append('\n');
     }
     return output.toString();
   }

   public static void main(String args[]) throws IOException {
     final String HOME = getHome();
     final String CLASSPATH = HOME + "/DockerImages/HADS-Docker-Minimal/lib/HADS-Maven/target/:" + HOME + "/DockerImages/HADS-Docker-Minimal/lib/HADS-Maven/target/HADS-Maven-1.0-SNAPSHOT.jar";
     final String DOCKER_CLASSPATH = "./root/HADS-Maven:./root/HADS-Maven/HADS-Maven-1.0-SNAPSHOT.jar";
     final String PID = getPid();
     final String SHELL = "/bin/bash";
     final String SHELL_FLAGS = "-c";
     final String SRC_DIR = "DockerImages/HADS-Docker-Minimal/lib/HADS-Maven/src/main/java";
     // Create a new instance of the Hads XML Parser that takes in
     // the XML file to be parsed
     final String XML_FILE_NAME = args[0];
     DataHADS dataHADS = new DataHADS(XML_FILE_NAME);

     final String GENERATOR = dataHADS.getGENERATOR();
     final String BATCH_PREFIX = dataHADS.getBATCH_PREFIX();
     final String OUTPUT_PREFIX = dataHADS.getOUTPUT_PREFIX();
     final String GENOUT_PREFIX = dataHADS.getGENOUT_PREFIX();
     final String RESULTS_PREFIX = dataHADS.getRESULTS_PREFIX();
     final String MERGED_PREFIX = dataHADS.getMERGED_PREFIX();
     final String STATS_PREFIX = dataHADS.getSTATS_PREFIX();

     String[] arguments = new String[8];
     arguments[0] = "xterm";
     arguments[1] = "-T";
     arguments[3] = "-hold";
     arguments[4] = "-e";
     arguments[5] = SHELL;  arguments[6] = SHELL_FLAGS;
     Process p;

     System.out.println(
            "HOME=" + HOME
        + ", CLASSPATH=" + CLASSPATH
        + ", PID=" + PID
     );

     System.out.println(dataHADS);

     List<String> servers = dataHADS.getServers();

     for (int i = 0; i < servers.size(); i++) {
          String currentServer = (String)servers.get(i);
	  arguments[2] = "Output-"+currentServer;
          arguments[7] = "docker exec -it HADS-Docker-Minimal-Server"+i
	    + " java"
	    + " HADS.Server.ServerImpl docker.xml"
            + " " + PID + " " + currentServer + " " + DOCKER_CLASSPATH + " " + SHELL + " " 
	    + SHELL_FLAGS + " " + OUTPUT_PREFIX;

          // All but XML file name, PID, and currentServer are in the XML
          // file and read by the generator.
          /*arguments[2] = "ssh "
             + currentServer + " 'cd ~/temp; CLASSPATH=" + CLASSPATH
             + " java HADS.Server.ServerImpl ../HADS/" + XML_FILE_NAME
             + " " + PID + " " + currentServer + " "
             + " </dev/null 1>" + OUTPUT_PREFIX + PID + "-" + currentServer
             + " 2>&1 &'; echo 'past ssh to server'";*/

          System.out.println(arguments[7]);
          p = runtime.exec(arguments);
          //System.out.println(getProcessOutput(p));
          System.out.println("DONE: " + arguments[7]);
          try { Thread.currentThread().sleep(SLEEP_TIME); }
             catch (InterruptedException e) { /* ignore */ }
     }
     arguments[0] = SHELL;  
     arguments[1] = SHELL_FLAGS;
     // cd $HOME/temp      for all commands below
	System.out.println("===STARTING GENERATOR===");
     // All but XML file name and PID are in the XML file and
     // read by the generator.
	arguments[2] = "docker exec HADS-Docker-Minimal-Server0"
        + " java HADS.Generator.TransGenerator "
        + "docker.xml" + " " + PID + " ";


	/*arguments[2] = "CLASSPATH=" + CLASSPATH
        + " java HADS.Generator.TransGenerator " + HOME + "/HADS/"
        + XML_FILE_NAME + " " + PID + " ";
	
     arguments[2] = "cd ~/temp; CLASSPATH=" + CLASSPATH
        + " java HADS.Generator.TransGenerator ../HADS/"
        + XML_FILE_NAME + " " + PID + " ";*/

     arguments[2] += " 1>" + GENOUT_PREFIX + PID + "-" + GENERATOR + " 2>&1";

      System.out.println(arguments[2]);
      p = runtime.exec(arguments);
      System.out.println(getProcessOutput(p));
      System.out.println("DONE: " + arguments[2]);

      arguments[2] = "cd " + HOME + "/temp; "
        + "sort -n +0 -1 <" + MERGED_PREFIX + PID + "-" + GENERATOR
        +              " >" + MERGED_PREFIX + PID + "-" + GENERATOR + ".sorted";
      System.out.println(arguments[2]);
      p = runtime.exec(arguments);
      System.out.println(getProcessOutput(p));
      System.out.println("DONE: " + arguments[2]);

     /*arguments[2] = "cd " + HOME + "/temp; "
        + "enscript -MLetter -r -p" + BATCH_PREFIX + PID + "-" + GENERATOR
        + ".ps -fCourier9 "         + BATCH_PREFIX + PID + "-" + GENERATOR;
      System.out.println(arguments[2]);
      p = runtime.exec(arguments);
      System.out.println(getProcessOutput(p));
      System.out.println("DONE: " + arguments[2]);*/

     /*arguments[2] = "cd " + HOME + "/temp; "
        + "enscript -MLetter -r -p" + MERGED_PREFIX + PID + "-" + GENERATOR
        + ".ps -fCourier9 " + MERGED_PREFIX + PID + "-" + GENERATOR + ".sorted";
      System.out.println(arguments[2]);
      p = runtime.exec(arguments);
      System.out.println(getProcessOutput(p));
      System.out.println("DONE: " + arguments[2]);*/
	System.out.println("===STARTING STATISTICS===");
      arguments[2] = "cd " + HOME + "/temp; "
        + "CLASSPATH=" + CLASSPATH
        + " java HADS.Generator.Statistics ../HADS/" + XML_FILE_NAME
        + " " + PID + " "
        + MERGED_PREFIX + PID + "-" + GENERATOR + ".sorted"
        + " >" + STATS_PREFIX + PID + "-" + GENERATOR;

      System.out.println(arguments[2]);
      p = runtime.exec(arguments);
      System.out.println(getProcessOutput(p));
      System.out.println("DONE: " + arguments[2]);

     arguments[2] = "cd " + HOME + "/temp; "
        + "cat " + STATS_PREFIX + PID + "-" + GENERATOR;
      System.out.println(arguments[2]);
      p = runtime.exec(arguments);
      System.out.println(getProcessOutput(p));
      System.out.println("DONE: " + arguments[2]);

     /*arguments[2] = "cd " + HOME + "/temp; "
        + "enscript -MLetter -p" + STATS_PREFIX + PID + "-" + GENERATOR
        + ".ps -fCourier10 " + STATS_PREFIX + PID + "-" + GENERATOR;
      System.out.println(arguments[2]);
      p = runtime.exec(arguments);
      System.out.println(getProcessOutput(p));
      System.out.println("DONE: " + arguments[2]);*/

      /*for (int i = 0; i < servers.size(); i++) {
          String currentServer =(String)servers.get(i);
          // If the generator is also a server, then the following
          // remote copy is not needed and in fact removes the
          // generator's results files!
          if (!currentServer.equals(GENERATOR)) {
             arguments[2] = "ssh " + currentServer
                + " 'scp ~/temp/*" + PID + "* "
                + GENERATOR + ":temp; " + "rm -f ~/temp/*" + PID
                + "*' </dev/null 2>/dev/null 1>/dev/null";
             System.out.println(arguments[2]);
             p = runtime.exec(arguments);
             System.out.println(getProcessOutput(p));
             System.out.println("DONE: " + arguments[2]);
          }
     }*/
	System.out.println("===CREATING RESULTS===");
     arguments[2] = "mkdir " + HOME  + SRC_DIR + "/HADS/Results/" + PID
        + "; cd " + HOME  + SRC_DIR + "/HADS/Results/" + PID
        + "; mv " + HOME + "/temp/*" + PID + "* .";
      System.out.println(arguments[2]);
      p = runtime.exec(arguments);
      System.out.println(getProcessOutput(p));
      System.out.println("DONE: " + arguments[2]);

     /*arguments[2] = "cd " + HOME  + "/src/HADS/Results/" + PID + ";"
        + " enscript -MLetter -p" + GENOUT_PREFIX + PID + "-" + GENERATOR
        + ".ps -fCourier7 "       + GENOUT_PREFIX + PID + "-" + GENERATOR;
      System.out.println(arguments[2]);
      p = runtime.exec(arguments);
      System.out.println(getProcessOutput(p));
      System.out.println("DONE: " + arguments[2]);*/

      /*for (int i = 0; i < servers.size(); i++) {
          String currentServer = (String)servers.get(i);
          arguments[2] = "cd " + HOME  + "/src/HADS/Results/" + PID + ";"
             + " enscript -MLetter -p" + OUTPUT_PREFIX + PID + "-" + currentServer
             + ".ps -fCourier7 "       + OUTPUT_PREFIX + PID + "-" + currentServer;
          System.out.println(arguments[2]);
          p = runtime.exec(arguments);
          System.out.println(getProcessOutput(p));
          System.out.println("DONE: " + arguments[2]);
     }*/
     System.out.println("DONE");
   }
}
