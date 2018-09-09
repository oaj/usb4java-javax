import dk.amfibia.usb4java.Usb4java;

import javax.usb.*;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            Usb4java usb4java = new Usb4java();

//            short vid = (short) 0x08FF;
//            short pid = (short) 0x0009;
            short vid = (short) 0x6352;
            short pid = (short) 0x213A;

            UsbDevice usbDevice = usb4java.findDevice(vid, pid);

            System.out.println("-----------------------------------------------------");
            System.out.println("usbDevice = " + usbDevice);

            UsbConfiguration configuration = usbDevice.getActiveUsbConfiguration();
            System.out.println("configuration = " + configuration);

            UsbConfigurationDescriptor usbConfigurationDescriptor = configuration.getUsbConfigurationDescriptor();
            System.out.println("usbConfigurationDescriptor = " + usbConfigurationDescriptor);

            System.out.println("-----------------------------------------------------");
            int interfaceIndex = 0;
            UsbInterface usbInterface = usb4java.getDeviceInterface(usbDevice, interfaceIndex);
            System.out.println("usbInterface" + "(" + interfaceIndex + ") = " + usbInterface);
            if (usbInterface != null) {
                try {
                    System.out.println("usbInterface.getInterfaceString() = " + usbInterface.getInterfaceString());
                    System.out.println("usbInterface.getUsbInterfaceDescriptor() = " + usbInterface.getUsbInterfaceDescriptor());
                } catch (UsbException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("-----------------------------------------------------");

            System.out.println("usbInterface.isClaimed() = " + usbInterface.isClaimed());
            System.out.println("Claiming interface");
            usbInterface.claim();
            System.out.println("usbInterface.isClaimed() = " + usbInterface.isClaimed());
            try {
                usb4java.readMessage(usbInterface, 0);
            } catch (UsbException e) {
                e.printStackTrace();
            } finally {
                try {
                    usbInterface.release();
                } catch (UsbException e) {
                    e.printStackTrace();
                }
            }
        } catch (UsbException e) {
            System.out.println("Some error");
            e.printStackTrace();
        }

    }
}
