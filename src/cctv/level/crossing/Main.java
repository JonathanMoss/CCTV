package cctv.level.crossing;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Jonathan Moss
 * @version v1.0 November 2016
 */
public class Main extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch (args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        LevelCrossingCCTV alsagerCCTV = new LevelCrossingCCTV();
        primaryStage.setScene(new Scene(alsagerCCTV));
        primaryStage.setTitle ("N/X Panel Closed Circuit Television Crossing (CCTV)");
        primaryStage.show();

        
        new Thread (()->{
        
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {}
            System.out.println("Starting Lower Sequence");
            alsagerCCTV.startLowerSequence();
            
            try {
                Thread.sleep(25000);
            } catch (InterruptedException ex) {}
            System.out.println("Train Left to Right");
            alsagerCCTV.showTrainLeftToRight();
            
            try {
                Thread.sleep(20000);
            } catch (InterruptedException ex) {}
            System.out.println("Train Train Right to Left");
            alsagerCCTV.showTrainRightToLeft();
            
            
            try {
                Thread.sleep(20000);
            } catch (InterruptedException ex) {}
            System.out.println("Starting Raise Sequence");
            alsagerCCTV.startRaiseSequence();
        
        }).start();
        
    }
    
}
