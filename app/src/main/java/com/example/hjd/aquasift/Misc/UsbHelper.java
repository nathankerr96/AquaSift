package com.example.hjd.aquasift.Misc;

import android.app.PendingIntent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.beans.PropertyChangeSupport;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.lang.Math.abs;

/**
 * Created by HJD on 9/5/2016.
 */
public class UsbHelper {

    int PID = 24577;
    int VID = 1027;
    int DID = 2002;

    private UsbManager usbManager;
    private UsbDevice targetDevice;
    private UsbInterface targetInterface;
    private UsbDeviceConnection deviceConnection;
    private UsbEndpoint inPoint;
    private UsbEndpoint outPoint;


    private class CurrentSettings {
        public int firmwareRevisionNum;
        public int productId;
        public int electrodeNum;
        public int dataRateOut;
        public int gainResistor;
        public int depositionOn;
        public int depositionTime;
        public int depositionVoltage;
        public int quietTime;
        public int recordDepositionSequence;
        public int linearSweepStartVoltage;
        public int linearSweepEndVoltage;
        public int linearSweepRate;
        public int linearSweepCyclic;
        public int linearSweepNumCycles;
        public int diffPulseStartVoltage;
        public int diffPulseEndVoltage;
        public int diffPulseIncrement;
        public int diffPulsePulseVoltage;
        public int diffPulsePrePulseTime;
        public int diffPulsePulseTime;
        public int diffPulseWindowWidth;
        public int arbitaryWaveformStoredValues;
        public int selectedFilter;

        CurrentSettings(byte[] rawSettings) {
            if (rawSettings.length != 47) {
                Log.d("DEBUGGING", "Incorrect array passed to settings");
                return;
            }
            firmwareRevisionNum = ((rawSettings[0]&0xFF)<<8) + (rawSettings[1]&0xFF);
            productId = ((rawSettings[2]&0xFF)<<24) + ((rawSettings[3]&0xFF)<<16) +
                    ((rawSettings[4]&0xFF)<<8) + ((rawSettings[5]&0xFF));
            electrodeNum = ((rawSettings[6])&0xFF);
            dataRateOut = ((rawSettings[7]&0xFF)<<8) + (rawSettings[8]&0xFF);
            gainResistor = (rawSettings[9]&0xFF);
            depositionOn = (rawSettings[10]&0xFF);
            depositionTime = ((rawSettings[11]&0xFF)<<24) + ((rawSettings[12]&0xFF)<<16) +
                    ((rawSettings[13]&0xFF)<<8) + ((rawSettings[14]&0xFF));
            depositionVoltage = ((rawSettings[15]&0xFF)<<8) + (rawSettings[16]&0xFF);
            if ((depositionVoltage & 0x8000) != 0) {
                depositionVoltage |= 0xFFFF0000;
            }
            quietTime = ((rawSettings[17]&0xFF)<<24) + ((rawSettings[18]&0xFF)<<16) +
                    ((rawSettings[19]&0xFF)<<8) + ((rawSettings[20]&0xFF));
            recordDepositionSequence = (rawSettings[21]&0xFF);
            linearSweepStartVoltage = ((rawSettings[22]&0xFF)<<8) + (rawSettings[23]&0xFF);
            if ((linearSweepStartVoltage & 0x8000) != 0) {
                linearSweepStartVoltage |= 0xFFFF0000;
            }
            linearSweepEndVoltage = ((rawSettings[24]&0xFF)<<8) + (rawSettings[25]&0xFF);
            if ((linearSweepEndVoltage & 0x8000) != 0) {
                linearSweepEndVoltage |= 0xFFFF0000;
            }
            linearSweepRate = ((rawSettings[26]&0xFF)<<8) + (rawSettings[27]&0xFF);
            linearSweepCyclic = (rawSettings[28]&0xFF);
            linearSweepNumCycles = (rawSettings[29]&0xFF);
            diffPulseStartVoltage = ((rawSettings[30]&0xFF)<<8) + (rawSettings[31]&0xFF);
            if ((diffPulseStartVoltage & 0x8000) != 0) {
                diffPulseStartVoltage |= 0xFFFF0000;
            }
            diffPulseEndVoltage = ((rawSettings[32]&0xFF)<<8) + (rawSettings[33]&0xFF);
            if ((diffPulseEndVoltage & 0x8000) != 0) {
                diffPulseEndVoltage |= 0xFFFF0000;
            }
            diffPulseIncrement = ((rawSettings[34]&0xFF)<<8) + (rawSettings[35]&0xFF);
            diffPulsePulseVoltage = ((rawSettings[36]&0xFF)<<8) + (rawSettings[37]&0xFF);
            if ((diffPulsePulseVoltage & 0x8000) != 0) {
                diffPulsePulseVoltage |= 0xFFFF0000;
            }
            diffPulsePrePulseTime = ((rawSettings[38]&0xFF)<<8) + (rawSettings[39]&0xFF);
            diffPulsePulseTime = ((rawSettings[40]&0xFF)<<8) + (rawSettings[41]&0xFF);
            diffPulseWindowWidth = ((rawSettings[42]&0xFF)<<8) + (rawSettings[43]&0xFF);
            arbitaryWaveformStoredValues = ((rawSettings[44]&0xFF)<<8) + (rawSettings[45]&0xFF);
            selectedFilter = (rawSettings[46]&0xFF);
        }

