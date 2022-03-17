package org.transitclock.core.dataCache.keys.byroute;

import java.util.Date;

/**
 * @author Sean Og Crudden
 * This key uses route and stop data rather than trip_id and stop path as a mean of looking up an individual stop paths in the cache.
 * 
 */
public class StopPathCacheKey extends TripKey implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5269421047453594588L;
	
	private String originStopId;
	private String destinationStopId;
		
	private boolean travelTime;

	

	public StopPathCacheKey(String routeId, String directionId, Integer startTime, Date tripStartDate,
			String originStopId, String destinationStopId, boolean travelTime) {
		super(routeId, directionId, startTime, tripStartDate);
		this.originStopId = originStopId;
		this.destinationStopId = destinationStopId;
		this.travelTime = travelTime;
	}

	public String getOriginStopId() {
		return originStopId;
	}

	public String getDestinationStopId() {
		return destinationStopId;
	}

	public boolean isTravelTime() {
		return travelTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((destinationStopId == null) ? 0 : destinationStopId.hashCode());
		result = prime * result + ((originStopId == null) ? 0 : originStopId.hashCode());
		result = prime * result + (travelTime ? 1231 : 1237);
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
		StopPathCacheKey other = (StopPathCacheKey) obj;
		if (destinationStopId == null) {
			if (other.destinationStopId != null)
				return false;
		} else if (!destinationStopId.equals(other.destinationStopId))
			return false;
		if (originStopId == null) {
			if (other.originStopId != null)
				return false;
		} else if (!originStopId.equals(other.originStopId))
			return false;
		if (travelTime != other.travelTime)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StopPathCacheKey [originStopId=" + originStopId + ", destinationStopId=" + destinationStopId
				+ ", travelTime=" + travelTime + "]";
	}
	
		
}
