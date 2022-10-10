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
        truck.setFileLocation(truckDatabase.getLink(truckNumber));
        truck.setTransmittalLocation(truckDatabase.getLink(truckNumber));
        return truck;
    }
    
    public ArrayList<String> getAutoCompleteList(){
        return truckDatabase.autoCompleteList();
    }
    
    public void refreshAllDatabases(){
        truckDatabase.refresh();
        transmittalDatabase.refresh();
        workOrderDatabase.refresh();
    }
    private HashMap<String, String> fileLocationsHashmap() {
        HashMap<String, String> locations;
        InputStream inputStream = getClass().getResourceAsStream("/FileLocations.json");
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
