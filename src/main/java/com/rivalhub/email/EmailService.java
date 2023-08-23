package com.rivalhub.email;

import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.exception.EmailNotSentException;
import com.rivalhub.organization.Organization;
import com.rivalhub.user.UserDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final ResourceLoader resourceLoader;
    private final InvitationHelper invitationHelper;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${app.frontUrl}")
    private String frontUrl;


    public void sendEmailWithInvitationToOrganization(String email, Organization organization){
        Context context = new Context();
        context.setVariable("username", email);
        context.setVariable("invitation", invitationHelper.createInvitationLink(organization));
        context.setVariable("organizationName", organization.getName());

        ServletUriComponentsBuilder uri = ServletUriComponentsBuilder.fromCurrentRequest();
        uri.replacePath("");
        String message = templateEngine.process("confirmOrganizationInvitation.html", context);
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
            helper.setTo(email);
            helper.setFrom(sender);
            helper.setSubject("Zaproszenie do " + organization.getName());
            helper.setText(message,true);

            Resource resource = resourceLoader.getResource("classpath:/templates/logo.png");
            helper.addInline("identifier1234", resource);
        } catch (MessagingException exception){
            throw new EmailNotSentException();
        }
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
        String message = templateEngine.process("confirmAccount.html", context);
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
            helper.setTo(user.getEmail());
            helper.setFrom(sender);
            helper.setSubject(subject);
            helper.setText(message,true);

            Resource resource = resourceLoader.getResource("classpath:/templates/logo.png");
            helper.addInline("identifier1234", resource);
        } catch (MessagingException exception){
            throw new EmailNotSentException();
        }
        javaMailSender.send(mailMessage);
    }

}
