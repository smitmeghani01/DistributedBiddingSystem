import java.util.*;

class Process{
    public int id;
    public boolean active;

    public Process(int id){
        this.id = id;
        active = true;
    }
}
public class RingAlgo{
    int noOfProcesses;
    Process[] process;
    Scanner sc;

    public RingAlgo(){
        sc = new Scanner(System.in);
    }

    public void initializeRing(){
        System.out.print("Enter no. of process: ");
        noOfProcesses = sc.nextInt();

        process = new Process[noOfProcesses];
        for(int i=0; i<process.length;i++){
            process[i] = new Process(i);
        }
    }

    public int getMax(){
        int maxId = -99;
        int maxIdIndex = 0;
        for(int i=0; i<process.length; i++){
            if(process[i].active && process[i].id>maxId){
                maxId = process[i].id;
                maxIdIndex = i;
            }
        }
        return maxIdIndex;
    }

    public void performElection(){
        System.out.println("\nProcess no. "+process[getMax()].id + " fails");
        process[getMax()].active = false;
        System.out.println("\nElection Initiated by: ");
        int initiatorProcess = sc.nextInt();

        int prev = initiatorProcess;
        int next =prev+1;

        while(true){
            if(process[next].active){
                System.out.println("Process " + process [prev].id + " pass Election(" + process  [prev].id + ") to" + process[next].id);
            }
            next = (next+1)%noOfProcesses;
            if(next==initiatorProcess){
                break;
            }
        }

        System.out.println("Process " + process[getMax()].id + " becomes coordinator");
        int coordinator = process[getMax()].id;

        prev = coordinator;
        next = (prev+1)%noOfProcesses;

        while(true){
            if(process[next].active){
                System.out.println("Process " + process [prev].id + " pass Coordinator(" + coordinator + ") message to process " +   process[next].id);
                prev = next;
            }
            next = (next+1)%noOfProcesses;
            if(next == coordinator){
                System.out.println("End of Election");
                break;
            }
        }
    }

    public static void main(String[] args) {
        RingAlgo r = new RingAlgo();
        r.initializeRing();
        r.performElection();
    }
}