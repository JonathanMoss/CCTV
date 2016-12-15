package cctv.level.crossing;

/**
 * This Enumeration defines the media files that represent various Level Crossing Status.
 * @author Jonathan Moss
 * @version v1.0 November 2016
 */
public enum MediaChapter {

    /**
     * The barriers are in the raised position.
     */
    BARRIERS_UP_STILL ("/resources/BarriersUp.mp4"),
    
    /**
     * The barriers are in the lower sequence.
     */
    LOWER_SEQUENCE ("/resources/LowerSequence.mp4"),
    
    /**
     * The barriers are in the raise sequence.
     */
    RAISE_SEQUENCE ("/resources/BarriersRaise.mp4"),
    
    /**
     * The barriers are in the down position, a train is passing right to left.
     */
    TRAIN_RIGHT_TO_LEFT ("/resources/TrainRightToLeft.mp4"),
    
    /**
     * The barriers are in the down position, a train is passing left to right.
     */
    TRAIN_LEFT_TO_RIGHT ("/resources/TrainLeftToRight.mp4"),
    
    /**
     * The barriers are in the down position.
     */
    BARRIERS_DOWN ("/resources/BarriersDownNoTrains.mp4");
    
    private final String resource;

    MediaChapter (String resource) {
    
        this.resource = resource;
    
    }
    
    /**
     * This method returns the media file resource associated with the status constant.
     * @return <code>String</code> The media file.
     */
    public String getResource() {
        
        return this.resource;
        
    }
    

    
}
