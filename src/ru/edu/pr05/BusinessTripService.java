package ru.edu.pr05;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BusinessTripService {

    private final List<BusinessTrip> trips = new ArrayList<>();

    public BusinessTrip create(String id, Employee employee, LocalDate start, LocalDate end) {
        BusinessTrip trip = new BusinessTrip(id, employee, start, end);
        trips.add(trip);
        return trip;
    }

    public List<BusinessTrip> findByStatus(TripStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть null");
        }
        List<BusinessTrip> result = new ArrayList<>();
        for (BusinessTrip trip : trips) {
            if (trip.getStatus() == status) {
                result.add(trip);
            }
        }
        return List.copyOf(result);
    }

    public List<BusinessTrip> getAll() {
        return List.copyOf(trips);
    }
}
