package org.transitclock.core.dataCache.keys.byroute;

import java.util.Date;

public class KalmanErrorCacheKey extends StopPathCacheKey implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7423030175916471434L;

	// The vehicleId is only used for debug purposed we know in log which vehicle set the error value
	private String vehiceId;


	public KalmanErrorCacheKey(String routeId, String directionId, Integer startTime, Date tripStartDate,
			String originStopId, String destinationStopId, boolean travelTime, String vehiceId) {
		super(routeId, directionId, startTime, tripStartDate, originStopId, destinationStopId, travelTime);
		this.vehiceId = vehiceId;
	}

	public String getVehiceId() {
		return vehiceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((vehiceId == null) ? 0 : vehiceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		KalmanErrorCacheKey other = (KalmanErrorCacheKey) obj;
		if (vehiceId == null) {
			if (other.vehiceId != null)
				return false;
		} else if (!vehiceId.equals(other.vehiceId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "KalmanErrorCacheKey [vehiceId=" + vehiceId + ", toString()=" + super.toString() + "]";
	}
	
}
