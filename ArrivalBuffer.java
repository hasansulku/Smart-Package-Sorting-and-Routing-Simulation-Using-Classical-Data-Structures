public class ArrivalBuffer {
    private Parcel[] queue;
    private int front;
    private int rear;
    private int count;
    private int capacity;
    
    public ArrivalBuffer(int capacity) {
        this.capacity = capacity;
        this.queue = new Parcel[capacity];
        this.front = 0;
        this.rear = -1;
        this.count = 0;
    }
    
    public void enqueue(Parcel parcel) {
        if (isFull()) {
            System.out.println("WARNING: Queue overflow - discarding parcel " + parcel.getParcelID() + 
                             " to " + parcel.getDestinationCity());
            return;
        }
        
        rear = (rear + 1) % capacity;
        queue[rear] = parcel;
        count++;
    }
    
    public Parcel dequeue() {
        if (isEmpty()) {
            return null;
        }
        
        Parcel parcel = queue[front];
        front = (front + 1) % capacity;
        count--;
        
        if (count == 0) {
            front = 0;
            rear = -1;
        }
        
        return parcel;
    }
    
    public Parcel peek() {
        if (isEmpty()) {
            return null;
        }
        return queue[front];
    }
    
    public boolean isFull() {
        return count == capacity;
    }
    
    public boolean isEmpty() {
        return count == 0;
    }
    
    public int size() {
        return count;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void printQueueStatus() {
        System.out.println("Queue Status - Front: " + front + ", Rear: " + rear + 
                          ", Count: " + count + ", Capacity: " + capacity);
    }
} 