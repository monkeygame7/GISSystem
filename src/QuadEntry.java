import java.util.Vector;

/**
 * // -------------------------------------------------------------------------
/**
 *  Entries to be stored in the QuadTree.  Contains a coordinate, showing its location,
 *  as well as a vector holding all the locations at that coordinate
 *
 *  @author Shayan Motevalli
 *  @version Nov 28, 2012
 */
public class QuadEntry implements Compare2D<QuadEntry>
{
    private Vector<Long> offsets;
    private Coordinate coord;

    /**
     * constructor, sets coord to c, entries must be added separately
     */
    public QuadEntry(Coordinate c) {
        coord = c;
        offsets = new Vector<Long>();
    }

    // ----------------------------------------------------------
    /**
     * adds the given offset to the list unless it is already there
     * @param l the offset to add
     */
    public boolean add(Long l) {
        if (!offsets.contains(l)) { //not already there
            offsets.add(l); //adds to list of offsets
            return true;
        }
        return false;
    }

    // ----------------------------------------------------------
    /**
     * removes the given
     * @param l the offset to remove
     * @return true if successful removal
     */
    public boolean remove(Long l) {
        return offsets.remove(l);
    }

    // ----------------------------------------------------------
    /**
     * returns the list of offsets
     * @return the list of offsets
     */
    public Vector<Long> getOffsets(){
        return offsets;
    }

    // ----------------------------------------------------------
    /**
     * returns the coordinate
     * @return the coordinate of the entry
     */
    public Coordinate getCoord() {
        return coord;
    }

    public boolean equals(Object o) {
        if (this.getClass().getName().equals(o.getClass().getName())) { //object is a quad entry
            QuadEntry qe = (QuadEntry) o;
            return coord.equals(qe.getCoord());
        }
        return false;
    }

    @Override
    public Direction directionFrom( long X, long Y )
    {
        return coord.directionFrom( X, Y );
    }

    @Override
    public long getX()
    {
        return coord.getX();
    }

    @Override
    public long getY()
    {
        return coord.getY();
    }

    @Override
    public boolean inBox( double xLo, double xHi, double yLo, double yHi )
    {
        return coord.inBox( xLo, xHi, yLo, yHi );
    }

    @Override
    public Direction inQuadrant( double xLo, double xHi, double yLo, double yHi )
    {
        return coord.inQuadrant( xLo, xHi, yLo, yHi );
    }

    public String toString() {
    	return "{" + coord.toString() + ": " + offsets.toString() + "}";
    }
}
