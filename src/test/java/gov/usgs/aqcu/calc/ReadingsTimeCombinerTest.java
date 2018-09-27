package gov.usgs.aqcu.calc;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import gov.usgs.aqcu.calc.ReadingsTimeCombiner;
import gov.usgs.aqcu.model.Readings;
import gov.usgs.aqcu.model.SensorReadingSummaryReading;

public class ReadingsTimeCombinerTest {

	Readings recReading1 = new Readings("2DC362BC6BF255AEE0530100007F524B",
			"TODO",
			"SHB",
			"Routine",
			null,
			"Gage height",
			"Non-subm pressure  transducer",
			Instant.parse("2013-10-28T09:53:00Z"),
			"4.17",
			null,
			Instant.parse("2013-10-28T09:32:00Z"),
			Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false")
			);			
	
	Readings recReading2 = new Readings(
			"2DC362BC6BF255AEE0530100007F524B",
			"TODO",
			"SHB",
			"Routine",
			null,
			"Gage height",
			"Non-subm pressure  transducer",
			Instant.parse("2013-10-28T11:02:00Z"),
			"4.17",
			null,
			Instant.parse("2013-10-28T09:32:00Z"),
			Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false")
			);
	
	Readings refReading1 = new Readings(
			"2DC362BC6BF255AEE0530100007F524B",
			"TODO",
			"SHB",
			"ReferencePrimary",
			null,
			"Gage height",
			"Reference Point",
			Instant.parse("2013-10-28T09:53:00Z"),
			"4.15",
			"0.01",
			Instant.parse("2013-10-28T09:32:00Z"),
			Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15")
			);
	
	Readings refReading2 = new Readings(
			"2DC362BC6BF255AEE0530100007F524B",
			"TODO",
			"SHB",
			"ReferencePrimary",
			null,
			"Gage height",
			"Reference Point",
			Instant.parse("2013-10-28T11:02:00Z"),
			"4.15",
			"0.01",
			Instant.parse("2013-10-28T09:32:00Z"),
			Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15")
			);
	
	Readings sameTimeRefReading2 = new Readings(
			"duplicateTimeId",
			"TODO",
			"PT",
			"ReferencePrimary",
			null,
			"Gage height",
			"Reference Point",
			Instant.parse("2013-10-28T11:02:00Z"),
			"4.55",
			"0.5",
			Instant.parse("2013-10-28T09:32:00Z"),
			Arrays.asList("TEST COMMENT")
			);
	
	Readings sameTimeRecReading2 = new Readings(
			"testRecorderId",
			"TODO",
			"PT",
			"Routine",
			null,
			"Gage height",
			"TEST RECORDER METHOD",
			Instant.parse("2013-10-28T11:02:00Z"),
			"4.00",
			null,
			Instant.parse("2013-10-28T09:32:00Z"),
			Arrays.asList("TEST RECORDER COMMENT")
			);
	
