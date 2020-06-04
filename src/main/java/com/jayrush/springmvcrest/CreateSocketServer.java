package com.jayrush.springmvcrest;



import com.jayrush.springmvcrest.Nibss.network.ChannelSocketRequestManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
@PropertySource("classpath:application.properties")
public class CreateSocketServer {
    @Value("${tms-socket-port}")
    private int port;
    @Autowired
    private ApplicationContext applicationContext;
    private static Logger logger = LoggerFactory.getLogger(CreateSocketServer.class);

    //method to create socket
    public  void createSocket()
    {
        ServerSocket serverSocket=null;
        try
        {
            serverSocket = new ServerSocket(port);
        // running infinite loop for getting

        // client request
        while (true)
        {
            Socket socketInstance = new Socket();
//            socketInstance.setSoTimeout(120000);
            try
            {
                // socket object to receive incoming client requests
                socketInstance = serverSocket.accept();
                logger.info("Client is connected on : {}" , socketInstance);

                // obtaining input and out streams
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socketInstance.getInputStream()));
                DataOutputStream outputStream = new DataOutputStream(socketInstance.getOutputStream());

                logger.info("Assigning new thread for this client");

////            // create a new thread object
                ChannelSocketRequestManager socketRequestManager=new ChannelSocketRequestManager();
                applicationContext.getAutowireCapableBeanFactory().autowireBean(socketRequestManager);

                Thread clientHandler=new ClientHandler(socketInstance,inputStream,outputStream);

                applicationContext.getAutowireCapableBeanFactory().autowireBean(clientHandler);
                clientHandler.start();
//                clientHandler.stop();


            }

            catch (Exception e){
                socketInstance.close();
                e.printStackTrace();
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

