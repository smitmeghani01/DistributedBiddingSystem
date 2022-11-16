import java.rmi.RemoteException;
import java.rmi.Remote;

public interface SystemTime extends Remote{
    long getSystemTime() throws RemoteException;
}
