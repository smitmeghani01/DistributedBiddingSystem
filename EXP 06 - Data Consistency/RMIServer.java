import java.rmi.*;
import java.rmi.server.*;
import java.time.*;
import java.util.*;
import java.io.*;

public class RMIServer extends UnicastRemoteObject implements MyInterface {
    int serverNo;
    Clock clock;
    int RN[];
    boolean critical;
    int no_of_requests;
    TokenInterface token;

    public RMIServer(int serverNo) throws RemoteException {
        System.out.println("Remote Server is running now.");
        this.serverNo = serverNo;
        this.clock = Clock.systemUTC();
        RN = new int[2];
        no_of_requests = 0;
        critical = false;
        try {
            token = (TokenInterface) Naming.lookup("Token");
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e.getMessage());
        }
    }

    public static void main(String arg[]) {
        int serverNo = Integer.parseInt(arg[0]);
        try {

            RMIServer p = new RMIServer(serverNo);
            Naming.rebind("server" + serverNo, p);

        } catch (Exception e) {
            System.out.println("Exception occurred :" + e.getMessage());
        }
    }

    @Override
    public boolean bid(float bid, int client_no) throws

    RemoteException {

        try {
            ClockInterface cs = (ClockInterface)

            Naming.lookup("ClockServer");

            ClockMessage taggedTime = cs.getTaggedTime();
            long start = taggedTime.in;
            long end = taggedTime.out;
            long serverTime = taggedTime.time;
            long old_time = clock.instant().toEpochMilli();
            long rtt = (end - start) / 2; // calculate round trip time
            long updatedTime = serverTime + rtt; // new Local Clock Time
            long diff = updatedTime - old_time; // the difference between

            // old and new time

            Duration duration = Duration.ofMillis(updatedTime -

                    clock.instant().toEpochMilli());

            clock = clock.offset(clock, duration);
            long bid_time = updatedTime;
            System.out.println(
                    "Received bid from client " + client_no + ".Bid =" + bid + " at server at time " + bid_time);

            if (token.getOwner() == -1) {
                token.setOwner(serverNo);
                // System.out.println("No owner");
                no_of_requests++;
                RN[serverNo]++;
            } else {
                sendRequest();
            }

            while (token.getOwner() != serverNo)
                ;
            System.out.println("Lock Acquired");
            critical = true;
            critical_section(bid, client_no, bid_time);
            critical = false;
            releaseToken();
            System.out.println("Lock Released");
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }

    public void sendRequest() throws RemoteException {
        no_of_requests++;
        for (int i = 0; i < 2; i++) {
            try {
                MyInterface server = (MyInterface) Naming.lookup("server"+ i);

                server.recieveRequest(serverNo, no_of_requests);
            } catch (Exception e) {
                System.out.println("Exception occurred : " +e.getMessage());
            }
        }
    }

    public void critical_section(float bid, int client_no, long bid_time) throws RemoteException {
        float max_bid = 0;
        File file2 = null;
        Scanner sc2 = null;
        try {
            File file1 = new File("bid1.txt");

            Scanner sc1 = new Scanner(file1);
            file2 = new File("bid2.txt");
            sc2 = new Scanner(file2);
            sc1.useDelimiter(",");
            max_bid = Float.parseFloat(sc1.next());
            System.out.println("Previous maximum bid - " + max_bid);

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Could not access file bid1.txt, trying to access backup");

            sc2.useDelimiter(",");
            max_bid = Float.parseFloat(sc2.next());
            System.out.println("Previous maximum bid - " + max_bid);
        }
        if (max_bid < bid) {
            try {
                BufferedWriter outputWriter = new BufferedWriter(new

                FileWriter("bid1.txt"));

                outputWriter.write(bid + "," + client_no + "," +bid_time);

                outputWriter.flush();
                outputWriter.close();
                outputWriter = new BufferedWriter(new

                FileWriter("bid2.txt"));

                outputWriter.write(bid + "," + client_no + "," +bid_time);

                outputWriter.flush();
                outputWriter.close();

            } catch (Exception e) {
                System.out.println("Error writing to bid files");
            }

        }
        try {
            BufferedWriter outputWriter = new BufferedWriter(
            new FileWriter("transactions_server" + this.serverNo + ".txt", true));
            outputWriter.write(bid + "," + client_no + "," + bid_time +"\n");

            outputWriter.flush();
            outputWriter.close();
        } catch (Exception e) {
            System.out.println("Failed to write to transaction file " +e);
        }
    }

    public void recieveRequest(int i, int n) throws RemoteException {
        if (RN[i] <= n) {
            RN[i] = n;
            if (token.getToken()[i] + 1 == RN[i]) {
                if (token.getOwner() == serverNo) {
                    if (critical) {
                        token.getQueue()[token.getTail()] = i;
                        token.setTail(token.getTail() + 1);
                    } else {
                        token.setOwner(i);
                    }
                }
            }
        }
    }

    public void releaseToken() throws RemoteException {
        token.setToken(serverNo, RN[serverNo]);
        if (token.getHead() != token.getTail()) {
            token.setOwner(token.getQueue()[token.getHead()]);
            token.setHead(token.getHead() + 1);
        }
    }

    public String getName() throws RemoteException {
        return "server" + serverNo;
    }
}