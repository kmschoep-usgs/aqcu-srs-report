/*
 */
package gov.usgs.aqcu.calc.sensorreadingsummary;

/**
 *
 * @author dpattermann
 */
public enum TimeCombinerEnum {
	METHOD("monitoringMethod"),
	TYPE("type"),
	VALUE("value"),
	TIME("time"),
	UNCERTAINTY("uncertainty"),
	VISITTIME("visitTime"),
	DISPLAYTIME("displayTime"),
	PARTY("party"),
	SUBLOCATION("sublocation"),
	VISITSTATUS("visitStatus"),
	RECORDERMETHOD("recorderMethod"),
	RECORDERTYPE("recorderType"),
	RECORDERVALUE("recorderValue"),
	RECORDERUNCERTAINTY("recorderUncertainty"),
	COMMENTS("comments"),
	QUALIFIERS("qualifiers");

	private String code;
	private TimeCombinerEnum(String code){
		this.code = code;
	}
	
	@Override
	public String toString(){
		return this.code;
	}
}
