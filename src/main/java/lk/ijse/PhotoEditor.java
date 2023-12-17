package lk.ijse;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PhotoEditor extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage editedImage;

    private JLabel imageLabel;
    private JSlider brightnessSlider;

    public PhotoEditor() {
        setTitle("Photo Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        createMenu();
        createBrightnessSlider();
        createOpenFileButton();

        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.add(brightnessSlider);
        controlPanel.add(createOpenFileButton());
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createOpenFileButton() {
        JButton openFileButton = new JButton("Open Image File");
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });
        return openFileButton;
    }

    private void createBrightnessSlider() {
        brightnessSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        brightnessSlider.setMajorTickSpacing(50);
        brightnessSlider.setMinorTickSpacing(10);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);

        brightnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateBrightness();
            }
        });
    }

    private void updateBrightness() {
        if (originalImage != null) {
            float scaleFactor = 1.0f + brightnessSlider.getValue() / 100.0f;
            RescaleOp op = new RescaleOp(scaleFactor, 0, null);
            editedImage = op.filter(originalImage, null);
            updateImageLabel();
        }
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                originalImage = ImageIO.read(selectedFile);
                editedImage = deepCopy(originalImage);
                updateImageLabel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                ImageIO.write(editedImage, "png", selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateImageLabel() {
        ImageIcon imageIcon = new ImageIcon(editedImage);
        imageLabel.setIcon(imageIcon);
    }

    private BufferedImage deepCopy(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PhotoEditor();
            }
        });
    }
}
