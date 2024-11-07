package org.transitclock.custom.mymetro;

import org.transitclock.avl.GtfsRealtimeModule;
import org.transitclock.db.structs.AvlReport;

public class KsrtcGtfsRealtimeModule extends GtfsRealtimeModule {
    /**
     * @param projectId
     */
    public KsrtcGtfsRealtimeModule(String projectId) {
        super(projectId);
    }

    @Override
    protected void processAvlReport(AvlReport avlReport) {
        if (avlReport != null) {
            avlReport.setAssignment(avlReport.getVehicleId(), AvlReport.AssignmentType.BLOCK_ID);
        }
        super.processAvlReport(avlReport);
    }
}
