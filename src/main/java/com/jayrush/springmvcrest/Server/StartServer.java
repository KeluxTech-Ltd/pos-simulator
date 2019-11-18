package com.jayrush.springmvcrest.Server;

import com.jayrush.springmvcrest.CreateSocketServer;
import com.jayrush.springmvcrest.Repositories.UserRepository;
//import com.jayrush.springmvcrest.domain.exporttoExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.channels.AsynchronousServerSocketChannel;

@Component
public class StartServer implements CommandLineRunner { //command line starter simply says run me on startup

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        double inf = Double.POSITIVE_INFINITY;
        System.out.println("##############################################################################");
        System.out.println("***********************************3Line TMS**********************************");
        System.out.println("Awaiting Connection\n");
        //todo setup logs to file
        while (true) {
            try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open())
            {
                CreateSocketServer createSocketServer = new CreateSocketServer();
                applicationContext.getAutowireCapableBeanFactory().autowireBean(createSocketServer);
                createSocketServer.createSocket();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


}

