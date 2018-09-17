/*
 */
package gov.usgs.aqcu.calc.sensorreadingsummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class that groups readings by a specified parameter and then combines
 * those that have duplicates in another field.
 * 
 * @author dpattermann
 */
public class ReadingsTimeCombiner {
	
	private final String RECORDER_COMMENTS_OUT_FIELD = "recorderComments";
	private final String REFERENCE_COMMENTS_OUT_FIELD = "referenceComments";
	
	/**
	 * This method is similar to the fill() method, but differs in that the list 
	 * is sorted by a field and using that field looks for duplicates. The current 
	 * primary purpose of this method is to look for points with the same time field.
	 * @param sourceJson The source JSON to process and remove duplicates from
	 * @param compareField The field to search in for duplicates
	 * @param keyField The field to extract
	 * @param backupField If keyField is not found the field to fall back to
	 * @param combineBy The field to group readings by
	 * @return 
	*/
	public List<Map<String, Object>> combine(List<Map<String, Object>> readings, String compareField, String keyField, String backupField, List<String> combineByFields) {
		
		Map<String, List<Map<String,Object>>> referenceReadingsByKeyField = new HashMap<>() ;
		Map<String, List<Map<String,Object>>> recorderReadingsByKeyField = new HashMap<>() ;
		
		List<Map<String, Object>> toRet = new ArrayList<>();
		
		//first separate readings into reference readings or recorder readings
		for(Map<String, Object> row : readings) {
			
			//Display time
			String rowKey = (String) (row.get(keyField) != null? row.get(keyField):row.get(backupField));
			
			//Look in map matcher for entry.
			Map<String, Object> tableEntry = new HashMap<>();

			//always add information which is not type specific
			addUniversalFields(row, tableEntry);
			
			if(combineByFields.contains(row.get(compareField))){
				addReferenceFields(row, tableEntry);
				List<Map<String, Object>> refReadingsList = referenceReadingsByKeyField.get(rowKey);
				if(refReadingsList == null){
					refReadingsList = new ArrayList<>();
					referenceReadingsByKeyField.put(rowKey, refReadingsList);
				}
				refReadingsList.add(tableEntry);
			} else { //recorder reading
				addRecorderFields(row, tableEntry);
				List<Map<String, Object>> recReadingsList = recorderReadingsByKeyField.get(rowKey);
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
			List<Map<String,Object>> referenceReadings = referenceReadingsByKeyField.get(rowKey);
			List<Map<String,Object>> recorderReadings = recorderReadingsByKeyField.get(rowKey);
			
			for(Map<String,Object> referenceReading: referenceReadings) {
				if(recorderReadings == null || recorderReadings.size() == 0) { //no recorder readings here, just add the reference reading as a row
					toRet.add(referenceReading);
				} else {
					for(Map<String,Object> recorderReading: recorderReadings) {
						Map<String,Object> newCombinedRow = new HashMap<>();
						newCombinedRow.putAll(referenceReading);
						newCombinedRow.putAll(recorderReading);
						toRet.add(newCombinedRow);
					}
				}
			}
		}
		
		//add recorder readings that have no associated reference reading
		for(String rowKey : recorderReadingsByKeyField.keySet()) {
			List<Map<String,Object>> referenceReadings = referenceReadingsByKeyField.get(rowKey);
			List<Map<String,Object>> recorderReadings = recorderReadingsByKeyField.get(rowKey);
			
			if(referenceReadings == null || referenceReadings.size() == 0) {
				for(Map<String, Object > recorderReading : recorderReadings) {
					toRet.add(recorderReading);
				}
			}
		}
		
		Collections.sort(toRet, new JsonMapComparator(TimeCombinerEnum.DISPLAYTIME.toString()));
		return  toRet;
	}
	
	private void addUniversalFields(Map<String, Object> incomingData, Map<String, Object> tableEntry) {
		tableEntry.put(TimeCombinerEnum.DISPLAYTIME.toString(), incomingData.get(TimeCombinerEnum.TIME.toString()) != null?
				incomingData.get(TimeCombinerEnum.TIME.toString()) : incomingData.get(TimeCombinerEnum.VISITTIME.toString()));
		tableEntry.put(TimeCombinerEnum.PARTY.toString(),incomingData.get(TimeCombinerEnum.PARTY.toString()));
		tableEntry.put(TimeCombinerEnum.SUBLOCATION.toString(),incomingData.get(TimeCombinerEnum.SUBLOCATION.toString()));
		tableEntry.put(TimeCombinerEnum.VISITSTATUS.toString(),incomingData.get(TimeCombinerEnum.VISITSTATUS.toString()));
		tableEntry.put(TimeCombinerEnum.QUALIFIERS.toString(),incomingData.get(TimeCombinerEnum.QUALIFIERS.toString()));
	}
	
	private void addRecorderFields(Map<String, Object> incomingData, Map<String, Object> tableEntry) {
		tableEntry.put(TimeCombinerEnum.RECORDERMETHOD.toString(), incomingData.get(TimeCombinerEnum.METHOD.toString()));
		tableEntry.put(TimeCombinerEnum.RECORDERTYPE.toString(), incomingData.get(TimeCombinerEnum.TYPE.toString()));
		tableEntry.put(TimeCombinerEnum.RECORDERVALUE.toString(), incomingData.get(TimeCombinerEnum.VALUE.toString()));
		tableEntry.put(TimeCombinerEnum.RECORDERUNCERTAINTY.toString(), incomingData.get(TimeCombinerEnum.UNCERTAINTY.toString()));
		tableEntry.put(RECORDER_COMMENTS_OUT_FIELD,incomingData.get(TimeCombinerEnum.COMMENTS.toString()));
	}
	
	private void addReferenceFields(Map<String, Object> incomingData, Map<String, Object> tableEntry) {
		tableEntry.put(TimeCombinerEnum.METHOD.toString(), incomingData.get(TimeCombinerEnum.METHOD.toString()));
		tableEntry.put(TimeCombinerEnum.TYPE.toString(), incomingData.get(TimeCombinerEnum.TYPE.toString()));
		tableEntry.put(TimeCombinerEnum.VALUE.toString(), incomingData.get(TimeCombinerEnum.VALUE.toString()));
		tableEntry.put(TimeCombinerEnum.UNCERTAINTY.toString(),incomingData.get(TimeCombinerEnum.UNCERTAINTY.toString()));
		tableEntry.put(REFERENCE_COMMENTS_OUT_FIELD,incomingData.get(TimeCombinerEnum.COMMENTS.toString()));
	}
	
	/**
	 * Creates a comparator for parsed JSON Maps
	 */
	public class JsonMapComparator implements Comparator<Map<String, Object>> {

		private String keyField;

		/**
		 * Creates the comparator based on the key field.
		 * @param inKeyField
		 */
		public JsonMapComparator(String inKeyField) {
			this.keyField = inKeyField;
		}

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			String v1 = (String) o1.get(keyField);
			String v2 = (String) o2.get(keyField);

			if (v1 == null && v2 == null) {
				return 0;
			}

			if (v1 == null && v2 != null) {
				return -1;
			}

			if (v1 != null && v2 == null) {
				return 1;
			}

			Comparator<String> c = Comparator.comparing(String::toString);
			return c.compare(v1, v2);
		}
	}
}
