package de.danielprinz.ProjectGUI.resources;

import java.text.MessageFormat;

public enum Strings {

    ICON_NOT_FOUND("Das angegebene Icon konnte nicht gefunden werden: {0}"),

    MENUBAR_FILE("File"),
        MENUBAR_LOAD("Load"),
        MENUBAR_SAVEAS("Save as..."),
        MENUBAR_SETTINGS("Settings"),
        MENUBAR_CLOSE("Close"),
    SAVE_CHANGES_QUESTION("Do you want to save changes to {0}?"),
    SAVE_CHANGES_ANSWER_YES("Save"),
    SAVE_CHANGES_ANSWER_NO("Don't save"),
    SAVE_CHANGES_ANSWER_CANCEL("Cancel");


    String s;
    Strings(String s) {
        this.s = s;
    }

    public String format(Object... args) {
        return MessageFormat.format(s, args);
    }
}
