package application;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class DxfFinder {
    private String dxf;
    private HashMap<String, String> drawingLocation;
    private HashMap<String, String> familyTable;
    
    /**
     * This takes a dxf number of length 6 or more;
     * drawingLocation pulls the list from resources using getPrefixes() method.
     */
    public DxfFinder(String string){
        if (string.length() < 6)
            dxf = "-1";
        else
            this.dxf = string;
        
        drawingLocation = getPrefixes();
        familyTable = mapBuilder();
    }
    
    public HashMap<String, String> getFamilyTable() {
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
        link.append(getServerPathToDrawings());
        link.append("/");
        link.append(drawingLocation.get(dxf.substring(0, 2)));
        link.append("/");
        link.append(dxf.toLowerCase());
        link.append(".dxf");
        System.out.println(link);
        return link.toString();
    }
    
    /**
     * private method to return and ArrayList of drawing names in its folder
     * @return ArrayList of PDF's in a year folder
     */
    private ArrayList<String> fileContents(){
        StringBuilder link = new StringBuilder();
        link.append(getServerPathToDrawings());
        link.append("/");
        link.append(drawingLocation.get(dxf.substring(0, 2)));
        
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
    private String getServerPathToDrawings(){
        String returnString = "";
        
        InputStream inputStream = null;
        try{
            inputStream = getClass().getResourceAsStream("/FileLocations.txt");
            Scanner scanner = new Scanner(inputStream);
            scanner.nextLine();
            String[] parts = scanner.nextLine().split(";");
            returnString = parts[1];
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        
        return returnString;
    }
    
    /**
     * Pulls the HashMap for drawing prefixes from resources file drawingPrefix.txt
     */
    public HashMap<String, String> getPrefixes(){
        HashMap<String, String> returnMap = new HashMap<>();
        
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
    
    private HashMap<String, String> mapBuilder(){
        HashMap<String, String> mainMap = new HashMap<>();
            StringBuilder link = new StringBuilder();
            link.append(getServerPathToDrawings());
            link.append("/");
            link.append(drawingLocation.get(dxf.substring(0, 2)));
            File tempFile = new File(link.toString());
            String[] templist;
            templist = tempFile.list();
            
            if (tempFile.exists()) {
                for (String subelement : templist) {
                    if (subelement.startsWith(dxf)) {
                        StringBuilder subLink = new StringBuilder();
                        subLink.append(getServerPathToDrawings());
                        subLink.append("/");
                        subLink.append(drawingLocation.get(subelement.substring(0, 2)));
                        subLink.append("/");
                        subLink.append(subelement);
                        mainMap.put(subelement, subLink.toString());
                    }
        
                }
    
                return mainMap;
            }
            return new HashMap<>();
        }
    
    
    
    
}
