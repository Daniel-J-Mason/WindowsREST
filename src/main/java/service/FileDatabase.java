package service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//Serves transmittal, truck, archive units, and work orders
public class FileDatabase {
    
    private HashMap<String, String> nameAndLink;
    private String serverLocation;
    private String resourceFolder;
    
    /**
     * Creates the map once when the class Object is created.
     */
    public FileDatabase(String serverLocation, String resourceFolder) {
        this.serverLocation = serverLocation;
        this.resourceFolder = resourceFolder;
        nameAndLink = mapBuilder();
    }
    
    public FileDatabase() {
    }
    
    public void setLocation(String serverLocation, String resourceFolder) {
        this.serverLocation = serverLocation;
        this.resourceFolder = resourceFolder;
        nameAndLink = mapBuilder();
    }
    
    /**
     * Creates a Hashmap: This takes file locations from resources, goes through every folder and adds every truck.
     * Key - FileName, Value - FullPath (link)
     *
     * @return Truck HashMap
     */
    private HashMap<String, String> mapBuilder() {
        HashMap<String, String> mainMap = new HashMap<>();
        
        for (String directory : networkDirectories()) {
            StringBuilder directoryLink = new StringBuilder();
            directoryLink.append(this.serverLocation)
                    .append("\\")
                    .append(directory);
            File tempFile = new File(directoryLink.toString());
            String[] directoryContents;
            directoryContents = tempFile.list();
            if (directoryContents == null || directoryContents.length == 0)
                continue;
            for (String fileName : directoryContents) {
                //This is specifically to help with excel, and it doesnt impact other files. May either parse off
                //Section for filters or create a separate method
                if (!fileName.startsWith("~") &&
                        !fileName.toLowerCase().contains("interior") &&
                        !fileName.toLowerCase().contains("cabinet")) {
                    StringBuilder subLink = new StringBuilder();
                    subLink.append(this.serverLocation)
                            .append("\\")
                            .append(directory)
                            .append("\\")
                            .append(fileName);
                    mainMap.put(fileName, subLink.toString());
                }
            }
            
        }
        
        return mainMap;
    }
    
    /**
     * Pulls from resources listing all archive folder locations
     *
     * @return List of all Archive sub folders
     */
    private ArrayList<String> networkDirectories() {
        ArrayList<String> files = new ArrayList<>();
        
        InputStream inputStream;
        try {
            inputStream = getClass().getResourceAsStream(resourceFolder);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                files.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return files;
    }
    
    /**
     * Takes one foldername and returns its hyperlink address
     *
     * @param fullName
     * @return fullPath
     */
    public String getLink(String fullName) {
        return nameAndLink.get(fullName);
    }
    
    /**
     * This is for use in recreating the truck map. This only runs on app start up and may need ot be refreshed
     * as units are uploaded
     */
    public void refresh() {
        nameAndLink = mapBuilder();
    }
    
    /**
     * This is passed to controlsfx for predictive searching
     *
     * @return ArrayList of all keys for the units HashMap
     */
    public ArrayList<String> autoCompleteList() {
        return new ArrayList<>(nameAndLink.keySet());
    }
}
