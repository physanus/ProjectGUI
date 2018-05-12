package de.danielprinz.ProjectGUI.files;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBox;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorBox;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorType;
import de.danielprinz.ProjectGUI.resources.Strings;

import java.io.*;
import java.util.ArrayList;

public class OpenFileHandler {

    private File openFile;
    private ArrayList<String> fileContent = new ArrayList<>();
    private boolean isSaved = true;

    public OpenFileHandler(File openFile) {
        this.openFile = openFile;
    }

    public void save(CloseSaveBoxResult result) {
        if(!result.equals(CloseSaveBoxResult.SAVE)) return;
        save();
    }
    public void save() {
        System.out.println("saving...");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(openFile))) {
            // TODO save current document
            /*for(;;) {

            }*/
        } catch (IOException e) {
            e.printStackTrace();
            FileErrorBox.display(FileErrorType.WRITE_ERROR, Main.WINDOW_TITLE, Strings.FILE_WRITE_ERROR.format());
        }
    }

    public void read(File file) {
        this.openFile = file;

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(openFile))) {
            // lets try to read the file.
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if(line.equals("")) continue;
                this.fileContent.add(line);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            FileErrorBox.display(FileErrorType.NO_SUCH_FILE, Main.WINDOW_TITLE, Strings.FILE_ERROR_NO_SUCH_FILE.format());
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
