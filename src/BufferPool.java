import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * // -------------------------------------------------------------------------
/**
 *  The buffer pool class
 *
 *  @author Shayan Motevalli
 *  @version Nov 19, 2012
 */

public class BufferPool
{
	private final int POOL_SIZE = 20; //the maximum number of records in the pool
	private LinkedList<BufferPoolEntry> pool; //pool of records
	private RandomAccessFile records; //the records to pull from

	public BufferPool(RandomAccessFile r) {
		records = r;
		pool = new LinkedList<BufferPoolEntry>();
	}

	public Record get(long offset) {
		BufferPoolEntry bpe = new BufferPoolEntry(offset); //the entry for this
		int index = pool.indexOf(bpe); //searches pool for record

		if (index == -1) { //record is not in pool
			if (pool.size() == POOL_SIZE) { //no more room in bufferpool
				pool.removeLast(); //removes the last element to make room
			}
			try
			{
				records.seek(offset);//goes to right offset
				bpe.setRecord(new Record(records.readLine()));
			}
			catch ( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else { //record is in pool
			bpe = pool.remove(index); //removes the record so it can be moved to front
		}
		pool.addFirst(bpe); //adds the record to front of list
		return bpe.getRecord();
	}

	public void debug(RandomAccessFile l) {
		try {
			for (int k = 0; k < POOL_SIZE; k++) { //goes through whole table
				l.writeBytes("" + k + "\t\t\t");
				if (k >= pool.size()) { //nothing in that spot
					l.writeBytes("-\n");
				}
				else {
					BufferPoolEntry bpe = pool.get(k); //gets corresponding pool entry
					l.writeBytes(bpe.toString() + "\n");
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
