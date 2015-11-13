package HADS.Server;

public class Constants {

  private Constants() { }         // no objects to be created from this class

  public static final boolean ALWAYS_PRINT_DEBUG_OUTPUT = false;
  public static final boolean MINIMAL_PRINT_DEBUG_OUTPUT = true;

  // Server Impl
  public static final boolean SERVI_BASIC_DEBUG_OUTPUT = false;
  public static final boolean SERVI_PQUEUE_DEBUG_OUTPUT = false;
  public static final boolean SERVI_CQUEUE_DEBUG_OUTPUT = false;

  /* Transaction codes come in Two Categories:
       Category 1: Client Requests to Server
       Category 2: Server to Server Interaction
  */

  /* **************** Category 1: Client Requests to Server**************/

  public static final int INCREASE=2001;
  public static final int DECREASE=2002;
  public static final int MIXTURE=2003;
  public static final int DONATION=2004;

  /* **************** Category 2: Server To server Interaction Codes *********/

  public static final int TEMP_INCREASE=3001;
  public static final int TEMP_DECREASE=3002;
  public static final int TEMP_COMPLETE=3003;
  public static final int GLOBAL_COMPLETE=3004;
  public static final int SELECTED_FOR_GLOBAL=3005;
  public static final int TEMP_MIXTURE=3006;
  public static final int DID_TEMP_FIRST=3007;
  public static final int TEMP_ANNOUNCE=3008;
  public static final int TEMP_COMPENSATE=3009;
  public static final int TEMP_FREEZE=3010;
  public static final int TEMP_FREEZE_REPLY=3011;
  public static final int TEMP_UNFREEZE=3012;
  public static final int TEMP_UNFREEZE_REPLY=3013;
  public static final int TEMP_DONATION=3014;

  public static final int CANCOMMIT=4001;
  public static final int YES=4002;
  public static final int NO=4003;
  public static final int ABORT=4004;
  public static final int COMMIT=4005;
  public static final int COMMITDONE=4006;

  public static final int SERVI_SLEEPER=10000;
  public static final int SERVI_COUNTER_MAX=1000;

  // Perm Proc
  public static final boolean PERMP_BASIC_DEBUG_OUTPUT = false;
  public static final boolean PERMP_PQUEUE_DEBUG_OUTPUT = false;

  public static final int PRECOMMIT=5000;  // was false
  public static final int COMPLETE=5001;  // was true

  // Temp Proc (transaction states)
  public static final boolean TEMPP_BASIC_DEBUG_OUTPUT = false;
  public static final boolean TEMPP_CQUEUE_DEBUG_OUTPUT = false;
  public static final boolean TEMPP_COMPN_DEBUG_OUTPUT = false;
  public static final boolean TEMPP_FREEZE_DEBUG_OUTPUT = true;

  public static final int NEW_ARRIVAL = 0;
  public static final int COST_BOUND_NOT_SATISFIED = 1;
  public static final int CB_SATISFIED_AWAITING_PROC = 2;
  public static final int DOING_TEMP_PROCESSING = 3;        // unused
  public static final int TEMP_PROCESSING_COMPLETE = 4;
  public static final int BEING_PROCESSED_GLOBALLY = 5;

  public static String numToName(int i) {
    switch (i) {
      case INCREASE: return "INCREASE";
      case DECREASE: return "DECREASE";
      case MIXTURE: return "MIXTURE";
      case DONATION: return "DONATION";

      case TEMP_INCREASE: return "TEMP_INCREASE";
      case TEMP_DECREASE: return "TEMP_DECREASE";
      case TEMP_COMPLETE: return "TEMP_COMPLETE";
      case GLOBAL_COMPLETE: return "GLOBAL_COMPLETE";
      case SELECTED_FOR_GLOBAL: return "SELECTED_FOR_GLOBAL";
      case TEMP_MIXTURE: return "TEMP_MIXTURE";
      case DID_TEMP_FIRST: return "DID_TEMP_FIRST";
      case TEMP_ANNOUNCE: return "TEMP_ANNOUNCE";
      case TEMP_COMPENSATE: return "TEMP_COMPENSATE";
      case TEMP_FREEZE: return "TEMP_FREEZE";
      case TEMP_FREEZE_REPLY: return "TEMP_FREEZE_REPLY";
      case TEMP_UNFREEZE: return "TEMP_UNFREEZE";
      case TEMP_UNFREEZE_REPLY: return "TEMP_UNFREEZE_REPLY";
      case TEMP_DONATION: return "TEMP_DONATION";

      case CANCOMMIT: return "CANCOMMIT";
      case YES: return "YES";
      case NO: return "NO";
      case ABORT: return "ABORT";
      case COMMIT: return "COMMIT";
      case COMMITDONE: return "COMMITDONE";

      case PRECOMMIT: return "PRECOMMIT";
      case COMPLETE: return "COMPLETE";

      case NEW_ARRIVAL: return "NEW_ARRIVAL";
      case COST_BOUND_NOT_SATISFIED: return "COST_BOUND_NOT_SATISFIED";
      case CB_SATISFIED_AWAITING_PROC: return "CB_SATISFIED_AWAITING_PROC";
      case TEMP_PROCESSING_COMPLETE: return "TEMP_PROCESSING_COMPLETE";
      case BEING_PROCESSED_GLOBALLY: return "BEING_PROCESSED_GLOBALLY";

      default: return "ILLEGAL_CONSTANT";
    } // end switch
  } // end numToName

}
