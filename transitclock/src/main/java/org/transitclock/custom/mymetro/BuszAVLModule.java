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
public class BuszAVLModule extends MyMetroAVLModule {
    private static final Logger logger = LoggerFactory.getLogger(BuszAVLModule.class);

    public BuszAVLModule(String agencyId) throws Throwable {
        super(agencyId);
    }

    @Override
    protected void getAndProcessData() throws Exception {
        Collection<AvlReport> avlReportsReadIn = new ArrayList<AvlReport>();
        long startTime = System.currentTimeMillis();
        List<Device> devices = api.devicesGet(false, user.getId(), null, null);
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        logger.debug("Device list retrieved in: " + duration + " seconds. " + devices);

        if (api != null && user != null) {
            List<Position> results = api.positionsGet(null, null, null, null);
            for (Position position : results) {
                AvlReport avlReport = populateAvlReport(devices, position);

                logger.debug("Overriding AVL Report...");
                logger.debug("Original AVL Report: " + avlReport);

                // if the assignment type is BLOCK_ID and the assignment id
                // is device id, then we will replace it with device name
                Device device = MyMetroUtils.findDeviceById(devices, position.getDeviceId());
                String deviceId = null;
                String deviceName = null;
                if (device != null) {
                    deviceId = device.getUniqueId();
                    deviceName = device.getName();
                    logger.debug("Device Unique ID: " + deviceId + ", Device Name: " + deviceName);

                    if (deviceName != null && !deviceName.isEmpty()) {
                        if ((avlReport.getAssignmentType() == AssignmentType.UNSET)
                                || (avlReport.getAssignmentType() == AssignmentType.BLOCK_ID
                                && avlReport.getAssignmentId().equals(deviceId))) {
                            avlReport.setAssignment(deviceName, AssignmentType.BLOCK_ID);
                            logger.debug("Updated AVL Report: " + avlReport);
                        } else {
                            logger.error("Overriding AVL Report failed because either assignment type has value or assignment is BLOCK_ID and assignment ID is not set as device ID previously.");
                        }
                    } else {
                        logger.error("Overriding AVL Report failed because device Name is null or empty AVL Report");
                    }
                } else {
                    logger.error("Overriding AVL Report failed because device is null");
                }

                avlReportsReadIn.add(avlReport);
            }

            forwardAvlReports(avlReportsReadIn);
        }
    }
}