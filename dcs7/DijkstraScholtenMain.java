// DijkstraScholtenMain.java
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DijkstraScholtenMain {

    public static void main(String[] args) {
        final int NUM_PROCESSES = 5;

        // 1. Create a map to hold all processes by their ID
        Map<Integer, Process> allProcesses = new HashMap<>();

        // 2. Create all processes and their inboxes
        for (int i = 0; i < NUM_PROCESSES; i++) {
            BlockingQueue<Message> inbox = new LinkedBlockingQueue<>();
            boolean isRoot = (i == 0); // Process 0 will be the root
            
            // Pass each process its ID, its inbox, and the *map of all processes*
            Process p = new Process(i, inbox, allProcesses, isRoot);
            allProcesses.put(i, p);
        }

        // 3. Start all process threads
        System.out.println("Starting all " + NUM_PROCESSES + " processes...");
        for (Process p : allProcesses.values()) {
            p.start();
        }
        
        // The root process (Process 0) will automatically start the computation
        // from its run() method. The main thread can now finish.
    }
}