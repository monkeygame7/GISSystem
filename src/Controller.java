import java.io.FileNotFoundException;
import java.util.Vector;
import java.io.IOException;
import java.util.Scanner;
import java.io.RandomAccessFile;

/**
 * // -------------------------------------------------------------------------
/**
 *  The controller class which processes the commands and does all the data
 *  manipulation.  Holds QuadTree, HashTable, and BufferPool classes
 *
 *  @author Shayan Motevalli
 *  @version Nov 19, 2012
 */

public class Controller
{
	private prQuadtree<QuadEntry> tree; //tree to sort records by location
	private HashTable hTable; //hash table to sort records by name
	private BufferPool pool; //buffer pool used to get things from memory
	private RandomAccessFile instructions, log, db; //the files containing the commands, the log file to write to, and the database
	private Parser parser; //parser to go through commands file and return each command
	private int commandCount; //used to number commands
	String inst, lg, dbase; //holds the names for the specified files for log purposes

	// ----------------------------------------------------------
	/**
	 * Create a new Controller object.
	 * @param i the instructions
	 * @param l the log file to write to
	 * @param d the database of records, where imported records are added
	 */
	public Controller(String i, String l, String d) {
		tree = null;
		hTable = new HashTable();
		commandCount = 1;
		inst = i;
		lg = l;
		dbase = d;
		try {
			instructions = new RandomAccessFile(inst, "rw");
			db = new RandomAccessFile(dbase, "rw");
			log = new RandomAccessFile(lg, "rw");
			db.seek(0);
		}
		catch ( FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser = new Parser(instructions, log);
		pool = new BufferPool(db);
	}

	/**
	 * begins running and executing the commands
	 */
	public void run() {
		String line; //holds each line of instruction
		Scanner scn; //used to parse each instruction
		try{
			while ((line = parser.nextInstruction()) != null) { //loops while there is a next command
				scn = new Scanner(line); //loads instruction
				String command = scn.next(); //loads instruction word (first word of instruction)

				if(command.equals("world")) { //world command
					log.writeBytes(line + "\n\n");
					world(scn.next(), scn.next(), scn.next(), scn.next()); //creates new world
					continue; //so it doesn't increment the command counter
				}

				log.writeBytes("Command " + commandCount + ":   "); //numbers commands
				if(command.equals("import")) { //import command
					log.writeBytes(line + "\n\n");
					importDB(scn.next()); //imports the database file specified
				}
				else if(command.equals("what_is_at")) { //what is at command
					log.writeBytes(line + "\n\n");
					command = scn.next(); //gets switch or coordinate
					if (command.charAt(0) == '-') { //indicates presence of a switch
						whatIsAt(command, scn.next(), scn.next());
					}
					else {
						whatIsAt(null, command, scn.next());
					}
				}
				else if(command.equals("what_is")) { //what is command
					log.writeBytes(line + "\n\n");
					String feature, state; //used to concatenate feature name and get state name
					command = scn.next(); //gets switch or feature name
					if (command.charAt(0) == '-') { //has switch
						feature = scn.next(); //next thing is start of feature name
					}
					else {
						feature = command; //if not, the first token was part of the feature name
						command = null; //no command switch
					}
					state = scn.next();
					while (scn.hasNext()) { //if there is another, it can add what's in state to the end of feature
						feature += " " + state; //adds next part of feature
						state = scn.next();
					}
					whatIs(command, feature, state);
				}
				else if(command.equals("what_is_in")) { //region search
					log.writeBytes(line + "\n\n");
					command = scn.next(); //gets switch command
					if (command.charAt(0) == '-') { //if there is a switch
						whatIsIn(command, scn.next(), scn.next(), scn.nextLong(), scn.nextLong()); //passes parameters
					}
					else {
						whatIsIn(null, command, scn.next(), scn.nextLong(), scn.nextLong()); //passes parameters
					}
				}
				else if(command.equals("debug")) { //debug command
					log.writeBytes(line + "\n\n");
					command = scn.next(); //what to debug
					if (command.equals("quad")) { //debugs tree
						tree.debug(log);
						log.writeBytes("\n");
					}
					else if(command.equals("hash")) { //debugs hash table
						hTable.debug(log);
						log.writeBytes("\n");
					}
					else if(command.equals("pool")) {
						pool.debug(log);
						log.writeBytes("\n");
					}
				}
				else if(command.equals("quit")) {
					log.writeBytes(line + "\n\nTerminating execution of commands.\n");
					break; //ends program
				}
				log.writeBytes("--------------------------------------------------------------------------------\n");
				commandCount++;
			}
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * converts each string into the correct latitude or longitude values and
	 * uses those as the bounds for the quadtree
	 */
	public void world(String wLong, String eLong, String sLat, String nLat) {
		// format for latitude is DDMMSS['N' | 'S'] so I am just taking each respective substring
		LatLong minLat = toLatLong(sLat);
		LatLong maxLat = toLatLong(nLat);

		// format for longitude is DDDMMSS['E' | 'W'] so I am just taking each respective substring
		LatLong minLong = toLatLong(wLong);
		LatLong maxLong = toLatLong(eLong);

		tree = new prQuadtree<QuadEntry>(minLong.totalSeconds(), maxLong.totalSeconds(),
			minLat.totalSeconds(), maxLat.totalSeconds()); //creates new tree in given space

		try {
			log.writeBytes("\nGIS Program\n\ndbFile:\t\t" + dbase + "\n");
			log.writeBytes("script:\t\t" + inst + "\nlog:\t\t" + lg + "\n");
			log.writeBytes("--------------------------------------------------------------------------------\n\n");
			log.writeBytes("Latitude/longitude values in index entries are shown as signed integers, in total seconds.\n\nWorld Boundaries:\n");
			log.writeBytes("\t\t\t\t" + tree.getYMax());
			log.writeBytes("\n\t\t" + tree.getXMin() + "\t\t\t\t" + tree.getXMax());
			log.writeBytes("\n\t\t\t\t" + tree.getYMin() + "\n");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void importDB(String s) {
		try {
			RandomAccessFile newDB = new RandomAccessFile(s, "rw");
			String curLine; //the current line in the file
			int numName = 0; //counts the number of features imported by name
			int numLoc = 0; //counts the number of features imported by location
			db.seek(db.length()); //seeks to the end of the file to start writing
			if (db.getFilePointer() == 0) { //beginning of database file
				db.writeBytes(newDB.readLine() + "\n"); //puts header at top
			}
			else { //doesn't need to copy header
				newDB.readLine();
			}

			while((curLine = newDB.readLine()) != null) { //loops through file
				Record r = new Record(curLine);
				if(hTable.insert(r.getName() + ":" + r.getStateName(), db.getFilePointer())) { //inserts into the hash table
					numName++;
				}

				QuadEntry temp = new QuadEntry(r.getPrimCoord()); //used to search for entry
				QuadEntry q = tree.find(temp); //tries to find an entry at that coordinate
				if (q == null) { //there is no entry at that coordinate
					temp.add(db.getFilePointer()); //adds the pointer to the previos entry
					if (tree.insert(temp)) {
						numLoc++;
					}
				}
				else { //an entry at that coordinate was found
					if (q.add(db.getFilePointer())) { //adds the offset to the entry
						numLoc++;
					}
				}
				db.writeBytes(curLine + "\n"); //appends entry to end of database
			}
			log.writeBytes("Imported Features by Name: " + numName);
			log.writeBytes("\nLongest Probe Sequence: " + hTable.longestProbe());
			log.writeBytes("\nImported Locations: " + numLoc + "\n");
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void whatIsAt(String s, String lat, String lng) {
		//create an empty quad entry for search purposes
		QuadEntry temp = new QuadEntry(new Coordinate(toLatLong(lng), toLatLong(lat)));
		temp = tree.find(temp); //finds the entry in the tree with those coordinates
		try {
			if (temp == null) { //no results found
				log.writeBytes("\tNothing was found at " + lng + ", " + lat + ".\n");
			}
			else {
				Vector<Long> v = temp.getOffsets();
				if (s == null) { //no switch
					log.writeBytes("\tThe following features were found at " + lng + ", " + lat + ":\n");
					for(long offset:v) { //goes through list of all matching offsets
						Record r = pool.get(offset);
						log.writeBytes("" + offset + ":\t" + r.getName() + "\t" + r.getCountyName() + "\t" + r.getStateName() + "\n");
					}
				}
				else if(s.equals("-l")) { //prints details about each record
					log.writeBytes("\tThe following features were found at " + lng + ", " + lat + ":\n");
					for(long offset:v) { //goes through list of all matching offsets
						Record r = pool.get(offset); //gets record at that offset
						r.write(log);
						log.writeBytes("\n\n");
					}
				}
				else if(s.equals("-c")) { //only prints number of records
					log.writeBytes("The number of records for " + lng + ", " + lat + " was " + v.size() + "\n");
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------
	/**
	 * processes and returns information based on feature names
	 * @param s switch for how to display information
	 * @param feat feature name
	 * @param state state name
	 */
	public void whatIs(String s, String feat, String state) {
		HashEntry temp = hTable.find(feat + ":" + state); //gets the entry corresponding to the name
		try {
			if (temp == null) { //no records match
				log.writeBytes("No records match " + feat + " and " + state + ".");
			}
			else {
				Vector<Long> v = temp.getOffsets(); //gets the list of corresponding offsets
				if (s == null) { //no switch
					for(long offset:v) { //goes through list of all matching offsets
						Record r = pool.get(offset);
						log.writeBytes("" + offset + ":\t" + r.getCountyName() + "\t" + r.getPrimCoord() + "\n");
					}
				}

				else if(s.equals("-l")) { //prints details about each record
					for(long offset:v) { //goes through list of all matching offsets
						log.writeBytes("\tFound matching record at offset " + offset + ":\n");
						Record r = pool.get(offset); //gets record at that offset
						r.write(log);
						log.writeBytes("\n\n");
					}
				}
				else if(s.equals("-c")) { //only prints number of records
					log.writeBytes("The number of records for " + feat + " and " + state + " was " + v.size() + "\n");
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------
	/**
	 * Place a description of your method here.
	 * @param s the switch statement
	 * @param lat latitude of center of region
	 * @param lng longitude of center of region
	 * @param hHeight half the height of the region
	 * @param hWidth half the width of the region
	 */
	public void whatIsIn(String s, String lat, String lng, long hHeight, long hWidth) {
		Coordinate coord = new Coordinate(toLatLong(lng), toLatLong(lat)); //converts center point to number
		Vector<QuadEntry> v = tree.find(coord.getX() - hWidth, coord.getX() + hWidth,
			coord.getY() - hHeight, coord.getY() + hHeight); //gets all entries in region

		try {
			if (v.size() == 0) {
				log.writeBytes("Nothing was found in (" + lng + " +/- " + hWidth +
					", " + lat + " +/- " + hHeight + ")\n");
			}
			else{
				Vector<Long> offsets = new Vector<Long>(); //will hold all offsets together
				for (QuadEntry qe : v) { //goes through each quad entry in region
					for (long offset : qe.getOffsets()) { //goes through each offset in the entry
						offsets.add(offset); //adds offset to total list
					}
				}

				//prints information
				if (s == null) { //no switch command
					log.writeBytes("\tThe following " + offsets.size() + " features were found in (" + lng + " +/- " + hWidth +
						", " + lat + " +/- " + hHeight + ")\n");
					for(long offset : offsets) { //goes through list of all matching offsets
						Record r = pool.get(offset);
						log.writeBytes("" + offset + ":\t" + r.getName() + "\t" + r.getCountyName() + "\t" + r.getStateName() + "\n");
					}
				}
				else if(s.equals("-l")) { //prints details about each record
					log.writeBytes("\tThe following " + offsets.size() + " features were found in (" + lng + " +/- " + hWidth +
						", " + lat + " +/- " + hHeight + ")\n");
					for(long offset:offsets) { //goes through list of all matching offsets
						Record r = pool.get(offset); //gets record at that offset
						r.write(log);
						log.writeBytes("\n\n");
					}
				}
				else if(s.equals("-c")) { //only prints number of records
					log.writeBytes("" + offsets.size() + " features were found in (" + lng + " +/- " + hWidth +
						", " + lat + " +/- " + hHeight + ")\n");
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * converts a string to a latitude or longitude
	 */
	private LatLong toLatLong(String s) {
		if (s.length() == 7) { //latitude
			return new LatLong(Integer.parseInt(s.substring(0, 2)),
				Integer.parseInt(s.substring(2, 4)),
				Integer.parseInt(s.substring(4, 6)), getLatLongDir(s.charAt(6)));
		}
		else if (s.length() == 8) { //longitude
			return new LatLong(Integer.parseInt(s.substring(0, 3)),
				Integer.parseInt(s.substring(3, 5)),
				Integer.parseInt(s.substring(5, 7)), getLatLongDir(s.charAt(7)));
		}
		return null;
	}

	private Direction getLatLongDir(char s) {
		if (s == 'N')
			return Direction.N;
		else if (s == 'S')
			return Direction.S;
		else if (s == 'E')
			return Direction.E;
		else if (s == 'W')
			return Direction.W;
		else
			return Direction.NOQUADRANT;
	}
}
