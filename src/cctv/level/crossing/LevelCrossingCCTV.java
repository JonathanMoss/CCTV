package cctv.level.crossing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;
import static javafx.scene.media.AudioClip.INDEFINITE;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import static javafx.scene.media.MediaPlayer.Status.PAUSED;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * This Class provides a CCTV Level Crossing Control for use on an N/X Panel.
 * @author Jonathan Moss
 * @version v1.0 November 2016
 */
public class LevelCrossingCCTV extends AnchorPane{

    private FXMLLoader fxmlLoader;
    
    @FXML private AnchorPane pane;
    @FXML private Pane lowerButtonPane;
    @FXML private Pane crossingClearButtonPane;
    @FXML private Pane stopButtonPane;
    @FXML private Pane raiseButtonPane;
    @FXML private Pane pictureButtonPane;
    @FXML private Pane wipersSwitch;
    @FXML private Pane illuminationSwitch;
    @FXML private Pane camerasSwitch;
    @FXML private Pane monitorSwitch;
    @FXML private Pane barrierDetectionFailureSwitch;
    @FXML private Pane powerFailureSwitch;
    @FXML private Pane roadSignalsSwitch;
    @FXML private Circle barrierDetectionFailureLight;
    @FXML private Circle roadSignalsFailedLight;
    @FXML private Circle roadSignalsWorkingLight;
    @FXML private Circle powerFailedLight;
    @FXML private Circle barriersUpLight;
    @FXML private Circle barriersDownLight;
    @FXML private Pane raiseSwitch;
    @FXML private Pane lowerSwitch;
    @FXML private Region wiperClicktarget;
    @FXML private Region illuminationClicktarget;
    @FXML private Region camerasClickTarget;
    @FXML private Region monitorClickTarget;
    @FXML private Region barrierDetectionFailureSwitchClickTarget;
    @FXML private Region powerFailureSwitchClickTarget;
    @FXML private Region roadSignalsClickTarget;
    @FXML private Region raiseButtonClickTarget;
    @FXML private Region stopButtonClickTarget;
    @FXML private Region lowerButtonClickTarget;
    @FXML private Region crossingClearClickTarget;
    @FXML private Region pictureButtonClickTarget;
    @FXML private Region raiseSwitchClickTarget;
    @FXML private Region lowerSwitchClickTarget;
    @FXML private MediaView mv;
    @FXML private Rectangle monitorPowerLight;
    @FXML private Circle crossingClearButtonLight;
    @FXML private Circle reminderApplianceSwitch;
    @FXML private Circle reminderApplianceSwitchRaise;
    
    private final URL[] mediaFile; 
    private final Media[] videoClip;
    private final MediaPlayer[] mp;

    private ObjectProperty <LevelCrossingActionStatus> status = new SimpleObjectProperty<>(LevelCrossingActionStatus.BARRIERS_UP);
    public LevelCrossingActionStatus getLevelCrossingActionStatus() {return this.status.get();}
    public void setLevelCrossingActionStatus(LevelCrossingActionStatus status){this.status.set(status);}
    
    private BooleanProperty monitorPowerOn = new SimpleBooleanProperty(true);
    public Boolean getMonitorPowerOn () {return this.monitorPowerOn.get();}
    public void setMonitorPowerOn (Boolean monitorPowerOn) {this.monitorPowerOn.set(monitorPowerOn);}
    
    private ColorAdjust day = new ColorAdjust();
    
    private ObjectProperty <CameraFunction> cameraSelection = new SimpleObjectProperty<>(CameraFunction.CAMERA_ONE);
    public CameraFunction getCameraSelection () {return this.cameraSelection.get();}
    public void setCameraSelection (CameraFunction cameraFunction) {this.cameraSelection.set(cameraFunction);}
    
    private final static int PICTURE_HIDE_MILLISECONDS = 15000;
    private final static String ALARM_CLIP = "/resources/alert.wav";
    private final URL audioClipFile = getClass().getResource(ALARM_CLIP);
    private final AudioClip alarmAudio = new AudioClip(audioClipFile.toString());
    
    private final static String BUTTON_CLICK = "/resources/button.wav";
    private final URL buttonClickFile = getClass().getResource(BUTTON_CLICK);
    private final AudioClip buttonClickAudio = new AudioClip(buttonClickFile.toString());
    
    private final static String SWITCH_CLICK = "/resources/switch.wav";
    private final URL switchFile = getClass().getResource(SWITCH_CLICK);
    private final AudioClip switchAudio = new AudioClip(switchFile.toString());
    
