package de.danielprinz.ProjectGUI.files;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBox;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.resources.Strings;

import java.io.File;

public class OpenFileHandler {

    private File openFile;
    private boolean isSaved = false;

    public OpenFileHandler(File openFile) {
        this.openFile = openFile;
    }

    public void save(CloseSaveBoxResult result) {
        if(!result.equals(CloseSaveBoxResult.SAVE)) return;

        // TODO save current document
        System.out.println("saving...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    /**
     * Shows the dialog box if the file has not been saved
     * @return /SAVE/NOSAVE/CANCEL
     */
    public CloseSaveBoxResult showDialogBox() {
        if(this.isSaved) return CloseSaveBoxResult.NOSAVE; // TODO implement isSaved variable
        CloseSaveBoxResult result = CloseSaveBox.display(Main.WINDOW_TITLE, Strings.SAVE_CHANGES_QUESTION.format(this.openFile.getName())); // TODO show the current filename
        if(result == null) result.equals(CloseSaveBoxResult.CANCEL);
        return result;
    }

}
