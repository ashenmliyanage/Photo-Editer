package lk.ijse;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class PhotoEditorFX extends Application {

    private BufferedImage originalImage;
    private BufferedImage editedImage;

    private ImageView imageView;
    private Slider brightnessSlider;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Photo Editor");

        createMenu(primaryStage);
        createBrightnessSlider();
        createOpenFileButton(primaryStage);

        imageView = new ImageView();
        BorderPane root = new BorderPane();
        root.setCenter(imageView);

        Slider brightnessSlider = new Slider(-100, 100, 0);
        brightnessSlider.setMajorTickUnit(50);
        brightnessSlider.setMinorTickCount(10);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setShowTickMarks(true);
        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> updateBrightness());

        BorderPane controlPane = new BorderPane();
        controlPane.setLeft(brightnessSlider);
        controlPane.setRight(createOpenFileButton(primaryStage));
        root.setBottom(controlPane);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private Button createOpenFileButton(Stage primaryStage) {
        Button openFileButton = new Button("Open Image File");
        openFileButton.setOnAction(e -> openImage(primaryStage));
        return openFileButton;
    }

    private void createBrightnessSlider() {
        brightnessSlider = new Slider(-100, 100, 0);
        brightnessSlider.setMajorTickUnit(50);
        brightnessSlider.setMinorTickCount(10);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setShowTickMarks(true);
        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> updateBrightness());
    }

    private void updateBrightness() {
        if (originalImage != null) {
            float scaleFactor = 1.0f + (float) brightnessSlider.getValue() / 100.0f;
            java.awt.image.RescaleOp op = new java.awt.image.RescaleOp(scaleFactor, 0, null);
            editedImage = op.filter(originalImage, null);
            Image image = SwingFXUtils.toFXImage(editedImage, null);
            imageView.setImage(image);
        }
    }

    private void createMenu(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(e -> openImage(primaryStage));

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(e -> saveImage(primaryStage));

        fileMenu.getItems().addAll(openItem, saveItem);
        menuBar.getMenus().add(fileMenu);

        BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
        root.setTop(menuBar);
    }

    private void openImage(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            try {
                originalImage = ImageIO.read(selectedFile);
                editedImage = deepCopy(originalImage);
                Image image = SwingFXUtils.toFXImage(originalImage, null);
                imageView.setImage(image);
                updateBrightness();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImage(Stage primaryStage) {
        if (editedImage != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                try {
                    ImageIO.write(editedImage, "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private BufferedImage deepCopy(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