        public void logSettings() {
            Log.d("DEBUGGING", "Firmware Revision: "+Integer.toString(firmwareRevisionNum));
            Log.d("DEBUGGING", "Product ID: "+Integer.toString(productId));
            Log.d("DEBUGGING", "Number of electrodes: "+Integer.toString(electrodeNum));
            Log.d("DEBUGGING", "Output Data Rate: "+Integer.toString(dataRateOut));
            Log.d("DEBUGGING", "TIA Gain Resistor: "+Integer.toString(gainResistor));
            Log.d("DEBUGGING", "Enable Deposition: "+Integer.toString(depositionOn));
            Log.d("DEBUGGING", "Deposition Time: "+Integer.toString(depositionTime));
            Log.d("DEBUGGING", "Deposition Voltage: "+Integer.toString(depositionVoltage));
            Log.d("DEBUGGING", "Quiet Time: "+Integer.toString(quietTime));
            Log.d("DEBUGGING", "Record Deposition Sequence: "+Integer.toString(recordDepositionSequence));
            Log.d("DEBUGGING", "Linear Sweep Start Voltage: "+Integer.toString(linearSweepStartVoltage));
            Log.d("DEBUGGING", "Linear Sweep End Voltage: "+Integer.toString(linearSweepEndVoltage));
            Log.d("DEBUGGING", "Linear Sweep-Rate: "+Integer.toString(linearSweepRate));
            Log.d("DEBUGGING", "Linear Sweep Cyclic: "+Integer.toString(linearSweepCyclic));
            Log.d("DEBUGGING", "Linear Sweep Number of Cycles: "+Integer.toString(linearSweepNumCycles));
            Log.d("DEBUGGING", "Diff Pulse Start Voltage: "+Integer.toString(diffPulseStartVoltage));
            Log.d("DEBUGGING", "Diff Pulse End Voltage: "+Integer.toString(diffPulseEndVoltage));
            Log.d("DEBUGGING", "Diff Pulse Increment: "+Integer.toString(diffPulseIncrement));
            Log.d("DEBUGGING", "Diff Pulse Pulse Voltage: "+Integer.toString(diffPulsePulseVoltage));
            Log.d("DEBUGGING", "Diff Pulse Pre-pulse Time: "+Integer.toString(diffPulsePrePulseTime));
            Log.d("DEBUGGING", "Diff Pulse Pulse Time: "+Integer.toString(diffPulsePulseTime));
            Log.d("DEBUGGING", "Diff Pulse Window Width: "+Integer.toString(diffPulseWindowWidth));
            Log.d("DEBUGGING", "Arbitrary Waveform Stored Values: "+Integer.toString(arbitaryWaveformStoredValues));
            Log.d("DEBUGGING", "Selected Low-pass Filter: "+Integer.toString(selectedFilter));
        }
    }

    private CurrentSettings currentSettings;



