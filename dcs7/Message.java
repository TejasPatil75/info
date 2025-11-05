// Message.java
public class Message {
    
    // We have two types of messages:
    // 1. TASK: Represents a computational job
    // 2. SIGNAL: The control message for the D-S algorithm
    public enum Type {
        TASK,
        SIGNAL
    }

    public final Type type;
    public final int senderId;

    public Message(Type type, int senderId) {
        this.type = type;
        this.senderId = senderId;
    }
}