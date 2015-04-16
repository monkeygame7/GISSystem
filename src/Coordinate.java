public class Coordinate implements Compare2D<Coordinate> {

	private LatLong lon; //longitude
	private LatLong lat; //latitude

	public Coordinate() {
		lon = null;
		lat = null;
	}

	public Coordinate(LatLong lo, LatLong la) {
		lon = lo;
		lat = la;
	}

	public boolean isEmpty() {
		return (lon == null) && (lat == null);
	}

	public long getX() {
		return lon.totalSeconds();
	}

	public long getY() {
		return lat.totalSeconds();
	}

	public Direction directionFrom(long X, long Y) {
		if (lat.totalSeconds() >= Y && lon.totalSeconds() > X) {
			return Direction.NE;
		}
		if (lon.totalSeconds() <= X && lat.totalSeconds() > Y) {
			return Direction.NW;
		}
		if (lat.totalSeconds() <= Y && lon.totalSeconds() < X) {
			return Direction.SW;
		}
		if (lat.totalSeconds() < Y && lon.totalSeconds() >= X) {
			return Direction.SE;
		}
		return Direction.NOQUADRANT;
	}

	public Direction inQuadrant(double xLo, double xHi,
		double yLo, double yHi) {
		if (lon.totalSeconds() < xLo || lon.totalSeconds() > xHi || lat.totalSeconds() < yLo || lat.totalSeconds() > yHi) {
			return Direction.NOQUADRANT;
		}
		double X = (xLo + xHi)/2;
		double Y = (yLo + yHi)/2;
		if ((lon.totalSeconds() == X && lat.totalSeconds() == Y) || (lat.totalSeconds() >= Y && lon.totalSeconds() > X)) {
			return Direction.NE;
		}
		if (lon.totalSeconds() <= X && lat.totalSeconds() > Y) {
			return Direction.NW;
		}
		if (lat.totalSeconds() <= Y && lon.totalSeconds() < X) {
			return Direction.SW;
		}
		if (lat.totalSeconds() < Y && lon.totalSeconds() >= X) {
			return Direction.SE;
		}
		return Direction.NOQUADRANT;
	}

	public boolean   inBox(double xLo, double xHi,
		double yLo, double yHi) {
		return ((lon.totalSeconds() >= xLo && lon.totalSeconds() <= xHi) && (lat.totalSeconds() >= yLo && lat.totalSeconds() <= yHi));
	}

	public String toString() {
		return ("" + lon.toString() + " " + lat.toString());
	}

	public boolean equals(Object o) {
		if (this.getClass().getName().equals( o.getClass().getName())) {
			Coordinate p = (Coordinate)o;
			return (lon.totalSeconds() == p.getX() && lat.totalSeconds() == p.getY());
		}
		return false;
	}

	public void setLat(LatLong l) {
		lat = l;
	}

	public void setLong(LatLong l) {
		lon = l;
	}

	public LatLong getLat() {
		return lat;
	}

	public LatLong getLong() {
		return lon;
	}
}