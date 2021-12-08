package modules;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PartsByTruck {
    private TreeMap<String, List<String>> partsMap;
    
    public PartsByTruck (){
        FileSortComparator comparator = new FileSortComparator();
        partsMap = new TreeMap<>(comparator.reversed());
    }
    
    public PartsByTruck (String fileName) throws IOException {
        String myJson = new String(Files.readAllBytes(Paths.get(fileName)));
    
        partsMap = new Gson().fromJson(myJson, new TypeToken<HashMap<String, List<String>>>(){}.getType());
    }
    
    public void add(String fileName) throws IOException{
        String myJson = new String(Files.readAllBytes(Paths.get(fileName)));
        TreeMap<String, List<String>> importMap = new Gson().fromJson(myJson, new TypeToken<TreeMap<String, List<String>>>(){}.getType());
        
        for (String key: importMap.keySet()){
            if (!(partsMap.get(key) == null)){
                List<String> newList = new ArrayList<>();
                newList.addAll(partsMap.get(key));
                newList.addAll(importMap.get(key));
                partsMap.put(key, newList);
            } else {
                partsMap.put(key, importMap.get(key));
            }
        }
    }
    
    public void addAll (List<String> fileNames) throws IOException {
        for (String file: fileNames){
            this.add(file);
        }
    }
    
    public void addAllFiles (List<File> files) throws IOException {
        for (File file: files){
            System.out.println("Adding file:" + file.getAbsolutePath());
            this.add(file.getAbsolutePath());
        }
    }
    
    public void printMap(){
        System.out.println(partsMap);
    }
    
    public List<String> trucksWithParts(List<String> partNumbers){
        
        for (int i = 0; i < partNumbers.size(); i++){
            partNumbers.set(i, partNumbers.get(i).trim());
            if (!partNumbers.get(i).endsWith("d")){
                partNumbers.set(i, partNumbers.get(i) + "d");
            }
        }
        
        //Need to merge, int overriding rr ext
        List<String> starterList = trucksWithPart(partNumbers.get(0));
        
        for (String part: partNumbers){
            starterList.retainAll(trucksWithPart(part));
        }
        return starterList;
    }
    
    public List<String> trucksWithPart(String partNumber){
        if (!partNumber.toLowerCase().endsWith("d")){
            partNumber = partNumber + "d";
        }
        List<String> returnList = new ArrayList<>();
        for (String key : partsMap.keySet()) {
            List<String> valueList = partsMap.get(key);
            for (String value : valueList) {
                if (value.toLowerCase().equals(partNumber.toLowerCase())) {
                    returnList.add(key);
                }
            }
        }
        
        return returnList;
    }
}
