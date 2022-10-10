package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Drawing;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DrawingService {
    private final HashMap<String, String> drawingPrefixMap;
    
    public DrawingService(){
        drawingPrefixMap = getPrefixes();
    }
    
    public Drawing getDrawing(String partNumber) {
        String formattedPartNumber = formatPartNumber(partNumber);
        Drawing drawing = new Drawing();
        drawing.setPartNumber(formattedPartNumber);
        
        int count = 0;
        for (String drawingFile: fileContents(formattedPartNumber)){
            if (drawingFile.toLowerCase().startsWith(formattedPartNumber)){
                count++;
                drawing.setFileLocation(getLink(drawingFile));
            }
        }
        if (count > 0)
            return drawing;
        else
            return null;
    }

    private String getLink(String partNumber){
        String formattedPartNumber = formatPartNumber(partNumber);
        
        StringBuilder link = new StringBuilder();
        link.append(getServerPathToDrawings());
        link.append("\\");
        link.append(drawingPrefixMap.get(formattedPartNumber.substring(0, 2)));
        link.append("\\");
        link.append(formattedPartNumber.toLowerCase());
        
        return link.toString();
    }

    private ArrayList<String> fileContents(String partNumber){
        String formattedPartNumber = formatPartNumber(partNumber);
        
        StringBuilder link = new StringBuilder();
        link.append(getServerPathToDrawings());
        link.append("\\");
        link.append(drawingPrefixMap.get(formattedPartNumber.substring(0, 2)));
        
        File file = new File(link.toString());
        if (file.exists()) {
            ArrayList<String> pathName = new ArrayList<>(Arrays.asList(Objects.requireNonNull(file.list())));
            pathName.stream()
                    .forEach(String::toLowerCase);
    
            return pathName;
        }
        return new ArrayList<>();
    }
    
    private String getServerPathToDrawings() {
        HashMap<String, String> locations;
        InputStream inputStream = getClass().getResourceAsStream("/FileLocations.json");
        String myJson = null;
        try {
            myJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        locations = new Gson().fromJson(myJson, new TypeToken<HashMap<String, String>>(){}.getType());
    
        return locations.get("Drawings");
    }
    
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
    
    private String formatPartNumber(String partNumber){
        if (partNumber.endsWith("D") || (partNumber.endsWith("d")))
            return partNumber.toLowerCase();
        else
            return partNumber + "d";
    };
}
