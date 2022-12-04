import java.rmi.*;
import java.io.*;
import java.time.*;

public class RMIClient {
    public static void main(String args[]) {
        int client_no = Integer.parseInt(args[0]);
        Clock clientClock = Clock.systemUTC();
        try {

            BufferedReader br = new BufferedReader(new

            InputStreamReader(System.in));

            LoadBalancerInterface lb = (LoadBalancerInterface)

            Naming.lookup("LoadBalancer");
            boolean flag = true;
            while (flag) {
                System.out.println("Enter bid");
                float input = Float.parseFloat(br.readLine());

                MyInterface server = lb.getServer();
                // System.out.println("Connecting to " + server.getName());
                flag = server.bid(input, client_no);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e);
        }
    }
}