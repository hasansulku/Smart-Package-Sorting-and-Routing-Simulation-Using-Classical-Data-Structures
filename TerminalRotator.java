public class TerminalRotator {
    private TerminalNode head;
    private TerminalNode currentActiveTerminal;
    private int size;
    
    private class TerminalNode {
        String cityName;
        TerminalNode next;
        
        TerminalNode(String cityName) {
            this.cityName = cityName;
            this.next = null;
        }
    }
    
    public TerminalRotator() {
        this.head = null;
        this.currentActiveTerminal = null;
        this.size = 0;
    }
    
    public void initializeFromCityList(String[] cityArray) {
        if (cityArray == null || cityArray.length == 0) {
            return;
        }
        
        // Create the first node
        head = new TerminalNode(cityArray[0]);
        TerminalNode current = head;
        size = 1;
        
        // Create remaining nodes
        for (int i = 1; i < cityArray.length; i++) {
            TerminalNode newNode = new TerminalNode(cityArray[i]);
            current.next = newNode;
            current = newNode;
            size++;
        }
        
        // Make it circular
        current.next = head;
        
        // Set initial active terminal
        currentActiveTerminal = head;
    }
    
    public void advanceTerminal() {
        if (currentActiveTerminal != null) {
            currentActiveTerminal = currentActiveTerminal.next;
        }
    }
    
    public String getActiveTerminal() {
        return currentActiveTerminal != null ? currentActiveTerminal.cityName : null;
    }
    
    public void printTerminalOrder() {
        if (head == null) {
            System.out.println("No terminals available");
            return;
        }
        
        System.out.println("Terminal Rotation Order:");
        TerminalNode current = head;
        int count = 0;
        
        do {
            String marker = (current == currentActiveTerminal) ? " [ACTIVE]" : "";
            System.out.println((count + 1) + ". " + current.cityName + marker);
            current = current.next;
            count++;
        } while (current != head);
    }
    
    public int getSize() {
        return size;
    }
} 