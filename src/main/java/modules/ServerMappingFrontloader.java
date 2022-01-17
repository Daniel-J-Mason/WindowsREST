package modules;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//Serves transmittal, truck, archive units, and pending future update for work orders
public class ServerMappingFrontloader {
    
    private HashMap<String, String> nameAndLink;
    private String location;
    private String subfolderLocation;
    
    /**
     * Creates the map once when the class Object is created.
     */
    public ServerMappingFrontloader(String location, String subfolderLocation){
        this.location = location;
        this.subfolderLocation = subfolderLocation;
        nameAndLink = mapBuilder();
    }
    
    public ServerMappingFrontloader(){
    }
    
    public void setLocation(String location, String subfolderLocation){
        this.location = location;
        this.subfolderLocation = subfolderLocation;
        nameAndLink = mapBuilder();
    }
    
    /**
     * Takes one foldername and returns its hyperlink address
     * @param fullName
     * @return fullPath
     */
    public String getLink(String fullName){
        return nameAndLink.get(fullName);
    }
    
    /**
     * This is for use in recreating the truck map. This only runs on app start up and may need ot be refreshed
     * as units are uploaded
     */
    public void refresh(){
        nameAndLink = mapBuilder();
    }
    
    /**
     * This is passed to controlsfx for predictive searching
     * @return ArrayList of all keys for the units HashMap
     */
    public ArrayList<String> autoCompleteList(){
        return new ArrayList<>(nameAndLink.keySet());
    }
    
    /**
     * Creates a Hashmap: This takes file locations from resources, goes through every folder and adds every truck.
     * Key - FileName, Value - FullPath (link)
     * @return Truck HashMap
     */
    private HashMap<String, String> mapBuilder(){
        HashMap<String, String> mainMap = new HashMap<>();
        
        for (String element: subFolders()){
            StringBuilder link = new StringBuilder();
            link.append(getServerPathToUnits());
            link.append("\\");
            link.append(element);
            File tempFile = new File(link.toString());
            String[] templist;
            templist = tempFile.list();
            if (templist == null || templist.length == 0)
                continue;
            for(String subelement: templist){
                //This is specifically to help with excel, and it doesnt impact other files. May either parse off
                //Section for filters or create a separate method
                if (!subelement.startsWith("~") &&
                        !subelement.toLowerCase().contains("interior") && !subelement.toLowerCase().contains("cabinet")) {
                    StringBuilder subLink = new StringBuilder();
                    subLink.append(getServerPathToUnits());
                    subLink.append("\\");
                    subLink.append(element);
                    subLink.append("\\");
                    subLink.append(subelement);
                    mainMap.put(subelement, subLink.toString());
                }
            }
            
        }
        
        return mainMap;
    }
    
    /**
     * Pulls from resources listing all archive folder locations
     * @return List of all Archive sub folders
     */
    private ArrayList<String> subFolders(){
        ArrayList<String> files = new ArrayList<>();
        
        InputStream inputStream = null;
        try{
            inputStream = getClass().getResourceAsStream(subfolderLocation);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()){
                files.add(scanner.nextLine());
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    
        return files;
    }
    
    /**
     * Uses the resources' folder to pull Archive location on the server.
     * @return Server location String
     */
    private String getServerPathToUnits(){
        return location;
    }
}
