// Process.java
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Process extends Thread {

    private final int id;
    private final BlockingQueue<Message> inbox; // This process's personal message queue
    private final Map<Integer, Process> allProcesses; // Reference to all processes to send messages

    // Dijkstra-Scholten state variables
    private boolean isRoot;
    private boolean isIdle;
    private Integer parent; // The ID of the process that sent the first task
    private int deficit;    // Counter for outstanding signals owed

    public Process(int id, BlockingQueue<Message> inbox, Map<Integer, Process> allProcesses, boolean isRoot) {
        this.id = id;
        this.inbox = inbox;
        this.allProcesses = allProcesses;
        this.isRoot = isRoot;

        // Initial state
        this.isIdle = true;
        this.parent = null;
        this.deficit = 0;
        setName("Process-" + id);
    }

    /**
     * Helper method to "send" a message to another process
     * by adding it to their inbox.
     */
    public void sendMessage(int targetId, Message message) {
        try {
            allProcesses.get(targetId).inbox.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        try {
            // The root process starts the computation
            if (isRoot) {
                startComputation();
            }

            // Main message-processing loop for all processes
            while (true) {
                Message msg = inbox.take(); // Wait for a message
                
                System.out.println("Process " + id + " received " + msg.type + " from " + msg.senderId);

                switch (msg.type) {
                    case TASK:
                        handleTask(msg);
                        break;
                    case SIGNAL:
                        handleSignal(msg);
                        break;
                }
            }
        } catch (InterruptedException e) {
            // Thread is interrupted, likely from a shutdown signal (not implemented here)
            System.out.println("Process " + id + " shutting down.");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Root process (only) calls this to kick off the computation.
     */
    private void startComputation() {
        System.out.println("Process " + id + " (Root) starting computation...");
        isIdle = false; // Become active

        // Send out some initial tasks
        // For this demo, let's send to Process 1 and 2
        sendMessage(1, new Message(Message.Type.TASK, this.id));
        deficit++;
        
        sendMessage(2, new Message(Message.Type.TASK, this.id));
        deficit++;

        // After sending initial tasks, the root becomes idle
        System.out.println("Process " + id + " (Root) finished initial work, deficit is " + deficit);
        isIdle = true;
        checkTerminationCondition();
    }

    /**
     * Logic for handling a TASK message.
     */
    private void handleTask(Message msg) {
        isIdle = false; // Become active

        // D-S Rule: If this is the first task, set the sender as the parent
        if (parent == null && !isRoot) {
            parent = msg.senderId;
            System.out.println("Process " + id + " set parent to " + parent);
        }

        // --- Simulate doing work ---
        // This work might randomly spawn more tasks for other processes
        try {
            Thread.sleep((long) (Math.random() * 2000));
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Randomly decide to spawn a new task
        if (Math.random() > 0.5 && this.id < (allProcesses.size() - 2)) {
            int targetId = (this.id + 1); // Send to the next process
            System.out.println("Process " + id + " spawning new task for " + targetId);
            sendMessage(targetId, new Message(Message.Type.TASK, this.id));
            deficit++; // D-S Rule: Increment deficit for every task sent
        }
        // --- End of work simulation ---

        System.out.println("Process " + id + " finished its task. Deficit=" + deficit);
        isIdle = true;
        checkTerminationCondition(); // Check if we can send a signal
    }

    /**
     * Logic for handling a SIGNAL message.
     */
    private void handleSignal(Message msg) {
        deficit--; // D-S Rule: Decrement deficit on receiving a signal
        System.out.println("Process " + id + " received SIGNAL from " + msg.senderId + ". Deficit now " + deficit);
        checkTerminationCondition(); // Check if we can send our own signal
    }

    /**
     * The core D-S logic: check if this process is done and can
     * "report back" to its parent.
     */
    private void checkTerminationCondition() {
        // D-S Rule: If idle and deficit is zero, send a signal
        if (isIdle && deficit == 0) {
            if (isRoot) {
                // Root is idle and deficit is 0: Termination detected!
                System.out.println("\n***************************************************");
                System.out.println(">>> TERMINATION DETECTED BY ROOT (Process " + id + ") <<<");
                System.out.println("***************************************************\n");
                
                // In a real system, root would now tell all processes to shut down.
                // For this simulation, we'll just exit.
                System.exit(0);

            } else {
                // Non-root is idle and deficit is 0: Send signal to parent
                System.out.println("Process " + id + " is idle (deficit 0), sending SIGNAL to parent " + parent);
                sendMessage(parent, new Message(Message.Type.SIGNAL, this.id));
                parent = null; // Reset parent for any future computations
            }
        }
    }
}