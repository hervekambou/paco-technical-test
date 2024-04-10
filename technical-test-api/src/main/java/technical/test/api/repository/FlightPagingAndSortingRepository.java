package technical.test.api.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import technical.test.api.record.FlightRecord;

import java.util.UUID;

@Repository
public interface FlightPagingAndSortingRepository extends ReactiveSortingRepository<FlightRecord, UUID> {
    Flux<FlightRecord> findAllBy(Pageable pageable);
}
