package gov.usgs.aqcu.model;

import java.math.BigDecimal;
import java.time.temporal.Temporal;

/** 
 * This class is a substitute for com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint
 * DV Hydrograph requires Points that:
 * a) Have an optional time component to the temporal value;
 * b) Have values expressed as BigDecimal, rather than imprecise Double approximations.
 */
public class SensorReadingSummaryPoint {
	private Temporal time;

	private BigDecimal value;

	public Temporal getTime() {
		return time;
	}

	public SensorReadingSummaryPoint setTime(Temporal time) {
		this.time = time;
		return this;
	}

	public BigDecimal getValue() {
		return value;
	}

	public SensorReadingSummaryPoint setValue(BigDecimal value) {
		this.value = value;
		return this;
	}
}
