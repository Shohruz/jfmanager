package org.jfmanager.course;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

public class Controller {

	@FXML
	VBox leftPanel, rightPanel;
	
	public void btnExitAction(ActionEvent actionEvent) {
		Platform.exit();
	}

	public void btnCopyAction(ActionEvent actionEvent) {
		PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
		PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");
		
		if(leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "No file choosen", ButtonType.OK);
			alert.show();
			return;
		}
		PanelController srcPC = null, dstPC = null;
		if(leftPC.getSelectedFilename() != null) {
			srcPC = leftPC;
			dstPC = rightPC;
		}
		if(rightPC.getSelectedFilename() != null) {
			srcPC = rightPC;
			dstPC = leftPC;
		}
		Path srcPath = Paths.get(srcPC.getCurrentPath(), srcPC.getSelectedFilename());
		Path dstPath = Paths.get(dstPC.getCurrentPath()).resolve(srcPath.getFileName());
		try {
			Files.copy(srcPath, dstPath);
			dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "File " + srcPath.normalize().toAbsolutePath().toString() + " already exists on " + dstPath.normalize().toAbsolutePath().toString(), ButtonType.OK);
			alert.show();
			return;
		}
	}
	
	public void btnMoveAction(ActionEvent actionEvent) {
		PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
		PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");
		
		if(leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "No file choosen", ButtonType.OK);
			alert.show();
			return;
		}
		PanelController srcPC = null, dstPC = null;
		if(leftPC.getSelectedFilename() != null) {
			srcPC = leftPC;
			dstPC = rightPC;
		}
		if(rightPC.getSelectedFilename() != null) {
			srcPC = rightPC;
			dstPC = leftPC;
		}
		Path srcPath = Paths.get(srcPC.getCurrentPath(), srcPC.getSelectedFilename());
		Path dstPath = Paths.get(dstPC.getCurrentPath()).resolve(srcPath.getFileName());
		try {
			Files.move(srcPath, dstPath);
			srcPC.updateList(Paths.get(srcPC.getCurrentPath()));
			dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "File " + srcPath.normalize().toAbsolutePath().toString() + " already exists on " + dstPath.normalize().toAbsolutePath().toString(), ButtonType.OK);
			alert.show();
			return;
		}
	}
	
	public void btnDeleteAction(ActionEvent actionEvent) {
		PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
		PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");
		
		if(leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "No file choosen", ButtonType.OK);
			alert.show();
			return;
		}
		PanelController srcPC = null;
		if(leftPC.getSelectedFilename() != null) {
			srcPC = leftPC;
		}
		if(rightPC.getSelectedFilename() != null) {
			srcPC = rightPC;
		}
		Path srcPath = Paths.get(srcPC.getCurrentPath(), srcPC.getSelectedFilename());
		try {
			Files.delete(srcPath);
			leftPC.updateList(Paths.get(leftPC.getCurrentPath()));
			rightPC.updateList(Paths.get(rightPC.getCurrentPath()));
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "Can not delete file " + srcPath.normalize().toAbsolutePath().toString(), ButtonType.OK);
			alert.show();
			return;
		}
	}
	
}
