/**
 * Author : Rishi Gupta
 * 
 * This file is part of 'serial communication manager' library.
 * Copyright (C) <2014-2016>  <Rishi Gupta>
 *
 * This 'serial communication manager' is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * The 'serial communication manager' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with 'serial communication manager'.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.embeddedunveiled.serial.SerialComManager;
import com.embeddedunveiled.serial.nullmodem.SerialComNullModem;
import com.embeddedunveiled.serial.SerialComLineErrors;
import com.embeddedunveiled.serial.SerialComManager.BAUDRATE;
import com.embeddedunveiled.serial.SerialComManager.DATABITS;
import com.embeddedunveiled.serial.SerialComManager.FLOWCONTROL;
import com.embeddedunveiled.serial.SerialComManager.PARITY;
import com.embeddedunveiled.serial.SerialComManager.STOPBITS;

public final class ParityFrameError {

    public static void main(String[] args) throws Exception {

        SerialComManager scm = new SerialComManager();
        final SerialComNullModem scnm = scm.getSerialComNullModemInstance();

        try {
            scnm.createStandardNullModemPair(-1, -1);

            String[] ports = scnm.getLastNullModemDevicePairNodes();

            long handle0 = scm.openComPort(ports[0], true, true, true);
            scm.configureComPortData(handle0, DATABITS.DB_8, STOPBITS.SB_1, PARITY.P_ODD, BAUDRATE.B115200, 0);
            scm.configureComPortControl(handle0, FLOWCONTROL.NONE, 'x', 'x', true, true);

            long handle1 = scm.openComPort(ports[1], true, true, true);
            scm.configureComPortData(handle1, DATABITS.DB_8, STOPBITS.SB_1, PARITY.P_ODD, BAUDRATE.B115200, 0);
            scm.configureComPortControl(handle1, FLOWCONTROL.NONE, 'x', 'x', true, true);


            byte[] buffer = new byte[100];
            SerialComLineErrors lineErr = new SerialComLineErrors();

            // PARITY 
            System.out.println("PARITY  before : " + lineErr.hasParityErrorOccurred());
            scnm.emulateLineError(ports[1], SerialComNullModem.ERR_PARITY);

            int ret = scm.readBytes(handle1, buffer, 0, 50, -1, lineErr);
            System.out.println("PARITY  after : " + lineErr.hasParityErrorOccurred());

            for(int x=0; x<ret; x++) {
                System.out.println("PARITY  after data : " + buffer[x]);
            }

            // OVERRUN
            lineErr.resetLineErrors();
            System.out.println("\nOVERRUN before : " + lineErr.hasOverrunErrorOccurred());
            scnm.emulateLineError(ports[1], SerialComNullModem.ERR_OVERRUN);

            int ret1 = scm.readBytes(handle1, buffer, 0, 50, -1, lineErr);
            System.out.println("OVERRUN after : " + lineErr.hasOverrunErrorOccurred());

            for(int x=0; x<ret1; x++) {
                System.out.println("OVERRUN after data : " + buffer[x]);
            }

            // FRAME
            lineErr.resetLineErrors();
            System.out.println("\nFRAME before : " + lineErr.hasFramingErrorOccurred());
            scnm.emulateLineError(ports[1], SerialComNullModem.ERR_FRAME);

            int ret2 = scm.readBytes(handle1, buffer, 0, 50, -1, lineErr);
            System.out.println("FRAME after : " + lineErr.hasFramingErrorOccurred());

            for(int x=0; x<ret2; x++) {
                System.out.println("FRAME after data : " + buffer[x]);
            }

            // BREAK
            lineErr.resetLineErrors();
            System.out.println("\nBREAK before : " + lineErr.isBreakReceived());
            scnm.emulateLineError(ports[1], SerialComNullModem.RCV_BREAK);

            int ret3 = scm.readBytes(handle1, buffer, 0, 50, -1, lineErr);
            System.out.println("BREAK after : " + lineErr.isBreakReceived());

            for(int x=0; x<ret3; x++) {
                System.out.println("BREAK after data : " + buffer[x]);
            }

            // BREAK SEND AND RECEIVE
            lineErr.resetLineErrors();
            System.out.println("\nBREAK before : " + lineErr.isBreakReceived());
            scm.sendBreak(handle0, 100);

            int ret4 = scm.readBytes(handle1, buffer, 0, 50, -1, lineErr);
            System.out.println("BREAK after : " + lineErr.isBreakReceived());

            for(int x=0; x<ret4; x++) {
                System.out.println("BREAK after data : " + buffer[x]);
            }

            scm.closeComPort(handle0);
            scm.closeComPort(handle1);

            //scnm.destroyAllVirtualDevices();
            scnm.releaseResources(); 

            System.out.println("Done !");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}