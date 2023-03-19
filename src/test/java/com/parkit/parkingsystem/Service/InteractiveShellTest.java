package com.parkit.parkingsystem.Service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;


@ExtendWith(MockitoExtension.class)
@Tag("InteractiveShellTest")
@DisplayName("interactif shell tests")
public class InteractiveShellTest {

    private static InteractiveShell interactiveShell;
    @Mock
    private static InputReaderUtil inputReaderUtil;

    @Mock
    private static ParkingService parkingService;

    @Test
    @DisplayName("load Interface; It should Call Process IncomingVehicle when Case = 1")
    public void loadInterface_shouldCallProcessIncomingVehicle_whenCase1() {
        when(inputReaderUtil.readSelection()).thenReturn(1, 3);
        interactiveShell.loadInterface(inputReaderUtil, parkingService);

        verify(parkingService, times(1)).processIncomingVehicle();
    }

    @Test
    @DisplayName("load Interface; It should Call Process IncomingVehicle when Case = 2")
    public void loadInterface_shouldCallProcessExitingVehicle_whenCase2() {
        when(inputReaderUtil.readSelection()).thenReturn(2, 3);
        interactiveShell.loadInterface(inputReaderUtil, parkingService);

        verify(parkingService, times(1)).processExitingVehicle();
    }

    @Test
    @DisplayName("load Interface; It should Call Process IncomingVehicle when Case = 3")
    public void loadInterface_shouldNotCallAnyone_whenCase3() {
        when(inputReaderUtil.readSelection()).thenReturn(3);
        interactiveShell.loadInterface(inputReaderUtil, parkingService);

        verify(parkingService, times(0)).processIncomingVehicle();
        verify(parkingService, times(0)).processExitingVehicle();
    }


    @Test
    @DisplayName("load Interface; It should not nothing when Other Cases")
    void loadInterface_shouldNotCallNothing_whenOtherCases() {
        when(inputReaderUtil.readSelection()).thenReturn(4, -1, 0, 3);
        interactiveShell.loadInterface(inputReaderUtil, parkingService);

        verify(parkingService, times(0)).processIncomingVehicle();
        verify(parkingService, times(0)).processExitingVehicle();
    }
}

