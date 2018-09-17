package gov.usgs.aqcu.model;

import java.time.ZoneOffset;

import gov.usgs.aqcu.parameter.SensorReadingSummaryRequestParameters;

public class SensorReadingSummaryReportMetadata extends ReportMetadata {
	private SensorReadingSummaryRequestParameters requestParameters;
	private String timeSeriesParams;
	private String timeSeriesUniqueId;
	private String timeseriesLabel;
	private String requestingUser;

	public String getTimeSeriesUniqueId() {
		return timeSeriesUniqueId;
	}
	
	public String getTimeseriesLabel() {
		return timeseriesLabel;
	}
	
	public String getTimeSeriesParams() {
		return timeSeriesParams;
	}

	public String getRequestingUser() {
		return requestingUser;
	}
	
	public SensorReadingSummaryRequestParameters getRequestParameters() {
		return requestParameters;
	}
	
	public void setTimeSeriesUniqueId(String val) {
		timeSeriesUniqueId = val;
	}

	public void setTimeSeriesParams(String val) {
		timeSeriesParams = val;
	}

	public void setRequestingUser(String val) {
		requestingUser = val;
	}
	
	public void setRequestParameters(SensorReadingSummaryRequestParameters val) {
		requestParameters = val;
		//Report Period displayed should be exactly as recieved, so get as UTC
		setStartDate(val.getStartInstant(ZoneOffset.UTC));
		setEndDate(val.getEndInstant(ZoneOffset.UTC));
		setTimeSeriesUniqueId(val.getPrimaryTimeseriesIdentifier());
	}
	
	public void setTimeseriesLabel(String timeseriesLabel) {
		this.timeseriesLabel = timeseriesLabel;
	}
}