package HADS.Server;

public abstract class StorageConnector {

  private static StorageConnector sc = null;
  private static final String TYPE = "HAZEL";
  //private static final String TYPE = "REDIS";
  //private static final String TYPE = "GRID";

  /*
  * Gets the storage we would like to use. This can be Hazelcast/Redis/Gridgain
  * returns an Object of the Storage we are using
  * @param none
  * @return StorageConnector
  */
  public static StorageConnector getStorage()
  {
	if(sc==null)
	{
		if(TYPE.equals("HAZEL"))
		{
			sc = new HazelcastStorage();
		}
		if(TYPE.equals("REDIS"))
		{
			sc = new RedisStorage();
		}
		if(TYPE.equals("GRID"))
		{
			sc = new GridGainStorage();
		}
	}

	return sc;
  }

   /*
  * Certain memory grids take a little longer for start up
  * With the use of COPAR we must wait for all the nodes to be up and ready before we can start generating transactions
  * returns the estimated time to wait per server before starting to generate transactions in MS
  * @param
  * @return int
  */
  public static int genDelay()
  {
	int wait = -1; //No storage has been set.
	if(TYPE.equals("HAZEL"))
	{
		wait = 6500;
	}
	if(TYPE.equals("REDIS"))
	{
		wait = 0;
	}
	if(TYPE.equals("GRID"))
	{
		wait = 2000;
	}
	return wait;
  }

  /*
  * Initializes the data structures and servers
  * returns a boolean, true if the initialize was successful, false otherwise
  * @param
  * @return boolean
  */
  public abstract boolean initialize();
  
  /*
  * Places a value <V> into the associated map with key <K>
  * returns the object previously associated with key <K>, null otherwise
  * @param Object<K>, Object<V>
  * @return Object<V>
  */
  public abstract Object put(Object K, Object V);

  /*
  * Returns a specific object found in the data structure with key <K>
  * returns the object found
  * @param Object<K>
  * @return Object<V>
  */
  public abstract Object get(Object K);

}
