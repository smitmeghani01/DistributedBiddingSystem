import java.rmi.*;
import java.rmi.server.*;
import java.time.*;
import java.util.*;
import java.io.*;



public class RMIServer extends UnicastRemoteObject implements MyInterface{
    
    int serverNo;
    Clock clock;
    int RN[];
    boolean critical;
    int no_of_requests;
    TokenInterface token;

    // float max_bid;
    // int client_no;
    // int number_of_bids;
    // long startTime;
    // long endTime;

    //constructor
    public RMIServer(int serverNo) throws RemoteException {
        // TODO Auto-generated constructor stub
        super();
        System.out.println("Remote Server is Running now!");
        this.serverNo = serverNo;
        this.clock = Clock.systemUTC();
        RN = new int[2];
        no_of_requests=0;
        critical =false;

        // max_bid = 0;
        // client_no = -1;
        // number_of_bids = 0;
        // DefaultSystemTime time = new DefaultSystemTime();

        try{
            token = (TokenInterface) Naming.lookup("Token");
            // String objPath = "//localhost:1099/SystemTime";
            // SystemTime stub = (SystemTime) UnicastRemoteObject.exportObject(time, 0);
            // Naming.bind(objPath, time);
        }catch(Exception e){
            System.out.println("Exception occured" + e.getMessage());
        }
        // startTime = time.getSystemTime();
        // endTime = startTime + 300000;
    }
    
    public static void main(String[] args) {
        int serverNo = Integer.parseInt(args[0]);
        try {   
            RMIServer p = new RMIServer(serverNo);
            Naming.rebind("server"+serverNo, p);
        } catch (Exception e) {
            System.out.println("Exception occured: " + e.getMessage());
        }
    }
    
    // DefaultSystemTime time;

    @Override
    public boolean bid(float bid, int client_no) throws RemoteException {
        // TODO Auto-generated method stub

        try {

            ClockInterface cs = (ClockInterface) Naming.lookup("ClockServer");

            ClockMessage taggedTime = cs.getTaggedTime();

            long start = taggedTime.in;
            long end = taggedTime.out;
            long serverTime = taggedTime.time;

            long old_time = clock.instant().toEpochMilli();

            long rtt = (end - start) / 2; // calculate round trip time
            long updatedTime = serverTime + rtt; // new Local Clock Time
            long diff = updatedTime - old_time; // the difference between old and new time

            Duration duration = Duration.ofMillis(updatedTime - clock.instant().toEpochMilli());

            clock = clock.offset(clock, duration);

            long bid_time = updatedTime;
            System.out.println("Received your bid " + bid + " at server at time " + bid_time);

            if (token.getOwner() == -1) {
                token.setOwner(serverNo);
                System.out.println("No owner");
                no_of_requests++;
                RN[serverNo]++;
            } else {
                sendRequest();
            }
            while (token.getOwner() != serverNo);
            System.out.println("Got token");
            critical = true;
            critical_section(bid);
            critical = false;
            releaseToken();
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }

    public void sendRequest() throws RemoteException {
        no_of_requests++;
        for (int i = 0; i < 2; i++) {
            try {
                MyInterface server = (MyInterface) Naming.lookup("server" + i);

                server.recieveRequest(serverNo, no_of_requests);
            } catch (Exception e) {

                System.out.println("Exception occurred : " + e.getMessage());
            }
        }
    }

    public void critical_section(float bid) throws RemoteException {
        try {
            File file = new File("bid.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));

            float max_bid = Float.parseFloat(br.readLine());
            System.out.println("Bid from file - " + max_bid);
            if (max_bid < bid) {

            // FileWriter fileWritter = new FileWriter(file.getName(), true);
            // BufferedWriter outputWriter = new BufferedWriter(fileWritter);
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("bid.txt"));
            
                outputWriter.write(bid + "");
                outputWriter.flush();
                outputWriter.close();

            }
        } catch (Exception e) {
            System.out.println("error pakad liya");
            System.out.println(e);
        }
    }

    public void recieveRequest(int i, int n) throws RemoteException {
        System.out.println("Recieved request from " + i);
        if (RN[i] <= n) {
            RN[i] = n;
            if (token.getToken()[i] + 1 == RN[i]) {
                if (token.getOwner() == serverNo) {
                    if (critical) {
                        // token.queue = i;
                        System.out.println("Add to queue");
                        token.getQueue()[token.getTail()] = i;
                        token.setTail(token.getTail() + 1);
                    } else {
                        System.out.println("Queue empty, setting owner");
                        token.setOwner(i);
                    }
                }
            }
        }
    }

    public void releaseToken() throws RemoteException {
        token.setToken(serverNo, RN[serverNo]);
        if (token.getHead() != token.getTail()) {
            System.out.println("Release token");
            token.setOwner(token.getQueue()[token.getHead()]);
            System.out.println("New owner" + token.getOwner());
            token.setHead(token.getHead() + 1);
        }
    }
}