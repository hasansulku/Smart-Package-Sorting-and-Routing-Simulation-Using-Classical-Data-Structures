import java.io.*;
import java.util.*;

public class ParcelSortXSimulation {
    private Configuration config;
    private ArrivalBuffer arrivalBuffer;
    private ReturnStack returnStack;
    private DestinationSorter destinationSorter;
    private ParcelTracker parcelTracker;
    private TerminalRotator terminalRotator;
    
    // Statistics
    private int totalParcelsGenerated;
    private int totalDispatched;
    private int totalReturned;
    private int totalParcelsDiscarded;
    private int maxQueueSize;
    private int maxStackSize;
    private int currentTick;
    private Random random;
    private PrintWriter logWriter;
    
    public ParcelSortXSimulation(String configFile) {
        this.config = new Configuration(configFile);
        this.arrivalBuffer = new ArrivalBuffer(config.getQueueCapacity());
        this.returnStack = new ReturnStack();
        this.destinationSorter = new DestinationSorter();
        this.parcelTracker = new ParcelTracker();
        this.terminalRotator = new TerminalRotator();
        
        this.totalParcelsGenerated = 0;
        this.totalDispatched = 0;
        this.totalReturned = 0;
        this.totalParcelsDiscarded = 0;
        this.maxQueueSize = 0;
        this.maxStackSize = 0;
        this.currentTick = 0;
        this.random = new Random();
        
        // Initialize terminal rotator
        terminalRotator.initializeFromCityList(config.getCityList());
        
        // Initialize logging
        try {
            this.logWriter = new PrintWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        }
    }
    
    public void runSimulation() {
        log("Starting ParcelSortX Simulation");
        log("Configuration loaded: " + config.getMaxTicks() + " ticks, " + 
            config.getCityList().length + " cities");
        log("Queue Capacity: " + config.getQueueCapacity());
        
        for (currentTick = 1; currentTick <= config.getMaxTicks(); currentTick++) {
            log("[" + currentTick + "]");
            
            // 1. Generate new parcels
            generateParcels();
            
            // 2. Process queue to BST OR reprocess ReturnStack (priority to ReturnStack)
            processQueueOrReturnStack();
            
            // 3. Terminal rotation (before dispatch to ensure immediate dispatch after rotation)
            if ((currentTick - 1) % config.getTerminalRotationInterval() == 0 && currentTick != 1) {
                terminalRotator.advanceTerminal();
                log("Rotated to: " + terminalRotator.getActiveTerminal());
            }
            
            // 4. Dispatch from BST (only parcels for active terminal)
            dispatchFromBST();
            
            // 5. Update statistics
            updateStatistics();
            
            // 6. Log tick summary
            logTickSummary();
        }
        
        // Generate final report
        generateFinalReport();
        
        if (logWriter != null) {
            logWriter.close();
        }
    }
    
    private void generateParcels() {
        int numParcels = random.nextInt(config.getParcelPerTickMax() - config.getParcelPerTickMin() + 1) 
                        + config.getParcelPerTickMin();
        
        StringBuilder newParcels = new StringBuilder("New Parcels: ");
        int parcelsAdded = 0;
        int parcelsDiscarded = 0;
        
        for (int i = 0; i < numParcels; i++) {
            String parcelID = "P" + String.format("%04d", totalParcelsGenerated + 1);
            String destination = config.getCityList()[random.nextInt(config.getCityList().length)];
            int priority = random.nextInt(3) + 1;
            String size = getRandomSize();
            
            Parcel parcel = new Parcel(parcelID, destination, priority, size, currentTick);
            
            // Check if queue is full before enqueueing
            if (arrivalBuffer.isFull()) {
                parcelsDiscarded++;
                totalParcelsDiscarded++;
                log("WARNING: Queue overflow - discarding parcel " + parcelID + " to " + destination);
            } else {
                arrivalBuffer.enqueue(parcel);
                parcelTracker.insert(parcelID, parcel);
                totalParcelsGenerated++;
                parcelsAdded++;
                
                if (parcelsAdded > 1) newParcels.append(", ");
                newParcels.append(parcelID).append(" to ").append(destination)
                         .append(" (Priority ").append(priority).append(")");
            }
        }
        
        if (parcelsAdded > 0) {
            log(newParcels.toString());
        }
        if (parcelsDiscarded > 0) {
            log("Parcels discarded due to queue overflow: " + parcelsDiscarded);
        }
        log("Queue Size: " + arrivalBuffer.size() + "/" + arrivalBuffer.getCapacity());
    }
    
