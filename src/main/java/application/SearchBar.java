package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import modules.ArchiveTruckMapDownloader;
import modules.DrawingFinder;
import modules.DxfFinder;
import modules.TruckMapDownloader;
import org.controlsfx.control.textfield.TextFields;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.TreeMap;

public class SearchBar extends Application{
    /**
     * Main Application. See Logic Web for layout information
     * @param window
     * @throws FileNotFoundException
     */
    @Override
    public void start(Stage window) throws FileNotFoundException {
        window.setTitle("REV Engineering Search Tool");
        window.setMaxHeight(655);
        window.setMaxWidth(1050);
        window.setResizable(false);
        window.getIcons().add(new Image(getClass().getResourceAsStream("/REST.ico")));
        
        TruckMapDownloader downloader = new TruckMapDownloader();
        ArchiveTruckMapDownloader archiveDownloader = new ArchiveTruckMapDownloader();
        
        //1. application.Main boxes
        VBox mainWrap = new VBox();
        GridPane mainGrid = new GridPane();
        mainGrid.setStyle("-fx-grid-lines-visible: true");
        mainWrap.getChildren().add(mainGrid);
        mainWrap.getChildren().add(new Label("By Daniel Mason" +
                "                       daniel.mason@revgroup.com"));
        
        //1.1 gridPane sizing
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMaxHeight(200);
        rowConstraints.setMinHeight(200);
        
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMaxWidth(350);
        columnConstraints.setMinWidth(350);
        for (int i = 1; i < 4; i++) {
            mainGrid.getRowConstraints().add(rowConstraints);
            mainGrid.getColumnConstraints().add(columnConstraints);
        }
        
        //2. GridPane insertables
        VBox topLeft = new VBox();
        VBox middleLeft = new VBox();
        VBox bottomLeft = new VBox();
        
        VBox centerBox = new VBox();
        VBox bottomMiddle = new VBox(30);
        
        VBox topRightBox = new VBox();
        VBox middleRightBox = new VBox();
        VBox bottomRightBox = new VBox();
        
        mainGrid.add(topLeft, 0, 0);
        mainGrid.add(middleLeft, 0, 1);
        mainGrid.add(bottomLeft, 0, 2);
        mainGrid.add(centerBox, 1, 1);
        mainGrid.add(bottomMiddle, 1, 2);
        mainGrid.add(topRightBox, 2, 0);
        mainGrid.add(middleRightBox, 2, 1);
        mainGrid.add(bottomRightBox, 2, 2);
        
        
        //3. Subsections of mainGrid
        
        //3.1 Top left
        Label upperTopLeft = new Label("  Drawing Number:");
        TextField middleTopLeft = new TextField();
        Button bottomTopLeft = new Button("Search");
        Label topLeftErrorLabel = new Label("");
        topLeft.getChildren().addAll(upperTopLeft, middleTopLeft, bottomTopLeft, topLeftErrorLabel);
        
        //3.2 Middle left
        Label upperMiddleLeft = new Label("  .dxf Number (123456 format):");
        TextField middleMiddleLeft = new TextField();
        Button bottomMiddleLeft = new Button("Search");
        Label middleLeftErrorLabel = new Label();
        middleLeft.getChildren().addAll(upperMiddleLeft, middleMiddleLeft, bottomMiddleLeft, middleLeftErrorLabel);
        
        //3.3 Bottom left -- consider refactoring these as well
        
        //3.3.1 truck search section
        Label upperBottomLeft = new Label("  Truck Number:");
        TextField middleBottomLeft = new TextField();
        Button bottomBottomLeft = new Button("Search");
        TextFields.bindAutoCompletion(middleBottomLeft, downloader.autoCompleteList());
        Label bottomLeftErrorLabel = new Label();
        
        //3.3.2 archive truck search section
        Label upperBottomLeftArchive = new Label("\n\n  Archive Truck Number:");
        TextField middleBottomLeftArchive = new TextField();
        Button bottomBottomLeftArchive = new Button("Search");
        TextFields.bindAutoCompletion(middleBottomLeftArchive, archiveDownloader.autoCompleteList());
        Label bottomLeftArchiveErrorLabel = new Label();
    
        bottomLeft.getChildren().addAll(upperBottomLeft, middleBottomLeft, bottomBottomLeft, bottomLeftErrorLabel,
                upperBottomLeftArchive, middleBottomLeftArchive, bottomBottomLeftArchive, bottomLeftArchiveErrorLabel);
        
        //3.4 Insert Logo Image
        InputStream stream = null;
        try{
            stream = getClass().getResourceAsStream("/REV_LOGO.png");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        VBox oneOff = new VBox();
        oneOff.setAlignment(Pos.CENTER);
        oneOff.getChildren().add(imageView);
        mainGrid.add(oneOff, 1, 0);
        
        
        //3.5 dxf Family table VBox Panel
        centerBox.getChildren().add(new Label("  .dxf Family Table"));
        ScrollPane centerPane = new ScrollPane();
        TextFlow center = new TextFlow();
        centerPane.setContent(center);
        centerBox.getChildren().add(centerPane);
        VBox.setVgrow(centerPane, Priority.ALWAYS);
        centerPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        
        //3.6 misc panel
        Button clearAll = new Button("Clear all panels");
        Hyperlink questionsLink = new Hyperlink("Need help?");
        
        Button refresh = new Button("Refresh");
        bottomMiddle.setAlignment(Pos.CENTER);
        bottomMiddle.getChildren().add(refresh);
        bottomMiddle.getChildren().add(clearAll);
        bottomMiddle.getChildren().add(questionsLink);


        //3.7 Top right -- Refactor .7-.9
        ScrollPane topRightPane = new ScrollPane();
        TextFlow topRight = new TextFlow();
        topRightPane.setContent(topRight);
        topRightBox.getChildren().add(new Label("  Drawing Search History:"));
        topRightBox.getChildren().add(topRightPane);
        VBox.setVgrow(topRightPane, Priority.ALWAYS);
        topRightPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        
        //3.8 Middle Right
        ScrollPane middleRightPane = new ScrollPane();
        TextFlow middleRight = new TextFlow();
        middleRightPane.setContent(middleRight);
        middleRightBox.getChildren().add(new Label("  .dxf Search History:"));
        middleRightBox.getChildren().add(middleRightPane);
        VBox.setVgrow(middleRightPane, Priority.ALWAYS);
        middleRightPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        
        //3.9 Bottom Right
        ScrollPane bottomRightPane = new ScrollPane();
        TextFlow bottomRight = new TextFlow();
        bottomRightPane.setContent(bottomRight);
        bottomRightBox.getChildren().add(new Label("  Truck Search History:"));
        bottomRightBox.getChildren().add(bottomRightPane);
        VBox.setVgrow(bottomRightPane, Priority.ALWAYS);
        bottomRightPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        
        
        //3.4.1 Drawing search button / enter keystroke consider
        
        //Enter key
        middleTopLeft.setOnKeyPressed(keyEvent ->{
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                String drawing = middleTopLeft.getText();
                DrawingFinder searcher = new DrawingFinder(drawing);
                if (searcher.getDrawing() == null) {
                    topLeftErrorLabel.setText("Error: " + drawing + " not found!");
                } else {
                    getHostServices().showDocument(searcher.getLink());
                    Hyperlink link = new Hyperlink(searcher.getDrawing());
                    link.setOnAction(actionEvent1 -> {
                        getHostServices().showDocument(searcher.getLink());
                        link.setVisited(false);
                    });
                    topRight.getChildren().add(link);
                    topRight.getChildren().add(new Text("\n"));
                    middleTopLeft.setText("");
                    topLeftErrorLabel.setText("");
                }
            }
        });
        
        //Button
        bottomTopLeft.setOnAction((actionEvent -> {
            String drawing = middleTopLeft.getText();
            DrawingFinder searcher = new DrawingFinder(drawing);
            if (searcher.getDrawing() == null) {
                topLeftErrorLabel.setText("Error: " + drawing + " not found!");
            } else {
                getHostServices().showDocument(searcher.getLink());
                Hyperlink link = new Hyperlink(searcher.getDrawing());
                link.setOnAction(actionEvent1 -> {
                    getHostServices().showDocument(searcher.getLink());
                    link.setVisited(false);
                });
                topRight.getChildren().add(link);
                topRight.getChildren().add(new Text("\n"));
                middleTopLeft.setText("");
                topLeftErrorLabel.setText("");
            }
        }));
        
        // dxf search
        
        // on Enter
        middleMiddleLeft.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                        center.getChildren().clear();
                        String dxf = middleMiddleLeft.getText();
                        DxfFinder searcher = new DxfFinder(dxf);
                        if (searcher.getDxf().equals("-1")) {
                            middleLeftErrorLabel.setText("Error: " + dxf + " not found!"); //!!!
                        } else {
                            TreeMap<String, String> familyTable = searcher.getFamilyTable();
                            for (String item : familyTable.keySet()) {
                                Hyperlink instanceLink = new Hyperlink(item);
                                center.getChildren().add(instanceLink);
                                instanceLink.setOnAction(actionEvent -> {
                                    getHostServices().showDocument(familyTable.get(item));
                                    Hyperlink separateCopyOfInstanceLink = new Hyperlink(item);
                                    middleRight.getChildren().add(separateCopyOfInstanceLink);
                                    middleRight.getChildren().add(new Text("\n"));
                                    separateCopyOfInstanceLink.setOnAction(actionEvent1 -> {
                                        getHostServices().showDocument(familyTable.get(item));
                                        separateCopyOfInstanceLink.setVisited(false);
                                    });
                                    instanceLink.setVisited(false);
                                });
                                center.getChildren().add(new Text("\n"));
                            }
                            middleLeftErrorLabel.setText("");
                        }
                        middleMiddleLeft.setText("");
                    }
                });
    
        // Button press
        bottomMiddleLeft.setOnKeyPressed(keyEvent -> {
            center.getChildren().clear();
            String dxf = middleMiddleLeft.getText();
            DxfFinder searcher = new DxfFinder(dxf);
            if (searcher.getDxf() == null) {
                middleLeftErrorLabel.setText("Error: " + dxf + " not found!"); //!!!
            } else {
                TreeMap<String, String> familyTable = searcher.getFamilyTable();
                for (String item : familyTable.keySet()) {
                    Hyperlink instanceLink = new Hyperlink(item);
                    center.getChildren().add(instanceLink);
                    instanceLink.setOnAction(actionEvent -> {
                        getHostServices().showDocument(familyTable.get(item));
                        Hyperlink separateCopyOfInstanceLink = new Hyperlink(item);
                        middleRight.getChildren().add(separateCopyOfInstanceLink);
                        middleRight.getChildren().add(new Text("\n"));
                        separateCopyOfInstanceLink.setOnAction(actionEvent1 -> {
                            getHostServices().showDocument(familyTable.get(item));
                            separateCopyOfInstanceLink.setVisited(false);
                        });
                        instanceLink.setVisited(false);
                    });
                    center.getChildren().add(new Text("\n"));
                }
            }
            middleMiddleLeft.setText("");
        });
        
        // Truck search buttons
    
        // Enter Key
        middleBottomLeft.setOnKeyPressed(keyEvent ->{
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                String truck = middleBottomLeft.getText();
                if (downloader.getLink(truck) == null) {
                    bottomLeftErrorLabel.setText("Error: " + truck + " not found!");
                } else {
                    getHostServices().showDocument(downloader.getLink(truck));
                    Hyperlink link = new Hyperlink(truck);
                    link.setOnAction(actionEvent1 -> {
                        getHostServices().showDocument(downloader.getLink(truck));
                        link.setVisited(false);
                    });
                    bottomRight.getChildren().add(link);
                    bottomRight.getChildren().add(new Text("\n"));
                    middleBottomLeft.setText("");
                    bottomLeftErrorLabel.setText("");
                }
            }
        });
    
        // Button press
        bottomBottomLeft.setOnAction((actionEvent -> {
            String truck = middleBottomLeft.getText();
            if (downloader.getLink(truck) == null) {
                bottomLeftErrorLabel.setText("Error: " + truck + " not found!");
            } else {
                getHostServices().showDocument(downloader.getLink(truck));
                Hyperlink link = new Hyperlink(truck);
                link.setOnAction(actionEvent1 -> {
                    getHostServices().showDocument(downloader.getLink(truck));
                    link.setVisited(false);
                });
                bottomRight.getChildren().add(link);
                bottomRight.getChildren().add(new Text("\n"));
                middleBottomLeft.setText("");
                bottomLeftErrorLabel.setText("");
            }
        }));
        
        
        //Archive Search tools
        
        // Enter key from text field
        middleBottomLeftArchive.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                        String truck = middleBottomLeftArchive.getText();
                        if (archiveDownloader.getLink(truck) == null) {
                            bottomLeftArchiveErrorLabel.setText("Error: " + truck + " not found!");
                        } else {
                            getHostServices().showDocument(archiveDownloader.getLink(truck));
                            Hyperlink link = new Hyperlink(truck);
                            link.setOnAction(actionEvent1 ->{
                                    getHostServices().showDocument(archiveDownloader.getLink(truck));
                                    link.setVisited(false);
                            });
                            bottomRight.getChildren().add(link);
                            bottomRight.getChildren().add(new Text("\n"));
                            middleBottomLeftArchive.setText("");
                            bottomLeftArchiveErrorLabel.setText("");
                        }
                    }
                });
    
        // Button press below field
        bottomBottomLeftArchive.setOnAction((actionEvent -> {
            String truck = middleBottomLeftArchive.getText();
            if (archiveDownloader.getLink(truck) == null) {
                bottomLeftArchiveErrorLabel.setText("Error: " + truck + " not found!");
            } else {
                getHostServices().showDocument(archiveDownloader.getLink(truck));
                Hyperlink link = new Hyperlink(truck);
                link.setOnAction(actionEvent1 -> {
                    getHostServices().showDocument(archiveDownloader.getLink(truck));
                    link.setVisited(false);
                });
                bottomRight.getChildren().add(link);
                bottomRight.getChildren().add(new Text("\n"));
                middleBottomLeftArchive.setText("");
                bottomLeftArchiveErrorLabel.setText("");
                
            }
        }));
        
        // Misc Panel Buttons
        
        refresh.setOnAction(actionEvent -> {
            downloader.refresh();
            archiveDownloader.refresh();
            TextFields.bindAutoCompletion(middleBottomLeft, downloader.autoCompleteList());
            TextFields.bindAutoCompletion(middleBottomLeftArchive, archiveDownloader.autoCompleteList());
        });
    
        clearAll.setOnAction(actionEvent -> {
            middleTopLeft.setText("");
            topLeftErrorLabel.setText("");
            middleMiddleLeft.setText("");
            middleLeftErrorLabel.setText("");
            middleBottomLeft.setText("");
            bottomLeftErrorLabel.setText("");
            middleBottomLeftArchive.setText("");
            bottomLeftArchiveErrorLabel.setText("");
            center.getChildren().clear();
            topRight.getChildren().clear();
            middleRight.getChildren().clear();
            bottomRight.getChildren().clear();
        });
        
        //questionsLink for need help?
        
        questionsLink.setOnAction(actionEvent -> {
            getHostServices().showDocument("\\\\WPFFILE1.REVGINC.NET\\AMB-WPF_ENG_Working\\" +
                    "Working Folder - Unshipped Units\\4. Personal Working Folder" +
                    "\\Daniel Mason\\R.E.S.T. Documentation");
            questionsLink.setVisited(false);
        });

        // main Scene wrap
        Scene scene = new Scene(mainWrap);
        window.setScene(scene);
        window.show();
    }
}
