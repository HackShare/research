package HADS.Server;

import org.redisson.*;

import org.redisson.core.RMap;
import org.redisson.core.RQueue;
import org.redisson.client.WriteRedisConnectionException;
import org.redisson.codec.SerializationCodec;
import org.redisson.core.ClusterNode;
import org.redisson.core.Node;
import org.redisson.core.NodesGroup;

import java.util.Map;

public class RedisStorage extends StorageConnector
{

	public Redisson redisson;

	public RedisStorage()
	{
		initialize();
	}

	public boolean initialize()
	{
		try {
			System.out.println("Initializing Redisson Cluster...");

        		String redisAddress = System.getProperty("redisAddress");
			System.out.println("Redis: Address is set to " + redisAddress);
			if (redisAddress == null) {
			    redisAddress = "127.0.0.1:6379";
			    System.out.println("Redis: Default address to " + redisAddress);
			}
        		Config config = new Config();
        		config.useSingleServer().setAddress(redisAddress);
			config.setCodec(new SerializationCodec());
			redisson = Redisson.create(config);

			NodesGroup<Node> nodes = redisson.getNodesGroup();
			System.out.println("Redis: A node has joined the cluster.");
			System.out.println("Redis: Current Cluster Size is " +
				nodes.getNodes().size() + ".");

			return true;
		}
		catch(Exception e)
		{
			System.err.println("There was an issue creating an instance.");
			e.printStackTrace();
			return false;
		}
	}

	public Object put(Object K, Object V)
	{
		RMap<Object, Object> redisMap = redisson.getMap("map");
        	return redisMap.put(K, V);
	}

	public Object get(Object K)
	{
		RMap<Object, Object> redisMap = redisson.getMap("map");
		return redisMap.get(K);
	}

}
