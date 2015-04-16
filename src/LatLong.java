/**
 *  Contains either a latitude or longitude value
 *  Stores the degrees, minutes, seconds and direction of the latitude or
 *  longitude
 *
 *  @author Shayan
 *  @version Aug 30, 2012
 */
public class LatLong
{
    //different parts of latitude/longitude
    private int degrees, minutes, seconds;
    private Direction direction;

    // ----------------------------------------------------------
    /**
     * Create a new LatLong object.
     * @param deg degrees
     * @param min minutes
     * @param sec seconds
     * @param dir direction, must be N, S, E, or W
     */
    public LatLong(int deg, int min, int sec, Direction dir) {
        setDegrees(deg );
        setMinutes(min );
        setSeconds(sec );
        setDirection(dir );
    }

    /**
     * returns a string of the latitude or longitude in the format of
     * DDd MMm SSs [North/South/East/West]
     *
     * @return the string
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(degrees + "\u00B0" + minutes + "'" + seconds + "\"");
        if (direction == Direction.N) {
            s.append("N");
        }
        else if (direction == Direction.S) {
            s.append("S");
        }
        else if (direction == Direction.E) {
            s.append("E");
        }
        else if (direction == Direction.W) {
            s.append("W");
        }
        return s.toString();
    }

    // ----------------------------------------------------------
    /**
     * @param degrees the degrees to set
     */
    public void setDegrees(int degrees ) {
        this.degrees = degrees;
    }

    // ----------------------------------------------------------
    /**
     * @return the degrees
     */
    public int getDegrees() {
        return degrees;
    }

    // ----------------------------------------------------------
    /**
     * @param minutes the minutes to set
     */
    public void setMinutes(int minutes ) {
        this.minutes = minutes;
    }

    // ----------------------------------------------------------
    /**
     * @return the minutes
     */
    public int getMinutes() {
        return minutes;
    }

    // ----------------------------------------------------------
    /**
     * @param seconds the seconds to set
     */
    public void setSeconds(int seconds ) {
        this.seconds = seconds;
    }

    // ----------------------------------------------------------
    /**
     * @return the seconds
     */
    public int getSeconds() {
        return seconds;
    }

    // ----------------------------------------------------------
    /**
     * @param direction the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    // ----------------------------------------------------------
    /**
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    // ----------------------------------------------------------
    /**
     * returns the total number of seconds.  South and West are negative numbers
     * @return total seconds
     */
    public int totalSeconds() {
        int val = (((degrees * 60) + minutes) * 60) + seconds;
        if (direction == Direction.S || direction == Direction.W) {
            val *= -1;
        }
        return val;
    }
}
