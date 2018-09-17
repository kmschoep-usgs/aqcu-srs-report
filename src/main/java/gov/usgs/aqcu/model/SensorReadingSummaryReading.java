/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.aqcu.model;

import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import java.time.temporal.Temporal;

/** 
 * Summarized AQCU version of a readings report.
 *
 * @author kmschoep
 */
public class SensorReadingSummaryReading {
	private Temporal displayTime;
	private List<String> recorderComments;
	private String visitStatus;
	private String recorderMethod;
	private String recorderValue;
	private String recorderType;
	private Temporal nearestCorrectedTime;
	private List<QualifierMetadata> qualifiers;
	private String nearestCorrectedValue;
	private Temporal nearestRawTime;
	private String nearestRawValue;
	private List<String> referenceComments;
	private String parameter;
	private String uncertainty;
	private String sublocation;
	private String value;
	private String type;
	private String party;
	private String monitoringMethod;
	
	
	public Temporal getDisplayTime() {
		return displayTime;
	}
	public List<String> getRecorderComments() {
		return recorderComments;
	}
	public String getVisitStatus() {
		return visitStatus;
	}
	public String getRecorderMethod() {
		return recorderMethod;
	}
	public String getRecorderValue() {
		return recorderValue;
	}
	public String getRecorderType() {
		return recorderType;
	}
	public Temporal getNearestCorrectedTime() {
		return nearestCorrectedTime;
	}
	public List<QualifierMetadata> getQualifiers() {
		return qualifiers;
	}
	public String getNearestCorrectedValue() {
		return nearestCorrectedValue;
	}
	public Temporal getNearestRawTime() {
		return nearestRawTime;
	}
	public String getNearestRawValue() {
		return nearestRawValue;
	}
	public List<String> getReferenceComments() {
		return referenceComments;
	}
	public String getParameter() {
		return parameter;
	}
	public String getUncertainty() {
		return uncertainty;
	}
	public String getSublocation() {
		return sublocation;
	}
	public String getValue() {
		return value;
	}
	public String getType() {
		return type;
	}
	public String getParty() {
		return party;
	}
	public String getMonitoringMethod() {
		return monitoringMethod;
	}
	public void setDisplayTime(Temporal displayTime) {
		this.displayTime = displayTime;
	}
	public void setRecorderComments(List<String> recorderComments) {
		this.recorderComments = recorderComments;
	}
	public void setVisitStatus(String visitStatus) {
		this.visitStatus = visitStatus;
	}
	public void setRecorderMethod(String recorderMethod) {
		this.recorderMethod = recorderMethod;
	}
	public void setRecorderValue(String recorderValue) {
		this.recorderValue = recorderValue;
	}
	public void setRecorderType(String recorderType) {
		this.recorderType = recorderType;
	}
	public void setNearestCorrectedTime(Temporal nearestCorrectedTime) {
		this.nearestCorrectedTime = nearestCorrectedTime;
	}
	public void setQualifiers(List<QualifierMetadata> qualifiers) {
		this.qualifiers = qualifiers;
	}
	public void setNearestCorrectedValue(String nearestCorrectedValue) {
		this.nearestCorrectedValue = nearestCorrectedValue;
	}
	public void setNearestRawTime(Temporal nearestRawTime) {
		this.nearestRawTime = nearestRawTime;
	}
	public void setNearestRawValue(String nearestRawValue) {
		this.nearestRawValue = nearestRawValue;
	}
	public void setReferenceComments(List<String> referenceComments) {
		this.referenceComments = referenceComments;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public void setUncertainty(String uncertainty) {
		this.uncertainty = uncertainty;
	}
	public void setSublocation(String sublocation) {
		this.sublocation = sublocation;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setParty(String party) {
		this.party = party;
	}
	public void setMonitoringMethod(String monitoringMethod) {
		this.monitoringMethod = monitoringMethod;
	}
	
}
	
