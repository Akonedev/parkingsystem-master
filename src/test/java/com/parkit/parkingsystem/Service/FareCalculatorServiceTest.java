package com.parkit.parkingsystem.Service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

@Tag("FareCalculationTests")
@DisplayName("Calculate fares according to several situations")
public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    @DisplayName("calculate Fare Car")
    public void calculate_Fare_For_a_Car_should_return_Price_For_An_Hour(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((Fare.CAR_RATE_PER_HOUR - (Fare.CAR_RATE_PER_HOUR / 2)), ticket.getPrice());
    }

    @Test
    @DisplayName("calculate Fare Bike")
    public void calculate_Fare_For_a_Bike_should_return_Price_For_An_Hour(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((Fare.BIKE_RATE_PER_HOUR - (Fare.BIKE_RATE_PER_HOUR / 2)), ticket.getPrice());
    }

    @Test
    @DisplayName("When no vehicle type has been registered")
    public void calculate_Fare_For_Unkown_type_should_Execption(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    @DisplayName("calculate Fare Car With Future In Time")
    public void calculate_Fare_For_a_Car_park_in_future_should_return_Exception(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }
    @Test
    @DisplayName("calculate Fare Bike With Future In Time")
    public void calculate_Fare_For_a_Bike_park_in_future_should_return_Exception(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    @DisplayName("calculate Fare Car WithLess Than One Hour Parking Time")
    public void calculate_Fare_For_a_Car_Less_Than_One_Hour_Parking_Time_Should_return_right_price(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(((0.75 * Fare.CAR_RATE_PER_HOUR) - (Fare.CAR_RATE_PER_HOUR / 2)), ticket.getPrice());
    }

    @Test
    @DisplayName("calculate Fare Bike WithLess Than One Hour Parking Time")
    public void calculate_Fare_For_a_Bike_Less_Than_One_Hour_Parking_Time_Should_return_right_price(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(((0.75 * Fare.BIKE_RATE_PER_HOUR) - (Fare.BIKE_RATE_PER_HOUR / 2)), ticket.getPrice());
    }

    @Test
    @DisplayName("calculate Fare Car With half an Hour Parking Time")
    void calculate_Fare_For_a_Car_Less_Than_half_an_Hour_Parking_Time_Should_return_right_price() {
        // 20 minutes parking time should be free
        final Date inTime = new Date(System.currentTimeMillis() - (20 * 60 * 1000));
        final Date outTime = new Date();
        final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    @DisplayName("calculate Fare Car Under half an Hour Parking Time")
    void calculate_Fare_For_a_Bike_Less_Than_half_an_Hour_Parking_Time_Should_return_right_price() {
        // 20 minutes parking time should be free
        final Date inTime = new Date(System.currentTimeMillis() - (20 * 60 * 1000));
        final Date outTime = new Date();
        final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    @DisplayName("calculate Fare Car With More Than A Day Parking Time")
    public void calculate_Fare_For_a_Car_More_Than_a_day_Parking_Time_Should_return_right_price(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(((24 * Fare.CAR_RATE_PER_HOUR) - (Fare.CAR_RATE_PER_HOUR / 2)), ticket.getPrice());
    }

    @Test
    @DisplayName("calculate Fare Bike With More Than A Day Parking Time")
    public void calculate_Fare_For_a_Bike_More_Than_a_day_Parking_Time_Should_return_right_price(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(((24 * Fare.BIKE_RATE_PER_HOUR) - (Fare.BIKE_RATE_PER_HOUR / 2)), ticket.getPrice());
    }
}
