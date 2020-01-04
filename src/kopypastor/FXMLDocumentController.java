/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kopypastor;

import fabFileLib.fabIO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

/**
 *
 * @author fabruz
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML private Label label;
    @FXML private ListView<String> listView;
    @FXML private TextField textField;
    @FXML private RadioButton radio;
    
    private String initialFolder, folder;
    
    fabIO myIO = new fabIO();
    ObservableList<String> observableList = FXCollections.observableArrayList();
    
    @FXML
    private void addLineFromField(Event event){
        String inputString = textField.getText();
        if(inputString.isEmpty()) return;
        observableList.add(inputString);
        textField.setText("");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listView.setItems(observableList);
        initialFolder = defaultDirectory();
        textField.setOnKeyPressed(event ->{
            if (event.getCode() == KeyCode.ENTER) addLineFromField(event);
        });
        textField.setOnMouseClicked(event ->{
            if (event.getClickCount() >= 2) addLinesFromFile(event);
        });
    }   
    
    private void addLinesFromFile(Event event){
        FileChooser fc = new FileChooser();
        java.io.File leFile = fc.showOpenDialog(null);
        
        if (leFile != null) {
            folder = leFile.getParent() + "/";
            textField.setText(leFile.getName());
            try {
                String[] content = myIO.readFile_asArray(leFile);
                for(String string: content){
                    if(!string.isEmpty()) observableList.add(string);
                } 
            } catch (Exception e) { label.setText("Error with File/Folder");
            }
        }
        else textField.setText("");
    }
    
    @FXML
    public void selectFromListView(Event event){
        if(radio.isSelected()){
            String selectedString = (String) listView.getSelectionModel().getSelectedItem();
            copyToClipboard(selectedString);
        }
    }
    
    private void copyToClipboard(String text) {
    java.util.HashMap<javafx.scene.input.DataFormat, Object> map = new java.util.HashMap<>();
    map.put(javafx.scene.input.DataFormat.PLAIN_TEXT, text);
    javafx.scene.input.Clipboard.getSystemClipboard().setContent(map);
    }
    
    
    @FXML
    public void makeTXT(ActionEvent event) throws java.io.IOException{
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("E_yyyy-MM-dd_hh-mm-ss");
        String nameOfFile = dateFormat.format(new java.util.Date());
        String onDesktop = initialFolder + "\\Desktop\\";
        myIO.writeToTxt(arrStringFromObserv(observableList), onDesktop, nameOfFile);
        
        Runtime.getRuntime().exec("cmd /c start " + onDesktop + nameOfFile + ".txt");
    }
    @FXML
    public void removeBtn(ActionEvent event){
        String selectedString = (String) listView.getSelectionModel().getSelectedItem();
        if (selectedString.isEmpty()) {
            observableList.clear();
            return;
        }
        int index = observableList.lastIndexOf(selectedString);
        observableList.remove(index);
    }
    
    private static String defaultDirectory(){
        return javax.swing.filechooser.FileSystemView.
                getFileSystemView().getDefaultDirectory().getParent();
    }
    
    public static String[] arrStringFromObserv(ObservableList<String> observ){
        String[] outArr = new String[observ.size()];
        int i = 0;
        java.util.Iterator<String> iterator = observ.iterator();
        while (iterator.hasNext()) {
            outArr[i] = iterator.next(); 
            i++;
        }
        return outArr;
    }
    
}
