import java.io.IOException;
import java.io.RandomAccessFile;


public class HashTable
{
	private HashEntry[] table;
	private int[] sizes = {1019, 2027, 4079, 8123, 16267,
		32503, 65011, 130027, 260111, 520279, 1040387, 2080763,
		4161539, 8323151, 16646323}; //list of sizes to advance table by
	private int curSize; //current location in size list
	private int tSize; //number of entries in table
	private int longestProbe; //holds track of the longest probe sequence

	public HashTable() {
		curSize = 0;
		longestProbe = 0;
		table = new HashEntry[sizes[curSize]];
	}

	/**
	 * used to start insertion
	 * @param s string key
	 * @param offset offset of record
	 * @return false if inserting duplicate
	 */
	public boolean insert(String s, long offset) {
		HashEntry entry = find(s); //finds the correct entry if it is in the table
		if (entry == null) { //is not already in the table
			entry = new HashEntry(s); //creates new entry for table
			entry.add(offset);
			insert(entry);
			return true;
		}
		else if (!entry.getOffsets().contains(offset)) {
			entry.add(offset);
			return true;
		}
		return false;
	}

	/**
	 * inserts the hash entry, also used for rehashing
	 */
	private void insert(HashEntry entry) {
		int hash = elfHash(entry.getName()); //calculates hash value
		int probeLength = 0; //counts how far the table had to probe
		hash %= table.length; //wraps around table
		int step = 1; //the step sizes, start at 1
		while (table[hash] != null) { //keeps calculating new spot as long as spot is full
			hash += (((step * step) + step) / 2); //step size is (n^2 + 2)/2
			hash %= table.length;
			step++;
			probeLength++;
		}
		table[hash] = entry; //inserts into table
		tSize++;
		longestProbe = Math.max(probeLength, longestProbe);
		if (tSize >= (table.length * 0.7)) { //table is more than 70% full
			rehash();
		}
	}

	/**
	 * used to recall entries in the table
	 */
	public HashEntry find(String s) {
		int hash = elfHash(s); //finds hash value of given string
		hash %= table.length; //wraps arond table
		int step = 1;
		while ((table[hash] != null) && (!table[hash].getName().equals(s))) { //keeps probing if doesn't contain
			hash += ((step * step) + step) / 2; //step size is (n^2 + 2)/2
			hash %= table.length;
			step++;
		}
		return table[hash];
	}

	/**
	 * increases size of hash table and rehashes all entries
	 */
	private void rehash() {
		curSize++;
		HashEntry[] oldTable = table;
		table = new HashEntry[sizes[curSize]];
		for (int k = 0; k < oldTable.length; k++) { //goes through old table
			if (oldTable[k] != null) { //if there is an entry
				insert(oldTable[k]); //reinserts into new table
			}
		}
	}

	/**
	 * elfHash function taken from course notes
	 */
	public static int elfHash(String toHash) {
		int hashValue = 0;
		for (int Pos = 0; Pos < toHash.length(); Pos++) { // use all elements
			hashValue = (hashValue << 4) + toHash.charAt(Pos); // shift/mix
			int hiBits = hashValue & 0xF0000000; // get high nybble
			if (hiBits != 0)
				hashValue ^= hiBits >> 24; // xor high nybble with second nybble
			hashValue &= ~hiBits; // clear high nybble
		}
		return hashValue;
	}

	/**
	 * prints contents of hash table
	 *
	 * @param f the file print to
	 */
	public void debug(RandomAccessFile f) {
		try
		{
			f.writeBytes("Slot\t\tName\t\t\tOffset(s)");
			for (int k = 0; k < table.length; k++) {
				HashEntry e = table[k]; //the current hash entry being examined
				if (e != null) {
					f.writeBytes("\n" + k + "\t\t" + e.toString());
				}
			}
		}
		catch ( IOException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// ----------------------------------------------------------
	/**
	 * returns the longest probe length
	 * @return the longest probe length
	 */
	public int longestProbe() {
		return longestProbe;
	}
}
