package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.Util.DataBaseTestConfig;
import com.parkit.parkingsystem.Util.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;


import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("ParkingDataBaseITTest")
@DisplayName("Parking DataBase Integration Test")
public class ParkingDataBaseITTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    @DisplayName("test Parking A Car")
    public void process_Incoming_Car_should_saved_tiket_and_parking_slot() throws Exception {
        final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        final Ticket dbTicket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
        assertEquals(ticketDAO.getTicket("ABCDEF").getId(), 1);
        assertEquals(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR), 2);
    }

    @Test
    @DisplayName("test Parking A Bike")
    public void process_Incoming_Bike_should_saved_tiket_and_parking_slot() throws Exception {
        final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        final Ticket dbTicket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
        assertEquals(ticketDAO.getTicket("ABCDEF").getId(), 1);
        assertEquals(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE), 4);
    }


    @Test
    @DisplayName("test exiting A vehicule")
    public void process_Exiting_Vehicule_Should_Update_Tiket_Out_Time() throws Exception {
        final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Thread.sleep(2000);
        parkingService.processExitingVehicle();
        final Ticket ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
        assertNotNull(ticket.getOutTime());

    }


}
