import java.io.File;


public class GIS
{

	// ----------------------------------------------------------
	/**
	 * Place a description of your method here.
	 * @param args
	 */
	public static void main( String[] args )
	{
		String dbName, csName, lfName; //the names of the database, command script, and log files
		dbName = args[0];
		csName = args[1];
		lfName = args[2];
		File log = new File(lfName);
		if (log.exists()) {
			log.delete(); //delete previous log file
		}
		File db = new File(dbName);
		if (db.exists()) {
			db.delete();
		}
		new Controller(csName ,lfName, dbName).run();
	}

}