    public UsbHelper(UsbManager manager) {
        usbManager = manager;
        targetDevice = null;
        targetInterface = null;
        deviceConnection = null;
    }



    //0: Success
    //1: Could not find device
    //2: Does not have permission to access device
    //3: Could not Claim interface
    //4: Could not set endpoints
    //5: Could not initialize connection;
    public int begin() {
        checkDevices();
        if(!hasDevice()) {
            return 1;
        }
        if (!hasPermission()) {
            return 2;
        }
        //Correct Device is connected and app has permission to access it
        if (hasConnection()) {
            closeConnection();
        }
        if(!claimInterface()) {
            return 3;
        }
        //Has open connection w/ claimed interface
        if(!setEndpoints()) {
            return 4;
        }
        //Ready to Initialize
        if(!initCommunication()) {
            return 5;
        }
        //Communication ready
        if(!setMode(0)) {
            return 6;
        }
        //Now in Binary Mode
        if(!getSettings()) {
            return 7;
        }

        return 0;
    }

    public boolean getSettings() {

        if (!hasConnection()) {
            Log.d("DEBUGGING", "No Connection");
            return false;
        }

        byte[] unread_data = read();
        if (unread_data.length > 0) {
            Log.d("DEBUGGING", "Unread Data from getSettings: " + Arrays.toString(unread_data));
        }

        byte[] getSettingsCommand = {0x0A};
        int len = write(getSettingsCommand);
        if (len <= 0) {
            return false;
        }

        byte[] rawData = read();
        if (rawData.length != 47) {
            Log.d("DEBUGGING", "Unexpected length for returned settings");
            return false;
        }

        currentSettings = new CurrentSettings(rawData);
        currentSettings.logSettings();

        return true;
    }

    //Getter Methods
    public int getDepositionTime() {
        if (currentSettings.depositionOn == 0) {
            return 0;  //Return 0 if deposition is disabled
        }

        return currentSettings.depositionTime;
    }

    //Calculates mV increment per data point output
    public float getSweepVoltageIncrement() {
        int startVoltage = currentSettings.linearSweepStartVoltage;
        int endVoltage = currentSettings.linearSweepEndVoltage;
        int dataRate = currentSettings.dataRateOut;
        int sweepRate = currentSettings.linearSweepRate;

        float testTime = abs(startVoltage - endVoltage) / sweepRate; //test time in seconds
        float numDataPoints = (testTime*1000)/dataRate;

        return abs(startVoltage-endVoltage) / numDataPoints;
    }

    public int getSweepStartVoltage() {
        return currentSettings.linearSweepStartVoltage;
    }

    public int getSweepEndVoltage() {
        return currentSettings.linearSweepEndVoltage;
    }

    public int getNumCycles() {
        if (currentSettings.linearSweepCyclic == 0) {
            return 0;
        }
        return currentSettings.linearSweepNumCycles;
    }

    public int getDepositionStatus() {
        return currentSettings.depositionOn;
    }

    public int isCyclic() {
        return currentSettings.linearSweepCyclic;
    }

    public int getGainResistor() {
        switch (currentSettings.gainResistor) {
            case 1:
                return 100;
            case 2:
                return 1000;
            case 3:
                return 5100;
            case 4:
                return 10000;
            case 5:
                return 51000;
            case 6:
                return 100000;
            default:
                Log.d("DEBUGGING", "Not a valid Gain Resistor");
                return -1;
        }
    }

    //sets mode; 0=Binary, 1=ASCII, 2=MatLab
    private boolean setMode(int mode) {
        int current_mode = getMode();
        if (current_mode < 0) {
            return  false;
        }
        if (current_mode == mode) {
            return true;
        }
        if (current_mode == 0) {
            //TODO Add Logic for Binary
        } else {
            String setModeCommand = "1 ";
            if (mode == 0) {
                setModeCommand += "B";
            }
            if (mode == 1) {
                setModeCommand += "A";
            }
            if (mode == 2) {
                setModeCommand += "M";
            }
            setModeCommand += "\r";

            write(setModeCommand.getBytes());
        }

        current_mode = getMode();
        if (current_mode == mode) {
            return true;
        } else {
            Log.d("DEBUGGING", "Could not set mode");
            return false;
        }
    }

