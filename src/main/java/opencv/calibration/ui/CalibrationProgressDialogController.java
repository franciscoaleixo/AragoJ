package opencv.calibration.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import opencv.calibration.model.CalibImageItem;
import ui.model.ImageItem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import opencv.calibration.model.CalibrationConfig;
import opencv.calibration.model.CalibrationResults;
import opencv.calibration.tools.CalibrationManager;

import java.io.IOException;
import java.util.List;

public class CalibrationProgressDialogController implements CalibrationManager.CalibrationRunListener {

    @FXML Label progressPrimary;
    @FXML Label progressDescription;
    @FXML VBox container;
    @FXML JFXButton cancelButton;

    private JFXDialog dialog;
    private ProgressListener progressListener;
    private CalibrationManager calibrationManager;

    public CalibrationProgressDialogController(ProgressListener progressListener){
        this.progressListener = progressListener;
    }

    @FXML
    private void initialize(){
        cancelButton.setOnAction(event -> onCancelClick(event));
    }

    @Override
    public void onProgress(int currentImage, int totalImages, String description) {
        Platform.runLater(() -> {
            progressPrimary.setText(currentImage + "/" + totalImages);
            progressDescription.setText(description);
        });

    }

    @Override
    public void onSuccess(CalibrationResults calibrationResults) {
        Platform.runLater(() -> {
            progressDescription.setText("Camera calibration successfully finished!");
            if(progressListener != null) progressListener.onCalibrationResult(calibrationResults);
            if(dialog != null) dialog.close();
        });

    }

    @Override
    public void onFailure() {
        Platform.runLater(() -> {
            if(dialog != null) dialog.close();
            //TODO
        });
    }

    @FXML
    public void onCancelClick(ActionEvent actionEvent) {
        calibrationManager.stopCalibration();
        dialog.close();
    }

    public void startCalibration(CalibrationConfig config, List<CalibImageItem> imageItemList, StackPane stackPane) throws IOException {
        dialog = new JFXDialog(stackPane, container, JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
        calibrationManager = new CalibrationManager(CalibrationProgressDialogController.this);
        calibrationManager.runCalibration(imageItemList, config);

    }

    public interface ProgressListener{
        void onCalibrationResult(CalibrationResults results);
    }

}
