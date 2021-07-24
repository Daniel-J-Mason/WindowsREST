package modules;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TruckMapDownloader {
    private HashMap<String, String> nameAndLink;
    private ArrayList<String> locationsFromResources;
    
    public TruckMapDownloader(){
        nameAndLink = mapBuilder();
    }
    
    public void refresh(){
        nameAndLink = mapBuilder();
    }
    
    public String getLink(String fullName){
        return nameAndLink.get(fullName);
    }
    
    public ArrayList<String> autoCompleteList(){
        return new ArrayList<>(nameAndLink.keySet());
    }
    
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
    
    private ArrayList<String> subFolders(){
        ArrayList<String> files = new ArrayList<>();
    
        InputStream inputStream = null;
        try{
            inputStream = getClass().getResourceAsStream("/truckLocations.txt");
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
    
    private String getServerPathToUnits(){
        String returnString = "";
        
        InputStream inputStream = null;
        try{
            
            inputStream = getClass().getResourceAsStream("/FileLocations.txt");
            Scanner scanner = new Scanner(inputStream);
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
