package cctv.level.crossing;


import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Circle;
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
    
    private final static String MEDIA = "/resources/media.mp4";
    private final URL mediaFile = getClass().getResource(MEDIA);
    private final Media videoClip = new Media(this.mediaFile.toString());
    private final MediaPlayer mp = new MediaPlayer (this.videoClip);
    
    private ObjectProperty <LevelCrossingActionStatus> status = new SimpleObjectProperty<>(LevelCrossingActionStatus.BARRIERS_UP);
    public LevelCrossingActionStatus getLevelCrossingActionStatus() {return this.status.get();}
    public void setLevelCrossingActionStatus(LevelCrossingActionStatus status){this.status.set(status);}
    
    private BooleanProperty mediaVisible = new SimpleBooleanProperty (false);
    public Boolean getMediaVisible() {return this.mediaVisible.get();}
    public void setMediaVisible(Boolean mediaVisible){this.mediaVisible.set(mediaVisible);}
    
    private Duration startTime = new Duration(MediaChapter.TRAIN_LEFT_TO_RIGHT.getStart());
    private Duration endTime = new Duration(MediaChapter.TRAIN_LEFT_TO_RIGHT.getEnd());
    
    public LevelCrossingCCTV () {
        
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
        this.mv.visibleProperty().bindBidirectional(mediaVisible);

        this.status.addListener(new ChangeListener () {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                switch ((LevelCrossingActionStatus) newValue) {
                    
                    case BARRIERS_UP:
                        startTime = new Duration(MediaChapter.BARRIERS_UP_STILL.getStart());
                        endTime = new Duration(MediaChapter.BARRIERS_UP_STILL.getEnd());
                        break;
                        
                    case BARRIERS_LOWERING:
                        startTime = new Duration(MediaChapter.LOWER_SEQUENCE.getStart());
                        endTime = new Duration(MediaChapter.LOWER_SEQUENCE.getEnd());
                        break;
                        
                    case BARRIERS_DOWN_NO_TRAINS:
                        startTime = new Duration(MediaChapter.BARRIERS_DOWN.getStart());
                        endTime = new Duration(MediaChapter.BARRIERS_DOWN.getEnd());
                        break;
                        
                    case BARRIERS_DOWN_TRAIN_LEFT_TO_RIGHT:
                        startTime = new Duration(MediaChapter.TRAIN_LEFT_TO_RIGHT.getStart());
                        endTime = new Duration(MediaChapter.TRAIN_LEFT_TO_RIGHT.getEnd());
                        break;
                        
                    case BARRIERS_DOWN_TRAIN_RIGHT_TO_LEFT:
                        startTime = new Duration(MediaChapter.TRAIN_RIGHT_TO_LEFT.getStart());
                        endTime = new Duration(MediaChapter.TRAIN_RIGHT_TO_LEFT.getEnd());
                        break;
                        
                    case BARRIERS_RAISING:
                        startTime = new Duration(MediaChapter.RAISE_SEQUENCE.getStart());
                        endTime = new Duration(MediaChapter.RAISE_SEQUENCE.getEnd());
                        break; 
                    
                }
                
                mp.seek(startTime);
                
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
                    
                    this.illuminationSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.illuminationSwitch.setRotate(45.0);
                    break;
            }
        });
        
        this.camerasClickTarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.camerasSwitch.setRotate(-45.0);
                    break;
                    
                case MIDDLE:
                    
                    this.camerasSwitch.setRotate(0.0);
                    break;
                    
                case SECONDARY:
                    
                    this.camerasSwitch.setRotate(45.0);
                    break;          
            }
        });
        
        this.monitorClickTarget.setOnMouseClicked (e -> {
            
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.monitorSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.monitorSwitch.setRotate(45.0);
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
                    
                    this.raiseSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.raiseSwitch.setRotate(45.0);
                    break;
            }
        });
        
        this.lowerSwitchClickTarget.setOnMouseClicked(e -> {
        
            switch (e.getButton()) {
                
                case PRIMARY:
                    
                    this.lowerSwitch.setRotate(-45.0);
                    break;
                    
                case SECONDARY:
                    
                    this.lowerSwitch.setRotate(45.0);
                    break;
            }
        
        });
        
        this.raiseButtonClickTarget.setOnMouseClicked(e -> {});
        
        this.crossingClearClickTarget.setOnMouseClicked(e -> {});
        
        this.lowerButtonClickTarget.setOnMouseClicked(e -> {});
        
        this.pictureButtonClickTarget.setOnMouseClicked(e -> {
        
            if (!this.getMediaVisible()) {
                this.setMediaVisible(true);
                System.out.println("HERE");
                
                new Thread(() -> {
                
                    try {
                        
                        Thread.sleep (10000);
                        this.setMediaVisible(false);
                        System.out.println("HERE2");
                        
                    } catch (InterruptedException ex) {}
                    
                }).start(); 
            }
        
        
        
        });
        
        this.stopButtonClickTarget.setOnMouseClicked(e -> {});

        
        this.mv.setMediaPlayer(mp);
        this.mp.setStartTime(this.startTime);
        //this.mp.setStopTime(this.endTime);
        
        this.mp.setOnReady(() -> {
            
            mp.play();
            
        });
        
        this.mp.setOnEndOfMedia(() -> {
        
            //this.mp.seek(this.mp.getStartTime());
        
        });
        
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
