package com.progmatic.recordislandbackend.listener;

import com.progmatic.recordislandbackend.config.RecordIslandProperties;
import com.progmatic.recordislandbackend.controller.OnRegistrationCompleteEvent;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.MailDTO;
import com.progmatic.recordislandbackend.exception.EmailSendingException;
import com.progmatic.recordislandbackend.exception.EmailTemplateNotFound;
import com.progmatic.recordislandbackend.service.IEmailService;
import com.progmatic.recordislandbackend.service.UserService;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final IEmailService emailService;
    private final UserService userService;
    private final RecordIslandProperties recordIslandProperties;

    private Logger logger = LoggerFactory.getLogger(RegistrationListener.class);

    @Autowired
    public RegistrationListener(IEmailService emailService, UserService userService,
            RecordIslandProperties recordIslandProperties) {
        this.emailService = emailService;
        this.userService = userService;
        this.recordIslandProperties = recordIslandProperties;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);

        MailDTO mailDTO = new MailDTO(recordIslandProperties.getOwnEmail(), user.getEmail(), user.getUsername(), "Registration Confirmation", token);
        mailDTO.setVerificationUrl(recordIslandProperties.getFrontend() + "/verify?token=");
        Map model = new HashMap();
        model.put("username", mailDTO.getName());
        model.put("signature", "RecordIsland Team");
        model.put("confirmationUrl", mailDTO.getVerificationUrl() + mailDTO.getVerificationToken());

        mailDTO.setModel(model);
        
        try {
            emailService.sendEmail(mailDTO, "verificationEmail");
        } catch (MessagingException ex) {
            logger.error(ex.getMessage());
            throw new EmailSendingException(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            throw new EmailSendingException(ex.getMessage());
        } catch (TemplateException ex) {
            logger.error(ex.getMessage());
            throw new EmailSendingException(ex.getMessage());
        } catch (EmailTemplateNotFound ex) {
            logger.error(ex.getMessage());
            throw new EmailSendingException(ex.getMessage());
        }
    }

    private void simpleEmailSend(User user, String token) {
        final SimpleMailMessage email = emailService.constructSimpleEmailMessageForEmailAddressVerification(user, token);
        emailService.sendSimpleMessage(email);
    }
}
