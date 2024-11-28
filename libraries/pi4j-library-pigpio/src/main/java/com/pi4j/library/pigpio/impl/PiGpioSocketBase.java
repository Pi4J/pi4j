package com.pi4j.library.pigpio.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  PiGpioSocketBase.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import static com.pi4j.library.pigpio.PiGpioConst.DEFAULT_HOST;
import static com.pi4j.library.pigpio.PiGpioConst.DEFAULT_PORT;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.library.pigpio.PiGpioCmd;
import com.pi4j.library.pigpio.PiGpioException;
import com.pi4j.library.pigpio.PiGpioPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Abstract PiGpioSocketBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public abstract class PiGpioSocketBase extends PiGpioBase implements PiGpio {

    private static final Logger logger = LoggerFactory.getLogger(PiGpioSocketBase.class);

    protected final PiGpioSocketMonitor monitor;

    protected String host = DEFAULT_HOST;
    protected int port = DEFAULT_PORT;
    protected boolean connected = false;
    protected Socket socket = null;

    // TODO :: IMPLEMENT CONNECTION MONITOR TO PROACTIVELY DETECT SOCKET DISCONNECTS AND AUTO-RETRY TO CONNECT IN BACKGROUND THREAD

    /**
     * ALTERNATE CONSTRUCTOR
     * <p>
     * Connects to a user specified socket hostname/ip address and port.
     *
     * @param host hostname or IP address of the RaspberryPi to connect to via TCP/IP socket.
     * @param port TCP port number of the RaspberryPi to connect to via TCP/IP socket.
     */
    protected PiGpioSocketBase(String host, int port) {
        this.host = host;
        this.port = port;
        this.connected = false;
        this.initialized = false;
        this.monitor = new PiGpioSocketMonitor(this);
    }

    /**
     * {@inheritDoc}
     *
     * Initializes the library.
     * (The Java implementation of this function does not return a value)
     * <p>
     * gpioInitialise must be called before using the other library functions with the following exceptions:
     * - gpioCfg*
     * - gpioVersion
     * - gpioHardwareRevision
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioInitialise">PIGPIO::gpioInitialise</a>
     */
    @Override
    public int gpioInitialise() {
        int result = 0;
        logger.trace("[INITIALIZE] -> STARTED");
        if(!this.initialized) {
//            // add a shutdown hook to perform any required clean up actions
//            // when this library is instructed to shutdown
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                try {
//                    // properly terminate this library
//                    shutdown();
//                } catch (Exception e) {
//                    logger.warn(e.getMessage() ,e);
//                }
//            }, "pigpio-shutdown"));
//
//            // set initialized flag
            this.initialized = true;
            result  = gpioVersion();
            logger.debug("[INITIALIZE] -- INITIALIZED SUCCESSFULLY");
        }
        else{
            logger.warn("[INITIALIZE] -- ALREADY INITIALIZED");
        }
        logger.trace("[INITIALIZE] <- FINISHED");
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * Shutdown the library.
     * <p>
     * Returns nothing.
     * Call before program exit.
     * This function resets the used DMA channels, releases memory, and terminates any running threads.
     */
    @Override
    public void gpioTerminate() {
        logger.trace("[SHUTDOWN] -> STARTED");
        if(this.initialized) {
            // close all open SPI, SERIAL, I2C handles
            closeAllOpenHandles();

            // shutdown GPIO notifications monitor
            if(monitor != null && monitor.isConnected())
                monitor.shutdown();
        }

        // shutdown connected socket
        if(socket != null && socket.isConnected())
            try {
                socket.close();
            }
            catch (IOException e) {
                throw new PiGpioException(e);
            }

        // clear initialized flag
        this.initialized = false;
        logger.trace("[SHUTDOWN] <- FINISHED");
    }

    /**
     * <p>sendCommand.</p>
     *
     * @param cmd a {@link com.pi4j.library.pigpio.PiGpioCmd} object.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    protected PiGpioPacket sendCommand(PiGpioCmd cmd) {
        return sendPacket(new PiGpioPacket(cmd));
    }

    /**
     * <p>sendCommand.</p>
     *
     * @param cmd a {@link com.pi4j.library.pigpio.PiGpioCmd} object.
     * @param p1 a int.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    protected PiGpioPacket sendCommand(PiGpioCmd cmd, int p1) {
        return sendPacket(new PiGpioPacket(cmd, p1));
    }
    /**
     * <p>sendCommand.</p>
     *
     * @param cmd a {@link com.pi4j.library.pigpio.PiGpioCmd} object.
     * @param p1 a int.
     * @param p2 a int.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    protected PiGpioPacket sendCommand(PiGpioCmd cmd, int p1, int p2) {
        return sendPacket(new PiGpioPacket(cmd, p1, p2));
    }
    /**
     * <p>sendPacket.</p>
     *
     * @param tx a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    protected PiGpioPacket sendPacket(PiGpioPacket tx) {
        validateReady();
        return sendPacket(tx, this.socket);
    }
    /**
     * <p>sendPacket.</p>
     *
     * @param tx a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     * @param sck a {@link java.net.Socket} object.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    protected PiGpioPacket sendPacket(PiGpioPacket tx, Socket sck) {
        try {
            try {
                // get socket streams
                var in = sck.getInputStream();
                var out = sck.getOutputStream();

                // transmit packet
                logger.trace("[TX] -> {}", tx.toString());
                out.write(PiGpioPacket.encode(tx));
                out.flush();

                // read receive packet
                PiGpioPacket rx = PiGpioPacket.decode(in);
                logger.trace("[RX] <- {}", rx.toString());
                return rx;
            } catch (SocketException se) {
                // socket is no longer connected
                this.connected = false;
                socket.close();
                socket = null;
                throw new PiGpioException(se);
            }
        } catch (IOException e) {
            throw new PiGpioException(e);
        }
    }

    /** {@inheritDoc} */
    public void gpioNotifications(int pin, boolean enabled){
        logger.trace("[GPIO] -> {} Pin [{}] Notifications", (enabled ? "ENABLE" : "DISABLE"), pin);
        validateReady();
        this.monitor.enable(pin, enabled);
        logger.trace("[GPIO] <- Pin [PIN {}] Notifications [{}]", pin, (enabled ? "ENABLED" : "DISABLED"));
    }

    /**
     * <p>disableNotifications.</p>
     */
    protected void disableNotifications() {
        logger.trace("[GPIO] -> DISABLE ALL Pin Notifications");
        validateReady();
        this.monitor.disable();
        logger.trace("[GPIO] <- All Pin Notifications are DISABLED");
    }

    /**
     * <p>validateReady.</p>
     */
    @Override
    protected void validateReady() {
        super.validateReady();
        validateConnection();
    }

    /**
     * <p>validateConnection.</p>
     */
    protected void validateConnection() {
        // if not connected, attempt to reconnect
        if(socket == null || !this.connected){
            // attempt to connect to PiGpio Daemon on remote Raspberry Pi
            try {
                this.socket = new Socket(host, port);
                this.socket.setSoTimeout(500);
            } catch (IOException e) {
                throw new PiGpioException(e);
            }

            // update connection status flag
            this.connected = this.socket.isConnected();
        }
//            throw new IOException("PIGPIO NOT CONNECTED TO REMOTE HOST [" + this.host + ":" + this.port +
//                    "]; make sure the PiGpio Daemon is running on the remote Raspberry Pi and the host is accessible.");
    }

//    protected void enableNotifications() {
////        PiGpioPacket noib = new PiGpioPacket(NOIB);
//        var listener = new Socket(this.host, this.port);
//
//        //listener.getOutputStream().write(PiGpioPacket.encode(noib));
//
//        // get the current pin states for pins 0-31
//        PiGpioPacket tx = new PiGpioPacket(BR1);
//        PiGpioPacket rx = sendPacket(tx);
//        pinState = rx.p3();
//
//        ByteBuffer b = ByteBuffer.allocate(4);
//        b.order(ByteOrder.LITTLE_ENDIAN);
//        b.putInt(rx.p3());
//        logger.info("[STATES] [" + Integer.toBinaryString(rx.p3()) + "]");
//
//        tx = new PiGpioPacket(NOIB);
//        rx = sendPacket(tx, listener);
//
//        tx = new PiGpioPacket(NB, rx.p3(), pinMonitor);
//        sendPacket(tx, listener);
//
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    var in = listener.getInputStream();
//                    logger.info("START MONITOR");
//                    while(initialized && pinMonitor != 0) {
//                        var available = in.available();
//                        if(in.available() >= 12) {
//                            byte[] raw = in.readNBytes(12);
//                            ByteBuffer buffer = ByteBuffer.wrap(raw);
//                            buffer.order(ByteOrder.LITTLE_ENDIAN);
//
//                            final long sequence = Integer.toUnsignedLong(buffer.getShort());
//                            final long flags = Integer.toUnsignedLong(buffer.getShort());
//                            final long tick = Integer.toUnsignedLong(buffer.getInt());
//                            final int level = buffer.getInt();
//                            final BitSet levels =  BitSet.valueOf(buffer.array());
//
//
//                            logger.info("[BYTES] [0x" + StringUtil.toHexString(buffer) + "]");
//                            logger.info("[SEQUE] " + sequence);
//                            logger.info("[FLAGS] " + flags);
//                            logger.info("[TICK ] " + tick);
//                            logger.info("[LEVEL] " + Integer.toBinaryString(level));
//                            logger.info("[STATE] " + BitSet.valueOf(raw).get(0));
//                            logger.info("--------------------------------------------------------");
//
//
//                            for (int i = 0; i < 32; i++) {
//
//                                int oldBit = (pinState >> i) & 1;
//                                int newBit = (level >> i) & 1;
//
//                                if(oldBit != newBit){
//                                    logger.info("[BIT" + i + "] " + oldBit + " > " + newBit);
//                                    final PiGpioState state = PiGpioState.from(newBit);
//                                    final PiGpioStateChangeEvent event = new PiGpioStateChangeEvent(i, state, sequence, flags, tick);
//                                    dispatchEvent(event);
//                                }
//                            }
//
//                            // sync last known states
//                            pinState = level;
//                        }
//                    }
//                    listener.close();
//                    logger.info("STOP MONITOR");
//                } catch (IOException e) {
//                    logger.error(e.getMessage(), e);
//                }
//            }
//        }.start();
//    }

}