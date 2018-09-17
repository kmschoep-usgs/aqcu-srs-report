package gov.usgs.aqcu.parameter;

public class SensorReadingSummaryRequestParameters extends ReportRequestParameters {

	private String excludeComments;

	public SensorReadingSummaryRequestParameters() {
		excludeComments = new String();
	}

	public String getExcludeComments() {		
		return excludeComments;
	}

	public void setExcludedCorrections(String val) {
		this.excludeComments = val;
	}

}
