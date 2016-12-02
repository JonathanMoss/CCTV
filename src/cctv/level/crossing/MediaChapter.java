package cctv.level.crossing;

/**
 *
 * @author Jonathan Moss
 * @version v1.0 November 2016
 */
public enum MediaChapter {

    BARRIERS_UP_STILL ("/resources/BarriersUp.mp4"),
    LOWER_SEQUENCE ("/resources/LowerSequence.mp4"),
    RAISE_SEQUENCE ("/resources/BarriersRaise.mp4"),
    TRAIN_RIGHT_TO_LEFT ("/resources/TrainRightToLeft.mp4"),
    TRAIN_LEFT_TO_RIGHT ("/resources/TrainLeftToRight.mp4"),
    BARRIERS_DOWN ("/resources/BarriersDownNoTrains.mp4");
    
    private final String resource;

    MediaChapter (String resource) {
    
        this.resource = resource;
    
    }
    
    public String getResource() {
        return this.resource;
    }
    

    
}
