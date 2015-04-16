import java.util.Vector;

/**
 * // -------------------------------------------------------------------------
/**
 *  records stored in hash table, consist of name/state and offset
 *
 *  @author Shayan
 *  @version Nov 13, 2012
 */
public class HashEntry
{
    private String name;
    private Vector<Long> offsets;

    public HashEntry(String n) {
        setName( n );
        offsets = new Vector<Long>();
    }

    public void add(long l) {
        if(!offsets.contains(l)) { //if not already there
            offsets.add(l);
        }
    }

    // ----------------------------------------------------------
    /**
     * @param name the name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }

    // ----------------------------------------------------------
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    // ----------------------------------------------------------
    /**
     * @return the offset
     */
    public Vector<Long> getOffsets()
    {
        return offsets;
    }

    public String toString() {
        return name + "\t\t\t" + offsets.toString();
    }
}
