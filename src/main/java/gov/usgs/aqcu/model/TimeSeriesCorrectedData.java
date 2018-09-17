package gov.usgs.aqcu.model;

import java.time.temporal.Temporal;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Approval;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GapTolerance;

public class TimeSeriesCorrectedData {

	//Required Properties
	private List<Approval> approvals; //not used according to doc, but required per R (level/approvalLevel, description/levelDescription, {dateApplied/dateAppliedUtc}, startTime, endTime)
	private Temporal endTime;
	private List<DataGap> gaps; //not used according to doc, but required per R
	private List<GapTolerance> gapTolerances; //not used according to doc, but required per R
	private List<String> grades; //not used according to doc, but required per R
	private boolean isVolumetricFlow;
	private String name; //not used according to doc, but required per R
	private List<SensorReadingSummaryPoint> points;
	private List<String> qualifiers; //not used according to doc, but required per R (startTime, endTime, identifier)
	private Temporal startTime;
	private String type;
	private String unit;

	//Optional Properties
	private List<InstantRange> estimatedPeriods;

	public List<Approval> getApprovals() {
		return approvals;
	}
	public void setApprovals(List<Approval> approvals) {
		this.approvals = approvals;
	}
	public Temporal getEndTime() {
		return endTime;
	}
	public void setEndTime(Temporal endTime) {
		this.endTime = endTime;
	}
	public List<DataGap> getGaps() {
		return gaps;
	}
	public void setGaps(List<DataGap> gaps) {
		this.gaps = gaps;
	}
	public List<GapTolerance> getGapTolerances() {
		return gapTolerances;
	}
	public void setGapTolerances(List<GapTolerance> gapTolerances) {
		this.gapTolerances = gapTolerances;
	}
	public List<String> getGrades() {
		return grades;
	}
	public void setGrades(List<String> grades) {
		this.grades = grades;
	}
	public boolean isVolumetricFlow() {
		return isVolumetricFlow;
	}
	public void setVolumetricFlow(boolean isVolumetricFlow) {
		this.isVolumetricFlow = isVolumetricFlow;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SensorReadingSummaryPoint> getPoints() {
		return points;
	}
	public void setPoints(List<SensorReadingSummaryPoint> points) {
		this.points = points;
	}
	public List<String> getQualifiers() {
		return qualifiers;
	}
	public void setQualifiers(List<String> qualifiers) {
		this.qualifiers = qualifiers;
	}
	public Temporal getStartTime() {
		return startTime;
	}
	public void setStartTime(Temporal startTime) {
		this.startTime = startTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public List<InstantRange> getEstimatedPeriods() {
		return estimatedPeriods;
	}
	public void setEstimatedPeriods(List<InstantRange> estimatedPeriods) {
		this.estimatedPeriods = estimatedPeriods;
	}
}
