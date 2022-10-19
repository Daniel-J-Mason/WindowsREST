package entity;

import module.FileSortComparator;

import java.util.Objects;
import java.util.TreeMap;

public class Dxf {
    private String partNumber;
    private String fileLocation;
    private TreeMap<String, String> familyTree;
    
    public Dxf(){}
    
    public Dxf(String partNumber, String fileLocation){
        this.partNumber = partNumber;
        this.fileLocation = fileLocation;
    }
    
    public Dxf(String partNumber, String fileLocation, TreeMap<String, String> familyTree){
        this.partNumber = partNumber;
        this.fileLocation = fileLocation;
        this.familyTree = new TreeMap<>();
        this.familyTree.putAll(familyTree);
    }
    
    public String getPartNumber() {
        return partNumber;
    }
    
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    
    public String getFileLocation() {
        return fileLocation;
    }
    
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
    
    public TreeMap<String, String> getFamilyTree() {
        return familyTree;
    }
    
    public void setFamilyTree(TreeMap<String, String> familyTree) {
        FileSortComparator fileSortComparator = new FileSortComparator();
        this.familyTree = new TreeMap<>(fileSortComparator);
        this.familyTree.putAll(familyTree);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dxf dxf)) return false;
        return partNumber.equals(dxf.partNumber) && fileLocation.equals(dxf.fileLocation) && Objects.equals(familyTree, dxf.familyTree);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(partNumber, fileLocation);
    }
}
