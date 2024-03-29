package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import module.FileDatabase;
import service.PartsByTruck;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ContainmentPageController {
    
    //TODO: refactor to match Service update on main
    public Button searchButton;
    public CheckBox wheeledCoachBox;
    public CheckBox roadRescueBox;
    public CheckBox metroMedicBox;
    public CheckBox type1Box;
    public CheckBox type3Box;
    public CheckBox type4Box;
    public CheckBox type6Box;
    public CheckBox type7Box;
    public CheckBox type9Box;
    public CheckBox toggleBox;
    public TextField partNumberField;
    public Label errorLabel;
    public TextFlow displayField;

    private final List<CheckBox> brandCheckboxes = new ArrayList<>();
    private final List<CheckBox> typeCheckboxes = new ArrayList<>();
    
    private final String queryFolder = "\\\\WPFFILE1.REVGINC.NET\\AMB-WPF_ENG_Working\\Working Folder - Unshipped Units" +
            "\\4. Personal Working Folder\\Daniel Mason\\Scripts\\Critical Location Folder\\QueryableData";
    
    private final HashMap<CheckBox, String> checkBoxFiles = new HashMap<>();
    private final FileDatabase downloader = new FileDatabase();
    private final FileDatabase transmittalDownloader = new FileDatabase();
    private final FileDatabase workOrderDownloader = new FileDatabase();
    
    HostServices hostServices;
    
    public void initialize() throws IOException {
        HashMap<String, String> locations = fileLocationsHashmap();
        downloader.setLocation(locations.get("TruckLocations"), "/locations/truckLocations.txt");
        transmittalDownloader.setLocation(locations.get("TransmittalLocations"), "/locations/transmittalLocations.txt");
        workOrderDownloader.setLocation(locations.get("WorkOrderLocations"), "/locations/workOrderLocations.txt");
        
        //Marry checkboxes to strings
        checkBoxFiles.put(wheeledCoachBox, "WHEELED_COACH");
        checkBoxFiles.put(roadRescueBox, "ROAD_RESCUE");
        checkBoxFiles.put(metroMedicBox, "METROMEDIC");
        checkBoxFiles.put(type1Box, "Type 1");
        checkBoxFiles.put(type3Box, "Type 3");
        checkBoxFiles.put(type4Box, "Type 4");
        checkBoxFiles.put(type6Box, "Type 6");
        checkBoxFiles.put(type7Box, "Type 7");
        checkBoxFiles.put(type9Box, "Type 9");
        
        //Box groups for iterating
        brandCheckboxes.add(wheeledCoachBox);
        brandCheckboxes.add(roadRescueBox);
        brandCheckboxes.add(metroMedicBox);
        typeCheckboxes.add(type1Box);
        typeCheckboxes.add(type3Box);
        typeCheckboxes.add(type4Box);
        typeCheckboxes.add(type6Box);
        typeCheckboxes.add(type7Box);
        typeCheckboxes.add(type9Box);
        
        for (CheckBox box: brandCheckboxes){
            box.setSelected(true);
        }
        
        for (CheckBox box: typeCheckboxes){
            box.setSelected(true);
        }
        
        toggleBox.setSelected(true);

    }
    
    public void setGetHostController(HostServices hostServices){
        this.hostServices=hostServices;
    }

    private void refresh() throws IOException {
        displayField.getChildren().clear();
        PartsByTruck queryObject = new PartsByTruck();
        File folder = new File(queryFolder);
        List<String> brands = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<File> postedFiles = new ArrayList<>();
        
        //Collect the brands
        for (CheckBox box: brandCheckboxes){
            if(box.isSelected()){
                brands.add(checkBoxFiles.get(box));
            }
        }
    
        //Collect the types
        for (CheckBox box: typeCheckboxes){
            if(!box.isSelected()){
                types.add(checkBoxFiles.get(box));
            }
        }
        
        //Additively create a list of jsons to pull based on brands
        for (File file: Objects.requireNonNull(folder.listFiles())){
            for (String brand: brands){
                if (file.getName().contains(brand)){
                    postedFiles.add(file);
                }
            }
        }
        
        //Remove the unchecked types from the created files
        List<File> removalList = new ArrayList<>();
        
        for (File file: postedFiles) {
            for (String type : types) {
                if (file.getName().contains(type)) {
                    removalList.add(file);
                }
            }
        }
        postedFiles.removeAll(removalList);
        System.out.println("Pulling parts from: ");
        for (File file: postedFiles){
            System.out.println(file.getName());
        }
        
        //Add all remaining files to the searchtool
        queryObject.addAllFiles(postedFiles);
        
        //System.out.println(queryObject.trucksWithPart(partNumberField.getText()));
        
        List<String> multiPartList = Arrays.asList(partNumberField.getText().split(","));
        
        List<String> displayList = new ArrayList<>(queryObject.trucksWithParts(multiPartList));
        for (String truck: displayList){
            linkByTruck(truck);
        }
    }
    
    
    
    public void linkByTruck(String truckName){
        List<String> fullNameFolders = downloader.autoCompleteList();
        List<String> fullNameTransmittal = transmittalDownloader.autoCompleteList();
        List<String> workOrders = workOrderDownloader.autoCompleteList();
        String folderLinkName = null;
        for (String name: fullNameFolders){
            if (name.startsWith(truckName)){
                folderLinkName = name;
            }
        }
        
        String transmittalLinkName = null;
        for (String name: fullNameTransmittal){
            if (name.startsWith(truckName)){
                transmittalLinkName = name;
            }
        }
        
        String workOrderLinkName = null;
        for (String name: workOrders){
            if (name.startsWith(truckName)){
                workOrderLinkName = name;
            }
        }
        
        if (downloader.getLink(folderLinkName) == null) {
            errorLabel.setText("Error: no unit files found!");
        } else {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem transmittalLink = new MenuItem("Transmittal");
            MenuItem workOrderLink = new MenuItem("Work Order");
            Hyperlink link = new Hyperlink(folderLinkName);
            String finalFolderLinkName = folderLinkName;
            link.setOnAction(actionEvent1 -> {
                hostServices.showDocument(downloader.getLink(finalFolderLinkName));
                link.setVisited(false);
            });
            String finalTransmittalLinkName = transmittalLinkName;
            transmittalLink.setOnAction(actionEvent2 -> {
                        hostServices.showDocument(transmittalDownloader.getLink(finalTransmittalLinkName));
                        link.setVisited(false);
                    });
            String finalWorkOrderLinkName = workOrderLinkName;
            workOrderLink.setOnAction(actionEvent3 -> {
                hostServices.showDocument(workOrderDownloader.getLink(finalWorkOrderLinkName));
            });
            contextMenu.getItems().add(transmittalLink);
            contextMenu.getItems().add(workOrderLink);
            link.setContextMenu(contextMenu);
            displayField.getChildren().add(link);
            displayField.getChildren().add(new Text("\n"));
            errorLabel.setText("");
        }
    }
    
    public void checkBox(ActionEvent actionEvent) throws IOException {
        
        refresh();
        
    }
    
    public void toggleAll(ActionEvent actionEvent) throws IOException {
        for (CheckBox box: brandCheckboxes){
            box.setSelected(toggleBox.isSelected());
        }
        for (CheckBox box: typeCheckboxes){
            box.setSelected(toggleBox.isSelected());
        }
        refresh();
    }
    
    public void searchButtonAction() throws IOException {
        refresh();
    }
    
    public void enterPartNumber(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode().equals(KeyCode.ENTER)){
            refresh();
        }
    }
    
    public HashMap<String, String> fileLocationsHashmap() throws IOException {
        HashMap<String, String> locations;
        InputStream inputStream = getClass().getResourceAsStream("/locations/FileLocations.json");
        String myJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        locations = new Gson().fromJson(myJson, new TypeToken<HashMap<String, String>>(){}.getType());
        return locations;
    }
}
