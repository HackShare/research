package HADS.Server;

import org.apache.ignite.*;
import org.apache.ignite.examples.*;

import java.util.concurrent.*;

public class GridGainStorage extends StorageConnector
{

	public Ignite ignite;

	public GridGainStorage()
	{
		initialize();
	}

	public boolean initialize()
	{
		try {
			System.out.println("Initializing GridGain Cluster...");
        		ignite = Ignition.start("/home/mike/gridgain-community-fabric-1.3.3/examples/config/example-cache.xml");

			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public Object put(Object K, Object V)
	{
		IgniteCache<Object, Object> cache = ignite.getOrCreateCache("cache");
		cache.put(K, V);
        	return V;
	}

	public Object get(Object K)
	{
		IgniteCache<Object, Object> cache = ignite.getOrCreateCache("cache");
		return cache.get(K);
	}

}
