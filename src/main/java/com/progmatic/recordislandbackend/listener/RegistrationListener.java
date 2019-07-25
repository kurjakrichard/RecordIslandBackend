package com.progmatic.recordislandbackend.listener;

import com.progmatic.recordislandbackend.controller.OnRegistrationCompleteEvent;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.service.UserService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private JavaMailSender mailSender;

    private UserService userService;

    @Autowired
    public RegistrationListener(JavaMailSender mailSender, UserService userService) {
        this.mailSender = mailSender;
        this.userService = userService;
    }
    
    //private MessageSource messages;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);

        final SimpleMailMessage email = constructEmailMessage(event, user, token);
        mailSender.send(email);
    }
    
     private final SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final User user, final String token) {
        final String recipientAddress = user.getEmail();
        final String subject = "Registration Confirmation";
        final String confirmationUrl = "http://localhost:4200/verify?token=" + token;
        //final String message = messages.getMessage("message.regSucc", null, event.getLocale());
        final String message = "Successful registration!!!";
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom("record.island@protonmail.com");
        //email.setFrom(env.getProperty("support.email"));
        return email;
    }

}
