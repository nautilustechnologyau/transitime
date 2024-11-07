/*
 * This file is part of thetransitclock.org
 *
 * thetransitclock.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * thetransitclock.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with thetransitclock.org .  If not, see <http://www.gnu.org/licenses/>.
 */
package org.transitclock.custom.mymetro;

import io.swagger.client.ApiClient;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.Device;
import io.swagger.client.model.Position;
import io.swagger.client.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.transitclock.avl.PollUrlAvlModule;
import org.transitclock.config.StringConfigValue;
import org.transitclock.db.structs.AvlReport;
import org.transitclock.db.structs.AvlReport.AssignmentType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Sean Óg Crudden This module integrates TheTransitClock with the API of a MyMetro traccar
 * server to get vehicle locations.
 * <p>
 * See http://www.traccar.org
 * <p>
 * It uses classes that where generated using the swagger file provided
 * with traccar.
 */
public class MyMetroAVLModule extends PollUrlAvlModule {

    protected User user = null;
    protected DefaultApi api = null;


    protected static StringConfigValue traccarEmail = new StringConfigValue("transitclock.avl.mymetro.traccar.email", "admin",
            "This is the username for the traccar server api.");

    protected static StringConfigValue traccarPassword = new StringConfigValue("transitclock.avl.mymetro.traccar.password",
            "admin", "This is the password for the traccar server api");

    protected static StringConfigValue traccarBaseUrl = new StringConfigValue("transitclock.avl.mymetro.traccar.baseurl",
            "http://127.0.0.1:8082/api", "This is the url for the traccar server api.");

    protected static StringConfigValue traccarSource = new StringConfigValue("transitclock.avl.mymetro.source",
            "MyMetro Traccar", "This is the value recorded in the source for the AVL Report.");

    private static final String TRIP_ID_FIELD = "tripId";
    private static final String ROUTE_ID_FIELD = "routeId";
    private static final String BLOCK_ID_FIELD = "blockId";
    private static final String HEADING_FIELD = "heading";
    private static final String DRIVER_ID_FIELD = "driverId";
    private static final String LICENSE_PLATE_FIELD = "licensePlate";
    private static final String PASSENGER_COUNT_FIELD = "passengerCount";
    private static final String PASSENGER_FULLNESS_FIELD = "passengerFullness";

    private static final Logger logger = LoggerFactory.getLogger(MyMetroAVLModule.class);

    public MyMetroAVLModule(String agencyId) throws Throwable {
        super(agencyId);
        api = new DefaultApi();
        ApiClient client = new ApiClient();
        client.setBasePath(traccarBaseUrl.getValue());
        client.setUsername(traccarEmail.getValue());
        client.setPassword(traccarPassword.getValue());
        api.setApiClient(client);
        user = api.sessionPost(traccarEmail.getValue(), traccarPassword.getValue());
        user.getId();
        if (user != null) {
            logger.debug("MyMetro Traccar login succeeded with user: " + user);
        }
    }

    @Override
    protected void getAndProcessData() throws Exception {
        Collection<AvlReport> avlReportsReadIn = new ArrayList<AvlReport>();
        long startTime = System.currentTimeMillis();
        List<Device> devices = api.devicesGet(false, user.getId(), null, null);
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        logger.debug("Device list retrieved in: " + duration + " seconds");

        if (api != null && user != null) {
            List<Position> results = api.positionsGet(null, null, null, null);
            for (Position position : results) {
                AvlReport avlReport = populateAvlReport(devices, position);
                avlReportsReadIn.add(avlReport);
            }

            forwardAvlReports(avlReportsReadIn);
        }
    }

    protected AvlReport populateAvlReport(List<Device> devices, Position position) {
        Device device = MyMetroUtils.findDeviceById(devices, position.getDeviceId());

        AvlReport avlReport = null;

        logger.debug("Device: " + device + " found for position: " + position);

        // If have device details use name.
        if (device != null && device.getName() != null && !device.getName().isEmpty()) {
            avlReport = new AvlReport(device.getName(),
                    position.getDeviceTime().toDate().getTime(), position.getLatitude().doubleValue(),
                    position.getLongitude().doubleValue(), traccarSource.toString());
        } else {
            avlReport = new AvlReport(position.getDeviceId().toString(),
                    position.getDeviceTime().toDate().getTime(), position.getLatitude().doubleValue(),
                    position.getLongitude().doubleValue(), traccarSource.toString());
        }

        logger.debug("Initial AVL Report created: " + avlReport);

        // Use device unique id as the block id by default
        // we will try to identify assignment later if possible
        if (device != null) {
            avlReport.setAssignment(device.getUniqueId(), AssignmentType.BLOCK_ID);
        }
        // Assign trip/route/block id if available
        Object tripId = MyMetroUtils.stripAgencyId(MyMetroUtils.findAttribute(position, TRIP_ID_FIELD));
        Object routeId = MyMetroUtils.stripAgencyId(MyMetroUtils.findAttribute(position, ROUTE_ID_FIELD));
        Object blockId = MyMetroUtils.stripAgencyId(MyMetroUtils.findAttribute(position, BLOCK_ID_FIELD));
        if (tripId != null && !tripId.toString().isEmpty()) {
            logger.debug("Trip ID received: " + tripId);
            avlReport.setAssignment(tripId.toString(), AssignmentType.TRIP_ID);
        } else if (routeId != null && !routeId.toString().isEmpty()) {
            logger.debug("Route ID received: " + routeId);
            avlReport.setAssignment(routeId.toString(), AssignmentType.ROUTE_ID);
        } else if (blockId != null && !blockId.toString().isEmpty()) {
            logger.debug("Block ID received: " + blockId);
            avlReport.setAssignment(blockId.toString(), AssignmentType.BLOCK_ID);
        }

        // Assign speed if available
        if (position.getSpeed() != null) {
            avlReport.setSpeed(position.getSpeed().floatValue());
        }

        // Assign bearing if available
        Object heading = MyMetroUtils.findAttribute(position, HEADING_FIELD);
        if (heading != null) {
            avlReport.setHeading(Float.parseFloat(heading.toString()));
        }

        // Assign driver id if available
        Object driverId = MyMetroUtils.findAttribute(position, DRIVER_ID_FIELD);
        if (driverId != null) {
            avlReport.setDriverId(driverId.toString());
        }

        // Assign license plate if available
        Object licensePlate = MyMetroUtils.findAttribute(position, LICENSE_PLATE_FIELD);
        if (licensePlate != null) {
            avlReport.setLicensePlate(licensePlate.toString());
        }

        // Assign passenger count if available
        Object passengerCount = MyMetroUtils.findAttribute(position, PASSENGER_COUNT_FIELD);
        if (passengerCount != null) {
            avlReport.setPassengerCount(Integer.parseInt(passengerCount.toString()));
        }

        // Assign fullness if available
        Object passengerFullness = MyMetroUtils.findAttribute(position, PASSENGER_FULLNESS_FIELD);
        if (passengerFullness != null) {
            avlReport.setPassengerCount(Integer.parseInt(passengerFullness.toString()));
        }

        return avlReport;
    }

    @Override
    protected Collection<AvlReport> processData(InputStream in) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected void forwardAvlReports(Collection<AvlReport> avlReportsReadIn) {
        processAvlReports(avlReportsReadIn);
    }

}