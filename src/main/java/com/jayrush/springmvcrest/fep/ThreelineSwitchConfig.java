//package com.jayrush.springmvcrest.fep;
//
//import integration._3line.events.NetworkEventListener;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.function.Consumer;
//
///**
// * @author JoyU
// * @date 12/18/2018
// */
//
//@Component
//public class ThreelineSwitchConfig {
//
//    private static final Logger logger = LoggerFactory.getLogger(ThreelineSwitchConfig.class);
//
//    @Value("${switch.host}")
//    private String switchHost;
//    @Value("${switch.port}")
//    private int switchPort;
//
//    private Socket socket;
//    private Consumer<byte[]> consumer;
//    private final ExecutorService executors = Executors.newFixedThreadPool(50);
//    private final Object disconnectLock = new Object();
//    private final Object retryConnectLock = new Object();
//    private final Object connectLock = new Object();
//    private static final int RECONNECT_WAIT_TIME = 1000;
//
//    @Autowired
//    private NetworkEventListener listener;
//
//
////    @PostConstruct
////    public void init() {
////        logger.info("The Threeline Connection");
////        executors.submit(() -> connect());
////        executors.submit(() -> read());
////    }
//
//    public void setResponseConsumer(Consumer<byte[]> consumer) {
//        this.consumer = consumer;
//    }
//
//    public void reset() {
//        closeSocket();
//    }
//
//    public void connect() {
//        while (true) {
//
//            while (isConnected()) {
//                waitForDisconnection();
//            }
//
//            try {
//
//                this.socket = new Socket();
//                this.socket.setTcpNoDelay(true);
//                this.socket.setKeepAlive(true);
//                this.socket.setSoLinger(true, 0);
//
//                try {
//                    logger.info("Attempting connection to server {}:{}", switchHost, switchPort);
//                    this.socket.connect(new InetSocketAddress(switchHost, switchPort));
//                    logger.info("Connected to server {}:{}", switchHost, switchPort);
//                } catch (IOException e) {
//                    closeSocket();
//                    logger.error(String.format("Could not connect to remote %s:%d", switchHost, switchPort), e);
//                    waitForRetryConnection();
//                }
//
//                if (isConnected()) {
//                    notifyConnect();
//                }
//
//            } catch (Exception e) {
//                logger.error("There was an error during connection", e);
//            }
//        }
//    }
//
//    public void read() {
//        while (true) {
//            try {
//
//                if (!isConnected()) {
//                    waitForConnection();
//                }
//
//                try {
//
//                    byte[] lenBytes = readToByteArray(socket.getInputStream(), 2);
//
//                    int len = decodeSignedLenBytes(lenBytes[0], lenBytes[1]);
//
//                    byte[] readData = readToByteArray(socket.getInputStream(), len);
//
//                    executors.submit(() -> consumer.accept(readData));
//
//                } catch (IOException e) {
//                    closeSocket();
//                    logger.error("There was an error reading data from socket", e);
//                }
//            } catch (Exception e) {
//                logger.error("There as an error in read process", e);
//            }
//        }
//    }
//
//    private byte[] readToByteArray(InputStream is, int len) throws IOException {
//        DataInputStream dataInputStream = new DataInputStream(is);
//        byte[] data = new byte[len];
//        dataInputStream.readFully(data);
//
//        return data;
//    }
//
//    private static int decodeSignedLenBytes(byte firstByte, byte secondByte) {
//        return ((firstByte & 255) << 8) + (secondByte & 255);
//    }
//
//    private boolean isConnected() {
//        if (this.socket == null) {
//            return false;
//        }
//
//        return this.socket.isConnected();
//    }
//
//    private void waitForDisconnection() {
//        synchronized (disconnectLock) {
//            try {
//                disconnectLock.wait();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    private void waitForConnection() {
//        synchronized (connectLock) {
//            try {
//                connectLock.wait();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    private void notifyConnect() {
//        synchronized (connectLock) {
//            connectLock.notifyAll();
//        }
//
//        listener.notifyConnectForThreeline();
//    }
//
//    private void waitForRetryConnection() {
//        synchronized (retryConnectLock) {
//            try {
//                retryConnectLock.wait(RECONNECT_WAIT_TIME);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    private synchronized void closeSocket() {
//        if (socket == null || !socket.isConnected()) {
//            return;
//        }
//
//        try {
//            socket.close();
//        } catch (IOException e) {
//            logger.error(String.format("There was an exception closing the client socket %s:%d", switchHost, switchPort), e);
//        } finally {
//            socket = null;
//            notifyDisconnect();
//        }
//    }
//
//    private void notifyDisconnect() {
//        synchronized (disconnectLock) {
//            disconnectLock.notifyAll();
//        }
//
//        listener.notifyDisconnect();
//    }
//
//    public void write(byte[] data) throws IOException {
//        if (socket == null || !socket.isConnected()) {
//            throw new IOException("Socket is not connected");
//        }
//
//        String log = String.format("writing to %s:%d %d bytes%n%s", switchHost, switchPort, data.length, new String(data));
//        logger.info(log);
//        socket.getOutputStream().write(data);
//    }
//
//}