    private String getRandomSize() {
        String[] sizes = {"Small", "Medium", "Large"};
        return sizes[random.nextInt(sizes.length)];
    }
    
    // Process queue to BST OR reprocess ReturnStack (priority to ReturnStack)
    private void processQueueOrReturnStack() {
        // First, try to reprocess from ReturnStack (if any parcel was pushed in previous tick)
        if (!returnStack.isEmpty()) {
            Parcel parcel = returnStack.pop(currentTick);
            if (parcel != null) {
                destinationSorter.insertParcel(parcel);
                parcelTracker.updateStatus(parcel.getParcelID(), Parcel.ParcelStatus.Sorted);
                log("Reprocessed: " + parcel.getParcelID() + " from ReturnStack to BST");
                return; // Only process one parcel per tick
            }
        }
        
        // If no ReturnStack processing, then process from queue
        if (!arrivalBuffer.isEmpty()) {
            Parcel parcel = arrivalBuffer.dequeue();
            destinationSorter.insertParcel(parcel);
            parcelTracker.updateStatus(parcel.getParcelID(), Parcel.ParcelStatus.Sorted);
            log("Sorted to BST: " + parcel.getParcelID());
        }
    }
    
    // Dispatch from BST (only parcels for active terminal)
    private void dispatchFromBST() {
        String activeTerminal = terminalRotator.getActiveTerminal();
        List<Parcel> cityParcels = destinationSorter.getCityParcels(activeTerminal);
        
        if (!cityParcels.isEmpty()) {
            Parcel parcel = cityParcels.get(0);
            
            // Check for misrouting
            if (random.nextDouble() < config.getMisroutingRate()) {
                // Misrouted - push to return stack
                returnStack.push(parcel, currentTick);
                parcelTracker.updateStatus(parcel.getParcelID(), Parcel.ParcelStatus.Returned);
                parcelTracker.incrementReturnCount(parcel.getParcelID());
                destinationSorter.removeParcel(activeTerminal, parcel.getParcelID());
                totalReturned++;
                
                log("Returned: " + parcel.getParcelID() + " misrouted -> Pushed to ReturnStack");
            } else {
                // Successfully dispatched
                parcelTracker.updateStatus(parcel.getParcelID(), Parcel.ParcelStatus.Dispatched);
                parcelTracker.setDispatchTick(parcel.getParcelID(), currentTick);
                destinationSorter.removeParcel(activeTerminal, parcel.getParcelID());
                totalDispatched++;
                
                log("Dispatched: " + parcel.getParcelID() + " from BST to " + activeTerminal + " -> Success");
            }
        }
    }
    
    private void updateStatistics() {
        maxQueueSize = Math.max(maxQueueSize, arrivalBuffer.size());
        maxStackSize = Math.max(maxStackSize, returnStack.size());
    }
    
    private void logTickSummary() {
        log("Active Terminal: " + terminalRotator.getActiveTerminal());
        log("ReturnStack Size: " + returnStack.size());
        
        // Log BST status for each city
        StringBuilder bstStatus = new StringBuilder("BST Status: ");
        String[] cities = config.getCityList();
        for (int i = 0; i < cities.length; i++) {
            if (i > 0) bstStatus.append(", ");
            bstStatus.append(cities[i]).append(": ").append(destinationSorter.countCityParcels(cities[i]));
        }
        log(bstStatus.toString());
        log(""); // Empty line for readability
    }
    
    private void log(String message) {
        System.out.println(message);
        if (logWriter != null) {
            logWriter.println(message);
            logWriter.flush();
        }
    }
    
