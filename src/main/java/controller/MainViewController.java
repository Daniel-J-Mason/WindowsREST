package controller;


import entity.Drawing;
import entity.Dxf;
import entity.Truck;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
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
import service.DrawingService;
import service.DxfService;
import org.controlsfx.control.textfield.TextFields;
import service.TruckService;
import service.WorkOrderService;

import java.io.IOException;
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
    public RadioButton archiveTruckRadioButton;
    public RadioButton archiveWorkOrderRadioButton;
    public RadioButton archiveTransmittalRadioButton;
    public TextField workOrderSearchField;
    public CheckBox archiveCheckBox;
    
    private final DrawingService drawingService = new DrawingService();
    private final DxfService dxfService = new DxfService();
    private final WorkOrderService workOrderService = new WorkOrderService();
    
    private final TruckService truckService =
            new TruckService("TruckLocations", "/truckLocations.txt",
                    "TransmittalLocations", "/transmittalLocations.txt",
                    "WorkOrderLocations", "/workOrderLocations.txt");
    private final TruckService archiveTruckService =
            new TruckService("TruckArchiveLocations", "/archiveTruckLocations.txt",
                    "TransmittalArchiveLocations", "/archiveTransmittalLocations.txt",
                    "WorkOrderLocations", "/archiveWorkOrderLocations.txt");
    
    private ArrayList<String> truckAutocompleteArray;
    private ArrayList<String> archiveAutocompleteArray;
    private ArrayList<String> workOrderAutocompleteArray;
    
    public HostServices hostServices;
    
    
    public void initialize() throws IOException {
        
        
        truckAutocompleteArray = truckService.getAutoCompleteList();
        archiveAutocompleteArray = archiveTruckService.getAutoCompleteList();
        workOrderAutocompleteArray = workOrderService.getWorkOrderList();
        
        TextFields.bindAutoCompletion(truckSearchField, truckAutocompleteArray);
        TextFields.bindAutoCompletion(archiveTruckSearchField, archiveAutocompleteArray);
        TextFields.bindAutoCompletion(workOrderSearchField, workOrderAutocompleteArray);
        
        ToggleGroup searchToggleGroup = new ToggleGroup();
        
        truckRadioButton.setToggleGroup(searchToggleGroup);
        workOrderRadioButton.setToggleGroup(searchToggleGroup);
        transmittalRadioButton.setToggleGroup(searchToggleGroup);
        truckRadioButton.setSelected(true);
        
        ToggleGroup archiveSearchToggleGroup = new ToggleGroup();
        archiveTruckRadioButton.setToggleGroup(archiveSearchToggleGroup);
        archiveWorkOrderRadioButton.setToggleGroup(archiveSearchToggleGroup);
        archiveTransmittalRadioButton.setToggleGroup(archiveSearchToggleGroup);
        archiveTruckRadioButton.setSelected(true);
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
    
    public void openMasterFiles() {
        hostServices.showDocument("file:\\\\WPFFILE1.REVGINC.NET\\AMB-WPF_ENG_Operations\\2004 Master Files");
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
    
    public void workOrderSearchKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            workOrderSearch();
        }
    }
    
    public void workOrderSearchClicked(ActionEvent actionEvent) {
        workOrderSearch();
    }
    
    public void refreshClicked() {
        truckService.refreshAllDatabases();
        workOrderService.refresh();
        truckAutocompleteArray.clear();
        truckAutocompleteArray.addAll(truckService.getAutoCompleteList());
        archiveAutocompleteArray.clear();
        archiveAutocompleteArray.addAll(archiveTruckService.getAutoCompleteList());
        workOrderAutocompleteArray.clear();
        workOrderAutocompleteArray.addAll(workOrderService.getWorkOrderList());
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
        Drawing drawing = drawingService.getDrawing(drawingNumber);
        if (drawing.getFileLocation() == null) {
            drawingSearchErrorLabel.setText("Error: " + drawingNumber + " not found!");
        } else {
            hostServices.showDocument(drawing.getFileLocation());
            Hyperlink link = hyperLinkWithOpenLocation(drawing.getPartNumber(), drawing.getFileLocation());
            drawingHistoryTextFlow.getChildren().add(link);
            drawingHistoryTextFlow.getChildren().add(new Text("\n"));
            drawingSearchField.setText("");
            drawingSearchErrorLabel.setText("");
        }
    }
    
    public void dxfSearch() {
        dxfFamilyTextFlow.getChildren().clear();
        String partNumber = dxfSearchField.getText();
        Dxf dxf = dxfService.getDxf(partNumber);
        if (dxf.getPartNumber().equals("-1")) {
            dxfSearchErrorLabel.setText("Error: " + partNumber + " not found!"); //!!!
        } else {
            TreeMap<String, String> familyTable = dxf.getFamilyTree();
            for (String item : familyTable.keySet()) {
                Hyperlink instanceLink = new Hyperlink(item);
                dxfFamilyTextFlow.getChildren().add(instanceLink);
                instanceLink.setOnAction(actionEvent -> {
                    hostServices.showDocument(familyTable.get(item));
                    Hyperlink separateCopyOfInstanceLink = hyperLinkWithOpenLocation(item, familyTable.get(item));
                    dxfHistoryTextFlow.getChildren().add(separateCopyOfInstanceLink);
                    dxfHistoryTextFlow.getChildren().add(new Text("\n"));
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
        Truck truck = truckService.getTruck(truckFileName);
        
        if (truck.getFileLocation() == null) {
            truckSearchErrorLabel.setText("Error: " + truckFileName + " not found!");
        } else {
            Hyperlink truckLinkWithContext = truckLinkNode(truck);
            
            if (truckRadioButton.isSelected()) {
                hostServices.showDocument(truck.getFileLocation());
            } else if (workOrderRadioButton.isSelected()) {
                hostServices.showDocument(truck.getWorkOrderLocation());
            } else {
                hostServices.showDocument(truck.getTransmittalLocation());
            }
            truckHistoryTextFlow.getChildren().add(truckLinkWithContext);
            truckHistoryTextFlow.getChildren().add(new Text("\n"));
            truckSearchField.setText("");
            truckSearchErrorLabel.setText("");
        }
        
    }
    
    public void archiveTruckSearch() {
        String truckFileName = archiveTruckSearchField.getText();
        Truck archiveTruck = archiveTruckService.getTruck(truckFileName);
        
        if (archiveTruck.getFileLocation() == null) {
            archiveTruckSearchErrorLabel.setText("Error: " + truckFileName + " not found!");
        } else {
            Hyperlink truckLinkWithContext = truckLinkNode(archiveTruck);
            
            if (archiveTruckRadioButton.isSelected()) {
                hostServices.showDocument(archiveTruck.getFileLocation());
            } else if (archiveWorkOrderRadioButton.isSelected()) {
                hostServices.showDocument(archiveTruck.getWorkOrderLocation());
            } else {
                hostServices.showDocument(archiveTruck.getTransmittalLocation());
            }
            
            truckHistoryTextFlow.getChildren().add(truckLinkWithContext);
            truckHistoryTextFlow.getChildren().add(new Text("\n"));
            archiveTruckSearchField.setText("");
            archiveTruckSearchErrorLabel.setText("");
        }
    }
    
    private void workOrderSearch() {
        String fullWorkOrderName = workOrderSearchField.getText();
        hostServices.showDocument(workOrderService.getLink(fullWorkOrderName));
    }
    
    private Hyperlink truckLinkNode(Truck truck) {
        Hyperlink mainLink = hyperLinkNode(truck.getTruckFileName(), truck.getFileLocation());
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(menuItemLink("Transmittal", truck.getTransmittalLocation()));
        contextMenu.getItems().add(menuItemLink("Work Order", truck.getWorkOrderLocation()));
        
        mainLink.setContextMenu(contextMenu);
        return mainLink;
    }
    
    private MenuItem menuItemLink(String name, String link) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(actionEvent -> {
            hostServices.showDocument(link);
        });
        return menuItem;
    }
    
    //For Truck Hyperlink since right click is a ContextMenu
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
        hyperlink.addEventHandler(MouseEvent.ANY, mouseHandler);
        return hyperlink;
    }
    
    private EventHandler<MouseEvent> hyperLinkHandler(String link) {
        return event -> {
            Hyperlink hostLink = (Hyperlink) event.getSource();
            if (event.isPrimaryButtonDown()) {
                hostServices.showDocument(link);
                hostLink.setVisited(false);
                System.out.println("My link is " + link);
            } else if (event.isSecondaryButtonDown()) {
                try {
                    Runtime.getRuntime().exec("explorer.exe /select, " + link);
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
    
}
