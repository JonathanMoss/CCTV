package cctv.level.crossing;

/**
 * This interface provides mandatory methods for a CCTV Crossing (N/X Panel)
 * @author Jonathan Moss
 * @version v1.0 December 2016
 */
public interface ClosedCircuitTelevisionCrossing {

    /**
     * This method returns whether or not the Level Crossing is 'Clear'.
     * @return <code>Boolean</code> <i>'true'</i> indicates that the crossing is clear, otherwise <i>'false'</i>.
     */
    Boolean getCrossingClear();
    
    /**
     * This method removes power to the Level Crossing.
     */
    void setPowerFailure();
    
    /**
     * This method restores power to the Level Crossing following a power failure.
     */
    void restorePowerFailure();
    
    /**
     * This method sets the available detection for the Level Crossing Barriers in the down position.
     * @param barrierDownDetection <code>Boolean</code> <i>'true'</i> indicates detection is available, otherwise <i>'false'</i>.
     */
    void setBarrierDownDetection (Boolean barrierDownDetection);
    
    /**
     * This method sets the available detection for the Level Crossing Barriers in the raised position.
     * @param barrierDownDetection <code>Boolean</code> <i>'true'</i> indicates detection is available, otherwise <i>'false'</i>.
     */
    void setBarrierUpDetection (Boolean barrierDownDetection);
    
    /**
     * This method fails the road traffic lights.
     */
    void setRoadSignalFailure();
    
    /**
     * This method restores the road traffic lights to working order.
     */
    void restoreRoadSignalFailure();
    
    /**
     * This method starts the Level Crossing Lower Sequence (Automatic Working).
     */
    void startLowerSequence();
    
    /**
     * This method starts the Level Crossing Raise Sequence (Automatic Working).
     */
    void startRaiseSequence();
    
    /**
     * This method simulates that Camera One has failed.
     */
    void failCameraOne();
    
    /**
     * This method restores Camera One following a failure.
     */
    void restoreCameraOne();
    
    /**
     * This method simulates that Camera Two has failed.
     */
    void faileCameraTwo();
    
    /**
     * This method restores Camera Two following a failure.
     */
    void restoreCameraTwo();
    
    /**
     * This method simulates the taking of Local Control at the Level Crossing.
     */
    void takeLocalControl();
    
    /**
     * This method simulates the restoration of the Level Crossing to Signaller Control.
     */
    void cancelLocalControl();
}
