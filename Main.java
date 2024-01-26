import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.PriorityQueue;

public class Main {

	public static void main(String[] args) {
		long start2 = System.currentTimeMillis();
		// TODO Auto-generated method stub
		HashMap<String, airport> airports=new HashMap<>();
		
		try {
			BufferedReader reader= new BufferedReader(new FileReader(args[0]));
			String line=reader.readLine();
			line=reader.readLine();
			while (line!=null) {
				
				String[] splitted=line.split(",");
				airport newAirport=new airport(splitted[0], splitted[1], Double.valueOf(splitted[2]), Double.valueOf(splitted[3]), Double.valueOf(splitted[4]));
				airports.put(splitted[0], newAirport);
				line=reader.readLine();
			}
			reader.close();
			BufferedReader reader2= new BufferedReader(new FileReader(args[1]));
			String line2=reader2.readLine();
			line2=reader2.readLine();
			while (line2!=null) {
				String[] splitted=line2.split(",");
				airports.get(splitted[0]).directions.add(splitted[1]);
				line2=reader2.readLine();
			}
			
			reader2.close();
			BufferedReader reader3= new BufferedReader(new FileReader(args[2]));
			//System.out.println(airports.get("LTBZ").directions);
			HashMap<String, HashMap<Long, Integer>> weatherMap=new HashMap<>();
			String line3=reader3.readLine();
			line3=reader3.readLine();
			while (line3!=null) {
				String[] splitted=line3.split(",");
				if (!weatherMap.keySet().contains(splitted[0])) {
					HashMap<Long, Integer> newMap=new HashMap<>();
					newMap.put(Long.valueOf(splitted[1]), Integer.valueOf(splitted[2]));
					weatherMap.put(splitted[0], newMap);
					
				}
				else {
					weatherMap.get(splitted[0]).put(Long.valueOf(splitted[1]), Integer.valueOf(splitted[2]));
				}
				line3=reader3.readLine();
			}
			reader3.close();
			BufferedReader reader4= new BufferedReader(new FileReader(args[3]));
			String line4=reader4.readLine();
			line4=reader4.readLine();
			String log="";
			while (line4!=null) {
				String[] splitted=line4.split(" ");
				airport ar1=airports.get(splitted[0]);
				airport ar2=airports.get(splitted[1]);
				String addLog=findPath(ar1, ar2, airports, weatherMap, Long.valueOf(splitted[2]),log);
				log=log+addLog;
				line4=reader4.readLine();
			}
			reader4.close();
			//System.out.println(log);
			try {
				FileWriter abc=new FileWriter(args[4]);
				abc.write(log);
				abc.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			long end2 = System.currentTimeMillis();
			//System.out.println("Elapsed Time in milli seconds: "+ (end2-start2));
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}

	}
	public static double calculateWeatherMultiplier(int number) {
      
        // Convert the number to binary using Integer.toBinaryString()
        String binaryRepresentation = Integer.toBinaryString(number);

        // Pad with leading zeros to make it a 5-digit binary representation
        while (binaryRepresentation.length() < 5) {
            binaryRepresentation = "0" + binaryRepresentation;
        }

        // Extract individual bits from the binary representation
        int Bw = Character.getNumericValue(binaryRepresentation.charAt(0));
        int Br = Character.getNumericValue(binaryRepresentation.charAt(1));
        int Bs = Character.getNumericValue(binaryRepresentation.charAt(2));
        int Bh = Character.getNumericValue(binaryRepresentation.charAt(3));
        int Bb = Character.getNumericValue(binaryRepresentation.charAt(4));

        // Calculate the weather multiplier (W) using the extracted bits
        double W = (Bw * 1.05 + (1 - Bw)) * (Br * 1.05 + (1 - Br)) * (Bs * 1.10 + (1 - Bs))
                * (Bh * 1.15 + (1 - Bh)) * (Bb * 1.20 + (1 - Bb));

        return W;
	}
	public static double costFinder(HashMap<String, HashMap<Long, Integer>> weatherMap,long time,airport ar1,airport ar2) {
		double lat1 = Math.toRadians(ar1.latitude);
        double lon1 = Math.toRadians(ar1.longitude);
        double lat2 = Math.toRadians(ar2.latitude);
        double lon2 = Math.toRadians(ar2.longitude);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;

        // Calculate the distance using the Haversine formula
        double distance = r * c;
        /////////////////
        String airfield1=ar1.airfieldName;
        String airfield2=ar2.airfieldName;
        Integer weatherNum1=weatherMap.get(airfield1).get(time);
        Integer weatherNum2=weatherMap.get(airfield2).get(time);
        double w_d=calculateWeatherMultiplier(weatherNum1);
        double w_l=calculateWeatherMultiplier(weatherNum2);
        
        double flightCost=300*w_d*w_l+distance;
        return flightCost;
	}
	public static String findPath(airport ar1,airport ar2,HashMap<String, airport> airports, HashMap<String, HashMap<Long, Integer>> weatherMap,long time,String log) {
		PriorityQueue<airport> airportQ=new PriorityQueue<>(Comparator.comparingDouble(airport::getDistance));
		HashMap<airport, Double> distances=new HashMap<>();
		HashMap<airport, airport> predecessors= new HashMap<>();
		ar1.setDistance(0.0);
		distances.put(ar1, 0.0);
		airportQ.add(ar1);
		while (!airportQ.isEmpty()) {
			airport currentAirport=airportQ.poll();
			if (currentAirport.equals(ar2)) {
				String returnString=printShortestPath(ar2, predecessors, distances);
				//System.out.println(returnString);
				log=log+returnString;
				return returnString;
				
			}			
			
			for (String airport : currentAirport.directions) {
				airport neighbourAirport=airports.get(airport);
				double newDistance=distances.get(currentAirport) + costFinder(weatherMap, time, currentAirport, neighbourAirport);
				if (newDistance<distances.getOrDefault(neighbourAirport, Double.MAX_VALUE)) {
					distances.put(neighbourAirport, newDistance);
					predecessors.put(neighbourAirport, currentAirport);
					neighbourAirport.setDistance(newDistance);
					airportQ.add(neighbourAirport);
				}
			}
		}
		return "";
	}
	private static String printShortestPath(airport destination, HashMap<airport, airport> predecessors, HashMap<airport, Double> distances) {
        // Retrieve the path and cost
		String logf="";
        ArrayList<airport> path = new ArrayList<>();
        double cost = distances.getOrDefault(destination, Double.POSITIVE_INFINITY);

        while (destination != null) {
            path.add(destination);
            destination = predecessors.get(destination);
        }

        // Print the path in reverse order (from source to destination)
        Collections.reverse(path);

        // Print the path and cost
        //System.out.println("Shortest Path: " );
        for (airport airport : path) {
			//System.out.print(airport.airportCode + " ");
			logf=logf+airport.airportCode+" ";
		}
        //System.out.printf(Locale.US, "%.5f%n", cost);
        String formattedCost = String.format(Locale.US, "%.5f", cost);
        logf=logf+formattedCost+"\n";
        
        return logf;
    }
	

}
