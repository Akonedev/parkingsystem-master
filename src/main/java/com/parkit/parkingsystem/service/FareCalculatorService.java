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

        Instant inHour = ticket.getInTime().toInstant();
        Instant outHour = ticket.getOutTime().toInstant();

        long duration = Duration.between(inHour, outHour).toMinutes();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        if (duration<30) {
            ticket.setPrice(0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                   // ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    ticket.setPrice((duration * (Fare.CAR_RATE_PER_HOUR / 60))
                            - (Fare.CAR_RATE_PER_HOUR / 2));
                    break;
                }
                case BIKE: {
                    //ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                    ticket.setPrice((duration * (Fare.BIKE_RATE_PER_HOUR / 60))
                            - (Fare.BIKE_RATE_PER_HOUR / 2));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}