package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Dxf {
    private final String partNumber;
    private final TreeMap<String, String> prefixMap;
    private final TreeMap<String, String> familyTable;
    private final String serverLocation;
    /**
     * This takes a dxf number of length 6 or more;
     * drawingLocation pulls the list from resources using getPrefixes() method.
     */
    public Dxf(String partNumber) {
        if (partNumber.length() < 6) {
            this.partNumber = "-1";
        } else {
            this.partNumber = partNumber;
        }
        prefixMap = getPrefixes();
        familyTable = familyTableBuilder();
        serverLocation = loadServerPathToDxfs();
    }
    
    /**
     * TreeMap of the name and link family table for center grid
     *
     * @return TreeMap of dxf family table
     */
    public TreeMap<String, String> getFamilyTable() {
        return familyTable;
    }
    
    /**
     * Checks the file the drawing should be located in to see if its in the drawings list
     * returns null if it doesn't find a match
     * and returns the String drawing number if there is a match
     */
    public String getDxf() {
        int count = 0;
        for (String drawingFile : fileContents()) {
            if (drawingFile.startsWith(partNumber)) {
                count++;
            }
        }
        if (count > 0)
            return partNumber;
        else
            return "-1";
    }
    
    /**
     * Creates the hyperlink String for the drawing
     *
     * @return drawing hyperlink
     */
    public String getLink() {
        StringBuilder link = new StringBuilder();
        link.append(serverLocation);
        link.append("\\");
        link.append(prefixMap.get(partNumber.substring(0, 2)));
        link.append("\\");
        link.append(partNumber.toLowerCase());
        link.append(".dxf");
        System.out.println(link);
        return link.toString();
    }
    
    /**
     * private method to return an ArrayList of dxf names in its folder
     *
     * @return ArrayList of PDF's in a year folder
     */
    private ArrayList<String> fileContents() {
        StringBuilder link = new StringBuilder();
        link.append(serverLocation);
        link.append("\\");
        link.append(prefixMap.get(partNumber.substring(0, 2)));
        
        File file = new File(link.toString());
        if (file.exists()) {
            ArrayList<String> folderContents = new ArrayList<>(Arrays.asList(Objects.requireNonNull(file.list())));
            ArrayList<String> modifiedFolderContents =
                    (ArrayList<String>) folderContents.stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());
            
            return modifiedFolderContents;
        }
        return new ArrayList<>();
    }
    
    /**
     * private method to pull the base file path from the resources file "FileLocations.txt" Line 1.
     *
     * @return "FileLocations.txt" Line 1 forward of the :
     */
    private String loadServerPathToDxfs() {
        HashMap<String, String> locations;
        InputStream inputStream = getClass().getResourceAsStream("/FileLocations.json");
        String myJson = null;
        try {
            myJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        locations = new Gson().fromJson(myJson, new TypeToken<HashMap<String, String>>() {
        }.getType());
        
        return locations.get("dxfLocation");
    }
    
    /**
     * Pulls the HashMap for drawing prefixes from resources file drawingPrefix.txt as they apply to dxfs
     */
    public TreeMap<String, String> getPrefixes() {
        TreeMap<String, String> returnMap = new TreeMap<>();
        
        InputStream inputStream = null;
        try {
            inputStream = getClass().getResourceAsStream("/drawingPrefix.txt");
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(":");
                returnMap.put(parts[0], parts[1]);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return returnMap;
    }
    
    /**
     * Creates a TreeMap that consists of fileName and location for all elements that begin with the 6 digit dxf value
     * Key - FileName, Value - FullPath (link)
     *
     * @return Truck HashMap
     */
    private TreeMap<String, String> familyTableBuilder() {
        // Order dxf's correctly
        FileSortComparator comparator = new FileSortComparator();
        TreeMap<String, String> mainMap = new TreeMap<>(comparator);
        StringBuilder link = new StringBuilder();
        link.append(serverLocation);
        link.append("\\");
        link.append(prefixMap.get(partNumber.substring(0, 2)));
        File tempFile = new File(link.toString());
        String[] templist;
        templist = tempFile.list();
        
        if (tempFile.exists()) {
            for (String subelement : templist) {
                if (subelement.startsWith(partNumber) && subelement.toLowerCase().endsWith("dxf")) {
                    StringBuilder subLink = new StringBuilder();
                    subLink.append(serverLocation);
                    subLink.append("\\");
                    subLink.append(prefixMap.get(subelement.substring(0, 2)));
                    subLink.append("\\");
                    subLink.append(subelement);
                    mainMap.put(subelement, subLink.toString());
                }
                
            }
            
            return mainMap;
        }
        return new TreeMap<>();
    }
}
