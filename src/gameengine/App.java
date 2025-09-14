package gameengine;

import gameengine.Shapes.FinishLine;
import gameengine.Shapes.Shape;
import gameengine.Shapes.Square;
import gameengine.Shapes.Wall;
import gameengine.typedefs.Directories_TypeDefs;
import gameengine.typedefs.Shapes_TypeDefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gameengine.Popups.*;
import gameengine.HelperFunctions.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;




public class App extends Application {
    double screenWidth = 500;
    double screenHeight = 500;

    Canvas canvas = new Canvas(screenWidth, screenHeight);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    Entities entities = new Entities(canvas);
        
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        MenuBar menuBar = setupMenuBar(entities);
        root.setTop(menuBar);
        root.setCenter(canvas);

        new AnimationTimer() {
            public void handle(long now) {
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                if (!entities.running) {
                    for (Shape s : entities.Shapes) {
                        s.render(gc);
                    }
                    return;
                }

                double maxGridLen = 1;
                for (int idx = 0; idx < entities.Shapes.size(); idx++) {
                    Shape s = entities.Shapes.get(idx);
                    s.cannotBeDeleted--;

                    if(s.deleteMark){
                    if(!s.deleteAfterReset){
                        s.deleteMark = false;
                        entities.restoreShapes.add(s);
                    }
                        if(s.cannotBeDeleted > 0){
                            s.deleteMark = false;
                        }
                        else{
                            entities.removeShape(s);
                            idx--;
                            continue;
                        }
                    }

                    double gridLen = s.getMinGridLen();
                    if (gridLen > maxGridLen) {
                        maxGridLen = gridLen;
                    }
                    s.update(entities);
                    s.render(gc);

                    if (entities.canAddShape && s.copiesToCreate > 0) {
                        Random rand = new Random();
                        Shape sCopy = s.Copy();
                        double totalVel = Math.abs(s.storedXVel) + Math.abs(s.storedXVel);
                        sCopy.XPos = s.orgXPos;
                        sCopy.YPos = s.orgYPos;
                        sCopy.orgXPos = s.orgXPos;
                        sCopy.orgYPos = s.orgYPos;
                        sCopy.XVel = (Math.random()*-1)*(Math.random() * totalVel  + 0.5);
                        sCopy.YVel = (Math.random()*-1)*(totalVel - sCopy.XVel);
                        sCopy.wallAndDup = s.wallAndDup; 
                        sCopy.bounceAndDel = s.wallAndDup; 
                        sCopy.deleteAfterReset = true;
                        sCopy.cannotBeDeleted = 30;
                        entities.Shapes.add(sCopy);
                        sCopy.Color = Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
                        s.copiesToCreate--;  
                        entities.canAddShape = false;
                    }
                }
                entities.GridLen = maxGridLen;
                entities.canAddShape = true;

                entities.grid.clear();
                for (Shape s : entities.Shapes) {
                    entities.insertIntoGrid(s, entities.GridLen);
                }

                for (List<Shape> cellShapes : entities.grid.values()) {
                    for (int i = 0; i < cellShapes.size(); i++) {
                        for (int j = i + 1; j < cellShapes.size(); j++) {
                            Shape a = cellShapes.get(i);
                            Shape b = cellShapes.get(j);

                            if (a.collidesWith(b)) {
                                double[] mtv = CollisionHelper.polygonCollision(a.getCorners(), b.getCorners());
                                if (mtv != null) CollisionHelper.resolveCollision(a, b, mtv);
                            }
                        }
                    }
                }
                
            }
        }.start();

    canvas.setOnMousePressed(e -> {
        if (entities.running) return;

        if (entities.activeContextMenu != null && !e.isSecondaryButtonDown()) {
            entities.activeContextMenu.hide();
            entities.activeContextMenu = null;
        }

        if (e.isSecondaryButtonDown()) {
            for (Shape s : entities.Shapes) {
                if (s.contains(e.getX(), e.getY())) {
                    if (entities.activeContextMenu != null) {
                        entities.activeContextMenu.hide();
                    }
                    entities.activeContextMenu = rightClickShapeContextMenu(s);
                    entities.activeContextMenu.show(canvas, e.getScreenX(), e.getScreenY());
                    return;
                }
            }
        }

        boolean shapeClicked = false;

        for (Shape s : entities.Shapes) {
            boolean clickOnShape = s.contains(e.getX(), e.getY());
            String clickedHandle = s.isSelected ? s.getClickedHandle(e.getX(), e.getY()) : null;

            if (clickOnShape || clickedHandle != null) {
                entities.selectedShape = s;
                s.isSelected = true;
                entities.offsetX = e.getX() - s.XPos;
                entities.offsetY = e.getY() - s.YPos;
                shapeClicked = true;

                s.activeHandle = clickedHandle;
            } else {
                s.isSelected = false;
            }
        }

        if (!shapeClicked) {
            entities.selectedShape = null;
        }
    });


