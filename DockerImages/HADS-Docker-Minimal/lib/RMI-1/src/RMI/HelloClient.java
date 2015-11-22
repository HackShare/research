import java.rmi.RMISecurityManager;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class HelloClient
{
    public static void main(String arg[])
    {
        String message = "blank";
	String connectorIP = "10.0.2.15";
	
        try
        {
           Hello obj = (Hello) Naming.lookup( "//" +
                connectorIP +
                "/HelloServer");         //objectname in registry
           System.out.println(obj.sayHello(connectorIP));
        }
        catch (Exception e)
        {
           System.out.println("HelloClient exception: " + e.getMessage());
           e.printStackTrace();
        }
    }
}
