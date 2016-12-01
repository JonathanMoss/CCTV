package cctv.level.crossing;

/**
 *
 * @author Jonathan Moss
 * @version v1.0 November 2016
 */
public enum MediaChapter {

    BARRIERS_UP_STILL (2.9 ,3.0),
    LOWER_SEQUENCE (3, 27),
    RAISE_SEQUENCE (300, 311),
    TRAIN_RIGHT_TO_LEFT (236, 251),
    TRAIN_LEFT_TO_RIGHT (281.0, 300.0),
    BARRIERS_DOWN (25, 101);
    
    private final double startSeconds;
    private final double endSeconds;

    MediaChapter (double start, double end) {
    
        this.startSeconds = start;
        this.endSeconds = end;
    
    }
    
    public double getStart() {
        return this.startSeconds;
    }
    
    public double getEnd() {
        return this.endSeconds;
    }
    
}