    //gets current mode; 0=Binary, 1=ASCII, 2=MatLab
    private int getMode() {
        byte[] unread_data = read();
        if (unread_data.length > 0) {
            Log.d("DEBUGGING", "Unread Data from getMode: " + Arrays.toString(unread_data));
        }

        byte[] getModeCommand = {0x54};
        int len = write(getModeCommand);
        if (len != 1) {
            Log.d("DEBUGGING", "More than one byte was written!");
            return -1;
        }
        byte[] rawData = read();
        if (rawData.length != 1) {
            Log.d("DEBUGGING", "Unexpected return length for getMode, Array: " + Arrays.toString(rawData));
            return -1;
        }
        int type = (rawData[0] & 0xFF);
        switch (type) {
            case 0x42:
                return 0;
            case 0x41:
                return 1;
            case 0x4d:
                return 2;
            default:
                Log.d("DEBUGGING", "Unexpected type from getMode, Mode: " + Integer.toString(type));
                return -1;

        }
    }

    //does no reading after to confirm, but sets value recommend double checking settings
    public boolean startLinearSweep() {
        if (!hasConnection()) {
            Log.d("DEBUGGING", "Could not start linear sweep, no connection");
            return false;
        }

        byte[] unread_data = read();
        if (unread_data.length > 0) {
            Log.d("DEBUGGING", "Unread Data from startLinearSweep: " + Arrays.toString(unread_data));
        }

        byte[] startCommand = {0x4C};
        int len = write(startCommand);
        if (len <= 0) {
            Log.d("DEBUGGING", "Linear sweep start command not written");
            return false;
        }

        return true;
    }

    public boolean setNumElectrodes(int numElectrodes) {
        if (numElectrodes < 2 || numElectrodes > 3) {
            Log.d("DEBUGGING", "Could not set num electrodes, " + Integer.toString(numElectrodes) + " not in valid range");
            return false;
        }
        if (changeSetting((byte)0x02, numElectrodes, "setNumElectrodes")) {
            currentSettings.electrodeNum = numElectrodes;
            return true;
        } else {
            return false;
        }
    }

    public boolean setDataRate(int dataRate) {
        if (dataRate < 1 || dataRate > 1000) {
            Log.d("DEBUGGING", "Could not set data rate, " + Integer.toString(dataRate) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x03, dataRate, "setDataRate")) {
            currentSettings.dataRateOut = dataRate;
            return true;
        } else {
            return false;
        }
    }

    public boolean setTiaGainResistor(int resistorSelection) {
        if (resistorSelection < 1 || resistorSelection > 6) {
            Log.d("DEBUGGING", "Could not gain resistor, " + Integer.toString(resistorSelection) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x0B, resistorSelection, "setTiaGainResistor")) {
            currentSettings.gainResistor = resistorSelection;
            return true;
        } else {
            return false;
        }
    }

    public boolean enableDeposition(int depositionOn) {
        if (depositionOn < 0 || depositionOn > 1) {
            Log.d("DEBUGGING", "Could change deposition state, " + Integer.toString(depositionOn) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x0C, depositionOn, "enableDeposition")) {
            currentSettings.depositionOn = depositionOn;
            return true;
        } else {
            return false;
        }
    }

    public boolean setDepositionTime(int depositionTime) {
        if (depositionTime < 1 || depositionTime > 800000) {
            Log.d("DEBUGGING", "Could change deposition voltage, " + Integer.toString(depositionTime) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x0D, depositionTime, "setDepositionTime")) {
            currentSettings.depositionTime = depositionTime;
            return true;
        } else {
            return false;
        }
    }

    public boolean setDepositionVoltage(int depositionVoltage) {
        if (depositionVoltage < -1650 || depositionVoltage > 1650) {
            Log.d("DEBUGGING", "Could change deposition voltage, " + Integer.toString(depositionVoltage) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x0E, depositionVoltage, "enableDeposition")) {
            currentSettings.depositionVoltage = depositionVoltage;
            return true;
        } else {
            return false;
        }
    }

