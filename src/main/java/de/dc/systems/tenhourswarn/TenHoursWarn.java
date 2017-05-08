package de.dc.systems.tenhourswarn;

import javax.swing.JOptionPane;

import de.dc.systems.tenhourswarn.controller.TimerController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Thomas Reno
 *
 */
public class TenHoursWarn extends Application {

	public static void main(String[] args) {
		try {
			launch();
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(null, t.getClass().getSimpleName() + ": " + t.getMessage());
			throw t;
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Label label = new Label();
		label.setFont(Font.font(20));
		HBox layout = new HBox(4);
		layout.setStyle("-fx-background-color: TRANSPARENT");
		layout.getChildren().add(label);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setScene(new Scene(layout, Color.TRANSPARENT));
		primaryStage.show();
		TimerController ctrl = new TimerController();
		ctrl.run();
	}
}
