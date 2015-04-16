//    On my honor:
//
//    - I have not discussed the Java language code in my program with
//      anyone other than my instructor or the teaching assistants
//      assigned to this course.
//
//    - I have not used Java language code obtained from another student,
//      or any other unauthorized source, either modified or unmodified.
//
//    - If any Java language code or documentation used in my program
//      was obtained from another source, such as a text book or course
//      notes, that has been clearly noted with a proper citation in
//      the comments of my program.
//
//    - I have not designed this program in such a way as to defeat or
//      interfere with the normal operation of the Curator System.
//
//    Shayan Motevalli


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

// The test harness will belong to the following package; the quadtree
// implementation must belong to it as well.  In addition, the quadtree
// implementation must specify package access for the node types and tree
// members so that the test harness may have access to it.
//

public class prQuadtree< T extends Compare2D<? super T> > {

	prQuadNode root;
	long xMin, xMax, yMin, yMax;
	private boolean successFlag; //used by insert and delete
	private final int BUCKET_SIZE = 4; //size of the bucket, change to increase bucket size, minimum of 1

	// You must use a hierarchy of node types with an abstract base
	// class.  You may use different names for the node types if
	// you like (change displayHelper() accordingly).
	abstract class prQuadNode {
		boolean leaf; //whether the node is a leaf or not
	}

	class prQuadLeaf extends prQuadNode {
		Vector<T> Elements; //the 'bucket' which holds the data

		public prQuadLeaf() {
			Elements = new Vector<T>(BUCKET_SIZE); //creates vector of bucket size to save space
			leaf = true;
		}
	}

	class prQuadInternal extends prQuadNode {
		prQuadNode NW, NE, SE, SW; //the four quadrants

		public prQuadInternal() {
			NW = NE = SE = SW = null; //initializes all nodes as null
			leaf = false;
		}

		public int numNodes() { //counts the number of non-null nodes
			int num = 0;
			num += (NW == null)? 0:1; //adds 1 if NW is not null
			num += (NE == null)? 0:1; //adds 1 if NE is not null
			num += (SE == null)? 0:1; //adds 1 if SE is not null
			num += (SW == null)? 0:1; //adds 1 if SW is not null
			return num;
		}
	}

