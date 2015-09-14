package HADS.Server;

public abstract class StorageConnector {

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
