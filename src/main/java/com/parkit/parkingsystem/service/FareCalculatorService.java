package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        final Instant inHour = ticket.getInTime().toInstant();
        final Instant outHour = ticket.getOutTime().toInstant();

        final long duration = Duration.between(inHour, outHour).toMinutes();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        if (duration<30) {
            ticket.setPrice(0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    final double standardPrice = (duration * (Fare.CAR_RATE_PER_HOUR / 60)) - (Fare.CAR_RATE_PER_HOUR / 2);
                    ticket.setPrice((duration * (Fare.CAR_RATE_PER_HOUR / 60)) - (Fare.CAR_RATE_PER_HOUR / 2));
                    break;
                }
                case BIKE: {
                    final double standardPrice = (duration * (Fare.BIKE_RATE_PER_HOUR / 60))
                            - (Fare.BIKE_RATE_PER_HOUR / 2);
                    ticket.setPrice((duration * (Fare.BIKE_RATE_PER_HOUR / 60))
                            - (Fare.BIKE_RATE_PER_HOUR / 2));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }

    public void calculateReducedFare(Ticket ticket){
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        ticket.setPrice( ticket.getPrice() * Fare.DISCOUNT_RATE_PER_HOUR);
    }


}