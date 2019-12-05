//package com.jayrush.springmvcrest.fep;
//
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//import java.io.BufferedInputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//
//@Component
//public class CreateSocketServer {
//    private ApplicationContext applicationContext;
//
//    public  void createSocket()
//    {
//        // server is listening on port 5891
//        ServerSocket ss = null;
//        try {
//            ss = new ServerSocket(5891);
//
//
//        // running infinite loop for getting
//        // client request
//        while (true)
//        {
//            Socket s = null;
//
//            try
//            {
//                // socket object to receive incoming client requests
//                s = ss.accept();
//
//                System.out.println("A new client is connected : " + s);
//
//                // obtaining input and out streams
//                DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
//                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//
//                System.out.println("Assigning new thread for this client");
//
//////                // create a new thread object
////                Thread t = new ClientHandler(s, dis, dos);
////
////                // Invoking the start() method
////                t.start();
//                Thread clientHandler=new ClientHandler(s,dis,dos);
//                applicationContext.getAutowireCapableBeanFactory().autowireBean(clientHandler);
//                clientHandler.start();
//
//            }
//
//            catch (Exception e){
//                s.close();
//                e.printStackTrace();
//            }
//        }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