    private IntegerProperty barrierUpIndicationOffset = new SimpleIntegerProperty (3000);
    public int getBarrierUpIndicationOffset () {return this.barrierUpIndicationOffset.get();}
    public void setBarrierUpIndicationOffset (int barrierUpIndicationOffset) {this.barrierUpIndicationOffset.set (barrierUpIndicationOffset);}
    
    private Boolean autoLower = false;
    private Boolean autoRaise = false;
    private volatile Boolean autoHidePicture = true;
    
    private FillTransition barriersIndicationFlash = new FillTransition (Duration.millis(1000), Color.SLATEGREY, Color.YELLOW);
    private FillTransition crossingClearFlash = new FillTransition (Duration.millis(1000), Color.WHITE, Color.YELLOW);
    private FillTransition powerFailureFlash = new FillTransition (Duration.millis(1000), Color.SLATEGREY, Color.RED);
    private FillTransition barrierDetectionFailureFlash = new FillTransition (Duration.millis(1000), Color.SLATEGREY, Color.RED);
    private FillTransition roadLightsWorkingFlash = new FillTransition (Duration.millis(1000), Color.SLATEGREY, Color.YELLOW);
    private FillTransition roadLightsFailedFlash = new FillTransition (Duration.millis(1000), Color.SLATEGREY, Color.RED);
    private FillTransition monitorOn = new FillTransition (Duration.millis(500), Color.SLATEGREY, Color.RED);
    private FillTransition monitorOff = new FillTransition (Duration.millis(500), Color.RED, Color.SLATEGREY);
    private FadeTransition monitorPictureTransition = new FadeTransition(Duration.millis(2000));
    
    private Boolean waitingCrossingClear = false;
    private Boolean crossingClear = false;
    private Thread alertThread;
    
    private Thread autoHidePictureThread;
    private long pictureOnMilli = 0;
    
    private WritableImage switchReminderSnapshot;
    private SnapshotParameters parameters = new SnapshotParameters();

    
    private ArrayList <String> audioAlertMap = new ArrayList<>();
    
    private void reverseBarrierDownMediaClip () {
        
        this.mv.getMediaPlayer().pause();
        barriersDownLight.setFill(Color.SLATEGREY);
        barriersIndicationFlash.stop();
        barriersIndicationFlash.setToValue(Color.RED);
        barriersIndicationFlash.setShape (barriersUpLight);
        barriersIndicationFlash.play();
        
        new Thread(()->{

            double currentTime = Math.round(this.mv.getMediaPlayer().getCurrentTime().toMillis());

            while (this.mv.getMediaPlayer().getCurrentTime() != Duration.millis(0.0)) {
        
                try {
                
                    Thread.sleep (40);
                    currentTime = currentTime - 125;
                    mv.getMediaPlayer().seek (Duration.millis(currentTime));
                
                } catch (InterruptedException ex) {}
            
            }
            
            this.setLevelCrossingActionStatus(LevelCrossingActionStatus.BARRIERS_UP);
            this.autoHidePicture = true;
            
        }).start();
 
    }
    
