package org.transitclock.custom.gcrpc;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.transitclock.avl.AvlExecutor;
import org.transitclock.db.structs.AvlReport;
import org.transitclock.db.structs.AvlReport.AssignmentType;




public class Report {

	String vehicleId;
	Date time;
	Double longditude;
	Double latitude;
	// Speed is in Knots
	Float speed;
	Float heading;
	String routeName;
	
	public String getRouteName() {
		return routeName;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public Date getTime() {
		return time;
	}

	public Double getLongditude() {
		return longditude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Float getSpeed() {
		return speed;
	}

	public Float getHeading() {
		return heading;
	}

	public Report(String vehicleId, String routeName, Date time,  Double latitude, Double longditude,  Float speed, Float heading) {
		super();
		this.vehicleId = vehicleId;
		this.time = time;
		this.longditude = longditude;
		this.latitude = latitude;
		this.speed = speed;
		this.heading = heading;
	}

	public void process() {		
		AvlReport avlReport =
				new AvlReport(getVehicleId(), getTime().getTime(), getLatitude(),
						getLongditude(), new Float(getSpeed()*0.514444), getHeading(), "GCRPC");
		
	

		// Use AvlExecutor to actually process the data using a thread
		// executor
		AvlExecutor.getInstance().processAvlReport(avlReport);
	}

	public static Report parseReport(DatagramPacket packet) {
					
		String s = new String(packet.getData(), StandardCharsets.UTF_8);
				
		String[] fields = s.split(",");		
						
		if(fields.length>6 && fields[2].equalsIgnoreCase("A"))
		{
						
			Report report;
			try {
				String dateAndTime=fields[9]+fields[1];
				SimpleDateFormat dateFormatter=new SimpleDateFormat("ddMMyyHHmmss");
				dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
				
				String gpsParts[]=SeparararDMS(fields[3], 1);
				double latitute = DMSaDecimal(new Integer(gpsParts[0]),new Integer(gpsParts[1]),new Double(gpsParts[2]), fields[4]);
				gpsParts=SeparararDMS(fields[5], 2);
				double longditude= DMSaDecimal(new Integer(gpsParts[0]),new Integer(gpsParts[1]),new Double(gpsParts[2]), fields[6]);
				
				report = new Report(fields[13].split("\\*")[0], null,
						dateFormatter.parse(dateAndTime.substring(0, 12)), latitute,longditude,new Float(fields[7]),Float.NaN);
				return report;
			} catch (Exception e) {				
				e.printStackTrace();
			} 			
		}
		return null;
	}

	@Override
	public String toString() {
		return "Report [vehicleId=" + vehicleId + ", time=" + time + ", longditude=" + longditude + ", latitude="
				+ latitude + ", speed=" + speed + ", heading=" + heading + "]";
	}

	public static double DMSaDecimal(int grados, int minutos, double segundos, String direccion) {

	    double decimal = Math.signum(grados) * (Math.abs(grados) + (minutos / 60.0) + (segundos / 3600.0));

	    //reverse for south or west coordinates; north is assumed
	    if (direccion.equals("S") || direccion.equals("W")) {
	        decimal *= -1;
	    }

	    return decimal;
	}

	public static String[] SeparararDMS(String coordenada, int type) {

	    String grados = null;
	    String minutos = null;
	    String segundos = null;
	    String direccion = null;


	    switch (type) {
	        case 1: // latitude
	            grados = coordenada.substring(0, 2);
	            minutos = coordenada.substring(2, 4);
	            segundos = coordenada.substring(5, coordenada.length() - 1);
	            break;
	        case 2: // longitude
	            grados = coordenada.substring(0, 3);
	            minutos = coordenada.substring(3, 5);
	            segundos = coordenada.substring(6, coordenada.length() - 1);
	            break;
	        default:

	    }

	    double sec = 0;
	    try {
	        sec = Double.parseDouble("0."+segundos);
	    }catch(Exception e) {

	    }


	    sec = (sec * 60);
	    direccion = coordenada.substring(coordenada.length() - 1);

	    String[] data = {grados, minutos, sec + "", direccion};
	    return data;
	}
}
