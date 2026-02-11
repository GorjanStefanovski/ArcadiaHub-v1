package com.example.ArcadiaHub_v1.EmailService;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;


    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(String to,String subject,String body){
        try{
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom("mkdgorjans@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
            System.out.println("EMAIL SENT SUCCESSFULLY");
        }catch (Exception e){
            System.out.println("ERROR SENDING MESSAGE "+e.getMessage());
        }
    }
}
