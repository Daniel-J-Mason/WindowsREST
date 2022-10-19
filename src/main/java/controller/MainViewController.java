package controller;

import entity.Drawing;
import entity.Dxf;
import entity.Truck;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    public TextField workOrderSearchField;
    public CheckBox archiveCheckBox;
    
    private final DrawingService drawingService = new DrawingService();
    private final DxfService dxfService = new DxfService();
    private final WorkOrderService workOrderService = new WorkOrderService();
    
    private final TruckService truckService =
            new TruckService("TruckLocations", "/locations/truckLocations.txt",
                    "TransmittalLocations", "/locations/transmittalLocations.txt",
                    "WorkOrderLocations", "/locations/workOrderLocations.txt");
    private final TruckService archiveTruckService =
            new TruckService("TruckArchiveLocations", "/locations/archiveTruckLocations.txt",
                    "TransmittalArchiveLocations", "/locations/archiveTransmittalLocations.txt",
                    "WorkOrderLocations", "/locations/archiveWorkOrderLocations.txt");
    public RadioMenuItem darkModeButton;
    
    private ArrayList<String> truckAutocompleteArray;
    private ArrayList<String> archiveAutocompleteArray;
    private ArrayList<String> workOrderAutocompleteArray;
    
    SuggestionProvider<String> truckSuggestionProvider;
    SuggestionProvider<String> workOrderSuggestionProvider;
    
    public HostServices hostServices;
    
    
    public void initialize() {
        truckAutocompleteArray = truckService.getAutoCompleteList();
        archiveAutocompleteArray = archiveTruckService.getAutoCompleteList();
        workOrderAutocompleteArray = workOrderService.getWorkOrderList();
        
        truckSuggestionProvider = SuggestionProvider.create(truckAutocompleteArray);
        new AutoCompletionTextFieldBinding<>(truckSearchField, truckSuggestionProvider);
        
        workOrderSuggestionProvider = SuggestionProvider.create(workOrderAutocompleteArray);
        new AutoCompletionTextFieldBinding<>(workOrderSearchField, workOrderSuggestionProvider);
        
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
        if (darkModeButton.isSelected()) {
            scene.getStylesheets().add("/styles/dark-theme.css");
        }
        containmentStage.setResizable(false);
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
            if (archiveCheckBox.isSelected()) {
                archiveTruckSearch();
            } else {
                truckSearch();
            }
        }
    }
    
    public void truckSearchClicked() {
        if (archiveCheckBox.isSelected()) {
            archiveTruckSearch();
        } else {
            truckSearch();
        }
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
        
        if (archiveCheckBox.isSelected()){
            truckSuggestionProvider.clearSuggestions();
            truckSuggestionProvider.addPossibleSuggestions(archiveTruckService.getAutoCompleteList());
        } else {
            truckSuggestionProvider.clearSuggestions();
            truckSuggestionProvider.addPossibleSuggestions(truckService.getAutoCompleteList());
        }
        
        workOrderSuggestionProvider.clearSuggestions();
        workOrderSuggestionProvider.addPossibleSuggestions(workOrderService.getWorkOrderList());
    }
    
    public void clearAllClicked() {
        drawingSearchField.setText("");
        drawingSearchErrorLabel.setText("");
        dxfSearchField.setText("");
        dxfSearchErrorLabel.setText("");
        truckSearchField.setText("");
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
        if (drawing == null) {
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
            for (String fileName : familyTable.keySet()) {
                Hyperlink instanceLink = new Hyperlink(fileName);
                String link = familyTable.get(fileName);
                EventHandler<MouseEvent> eventHandler = dxfHistoryEventHandler(fileName, link);
                instanceLink.addEventHandler(MouseEvent.ANY, eventHandler);
                instanceLink.setOnAction(action -> instanceLink.setVisited(false));
                dxfFamilyTextFlow.getChildren().add(instanceLink);
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
            //truckSearchErrorLabel.setText("Error: " + truckFileName + " not found!");
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
            //truckSearchErrorLabel.setText("");
        }
        
    }
    
    public void archiveTruckSearch() {
        String truckFileName = truckSearchField.getText();
        Truck archiveTruck = archiveTruckService.getTruck(truckFileName);
        
        if (archiveTruck.getFileLocation() == null) {
            //truckSearchErrorLabel.setText("Error: " + truckFileName + " not found!");
        } else {
            Hyperlink truckLinkWithContext = truckLinkNode(archiveTruck);
            
            if (truckRadioButton.isSelected()) {
                hostServices.showDocument(archiveTruck.getFileLocation());
            } else if (workOrderRadioButton.isSelected()) {
                hostServices.showDocument(archiveTruck.getWorkOrderLocation());
            } else {
                hostServices.showDocument(archiveTruck.getTransmittalLocation());
            }
            
            truckHistoryTextFlow.getChildren().add(truckLinkWithContext);
            truckHistoryTextFlow.getChildren().add(new Text("\n"));
            truckSearchField.setText("");
            //truckSearchErrorLabel.setText("");
        }
    }
    
    private void workOrderSearch() {
        String fullWorkOrderName = workOrderSearchField.getText();
        hostServices.showDocument(workOrderService.getLink(fullWorkOrderName));
        workOrderSearchField.clear();
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
        hyperlink.setOnAction(action -> hyperlink.setVisited(false));
        return hyperlink;
    }
    
    private EventHandler<MouseEvent> hyperLinkHandler(String link) {
        return event -> {
            Hyperlink hostLink = (Hyperlink) event.getSource();
            if (event.isPrimaryButtonDown()) {
                hostServices.showDocument(link);
            } else if (event.isSecondaryButtonDown()) {
                try {
                    Runtime.getRuntime().exec("explorer.exe /select, " + link);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    
    // hyperLinkHandler with additional step to add to history
    private EventHandler<MouseEvent> dxfHistoryEventHandler(String fileName, String link){
        return event -> {
            Hyperlink hostLink = (Hyperlink) event.getSource();
            if (event.isPrimaryButtonDown()) {
                hostServices.showDocument(link);
                hostLink.setVisited(false);
                Hyperlink historyLink = hyperLinkWithOpenLocation(fileName, link);
                dxfHistoryTextFlow.getChildren().add(historyLink);
                dxfHistoryTextFlow.getChildren().add(new Text("\n"));
            } else if (event.isSecondaryButtonDown()) {
                try {
                    Runtime.getRuntime().exec("explorer.exe /select, " + link);
                    hostLink.setVisited(false);
                    Hyperlink historyLink = hyperLinkWithOpenLocation(fileName, link);
                    dxfHistoryTextFlow.getChildren().add(historyLink);
                    dxfHistoryTextFlow.getChildren().add(new Text("\n"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        };
    }
    
    public void setGetHostController(HostServices hostServices) {
        this.hostServices = hostServices;
    }
    
    public void darkModeToggle() {
        Node root = truckRadioButton.getScene().getRoot();
        if (darkModeButton.isSelected()){
            root.getScene().getStylesheets().clear();
            root.getScene().getStylesheets().add("/styles/dark-theme.css");
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/REV_LOGO_DARK.png")));
            imageView.setImage(image);
        } else {
            root.getScene().getStylesheets().clear();
            root.getScene().getStylesheets().add("/styles/default-theme.css");
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/REV_LOGO.png")));
            imageView.setImage(image);
        }
    }
    
    public void toggleAutoComplete() {
        if (archiveCheckBox.isSelected()) {
            truckSuggestionProvider.clearSuggestions();
            truckSuggestionProvider.addPossibleSuggestions(archiveAutocompleteArray);
        } else {
            truckSuggestionProvider.clearSuggestions();
            truckSuggestionProvider.addPossibleSuggestions(truckAutocompleteArray);
        }
    }
}
