/*
 */
package gov.usgs.aqcu.calc.sensorreadingsummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.usgs.aqcu.model.Readings;
import gov.usgs.aqcu.model.SensorReadingSummaryReading;
/**
 * Service class that groups readings by a specified parameter and then combines
 * those that have duplicates in another field.
 * 
 * @author kmschoep
 */
public class ReadingsTimeCombiner {
		
	public List<SensorReadingSummaryReading> combine(List<Readings> readings, List<String> combineByFields) {
		
		Map<String, List<SensorReadingSummaryReading>> referenceReadingsByKeyField = new HashMap<>() ;
		Map<String, List<SensorReadingSummaryReading>> recorderReadingsByKeyField = new HashMap<>() ;
		
		List<SensorReadingSummaryReading> toRet = new ArrayList<>();
		
		//first separate readings into reference readings or recorder readings
		for(Readings row : readings) {
			// "type", "time", "visitTime"
			//Display time
			//String rowKey = (String) (row.get(keyField) != null? row.get(keyField):row.get(backupField));
			String rowKey = (String) (row.getTime().toString() != null? row.getTime().toString():row.getVisitTime().toString());
			
			//Look in map matcher for entry.
			SensorReadingSummaryReading tableEntry = new SensorReadingSummaryReading();

			//always add information which is not type specific
			addUniversalFields(row, tableEntry);
			
			if(combineByFields.contains(row.getType())){
				addReferenceFields(row, tableEntry);
				List<SensorReadingSummaryReading> refReadingsList = referenceReadingsByKeyField.get(rowKey);
				if(refReadingsList == null){
					refReadingsList = new ArrayList<>();
					referenceReadingsByKeyField.put(rowKey, refReadingsList);
				}
				refReadingsList.add(tableEntry);
			} else { //recorder reading
				addRecorderFields(row, tableEntry);
				List<SensorReadingSummaryReading> recReadingsList = recorderReadingsByKeyField.get(rowKey);
				if(recReadingsList == null){
					recReadingsList = new ArrayList<>();
					recorderReadingsByKeyField.put(rowKey, recReadingsList);
				}
				recReadingsList.add(tableEntry);
			}
		}
		
		//for every reference reading, append recorder information from the same time, there may be multiple recorder readings and reference readings
		//at the same time and this loop amounts to a cartesian join. Potential place for optimization
		for(String rowKey : referenceReadingsByKeyField.keySet()) {
			List<SensorReadingSummaryReading> referenceReadings = referenceReadingsByKeyField.get(rowKey);
			List<SensorReadingSummaryReading> recorderReadings = recorderReadingsByKeyField.get(rowKey);
			
			for(SensorReadingSummaryReading referenceReading: referenceReadings) {
				if(recorderReadings == null || recorderReadings.size() == 0) { //no recorder readings here, just add the reference reading as a row
					toRet.add(referenceReading);
				} else {
					for(SensorReadingSummaryReading recorderReading: recorderReadings) {
						SensorReadingSummaryReading newCombinedRow = new SensorReadingSummaryReading();
						setUniversalFields(recorderReading,newCombinedRow);
						setReferenceFields(referenceReading,newCombinedRow);
						setRecorderFields(recorderReading,newCombinedRow);
						toRet.add(newCombinedRow);
					}
				}
			}
		}
		
		//add recorder readings that have no associated reference reading
		for(String rowKey : recorderReadingsByKeyField.keySet()) {
			List<SensorReadingSummaryReading> referenceReadings = referenceReadingsByKeyField.get(rowKey);
			List<SensorReadingSummaryReading> recorderReadings = recorderReadingsByKeyField.get(rowKey);
			
			if(referenceReadings == null || referenceReadings.size() == 0) {
				for(SensorReadingSummaryReading recorderReading : recorderReadings) {
					toRet.add(recorderReading);
				}
			}
		}

		toRet.sort((SensorReadingSummaryReading r1, SensorReadingSummaryReading r2)->r1.getDisplayTime().toString().compareTo(r2.getDisplayTime().toString()));
		//Collections.sort(toRet, new JsonMapComparator(TimeCombinerEnum.DISPLAYTIME.toString()));
		return  toRet;
	}
	
	private void addUniversalFields(Readings incomingData, SensorReadingSummaryReading tableEntry) {
		tableEntry.setDisplayTime(incomingData.getTime() != null?
				incomingData.getTime() : incomingData.getVisitTime());
		tableEntry.setParty(incomingData.getParty());
		tableEntry.setSublocation(incomingData.getSublocation());
		tableEntry.setVisitStatus(incomingData.getVisitStatus());
	}
	
	private void setUniversalFields(SensorReadingSummaryReading incomingData, SensorReadingSummaryReading tableEntry) {
		tableEntry.setDisplayTime(incomingData.getDisplayTime());
		tableEntry.setParty(incomingData.getParty());
		tableEntry.setSublocation(incomingData.getSublocation());
		tableEntry.setVisitStatus(incomingData.getVisitStatus());
	}
	
	private void addRecorderFields(Readings incomingData, SensorReadingSummaryReading tableEntry) {
		tableEntry.setRecorderMethod(incomingData.getMonitoringMethod());
		tableEntry.setRecorderType(incomingData.getType());
		tableEntry.setRecorderValue(incomingData.getValue());
		tableEntry.setRecorderUncertainty(incomingData.getUncertainty());
		tableEntry.setRecorderComments(incomingData.getComments());
	}
	
	private void setRecorderFields(SensorReadingSummaryReading incomingData, SensorReadingSummaryReading tableEntry) {
		tableEntry.setRecorderMethod(incomingData.getRecorderMethod());
		tableEntry.setRecorderType(incomingData.getRecorderType());
		tableEntry.setRecorderValue(incomingData.getRecorderValue());
		tableEntry.setRecorderUncertainty(incomingData.getRecorderUncertainty());
		tableEntry.setRecorderComments(incomingData.getRecorderComments());
	}
	
	private void addReferenceFields(Readings incomingData, SensorReadingSummaryReading tableEntry) {
		tableEntry.setMonitoringMethod(incomingData.getMonitoringMethod());
		tableEntry.setType(incomingData.getType());
		tableEntry.setValue(incomingData.getValue());
		tableEntry.setUncertainty(incomingData.getUncertainty());
		tableEntry.setReferenceComments(incomingData.getComments());
	}
	
	private void setReferenceFields(SensorReadingSummaryReading incomingData, SensorReadingSummaryReading tableEntry) {
		tableEntry.setMonitoringMethod(incomingData.getMonitoringMethod());
		tableEntry.setType(incomingData.getType());
		tableEntry.setValue(incomingData.getValue());
		tableEntry.setUncertainty(incomingData.getUncertainty());
		tableEntry.setReferenceComments(incomingData.getReferenceComments());
	}

}
