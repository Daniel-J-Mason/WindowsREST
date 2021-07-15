package application;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class DrawingFinder {
    private String drawing;
    private HashMap<String, String> drawingLocation;
    
    /**
     * This takes a drawing string of any length then makes sure it ends with drawingString + "d";
     * drawingLocation pulls the list from resources using getPrefixes() method.
     */
    public DrawingFinder(String string){
        if (string.endsWith("D") || (string.endsWith("d")))
            drawing = string.toLowerCase();
        else
            this.drawing = string + "d";
        
        drawingLocation = getPrefixes();
    }
    
    /**
     * Checks the file the drawing should be located in to see if its in the drawings list
     * returns null if it doesn't find a match
     *  and returns the String drawing number if there is a match
     */
    public String getDrawing() {
        int count = 0;
        for (String drawingFile: fileContents()){
            if (drawingFile.startsWith(drawing)){
                count++;
            }
        }
        if (count > 0)
            return drawing;
        else
            return null;
    }
    
    /**
     * Creates the hyperlink String for the drawing
     * @return drawing hyperlink
     */
    public String getLink(){
        StringBuilder link = new StringBuilder();
        link.append(getServerPathToDrawings());
        link.append("/");
        link.append(drawingLocation.get(drawing.substring(0, 2)));
        link.append("/");
        link.append(drawing.toLowerCase());
        link.append(".pdf");
        
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
        link.append(drawingLocation.get(drawing.substring(0, 2)));
        
        File file = new File(link.toString());
        System.out.println("Searching " + link);
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
    
    
    
    
    
    
}