	@Test
	public void testCombine_no_recorder_readings() {
		ImmutableList<Readings> testRefsAndRecorders = ImmutableList.<Readings>builder()
			.add(refReading1)
			.add(refReading2)
			.build();
		
		List<SensorReadingSummaryReading> combinedResults = new ReadingsTimeCombiner().combine(
				(List<Readings>) testRefsAndRecorders, 
				Arrays.asList(new String[] {"Reference", "ReferencePrimary", "REFERENCE_PRIMARY"}));
		
		assertEquals(2, combinedResults.size());
		assertEquals(Instant.parse("2013-10-28T09:53:00Z"), combinedResults.get(0).getDisplayTime());
		assertEquals(null, combinedResults.get(0).getRecorderComments());
		assertEquals("Reference Point", combinedResults.get(0).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResults.get(0).getType());
		assertEquals("0.01", combinedResults.get(0).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResults.get(0).getReferenceComments());
		assertEquals("TODO", combinedResults.get(0).getVisitStatus());
		assertEquals(null, combinedResults.get(0).getRecorderMethod());
		assertEquals("4.15", combinedResults.get(0).getValue());
		assertEquals(null, combinedResults.get(0).getRecorderValue());
		assertEquals("SHB", combinedResults.get(0).getParty());
		assertEquals(null, combinedResults.get(0).getRecorderType());
		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResults.get(1).getDisplayTime());
		assertEquals(null, combinedResults.get(1).getRecorderComments());
		assertEquals("Reference Point", combinedResults.get(1).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResults.get(1).getType());
		assertEquals("0.01", combinedResults.get(1).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResults.get(1).getReferenceComments());
		assertEquals("TODO", combinedResults.get(1).getVisitStatus());
		assertEquals(null, combinedResults.get(1).getRecorderMethod());
		assertEquals("4.15", combinedResults.get(1).getValue());
		assertEquals(null, combinedResults.get(1).getRecorderValue());
		assertEquals("SHB", combinedResults.get(1).getParty());
		assertEquals(null, combinedResults.get(1).getRecorderType());
	}
	
	@Test
	public void testCombine_no_reference_readings() {
		ImmutableList<Readings> testRefsAndRecorders = ImmutableList.<Readings>builder()
			.add(recReading1)
			.add(recReading2)
			.build();
		
		List<SensorReadingSummaryReading> combinedResults = new ReadingsTimeCombiner().combine(
				(List<Readings>) testRefsAndRecorders, 
				Arrays.asList(new String[] {"Reference", "ReferencePrimary", "REFERENCE_PRIMARY"}));
		
		assertEquals(2, combinedResults.size());
		assertEquals(Instant.parse("2013-10-28T09:53:00Z"), combinedResults.get(0).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResults.get(0).getRecorderComments());
		assertEquals(null, combinedResults.get(0).getMonitoringMethod());
		assertEquals(null, combinedResults.get(0).getType());
		assertEquals(null, combinedResults.get(0).getUncertainty());
		assertEquals(null, combinedResults.get(0).getReferenceComments());
		assertEquals("TODO", combinedResults.get(0).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResults.get(0).getRecorderMethod());
		assertEquals(null, combinedResults.get(0).getValue());
		assertEquals("4.17", combinedResults.get(0).getRecorderValue());
		assertEquals("SHB", combinedResults.get(0).getParty());
		assertEquals("Routine", combinedResults.get(0).getRecorderType());
		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResults.get(1).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResults.get(1).getRecorderComments());
		assertEquals(null, combinedResults.get(1).getMonitoringMethod());
		assertEquals(null, combinedResults.get(1).getType());
		assertEquals(null, combinedResults.get(1).getUncertainty());
		assertEquals(null, combinedResults.get(1).getReferenceComments());
		assertEquals("TODO", combinedResults.get(1).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResults.get(1).getRecorderMethod());
		assertEquals(null, combinedResults.get(1).getValue());
		assertEquals("4.17", combinedResults.get(1).getRecorderValue());
		assertEquals("SHB", combinedResults.get(1).getParty());
		assertEquals("Routine", combinedResults.get(1).getRecorderType());
	}

	@Test
	public void testCombine_1_recorder_per_reference() {
		ImmutableList<Readings> testRefsAndRecorders = ImmutableList.<Readings>builder()
			.add(recReading1)
			.add(recReading2)
			.add(refReading1)
			.add(refReading2)
			.build();
		
		List<SensorReadingSummaryReading> combinedResults = new ReadingsTimeCombiner().combine(
				(List<Readings>) testRefsAndRecorders,  
				Arrays.asList(new String[] {"Reference", "ReferencePrimary", "REFERENCE_PRIMARY"}));
		
		assertEquals(2, combinedResults.size());
		assertEquals(Instant.parse("2013-10-28T09:53:00Z"), combinedResults.get(0).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResults.get(0).getRecorderComments());
		assertEquals("Reference Point", combinedResults.get(0).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResults.get(0).getType());
		assertEquals("0.01", combinedResults.get(0).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResults.get(0).getReferenceComments());
		assertEquals("TODO", combinedResults.get(0).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResults.get(0).getRecorderMethod());
		assertEquals("4.15", combinedResults.get(0).getValue());
		assertEquals("4.17", combinedResults.get(0).getRecorderValue());
		assertEquals("SHB", combinedResults.get(0).getParty());
		assertEquals("Routine", combinedResults.get(0).getRecorderType());
		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResults.get(1).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResults.get(1).getRecorderComments());
		assertEquals("Reference Point", combinedResults.get(1).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResults.get(1).getType());
		assertEquals("0.01", combinedResults.get(1).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResults.get(1).getReferenceComments());
		assertEquals("TODO", combinedResults.get(1).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResults.get(1).getRecorderMethod());
		assertEquals("4.15", combinedResults.get(1).getValue());
		assertEquals("4.17", combinedResults.get(1).getRecorderValue());
		assertEquals("SHB", combinedResults.get(1).getParty());
		assertEquals("Routine", combinedResults.get(1).getRecorderType());
	}
	
	@Test
	public void testCombine_2_reference_readings_for_1_recorder() {
		ImmutableList<Readings> testRefsAndRecordersWithDupe = ImmutableList.<Readings>builder()
			.add(recReading1)
			.add(recReading2)
			.add(refReading1)
			.add(refReading2)
			.add(sameTimeRefReading2)
			.build();
		
		List<SensorReadingSummaryReading> combinedResultsWithDupe = new ReadingsTimeCombiner().combine(
				(List<Readings>) testRefsAndRecordersWithDupe, 
				Arrays.asList(new String[] {"Reference", "ReferencePrimary", "REFERENCE_PRIMARY"}));
		
		assertEquals(3, combinedResultsWithDupe.size()); //2 rows for the reference readings at the same time, the recorder information is duplicated for each
		assertEquals(Instant.parse("2013-10-28T09:53:00Z"), combinedResultsWithDupe.get(0).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResultsWithDupe.get(0).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(0).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(0).getType());
		assertEquals("0.01", combinedResultsWithDupe.get(0).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResultsWithDupe.get(0).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(0).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResultsWithDupe.get(0).getRecorderMethod());
		assertEquals("4.15", combinedResultsWithDupe.get(0).getValue());
		assertEquals("4.17", combinedResultsWithDupe.get(0).getRecorderValue());
		assertEquals("SHB", combinedResultsWithDupe.get(0).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(0).getRecorderType());
		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResultsWithDupe.get(1).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResultsWithDupe.get(1).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(1).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(1).getType());
		assertEquals("0.01", combinedResultsWithDupe.get(1).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResultsWithDupe.get(1).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(1).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResultsWithDupe.get(1).getRecorderMethod());
		assertEquals("4.15", combinedResultsWithDupe.get(1).getValue());
		assertEquals("4.17", combinedResultsWithDupe.get(1).getRecorderValue());
		assertEquals("SHB", combinedResultsWithDupe.get(1).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(1).getRecorderType());
		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResultsWithDupe.get(2).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResultsWithDupe.get(2).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(2).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(2).getType());
		assertEquals("0.5", combinedResultsWithDupe.get(2).getUncertainty());
		assertEquals(Arrays.asList("TEST COMMENT"), combinedResultsWithDupe.get(2).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(2).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResultsWithDupe.get(2).getRecorderMethod());
		assertEquals("4.55", combinedResultsWithDupe.get(2).getValue());
		assertEquals("4.17", combinedResultsWithDupe.get(2).getRecorderValue());
		assertEquals("SHB", combinedResultsWithDupe.get(2).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(2).getRecorderType());
	}
	
	@Test
	public void testCombine_2_recorder_readings_for_1_reference() {
		ImmutableList<Readings> testRefsAndRecordersWithDupe = ImmutableList.<Readings>builder()
			.add(recReading1)
			.add(recReading2)
			.add(refReading1)
			.add(refReading2)
			.add(sameTimeRecReading2)
			.build();
		
		List<SensorReadingSummaryReading> combinedResultsWithDupe = new ReadingsTimeCombiner().combine(
				(List<Readings>) testRefsAndRecordersWithDupe, 
				Arrays.asList(new String[] {"Reference", "ReferencePrimary", "REFERENCE_PRIMARY"}));
		
		assertEquals(3, combinedResultsWithDupe.size()); //2 rows for the recorder readings at the same time, the reference information is duplicated for each
		assertEquals(Instant.parse("2013-10-28T09:53:00Z"), combinedResultsWithDupe.get(0).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResultsWithDupe.get(0).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(0).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(0).getType());
		assertEquals("0.01", combinedResultsWithDupe.get(0).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResultsWithDupe.get(0).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(0).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResultsWithDupe.get(0).getRecorderMethod());
		assertEquals("4.15", combinedResultsWithDupe.get(0).getValue());
		assertEquals("4.17", combinedResultsWithDupe.get(0).getRecorderValue());
		assertEquals("SHB", combinedResultsWithDupe.get(0).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(0).getRecorderType());
		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResultsWithDupe.get(1).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResultsWithDupe.get(1).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(1).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(1).getType());
		assertEquals("0.01", combinedResultsWithDupe.get(1).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResultsWithDupe.get(1).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(1).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResultsWithDupe.get(1).getRecorderMethod());
		assertEquals("4.15", combinedResultsWithDupe.get(1).getValue());
		assertEquals("4.17", combinedResultsWithDupe.get(1).getRecorderValue());
		assertEquals("SHB", combinedResultsWithDupe.get(1).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(1).getRecorderType());
		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResultsWithDupe.get(2).getDisplayTime());
		assertEquals(Arrays.asList("TEST RECORDER COMMENT"), combinedResultsWithDupe.get(2).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(2).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(2).getType());
		assertEquals("0.01", combinedResultsWithDupe.get(2).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResultsWithDupe.get(2).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(2).getVisitStatus());
		assertEquals("TEST RECORDER METHOD", combinedResultsWithDupe.get(2).getRecorderMethod());
		assertEquals("4.15", combinedResultsWithDupe.get(2).getValue());
		assertEquals("4.00", combinedResultsWithDupe.get(2).getRecorderValue());
		assertEquals("PT", combinedResultsWithDupe.get(2).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(2).getRecorderType());
	}
	

	@Test
	public void testCombine_2_recorder_readings_for_2_reference_readings() {
		ImmutableList<Readings> testRefsAndRecordersWithDupe = ImmutableList.<Readings>builder()
			.add(recReading2)
			.add(sameTimeRecReading2)
			.add(refReading2)
			.add(sameTimeRefReading2)
			.build();
		
		List<SensorReadingSummaryReading> combinedResultsWithDupe = new ReadingsTimeCombiner().combine(
				(List<Readings>) testRefsAndRecordersWithDupe, 
				Arrays.asList(new String[] {"Reference", "ReferencePrimary", "REFERENCE_PRIMARY"}));
		
		assertEquals(4, combinedResultsWithDupe.size()); //4 rows, one for each ref + rec permutation
		

		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResultsWithDupe.get(0).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResultsWithDupe.get(0).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(0).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(0).getType());
		assertEquals("0.01", combinedResultsWithDupe.get(0).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResultsWithDupe.get(0).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(0).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResultsWithDupe.get(0).getRecorderMethod());
		assertEquals("4.15", combinedResultsWithDupe.get(0).getValue());
		assertEquals("4.17", combinedResultsWithDupe.get(0).getRecorderValue());
		assertEquals("SHB", combinedResultsWithDupe.get(0).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(0).getRecorderType());
		
		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResultsWithDupe.get(1).getDisplayTime());
		assertEquals(Arrays.asList("TEST RECORDER COMMENT"), combinedResultsWithDupe.get(1).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(1).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(1).getType());
		assertEquals("0.01", combinedResultsWithDupe.get(1).getUncertainty());
		assertEquals(Arrays.asList("Comment : AF RP1 4.04 + 0.11 WS 4.15 // AL + 0.11 WS 4.15"), combinedResultsWithDupe.get(1).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(1).getVisitStatus());
		assertEquals("TEST RECORDER METHOD", combinedResultsWithDupe.get(1).getRecorderMethod());
		assertEquals("4.15", combinedResultsWithDupe.get(1).getValue());
		assertEquals("4.00", combinedResultsWithDupe.get(1).getRecorderValue());
		assertEquals("PT", combinedResultsWithDupe.get(1).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(1).getRecorderType());

		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResultsWithDupe.get(2).getDisplayTime());
		assertEquals(Arrays.asList("OrificeServicedCode: PRGD", "OrificeServicedDateTime: 2013-10-28T10:50:00-04:00 (EDT)", "DesiccantConditionCode: DESG", "DesiccantChangedIndicator: false", "GasSystemTypeCode: BAIR", "GasTankChangedIndicator: false"), combinedResultsWithDupe.get(2).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(2).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(2).getType());
		assertEquals("0.5", combinedResultsWithDupe.get(2).getUncertainty());
		assertEquals(Arrays.asList("TEST COMMENT"), combinedResultsWithDupe.get(2).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(2).getVisitStatus());
		assertEquals("Non-subm pressure  transducer", combinedResultsWithDupe.get(2).getRecorderMethod());
		assertEquals("4.55", combinedResultsWithDupe.get(2).getValue());
		assertEquals("4.17", combinedResultsWithDupe.get(2).getRecorderValue());
		assertEquals("SHB", combinedResultsWithDupe.get(2).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(2).getRecorderType());
		
		assertEquals(Instant.parse("2013-10-28T11:02:00Z"), combinedResultsWithDupe.get(3).getDisplayTime());
		assertEquals(Arrays.asList("TEST RECORDER COMMENT"), combinedResultsWithDupe.get(3).getRecorderComments());
		assertEquals("Reference Point", combinedResultsWithDupe.get(3).getMonitoringMethod());
		assertEquals("ReferencePrimary", combinedResultsWithDupe.get(3).getType());
		assertEquals("0.5", combinedResultsWithDupe.get(3).getUncertainty());
		assertEquals(Arrays.asList("TEST COMMENT"), combinedResultsWithDupe.get(3).getReferenceComments());
		assertEquals("TODO", combinedResultsWithDupe.get(3).getVisitStatus());
		assertEquals("TEST RECORDER METHOD", combinedResultsWithDupe.get(3).getRecorderMethod());
		assertEquals("4.55", combinedResultsWithDupe.get(3).getValue());
		assertEquals("4.00", combinedResultsWithDupe.get(3).getRecorderValue());
		assertEquals("PT", combinedResultsWithDupe.get(3).getParty());
		assertEquals("Routine", combinedResultsWithDupe.get(3).getRecorderType());
	}
}
