/*
 */
package gov.usgs.aqcu.calc;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import gov.usgs.aqcu.model.SensorReadingSummaryPoint;
import gov.usgs.aqcu.model.SensorReadingSummaryReading;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;

import java.util.ArrayList;

import static java.util.Collections.binarySearch;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import java.time.Instant;

/**
 * Service that calculates and returns the nearest points to a supplied time
 * 
 * @author kmschoep
 */
public class NearestTimePointCalculator {

	
	public List<SensorReadingSummaryReading> findNearest(
			List<SensorReadingSummaryReading> readings, 
			TimeSeriesDataServiceResponse timeSeries,
			Boolean isCorrected) {
		List<SensorReadingSummaryPoint> timeSeriesPoints = createSrsPoints(timeSeries.getPoints());
		List<Instant> timeSeriesPointsTimeList = timeSeriesPoints.stream().map(SensorReadingSummaryPoint::getTime).collect(Collectors.toList());
				
		//LinkedTreeMap comparator to binary search the list for a close point.
		Comparator<Instant> c = new Comparator<Instant>() {
			@Override
			public int compare(Instant o1, Instant o2) {
				return o1.compareTo(o2);
			}
		};
		
		//Point to look into and placeholder hashmap to add into the list.
		int index;
		
		//Binary searches using each of the samplePoints. 
		for(SensorReadingSummaryReading pointToLookFor : readings){
			index = binarySearch(timeSeriesPointsTimeList, pointToLookFor.getDisplayTime(), c);
			if(index < 0){
				//If index is -1, then it wants to insert at point 0 which means 
				//that 0th point is the closet point.
				if(index == -1){
					index = 0;
				}
				//Otherwise, flip index, and look to see which of the two points
				// index is between is closest.
				else {
					index = -index;
					if (index - 1 == timeSeriesPoints.size()){
						index = index - 2;
					}
					else if (Math.abs(dateTimeDistance(pointToLookFor.getDisplayTime(), timeSeriesPoints.get(index - 1).getTime()))
							< (Math.abs(dateTimeDistance(pointToLookFor.getDisplayTime(), timeSeriesPoints.get(index - 2).getTime())))) {
						index = index - 1;
					} else {
						index = index - 2;
					}
				}
			}
			if (isCorrected) {
				setNearestCorrected(timeSeriesPoints, pointToLookFor, index);
			} else {
				setNearestRaw(timeSeriesPoints, pointToLookFor, index);
			}
		
			//The qualifiers to add to the current reading
			List<Qualifier> tsQualifiers = timeSeries.getQualifiers();
			
			if (tsQualifiers != null) {
				List<Qualifier> qualifiers = new ArrayList<>();
				
				//Go through quals and check if time is within the range.
				for(Qualifier qual : tsQualifiers){
					if(c.compare(pointToLookFor.getDisplayTime(), qual.getStartTime()) >= 0 && !(qualifiers.contains(qual))){
						if(c.compare(pointToLookFor.getDisplayTime(), qual.getEndTime()) <= 0){
							qualifiers.add(qual);
						}
					}
				}
				pointToLookFor.setQualifiers(qualifiers);
				
			} else {
				pointToLookFor.setQualifiers(null);
			}
		}
		return readings;
		
	}

	
	/**
	 * Calculates the distance between two times
	 * 
	 * @param o1 The first time to compare
	 * @param o2 The second time to compare
	 * @return
	 */
	public long dateTimeDistance(Instant o1, Instant o2){
		return o1.toEpochMilli() - o2.toEpochMilli();
	}
	
	/**
	 * This method should only be called if the timeSeriesPoints list is not null.
	 */
	protected List<SensorReadingSummaryPoint> createSrsPoints(List<TimeSeriesPoint> timeSeriesPoints) {
		List<SensorReadingSummaryPoint> srsPoints = timeSeriesPoints.parallelStream()
				.filter(x -> x.getValue().getNumeric() != null)
				.map(x -> {
					SensorReadingSummaryPoint srsPoint = new SensorReadingSummaryPoint();
					srsPoint.setTime(x.getTimestamp().DateTimeOffset);
					srsPoint.setValue(DoubleWithDisplayUtil.getRoundedValue(x.getValue()));
					return srsPoint;
				})
				.collect(Collectors.toList());
		return srsPoints;
	}
	
	protected void setNearestCorrected(List<SensorReadingSummaryPoint> tsPoints, SensorReadingSummaryReading reading, Integer idx) {
		if (tsPoints.isEmpty()) {
			reading.setNearestCorrectedTime(null);
			reading.setNearestCorrectedValue(null);
		} else { //otherwise, get times and corresponding points.
			SensorReadingSummaryPoint nearestPoint = tsPoints.get(idx);
			reading.setNearestCorrectedTime(nearestPoint.getTime());
			if (nearestPoint.getValue() != null) {
				reading.setNearestCorrectedValue(nearestPoint.getValue().toString());
			} else {
				reading.setNearestCorrectedValue("");
			}
		}
	}
	
	protected void setNearestRaw(List<SensorReadingSummaryPoint> tsPoints, SensorReadingSummaryReading reading, Integer idx) {
		if (tsPoints.isEmpty()) {
			reading.setNearestRawTime(null);
			reading.setNearestRawValue(null);
		} else { //otherwise, get times and corresponding points.
			SensorReadingSummaryPoint nearestPoint = tsPoints.get(idx);
			reading.setNearestRawTime(nearestPoint.getTime());
			if (nearestPoint.getValue() != null) {
				reading.setNearestRawValue(nearestPoint.getValue().toString());
			} else {
				reading.setNearestRawValue("");
			}
		}
	}
}
