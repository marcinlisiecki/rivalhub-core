package com.rivalhub.email;

import com.rivalhub.user.UserDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Value("${app.frontUrl}")
    private String frontUrl;


    public void sendSimpleMessage(String receiver,String subject,String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setText(message);
        mailMessage.setTo(receiver);
        mailMessage.setFrom(sender);
        mailMessage.setSubject(subject);
        javaMailSender.send(mailMessage);
    }

    public void sendThymeleafInvitation(UserDto user, String subject)  {

        Context context = new Context();
        context.setVariable("username",user.getName());
        ServletUriComponentsBuilder uri = ServletUriComponentsBuilder.fromCurrentRequest();
        uri.replacePath("");
        StringBuilder builder = new StringBuilder();
        builder.append(frontUrl)
                .append("/users/confirm/")
                .append(user.getActivationHash());
        context.setVariable("activationLink",builder.toString());
        String message = templateEngine.process("welcome.html", context);
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
            helper.setTo(user.getEmail());
            helper.setFrom(sender);
            helper.setSubject(subject);
            helper.setText(message,true);
            FileSystemResource res = new FileSystemResource(new File("src/main/resources/templates/logo.png"));
            helper.addInline("identifier1234", res);
        } catch (MessagingException exception){
            throw new EmailNotSentException();
        }
        javaMailSender.send(mailMessage);
    }

}
