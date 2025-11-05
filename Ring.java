import java.util.*;

public class Ring {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of process : \n");
        int n = sc.nextInt();
        int process[] = new int[n];
        boolean active[] = new boolean[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Enter the id of process : \n");
            process[i] = sc.nextInt();
            active[i] = true; // initially all processes are active
        }
        for (int i = 0; i < n; i++) {
            System.out.print(" [" + i + "] " + process[i]);
        }
        System.out.println();
        int coordinator = getCoordinator(process, active);
        System.out.println(" process " + coordinator + " select as co-ordinator\n");
        int choice;
        do {
            System.out.println(" 1. Crash A Process");
            System.out.println(" 2. Recover A Process");
            System.out.println(" 3. Display New Coordinator");
            System.out.println(" 4. Quit");
            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("Enter Process ID to crash: ");
                    int crash = sc.nextInt();
                    active[crash] = false;
                    System.out.println("Process " + process[crash] + " crashed.\n");
                    break;
                case 2:
                    System.out.print("Enter Process ID to recover: ");
                    int recover = sc.nextInt();
                    active[recover] = true;
                    System.out.println("Process " + process[recover] +
                            " recovered.\n");
                    break;
                case 3:
                    System.out.print("Enter the Process number who initiates election: ");
                    int initiator = sc.nextInt();
                    coordinator = ringElection(process, active,
                            initiator);
                    System.out.println("\n process " + coordinator + " select as co-ordinator\n");
                    break;
                case 4:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice! Try again.\n");
            }
        } while (choice != 4);
        sc.close();
    }

    static int ringElection(int[] process, boolean[] active, int initiator) {
        int n = process.length;
        int i = initiator;
        int next = (i + 1) % n;
        int max = active[i] ? process[i] : Integer.MIN_VALUE;
        do {
            if (active[i] && active[next]) {
                System.out.println("Process " + process[i] + " send message to " + process[next]);
            } else if (!active[i]) {
                System.out.println("Process " + process[i] + " has crashed. Skipping message.");
            }
            if (active[next] && process[next] > max) {
                max = process[next];
            }
            i = next;
            next = (next + 1) % n;
        } while (i != initiator);
        return max;
    }

    static int getCoordinator(int[] process, boolean[] active) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < process.length; i++) {
            if (active[i] && process[i] > max) {
                max = process[i];
            }
        }
        return max;
    }
}
