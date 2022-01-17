package modules;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DxfFinder {
    public String dxf;
    private TreeMap<String, String> dxfLocation;
    private TreeMap<String, String> familyTable;
    
    /**
     * This takes a dxf number of length 6 or more;
     * drawingLocation pulls the list from resources using getPrefixes() method.
     */
    public DxfFinder(String string){
        if (string.length() < 6)
            dxf = "-1";
        else
            this.dxf = string;
        
        dxfLocation = getPrefixes();
        familyTable = mapBuilder();
    }
    
    /**
     * TreeMap of the name and link family table for center grid
     * @return TreeMap of dxf family table
     */
    public TreeMap<String, String> getFamilyTable() {
        return familyTable;
    }
    
    /**
     * Checks the file the drawing should be located in to see if its in the drawings list
     * returns null if it doesn't find a match
     *  and returns the String drawing number if there is a match
     */
    public String getDxf() {
        int count = 0;
        for (String drawingFile: fileContents()){
            if (drawingFile.startsWith(dxf)){
                count++;
            }
        }
        if (count > 0)
            return dxf;
        else
            return "-1";
    }
    
    /**
     * Creates the hyperlink String for the drawing
     * @return drawing hyperlink
     */
    public String getLink(){
        StringBuilder link = new StringBuilder();
        link.append(getServerPathToDxfs());
        link.append("\\");
        link.append(dxfLocation.get(dxf.substring(0, 2)));
        link.append("\\");
        link.append(dxf.toLowerCase());
        link.append(".dxf");
        System.out.println(link);
        return link.toString();
    }
    
    /**
     * private method to return an ArrayList of dxf names in its folder
     * @return ArrayList of PDF's in a year folder
     */
    private ArrayList<String> fileContents(){
        StringBuilder link = new StringBuilder();
        link.append(getServerPathToDxfs());
        link.append("\\");
        link.append(dxfLocation.get(dxf.substring(0, 2)));
        
        File file = new File(link.toString());
        if (file.exists()) {
            ArrayList<String> pathName = new ArrayList<>(Arrays.asList(Objects.requireNonNull(file.list())));
            pathName.stream()
                    .forEach(String::toLowerCase);
    
            return pathName;
        }
        return new ArrayList<>();
    }
    
    /**
     * private method to pull the base file path from the resources file "FileLocations.txt" Line 1.
     * @return "FileLocations.txt" Line 1 forward of the :
     */
    public String getServerPathToDxfs(){
        HashMap<String, String> locations;
        InputStream inputStream = getClass().getResourceAsStream("/FileLocations.json");
        String myJson = null;
        try {
            myJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        locations = new Gson().fromJson(myJson, new TypeToken<HashMap<String, String>>(){}.getType());
    
        return locations.get("dxfLocation");
    }
    
    /**
     * Pulls the HashMap for drawing prefixes from resources file drawingPrefix.txt as they apply to dxfs
     */
    public TreeMap<String, String> getPrefixes(){
        TreeMap<String, String> returnMap = new TreeMap<>();
        
        InputStream inputStream = null;
        try{
            inputStream = getClass().getResourceAsStream("/drawingPrefix.txt");
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()){
                String[] parts = scanner.nextLine().split(":");
                returnMap.put(parts[0], parts[1]);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        
        return returnMap;
    }
    
    /**
     * Creates a TreeMap that consists of fileName and location for all elements that begin with the 6 digit dxf value
     * Key - FileName, Value - FullPath (link)
     * @return Truck HashMap
     */
    private TreeMap<String, String> mapBuilder(){
        // Order dxf's correctly
        FileSortComparator comparator = new FileSortComparator();
        TreeMap<String, String> mainMap = new TreeMap<>(comparator);
            StringBuilder link = new StringBuilder();
            link.append(getServerPathToDxfs());
            link.append("\\");
            link.append(dxfLocation.get(dxf.substring(0, 2)));
            File tempFile = new File(link.toString());
            String[] templist;
            templist = tempFile.list();
            
            if (tempFile.exists()) {
                for (String subelement : templist) {
                    if (subelement.startsWith(dxf) && subelement.toLowerCase().endsWith("dxf")) {
                        StringBuilder subLink = new StringBuilder();
                        subLink.append(getServerPathToDxfs());
                        subLink.append("\\");
                        subLink.append(dxfLocation.get(subelement.substring(0, 2)));
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
