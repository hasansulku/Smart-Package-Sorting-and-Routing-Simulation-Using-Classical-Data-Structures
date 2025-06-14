public class ReturnStack {
    private StackNode top;
    private int size;
    
    private class StackNode {
        Parcel parcel;
        int pushTick;  // Tick when parcel was pushed to stack
        StackNode next;
        
        StackNode(Parcel parcel, int pushTick) {
            this.parcel = parcel;
            this.pushTick = pushTick;
            this.next = null;
        }
    }
    
    public ReturnStack() {
        this.top = null;
        this.size = 0;
    }
    
    public void push(Parcel parcel, int currentTick) {
        StackNode newNode = new StackNode(parcel, currentTick);
        newNode.next = top;
        top = newNode;
        size++;
    }
    
    public Parcel pop(int currentTick) {
        if (isEmpty()) {
            return null;
        }
        
        // Only pop if parcel was pushed in a previous tick
        if (top.pushTick >= currentTick) {
            return null; // Parcel was pushed in current tick, cannot pop yet
        }
        
        Parcel parcel = top.parcel;
        top = top.next;
        size--;
        return parcel;
    }
    
    public Parcel peek() {
        if (isEmpty()) {
            return null;
        }
        return top.parcel;
    }
    
    public boolean isEmpty() {
        return top == null;
    }
    
    public int size() {
        return size;
    }
} 