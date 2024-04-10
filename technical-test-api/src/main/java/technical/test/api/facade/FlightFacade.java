package technical.test.api.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.api.mapper.AirportMapper;
import technical.test.api.mapper.FlightMapper;
import technical.test.api.record.AirportRecord;
import technical.test.api.record.FlightRecord;
import technical.test.api.representation.FlightRepresentation;
import technical.test.api.services.AirportService;
import technical.test.api.services.FlightService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FlightFacade {
    private final FlightService flightService;
    private final AirportService airportService;
    private final FlightMapper flightMapper;
    private final AirportMapper airportMapper;

    public Flux<FlightRepresentation> getAllFlights() {
        return flightService.getAllFlights()
                .flatMap(flightRecord -> airportService.findByIataCode(flightRecord.getOrigin())
                        .zipWith(airportService.findByIataCode(flightRecord.getDestination()))
                        .flatMap(tuple -> {
                            AirportRecord origin = tuple.getT1();
                            AirportRecord destination = tuple.getT2();
                            FlightRepresentation flightRepresentation = this.flightMapper.convert(flightRecord);
                            flightRepresentation.setOrigin(this.airportMapper.convert(origin));
                            flightRepresentation.setDestination(this.airportMapper.convert(destination));
                            return Mono.just(flightRepresentation);
                        }));
    }

    public Mono<FlightRepresentation> findFlightById(UUID id) {
        return flightService.getFlightByID(id)
                .flatMap(flightRecord ->
                        airportService.findByIataCode(flightRecord.getOrigin())
                                .zipWith(airportService.findByIataCode(flightRecord.getDestination()))
                                .flatMap(tuple -> {
                                    AirportRecord origin = tuple.getT1();
                                    AirportRecord destination = tuple.getT2();
                                    FlightRepresentation flightRepresentation = this.flightMapper.convert(flightRecord);
                                    flightRepresentation.setOrigin(this.airportMapper.convert(origin));
                                    flightRepresentation.setDestination(this.airportMapper.convert(destination));
                                    return Mono.just(flightRepresentation);
                                })
                );
    }

    public Mono<FlightRepresentation> createFlight(FlightRepresentation flightRepresentation) {

        if (flightRepresentation == null) return Mono.empty();

        FlightRecord flightRec = this.flightMapper.convert(flightRepresentation);
        flightRec.setOrigin(flightRepresentation.getOrigin().getIata());
        flightRec.setDestination(flightRepresentation.getDestination().getIata());

        return flightService.save(flightRec).flatMap(flightRecord ->
                airportService.findByIataCode(flightRecord.getOrigin())
                        .zipWith(airportService.findByIataCode(flightRecord.getDestination()))
                        .flatMap(tuple -> {
                            AirportRecord origin = tuple.getT1();
                            AirportRecord destination = tuple.getT2();
                            FlightRepresentation representation = this.flightMapper.convert(flightRecord);
                            representation.setOrigin(this.airportMapper.convert(origin));
                            representation.setDestination(this.airportMapper.convert(destination));
                            return Mono.just(representation);
                        })
        );
    }
}
