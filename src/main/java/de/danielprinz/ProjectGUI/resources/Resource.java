package de.danielprinz.ProjectGUI.resources;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;

public class Resource {

    InputStream resourceAsStream;
    public Resource(InputStream resourceAsStream) {
        this.resourceAsStream = resourceAsStream;
    }

    /**
     * Converts a buffered image into a JavaFX-compatible Image
     * @return The JavaFX compatible image
     */
    public Image convertToImage() {
        //return SwingFXUtils.toFXImage(ImageIO.read(resourceAsStream), null);
        return new Image(resourceAsStream);
    }

}
