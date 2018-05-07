package de.danielprinz.ProjectGUI.resources;

import de.danielprinz.ProjectGUI.Main;

public enum Setting {

    ICON("plotter.png");

    String s;
    Setting(String s) {
        this.s = s;
    }
    public String getValue() {
        return "/" + this.s;
    }

    /**
     * Gets the corresponding resource and returns the file
     * @return The file
     */
    public Resource getResource() {
        return new Resource(Main.getInstance().getClass().getResource(getValue()).getFile().replaceAll("%20", " "));
    }

}
