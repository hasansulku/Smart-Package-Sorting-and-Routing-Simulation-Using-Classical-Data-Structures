# ParcelSortX: Smart Package Sorting and Routing Simulation

## Project Overview
ParcelSortX is a Java-based simulation of a smart logistics center that demonstrates the use of classical data structures in a real-world application. The simulation manages parcel intake, sorting, routing, and dispatch using five core data structures:

1. **Queue (ArrivalBuffer)** - Circular array implementation for FIFO parcel processing
2. **Stack (ReturnStack)** - Singly linked list implementation for LIFO return processing
3. **Binary Search Tree (DestinationSorter)** - BST for efficient city-based parcel organization
4. **Hash Table (ParcelTracker)** - Chaining-based hash table for O(1) parcel tracking
5. **Circular Linked List (TerminalRotator)** - Circular list for round-robin terminal rotation

## Project Structure
```
ParcelSortX/
├── config.txt                    # Configuration file with simulation parameters
├── Parcel.java                   # Parcel entity with status enum
├── ArrivalBuffer.java           # Queue implementation (circular array)
├── ReturnStack.java             # Stack implementation (singly linked list)
├── DestinationSorter.java       # BST implementation for city sorting
├── ParcelTracker.java           # Hash table implementation for parcel tracking
├── TerminalRotator.java         # Circular linked list for terminal rotation
├── Configuration.java           # Configuration file parser
├── ParcelSortXSimulation.java   # Main simulation engine
├── README.md                    # This file
├── log.txt                      # Generated during simulation (detailed logs)
└── report.txt                   # Generated after simulation (final report)
```

## Compilation and Execution

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Command line access

### Compilation
```bash
javac *.java
```

### Execution
```bash
java ParcelSortXSimulation
```

## Configuration File (config.txt)
The simulation parameters are defined in `config.txt`:

- `MAX_TICKS=300` - Total simulation duration
- `QUEUE_CAPACITY=30` - Maximum parcels in arrival buffer
- `TERMINAL_ROTATION_INTERVAL=5` - Ticks between terminal rotations
- `PARCEL_PER_TICK_MIN=1` - Minimum parcels generated per tick
- `PARCEL_PER_TICK_MAX=3` - Maximum parcels generated per tick
- `MISROUTING_RATE=0.1` - Probability of parcel misrouting (10%)
- `CITY_LIST=Istanbul,Ankara,Izmir,Bursa,Antalya` - Available destinations

## Simulation Flow
1. **Tick Initialization** - Increment tick counter
2. **Parcel Generation** - Random number of parcels with random properties
3. **Queue Processing** - Dequeue parcels and insert into BST by destination
4. **Dispatch Evaluation** - Check active terminal and dispatch matching parcels
5. **Return Processing** - Handle misrouted parcels via return stack
6. **Terminal Rotation** - Rotate active terminal at specified intervals
7. **Statistics Update** - Track performance metrics

## Output Files

### log.txt
Detailed per-tick logging including:
- New parcel arrivals
- Queue processing
- Dispatch events
- Return events
- Terminal rotations
- Data structure status

### report.txt
Comprehensive final report with:
- Simulation overview
- Parcel statistics
- Destination metrics
- Timing and delay analysis
- Data structure performance metrics

## Data Structure Implementations

### Queue (ArrivalBuffer)
- **Implementation**: Circular array
- **Operations**: enqueue, dequeue, peek, isFull, isEmpty, size
- **Purpose**: FIFO processing of incoming parcels

### Stack (ReturnStack)
- **Implementation**: Singly linked list
- **Operations**: push, pop, peek, isEmpty, size
- **Purpose**: LIFO processing of returned parcels

### Binary Search Tree (DestinationSorter)
- **Implementation**: Standard BST with city nodes containing parcel lists
- **Operations**: insertParcel, getCityParcels, removeParcel, inOrderTraversal
- **Purpose**: Efficient city-based parcel organization

### Hash Table (ParcelTracker)
- **Implementation**: Chaining with linked lists
- **Operations**: insert, updateStatus, get, incrementReturnCount, exists
- **Purpose**: O(1) parcel tracking and status management

### Circular Linked List (TerminalRotator)
- **Implementation**: Circular singly linked list
- **Operations**: initializeFromCityList, advanceTerminal, getActiveTerminal
- **Purpose**: Round-robin terminal rotation

## Features
- ✅ Complete data structure implementations without standard libraries
- ✅ Comprehensive logging and reporting
- ✅ Configurable simulation parameters
- ✅ Error handling for queue overflow and misrouting
- ✅ Performance statistics and metrics
- ✅ Modular and extensible design

## Sample Output
```
[Tick 13]
New Parcels: P023 to Istanbul (Priority 2), P024 to Izmir (Priority 1)
Queue Size: 3
Sorted to BST: P023, P024
Dispatched: P020 from BST to Ankara -> Success
Returned: P018 misrouted -> Pushed to ReturnStack
Active Terminal: Ankara
ReturnStack Size: 1
BST Status: Istanbul: 5, Ankara: 2, Izmir: 3, Bursa: 1, Antalya: 0
```

