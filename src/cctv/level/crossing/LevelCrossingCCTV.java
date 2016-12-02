package cctv.level.crossing;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    @FXML private Pane levelCrossingSwitch;
    @FXML private Pane powerSwitch;
    @FXML private Pane roadSignalsSwitch;
    @FXML private Circle levelCrossingFailedLight;
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
    @FXML private Region inOrderClickTarget;
    @FXML private Region powerClickTarget;
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
    
    private FadeTransition ft = new FadeTransition(Duration.millis(3000), this.mv);
    private ColorAdjust day = new ColorAdjust();
    
    private ObjectProperty <CameraFunction> cameraSelection = new SimpleObjectProperty<>(CameraFunction.CAMERA_ONE);
    public CameraFunction getCameraSelection () {return this.cameraSelection.get();}
    public void setCameraSelection (CameraFunction cameraFunction) {this.cameraSelection.set(cameraFunction);}
    
    private final static String ALARM_CLIP = "/resources/NeedCrossingClearAlert.wav";
    private final URL audioClipFile = getClass().getResource(ALARM_CLIP);
    private final AudioClip alarmAudio = new AudioClip(audioClipFile.toString());
    
    private Boolean autoLower = false;
    private Boolean autoRaise = false;
    
    private void lowerSequence () {
        
        this.mv.setMediaPlayer(this.mp[1]);
        this.showPicture();
        this.setLevelCrossingActionStatus(LevelCrossingActionStatus.BARRIERS_LOWERING);
        this.mp[1].play();
        this.mp[1].setOnEndOfMedia(()->{
        
            this.barriersDownLight.setFill(Color.YELLOW);
            this.mv.setMediaPlayer(this.mp[5]);
            this.mp[5].play();
            FillTransition ft = new FillTransition (Duration.millis(500), this.crossingClearButtonLight, Color.WHITE, Color.YELLOW);
            ft.setCycleCount(INDEFINITE);
            ft.play();
            this.alarmAudio.setCycleCount(INDEFINITE);
        this.alarmAudio.setVolume(0.5);
        this.alarmAudio.play();
        
        });

        this.barriersUpLight.setFill(Color.SLATEGREY);
        this.roadSignalsWorkingLight.setFill(Color.YELLOW);
        
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
        
        this.status.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                switch ((LevelCrossingActionStatus) newValue) {
                    
                    case BARRIERS_UP:

                        break;
                        
                    case BARRIERS_LOWERING:
     
                        break;
                        
                    case BARRIERS_DOWN_NO_TRAINS:
    
                        break;
                        
                    case BARRIERS_DOWN_TRAIN_LEFT_TO_RIGHT:
          
                        break;
                        
                    case BARRIERS_DOWN_TRAIN_RIGHT_TO_LEFT:
       
                        break;
                        
                    case BARRIERS_RAISING:
   
                        break; 
                    
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
        
        this.inOrderClickTarget.setOnMouseClicked(e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.levelCrossingSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.levelCrossingSwitch.setRotate(45.0);
                    break;
            }
        });
        
        this.powerClickTarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.powerSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.powerSwitch.setRotate(45.0);
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
        
        this.crossingClearClickTarget.setOnMouseClicked(e -> {});
        
        this.lowerButtonClickTarget.setOnMouseClicked(e -> {
        
            if (!this.autoLower) {
            
                lowerSequence();
                
            }

        });
        
        this.pictureButtonClickTarget.setOnMouseClicked(e -> {
        
            if (this.mv.getOpacity() == 0.0 && this.getMonitorPowerOn() && this.getCameraSelection() != CameraFunction.OFF) {

                this.showPicture();
                
                new Thread(() -> {
                
                    try {
                        
                        Thread.sleep (15000);
                        this.hidePicture();

                    } catch (InterruptedException ex) {}
                    
                }).start(); 
                
            }
  
        });
        
        this.stopButtonClickTarget.setOnMouseClicked(e -> {});


      
    }
    
    private synchronized void showPicture() {
        
        ft.setNode(this.mv);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.play();
        

    }
    
    private synchronized void hidePicture() {
        
        ft.setNode(this.mv);
        ft.setToValue(0.0);
        ft.setCycleCount(1);
        ft.play();

    }
    
    /**
     * This method sets the FXML Controller object.
     */
    private void setController() {this.fxmlLoader.setController(this);}
    
    /**
     * This method sets the root object.
     */
    private void setRoot() {this.fxmlLoader.setRoot(this);}
    
}
