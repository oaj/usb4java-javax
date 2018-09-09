package dk.amfibia.usb4java;

import javax.usb.*;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class Usb4java {

    private Charset charset = Charset.forName("US-ASCII");
    public static final char EOF = '\u001a';


    public UsbDevice getUsbRootHoob() {
        try {
            final UsbServices services = UsbHostManager.getUsbServices();
            System.out.println("services = " + services);
            UsbHub rootHub = services.getRootUsbHub();
            System.out.println("rootHub = " + rootHub);
            return rootHub;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (UsbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UsbDevice findDevice(short vendorId, short productId) {
        return findDevice((UsbHub) getUsbRootHoob(), vendorId, productId);
    }

    public UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
    }

    public UsbInterface getDeviceInterface(UsbDevice device, int index) {
        UsbConfiguration configuration = device.getActiveUsbConfiguration();
        UsbInterface iface = (UsbInterface) configuration.getUsbInterfaces().get(index); // there can be more 1,2,3..
        return iface;
    }

    private StringBuilder line = new StringBuilder();

    public void parseChars(char ch) {
        if (ch == '\n') {
            char[] scan = line.toString().toCharArray();
            String data = new String(scan);
            System.out.println();
            System.out.println("Data from Scanner: '" + data + "'");
            line = new StringBuilder();
        } else {
            if (ch != EOF) {
                line.append(ch);
            }
        }
    }

    public void readMessage(UsbInterface iface, int endPointIndex) throws UsbException {
        System.out.println("readMessage, endPoint = " + endPointIndex);
        UsbEndpoint endpoint = (UsbEndpoint) iface.getUsbEndpoints().get(endPointIndex); // there can be more 1,2,3..
        System.out.println("endpoint = " + endpoint);

        UsbPipe pipe = endpoint.getUsbPipe();

        System.out.println("pipe = " + pipe);
//        readSync(pipe);
        readASync(pipe);
    }

    private void readSync(UsbPipe pipe) throws UsbException {
        pipe.open();
        try {
//            int byteArraySize= 16384;
            int byteArraySize= 32;
            System.out.println("byteArraySize = " + byteArraySize);

            byte[] baData = new byte[byteArraySize];

            int received = byteArraySize;
            System.out.println("initial received = " + received);

            while (received >= byteArraySize ) {
                received = pipe.syncSubmit(baData);

                System.out.println(received + ": ");
                for (int i = 0; i < baData.length; i = i + 8) {
                    byte[] bytes8 = Arrays.copyOfRange(baData, i, i + 8);
                    process8Bytes(bytes8);
                }
            }
        } finally {
            pipe.close();
        }
    }

    private void process8Bytes(byte[] bytes) {
//        System.out.println("process8Bytes = " + bytes.length);
//        int i = 0;
//        while (i < bytes.length) {
//            byte b = bytes[i];
//            System.out.print(b);
//            if (i != bytes.length - 1) {
//                System.out.print(", ");
//            } else {
//                System.out.println();
//            }
//            i++;
//        }

        if (bytes[2] != 0) {
            byte asciiByte = UsbScancodesConverter.HIDtoA(bytes[2], bytes[0]);
            byte[] singleByte = {asciiByte};
            char ch = (new String(singleByte, charset)).charAt(0);
            System.out.println("ch = " + ch);
            parseChars(ch);
        }
    }

//    private void readSync(UsbPipe pipe) throws UsbException {
//        pipe.open();
//        try {
//            byte[] data = new byte[8];
//
//            int received = pipe.syncSubmit(data);
//            System.out.println("data.length = " + data.length);
//            System.out.println(received + " bytes received");
//            System.out.println("data = ");
//            int i = 0;
//            for (byte b : data) {
//                i +=1;
//                System.out.print(b + " ,");
//            }
//            System.out.println();
//            System.out.println("i = " + i);
//
//            received = pipe.syncSubmit(data);
//            System.out.println("data.length = " + data.length);
//            System.out.println(received + " bytes received");
//            System.out.println("data = ");
//            for (byte b : data) {
//                System.out.print(b + " ,");
//            }
//            System.out.println();
//
//        } finally {
//            pipe.close();
//        }
//    }

    private void readASync(UsbPipe pipe) throws UsbException {
        pipe.open();
        try {
            byte[] data = new byte[8];
            pipe.addUsbPipeListener(new UsbPipeListener() {
                @Override
                public void errorEventOccurred(UsbPipeErrorEvent event) {
                    UsbException error = event.getUsbException();
                    System.out.println("Async read error");
                    System.out.println("error.getMessage() = " + error.getMessage());
                    error.printStackTrace();
                }

                @Override
                public void dataEventOccurred(UsbPipeDataEvent event) {
                    byte[] data = event.getData();
                    System.out.println("event.getActualLength() = " + event.getActualLength());
                    for (byte b : data) {
                        System.out.print("data = ");
                        System.out.print(data[0] + " ,");
                    }
                }
            });
            UsbIrp usbIrp = pipe.asyncSubmit(data);
            System.out.println("usbIrp = " + usbIrp);
        } finally {
//            pipe.close();
        }
    }

}
