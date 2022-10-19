package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Truck;
import module.FileDatabase;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TruckService {
    private HashMap<String, String> locations;
    private final FileDatabase truckDatabase = new FileDatabase();
    private final FileDatabase transmittalDatabase = new FileDatabase();
    private final FileDatabase workOrderDatabase = new FileDatabase();
    
    public TruckService(String truckServerPath, String truckResourceFolder,
                        String transmittalServerPath, String transmittalResourceFolder,
                        String workOrderServerPath, String workOrderResourceFolder){
        locations = fileLocationsHashmap();
        truckDatabase.setLocation(locations.get(truckServerPath), truckResourceFolder);
        transmittalDatabase.setLocation(locations.get(transmittalServerPath), transmittalResourceFolder);
        workOrderDatabase.setLocation(locations.get(workOrderServerPath), workOrderResourceFolder);
    }
    
    public Truck getTruck(String truckFileName){
        Truck truck = new Truck();
        String truckNumber = truckFileName.substring(0, 6);
        truck.setTruckNumber(truckNumber);
        truck.setTruckFileName(truckFileName);
        truck.setFileLocation(truckDatabase.getLink(truckFileName));
        truck.setTransmittalLocation(getTransmittalLink(truckNumber));
        truck.setWorkOrderLocation(getWorkOrderLink(truckNumber));
        return truck;
    }
    
    public ArrayList<String> getAutoCompleteList(){
        return truckDatabase.autoCompleteList();
    }
    
    private String getTransmittalLink(String truckNumber) {
        List<String> listOfTransmittals = transmittalDatabase.autoCompleteList();
        
        for (String transmittal: listOfTransmittals){
            if (transmittal.startsWith(truckNumber)){
                return transmittalDatabase.getLink(transmittal);
            }
        }
        
        return null;
    }
    
    private String getWorkOrderLink(String truckNumber) {
        List<String> listOfWorkOrders = workOrderDatabase.autoCompleteList();
        
        for (String workOrder: listOfWorkOrders){
            if (workOrder.startsWith(truckNumber)){
                return workOrderDatabase.getLink(workOrder);
            }
        }
        
        return null;
    }
    
    public void refreshAllDatabases(){
        truckDatabase.refresh();
        transmittalDatabase.refresh();
        workOrderDatabase.refresh();
    }
    private HashMap<String, String> fileLocationsHashmap() {
        HashMap<String, String> locations;
        InputStream inputStream = getClass().getResourceAsStream("/locations/FileLocations.json");
        assert inputStream != null;
        String myJson = null;
        try {
            myJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        locations = new Gson().fromJson(myJson, new TypeToken<HashMap<String, String>>() {
        }.getType());
        return locations;
    }
    
}
