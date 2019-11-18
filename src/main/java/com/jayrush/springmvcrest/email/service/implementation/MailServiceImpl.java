//package com.jayrush.springmvcrest.email.service.implementation;
//
//
//import com.jayrush.springmvcrest.email.model.Mail;
//import com.jayrush.springmvcrest.email.repository.MailRepository;
//import com.jayrush.springmvcrest.email.service.MailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.MailException;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.mail.javamail.MimeMessagePreparator;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.context.IContext;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Service
//public class MailServiceImpl implements MailService {
//    private String mailFrom = "joshua.omonigho@3lineng.com";
//
//    private MailRepository mailRepository;
//
//
//    private TemplateEngine templateEngine;
//
//    private JavaMailSender emailSender;
//
//    public MailServiceImpl() {
//    }
//
//
//    @Override
//    public void sendMail(String mailSubject, String mailTo, String[] copy, Map<String, Object> parameters, String templateLocation, String Name) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.submit(new Runnable() {
//
//            @Override
//            public void run() {
//                Mail mail = new Mail();
//                if (Objects.nonNull(copy)) mail.setCopy(Arrays.asList(copy));
//                mail.setMailHeader(mailSubject);
//                mail.setMailTo(mailTo);
//                mail.setTmsAdminName(Name);
//                IContext context = new Context();
//                ((Context) context).setVariables(parameters);
//                mail.setMailContent(templateEngine.process("mail/" + templateLocation, context));
//                System.out.println("MAIL -> "+ mail.toString());
//                System.out.println("Sending mail to ->"+ mail.getMailTo());
//                MimeMessagePreparator messagePreparator = mimeMessage -> {
//                    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
//                    messageHelper.setFrom(mailFrom);
//                    messageHelper.setTo(mailTo);
//                    messageHelper.setSubject(mailSubject);
//                    messageHelper.setText(mail.getMailContent(), true);
//                };
//                try{
//                    mail.setLastSent(new Date());
//                    emailSender.send(messagePreparator);
//                    System.out.println("Mail Sent to "+ mail.getTmsAdminName());
//                } catch (MailException e) {
//                    System.out.println("Error send mail "+e);
//                }
//                mailRepository.save(mail);
//
//
//            }
//        });
//    }
//
//    @Override
//    public void sendMailAttachments(String mailSubject, String mailTo, String[] copy, Map<String, Object> parameters, String templateLocation, Map<String, Objects> attachements) {
//
//    }
//}