    public LevelCrossingCCTV () throws MalformedURLException {

        // Get a reference to the FXML file.
        this.fxmlLoader = new FXMLLoader(getClass().getResource("LevelCrossingCCTV.fxml"));
        
        // Set the root and Controller Objects
        this.setRoot();
        this.setController();
        
        // Attempt to load the FXML file.
        try {
            fxmlLoader.load();
        } catch (IOException e) {}
        
        parameters.setFill(Color.TRANSPARENT);
        switchReminderSnapshot = this.reminderApplianceSwitch.snapshot(parameters, null);
        
        this.autoHidePictureThread = new Thread(()->{
        
            while (true) {
                
                try {
                    
                    Thread.sleep (1000);
                    Long picShowingFor = System.currentTimeMillis() - this.pictureOnMilli;

                    if (this.autoHidePicture && picShowingFor > PICTURE_HIDE_MILLISECONDS) {
                        
                        this.hidePicture();
                        
                    }
                    
                } catch (InterruptedException ex) {
                   
                }
            }
        });
        
        // This is the TARGET for a Switch Reminder Appliance for the Raise Switch - Dragging Over
        this.raiseSwitchClickTarget.setOnDragOver(e -> {
            
            e.acceptTransferModes(TransferMode.MOVE);
            e.consume();
        
        });
        
        // This is the TARGET for a Switch Reminder Appliance for the Raise Switch - Dropped
        this.raiseSwitchClickTarget.setOnDragDropped(e -> {
            
            // Show the reminder appliance in situation.
            this.reminderApplianceSwitchRaise.setDisable(false);
            this.reminderApplianceSwitchRaise.setVisible(true);
            e.setDropCompleted(true);
            e.consume();
            
        });
        
        // This is the SOURCE for when a user wants to remove the reminder appliance from the Raise Switch.
        this.reminderApplianceSwitchRaise.setOnDragDetected(e -> {
        
            Dragboard db = this.reminderApplianceSwitchRaise.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(this.switchReminderSnapshot);
            db.setDragView(this.switchReminderSnapshot);
            db.setContent(content);
            this.reminderApplianceSwitchRaise.setDisable(true);
            this.reminderApplianceSwitchRaise.setVisible(false);
            e.consume();

        });
        
        // This is the SOURCE for when a user wants to apply a reminder appliance
        this.reminderApplianceSwitch.setOnDragDetected(e -> { // Source
        
            Dragboard db = this.reminderApplianceSwitch.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(this.switchReminderSnapshot);
            db.setDragView(this.switchReminderSnapshot);
            db.setContent(content);
            e.consume();
        
        });
        
        // This is the TARGET for placing a reminder appliance back 'in the pile'
        this.reminderApplianceSwitch.setOnDragOver(e -> {
            
            e.acceptTransferModes(TransferMode.MOVE);
            e.consume();
        
        });
        
        // This is the TARGET for placing a reminder appliance back 'in the pile'
        this.reminderApplianceSwitch.setOnDragDropped(e -> {
        
            e.setDropCompleted(true);
            e.consume();
        
        });

        this.autoHidePictureThread.setDaemon(true);
        this.autoHidePictureThread.setName("AUTO_HIDE_PICTURE_THREAD");
        this.autoHidePictureThread.start();
       
        // This thread takes care of the audiable alarm.
        this.alertThread = new Thread(() -> {
        
            this.alarmAudio.setVolume(0.5);
            this.alarmAudio.setCycleCount(INDEFINITE);
            this.alarmAudio.setRate(1.0);
            
            while (true) {
                
                try {
                    
                    Thread.sleep(1000);
                    
                } catch (InterruptedException ex) {}
                
                if (!this.audioAlertMap.isEmpty()) {
                    
                    if (!this.alarmAudio.isPlaying()) {
                        
                        this.alarmAudio.play();
                        
                    }
                    
                } else {
                    
                    this.alarmAudio.stop();
                    
                }
            }
        });
        
        this.alertThread.setDaemon(true);
        this.alertThread.setName("ALARM_ALERT_THREAD");
        this.alertThread.start();

        // Setup the BarrierDetectionFailureFlash animation.
        this.barrierDetectionFailureFlash.setShape(this.barrierDetectionFailureLight);
        this.barrierDetectionFailureFlash.setCycleCount (INDEFINITE);

        // Setup the PowerFailureFlash animation.
        this.powerFailureFlash.setShape(this.powerFailedLight);
        this.powerFailureFlash.setCycleCount (INDEFINITE);

        // Setup the RoadLight Flash animations.
        this.roadLightsFailedFlash.setShape (this.roadSignalsFailedLight);
        this.roadLightsFailedFlash.setCycleCount(INDEFINITE);
        
        this.roadLightsWorkingFlash.setShape (this.roadSignalsWorkingLight);
        this.roadLightsWorkingFlash.setCycleCount(INDEFINITE);
        
        // Setup the CrossingClear Button Flash animation.
        this.crossingClearFlash.setShape(this.crossingClearButtonLight);
        this.crossingClearFlash.setCycleCount(INDEFINITE);
        
        // Setup the Monitor Power Light animation.
        this.monitorOn.setShape(this.monitorPowerLight);
        this.monitorOn.setCycleCount(1);
        this.monitorOff.setShape(this.monitorPowerLight);
        this.monitorOff.setCycleCount(1);
        
        this.barriersIndicationFlash.setCycleCount(INDEFINITE);
        
        this.status.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                
                if (!oldValue.toString().contains("BARRIERS_DOWN")) {
                    refreshRoadLightsStatus();
                }
                
                refreshBarrierStatus();
                processBarrierFailureStatus();
                
                switch ((LevelCrossingActionStatus)newValue) {
                    
                    case BARRIERS_UP:
                        
                        if (getBarriersUpDetectionAvailable()) {
                        
                            barriersIndicationFlash.stop();
                            barriersUpLight.setFill(Color.RED);
                            barriersDownLight.setFill(Color.SLATEGREY);
                            
                        } else {
                        
                            barriersDownLight.setFill(Color.SLATEGREY);
                            barriersIndicationFlash.stop();
                            barriersIndicationFlash.setToValue(Color.RED);
                            barriersIndicationFlash.setShape (barriersUpLight);
                            barriersIndicationFlash.play();
                            
                        }
                        
                        break;
                        
                    case BARRIERS_LOWERING:
                        
                        new Thread(()->{ // Remove the barriers up lights following after the offset.
        
                            try {
                
                                Thread.sleep (getBarrierUpIndicationOffset()); // Sleep for the amount of time specified by the offset.
                                barriersUpLight.setFill(Color.SLATEGREY);
                                barriersIndicationFlash.stop();
                                barriersIndicationFlash.setToValue(Color.YELLOW);
                                barriersIndicationFlash.setShape (barriersDownLight);
                                barriersIndicationFlash.play();

                            } catch (InterruptedException ex) {} 

                        }).start();
                        
                        
                        break;
                        
                    case BARRIERS_RAISING:
                        
                        crossingClearFlash.stop();
                        crossingClearButtonLight.setFill (Color.WHITE);
                        crossingClear = false;
                        waitingCrossingClear = false;
                        
                        new Thread(()->{
                        
                            try {
                        
                            Thread.sleep (getBarrierUpIndicationOffset() * 2); // Sleep for the amount of time specified by the offset.
                            refreshRoadLightsStatus();
                        
                            } catch (InterruptedException ex) {}
                        
                        }).start();

                        barriersDownLight.setFill(Color.SLATEGREY);
                        barriersIndicationFlash.stop();
                        barriersIndicationFlash.setToValue(Color.RED);
                        barriersIndicationFlash.setShape (barriersUpLight);
                        barriersIndicationFlash.play();
                        break;
                        
                    case BARRIERS_DOWN_NO_TRAINS:
                    case BARRIERS_DOWN_TRAIN_LEFT_TO_RIGHT:
                    case BARRIERS_DOWN_TRAIN_RIGHT_TO_LEFT:
                        
                        if (getBarriersDownDetectionAvailable()) {
                            
                            barriersIndicationFlash.stop();
                            barriersUpLight.setFill(Color.SLATEGREY);
                            barriersDownLight.setFill(Color.YELLOW);
                            
                            if (waitingCrossingClear) {
                                
                                crossingClearFlash.play();
                                
                            }
                            
                        } else {
                            
                            barriersUpLight.setFill(Color.SLATEGREY);
                            barriersIndicationFlash.stop();
                            barriersIndicationFlash.setToValue(Color.YELLOW);
                            barriersIndicationFlash.setShape (barriersDownLight);
                            barriersIndicationFlash.play();
                            
                        }
                        
                        break;
                    
                }
                
            }
        
        
        });
        
        this.roadSignalsSwitch.rotateProperty().addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                refreshRoadLightsStatus();
                
            }
 
        });
        
        // This code block ensures that any time the 'Barrier Detection' (Level Crossing) switch is operated on the GUI, the
        // relevant method is called to update the status of the control / system.
        this.barrierDetectionFailureSwitch.rotateProperty().addListener(new ChangeListener() {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                processBarrierFailureStatus();
                
            }
        });
        
        // This code block adds a listener to the BarriersFailed property, and ensures that the relevant method
        // is called should any changes be made.
        this.barriersFailed.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            
                processBarrierFailureStatus();
                
            }
        });
        
        this.powerFailureSwitch.rotateProperty().addListener (new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                processPowerFailureStatus();
                
            }

        });
        
        this.levelCrossingPower.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
              
                processPowerFailureStatus();
                
            }

        });
        
        //mp.setMute(true);
        mv.setOpacity(0.0);
        mv.setVisible(true);
        mv.setEffect(day);
        
        this.mediaFile = new URL [MediaChapter.values().length];
        this.videoClip = new Media [MediaChapter.values().length];
        this.mp = new MediaPlayer [MediaChapter.values().length];
        
        for (int i = 0; i < MediaChapter.values().length; i++) {
            
            URL tempURL = getClass().getResource(MediaChapter.values()[i].getResource());
            this.mediaFile[i] = new URL (tempURL.toString());
            this.videoClip[i] = new Media (this.mediaFile[i].toString());
            this.mp[i] = new MediaPlayer (this.videoClip[i]);

        }
        
        this.mv.setMediaPlayer(this.mp[0]);
        this.day.setContrast(-0.5);
        this.day.setSaturation(-1.0);
        
        this.monitorPowerOn.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                if ((Boolean)newValue) {
                    
                    monitorOn.play();

                    if (getCameraSelection() != CameraFunction.OFF) {
                        mv.setVisible (true);
                    }
                    
                } else {
                    
                    monitorOff.play();
                    mv.setVisible(false);
                    
                }
                
            }

        });
        
        this.wiperClicktarget.setOnMouseClicked(e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.rotateButton(this.wipersSwitch, -45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.rotateButton(this.wipersSwitch, 45.0);
                    break;
            }
        });
        
        this.illuminationClicktarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    this.day.setContrast(-0.5);
                    this.rotateButton(this.illuminationSwitch, -45.0);
                    break;
                    
                case SECONDARY:
                    this.day.setContrast(+0.5);
                    this.rotateButton(this.illuminationSwitch, 45.0);
                    break;
            }
        });
        
        this.camerasClickTarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    this.mv.setLayoutX(452.0);
                    this.mv.setLayoutY(54.0);
                    
                    if (this.getMonitorPowerOn()) {
                        this.mv.setVisible(true);
                    }
                    
                    this.rotateButton(this.camerasSwitch, -45.0);
                    this.setCameraSelection(CameraFunction.CAMERA_ONE);
                    break;
                    
                case MIDDLE:
                    this.mv.setVisible(false);
                    this.rotateButton(this.camerasSwitch, 0.0);
                    this.setCameraSelection(CameraFunction.OFF);
                    break;
                    
                case SECONDARY:
                    this.mv.setLayoutX(453.0);
                    this.mv.setLayoutY(55.0);
                    
                    if (this.getMonitorPowerOn()) {
                        this.mv.setVisible(true);
                    }
                    
                    this.rotateButton(this.camerasSwitch, 45.0);
                    this.setCameraSelection(CameraFunction.CAMERA_TWO);
                    break;          
            }
        });
        
        this.monitorClickTarget.setOnMouseClicked (e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.rotateButton(this.monitorSwitch, -45.0);
                    this.setMonitorPowerOn(true);
                    break;
                    
                case SECONDARY:
                    
                    this.rotateButton(this.monitorSwitch, 45.0);
                    this.setMonitorPowerOn(false);
                    break;
            }
        });
        
        this.barrierDetectionFailureSwitchClickTarget.setOnMouseClicked(e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.rotateButton(this.barrierDetectionFailureSwitch, -45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.rotateButton(this.barrierDetectionFailureSwitch, 45.0);
                    break;
            }
        });
        
        this.powerFailureSwitchClickTarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.rotateButton(this.powerFailureSwitch, -45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.rotateButton(this.powerFailureSwitch, 45.0);
                    break;
            }
        });
        
        this.roadSignalsClickTarget.setOnMouseClicked(e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.rotateButton(this.roadSignalsSwitch, -45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.rotateButton(this.roadSignalsSwitch, 45.0);
                    break;
            }
        });
        
        this.raiseSwitchClickTarget.setOnMouseClicked(e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    this.autoRaise = true;
                    this.rotateButton(this.raiseSwitch, -45.0);
                    break;
                    
                case SECONDARY:
                    this.autoRaise = false;
                    this.rotateButton(this.raiseSwitch, 45.0);
                    break;
            }
        });
        
        this.lowerSwitchClickTarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    this.autoLower = true;
                    this.rotateButton(this.lowerSwitch, -45.0);
                    break;
                    
                case SECONDARY:
                    this.autoLower = false;
                    this.rotateButton(this.lowerSwitch, 45.0);
                    break;
            }
        
        });
        
        this.raiseButtonClickTarget.setOnMouseClicked(e -> {
            
            if (e.getButton() == MouseButton.PRIMARY) {
                
                this.pushButton(this.raiseButtonPane);
                this.buttonClickAudio.play();
                
            }
            
            if (!this.autoRaise && this.getLevelCrossingActionStatus().toString().contains("BARRIERS_DOWN")) {
            
                this.raiseSequence();
                
            } else if (this.getLevelCrossingActionStatus().equals(LevelCrossingActionStatus.BARRIERS_RAISING)) {
                
                if (this.mp[2].getStatus().equals(PAUSED)) {
                
                    this.mp[2].play();

                }
                
            } else if (this.getLevelCrossingActionStatus().equals(LevelCrossingActionStatus.BARRIERS_LOWERING)) {
             
                this.reverseBarrierDownMediaClip();
                
            }

        
        });
        
        this.crossingClearClickTarget.setOnMouseClicked(e -> {
        
            if (e.getButton() == MouseButton.PRIMARY) {
                
                this.pushButton(this.crossingClearButtonPane);
                this.buttonClickAudio.play();
                
            }
            
            if (this.mv.isVisible() && this.mv.getOpacity() == 1.0) {
            
                if (this.waitingCrossingClear) {
                    
                    this.waitingCrossingClear = false;
                    this.crossingClear = true;
                    this.crossingClearFlash.stop();
                    this.crossingClearButtonLight.setFill (Color.YELLOW);
                    this.hidePicture();
                }
                
            }

        });
        
        this.lowerButtonClickTarget.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY) {
                
                this.pushButton(this.lowerButtonPane);
                this.buttonClickAudio.play();
                
            }
            
            if (!this.autoLower && this.getLevelCrossingActionStatus().equals(LevelCrossingActionStatus.BARRIERS_UP)) {
            
                lowerSequence();
                
            } else if (this.getLevelCrossingActionStatus().equals(LevelCrossingActionStatus.BARRIERS_LOWERING)) {
                
                if (this.mp[1].getStatus().equals(PAUSED)) {
                
                    this.mp[1].play();

                }
                
            }

        });
        
        this.pictureButtonClickTarget.setOnMouseClicked(e -> {
        
            if (e.getButton() == MouseButton.PRIMARY) {
                
                this.pushButton(this.pictureButtonPane);
                this.buttonClickAudio.play();
                
            }
            
            this.pictureOnMilli = System.currentTimeMillis();
            this.showPicture();
            
        });
        
        this.stopButtonClickTarget.setOnMouseClicked(e -> {
        
            if (e.getButton() == MouseButton.PRIMARY) {
                
                this.pushButton(this.stopButtonPane);
                this.buttonClickAudio.play();
                
            }
            
            if (this.getLevelCrossingActionStatus().equals(LevelCrossingActionStatus.BARRIERS_LOWERING)) {
                
                this.mp[1].pause();
                
            } else if (this.getLevelCrossingActionStatus().equals(LevelCrossingActionStatus.BARRIERS_RAISING)) {
                
                this.mp[2].pause();
                
            }

        });
        
        this.refreshBarrierStatus();
        this.processBarrierFailureStatus();
        this.refreshRoadLightsStatus();
        this.processPowerFailureStatus();

    }
    
    private void rotateButton (Node node, double angle) {
        
        RotateTransition rt = new RotateTransition(Duration.millis(150), node);
        rt.setToAngle(angle);
        rt.play();
        this.switchAudio.play();
        
    } 
    
    private void pushButton (Node node) {
    
        ScaleTransition st = new ScaleTransition(Duration.millis(250), node);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setFromX(0.9);
        st.setFromY(0.9);
        st.setAutoReverse(true);
        st.play();

    }
    
    private synchronized void showPicture() {
        
        if (this.getMonitorPowerOn() && this.getCameraSelection() != CameraFunction.OFF) {
            
            monitorPictureTransition.stop();
            monitorPictureTransition.setNode(this.mv);
            monitorPictureTransition.setToValue(1.0);
            monitorPictureTransition.setCycleCount(1);
            monitorPictureTransition.play();
      
        }

    }
    
    private synchronized void hidePicture() {
        
        monitorPictureTransition.stop();
        monitorPictureTransition.setNode(this.mv);
        monitorPictureTransition.setToValue(0.0);
        monitorPictureTransition.setCycleCount(1);
        monitorPictureTransition.play();

    }
    
    /**
     * This method sets the FXML Controller object.
     */
    private void setController() {this.fxmlLoader.setController(this);}
    
    /**
     * This method sets the root object.
     */
    private void setRoot() {this.fxmlLoader.setRoot(this);}
    
    private BooleanProperty barriersDownDetectionAvailable = new SimpleBooleanProperty(true);
    public Boolean getBarriersDownDetectionAvailable() {return this.barriersDownDetectionAvailable.get();}
    public void setBarriersDownDetectionAvailable(Boolean barriersDownDetectionAvailable) {this.barriersDownDetectionAvailable.set (barriersDownDetectionAvailable);}
    
    private BooleanProperty barriersUpDetectionAvailable = new SimpleBooleanProperty(true);
    public Boolean getBarriersUpDetectionAvailable() {return this.barriersUpDetectionAvailable.get();}
    public void setBarriersUpDetectionAvailable(Boolean barriersUpDetectionAvailable) {this.barriersUpDetectionAvailable.set (barriersUpDetectionAvailable);}
    
    private BooleanProperty barriersFailed = new SimpleBooleanProperty (false);
    public Boolean getBarriersFailed () {return this.barriersFailed.get();}
    public void setBarriersFailed (Boolean barriersFailed) {this.barriersFailed.set (barriersFailed);}
    
    private synchronized void stopAudioAlert(String value) {
       
        if (this.audioAlertMap.contains(value)) {
            
            this.audioAlertMap.remove(value);
            
        }
        
        
    }
    
    private synchronized void startAudioAlert(String value) {

        if (!this.audioAlertMap.contains(value)) {
            
            this.audioAlertMap.add(value);
            
        }
   
    }
    /**
     * This method provides GUI indication regarding the status of the Barrier Detection System.
     */
    private synchronized void processBarrierFailureStatus() {
        
        if (this.barrierDetectionFailureSwitch.getRotate() == 45.0) {
            
            if (this.getBarriersFailed()) {
            
                // Barriers failed, switch in 'Failed' position.
                this.barrierDetectionFailureFlash.stop();
                this.stopAudioAlert("BarrierDetectionFailure");
                this.barrierDetectionFailureLight.setFill(Color.RED);
                
            } else {
            
                // Barriers Working, switch in 'Failed' position.
                this.startAudioAlert("BarrierDetectionFailure");
                this.barrierDetectionFailureFlash.play();
                
            }
            
        } else {
            
            if (this.getBarriersFailed()) {
           
                // Barriers failed, switch in 'In Order' position.
                this.startAudioAlert("BarrierDetectionFailure");
                this.barrierDetectionFailureFlash.play();
                
                if (this.getLevelCrossingActionStatus().toString().contains("BARRIERS_UP")) {
        
                    barriersDownLight.setFill(Color.SLATEGREY);
                    barriersIndicationFlash.stop();
                    barriersIndicationFlash.setToValue(Color.RED);
                    barriersIndicationFlash.setShape (barriersUpLight);
                    barriersIndicationFlash.play();
            
                }
                
            } else {
            
                // Barriers Working, switch in 'In Order' position.
                this.barrierDetectionFailureFlash.stop();
                this.stopAudioAlert("BarrierDetectionFailure");
                this.barrierDetectionFailureLight.setFill(Color.SLATEGREY);
                
            } 
        }
        
        
    }
    
    private synchronized void refreshBarrierStatus() {
        
        switch (this.getLevelCrossingActionStatus()) {
            
            case BARRIERS_UP:
                
                if (this.getBarriersUpDetectionAvailable()) {
                    
                    this.setBarriersFailed (false);
                    
                } else {
                    
                    this.setBarriersFailed (true);
                }
                break;
                
            case BARRIERS_DOWN_NO_TRAINS:
            case BARRIERS_DOWN_TRAIN_LEFT_TO_RIGHT:
            case BARRIERS_DOWN_TRAIN_RIGHT_TO_LEFT:
                
                if (this.getBarriersDownDetectionAvailable()) {
                    
                    this.setBarriersFailed (false);
                    
                } else {
                    
                    this.setBarriersFailed (true);
                }
                break;
                
            case BARRIERS_LOWERING:
            case BARRIERS_RAISING:
                
                this.setBarriersFailed (false);
                break;
        }
    }
    
    private BooleanProperty levelCrossingPower = new SimpleBooleanProperty (true);
    public Boolean getLevelCrossingPower () {return this.levelCrossingPower.get();}
    public void setLevelCrossingPower (Boolean levelCrossingPower) {this.levelCrossingPower.set (levelCrossingPower);}
    
    /**
     * This method provides GUI indication regarding the status of the Level Crossing Power Supply System.
     */
    private synchronized void processPowerFailureStatus() {
        
        if (this.powerFailureSwitch.getRotate() == 45.0) {
                    
            if (this.getLevelCrossingPower()) {

                // Power available, switch in 'failed' position.
                powerFailureFlash.play();
                this.startAudioAlert("PowerFailure");

            } else {

                // Power not available, switch in 'failed' position.
                this.stopAudioAlert("PowerFailure");
                powerFailureFlash.stop();
                powerFailedLight.setFill(Color.RED);

            }
                    
        } else {
                    
            if (this.getLevelCrossingPower()) {

                // Power available, switch in 'on' position.
                this.stopAudioAlert("PowerFailure");
                powerFailureFlash.stop();
                powerFailedLight.setFill(Color.SLATEGREY);

            } else {

                // Power not available, switch in 'on' position.
                this.startAudioAlert("PowerFailure");
                powerFailureFlash.play();

            }
        }
    }
    
    private BooleanProperty roadLightsWorking = new SimpleBooleanProperty(true);
    public Boolean getRoadLightsWorking () {return this.roadLightsWorking.get();}
    public void setRoadLightsWorking (Boolean setRoadLightsWorking) {this.roadLightsWorking.set (setRoadLightsWorking);}
    
    private synchronized void refreshRoadLightsStatus() {
    
        switch (this.getLevelCrossingActionStatus()) {
            
            case BARRIERS_UP:
            case BARRIERS_RAISING:
                
                if (this.roadSignalsSwitch.getRotate() == 45.0) {
                    
                    this.roadLightsFailedFlash.play();
                    this.roadLightsWorkingFlash.stop();
                    this.roadSignalsWorkingLight.setFill (Color.SLATEGREY);
                    this.startAudioAlert("RoadSignals");
                    
                } else {
                    
                    this.roadLightsFailedFlash.stop();
                    this.roadLightsWorkingFlash.stop();
                    this.roadSignalsWorkingLight.setFill (Color.SLATEGREY);
                    this.roadSignalsFailedLight.setFill (Color.SLATEGREY);
                    this.stopAudioAlert("RoadSignals");
                    
                }

                break;
                
            case BARRIERS_DOWN_NO_TRAINS:
            case BARRIERS_DOWN_TRAIN_LEFT_TO_RIGHT:
            case BARRIERS_DOWN_TRAIN_RIGHT_TO_LEFT:
            case BARRIERS_LOWERING:
                
                if (this.getRoadLightsWorking()) {
                    
                    if (this.roadSignalsSwitch.getRotate() == 45.0) {
                        
                        // Road Lights Working, switch in 'Failed' Position.
                        this.roadLightsWorkingFlash.play();
                        this.roadLightsFailedFlash.stop();
                        this.roadSignalsFailedLight.setFill (Color.SLATEGREY);
                        this.startAudioAlert("RoadSignals");
                        
                    } else {
                        
                        // Road Lights Working, switch in 'Working' Position.
                        this.roadLightsWorkingFlash.stop();
                        this.roadLightsFailedFlash.stop();
                        this.roadSignalsWorkingLight.setFill (Color.YELLOW);
                        this.roadSignalsFailedLight.setFill (Color.SLATEGREY);
                        this.stopAudioAlert("RoadSignals");
                        
                    }
                    
                } else {
                    
                    if (this.roadSignalsSwitch.getRotate() == 45.0) {
                        
                        // Road Lights not Working, switch in 'Failed' Position.
                        this.roadLightsWorkingFlash.stop();
                        this.roadLightsFailedFlash.stop();
                        this.roadSignalsWorkingLight.setFill (Color.SLATEGREY);
                        this.roadSignalsFailedLight.setFill (Color.RED);
                        this.stopAudioAlert("RoadSignals");
                        
                    } else {
                        
                        // Road Lights Not Working, switch in 'Working' Position.
                        this.roadLightsWorkingFlash.stop();
                        this.roadLightsFailedFlash.play();
                        this.roadSignalsWorkingLight.setFill (Color.SLATEGREY);
                        this.startAudioAlert("RoadSignals");
                    }
                    
                }
                break;
        }        
    }
    
    /**
     * This method simulates the lower sequence of the barriers.
     */
    private void lowerSequence () {
        
        this.autoHidePicture = false; // Stop the picture being hidden after the requisite time.
        this.stopRemoveVideoClip();
        this.mv.setMediaPlayer(this.mp[1]); // Set the video clip to show the barriers down sequence.
        // This code block defines what happens when the lower sequence finishes.
        this.mp[1].setOnEndOfMedia(()->{

            this.autoHidePicture = true;
            this.waitingCrossingClear = true;
            this.setLevelCrossingActionStatus(LevelCrossingActionStatus.BARRIERS_DOWN_NO_TRAINS);
            this.mv.setMediaPlayer(this.mp[5]);
            this.mp[5].play();
            //this.hidePicture();

        });
        
        this.mp[1].play(); // Play the video clip.
        this.showPicture(); // Show the monitor picture.
        
        this.setLevelCrossingActionStatus(LevelCrossingActionStatus.BARRIERS_LOWERING);

    }
    
    private void raiseSequence () {
        
        this.autoHidePicture = false;
        this.stopRemoveVideoClip();
        this.mv.setMediaPlayer (this.mp[2]);
        this.mp[2].setOnEndOfMedia(() -> {
        
            this.setLevelCrossingActionStatus(LevelCrossingActionStatus.BARRIERS_UP);
            this.stopRemoveVideoClip();
            this.autoHidePicture = true;
            this.mv.setMediaPlayer(this.mp[0]);
            this.mp[0].play();
            //this.hidePicture();
        
        });
        
        this.mp[2].play();
        this.showPicture();
        
        new Thread (() -> {
            
            try {
                
                Thread.sleep(4000);
                this.setLevelCrossingActionStatus(LevelCrossingActionStatus.BARRIERS_RAISING);
                
            } catch (InterruptedException ex) {}
        
        }).start();

    }
    
    private void stopRemoveVideoClip() {
        
        this.mv.setMediaPlayer(null);
        
        for (MediaPlayer mp1 : this.mp) {
            mp1.stop();
            mp1.seek(Duration.ZERO);
        }
                
    }
}
