package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import module.FileDatabase;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkOrderService {
    private HashMap<String, String> locations;
    private final FileDatabase workOrderDatabase = new FileDatabase();
    
    public WorkOrderService(){
        locations = fileLocationsHashmap();
        workOrderDatabase.setLocation(locations.get("WorkOrderLocations"), "/locations/workOrderLocations.txt");
    }
    
    public String getLink(String workOrderFullName){
        return workOrderDatabase.getLink(workOrderFullName);
    }
    
    public ArrayList<String> getWorkOrderList(){
        return workOrderDatabase.autoCompleteList();
    }
    
    public void refresh(){
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