    canvas.setOnMouseDragged(e -> {
        if (entities.running || entities.selectedShape == null) return;

        Shape s = entities.selectedShape;

        if (s.activeHandle != null) {
            s.resizeShape(s.activeHandle, e.getX(), e.getY());
        } else {
            s.XPos = e.getX() - entities.offsetX;
            s.YPos = e.getY() - entities.offsetY;

            s.orgXPos = s.XPos;
            s.orgYPos = s.YPos;
        }
    });

    canvas.setOnMouseReleased(e -> {
        if (entities.selectedShape != null) {
            entities.selectedShape.activeHandle = null;
        }
        entities.selectedShape = null;
    });


    Scene scene = new Scene(root, screenWidth, screenHeight);

    canvas.widthProperty().bind(root.widthProperty());
    canvas.heightProperty().bind(root.heightProperty());


    stage.setScene(scene);
    stage.setTitle("Physics Window");
    stage.show();
    }

    private MenuBar setupMenuBar(Entities entities) {
        MenuBar menuBar = new MenuBar();
        Menu programMenu = new Menu("Program");
        Menu shapesMenu = new Menu("Shapes");
        Menu PresetMenu = new Menu("Presets");

        MenuItem startItem = new MenuItem("Start");
        startItem.setOnAction(e -> {
            entities.running = true;
            for (Shape s : entities.Shapes) {
                s.XVel = s.storedXVel;
                s.YVel = s.storedYVel;
                s.isSelected = false;
            }
        });
        
        MenuItem stopItem = new MenuItem("Stop");
        stopItem.setOnAction(e -> {
            entities.running = false;
            for (Shape s : entities.Shapes) {
                s.XVel = 0;
                s.YVel = 0;
            }
        });

        MenuItem resetItem = new MenuItem("Reset");
        resetItem.setOnAction(e -> {
            entities.running = false;
            Entities.leaderBoard = new ArrayList<>();
            Iterator<Shape> iter = entities.Shapes.iterator();
            while (iter.hasNext()) {
                Shape s = iter.next();
                
                if (s.deleteAfterReset) {
                    iter.remove(); 
                    continue;      
                }

                s.XPos = s.orgXPos;
                s.YPos = s.orgYPos;
                s.XVel = 0;
                s.YVel = 0;

                s.finished = false;
                s.finishing = false;
                if (!(s instanceof FinishLine)) s.ignoreCollision = false;
            }
            for (Shape s : entities.restoreShapes) {
                s.XPos = s.orgXPos;
                s.YPos = s.orgYPos;
            }

            entities.Shapes.addAll(entities.restoreShapes);
            entities.restoreShapes = new ArrayList<Shape>();
        });

        MenuItem clearItem = new MenuItem("Clear");
        clearItem.setOnAction(e -> {
            if(ConfirmationPopup.showConfirmation("Are You Sure?", "This will delete any unsaved changes."))
            entities.Shapes = new ArrayList<Shape>();
        });


        MenuItem halfSpeedItem = new MenuItem("0.5x Speed");
        halfSpeedItem.setOnAction(e -> {
            for (Shape s : entities.Shapes) {
                s.XVel *= 0.5;
                s.YVel *= 0.5;
            }
        });

        MenuItem doubleSpeedItem = new MenuItem("2x Speed");
        doubleSpeedItem.setOnAction(e -> {
            for (Shape s : entities.Shapes) {
                s.XVel *= 2;
                s.YVel *= 2;
            }
        });

        MenuItem resetSpeedItem = new MenuItem("Reset Speed");
        resetSpeedItem.setOnAction(e -> {
            for (Shape s : entities.Shapes) {
                s.XVel = s.storedXVel;
                s.YVel = s.storedYVel;
            }
        });

        Menu speedMenu = new Menu("Speed");
        speedMenu.getItems().addAll(
            resetSpeedItem,
            doubleSpeedItem,
            halfSpeedItem
        );

        MenuItem leaderBoardItem = new MenuItem("Leader Board");
        leaderBoardItem.setOnAction(e ->{
            LeaderBoardPopup.show();
        });


        MenuItem addSquareItem = new MenuItem("Square");
        addSquareItem.setOnAction(e -> {
            AddSquarePopup sPopup = new AddSquarePopup();
            sPopup.setConfirmButton(entities);
            sPopup.showScene();
        });
        
        Menu addShapesMenu = new Menu("Add Shape");
        addShapesMenu.getItems().addAll(
            addSquareItem
        );

        MenuItem addWallItem = new MenuItem("Wall");
        addWallItem.setOnAction(e -> {
            AddWallPopup wPopup = new AddWallPopup();
            wPopup.setConfirmButton(entities);
            wPopup.showScene();
        });

        MenuItem addFinishLineItem = new MenuItem("Finish Line");
        addFinishLineItem.setOnAction(e-> {
            AddFinishLinePopup fPopup = new AddFinishLinePopup();
            fPopup.setConfirmButton(entities);
            fPopup.showScene();
        }); 

                
        Menu addObstaclesMenu = new Menu("Add Obstacles");
        addObstaclesMenu.getItems().addAll(
            addWallItem,
            addFinishLineItem
        );

        Menu loadPresetsMenu = new Menu("Load Preset");

        if (Directories_TypeDefs.PRESETS.exists() && Directories_TypeDefs.PRESETS.isDirectory()) {
            File[] presetFiles = Directories_TypeDefs.PRESETS.listFiles((dir, name) -> name.endsWith(".json"));

            if (presetFiles != null) {
                for (File presetFile : presetFiles) {
                    String presetName = presetFile.getName().replaceFirst("\\.json$", "");
                    MenuItem item = new MenuItem(presetName);

                    item.setOnAction(e -> {
                        if(ConfirmationPopup.showConfirmation("Are You Sure?", "Loading a preset will overide all existing objects.")){
                            loadPreset(presetFile);
                        }
                    });

                    loadPresetsMenu.getItems().add(item);
                }
            }
        }

        MenuItem savePresetItem = new MenuItem("Save Preset");
        savePresetItem.setOnAction(e -> {
        SavePresetPopup spPopup = new SavePresetPopup();
        spPopup.confirmButton.setOnAction(event -> { 

            String presetName;
            if(spPopup.nameField.getText().isEmpty()){
                File[] existingFiles = Directories_TypeDefs.PRESETS.listFiles((dir, name) -> name.endsWith(".json"));
                int maxIndex = 0;
                if (existingFiles != null) {
                    for (File file : existingFiles) {
                        String name = file.getName().replaceFirst("\\.json$", ""); // remove extension
                        if (name.startsWith("Preset(") && name.endsWith(")")) {
                            try {
                                int index = Integer.parseInt(name.substring(7, name.length() - 1));
                                if (index > maxIndex) maxIndex = index;
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
                presetName = "Preset(" + (maxIndex + 1) + ")";
            }
            else{
                presetName = spPopup.nameField.getText();
                if(!ConfirmationPopup.showConfirmation("Are You Sure?", "This may overide an existing preset.")){
                    return;
                }
            }


            File presetFile = new File(Directories_TypeDefs.PRESETS, presetName + ".json");
            createPreset(presetName); 

            MenuItem newLoadItem = new MenuItem(presetName);
            newLoadItem.setOnAction(ev -> loadPreset(presetFile));
            loadPresetsMenu.getItems().add(newLoadItem);

            spPopup.stage.close();
        });
    });


        programMenu.getItems().add(startItem);
        programMenu.getItems().add(stopItem);
        programMenu.getItems().add(resetItem);
        programMenu.getItems().add(clearItem);
        programMenu.getItems().add(speedMenu);
        programMenu.getItems().add(leaderBoardItem);
        menuBar.getMenus().add(programMenu);

        shapesMenu.getItems().add(addShapesMenu);
        shapesMenu.getItems().add(addObstaclesMenu);
        menuBar.getMenus().add(shapesMenu);

        PresetMenu.getItems().add(savePresetItem);
        PresetMenu.getItems().add(loadPresetsMenu);
        menuBar.getMenus().add(PresetMenu);
        return menuBar;
    }

    private void loadPreset(File presetFile) {
        entities.Shapes = new ArrayList<Shape>();

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(presetFile);
            JsonNode shapesNode = root.get(1);

            for (int i = 0; i < shapesNode.size(); i++) {
                JsonNode node = shapesNode.get(i);
                String type = node.get("type").asText();

                switch (type) {
                    case Shapes_TypeDefs.SQUARE:
                        Square sq = new Square(
                            type,
                            node.get("name").asText(),
                            node.get("XLen").asDouble(),
                            node.get("YLen").asDouble(),
                            node.get("orgXPos").asDouble(),
                            node.get("orgYPos").asDouble(),
                            node.get("storedXVel").asDouble(),
                            node.get("storedYVel").asDouble(),
                            new Color(
                                node.get("Color").get("red").asDouble(),
                                node.get("Color").get("green").asDouble(),
                                node.get("Color").get("blue").asDouble(),
                                node.get("Color").get("opacity").asDouble()
                            ),
                            node.get("angle").asDouble()
                        );
                        sq.wallAndDup = node.get("wallAndDup").asBoolean();
                        sq.bounceAndDel = node.get("bounceAndDel").asBoolean();
                        entities.addShape(sq);
                        break;
                        

                    case Shapes_TypeDefs.WALL:
                        Wall w = new Wall(
                            type,
                            node.get("name").asText(),
                            node.get("len").asDouble(),
                            node.get("orgXPos").asDouble(),
                            node.get("orgYPos").asDouble(),
                            new Color(
                                node.get("Color").get("red").asDouble(),
                                node.get("Color").get("green").asDouble(),
                                node.get("Color").get("blue").asDouble(),
                                node.get("Color").get("opacity").asDouble()
                            ),
                            node.get("angle").asDouble()
                        );
                        entities.addShape(w);
                        break;
                    case Shapes_TypeDefs.FINISH:
                        FinishLine f = new FinishLine(
                            type,
                            node.get("name").asText(),
                            node.get("XLen").asDouble(),
                            node.get("YLen").asDouble(),
                            node.get("orgXPos").asDouble(),
                            node.get("orgYPos").asDouble(),
                            new Color(
                                node.get("Color").get("red").asDouble(),
                                node.get("Color").get("green").asDouble(),
                                node.get("Color").get("blue").asDouble(),
                                node.get("Color").get("opacity").asDouble()
                            ),
                            node.get("angle").asDouble()
                        );
                        entities.Shapes.add(0, f);
                        break;

                    default:
                        System.out.println("Please implement type " + type);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPreset(String filename) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );

        if (!Directories_TypeDefs.PRESETS.exists()) {
            Directories_TypeDefs.PRESETS.mkdirs(); 
        }

        File outFile = new File(Directories_TypeDefs.PRESETS, filename + ".json");

        try {
            mapper.writeValue(outFile, entities.Shapes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ContextMenu rightClickShapeContextMenu(Shape shape) {
        ContextMenu menu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit Properties");
        editItem.setOnAction(e -> {
            System.out.println(shape.type);
            switch (shape.type) {
                case Shapes_TypeDefs.SQUARE:
                    Square square = (Square) shape;  
                    EditSquarePopup sPopup = new EditSquarePopup(square);
                    sPopup.setConfirmButton(entities);
                    sPopup.showScene();
                    return;
                case Shapes_TypeDefs.WALL:
                    Wall wall = (Wall) shape;
                    EditWallPopup wPopup = new EditWallPopup(wall);
                    wPopup.setConfirmButton(entities);
                    wPopup.showScene();
                    return;
                case Shapes_TypeDefs.FINISH:
                    FinishLine finishLine = (FinishLine) shape;
                    EditFinishLinePopup fPopup = new EditFinishLinePopup(finishLine);
                    fPopup.setConfirmButton(entities);
                    fPopup.showScene();
                    return;
                default:
                    System.out.println("Please implement type " + shape.type);
                    return;

            }
        });

    MenuItem copyItem = new MenuItem("Create Copy");
    copyItem.setOnAction(e -> {
        Shape newShape = shape.Copy();

        newShape.orgXPos += 20;
        newShape.orgYPos += 20;
        newShape.XPos += 20;
        newShape.YPos += 20;
        entities.addShape(newShape);
    });

    MenuItem deleteItem = new MenuItem("Delete");
    deleteItem.setOnAction(e -> entities.Shapes.remove(shape));

    menu.getItems().addAll(
    editItem, 
    copyItem,
    deleteItem
    );

    menu.setOnHidden(ev -> entities.activeContextMenu = null);

    return menu;
}


    public static void main(String[] args) {
        launch(args);
    }
}
