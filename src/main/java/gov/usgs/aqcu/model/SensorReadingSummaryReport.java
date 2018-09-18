package gov.usgs.aqcu.model;

import java.util.List;

public class SensorReadingSummaryReport {	
	private SensorReadingSummaryReportMetadata reportMetadata;
	private List<Readings> readings;
	
	public SensorReadingSummaryReportMetadata getReportMetadata() {
		return reportMetadata;
	}
	
	public void setReportMetadata(SensorReadingSummaryReportMetadata val) {
		reportMetadata = val;
	}

	public List<Readings> getReadings() {
		return readings;
	}

	public void setReadings(List<Readings> readings) {
		this.readings = readings;
	}
	
}
	
