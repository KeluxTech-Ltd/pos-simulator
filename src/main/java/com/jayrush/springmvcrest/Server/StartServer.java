package com.jayrush.springmvcrest.Server;

import com.jayrush.springmvcrest.CreateSocketServer;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.channels.AsynchronousServerSocketChannel;

//import com.jayrush.springmvcrest.domain.exporttoExcel;

@Component
public class StartServer implements CommandLineRunner { //command line starter simply says run me on startup
    private static Logger logger = LoggerFactory.getLogger(StartServer.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        double inf = Double.POSITIVE_INFINITY;
        logger.info("##############################################################################");
        logger.info("***********************************3Line TMS**********************************");
        logger.info("Awaiting Connection\n");
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