    public boolean setQuietTime(int quietTime) {
        if (quietTime < 1 || quietTime > 800000) {
            Log.d("DEBUGGING", "Could not set Quiet Time, " + Integer.toString(quietTime) + " is out of range");
            return false;
        }
        if (changeSetting((byte)0x0F, quietTime, "setQuietTime")) {
            currentSettings.quietTime = quietTime;
            return true;
        } else {
            return false;
        }
    }

    public boolean recordDeposition(int recordDeposition) {
        if (recordDeposition < 0 || recordDeposition > 1) {
            Log.d("DEBUGGING", "Could not set record deposition, " + Integer.toString(recordDeposition) + " is out of range");
            return false;
        }
        if (changeSetting((byte)0x10, recordDeposition, "recordDeposition")) {
            currentSettings.recordDepositionSequence = recordDeposition;
            return true;
        } else {
            return false;
        }
    }

    public boolean setSweepStartVoltage(int startVoltage) {
        if (startVoltage < -1650 || startVoltage > 1650) {
            Log.d("DEBUGGING", "Could not set Sweep Start Voltage, " + Integer.toString(startVoltage) + "is out of range");
            return false;
        }
        if (changeSetting((byte)0x11, startVoltage, "setSweepStartVoltage")) {
            currentSettings.linearSweepStartVoltage = startVoltage;
            return true;
        } else {
            return false;
        }
    }

    public boolean setSweepEndVoltage(int endVoltage) {
        if (endVoltage < -1650 || endVoltage > 1650) {
            Log.d("DEBUGGING", "Could not setSweepEndVoltage, " + Integer.toString(endVoltage) + " is out of range");
            return false;
        }
        if (changeSetting((byte)0x12, endVoltage, "setSweepEndVoltage")) {
            currentSettings.linearSweepEndVoltage = endVoltage;
            return true;
        } else {
            return false;
        }
    }

    public boolean setSweepRate(int sweepRate) {
        if (sweepRate < 1 || sweepRate > 4000) {
            Log.d("DEBUGGING", "Could not setSweepRate, " + Integer.toString(sweepRate) + " is out of range");
            return false;
        }
        if (changeSetting((byte)0x13, sweepRate, "setSweepRate")) {
            currentSettings.linearSweepRate = sweepRate;
            return true;
        } else {
            return false;
        }
    }

    public boolean setCyclic(int isCyclic) {
        if (isCyclic < 0 || isCyclic > 1) {
            Log.d("DEBUGGING", "Could not setCyclic, " + Integer.toString(isCyclic) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x14, isCyclic, "setCyclic")) {
            currentSettings.linearSweepCyclic = isCyclic;
            return true;
        } else {
            return false;
        }
    }

