import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 *  Stores all the information for a record.
 *  Takes a line of text containing the records information and pareses it
 *  into all the different parts of teh record.
 *
 *  @author Shayan
 *  @version Aug 30, 2012
 */
public class Record {

	private int fid; //the Feature ID of the record
	private String name; //the name of the feature
	private String classification; //the classifciation of the feature
	private String stateName; //the two character abbreviation of the state
	private int stateNum; //the numeric code for the state
	private String countyName; //the county the feature is in
	private int countyNum; //the numeric code for the county
	//private LatLong primLat; //primary latitude
	//private LatLong primLong; //primary longitude
	private Coordinate primCoord; //primary latitude and longitude
	private double primLatDec; //decimal number for primary latitude
	private double primLongDec; //decimal number for primary longitude
	//private LatLong srcLat; //source latitude
	//private LatLong srcLong; //source longitude
	private Coordinate srcCoord; //source latitude and longitude
	private double srcLatDec; //decimal number for source latitude
	private double srcLongDec; //decimal number for source longitude
	private int elevationM; //elevation in meters
	private int elevationFt; //elevation in feet
	private String mapName; //name of map
	private String dateCreated; //the date the record was created
	private String dateEdited; //the date the record was edited

	// ----------------------------------------------------------
	/**
	 * Create a new Record object.
	 * @param s the string containing the entire record. assumed to be properly formatted
	 */
	public Record(String s) {
		String temp; //used to split of latitude and longitude
		Scanner scnr = new Scanner(s); //reads the line
		scnr.useDelimiter("\\|");

		/* scans in order and assigns corresponding record values */
		setFid(scnr.nextInt());
		setName(scnr.next());
		setClassification(scnr.next());
		setStateName(scnr.next());
		setStateNum(scnr.nextInt());
		setCountyName(scnr.next());
		setCountyNum(scnr.nextInt());

		temp = scnr.next();
		primCoord = new Coordinate();
		// format for latitude is DDMMSS['E' | 'W'] so I am just taking each respective substring
		setPrimLat(new LatLong(Integer.parseInt(temp.substring(0, 2)),
			Integer.parseInt(temp.substring(2, 4)),
			Integer.parseInt(temp.substring(4, 6)), getLatLongDir(temp.charAt(6))));
		temp = scnr.next();
		// format for longitude is DDDMMSS['N' | 'S'] so I am just taking each respective substring
		setPrimLong(new LatLong(Integer.parseInt(temp.substring(0, 3)),
			Integer.parseInt(temp.substring(3, 5)),
			Integer.parseInt(temp.substring(5, 7)), getLatLongDir(temp.charAt(7))));
		setPrimLatDec(scnr.nextDouble());
		setPrimLongDec(scnr.nextDouble());

		temp = scnr.next();
		srcCoord = new Coordinate();
		if (temp.isEmpty()) { //because it is optional, may not be there
			scnr.next(); //goes to next token
		}
		else {
			// format for latitude is DDMMSS['N' | 'S'] so I am just taking each respective substring
			setSrcLat(new LatLong(Integer.parseInt(temp.substring(0, 2)),
				Integer.parseInt(temp.substring(2, 4)),
				Integer.parseInt(temp.substring(4, 6)), getLatLongDir(temp.charAt(6))));
		}
		temp = scnr.next();
		if (temp.isEmpty()) { //because it is optional, may not be there
			scnr.next(); //goes to next token
		}
		else {
			// format for longitude is DDDMMSS['N' | 'S'] so I am just taking each respective substring
			setSrcLong(new LatLong(Integer.parseInt(temp.substring(0, 3)),
				Integer.parseInt(temp.substring(3, 5)),
				Integer.parseInt(temp.substring(5, 7)), getLatLongDir(temp.charAt(7))));
			setSrcLatDec(scnr.nextDouble());
			setSrcLongDec(scnr.nextDouble());
		}

		if (scnr.hasNextInt()) { //because it is optional
			setElevationM(scnr.nextInt());
		}
		else {
			setElevationM(-1); //-1 means no value
			scnr.next(); //proceeds to next token
		}
		if (scnr.hasNextInt()) { //because it is optional
			setElevationFt(scnr.nextInt());
		}
		else {
			setElevationFt(-1); //-1 means no value
			scnr.next(); //proceeds to next token
		}

		setMapName(scnr.next());
		setDateCreated(scnr.next());
		if (scnr.hasNext()){
			setDateEdited(scnr.next());
		}
		else {
			setDateEdited(null);
		}
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

	// ----------------------------------------------------------
	/**
	 * writes nonempty members to given file
	 * @param l file to write to
	 */
	public void write(RandomAccessFile l) {
		try {
			l.writeBytes(  "Feature ID       : " + getFid());
			l.writeBytes("\nFeature Name     : " + getName());
			l.writeBytes("\nFeature Cat      : " + getClassification());
			l.writeBytes("\nState            : " + getStateName());
			l.writeBytes("\nCounty           : " + getCountyName());
			l.writeBytes("\nLatitude         : " + getPrimLat().toString());
			l.writeBytes("\nLongitude        : " + getPrimLong().toString());
			if (!srcCoord.isEmpty()) {
				l.writeBytes("\nSrc Long         : " + getSrcLong().toString());
				l.writeBytes("\nSrc Lat          : " + getSrcLat().toString());
			}
			l.writeBytes("\nElev in ft       : " + getElevationFt());
			l.writeBytes("\nUSGS Quad        : " + getMapName());
			l.writeBytes("\nDate Created     : " + getDateCreated());
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------
	/**
	 * @param fid the fid to set
	 */
	public void setFid(int fid)
	{
		this.fid = fid;
	}

	// ----------------------------------------------------------
	/**
	 * @return the fID
	 */
	public int getFid()
	{
		return fid;
	}

	// ----------------------------------------------------------
	/**
	 * @param name the name to set
	 */
	public void setName(String name)
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
	 * @param classification the classification to set
	 */
	public void setClassification(String classification)
	{
		this.classification = classification;
	}

	// ----------------------------------------------------------
	/**
	 * @return the classification
	 */
	public String getClassification()
	{
		return classification;
	}

	// ----------------------------------------------------------
	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName)
	{
		this.stateName = stateName;
	}

	// ----------------------------------------------------------
	/**
	 * @return the stateName
	 */
	public String getStateName()
	{
		return stateName;
	}

	// ----------------------------------------------------------
	/**
	 * @param stateNum the stateNum to set
	 */
	public void setStateNum(int stateNum)
	{
		this.stateNum = stateNum;
	}

	// ----------------------------------------------------------
	/**
	 * @return the stateNum
	 */
	public int getStateNum()
	{
		return stateNum;
	}

	// ----------------------------------------------------------
	/**
	 * @param countyName the countyName to set
	 */
	public void setCountyName(String countyName)
	{
		this.countyName = countyName;
	}

	// ----------------------------------------------------------
	/**
	 * @return the countyName
	 */
	public String getCountyName()
	{
		return countyName;
	}

	// ----------------------------------------------------------
	/**
	 * @param countyNum the countyNum to set
	 */
	public void setCountyNum(int countyNum)
	{
		this.countyNum = countyNum;
	}

	// ----------------------------------------------------------
	/**
	 * @return the countyNum
	 */
	public int getCountyNum()
	{
		return countyNum;
	}

	public Coordinate getPrimCoord() {
		return primCoord;
	}

	// ----------------------------------------------------------
	/**
	 * @param primLat the primLat to set
	 */
	public void setPrimLat(LatLong primLat)
	{
		primCoord.setLat(primLat);
	}

	// ----------------------------------------------------------
	/**
	 * @return the primLat
	 */
	public LatLong getPrimLat()
	{
		return primCoord.getLat();
	}

	// ----------------------------------------------------------
	/**
	 * @param primLong the primLong to set
	 */
	public void setPrimLong(LatLong primLong)
	{
		primCoord.setLong(primLong);
	}

	// ----------------------------------------------------------
	/**
	 * @return the primLong
	 */
	public LatLong getPrimLong()
	{
		return primCoord.getLong();
	}

	// ----------------------------------------------------------
	/**
	 * @param primLatDec the primLatDec to set
	 */
	public void setPrimLatDec(double primLatDec)
	{
		this.primLatDec = primLatDec;
	}

	// ----------------------------------------------------------
	/**
	 * @return the primLatDec
	 */
	public double getPrimLatDec()
	{
		return primLatDec;
	}

	// ----------------------------------------------------------
	/**
	 * @param primLongDec the primLongDec to set
	 */
	public void setPrimLongDec(double primLongDec)
	{
		this.primLongDec = primLongDec;
	}

	// ----------------------------------------------------------
	/**
	 * @return the primLongDec
	 */
	public double getPrimLongDec()
	{
		return primLongDec;
	}

	// ----------------------------------------------------------
	/**
	 * @param srcLat the srcLat to set
	 */
	public void setSrcLat(LatLong srcLat)
	{
		srcCoord.setLat(srcLat);
	}

	// ----------------------------------------------------------
	/**
	 * @return the srcLat
	 */
	public LatLong getSrcLat()
	{
		return srcCoord.getLat();
	}

	// ----------------------------------------------------------
	/**
	 * @param srcLatDec the srcLatDec to set
	 */
	public void setSrcLatDec(double srcLatDec)
	{
		this.srcLatDec = srcLatDec;
	}

	// ----------------------------------------------------------
	/**
	 * @return the srcLatDec
	 */
	public double getSrcLatDec()
	{
		return srcLatDec;
	}

	// ----------------------------------------------------------
	/**
	 * @param srcLong the srcLong to set
	 */
	public void setSrcLong(LatLong srcLong)
	{
		srcCoord.setLong(srcLong);
	}

	// ----------------------------------------------------------
	/**
	 * @return the srcLong
	 */
	public LatLong getSrcLong()
	{
		return srcCoord.getLat();
	}

	// ----------------------------------------------------------
	/**
	 * @param elevationM the elevationM to set
	 */
	public void setElevationM(int elevationM)
	{
		this.elevationM = elevationM;
	}

	// ----------------------------------------------------------
	/**
	 * @return the elevationM
	 */
	public int getElevationM()
	{
		return elevationM;
	}

	// ----------------------------------------------------------
	/**
	 * @param srcLongDec the srcLongDec to set
	 */
	public void setSrcLongDec(double srcLongDec)
	{
		this.srcLongDec = srcLongDec;
	}

	// ----------------------------------------------------------
	/**
	 * @return the srcLongDec
	 */
	public double getSrcLongDec()
	{
		return srcLongDec;
	}

	// ----------------------------------------------------------
	/**
	 * @param elevationFt the elevationFt to set
	 */
	public void setElevationFt(int elevationFt)
	{
		this.elevationFt = elevationFt;
	}

	// ----------------------------------------------------------
	/**
	 * @return the elevationFt
	 */
	public int getElevationFt()
	{
		return elevationFt;
	}

	// ----------------------------------------------------------
	/**
	 * @param mapName the mapName to set
	 */
	public void setMapName(String mapName)
	{
		this.mapName = mapName;
	}

	// ----------------------------------------------------------
	/**
	 * @return the mapName
	 */
	public String getMapName()
	{
		return mapName;
	}

	// ----------------------------------------------------------
	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(String dateCreated)
	{
		this.dateCreated = dateCreated;
	}

	// ----------------------------------------------------------
	/**
	 * @return the dateCreated
	 */
	public String getDateCreated()
	{
		return dateCreated;
	}

	// ----------------------------------------------------------
	/**
	 * @param dateEdited the dateEdited to set
	 */
	public void setDateEdited(String dateEdited)
	{
		this.dateEdited = dateEdited;
	}

	// ----------------------------------------------------------
	/**
	 * @return the dateEdited
	 */
	public String getDateEdited()
	{
		return dateEdited;
	}
}
