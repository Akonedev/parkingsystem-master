package com.parkit.parkingsystem.Service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("ParkingServiceTest")
@DisplayName("ParkingService Tests")
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;


    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    private Ticket generateTicket(ParkingType type, Date inTime) {
        final ParkingSpot parkingSpot = new ParkingSpot(1, type, false);
        final Ticket ticket = new Ticket();
        ticket.setInTime(inTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        return ticket;
    }

    @Test
    @DisplayName("process Incoming Car . It should Park a Car when Parking Slot IsAvailable")
    public void process_Incoming_Car_should_Park_Car_when_Parking_Slot_Is_Available() throws SQLException {
        setUpPerTest();
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        parkingService.processIncomingVehicle();

        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    @DisplayName("process Incoming Bike. It should Park a bike when Parking Slot IsAvailable")
    public void process_Incoming_Bike_should_Park_BIKE_when_Parking_Slot_Is_Available() throws SQLException {
        setUpPerTest();
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        parkingService.processIncomingVehicle();

        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    @DisplayName("process Incoming Car. It Throw Exception when Parking Slot Illegal")
    public void process_Incoming_Vehicle_should_Throw_Exception_when_Parking_Spot_Is_Illegal() throws SQLException {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(0)).saveTicket(any(Ticket.class));
    }

    @Test
    @DisplayName("process Exiting Bike. It should Update Parking")
    public void process_Exiting_Vehicle_should_Update_Parking() throws SQLException {
        setUpPerTest();
        final Date inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
        final Ticket ticket = generateTicket(ParkingType.CAR, inTime);
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        parkingService.processExitingVehicle();

        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        assertTrue(ticket.getParkingSpot().isAvailable());
    }

    @Test
    @DisplayName("process Exiting Bike. It should Not Update Parking whenError Occurred")
    public void process_Exiting_Vehicle_should_Not_Update_Parking_when_Error_Occurred() throws SQLException {
        setUpPerTest();
        final Date inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
        final Ticket ticket = generateTicket(ParkingType.CAR, inTime);
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        parkingService.processExitingVehicle();

        assertEquals(0, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
        assertFalse(ticket.getParkingSpot().isAvailable());
    }

    @Test
    @DisplayName("process Exiting Bike. It should Throw SQLException whenError Occurred on GetTicket")
    public void process_Exiting_Vehicle_should_Throw_SQL_Exception_when_GetT_icket() throws SQLException {
        setUpPerTest();
        new Date(System.currentTimeMillis() - (60 * 60 * 1000));
        when(ticketDAO.getTicket(anyString())).thenThrow(new SQLException("Failed to get ticket"));

        parkingService.processExitingVehicle();

        verify(ticketDAO, times(0)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
    }

    @Test
    @DisplayName("process Exiting Bike.Parking Time is less than 30 mn. Should be Free")
    public void process_Exiting_Vehicle_parking_For_Less_Than_30_Minutes_shoul_dBe_Free() throws SQLException {
        setUpPerTest();
        final Date inTime = new Date(System.currentTimeMillis() - (20 * 60 * 1000));
        final Ticket ticket = generateTicket(ParkingType.CAR, inTime);
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        parkingService.processExitingVehicle();

        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        assertEquals(0, ticket.getPrice());
        assertTrue(ticket.getParkingSpot().isAvailable());

    }

    @Test
    @DisplayName("process Exiting Bik for recurring user")
    public void process_Exiting_Vehicle_Should_return_charge_For_Recurring_User_When_Exit() throws SQLException {
        setUpPerTest();
        final Date inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
        final Ticket ticket = generateTicket(ParkingType.CAR, inTime);
        when(ticketDAO.findRecurringUser(anyString())).thenReturn(true);
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        parkingService.processExitingVehicle();

        assertEquals(0.95 * 1.5/2, ticket.getPrice());
        verify(ticketDAO, times(1)).updateTicket(ticket);
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        assertTrue(ticket.getParkingSpot().isAvailable());
    }




}
