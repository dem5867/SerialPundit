package test14;

import com.embeddedunveiled.serial.SerialComManager;
import com.embeddedunveiled.serial.SerialComManager.BAUDRATE;
import com.embeddedunveiled.serial.SerialComManager.DATABITS;
import com.embeddedunveiled.serial.SerialComManager.FLOWCONTROL;
import com.embeddedunveiled.serial.SerialComManager.PARITY;
import com.embeddedunveiled.serial.SerialComManager.STOPBITS;
import com.embeddedunveiled.serial.ISerialComDataListener;
import com.embeddedunveiled.serial.SerialComDataEvent;

class Data implements ISerialComDataListener{
	@Override
	public void onNewSerialDataAvailable(SerialComDataEvent data) {
		System.out.println("Read from serial port : " + new String(data.getDataBytes()) + "\n");
	}
}

public class Test14 {
	public static void main(String[] args) {
		
		long handle = 0;
		SerialComManager scm = new SerialComManager();
		
		// instantiate class which is will implement ISerialComDataListener interface
		Data dataListener = new Data();
		
		try {
			// open and configure port that will listen data
			handle = scm.openComPort("COM51", true, true, false);
			scm.configureComPortData(handle, DATABITS.DB_8, STOPBITS.SB_1, PARITY.P_NONE, BAUDRATE.B115200, 0);
			scm.configureComPortControl(handle, FLOWCONTROL.NONE, 'x', 'x', false, false);
			
			// register data listener for this port
			scm.registerDataListener(handle, dataListener);
			scm.setMinDataLength(handle, 5);
			
			// open and configure port which will send data
			long handle1 = scm.openComPort("COM52", true, true, false);
			scm.configureComPortData(handle1, DATABITS.DB_8, STOPBITS.SB_1, PARITY.P_NONE, BAUDRATE.B115200, 0);
			scm.configureComPortControl(handle1, FLOWCONTROL.NONE, 'x', 'x', false, false);
			scm.writeString(handle1, "test", 0);
			Thread.sleep(1000);
			
			// although test string has been tranmitted, but listener will not get called because test has only
			// 4 bytes where as we set minnimum length as 5, so let us transmit 1 more byte and listener will
			// get called.
			scm.writeString(handle1, "H", 0);
			Thread.sleep(1000);
			
			// unregister data listener
			scm.unregisterDataListener(dataListener);
			
			// close the port releasing handle
			scm.closeComPort(handle);
			scm.closeComPort(handle1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
