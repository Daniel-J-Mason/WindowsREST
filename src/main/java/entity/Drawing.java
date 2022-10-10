package entity;

import java.util.Objects;

public class Drawing {
    private String partNumber;
    private String fileLocation;
    
    public Drawing(){}
    
    public Drawing(String partNumber, String fileLocation){
        this.partNumber = partNumber;
        this.fileLocation = fileLocation;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Drawing drawing)) return false;
        return Objects.equals(partNumber, drawing.partNumber) && Objects.equals(fileLocation, drawing.fileLocation);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(partNumber, fileLocation);
    }
}
