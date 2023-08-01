package com.rivalhub.email;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.AllArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;



    public void sendSimpleMessage(String receiver,String subject,String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setText(message);
        mailMessage.setTo(receiver);
        mailMessage.setFrom(sender);
        mailMessage.setSubject(subject);
        javaMailSender.send(mailMessage);
    }

    public void sendAttachmentMessage(String receiver,String subject,String messageBody, String pathToFile){

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(receiver);
            mimeMessageHelper.setSubject(subject);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(pathToFile));

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(messageBody);

            Multipart multipart = new MimeMultipart();

            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);
            javaMailSender.send(message);
        }catch(IOException e){

        }catch (MessagingException e){

        }
    }


}
