import java.rmi.RMISecurityManager;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class HelloClient
{
    public static void main(String arg[])
    {
        String message = "blank";
	
        try
        {
           Hello obj = (Hello) Naming.lookup( "//" +
                "10.0.2.15" +
                "/HelloServer");         //objectname in registry
           System.out.println(obj.sayHello());
        }
        catch (Exception e)
        {
           System.out.println("HelloClient exception: " + e.getMessage());
           e.printStackTrace();
        }
    }
}
