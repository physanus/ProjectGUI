package de.danielprinz.ProjectGUI.resources;

import de.danielprinz.ProjectGUI.Main;
import javafx.scene.image.Image;
import purejavacomm.CommPortIdentifier;

import java.lang.reflect.Field;

public class SettingsHandler {

    private static int AVAILABLE = -1;
    public static Image APP_ICON = getResourceByString("plotter.png").convertToImage();
    public static Image JOYSTICK_CROSSHAIRS = getResourceByString("add.png").convertToImage();

    public static CommPortIdentifier PORT;
    public static int PREVIEW_IMAGE_MAX_WIDTH = 650;
    public static int PREVIEW_IMAGE_MAX_HEIGHT = 750;
    public static int PRINT_IMAGE_MAX_WIDTH = 15500;
    public static int PRINT_IMAGE_MAX_HEIGHT = 6500;
    public static double HANDDRAWING_MOVEMENT_SPEED = 1.2;
    public static int SERIAL_CONNECTION_TIMEOUT = 2000;
    public static int SERIAL_CONNECTION_COMMANDS_SENT_PER_SECOND = 2;




    /**
     * Gets the corresponding resource and returns the file
     * @param path The path to the resource
     * @return The resource
     */
    public static Resource getResourceByString(String path) throws NullPointerException {
        return new Resource(Main.class.getResourceAsStream("/" + path));
    }

    public static boolean checkAvailableResources() {

        if(AVAILABLE == 0) {
            return false;
        } else if (AVAILABLE == 1)
            return true;

        boolean result = true;

        Field[] fields = SettingsHandler.class.getDeclaredFields();
        for(Field field : fields) {
            try {

                Class clazz = field.getType();
                field.setAccessible(true); // Suppress Java language access checking

                // Remove "final" modifier
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                int oldInt = modifiersField.getInt(field);
                //modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                // Get value
                Object value = field.get(null);
                if(clazz.equals(CommPortIdentifier.class)) {
                    // this will be null and it is okay since we check later
                    continue;
                }
                if(value == null) {
                    // the resource could not be found
                    System.err.println(Strings.RESOURCE_NOT_FOUND.format(field.getName()));
                    result = false;
                }


                // Set value
                //field.set(null, Boolean.TRUE);
                //System.out.println(enabled);

                modifiersField.setInt(field, oldInt);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(result == true) {
            AVAILABLE = 1;
        } else {
            AVAILABLE = 0;
        }

        return result;
    }

}

