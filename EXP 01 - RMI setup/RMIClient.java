import java.rmi.*;
import java.io.*;
public class RMIClient {
    public static void main(String[] args) {
        int client_no  = Integer.parseInt(args[0]);
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            MyInterface p = (MyInterface) Naming.lookup("myinterface");
            while(p.auctionon()){
                System.out.print("Enter new bid amount: ");
                float input = Float.parseFloat(br.readLine());
                p.bid(input, client_no);
                System.out.println("Current max bid: " + p.get_max_bid() + " creds.");
            }
            System.out.println("Auction closed, item sold to Client: " + p.get_winner());
        }
        catch(Exception e){
            System.out.println("Exception occured: " + e.getMessage());
        }
    }
}
