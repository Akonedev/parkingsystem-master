package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class App {
    private static final Logger logger = LogManager.getLogger("App");
    public static void main(String args[]){
        logger.info("Initializing Parking System");
        final InputReaderUtil inputReaderUtil = new InputReaderUtil();
        final ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        final TicketDAO ticketDAO = new TicketDAO();
        final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        InteractiveShell.loadInterface(inputReaderUtil, parkingService);
        //InteractiveShell.loadInterface();
    }
}
