import java.util.ArrayList;
import java.util.List;

public class DestinationSorter {
    private BSTNode root;
    
    private class BSTNode {
        String cityName;
        List<Parcel> parcelList;
        BSTNode left, right;
        
        BSTNode(String cityName) {
            this.cityName = cityName;
            this.parcelList = new ArrayList<>();
            this.left = null;
            this.right = null;
        }
    }
    
    public DestinationSorter() {
        this.root = null;
    }
    
    public void insertParcel(Parcel parcel) {
        String cityName = parcel.getDestinationCity();
        root = insertParcelRecursive(root, cityName, parcel);
    }
    
    private BSTNode insertParcelRecursive(BSTNode node, String cityName, Parcel parcel) {
        if (node == null) {
            BSTNode newNode = new BSTNode(cityName);
            newNode.parcelList.add(parcel);
            return newNode;
        }
        
        int comparison = cityName.compareTo(node.cityName);
        if (comparison == 0) {
            // City exists, add parcel to its list
            node.parcelList.add(parcel);
        } else if (comparison < 0) {
            node.left = insertParcelRecursive(node.left, cityName, parcel);
        } else {
            node.right = insertParcelRecursive(node.right, cityName, parcel);
        }
        
        return node;
    }
    
    public List<Parcel> getCityParcels(String city) {
        BSTNode node = findNode(root, city);
        return node != null ? new ArrayList<>(node.parcelList) : new ArrayList<>();
    }
    
    private BSTNode findNode(BSTNode node, String city) {
        if (node == null || node.cityName.equals(city)) {
            return node;
        }
        
        int comparison = city.compareTo(node.cityName);
        if (comparison < 0) {
            return findNode(node.left, city);
        } else {
            return findNode(node.right, city);
        }
    }
    
    public void inOrderTraversal() {
        inOrderTraversalRecursive(root);
    }
    
    private void inOrderTraversalRecursive(BSTNode node) {
        if (node != null) {
            inOrderTraversalRecursive(node.left);
            System.out.println("City: " + node.cityName + " - Parcels: " + node.parcelList.size());
            inOrderTraversalRecursive(node.right);
        }
    }
    
    public Parcel removeParcel(String city, String parcelID) {
        BSTNode node = findNode(root, city);
        if (node == null) {
            return null;
        }
        
        for (int i = 0; i < node.parcelList.size(); i++) {
            if (node.parcelList.get(i).getParcelID().equals(parcelID)) {
                return node.parcelList.remove(i);
            }
        }
        return null;
    }
    
    public int countCityParcels(String city) {
        BSTNode node = findNode(root, city);
        return node != null ? node.parcelList.size() : 0;
    }
    
    public int getHeight() {
        return getHeightRecursive(root);
    }
    
    private int getHeightRecursive(BSTNode node) {
        if (node == null) {
            return 0;
        }
        
        int leftHeight = getHeightRecursive(node.left);
        int rightHeight = getHeightRecursive(node.right);
        
        return Math.max(leftHeight, rightHeight) + 1;
    }
    
    public int getNodeCount() {
        return getNodeCountRecursive(root);
    }
    
    private int getNodeCountRecursive(BSTNode node) {
        if (node == null) {
            return 0;
        }
        
        return 1 + getNodeCountRecursive(node.left) + getNodeCountRecursive(node.right);
    }
    
    public String getCityWithHighestLoad() {
        return getCityWithHighestLoadRecursive(root, "", 0);
    }
    
    private String getCityWithHighestLoadRecursive(BSTNode node, String maxCity, int maxCount) {
        if (node == null) {
            return maxCity;
        }
        
        String leftResult = getCityWithHighestLoadRecursive(node.left, maxCity, maxCount);
        String currentCity = node.parcelList.size() > maxCount ? node.cityName : maxCity;
        int currentMax = Math.max(maxCount, node.parcelList.size());
        
        return getCityWithHighestLoadRecursive(node.right, currentCity, currentMax);
    }
} 