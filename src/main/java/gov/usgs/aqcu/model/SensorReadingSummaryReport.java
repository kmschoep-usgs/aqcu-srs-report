package gov.usgs.aqcu.model;

import java.util.List;

public class SensorReadingSummaryReport {	
	private SensorReadingSummaryReportMetadata reportMetadata;
	private List<SensorReadingSummaryReading> readings;
	
	public SensorReadingSummaryReportMetadata getReportMetadata() {
		return reportMetadata;
	}
	
	public void setReportMetadata(SensorReadingSummaryReportMetadata val) {
		reportMetadata = val;
	}

	public List<SensorReadingSummaryReading> getReadings() {
		return readings;
	}

	public void setReadings(List<SensorReadingSummaryReading> readings) {
		this.readings = readings;
	}
	
}
	
