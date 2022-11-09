import java.rmi.Remote;
import java.rmi.RemoteException;
public interface SystemTime extends Remote{
long getSystemTime() throws RemoteException;
}