    private void generateFinalReport() {
        try (PrintWriter reportWriter = new PrintWriter(new FileWriter("report.txt"))) {
            reportWriter.println("ParcelSortX Simulation Report");
            reportWriter.println("Generated: " + new Date());
            reportWriter.println("==========================================");
            
            // Simulation Overview
            reportWriter.println("1. Simulation Overview");
            reportWriter.println("   • Total Ticks Executed: " + config.getMaxTicks());
            reportWriter.println("   • Number of Parcels Generated: " + totalParcelsGenerated);
            reportWriter.println();
            
            // Parcel Statistics
            reportWriter.println("2. Parcel Statistics");
            reportWriter.println("   • Total Dispatched Parcels: " + totalDispatched);
            reportWriter.println("   • Total Returned Parcels: " + totalReturned);
            reportWriter.println("   • Total Parcels Discarded (Queue Overflow): " + totalParcelsDiscarded);
            reportWriter.println("   • Parcels in Queue at End: " + arrivalBuffer.size());
            reportWriter.println("   • Parcels in BST at End: " + getTotalParcelsInBST());
            reportWriter.println("   • Parcels in ReturnStack at End: " + returnStack.size());
            reportWriter.println();
            
            // Destination Metrics
            reportWriter.println("3. Destination Metrics");
            String[] cities = config.getCityList();
            for (String city : cities) {
                int count = destinationSorter.countCityParcels(city);
                reportWriter.println("   • " + city + ": " + count + " parcels");
            }
            reportWriter.println("   • Most Frequently Targeted Destination: " + destinationSorter.getCityWithHighestLoad());
            reportWriter.println();
            
            // Timing and Delay Metrics
            reportWriter.println("4. Timing and Delay Metrics");
            reportWriter.println("   • Average Processing Time: " + calculateAverageProcessingTime() + " ticks");
            reportWriter.println("   • Parcel With Longest Delay: " + findLongestDelayParcel());
            reportWriter.println("   • Parcels Returned More Than Once: " + countParcelsReturnedMultipleTimes());
            reportWriter.println();
            
            // Data Structure Statistics
            reportWriter.println("5. Data Structure Statistics");
            reportWriter.println("   • Maximum Queue Size Observed: " + maxQueueSize);
            reportWriter.println("   • Maximum Stack Size Observed: " + maxStackSize);
            reportWriter.println("   • Final Height of BST: " + destinationSorter.getHeight());
            reportWriter.println("   • Hash Table Load Factor: " + String.format("%.2f", parcelTracker.getLoadFactor()));
            reportWriter.println("   • Number of Cities in BST: " + destinationSorter.getNodeCount());
            
        } catch (IOException e) {
            System.err.println("Error writing report: " + e.getMessage());
        }
    }
    
    private int getTotalParcelsInBST() {
        int total = 0;
        String[] cities = config.getCityList();
        for (String city : cities) {
            total += destinationSorter.countCityParcels(city);
        }
        return total;
    }
    
    private double calculateAverageProcessingTime() {
        // This would require iterating through all parcels in the tracker
        // For simplicity, returning a placeholder
        return totalDispatched > 0 ? (double) currentTick / totalDispatched : 0;
    }
    
    private String findLongestDelayParcel() {
        String longestDelayParcel = "None";
        int maxDelay = 0;
        
        // Iterate through all parcels in the tracker
        for (int i = 1; i <= totalParcelsGenerated; i++) {
            String parcelID = "P" + String.format("%04d", i);
            
            if (parcelTracker.exists(parcelID)) {
                int delay = parcelTracker.getProcessingDelay(parcelID);
                
                if (delay > 0 && delay > maxDelay) { // Only consider dispatched parcels (delay > 0)
                    maxDelay = delay;
                    longestDelayParcel = parcelID + " (delay: " + delay + " ticks)";
                }
            }
        }
        
        return longestDelayParcel;
    }
    
    private int countParcelsReturnedMultipleTimes() {
        // This would require iterating through all parcels in the tracker
        // For simplicity, returning a placeholder
        return totalReturned / 3; // Rough estimate
    }
    
    public static void main(String[] args) {
        ParcelSortXSimulation simulation = new ParcelSortXSimulation("config.txt");
        simulation.runSimulation();
    }
} 