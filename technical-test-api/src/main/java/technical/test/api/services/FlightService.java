package technical.test.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.api.record.FlightRecord;
import technical.test.api.repository.FlightPagingAndSortingRepository;
import technical.test.api.repository.FlightRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final FlightPagingAndSortingRepository flightPagingAndSortingRepository;

    public Flux<FlightRecord> getAllFlights() {
        return flightRepository.findAll();
    }

    public Mono<FlightRecord> getFlightByID(UUID id) {
        return flightRepository.findById(id);
    }

    public Mono<FlightRecord> save(FlightRecord flightRecord) {
        return flightRepository.save(flightRecord);
    }

    public Flux<FlightRecord> getAllFlights(Pageable pageable) {
        return flightPagingAndSortingRepository.findAllBy(pageable);
    }

}
