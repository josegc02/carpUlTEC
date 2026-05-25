package com.dbp.democarpultec.repository;

import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.RequestPublication;
import com.dbp.democarpultec.model.Review;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.RidePassenger;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.Vehicle;
import com.dbp.democarpultec.model.enums.Status;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

final class RepositoryTestFactory {

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private RepositoryTestFactory() {
    }

    static User user(String prefix) {
        String value = prefix.toLowerCase() + SEQUENCE.incrementAndGet();
        return User.builder()
                .name("Test")
                .lastName("User")
                .email(value + "@utec.edu.pe")
                .build();
    }

    static Vehicle vehicle(User owner, String prefix) {
        String value = prefix.toUpperCase() + SEQUENCE.incrementAndGet();
        return Vehicle.builder()
                .owner(owner)
                .plate(value)
                .brand("Toyota")
                .model("Yaris")
                .color("Blue")
                .seats(4)
                .build();
    }

    static Publication publication(User author) {
        return Publication.builder()
                .author(author)
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(3)
                .titulo("Viaje desde UTEC")
                .descripcion("Salida principal")
                .destinationOrOrigin("Miraflores")
                .departureTime(LocalDateTime.now().plusDays(1))
                .build();
    }

    static RequestPublication request(Publication publication, User requester, Status status) {
        return RequestPublication.builder()
                .publication(publication)
                .requester(requester)
                .requesterIsDriver(false)
                .pickupPointOrDestine("Av. Arequipa")
                .seats(1)
                .message("Tengo interes")
                .status(status)
                .build();
    }

    static Ride ride(Publication publication, User driver, Vehicle vehicle) {
        return Ride.builder()
                .publication(publication)
                .driver(driver)
                .vehicle(vehicle)
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(LocalDateTime.now().plusDays(1))
                .build();
    }

    static RidePassenger ridePassenger(Ride ride, User passenger, int seatsReserved) {
        return RidePassenger.builder()
                .ride(ride)
                .passenger(passenger)
                .seatsReserved(seatsReserved)
                .pickupPoint("Puerta principal")
                .build();
    }

    static Review review(Ride ride, User reviewer, User reviewed) {
        return Review.builder()
                .ride(ride)
                .reviewer(reviewer)
                .reviewed(reviewed)
                .rating(5)
                .comment("Buen viaje")
                .build();
    }
}