    public boolean setNumCycles(int numCycles) {
        if (numCycles < 1 || numCycles > 100) {
            Log.d("DEBUGGING", "Could not setNumCycles, " + Integer.toString(numCycles) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x15, numCycles, "setNumCycles")) {
            currentSettings.linearSweepNumCycles = numCycles;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPulseStartVoltage(int startVoltage) {
        if (startVoltage < -1650 || startVoltage > 1650) {
            Log.d("DEBUGGING", "Could not setPulseStartVoltage, " + Integer.toString(startVoltage) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x16, startVoltage, "setPulseStartVoltage")) {
            currentSettings.diffPulseStartVoltage = startVoltage;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPulseEndVoltage(int endVoltage) {
        if (endVoltage < -1650 || endVoltage > 1650) {
            Log.d("DEBUGGING", "Could not setPulseEndVoltage, " + Integer.toString(endVoltage) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x17, endVoltage, "setPulseEndVoltage")) {
            currentSettings.diffPulseEndVoltage = endVoltage;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPulseIncrement(int increment) {
        if (increment < 0 || increment > 1650) {
            Log.d("DEBUGGING", "Could not setPulseIncrement, " + Integer.toString(increment) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x18, increment, "setPulseIncrement")) {
            currentSettings.diffPulseIncrement = increment;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPulseVoltage(int pulseVoltage) {
        if (pulseVoltage < -1650 || pulseVoltage > 1650) {
            Log.d("DEBUGGING", "Could not setPulseVoltage, " + Integer.toString(pulseVoltage) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x19, pulseVoltage, "setPulseVoltage")) {
            currentSettings.diffPulsePulseVoltage = pulseVoltage ;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPrePulseTime(int prePulseTime) {
        if (prePulseTime < 1 || prePulseTime > 10000) {
            Log.d("DEBUGGING", "Could not setNumCycles, " + Integer.toString(prePulseTime) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x1A, prePulseTime, "setPrePulseTime")) {
            currentSettings.diffPulsePrePulseTime = prePulseTime;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPulseTime(int pulseTime) {
        if (pulseTime < 1 || pulseTime > 10000) {
            Log.d("DEBUGGING", "Could not setPulseTime, " + Integer.toString(pulseTime) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x1B, pulseTime, "setPulseTime")) {
            currentSettings.diffPulsePulseTime = pulseTime;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPulseSamplingWindow(int samplingWindow) {
        if (samplingWindow < 1 || samplingWindow > 10000) {
            Log.d("DEBUGGING", "Could not setSamplingWindow, " + Integer.toString(samplingWindow) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x1C, samplingWindow, "setSamplingWindow")) {
            currentSettings.diffPulseWindowWidth = samplingWindow;
            return true;
        } else {
            return false;
        }
    }

    public boolean setLowPassFilter(int filterSelection) {
        if (filterSelection < 0 || filterSelection > 7) {
            Log.d("DEBUGGING", "Could not setLowPassFilter, " + Integer.toString(filterSelection) + " is not in valid range");
            return false;
        }
        if (changeSetting((byte)0x22, filterSelection, "setLowPassFilter")) {
            currentSettings.selectedFilter = filterSelection;
            return true;
        } else {
            return false;
        }
    }

    public boolean changeSetting(byte setting, int value, String detail) {
        if (getMode() != 0) {
            Log.d("DEBUGGING", "Could not " + detail + ", board in wrong mode");
            return false;
        }

        byte[] unread_data = read();
        if (unread_data.length > 0) {
            Log.d("DEBUGGING", "Unread Data from " + detail + ": " + Arrays.toString(unread_data));
        }

        //TODO Probably would be better as a private class for each setting including command and num bytes
        int numBytes;
        switch (setting) {
            case ((byte)0x02): case ((byte)0x0B): case ((byte)0x0C): case ((byte)0x10):
            case ((byte)0x14): case ((byte)0x15): case ((byte)0x22):
                numBytes = 1;
                break;
            case ((byte)0x0D): case ((byte)0x0F):
                numBytes = 4;
                break;
            default:
                numBytes = 2;
        }

        byte[] value_bytes = getBytes(value, numBytes);
        /*
        if (setting == 0x12) {
            value_bytes = new byte[] {(byte)0xFE, (byte)0xD4};
        }
        if (setting == 0x11) {
            value_bytes = new byte[] {(byte)0x02, (byte)0x58};
        }
        if (setting == 0x13) {
            value_bytes = new byte[] {(byte)0x03, (byte)0x84};
        }
        if (setting == 0x0D) {
            value_bytes = new byte[] {(byte)0x00, (byte)0x00, (byte)0x03, (byte)0xE8};
        }
        if (setting == 0x0E) {
            value_bytes = new byte[] {(byte)0x02, (byte)0x58};
        }
        */
        byte[] setCommand = new byte[1+value_bytes.length];
        setCommand[0] = setting;
        for(int i=0; i<value_bytes.length; i++) {
            setCommand[i+1] = value_bytes[i];
        }

        int len = write(setCommand);
        if (len <= 0) {
            Log.d("DEBUGGING", detail + " command not written");
            return false;
        }

        byte[] raw_data = read();
        if(raw_data.length != 1) {
            Log.d("DEBUGGING", "Unexpected Length returned from " + detail + ": " + Arrays.toString(raw_data));
            return false;
        }
        if (raw_data[0] != 0) {
            Log.d("DEBUGGING", "Error in " + detail + ": " + Byte.toString(raw_data[0]));
        }

        return true;
    }

    public int write(byte[] writeBuf) {
        Log.d("DEBUGGING", "Writing: " + Arrays.toString(writeBuf));
        int len = deviceConnection.bulkTransfer(outPoint, writeBuf, writeBuf.length, 0);

        if (len <= 0) {
            Log.d("DEBUGGING", "Nothing was written");
        }
        return len;
    }

    public byte[] read() {
        byte[] readInBuf= new byte[2048];
        int len = deviceConnection.bulkTransfer(inPoint, readInBuf, readInBuf.length, 0);

        byte[] toReturnBuf = new byte[len-2];

        System.arraycopy(readInBuf, 2, toReturnBuf, 0, len - 2);


        return toReturnBuf;
    }

    private byte[] getBytes(int toConvert, int numBytes) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(toConvert).array();

        //converts int to byte array
        if (numBytes == 1) {
            return new byte[] {bytes[3]};
        }
        if (numBytes == 2) {
            return new byte[] {bytes[2], bytes[3]};
        }

        return new byte[] {bytes[0], bytes[1], bytes[2], bytes[3]};
    }

    private boolean claimInterface() {
        targetInterface = targetDevice.getInterface(0);
        if (targetInterface == null) {
            Log.d("DEBUGGING", "Could not get Interface");
            return false;
        }
        deviceConnection = usbManager.openDevice(targetDevice);
        if (deviceConnection == null) {
            Log.d("DEBUGGING", "Could not connect device");
            return false;
        }
        if(!deviceConnection.claimInterface(targetInterface, true)) {
            Log.d("DEBUGGING", "Could not claim interface");
            return false;
        }

        return true;
    }

    private boolean hasConnection() {
        return (deviceConnection != null);
    }

    private void closeConnection() {
        if(targetInterface != null) {
            deviceConnection.releaseInterface(targetInterface);
            targetInterface = null;
        }
        deviceConnection.close();
        deviceConnection = null;
        targetDevice = null;
    }

    public boolean checkDevices() {
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            Log.d("DEBUGGING", device.toString());

            if (device.getProductId() == PID && device.getVendorId() == VID) {
                targetDevice = device;
                return true;
            }
        }
        targetDevice = null;
        return false;
    }

    public boolean hasDevice() {
        return (targetDevice != null);
    }

    public boolean hasPermission() {
        if (!hasDevice()) {
            return false;
        }
        return usbManager.hasPermission(targetDevice);
    }

    private boolean setEndpoints() {
        inPoint = targetInterface.getEndpoint(0);
        outPoint = targetInterface.getEndpoint(1);

        return (inPoint != null && outPoint != null);
    }

    private boolean initCommunication() {
        deviceConnection.controlTransfer(0x40, 0, 0, 1, null, 0, 0); //reset
        deviceConnection.controlTransfer(0x40, 0, 1, 1, null, 0, 0); //clear rx
        deviceConnection.controlTransfer(0x40, 0, 2, 1, null, 0, 0); //clear tx
        deviceConnection.controlTransfer(0x40, 0x02, 0x0000, 1, null, 0, 0); //flow control none

        int baud = 230400;
        int baud_rate = calc(baud, 48000000);
        Log.d("DEBUGGING", Integer.toString(baud_rate));
        int baud_divisor = 0xD0;
        int index = baud_divisor >> 16;
        index |= 1;
        Log.d("DEBUGGING", "Index: " + Integer.toString(index));


        deviceConnection.controlTransfer(0x40, 0x03, baud_rate, index, null, 0, 0);

        deviceConnection.controlTransfer(0x40, 0x04, 0x0008, 1, null, 0, 0); //parity, data bits, etc

        return true;
    }

    private int calc(int baud, int base) {
        int divisor;
        divisor = (base / 16 / baud) | (((base / 2 / baud) & 4) != 0 ? 0x4000 // 0.5
                : ((base / 2 / baud) & 2) != 0 ? 0x8000 // 0.25
                : ((base / 2 / baud) & 1) != 0 ? 0xc000 // 0.125
                : 0);
        return divisor;
    }

    public void requestPermission(PendingIntent permissionIntent) {
        usbManager.requestPermission(targetDevice, permissionIntent);
    }
}
