import java.rmi.*;
import java.rmi.server.*;

public class RMIServer extends UnicastRemoteObject implements MyInterface{
    
    float max_bid;
    int client_no;
    int number_of_bids;
    long startTime;
    long endTime;

    //constructor
    public RMIServer() throws RemoteException {
        // TODO Auto-generated constructor stub
        super();
        System.out.println("Remote Server is Running now!");
        max_bid = 0;
        client_no = -1;
        number_of_bids = 0;
        DefaultSystemTime time = new DefaultSystemTime();
        try{
            String objPath = "//localhost:1099/SystemTime";
            SystemTime stub = (SystemTime) UnicastRemoteObject.exportObject(time, 0);
            Naming.bind(objPath, time);
        }catch(Exception e){
            System.out.println("System time registry exists, skipping making new one");
        }
        startTime = time.getSystemTime();
        endTime = startTime + 30000;
    }
     
    public static void main(String[] args) {
        try {   
            RMIServer p = new RMIServer();
            Naming.rebind("myinterface", p);
        } catch (Exception e) {
            System.out.println("Exception occured: " + e.getMessage());
        }
    }
    
    DefaultSystemTime time;
    @Override
    public boolean bid(float bidAmount, int client_no) throws RemoteException {
        // TODO Auto-generated method stub

        time = new DefaultSystemTime();
        long bid_time = time.getSystemTime();

        System.out.println("Recieved bid " + (number_of_bids+1) + " for " + bidAmount + " creds at server time " + time+"!!");
        // number_of_bids++;

        if (auctionOn(bid_time)) {

            if (bidAmount > max_bid) {
                max_bid = bidAmount;
                this.client_no = client_no;
            }
        }else{
            System.out.println("Sold the item to Client " + client_no + "at bid amount: " + this.get_max_bid());
            return false;
        }
        return true;
    }

    @Override
    public float get_max_bid() throws RemoteException {
        // TODO Auto-generated method stub
        return max_bid;
    }

    @Override
    public boolean auctionOn(long curr_time) throws RemoteException {
        // TODO Auto-generated method stub
        if (curr_time<=endTime) {
            return true;
        }
        else
            return false;
    }


    @Override
    public float get_winner() throws RemoteException {
        // TODO Auto-generated method stub
        return client_no;
    }
}
