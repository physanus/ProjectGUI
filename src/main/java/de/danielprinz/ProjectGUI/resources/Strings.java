package de.danielprinz.ProjectGUI.resources;

import java.text.MessageFormat;

public enum Strings {

    RESOURCE_NOT_FOUND("The specified resource could not be found: {0}"),

    MENUBAR_FILE("File"),
        MENUBAR_OPEN("Open"),
        MENUBAR_SAVEAS("Save as..."),
        MENUBAR_SETTINGS("Settings"),
        MENUBAR_CLOSE("Close"),
    SAVE_CHANGES_QUESTION("Do you want to save changes to {0}?"),
        SAVE_CHANGES_ANSWER_YES("Save"),
        SAVE_CHANGES_ANSWER_NO("Don't save"),
        SAVE_CHANGES_ANSWER_CANCEL("Cancel"),
    FILE_ERROR_NO_SUCH_FILE("The selected file could not be loaded. Cancelling..."),
    FILE_ERROR_NOT_COMPATIBLE("The seleccted file is not compatible with this program. Cancelling..."),
        FILE_ERROR_OK("OK"),
    FILE_WRITE_ERROR("The specified file could not be saved. Cancelling..."),
    CONNECTION_ERROR_DIALOGUE("The connection could not be established. Aborting..."),
        CONNECTION_ERROR_DIALOGUE_OK("OK");


    String s;
    Strings(String s) {
        this.s = s;
    }

    public String format(Object... args) {
        return MessageFormat.format(s, args);
    }
}
