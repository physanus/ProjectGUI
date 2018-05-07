package de.danielprinz.ProjectGUI.resources;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Resource {

    private File file;

    public Resource(File file) {
        this.file = file;
    }
    public Resource(String fileString) {
        this.file = new File(fileString);
    }


    /**
     * Converts a buffered image into a JavaFX-compatible Image
     * @return The JavaFX compatible image
     */
    public WritableImage convertToImage() throws IOException {
        return SwingFXUtils.toFXImage(ImageIO.read(file), null);
    }

}
