package modules;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//refactor out fileinputStream locations and then condense this and Truckfinder into one file with a constructor
//variable coming from a fileInputStream code.

public class ArchiveTruckMapDownloader {
    private HashMap<String, String> nameAndLink;
    private ArrayList<String> locationsFromResources;
    
    /**
     * Creates the map once when the class Object is created.
     */
    public ArchiveTruckMapDownloader(){
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
                StringBuilder subLink = new StringBuilder();
                subLink.append(getServerPathToUnits());
                subLink.append("\\");
                subLink.append(element);
                subLink.append("\\");
                subLink.append(subelement);
                mainMap.put(subelement, subLink.toString());
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
            inputStream = getClass().getResourceAsStream("/archiveTruckLocations.txt");
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()){
                files.add(scanner.nextLine());
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        
        locationsFromResources = files;
        return files;
    }
    
    /**
     * Uses the resources folder to pull Archive location on the server.
     * @return Server location String
     */
    private String getServerPathToUnits(){
        String returnString = "";
        
        InputStream inputStream = null;
        try{
            
            inputStream = getClass().getResourceAsStream("/FileLocations.txt");
            Scanner scanner = new Scanner(inputStream);
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            String[] parts = scanner.nextLine().split(";");
            returnString = parts[1];
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        
        return returnString;
    }
}