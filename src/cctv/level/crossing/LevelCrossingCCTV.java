package cctv.level.crossing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
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
import javafx.scene.effect.ColorAdjust;
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
    
    private final URL[] mediaFile; 
    private final Media[] videoClip;
    private final MediaPlayer[] mp;

    private ObjectProperty <LevelCrossingActionStatus> status = new SimpleObjectProperty<>(LevelCrossingActionStatus.BARRIERS_UP);
    public LevelCrossingActionStatus getLevelCrossingActionStatus() {return this.status.get();}
    public void setLevelCrossingActionStatus(LevelCrossingActionStatus status){this.status.set(status);}
    
    private BooleanProperty monitorPowerOn = new SimpleBooleanProperty(true);
    public Boolean getMonitorPowerOn () {return this.monitorPowerOn.get();}
    public void setMonitorPowerOn (Boolean monitorPowerOn) {this.monitorPowerOn.set(monitorPowerOn);}
    
    private FadeTransition monitorPictureTransition = new FadeTransition(Duration.millis(3000));
    private ColorAdjust day = new ColorAdjust();
    
    private ObjectProperty <CameraFunction> cameraSelection = new SimpleObjectProperty<>(CameraFunction.CAMERA_ONE);
    public CameraFunction getCameraSelection () {return this.cameraSelection.get();}
    public void setCameraSelection (CameraFunction cameraFunction) {this.cameraSelection.set(cameraFunction);}
    
    private final static String ALARM_CLIP = "/resources/NeedCrossingClearAlert.wav";
    private final URL audioClipFile = getClass().getResource(ALARM_CLIP);
    private final AudioClip alarmAudio = new AudioClip(audioClipFile.toString());
    private final AudioClip powerFailureAlarm = new AudioClip(audioClipFile.toString());
    private final AudioClip barrierDetectionFailureAlarm = new AudioClip(audioClipFile.toString());
    private final AudioClip roadLightsAlarm = new AudioClip(audioClipFile.toString());
    
    private BooleanProperty roadLightsWorking = new SimpleBooleanProperty(true);
    public Boolean getRoadLightsWorking () {return this.roadLightsWorking.get();}
    public void setRoadLightsWorking (Boolean setRoadLightsWorking) {this.roadLightsWorking.set (setRoadLightsWorking);}
    
    private IntegerProperty barrierUpIndicationOffset = new SimpleIntegerProperty (3000);
    public int getBarrierUpIndicationOffset () {return this.barrierUpIndicationOffset.get();}
    public void setBarrierUpIndicationOffset (int barrierUpIndicationOffset) {this.barrierUpIndicationOffset.set (barrierUpIndicationOffset);}
    
    private Boolean autoLower = false;
    private Boolean autoRaise = false;
    private volatile Boolean autoHidePicture = true;
    
    private FillTransition barriersDownFlash = new FillTransition (Duration.millis(500), Color.YELLOW, Color.SLATEGREY);
    private FillTransition crossingClearFlash = new FillTransition (Duration.millis(500), Color.WHITE, Color.YELLOW);
    private FillTransition powerFailureFlash = new FillTransition (Duration.millis(500), Color.SLATEGREY, Color.RED);
    private FillTransition barrierDetectionFailureFlash = new FillTransition (Duration.millis(500), Color.SLATEGREY, Color.RED);
    private FillTransition roadLightsWorkingFlash = new FillTransition (Duration.millis(500), Color.SLATEGREY, Color.YELLOW);
    private FillTransition roadLightsFailedFlash = new FillTransition (Duration.millis(500), Color.SLATEGREY, Color.RED);
    
    private Boolean waitingCrossingClear = false;
    private Boolean crossingClear = false;
    private Boolean lowerSequenceStopped = false;
    
    private Thread lowerSequenceThread;
    
    private void refreshRoadLightsStatus() {
    
        switch (this.getLevelCrossingActionStatus()) {
            
            case BARRIERS_UP:
            case BARRIERS_RAISING:
                
                this.roadLightsFailedFlash.stop();
                this.roadLightsWorkingFlash.stop();
                this.roadSignalsWorkingLight.setFill (Color.SLATEGREY);
                this.roadSignalsFailedLight.setFill (Color.SLATEGREY);
                this.roadLightsAlarm.stop();
                
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
                        this.roadLightsAlarm.play();
                        
                    } else {
                        
                        // Road Lights Working, switch in 'Working' Position.
                        this.roadLightsWorkingFlash.stop();
                        this.roadLightsFailedFlash.stop();
                        this.roadSignalsWorkingLight.setFill (Color.YELLOW);
                        this.roadSignalsFailedLight.setFill (Color.SLATEGREY);
                        this.roadLightsAlarm.stop();
                        
                    }
                    
                } else {
                    
                    if (this.roadSignalsSwitch.getRotate() == 45.0) {
                        
                        // Road Lights not Working, switch in 'Failed' Position.
                        this.roadLightsWorkingFlash.stop();
                        this.roadLightsFailedFlash.stop();
                        this.roadSignalsWorkingLight.setFill (Color.SLATEGREY);
                        this.roadSignalsFailedLight.setFill (Color.RED);
                        this.roadLightsAlarm.stop();
                        
                    } else {
                        
                        // Road Lights Not Working, switch in 'Working' Position.
                        this.roadLightsWorkingFlash.stop();
                        this.roadLightsFailedFlash.play();
                        this.roadSignalsWorkingLight.setFill (Color.SLATEGREY);
                        this.roadLightsAlarm.play();
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
        this.mv.setMediaPlayer(this.mp[1]); // Set the video clip to show the barriers down sequence.
        this.showPicture(); // Show the monitor picture.
        
        

        this.mp[1].play(); // Play the video clip.
        
        new Thread(()->{ // Remove the barriers up lights following after the offset.
        
            try {
                
                Thread.sleep (this.getBarrierUpIndicationOffset()); // Sleep for the amount of time specified by the offset.
                this.setLevelCrossingActionStatus(LevelCrossingActionStatus.BARRIERS_LOWERING); // Set the barriers lowering status.
                this.barriersUpLight.setFill(Color.SLATEGREY); // Remove the Barrier Up Light Indication.
                
            } catch (InterruptedException ex) {} 

        }).start();
        
        // This code block defines what happens when the lower sequence finishes.
        this.mp[1].setOnEndOfMedia(()->{
            
            if (this.getBarriersDownDetectionAvailable()) {
                
                this.barriersDownFlash.stop(); 
                this.barriersDownLight.setFill(Color.YELLOW);
                this.crossingClearFlash.setCycleCount(INDEFINITE);
                this.crossingClearFlash.setShape(this.crossingClearButtonLight);
                this.waitingCrossingClear = true;
                this.crossingClearFlash.play();
                this.alarmAudio.setCycleCount(2);
                this.alarmAudio.setVolume(0.5);
                this.alarmAudio.play();
                this.autoHidePicture = true;
                this.hidePicture();
                
            } else {
                
                this.barriersDownFlash.setShape (this.barriersDownLight);
                this.barriersDownFlash.setCycleCount(INDEFINITE);
                this.barriersDownFlash.play();
                // TODO: Sound Alarm...

            }
            
            this.setLevelCrossingActionStatus(LevelCrossingActionStatus.BARRIERS_DOWN_NO_TRAINS);
            this.mv.setMediaPlayer(this.mp[5]);
            this.mp[5].play();

        });

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
        
        // Setup the BarrierDetectionFailureAlarm audio.
        this.barrierDetectionFailureAlarm.setCycleCount(INDEFINITE);
        this.barrierDetectionFailureAlarm.setVolume (0.5);
        
        // Setup the BarrierDetectionFailureFlash animation.
        this.barrierDetectionFailureFlash.setShape(this.barrierDetectionFailureLight);
        this.barrierDetectionFailureFlash.setCycleCount (INDEFINITE);
        this.barrierDetectionFailureFlash.setAutoReverse(true);
        
        // Setup the PowerFailureAlarm audio.
        this.powerFailureAlarm.setCycleCount (INDEFINITE);
        this.powerFailureAlarm.setVolume(0.5);
        
        // Setup the PowerFailureFlash animation.
        this.powerFailureFlash.setShape(this.powerFailedLight);
        this.powerFailureFlash.setCycleCount (INDEFINITE);
        this.powerFailureFlash.setAutoReverse(true);
        
        // Setup the RoadLightsAlarm audio.
        this.roadLightsAlarm.setCycleCount (INDEFINITE);
        this.roadLightsAlarm.setVolume(0.5);
        
        // Setup the RoadLight Flash animations.
        this.roadLightsFailedFlash.setShape (this.roadSignalsFailedLight);
        this.roadLightsFailedFlash.setCycleCount(INDEFINITE);
        this.roadLightsFailedFlash.setAutoReverse(true);
        this.roadLightsWorkingFlash.setShape (this.roadSignalsWorkingLight);
        this.roadLightsWorkingFlash.setCycleCount(INDEFINITE);
        this.roadLightsWorkingFlash.setAutoReverse(true);
        
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
        
        this.lowerSequenceThread = new Thread (() -> {
        this.lowerSequenceThread.setName("LOWER_SEQUENCE_THREAD");
            

            
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
        
        this.barriersDownDetectionAvailable.addListener (new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                if ((Boolean)newValue && getLevelCrossingActionStatus().toString().contains("BARRIERS_DOWN") ) {
                    
                    
                }
                
            }

        }); 
        
        this.monitorPowerOn.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                if ((Boolean)newValue) {
                    
                    monitorPowerLight.setFill(Color.RED);

                    if (getCameraSelection() != CameraFunction.OFF) {
                        mv.setVisible (true);
                    }
                    
                } else {
                    
                    monitorPowerLight.setFill(Color.SLATEGREY);
                    mv.setVisible(false);
                    
                }
                
            }

        });
        
        this.wiperClicktarget.setOnMouseClicked(e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.wipersSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.wipersSwitch.setRotate(45.0);
                    break;
            }
        });
        
        this.illuminationClicktarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    this.day.setContrast(-0.5);
                    this.illuminationSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    this.day.setContrast(+0.5);
                    this.illuminationSwitch.setRotate(45.0);
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
                    
                    this.camerasSwitch.setRotate(-45.0);
                    this.setCameraSelection(CameraFunction.CAMERA_ONE);
                    break;
                    
                case MIDDLE:
                    this.mv.setVisible(false);
                    this.camerasSwitch.setRotate(0.0);
                    this.setCameraSelection(CameraFunction.OFF);
                    break;
                    
                case SECONDARY:
                    this.mv.setLayoutX(453.0);
                    this.mv.setLayoutY(55.0);
                    
                    if (this.getMonitorPowerOn()) {
                        this.mv.setVisible(true);
                    }
                    
                    this.camerasSwitch.setRotate(45.0);
                    this.setCameraSelection(CameraFunction.CAMERA_TWO);
                    break;          
            }
        });
        
        this.monitorClickTarget.setOnMouseClicked (e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.monitorSwitch.setRotate(-45.0);
                    this.setMonitorPowerOn(true);
                    break;
                    
                case SECONDARY:
                    
                    this.monitorSwitch.setRotate(45.0);
                    this.setMonitorPowerOn(false);
                    break;
            }
        });
        
        this.barrierDetectionFailureSwitchClickTarget.setOnMouseClicked(e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.barrierDetectionFailureSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.barrierDetectionFailureSwitch.setRotate(45.0);
                    break;
            }
        });
        
        this.powerFailureSwitchClickTarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.powerFailureSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.powerFailureSwitch.setRotate(45.0);
                    break;
            }
        });
        
        this.roadSignalsClickTarget.setOnMouseClicked(e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.roadSignalsSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.roadSignalsSwitch.setRotate(45.0);
                    break;
            }
        });
        
        this.raiseSwitchClickTarget.setOnMouseClicked(e -> {
            switch (e.getButton()) {
                
                case PRIMARY:
                    this.autoRaise = true;
                    this.raiseSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    this.autoRaise = false;
                    this.raiseSwitch.setRotate(45.0);
                    break;
            }
        });
        
        this.lowerSwitchClickTarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    this.autoLower = true;
                    this.lowerSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    this.autoLower = false;
                    this.lowerSwitch.setRotate(45.0);
                    break;
            }
        
        });
        
        this.raiseButtonClickTarget.setOnMouseClicked(e -> {});
        
        this.crossingClearClickTarget.setOnMouseClicked(e -> {
        
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
        
            if (!this.autoLower && this.getLevelCrossingActionStatus().equals(LevelCrossingActionStatus.BARRIERS_UP)) {
            
                lowerSequence();
                
            } else if (this.getLevelCrossingActionStatus().equals(LevelCrossingActionStatus.BARRIERS_LOWERING)) {
                
                if (this.mp[1].getStatus().equals(PAUSED)) {
                
                    this.mp[1].play();

                }
                
            }

        });
        
        this.pictureButtonClickTarget.setOnMouseClicked(e -> {
        
            if (this.mv.getOpacity() == 0.0 && this.getMonitorPowerOn() && this.getCameraSelection() != CameraFunction.OFF) {

                this.showPicture();
                
                new Thread(() -> {
                
                    try {
                        
                        Thread.sleep (15000);
                        if (this.autoHidePicture) {
                            
                            this.hidePicture();
                        }

                    } catch (InterruptedException ex) {}
                    
                }).start(); 
                
            }
  
        });
        
        this.stopButtonClickTarget.setOnMouseClicked(e -> {
        
            if (this.getLevelCrossingActionStatus().equals(LevelCrossingActionStatus.BARRIERS_LOWERING)) {
                
                System.out.println("HERE");
                this.mp[1].pause();
                
            }

        });
        
        this.refreshBarrierStatus();

    }
    
    private synchronized void showPicture() {
        
        monitorPictureTransition.setNode(this.mv);
        monitorPictureTransition.setToValue(1.0);
        monitorPictureTransition.setCycleCount(1);
        monitorPictureTransition.play();

    }
    
    private synchronized void hidePicture() {
        
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
    
    private BooleanProperty barriersUpDetectionAvailable = new SimpleBooleanProperty(false);
    public Boolean getBarriersUpDetectionAvailable() {return this.barriersUpDetectionAvailable.get();}
    public void setBarriersUpDetectionAvailable(Boolean barriersUpDetectionAvailable) {this.barriersUpDetectionAvailable.set (barriersUpDetectionAvailable);}
    
    private BooleanProperty barriersFailed = new SimpleBooleanProperty (false);
    public Boolean getBarriersFailed () {return this.barriersFailed.get();}
    public void setBarriersFailed (Boolean barriersFailed) {this.barriersFailed.set (barriersFailed);}
    
    /**
     * This method provides GUI indication regarding the status of the Barrier Detection System.
     */
    private void processBarrierFailureStatus() {
        
        if (this.barrierDetectionFailureSwitch.getRotate() == 45.0) {
            
            if (this.getBarriersFailed()) {
            
                // Barriers failed, switch in 'Failed' position.
                this.barrierDetectionFailureFlash.stop();
                this.barrierDetectionFailureAlarm.stop();
                this.barrierDetectionFailureLight.setFill(Color.RED);
                
            } else {
            
                // Barriers Working, switch in 'Failed' position.
                this.barrierDetectionFailureAlarm.play();
                this.barrierDetectionFailureFlash.play();
                
            }
            
        } else {
            
            if (this.getBarriersFailed()) {
           
                // Barriers failed, switch in 'In Order' position.
                this.barrierDetectionFailureAlarm.play();
                this.barrierDetectionFailureFlash.play();
                
            } else {
            
                // Barriers Working, switch in 'In Order' position.
                this.barrierDetectionFailureFlash.stop();
                this.barrierDetectionFailureAlarm.stop();
                this.barrierDetectionFailureLight.setFill(Color.SLATEGREY);
                
            } 
        }
    }
    
    private void refreshBarrierStatus() {
        
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
    private void processPowerFailureStatus() {
        
        if (this.powerFailureSwitch.getRotate() == 45.0) {
                    
            if (this.getLevelCrossingPower()) {

                // Power available, switch in 'failed' position.
                powerFailureFlash.play();
                powerFailureAlarm.play();

            } else {

                // Power not available, switch in 'failed' position.
                powerFailureAlarm.stop();
                powerFailureFlash.stop();
                powerFailedLight.setFill(Color.RED);

            }
                    
        } else {
                    
            if (this.getLevelCrossingPower()) {

                // Power available, switch in 'on' position.
                powerFailureAlarm.stop();
                powerFailureFlash.stop();
                powerFailedLight.setFill(Color.SLATEGREY);

            } else {

                // Power not available, switch in 'on' position.
                powerFailureAlarm.play();
                powerFailureFlash.play();

            }
        }
    }
    
}
