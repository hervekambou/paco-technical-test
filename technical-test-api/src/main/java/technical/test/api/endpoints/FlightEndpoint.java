package technical.test.api.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.api.facade.FlightFacade;
import technical.test.api.representation.FlightRepresentation;

import java.util.UUID;

@RestController
@RequestMapping("/flight")
@RequiredArgsConstructor
public class FlightEndpoint {
    private final FlightFacade flightFacade;

    @GetMapping
    public Flux<FlightRepresentation> getAllFlights() {
        return flightFacade.getAllFlights();
    }

    @GetMapping("/{id}")
    public Mono<FlightRepresentation> getFlightById(@PathVariable UUID id) {
        return flightFacade.findFlightById(id);
    }

    @PostMapping(value = "/create", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FlightRepresentation> createFlight(@RequestBody FlightRepresentation flightRepresentation) {
        return flightFacade.createFlight(flightRepresentation);
    }

    @GetMapping("/page")
    public Mono<Page<FlightRepresentation>> getAllFlightsByPage(Pageable pageable) {
        return flightFacade.getAllFlightsByPage(pageable);
    }

}
