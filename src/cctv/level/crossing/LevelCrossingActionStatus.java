package cctv.level.crossing;

/**
 * This Enumeration provides constant values that represent the current action of the Crossing.
 * @author Jonathan Moss
 * @version v1.0 December 2016
 */
public enum LevelCrossingActionStatus {

    /**
     * The barriers are in the raised position.
     */
    BARRIERS_UP,
    
    /**
     * The barriers are in the barrier lower sequence.
     */
    BARRIERS_LOWERING,
    
    /**
     * The barriers are in the down position, there are no trains passing over the level crossing.
     */
    BARRIERS_DOWN_NO_TRAINS,
    
    /**
     * The barriers are in the down position, a train shall pass from left to right.
     */
    BARRIERS_DOWN_TRAIN_LEFT_TO_RIGHT,
    
    /**
     * The barriers are in the down position, a train shall pass from right to left.
     */
    BARRIERS_DOWN_TRAIN_RIGHT_TO_LEFT,
    
    /**
     * The barriers are in the barrier raise sequence.
     */
    BARRIERS_RAISING;
    
}
