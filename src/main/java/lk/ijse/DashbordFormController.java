package lk.ijse;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashbordFormController implements Initializable {

    @FXML
    private Slider brightnessSlider;

    private BufferedImage originalImage;
    private BufferedImage editedImage;

    @FXML
    private ImageView image;

    @FXML
    private Button btn;

    @FXML
    void btnOnActhion(ActionEvent event) {
        openImage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        brightnessSlider.setMajorTickUnit(50);
        brightnessSlider.setMinorTickCount(10);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setShowTickMarks(true);
        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> updateBrightness());
    }

    private void updateBrightness() {
        if (originalImage != null) {
            float scaleFactor = 1.0f + (float) brightnessSlider.getValue() / 100.0f;
            RescaleOp op = new RescaleOp(scaleFactor, 0, null);
            editedImage = op.filter(originalImage, null);
            Image images = SwingFXUtils.toFXImage(editedImage, null);
            image.setImage(images);
        }
    }

    private void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image images = new Image(selectedFile.toURI().toString());
            image.setImage(images);
            // If you need BufferedImage for further processing
            originalImage = SwingFXUtils.fromFXImage(images, null);
            editedImage = deepCopy(originalImage);
            updateBrightness();
        }
    }

    private BufferedImage deepCopy(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
