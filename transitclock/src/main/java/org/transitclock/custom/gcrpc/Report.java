package org.transitclock.custom.gcrpc;

import java.net.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Report {
				
	public static Report parseReport(DatagramPacket packet) {
		// TODO Auto-generated method stub
		logger.info("UDP packet received {}",new String(packet.getData()));
		return null;
	}
	protected static final Logger logger = 
			LoggerFactory.getLogger(Report.class);
	
	public void process() {
	
	}

}
