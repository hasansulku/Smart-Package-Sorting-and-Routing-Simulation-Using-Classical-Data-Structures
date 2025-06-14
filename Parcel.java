public class Parcel {
    private String parcelID;
    private String destinationCity;
    private int priority;
    private String size;
    private int arrivalTick;
    private ParcelStatus status;
    
    public enum ParcelStatus {
        InQueue, Sorted, Dispatched, Returned
    }
    
    public Parcel(String parcelID, String destinationCity, int priority, String size, int arrivalTick) {
        this.parcelID = parcelID;
        this.destinationCity = destinationCity;
        this.priority = priority;
        this.size = size;
        this.arrivalTick = arrivalTick;
        this.status = ParcelStatus.InQueue;
    }
    
    // Getters
    public String getParcelID() { return parcelID; }
    public String getDestinationCity() { return destinationCity; }
    public int getPriority() { return priority; }
    public String getSize() { return size; }
    public int getArrivalTick() { return arrivalTick; }
    public ParcelStatus getStatus() { return status; }
    
    // Setters
    public void setStatus(ParcelStatus status) { this.status = status; }
    
    @Override
    public String toString() {
        return "Parcel ID: " + parcelID + 
               ", Destination: " + destinationCity + 
               ", Priority: " + priority + 
               ", Size: " + size + 
               ", Arrival Tick: " + arrivalTick + 
               ", Status: " + status;
    }
} 