package controller;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.HostServices;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import service.Drawing;
import service.Dxf;
import service.FileDatabase;
import org.apache.commons.io.IOUtils;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    
    private final FileDatabase truckDatabase = new FileDatabase();
    private final FileDatabase archiveTruckDatabase = new FileDatabase();
    private final FileDatabase transmittalDatabase = new FileDatabase();
    private final FileDatabase workOrderDatabase = new FileDatabase();
    private ArrayList<String> truckAutocompleteArray;
    private ArrayList<String> archiveAutocompleteArray;
    
    public HostServices hostServices;
    
    
    public void initialize() throws IOException {
        HashMap<String, String> locations = fileLocationsHashmap();
        truckDatabase.setLocation(locations.get("TruckLocations"), "/truckLocations.txt");
        archiveTruckDatabase.setLocation(locations.get("TruckArchiveLocations"), "/archiveTruckLocations.txt");
        transmittalDatabase.setLocation(locations.get("TransmittalLocations"), "/transmittalLocations.txt");
        workOrderDatabase.setLocation(locations.get("WorkOrderLocations"), "/workOrderLocations.txt");
        
        truckAutocompleteArray = truckDatabase.autoCompleteList();
        archiveAutocompleteArray = archiveTruckDatabase.autoCompleteList();
        
        TextFields.bindAutoCompletion(truckSearchField, truckAutocompleteArray);
        TextFields.bindAutoCompletion(archiveTruckSearchField, archiveAutocompleteArray);
        
        ToggleGroup searchToggleGroup = new ToggleGroup();
        truckRadioButton.setToggleGroup(searchToggleGroup);
        workOrderRadioButton.setToggleGroup(searchToggleGroup);
        transmittalRadioButton.setToggleGroup(searchToggleGroup);
        truckRadioButton.setSelected(true);
    }
    
    public void drawingFieldKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            drawingSearch();
        }
    }
    
    public void drawingSearchClicked() {
        drawingSearch();
    }
    
    public void containmentClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/containmentPage.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ContainmentPageController controller = loader.getController();
        controller.setGetHostController(hostServices);
        assert root != null;
        Scene scene = new Scene(root);
        Stage containmentStage = new Stage();
        containmentStage.setScene(scene);
        containmentStage.getIcons().add(new Image(Objects.requireNonNull(ContainmentPageController.class.getResourceAsStream("/images/REST-ICO.png"))));
        containmentStage.setTitle("REST Units by Parts");
        containmentStage.show();
    }
    
    public void dxfFieldKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            dxfSearch();
        }
    }
    
    public void dxfSearchClicked() {
        dxfSearch();
    }
    
    public void truckFieldKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            truckSearch();
        }
    }
    
    public void truckSearchClicked() {
        truckSearch();
    }
    
    public void archiveTruckFieldKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            archiveTruckSearch();
        }
    }
    
    public void archiveTruckSearchClicked() {
        archiveTruckSearch();
    }
    
    public void openMasterFiles() {
        hostServices.showDocument("file:\\\\WPFFILE1.REVGINC.NET\\AMB-WPF_ENG_Operations\\2004 Master Files");
    }
    
    public void refreshClicked() {
        workOrderDatabase.refresh();
        transmittalDatabase.refresh();
        truckDatabase.refresh();
        truckAutocompleteArray.clear();
        truckAutocompleteArray.addAll(truckDatabase.autoCompleteList());
        archiveTruckDatabase.refresh();
        archiveAutocompleteArray.clear();
        archiveAutocompleteArray.addAll(archiveTruckDatabase.autoCompleteList());
    }
    
    public void clearAllClicked() {
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
    }
    
    
    public void questionLinkClicked() {
        hostServices.showDocument("\\\\WPFFILE1.REVGINC.NET\\AMB-WPF_ENG_Working\\" +
                "Working Folder - Unshipped Units\\4. Personal Working Folder" +
                "\\Daniel Mason\\R.E.S.T. Documentation");
        questionsLink.setVisited(false);
    }
    
    public void drawingSearch() {
        String drawingNumber = drawingSearchField.getText();
        Drawing drawing = new Drawing(drawingNumber);
        if (drawing.getDrawing() == null) {
            drawingSearchErrorLabel.setText("Error: " + drawing + " not found!");
        } else {
            hostServices.showDocument(drawing.getLink());
            Hyperlink link = hyperLinkWithOpenLocation(drawingNumber, drawing.getLink());
            drawingHistoryTextFlow.getChildren().add(link);
            drawingHistoryTextFlow.getChildren().add(new Text("\n"));
            drawingSearchField.setText("");
            drawingSearchErrorLabel.setText("");
        }
    }
    
    public void dxfSearch() {
        
        dxfFamilyTextFlow.getChildren().clear();
        String dxf = dxfSearchField.getText();
        Dxf searcher = new Dxf(dxf);
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
    
    // Adds a link to history and Launches one of the selected options
    public void truckSearch() {
        
        String truckFileName = truckSearchField.getText();
        String truckNumber = truckFileName.substring(0, 6);
        
        if (truckDatabase.getLink(truckFileName) == null) {
            truckSearchErrorLabel.setText("Error: " + truckFileName + " not found!");
        } else {
            Hyperlink truckLinkWithContext = truckLinkNode(truckFileName);
            
            if (truckRadioButton.isSelected()) {
                hostServices.showDocument(truckDatabase.getLink(truckFileName));
            } else if (workOrderRadioButton.isSelected()) {
                hostServices.showDocument(getFullWorkOrderFilePath(truckNumber));
            } else {
                hostServices.showDocument(getFullTransmittalFilePath(truckNumber));
            }
            truckHistoryTextFlow.getChildren().add(truckLinkWithContext);
            truckHistoryTextFlow.getChildren().add(new Text("\n"));
            truckSearchField.setText("");
            truckSearchErrorLabel.setText("");
        }
        
    }
    
    public void archiveTruckSearch() {
        String truck = archiveTruckSearchField.getText();
        if (archiveTruckDatabase.getLink(truck) == null) {
            archiveTruckSearchErrorLabel.setText("Error: " + truck + " not found!");
        } else {
            String archiveTruckFilePath = archiveTruckDatabase.getLink(truck);
            Hyperlink link = hyperLinkNode(truck, archiveTruckFilePath);
            truckHistoryTextFlow.getChildren().add(link);
            truckHistoryTextFlow.getChildren().add(new Text("\n"));
            archiveTruckSearchField.setText("");
            archiveTruckSearchErrorLabel.setText("");
        }
    }
    
    private Hyperlink truckLinkNode(String truckFilename) {
        String truckNumber = truckFilename.substring(0, 6);
        Hyperlink mainLink = hyperLinkNode(truckFilename, truckDatabase.getLink(truckFilename));
        ContextMenu contextMenu = new ContextMenu();
        String transmittalFilePath = getFullTransmittalFilePath(truckNumber);
        String workOrderFilePath = getFullWorkOrderFilePath(truckNumber);
        
        
        contextMenu.getItems().add(menuItemLink("Transmittal", transmittalFilePath));
        contextMenu.getItems().add(menuItemLink("Work Order", workOrderFilePath));
        
        mainLink.setContextMenu(contextMenu);
        return mainLink;
    }
    
    private MenuItem menuItemLink(String name, String link) {
        MenuItem menuItem = new MenuItem(name);
        EventHandler<MouseEvent> mouseHandler = hyperLinkHandler(link);
        menuItem.addEventHandler(MouseEvent.ANY, mouseHandler);
        return menuItem;
    }
    
    private Hyperlink hyperLinkNode(String name, String link) {
        Hyperlink hyperlink = new Hyperlink(name);
        hyperlink.setOnAction(actionEvent -> {
            hostServices.showDocument(link);
            hyperlink.setVisited(false);
        });
        
        return hyperlink;
    }
    
    private Hyperlink hyperLinkWithOpenLocation(String name, String link) {
        Hyperlink hyperlink = new Hyperlink(name);
        EventHandler<MouseEvent> mouseHandler = hyperLinkHandler(link);
        hyperlink.addEventHandler(MouseEvent.ANY,mouseHandler);
        return hyperlink;
}
    
    private String getFullTransmittalFilePath(String truckNumber) {
        Optional<String> filePath =
                transmittalDatabase.autoCompleteList().stream()
                        .filter(fileName -> fileName.startsWith(truckNumber))
                        .findAny();
        if (filePath.isEmpty()) {
            return null;
        } else {
            return filePath.toString();
        }
    }
    
    private String getFullWorkOrderFilePath(String truckNumber) {
        Optional<String> filePath =
                workOrderDatabase.autoCompleteList().stream()
                        .filter(fileName -> fileName.startsWith(truckNumber))
                        .findAny();
        if (filePath.isEmpty()) {
            return null;
        } else {
            return filePath.toString();
        }
    }
    
    private EventHandler<MouseEvent> hyperLinkHandler(String link) {
        return event -> {
            Hyperlink hostLink = (Hyperlink) event.getSource();
            if (event.isPrimaryButtonDown()) {
                hostServices.showDocument(link);
                hostLink.setVisited(false);
            } else if (event.isSecondaryButtonDown()) {
                try {
                    Runtime.getRuntime().exec("explorer /select, " + link);
                    hostLink.setVisited(false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    
    public void setGetHostController(HostServices hostServices) {
        this.hostServices = hostServices;
    }
    
    public HashMap<String, String> fileLocationsHashmap() throws IOException {
        HashMap<String, String> locations;
        InputStream inputStream = getClass().getResourceAsStream("/FileLocations.json");
        assert inputStream != null;
        String myJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        locations = new Gson().fromJson(myJson, new TypeToken<HashMap<String, String>>() {
        }.getType());
        return locations;
    }
}
