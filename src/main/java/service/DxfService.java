package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Dxf;
import module.FileSortComparator;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class DxfService {
    private final TreeMap<String, String> prefixMap;
    private final String serverLocation;
    
    public DxfService() {
        prefixMap = getPrefixes();
        serverLocation = loadServerPathToDxfs();
    }
    
    public Dxf getDxf(String partNumber) {
        String formattedPartNumber = formatPartNumber(partNumber);
        Dxf dxf = new Dxf();
        
        int count = 0;
        for (String drawingFile : fileContents(partNumber)) {
            if (drawingFile.startsWith(formattedPartNumber)) {
                count++;
            }
        }
        if (count > 0) {
            dxf.setPartNumber(partNumber);
            dxf.setFileLocation(getLink(partNumber));
            dxf.setFamilyTree(familyTableBuilder(partNumber));
        }
        else
            dxf.setPartNumber("-1");
        
        return dxf;
    }
    
    private String getLink(String formattedPartNumber) {
        StringBuilder link = new StringBuilder();
        link.append(serverLocation);
        link.append("\\");
        link.append(prefixMap.get(formattedPartNumber.substring(0, 2)));
        link.append("\\");
        link.append(formattedPartNumber.toLowerCase());
        link.append(".dxf");
        System.out.println(link);
        return link.toString();
    }
    
    private ArrayList<String> fileContents(String formattedDrawingNumber) {
        StringBuilder link = new StringBuilder();
        link.append(serverLocation);
        link.append("\\");
        link.append(prefixMap.get(formattedDrawingNumber.substring(0, 2)));
        
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
    private TreeMap<String, String> familyTableBuilder(String formattedPartNumber) {
        // Order dxf's correctly
        FileSortComparator comparator = new FileSortComparator();
        TreeMap<String, String> mainMap = new TreeMap<>(comparator);
        StringBuilder link = new StringBuilder();
        link.append(serverLocation);
        link.append("\\");
        link.append(prefixMap.get(formattedPartNumber.substring(0, 2)));
        File tempFile = new File(link.toString());
        String[] templist;
        templist = tempFile.list();
        
        if (tempFile.exists()) {
            for (String subelement : templist) {
                if (subelement.startsWith(formattedPartNumber) && subelement.toLowerCase().endsWith("dxf")) {
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
    
    private String formatPartNumber(String partNumber){
        if (partNumber.length() < 6) {
            return  "-1";
        } else {
            return partNumber;
        }
    }
    
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
    
}
