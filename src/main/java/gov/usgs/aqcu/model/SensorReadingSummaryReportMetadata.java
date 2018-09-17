package gov.usgs.aqcu.model;

public class SensorReadingSummaryReportMetadata extends ReportMetadata {	

	//Required Properties - Also includes timezone, startDate, endDate, title, stationName, stationId and qualifierMetadata
	private String firstStatDerivedLabel;
	private boolean isInverted;
	private String primarySeriesLabel;

	//Optional Properties
	private String comparisonSeriesLabel;
	private boolean excludeDiscrete;
	private boolean excludeZeroNegative;
	private boolean excludeMinMax;
	private String firstReferenceTimeSeriesLabel;
	private String fourthStatDerivedLabel;
	private String secondReferenceTimeSeriesLabel;
	private String secondStatDerivedLabel;
	private String thirdReferenceTimeSeriesLabel;
	private String thirdStatDerivedLabel;

	public String getPrimarySeriesLabel() {
		return primarySeriesLabel;
	}
	public void setPrimarySeriesLabel(String primarySeriesLabel) {
		this.primarySeriesLabel = primarySeriesLabel;
	}
	public String getFirstStatDerivedLabel() {
		return firstStatDerivedLabel;
	}
	public void setFirstStatDerivedLabel(String firstStatDerivedLabel) {
		this.firstStatDerivedLabel = firstStatDerivedLabel;
	}
	public String getSecondStatDerivedLabel() {
		return secondStatDerivedLabel;
	}
	public void setSecondStatDerivedLabel(String secondStatDerivedLabel) {
		this.secondStatDerivedLabel = secondStatDerivedLabel;
	}
	public String getThirdStatDerivedLabel() {
		return thirdStatDerivedLabel;
	}
	public void setThirdStatDerivedLabel(String thirdStatDerivedLabel) {
		this.thirdStatDerivedLabel = thirdStatDerivedLabel;
	}
	public String getFourthStatDerivedLabel() {
		return fourthStatDerivedLabel;
	}
	public void setFourthStatDerivedLabel(String fourthStatDerivedLabel) {
		this.fourthStatDerivedLabel = fourthStatDerivedLabel;
	}
	public String getFirstReferenceTimeSeriesLabel() {
		return firstReferenceTimeSeriesLabel;
	}
	public void setFirstReferenceTimeSeriesLabel(String firstReferenceTimeSeriesLabel) {
		this.firstReferenceTimeSeriesLabel = firstReferenceTimeSeriesLabel;
	}
	public String getSecondReferenceTimeSeriesLabel() {
		return secondReferenceTimeSeriesLabel;
	}
	public void setSecondReferenceTimeSeriesLabel(String secondReferenceTimeSeriesLabel) {
		this.secondReferenceTimeSeriesLabel = secondReferenceTimeSeriesLabel;
	}
	public String getThirdReferenceTimeSeriesLabel() {
		return thirdReferenceTimeSeriesLabel;
	}
	public void setThirdReferenceTimeSeriesLabel(String thirdReferenceTimeSeriesLabel) {
		this.thirdReferenceTimeSeriesLabel = thirdReferenceTimeSeriesLabel;
	}
	public String getComparisonSeriesLabel() {
		return comparisonSeriesLabel;
	}
	public void setComparisonSeriesLabel(String comparisonSeriesLabel) {
		this.comparisonSeriesLabel = comparisonSeriesLabel;
	}
	public boolean getExcludeZeroNegative() {
		return excludeZeroNegative;
	}
	public void setExcludeZeroNegative(boolean excludeZeroNegative) {
		this.excludeZeroNegative = excludeZeroNegative;
	}
	public boolean getExcludeMinMax() {
		return excludeMinMax;
	}
	public void setExcludeMinMax(boolean excludeMinMax) {
		this.excludeMinMax = excludeMinMax;
	}
	public boolean getExcludeDiscrete() {
		return excludeDiscrete;
	}
	public void setExcludeDiscrete(boolean excludeDiscrete) {
		this.excludeDiscrete = excludeDiscrete;
	}
	public boolean isInverted() {
		return isInverted;
	}
	public void setInverted(boolean isInverted) {
		this.isInverted = isInverted;
	}

}
