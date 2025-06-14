import java.io.*;
import java.util.*;

public class Configuration {
    private int maxTicks;
    private int queueCapacity;
    private int terminalRotationInterval;
    private int parcelPerTickMin;
    private int parcelPerTickMax;
    private double misroutingRate;
    private String[] cityList;
    
    public Configuration(String filename) {
        loadConfiguration(filename);
    }
    
    private void loadConfiguration(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    switch (key) {
                        case "MAX_TICKS":
                            maxTicks = Integer.parseInt(value);
                            break;
                        case "QUEUE_CAPACITY":
                            queueCapacity = Integer.parseInt(value);
                            break;
                        case "TERMINAL_ROTATION_INTERVAL":
                            terminalRotationInterval = Integer.parseInt(value);
                            break;
                        case "PARCEL_PER_TICK_MIN":
                            parcelPerTickMin = Integer.parseInt(value);
                            break;
                        case "PARCEL_PER_TICK_MAX":
                            parcelPerTickMax = Integer.parseInt(value);
                            break;
                        case "MISROUTING_RATE":
                            misroutingRate = Double.parseDouble(value);
                            break;
                        case "CITY_LIST":
                            cityList = value.split(",");
                            for (int i = 0; i < cityList.length; i++) {
                                cityList[i] = cityList[i].trim();
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
            // Set default values
            setDefaultValues();
        }
    }
    
    private void setDefaultValues() {
        maxTicks = 300;
        queueCapacity = 30;
        terminalRotationInterval = 5;
        parcelPerTickMin = 1;
        parcelPerTickMax = 3;
        misroutingRate = 0.1;
        cityList = new String[]{"Istanbul", "Ankara", "Izmir", "Bursa", "Antalya"};
    }
    
    // Getters
    public int getMaxTicks() { return maxTicks; }
    public int getQueueCapacity() { return queueCapacity; }
    public int getTerminalRotationInterval() { return terminalRotationInterval; }
    public int getParcelPerTickMin() { return parcelPerTickMin; }
    public int getParcelPerTickMax() { return parcelPerTickMax; }
    public double getMisroutingRate() { return misroutingRate; }
    public String[] getCityList() { return cityList; }
} 