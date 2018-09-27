package gov.usgs.aqcu.retrieval;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataRawServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

@Repository
public class TimeSeriesDataRawService {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDataRawService.class);

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public TimeSeriesDataRawService(
		AquariusRetrievalService aquariusRetrievalService
	) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public TimeSeriesDataServiceResponse get(String primaryTimeseriesIdentifier, Instant startDate, Instant endDate) {
		try {
			TimeSeriesDataRawServiceRequest request = new TimeSeriesDataRawServiceRequest()
					.setTimeSeriesUniqueId(primaryTimeseriesIdentifier)
					.setQueryFrom(startDate)
					.setQueryTo(endDate);
			TimeSeriesDataServiceResponse timeSeriesResponse = aquariusRetrievalService.executePublishApiRequest(request);
			return timeSeriesResponse;
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch TimeSeriesDataRawServiceRequest from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}
}
