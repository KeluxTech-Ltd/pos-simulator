//package com.jayrush.springmvcrest.email;
//
//import com.jayrush.springmvcrest.email.config.EmailCfg;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.MailException;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.security.core.parameters.P;
//import org.springframework.stereotype.Service;
//
///**
// * @author JoshuaO
// */
//
//@Service
//public class MailServiceImpl implements MailService {
//    @Value("${spring.mail.host}")
//    private String Host;
//    @Value("${spring.mail.port}")
//    private int Port;
//    @Value("${spring.mail.username}")
//    private String Username;
//    @Value("${spring.mail.password}")
//    private String Password;
//    @Value("${spring.mail.protocol}")
//    private String Protocol;
//    @Value("${spring.mail.default-encoding}")
//    private String DefaultEncoding;
//
//    @Autowired
//    JavaMailSenderImpl mailSender;
//
//    @Override
//    public void SendMail(String email, String body) {
//        mailSender.setHost(Host);
//        mailSender.setPort(Port);
//        mailSender.setUsername(Username);
//        mailSender.setPassword(Password);
//        mailSender.setProtocol(Protocol);
//        mailSender.setDefaultEncoding(DefaultEncoding);
//
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom("no-reply");
//        mailMessage.setTo(email);
//        mailMessage.setSubject("Terminal Management System");
//        mailMessage.setText(body);
//        mailMessage.setReplyTo("no-reply");
//
//        try {
//            mailSender.send(mailMessage);
//            System.out.println("Email Sent to "+email);
//        } catch (MailException e) {
//            e.printStackTrace();
//        }
//    }
//}
