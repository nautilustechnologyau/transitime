package org.transitclock.custom.gcrpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.timekeepers.assignmentgrabber.AssignmentGrabber;
import org.timekeepers.assignmentgrabber.VehicleAssignmentInfo;
import org.transitclock.avl.AvlExecutor;
import org.transitclock.avl.calAmp.CalAmpAvlModule;
import org.transitclock.config.StringConfigValue;
import org.transitclock.db.structs.AvlReport;
import org.transitclock.db.structs.AvlReport.AssignmentType;
import static java.util.concurrent.TimeUnit.*;

import java.io.File;

public class GCRPCAVLModule extends CalAmpAvlModule {
	static final Logger logger = LoggerFactory.getLogger(GCRPCAVLModule.class);
	
	private static StringConfigValue assignmentSheetLocation= new StringConfigValue(
			"transitclock.avl.gcrpc.assignmentSheetLocation", "/home/ubuntu/workspace/gcrpc/assignmentsheets/Bus Operator Schedule Fiscal Year 2020-2021.xlsx",
			"This is the location of the assignment spreadsheet on the server");
	
	private static AssignmentGrabber grabber = new AssignmentGrabber();

	public GCRPCAVLModule(String agencyId) {
		super(agencyId);

		AssignmentControl assingmentController = new AssignmentControl();
		assingmentController.grabAssignment();

	}

	@Override
	protected void processPackets(DatagramSocket socket) {
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
				if (report != null) {
					VehicleAssignmentInfo assignmentInfo = null;
					if (grabber != null && grabber.getAssignmentInfo() != null
							&& grabber.getAssignmentInfo().getAssignmentByVehicle() != null) {
						if (grabber.getAssignmentInfo().getAssignmentByVehicle().get(report.getVehicleId()) != null) {
							assignmentInfo = grabber.getAssignmentInfo().getAssignmentByVehicle()
									.get(report.getVehicleId());
						}
					}

					AvlReport avlReport = new AvlReport(report.getVehicleId(), report.getTime().getTime(),
							report.getLatitude(), report.getLongditude(), new Float(report.getSpeed() * 0.514444),
							report.getHeading(), "GCRPC");

					if (assignmentInfo != null)
					{						
						/*List<Route> routes = Core.getInstance().getDbConfig().getRoutes();
						while(routes.iterator().hasNext())
						{
							Route route=routes.iterator().next();
							if(route.getLongName().contains(assignmentInfo.getGTFSRouteId()))
							{																
								avlReport.setAssignment(route.getLongName(), AssignmentType.ROUTE_ID);
								
							}
							
						}
						*/
						if(assignmentInfo.getGTFSRouteId()!=null)
							avlReport.setAssignment(assignmentInfo.getGTFSRouteId(), AssignmentType.ROUTE_ID);
					}

					// Use AvlExecutor to actually process the data using a thread
					// executor
					AvlExecutor.getInstance().processAvlReport(avlReport);
				}
			}
		} catch (Exception e) {
			logger.error("Exception while parsing GCPRC message. {}", e.getMessage(), e);
		}
	}

	class AssignmentControl {
		private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		public void grabAssignment() {
			final Runnable grabberExecuteable = new Runnable() {
				public void run() {

					try {

						GCRPCAVLModule.grabber.updateAssignmentInfo(new File(assignmentSheetLocation.getValue()));
					} catch (Exception e) {
						logger.error("Failed to read assingment spreadsheet:" + assignmentSheetLocation.getValue(), e);
					}
				}
			};

			final ScheduledFuture<?> grabHandle = scheduler.scheduleAtFixedRate(grabberExecuteable, 10, 180, SECONDS);

		}
	}
}
