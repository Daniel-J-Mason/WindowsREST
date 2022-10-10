package entity;

import java.util.Objects;

public class Truck {
    private String truckNumber;
    private String truckFileName;
    private String fileLocation;
    private String transmittalLocation;
    private String workOrderLocation;
    
    public Truck(){}
    
    public Truck(String truckFileName){
        this.truckFileName = truckFileName;
    }
    
    public Truck(String truckNumber, String truckFileName, String fileLocation, String transmittalLocation, String workOrderLocation){
        this.truckNumber = truckNumber;
        this.truckFileName = truckFileName;
        this.fileLocation = fileLocation;
        this.transmittalLocation = transmittalLocation;
        this.workOrderLocation = workOrderLocation;
    }
    
    public String getTruckNumber() {
        return truckNumber;
    }
    
    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }
    
    public String getTruckFileName() {
        return truckFileName;
    }
    
    public void setTruckFileName(String truckFileName) {
        this.truckFileName = truckFileName;
    }
    
    public String getFileLocation() {
        return fileLocation;
    }
    
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
    
    public String getTransmittalLocation() {
        return transmittalLocation;
    }
    
    public void setTransmittalLocation(String transmittalLocation) {
        this.transmittalLocation = transmittalLocation;
    }
    
    public String getWorkOrderLocation() {
        return workOrderLocation;
    }
    
    public void setWorkOrderLocation(String workOrderLocation) {
        this.workOrderLocation = workOrderLocation;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Truck truck)) return false;
        return Objects.equals(truckNumber, truck.truckNumber) && Objects.equals(truckFileName, truck.truckFileName) && Objects.equals(fileLocation, truck.fileLocation) && Objects.equals(transmittalLocation, truck.transmittalLocation) && Objects.equals(workOrderLocation, truck.workOrderLocation);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(truckNumber, truckFileName, fileLocation, transmittalLocation, workOrderLocation);
    }
}
