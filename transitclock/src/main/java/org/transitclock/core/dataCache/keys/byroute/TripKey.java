package org.transitclock.core.dataCache.keys.byroute;

import java.util.Date;
/**
 * @author Sean Og Crudden
 * This key uses route rather than trip_id as a mean of looking up an individual trip in the cache.
 * 
 */
public class TripKey implements java.io.Serializable {
	
	/**
	 *  
	 */
	private static final long serialVersionUID = -2510753920238579214L;
	private String routeId;
	private String directionId;
	private Integer startTime;
	private Date tripStartDate;

	public TripKey(String routeId, String directionId, Integer startTime, Date tripStartDate) {
		super();
		this.routeId = routeId;
		this.directionId = directionId;
		this.startTime = startTime;
		this.tripStartDate = tripStartDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRouteId() {
		return routeId;
	}

	public String getDirectionId() {
		return directionId;
	}

	public Integer getStartTime() {
		return startTime;
	}

	public Date getTripStartDate() {
		return tripStartDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directionId == null) ? 0 : directionId.hashCode());
		result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((tripStartDate == null) ? 0 : tripStartDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TripKey other = (TripKey) obj;
		if (directionId == null) {
			if (other.directionId != null)
				return false;
		} else if (!directionId.equals(other.directionId))
			return false;
		if (routeId == null) {
			if (other.routeId != null)
				return false;
		} else if (!routeId.equals(other.routeId))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (tripStartDate == null) {
			if (other.tripStartDate != null)
				return false;
		} else if (!tripStartDate.equals(other.tripStartDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TripKey [routeId=" + routeId + ", directionId=" + directionId + ", startTime=" + startTime
				+ ", tripStartDate=" + tripStartDate + "]";
	}
	

	
}
