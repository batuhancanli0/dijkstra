import java.util.ArrayList;

public class airport {
	String airportCode;
	String airfieldName;
	double latitude;
	double longitude;
	double parkingCost;
	double distance;
	
	ArrayList<String> directions=new ArrayList<>();

	public airport(String airportCode, String airfieldName, double latitude, double longitude, double parkingCost) {
		
		this.airportCode = airportCode;
		this.airfieldName = airfieldName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.parkingCost = parkingCost;
		this.distance=Double.POSITIVE_INFINITY;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	
	
	

}
