import java.rmi.*;
import java.rmi.server.*;

public class RMIServer extends UnicastRemoteObject implements MyInterface{
    
    float max_bid;
    int client_no;
    int number_of_bids;

    //constructor
    public RMIServer() throws RemoteException {
        // TODO Auto-generated constructor stub
        super();
        System.out.println("Remote Server is Running now!");
        max_bid = 0;
        client_no = -1;
        number_of_bids = 0;
    }
    
    public static void main(String[] args) {
        try {
            RMIServer p = new RMIServer();
            Naming.rebind("myinterface", p);
        } catch (Exception e) {
            System.out.println("Exception occured: " + e.getMessage());
        }
    }

    @Override
    public void bid(float bidAmount, int client_no) throws RemoteException {
        // TODO Auto-generated method stub
        System.out.println("Recieved bid " + (number_of_bids+1) + " for " + bidAmount + " creds at Server!!");
        number_of_bids++;
        if (number_of_bids > 10) {
            System.out.println("Sold the item to Client " + client_no + "at bid amount: " + this.get_max_bid());
            return;
        }
        if (bidAmount > max_bid) {
            max_bid = bidAmount;
            this.client_no = client_no;
        }
    }

    @Override
    public boolean auctionon() throws RemoteException {
        // TODO Auto-generated method stub
        if (number_of_bids < 10) {
            return true;
        }
        else
            return false;
    }

    @Override
    public float get_max_bid() throws RemoteException {
        // TODO Auto-generated method stub
        return max_bid;
    }

    @Override
    public float get_winner() throws RemoteException {
        // TODO Auto-generated method stub
        return client_no;
    }
}
