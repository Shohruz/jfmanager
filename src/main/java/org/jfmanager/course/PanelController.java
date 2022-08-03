package org.jfmanager.course;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class PanelController implements Initializable {
	
	@FXML
	TableView<FileInfo> filesTable;
	
	@FXML
	ComboBox<String> disksBox;
	
	@FXML
	TextField pathField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
		fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
		fileTypeColumn.setPrefWidth(24);
		
		TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Name");
		fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
		fileNameColumn.setPrefWidth(300);
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		TableColumn<FileInfo, String> fileLastModifiedColumn = new TableColumn<>("Last Modified");
		fileLastModifiedColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
		fileLastModifiedColumn.setPrefWidth(140);
		
		TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Size");
		fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
		fileSizeColumn.setPrefWidth(150);
		fileSizeColumn.setCellFactory(column -> {
			return new TableCell<FileInfo, Long>() {
				@Override
				protected void updateItem(Long item, boolean empty) {
					super.updateItem(item, empty);
					if(item == null || empty) {
						setText("");
						setStyle("");
					} else {
						String text = item == -1 ? "[DIR]" : String.format("%,d bytes", item);
						setText(text);
					}
				}
			};
		});
		
		filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileLastModifiedColumn, fileSizeColumn);
		filesTable.getSortOrder().add(fileTypeColumn);
		
		disksBox.getItems().clear();
		for(Path p : FileSystems.getDefault().getRootDirectories()) {
			disksBox.getItems().add(p.toString());
		}
		disksBox.getSelectionModel().select(0);
		
		filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2) {
					Path path = Paths.get(pathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFilename());
					if(Files.isDirectory(path)) {
						updateList(path);
					}
				}
			}
		});
		
		updateList(Paths.get("."));
	}

	public void updateList(Path path) {
		try {
			pathField.setText(path.normalize().toAbsolutePath().toString());
			filesTable.getItems().clear();
			filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
			filesTable.sort();
		} catch (IOException ex) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "Error reload list", ButtonType.OK);
			alert.showAndWait();
		}
	}
	
	public void btnExitAction(ActionEvent actionEvent) {
		Platform.exit();
	}
	
	public void btnPathUpAction(ActionEvent actionEvent) {
		Path parentPath = Paths.get(pathField.getText()).getParent();
		if (parentPath != null) {
			updateList(parentPath);
		}
	}
	
	public void selectDiskAction(ActionEvent actionEvent) {
		ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
		updateList(Paths.get(element.getSelectionModel().getSelectedItem()));
	}
	
	public String getSelectedFilename() {
		if(!filesTable.isFocused()) {
			return null;
		}
		return filesTable.getSelectionModel().getSelectedItem().getFilename();
	}
	
	public String getCurrentPath() {
		return pathField.getText();
	}
	
}
