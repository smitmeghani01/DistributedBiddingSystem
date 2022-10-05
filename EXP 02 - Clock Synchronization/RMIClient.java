import java.rmi.*;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.io.*;
public class RMIClient {
    public static void main(String[] args) {
        
        int client_no  = Integer.parseInt(args[0]);
        Clock clientClock = Clock.systemUTC();

        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            MyInterface p = (MyInterface) Naming.lookup("myinterface");
            boolean flag = true;

            while(flag){
                System.out.print("Enter new bid amount: ");
                float input = Float.parseFloat(br.readLine());

                SystemTime stubTime = (SystemTime) Naming.lookup("SystemTime");
                long start = Instant.now().toEpochMilli(); //t1
                long serverTime = stubTime.getSystemTime();//t2
                System.out.println("Server time: " + serverTime);

                long end = Instant.now().toEpochMilli();
                long rtt = (end-start)/2;
                System.out.println("RTT of operation: " + rtt);

                long updatedTime = serverTime + rtt;

                Duration diff = Duration.ofMillis(updatedTime - clientClock.instant().toEpochMilli());

                clientClock = Clock.offset(clientClock, diff);
                System.out.println("New Client Clock Time: " + clientClock.instant().toEpochMilli());

                flag = p.bid(input, client_no);
                System.out.println("Current max bid: " + p.get_max_bid() + " creds.\n");
            }
            System.out.println("Auction closed, item sold to Client: " + p.get_winner());
            
        }
        catch(Exception e){
            System.out.println("Exception occured: " + e.getMessage());
        }
    }
}
