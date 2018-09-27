package gov.usgs.aqcu.model;

import java.math.BigDecimal;
import java.time.Instant;

public class SensorReadingSummaryPoint {
	private Instant time;

	private BigDecimal value;

	public Instant getTime() {
		return time;
	}

	public SensorReadingSummaryPoint setTime(Instant time) {
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
