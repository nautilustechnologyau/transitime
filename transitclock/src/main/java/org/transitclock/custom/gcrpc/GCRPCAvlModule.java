package org.transitclock.custom.gcrpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.transitclock.avl.AvlExecutor;
import org.transitclock.avl.AvlModule;
import org.transitclock.config.IntegerConfigValue;
import org.transitclock.core.dataCache.VehicleDataCache;
import org.transitclock.db.structs.AvlReport;
import org.transitclock.db.structs.VehicleConfig;

public class GCRPCAvlModule extends AvlModule {

	private static IntegerConfigValue gcrpcFeedPort = new IntegerConfigValue(
			"transitclock.avl.gcrpcfeedport", 8080,
			"The port number for the UDP socket connection for the "
					+ "GCPRC feed.");

	private static final Logger logger = 
			LoggerFactory.getLogger(GCRPCAvlModule.class);

	/********************** Member Functions **************************/

	/**
	 * Constructor
	 * 
	 * @param agencyId
	 */
	public GCRPCAvlModule(String agencyId) {
		super(agencyId);
	}

	private void processPackets(DatagramSocket socket) {
		// Read from the socket
		try {
			// Create the DatagramPacket that data is read into
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);

			while (true) {
				// Get the bytes containing the report via UDP
				socket.receive(packet);
				
				// Convert the bytes into a report
				Report report = Report.parseReport(packet);
				
				// Actually process the report. Creates an AvlReport and 
				// processes it
				if (report != null)
					report.process();
			}
		} catch (Exception e) {
			logger.error("Exception while parsing CalAmp message. {}", 
					e.getMessage(), e);
		}		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// Log that module successfully started
		logger.info("Started module {} for agencyId={}", getClass().getName(),
				getAgencyId());

		while (true) {
			logger.info("Starting DatagramSocket on port {}",
					gcrpcFeedPort.getValue());

			try {
				// Open up the DatagramSocket
				DatagramSocket socket = null;
				try {
					socket = new DatagramSocket(gcrpcFeedPort.getValue());
				} catch (SocketException e1) {
					logger.error("Exception occurred opening DatagramSocket "
							+ "on port {}. {}", gcrpcFeedPort.getValue(),
							e1.getMessage(), e1);
					System.exit(-1);
				}

				// Process the data from the socket
				processPackets(socket);

				// If made it here something went wrong so close up
				// DatagramSocket and try again.
				socket.close();
			} catch (Exception e) {
				logger.error("Unexpected exception {}", e.getMessage(), e);
			}
		}
	}
	
}
