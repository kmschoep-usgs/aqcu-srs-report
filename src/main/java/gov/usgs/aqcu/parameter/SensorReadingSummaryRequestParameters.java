package gov.usgs.aqcu.parameter;

public class SensorReadingSummaryRequestParameters extends ReportRequestParameters {

	private boolean excludeComments;

	public boolean getExcludeComments() {		
		return excludeComments;
	}

	public void setExcludedCorrections(boolean val) {
		this.excludeComments = val;
	}

}
