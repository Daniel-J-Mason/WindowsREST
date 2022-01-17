package Controllers;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import modules.DrawingFinder;
import modules.DxfFinder;
import modules.serverMappingFrontloader;
import org.apache.commons.io.IOUtils;
import org.controlsfx.control.textfield.TextFields;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class MainViewController {
    public TextField drawingSearchField;
    public Button drawingSearchButton;
    public Label drawingSearchErrorLabel;
    public TextField dxfSearchField;
    public Button dxfSearchButton;
    public Label dxfSearchErrorLabel;
    public TextField truckSearchField;
    public Button truckSearchButton;
    public RadioButton truckRadioButton;
    public RadioButton workOrderRadioButton;
    public RadioButton transmittalRadioButton;
    public Label truckSearchErrorLabel;
    public TextField archiveTruckSearchField;
    public Button archiveTruckSearchButton;
    public Label archiveTruckSearchErrorLabel;
    public ImageView imageView;
    public ScrollPane dxfFamilyPane;
    public TextFlow dxfFamilyTextFlow;
    public Button refreshButton;
    public Button clearAllButton;
    public Hyperlink questionsLink;
    public ScrollPane drawingHistoryPane;
    public TextFlow drawingHistoryTextFlow;
    public ScrollPane dxfHistoryPane;
    public TextFlow dxfHistoryTextFlow;
    public ScrollPane truckHistoryPane;
    public TextFlow truckHistoryTextFlow;
    public Button containment;
    public Button miscSearchButton;
    
    private final serverMappingFrontloader downloader = new serverMappingFrontloader();
    private final serverMappingFrontloader archiveDownloader = new serverMappingFrontloader();
    private final serverMappingFrontloader transmittalDownloader = new serverMappingFrontloader();
    private final serverMappingFrontloader workOrderDownloader = new serverMappingFrontloader();
    private ArrayList<String> truckAutocompleteArray;
    
    public HostServices hostServices;

    
    
    public void initialize() throws IOException {
        HashMap<String, String> locations = fileLocationsHashmap();
        downloader.setLocation(locations.get("TruckLocations"), "/truckLocations.txt");
        archiveDownloader.setLocation(locations.get("TruckArchiveLocations"), "/archiveTruckLocations.txt");
        transmittalDownloader.setLocation(locations.get("TransmittalLocations"), "/transmittalLocations.txt");
        workOrderDownloader.setLocation(locations.get("WorkOrderLocations"), "/workOrderLocations.txt");
        
        truckAutocompleteArray = downloader.autoCompleteList();
        ArrayList<String> archiveAutocompleteArray = archiveDownloader.autoCompleteList();
        
        TextFields.bindAutoCompletion(truckSearchField, truckAutocompleteArray);
        
        TextFields.bindAutoCompletion(archiveTruckSearchField, archiveAutocompleteArray);
        
        ToggleGroup searchToggleGroup = new ToggleGroup();
        truckRadioButton.setToggleGroup(searchToggleGroup);
        workOrderRadioButton.setToggleGroup(searchToggleGroup);
        transmittalRadioButton.setToggleGroup(searchToggleGroup);
        truckRadioButton.setSelected(true);
        
        
        drawingSearchField.setOnKeyPressed(keyEvent ->{
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                drawingSearch();
                }
        });
        
        drawingSearchButton.setOnAction((actionEvent -> drawingSearch()));
        
        
        containment.setOnAction((actionEvent -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/containmentPage.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ContainmentPage controller = loader.getController();
            controller.setGetHostController(hostServices);
            assert root != null;
            Scene scene = new Scene(root);
            Stage containmentStage = new Stage();
            containmentStage.setScene(scene);
        
            containmentStage.show();
        }));
        
        dxfSearchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                dxfSearch();
            }
        });
        
        dxfSearchButton.setOnAction(keyEvent -> dxfSearch());
        
        truckSearchField.setOnKeyPressed(keyEvent ->{
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                truckSearch();
            }
        });
        
        truckSearchButton.setOnAction((actionEvent -> truckSearch()));
        
        archiveTruckSearchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                archiveTruckSearch();
            }
        });
        
        archiveTruckSearchButton.setOnAction((actionEvent -> archiveTruckSearch()));
    
        refreshButton.setOnAction(actionEvent -> {
            workOrderDownloader.refresh();
            transmittalDownloader.refresh();
            downloader.refresh();
            truckAutocompleteArray.clear();
            truckAutocompleteArray.addAll(downloader.autoCompleteList());
            archiveDownloader.refresh();
            archiveAutocompleteArray.clear();
            archiveAutocompleteArray.addAll(archiveDownloader.autoCompleteList());
        });
    
        clearAllButton.setOnAction(actionEvent -> {
            drawingSearchField.setText("");
            drawingSearchErrorLabel.setText("");
            dxfSearchField.setText("");
            dxfSearchErrorLabel.setText("");
            truckSearchField.setText("");
            truckSearchErrorLabel.setText("");
            archiveTruckSearchField.setText("");
            archiveTruckSearchErrorLabel.setText("");
            dxfFamilyTextFlow.getChildren().clear();
            drawingHistoryTextFlow.getChildren().clear();
            dxfHistoryTextFlow.getChildren().clear();
            truckHistoryTextFlow.getChildren().clear();
        });
    
        questionsLink.setOnAction(actionEvent -> {
            hostServices.showDocument("\\\\WPFFILE1.REVGINC.NET\\AMB-WPF_ENG_Working\\" +
                    "Working Folder - Unshipped Units\\4. Personal Working Folder" +
                    "\\Daniel Mason\\R.E.S.T. Documentation");
            questionsLink.setVisited(false);
        });
    

    }
    
    public void setGetHostController(HostServices hostServices){
        this.hostServices=hostServices;
    }
    
    public HashMap<String, String> fileLocationsHashmap() throws IOException {
        HashMap<String, String> locations;
        InputStream inputStream = getClass().getResourceAsStream("/FileLocations.json");
        assert inputStream != null;
        String myJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        locations = new Gson().fromJson(myJson, new TypeToken<HashMap<String, String>>(){}.getType());
        return locations;
    }
    
    public void drawingSearch(){
        String drawing = drawingSearchField.getText();
        DrawingFinder searcher = new DrawingFinder(drawing);
        if (searcher.getDrawing() == null) {
            drawingSearchErrorLabel.setText("Error: " + drawing + " not found!");
        } else {
            hostServices.showDocument(searcher.getLink());
            Hyperlink link = new Hyperlink(searcher.getDrawing());
            link.setOnAction(actionEvent1 -> {
                hostServices.showDocument(searcher.getLink());
                link.setVisited(false);
            });
            drawingHistoryTextFlow.getChildren().add(link);
            drawingHistoryTextFlow.getChildren().add(new Text("\n"));
            drawingSearchField.setText("");
            drawingSearchErrorLabel.setText("");
        }
    }
    
    public void dxfSearch(){
    
        dxfFamilyTextFlow.getChildren().clear();
        String dxf = dxfSearchField.getText();
        DxfFinder searcher = new DxfFinder(dxf);
        if (searcher.getDxf().equals("-1")) {
            dxfSearchErrorLabel.setText("Error: " + dxf + " not found!"); //!!!
        } else {
            TreeMap<String, String> familyTable = searcher.getFamilyTable();
            for (String item : familyTable.keySet()) {
                Hyperlink instanceLink = new Hyperlink(item);
                dxfFamilyTextFlow.getChildren().add(instanceLink);
                instanceLink.setOnAction(actionEvent -> {
                    hostServices.showDocument(familyTable.get(item));
                    Hyperlink separateCopyOfInstanceLink = new Hyperlink(item);
                    dxfHistoryTextFlow.getChildren().add(separateCopyOfInstanceLink);
                    dxfHistoryTextFlow.getChildren().add(new Text("\n"));
                    separateCopyOfInstanceLink.setOnAction(actionEvent1 -> {
                        hostServices.showDocument(familyTable.get(item));
                        separateCopyOfInstanceLink.setVisited(false);
                    });
                    instanceLink.setVisited(false);
                });
                dxfFamilyTextFlow.getChildren().add(new Text("\n"));
            }
            dxfSearchErrorLabel.setText("");
        }
        dxfSearchField.setText("");
    
    }
    
    public void truckSearch(){
    
        String truck = truckSearchField.getText();
        if (downloader.getLink(truck) == null) {
            truckSearchErrorLabel.setText("Error: " + truck + " not found!");
        } else {
            Hyperlink link = new Hyperlink(truck);
            ContextMenu contextMenu = new ContextMenu();
            MenuItem transmittalLink = new MenuItem("Transmittal");
            MenuItem workOrderLink = new MenuItem("Work Order");
            link.setOnAction(actionEvent1 -> {
                hostServices.showDocument(downloader.getLink(truck));
                link.setVisited(false);
            });
            
            List<String> fullNameTransmittal = transmittalDownloader.autoCompleteList();
            List<String> workOrders = workOrderDownloader.autoCompleteList();
            String transmittalLinkName = null;
            for (String name: fullNameTransmittal){
                if (name.startsWith(truck.substring(0, 6))){
                    transmittalLinkName = name;
                }
            }
    
            String workOrderLinkName = null;
            for (String name: workOrders){
                if (name.startsWith(truck.substring(0, 6))){
                    workOrderLinkName = name;
                }
            }
            
            String finalTransmittalLinkName = transmittalLinkName;
            transmittalLink.setOnAction(actionEvent2 -> {
                hostServices.showDocument(transmittalDownloader.getLink(finalTransmittalLinkName));
                link.setVisited(false);
            });
            String finalWorkOrderLinkName = workOrderLinkName;
            workOrderLink.setOnAction(actionEvent3 -> {
                hostServices.showDocument(workOrderDownloader.getLink(finalWorkOrderLinkName));
            });
            
            if (truckRadioButton.isSelected()){
                hostServices.showDocument(downloader.getLink(truck));
            } else if (workOrderRadioButton.isSelected()) {
                hostServices.showDocument(workOrderDownloader.getLink(finalWorkOrderLinkName));
            } else {
                hostServices.showDocument(transmittalDownloader.getLink(finalTransmittalLinkName));
            }
            
            contextMenu.getItems().add(transmittalLink);
            contextMenu.getItems().add(workOrderLink);
            link.setContextMenu(contextMenu);
            truckHistoryTextFlow.getChildren().add(link);
            truckHistoryTextFlow.getChildren().add(new Text("\n"));
            truckSearchField.setText("");
            truckSearchErrorLabel.setText("");
        }
    
    }
    
    public void archiveTruckSearch(){
        String truck = archiveTruckSearchField.getText();
        if (archiveDownloader.getLink(truck) == null) {
            archiveTruckSearchErrorLabel.setText("Error: " + truck + " not found!");
        } else {
            hostServices.showDocument(archiveDownloader.getLink(truck));
            Hyperlink link = new Hyperlink(truck);
            link.setOnAction(actionEvent1 ->{
                hostServices.showDocument(archiveDownloader.getLink(truck));
                link.setVisited(false);
            });
            truckHistoryTextFlow.getChildren().add(link);
            truckHistoryTextFlow.getChildren().add(new Text("\n"));
            archiveTruckSearchField.setText("");
            archiveTruckSearchErrorLabel.setText("");
        }
    }
    
    public void openMasterFiles() {
        hostServices.showDocument("file:\\\\WPFFILE1.REVGINC.NET\\AMB-WPF_ENG_Operations\\2004 Master Files");
    }
}
