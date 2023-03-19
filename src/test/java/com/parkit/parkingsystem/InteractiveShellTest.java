package com.parkit.parkingsystem;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;


@ExtendWith(MockitoExtension.class)
public class InteractiveShellTest {

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @Mock
    private static ParkingService parkingService;

    @Test
    public void loadInterface_shouldCallProcessIncomingVehicle_whenCase1() {
        // Given When
        when(inputReaderUtil.readSelection()).thenReturn(1, 3);
        InteractiveShell.loadInterface(inputReaderUtil, parkingService);
        // Then
        verify(parkingService, times(1)).processIncomingVehicle();
    }


}

