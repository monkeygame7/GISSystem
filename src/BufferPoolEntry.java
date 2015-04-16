
public class BufferPoolEntry {
	private Record record;
	private long offset;

	public BufferPoolEntry(long o) {
		offset = o;
		record = null;
	}

	public void setRecord(Record r) {
		record = r;
	}

	public long getOffset() {
		return offset;
	}

	public Record getRecord() {
		return record;
	}

	public boolean equals(Object o) {
		//if (this.getClass().getName().equals(o.getClass().getName())) {
			BufferPoolEntry e = (BufferPoolEntry) o;
			return e.getOffset() == offset;
		//}
		//return false;
	}

	public String toString() {
		return "" + offset + ": " + record.getName();
	}
}
