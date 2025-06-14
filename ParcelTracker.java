public class ParcelTracker {
    private static final int TABLE_SIZE = 100;
    private HashNode[] table;
    private int size;
    
    private class HashNode {
        String parcelID;
        ParcelRecord record;
        HashNode next;
        
        HashNode(String parcelID, ParcelRecord record) {
            this.parcelID = parcelID;
            this.record = record;
            this.next = null;
        }
    }
    
    private class ParcelRecord {
        Parcel.ParcelStatus status;
        int arrivalTick;
        int dispatchTick;
        int returnCount;
        String destinationCity;
        int priority;
        String size;
        
        ParcelRecord(Parcel parcel) {
            this.status = parcel.getStatus();
            this.arrivalTick = parcel.getArrivalTick();
            this.dispatchTick = -1;
            this.returnCount = 0;
            this.destinationCity = parcel.getDestinationCity();
            this.priority = parcel.getPriority();
            this.size = parcel.getSize();
        }
    }
    
    public ParcelTracker() {
        this.table = new HashNode[TABLE_SIZE];
        this.size = 0;
    }
    
    private int hash(String parcelID) {
        int hash = 0;
        for (char c : parcelID.toCharArray()) {
            hash = (hash * 31 + c) % TABLE_SIZE;
        }
        return Math.abs(hash);
    }
    
    public void insert(String parcelID, Parcel parcel) {
        if (exists(parcelID)) {
            return; // Avoid duplicates
        }
        
        int index = hash(parcelID);
        ParcelRecord record = new ParcelRecord(parcel);
        HashNode newNode = new HashNode(parcelID, record);
        
        // Insert at the beginning of the chain
        newNode.next = table[index];
        table[index] = newNode;
        size++;
    }
    
    public void updateStatus(String parcelID, Parcel.ParcelStatus newStatus) {
        HashNode node = findNode(parcelID);
        if (node != null) {
            node.record.status = newStatus;
            if (newStatus == Parcel.ParcelStatus.Dispatched) {
                // This would be set when dispatch happens
                // node.record.dispatchTick = currentTick;
            }
        }
    }
    
    public ParcelRecord get(String parcelID) {
        HashNode node = findNode(parcelID);
        return node != null ? node.record : null;
    }
    
    public void incrementReturnCount(String parcelID) {
        HashNode node = findNode(parcelID);
        if (node != null) {
            node.record.returnCount++;
        }
    }
    
    public boolean exists(String parcelID) {
        return findNode(parcelID) != null;
    }
    
    private HashNode findNode(String parcelID) {
        int index = hash(parcelID);
        HashNode current = table[index];
        
        while (current != null) {
            if (current.parcelID.equals(parcelID)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }
    
    public int getSize() {
        return size;
    }
    
    public double getLoadFactor() {
        return (double) size / TABLE_SIZE;
    }
    
    public void setDispatchTick(String parcelID, int dispatchTick) {
        HashNode node = findNode(parcelID);
        if (node != null) {
            node.record.dispatchTick = dispatchTick;
        }
    }
    
    public int getProcessingDelay(String parcelID) {
        HashNode node = findNode(parcelID);
        if (node != null && node.record.dispatchTick != -1) {
            return node.record.dispatchTick - node.record.arrivalTick;
        }
        return -1;
    }
    
    public int getReturnCount(String parcelID) {
        HashNode node = findNode(parcelID);
        return node != null ? node.record.returnCount : 0;
    }
} 