	// Initialize quadtree to empty state, representing the specified region.
	public prQuadtree(long xMin, long xMax, long yMin, long yMax) {
		root = null;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	// Pre:   elem != null
	// Post:  If elem lies within the tree's region, and elem is not already
	//        present in the tree, elem has been inserted into the tree.
	// Return true iff elem is inserted into the tree.
	public boolean insert(T elem) {
		successFlag = false;
		if (elem.inQuadrant( xMin, xMax, yMin, yMax ) != Direction.NOQUADRANT) { //data is within bounds of tree
			root = insertHelp(elem, root, xMin, xMax, yMin, yMax);
		}
		return successFlag;
	}

	/**
	 * helper method for insert, assumes data is within bounds of tree
	 * xMin/xMax/yMin/yMax are the bounds of sroot
	 */
	private prQuadNode insertHelp(T elem, prQuadNode sroot, long xMin, long xMax,
		long yMin, long yMax) {
		if (sroot == null) { //no node to insert to, must create new node
			prQuadLeaf newNode = new prQuadLeaf(); //create new Leaf
			newNode.Elements.add( elem ); //insert data into leaf
			successFlag = true; //successful insertion
			return newNode;
		}
		else if (sroot.leaf) { //trying to insert into a leaf
			prQuadLeaf leaf = (prQuadLeaf) sroot;
			if (leaf.Elements.contains(elem)) { //duplicate element, does nothing
				return sroot; //does nothing
			}
			if (leaf.Elements.size() < BUCKET_SIZE) { //leaf is not full
				leaf.Elements.add( elem ); //add the new element
				successFlag = true; //successful insertion
				return sroot;
			}
			else { //leaf is full, must create new internal node
				prQuadInternal internal = new prQuadInternal();
				for (int k = 0; k < leaf.Elements.size(); k++) { //goes through vector
					insertHelp(leaf.Elements.get(k), internal, xMin, xMax, yMin, yMax);
				}
				insertHelp(elem, internal, xMin, xMax, yMin, yMax); //inserts new data as well
				return internal;
			}
		}
		else { //trying to insert into an internal node
			prQuadInternal internal = (prQuadInternal) sroot;
			long xMid = (xMin + xMax)/2; //the middle X point
			long yMid = (yMin + yMax)/2; //the middle Y point
			Direction d = elem.inQuadrant( xMin, xMax, yMin, yMax ); //gets which quadrant to insert into
			if (d == Direction.NE) { //data goes in NE quadrant
				internal.NE = insertHelp(elem, internal.NE, xMid, xMax, yMid, yMax); //inserts to NE
			}
			else if (d == Direction.NW) { //data goes in NW quadrant
				internal.NW = insertHelp(elem, internal.NW, xMin, xMid, yMid, yMax); //inserts to NW
			}
			else if (d == Direction.SW) { //data goes in SW quadrant
				internal.SW = insertHelp(elem, internal.SW, xMin, xMid, yMin, yMid); //inserts to SW
			}
			else if (d == Direction.SE) { //data goes in SE quadrant
				internal.SE = insertHelp(elem, internal.SE, xMid, xMax, yMin, yMid); //inserts to SE
			}
			return sroot;
		}
	}


	// Pre:  elem != null
	// Post: If elem lies in the tree's region, and a matching element occurs
	//       in the tree, then that element has been removed.
	// Returns true iff a matching element has been removed from the tree.
	public boolean delete(T Elem) {
		successFlag = false;
		root = deleteHelp(Elem, root, xMin, xMax, yMin, yMax);
		return successFlag;
	}

	/**
	 *
	 */
	private prQuadNode deleteHelp(T elem, prQuadNode sroot, long xMin, long xMax, long yMin, long yMax) {
		if (sroot == null) { //not found
			return null;
		}
		if (sroot.leaf) { //leaf node, check elements to see if it's in there
			prQuadLeaf leaf = (prQuadLeaf) sroot;
			if (leaf.Elements.remove(elem)) { //try to remove data from leaf
				successFlag = true; //successful removal
				if (leaf.Elements.isEmpty()) { //check if leaf is now empty
					return null; //contracts branch
				}
				return sroot; //otherwise leaf is not empty
			}
			else { //data not found
				return sroot;
			}
		}
		else { //internal node, check which quadrant
			prQuadInternal internal = (prQuadInternal) sroot;
			long xMid = (xMin + xMax)/2; //the middle X point
			long yMid = (yMin + yMax)/2; //the middle Y point
			Direction d = elem.inQuadrant( xMin, xMax, yMin, yMax ); //gets which quadrant the data would be in
			if (d == Direction.NE) { //data goes in NE quadrant
				internal.NE = deleteHelp(elem, internal.NE, xMid, xMax, yMid, yMax); //inserts to NE
			}
			else if (d == Direction.NW) { //data goes in NW quadrant
				internal.NW = deleteHelp(elem, internal.NW, xMin, xMid, yMid, yMax); //inserts to NW
			}
			else if (d == Direction.SW) { //data goes in SW quadrant
				internal.SW = deleteHelp(elem, internal.SW, xMin, xMid, yMin, yMid); //inserts to SW
			}
			else if (d == Direction.SE) { //data goes in SE quadrant
				internal.SE = deleteHelp(elem, internal.SE, xMid, xMax, yMin, yMid); //inserts to SE
			}
			if (internal.numNodes() == 1) { //only 1 node, moves it up
				if (internal.NE != null) { //NE is the only remaining node
					if (internal.NE.leaf) { //only contracts if its a leaf
						return internal.NE;
					}
				}
				else if (internal.NW != null) { //NW is the only remaining node
					if (internal.NW.leaf) {
						return internal.NW;
					}
				}
				else if (internal.SE != null) { //SE is the only remaining node
					if (internal.SE.leaf) {
						return internal.SE;
					}
				}
				else if (internal.SW != null) { //SW is the only remaining node
					if (internal.SW.leaf) {
						return internal.SW;
					}
				}
			}
			return sroot;
		}
	}

	// Pre:  elem != null
	// Returns reference to an element x within the tree such that
	// elem.equals(x)is true, provided such a matching element occurs within
	// the tree; returns null otherwise.
	public T find(T Elem) {
		return findHelp(Elem, root, xMin, xMax, yMin, yMax);
	}

	//recursive function to help search for elements
	private T findHelp(T elem, prQuadNode sroot, long xMin, long xMax, long yMin, long yMax) {
		if (sroot == null) { //could not find data
			return null;
		}
		else if (sroot.leaf) { //is a leaf, would contain the data
			prQuadLeaf leaf = (prQuadLeaf) sroot;
			int index = leaf.Elements.indexOf(elem);
			if (index < 0) { //could not find element in Vector
				return null; //not in tree
			}
			else { //otherwise, found data
				return leaf.Elements.get(index); //returns the data in the vector
			}
		}
		else { //trying to insert into an internal node
			prQuadInternal internal = (prQuadInternal) sroot;
			long xMid = (xMin + xMax)/2; //the middle X point
			long yMid = (yMin + yMax)/2; //the middle Y point
			Direction d = elem.inQuadrant( xMin, xMax, yMin, yMax ); //gets which quadrant to search
			if (d == Direction.NE) { //data in NE quadrant
				return findHelp(elem, internal.NE, xMid, xMax, yMid, yMax); //checks NE
			}
			else if (d == Direction.NW) { //data in NW quadrant
				return findHelp(elem, internal.NW, xMin, xMid, yMid, yMax); //checks NW
			}
			else if (d == Direction.SW) { //data in SW quadrant
				return findHelp(elem, internal.SW, xMin, xMid, yMin, yMid); //checks SW
			}
			else if (d == Direction.SE) { //data in SE quadrant
				return findHelp(elem, internal.SE, xMid, xMax, yMin, yMid); //checks SE
			}
			return null; //could not find
		}
	}

	// Pre:  xLo, xHi, yLo and yHi define a rectangular region
	// Returns a collection of (references to) all elements x such that x is
	//in the tree and x lies at coordinates within the defined rectangular
	// region, including the boundary of the region.
	public Vector<T> find(long xLo, long xHi, long yLo, long yHi) {
		Vector<T> results = new Vector<T>(); //holds the results
		findHelp2(root, results, xLo, xHi, yLo, yHi, xMin, xMax, yMin, yMax);
		return results;
	}

	// helper functino for region search
	// xLo/xHi/yLo/yHi are the region to search
	// xMin/xMax/yMin/yMax are the bounds of the current node
	private void findHelp2(prQuadNode sroot, Vector<T> results, long xLo,
		long xHi, long yLo, long yHi, long xMin, long xMax, long yMin, long yMax) {
		if (sroot == null) { //no data to add
			return;
		}
		if (sroot.leaf) { //if leaf node, check all data to see if they go in
			@SuppressWarnings("unchecked")
			prQuadLeaf leaf = (prQuadLeaf) sroot;
			for (int k = 0; k < leaf.Elements.size(); k++) { //goes through each data in bucket
				if (leaf.Elements.get(k).inBox(xLo, xHi, yLo, yHi)) { //checks if point falls in region
					results.add(leaf.Elements.get(k));
				}
			}
		}
		else { //otherwise it's an internal node
			prQuadInternal internal = (prQuadInternal) sroot;
			long xMid = (xMin + xMax)/2; //the middle X point
			long yMid = (yMin + yMax)/2; //the middle Y point
			// check if each quadrant overlaps, if it does search that one
			if (overlap(xLo, xHi, yLo, yHi, xMid, xMax, yMid, yMax)) { //NE quadrant
				findHelp2(internal.NE, results, xLo, xHi, yLo, yHi, xMid, xMax, yMid, yMax); //searches NE quadrant
			}
			if (overlap(xLo, xHi, yLo, yHi, xMin, xMid, yMid, yMax)) { //NW quadrant
				findHelp2(internal.NW, results, xLo, xHi, yLo, yHi, xMin, xMid, yMid, yMax); //searches NW quadrant
			}
			if (overlap(xLo, xHi, yLo, yHi, xMin, xMid, yMin, yMid)) { //SW quadrant
				findHelp2(internal.SW, results, xLo, xHi, yLo, yHi, xMin, xMid, yMin, yMid); //searches SW quadrant
			}
			if (overlap(xLo, xHi, yLo, yHi, xMid, xMax, yMin, yMid)) { //SE quadrant
				findHelp2(internal.SE, results, xLo, xHi, yLo, yHi, xMid, xMax, yMin, yMid); //searches SE quadrant
			}
		}
	}

	public long getXMin() {
		return xMin;
	}
	public long getXMax() {
		return xMax;
	}
	public long getYMin() {
		return yMin;
	}
	public long getYMax() {
		return yMax;
	}

	// returns if the two rectangles given overlap in any way
	private boolean overlap(long xMin1, long xMax1, long yMin1, long yMax1,
		long xMin2, long xMax2, long yMin2, long yMax2) {

		//one of these will be true if the rectangles do not intersect
		if (xMin1 > xMax2 || xMax1 < xMin2 || yMax1 < yMin2 || yMin1 > yMax2) {
			return false;
		}
		return true; //otherwise they overlap
	}

	public void debug(RandomAccessFile l) {
		printTreeHelper(root, "", l);
	}

	public void printTreeHelper(prQuadNode sRoot, String Padding, RandomAccessFile l) {
		try {
			String newPadding = Padding.replace('-', ' ');;
			// Check for empty leaf
			if ( sRoot == null ) {
				l.writeBytes(newPadding.substring(0, Math.max(0, newPadding.lastIndexOf('|') + 1)) + "\n");
				l.writeBytes(Padding + "*\n");
				l.writeBytes(newPadding.substring(0, Math.max(0, newPadding.lastIndexOf('|') + 1)) + "\n");
				return;
			}
			// Check for and process SW and SE subtrees
			if ( sRoot.getClass().getName().equals("prQuadtree$prQuadInternal") ) {
				prQuadInternal p = (prQuadInternal) sRoot;
				l.writeBytes(newPadding.substring(0, Math.max(0, newPadding.lastIndexOf('|') + 1)) + "\n");
				printTreeHelper(p.SW, newPadding + "|----", l);
				printTreeHelper(p.SE, newPadding + "|----", l);
			}
			// Display indentation padding for current node
			//l.writeBytes(Padding);
			// Determine if at leaf or internal and display accordingly
			if ( sRoot.getClass().getName().equals("prQuadtree$prQuadLeaf") ) {
				prQuadLeaf p = (prQuadLeaf) sRoot;
				l.writeBytes(newPadding.substring(0, Math.max(0, newPadding.lastIndexOf('|') + 1)) + "\n");
				l.writeBytes( Padding + p.Elements.toString() + "\n");
				l.writeBytes(newPadding.substring(0, Math.max(0, newPadding.lastIndexOf('|') + 1)) + "\n");
			}
			else {
				l.writeBytes( Padding + "@\n" );
				// Check for and process NE and NW subtrees
				if ( sRoot.getClass().getName().equals("prQuadtree$prQuadInternal") ) {
					prQuadInternal p = (prQuadInternal) sRoot;
					//newPadding = newPadding.replace('|', ' ');
					printTreeHelper(p.NE, newPadding + "|----", l);
					printTreeHelper(p.NW, newPadding + "|----", l);
					l.writeBytes(newPadding.substring(0, Math.max(0, newPadding.lastIndexOf('|') + 1)) + "\n");
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}