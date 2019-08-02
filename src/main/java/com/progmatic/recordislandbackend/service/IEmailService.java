package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.MailDTO;
import com.progmatic.recordislandbackend.exception.EmailTemplateNotFound;
import freemarker.template.TemplateException;
import java.io.IOException;
import javax.mail.MessagingException;
import org.springframework.mail.SimpleMailMessage;

public interface IEmailService {
    
    SimpleMailMessage constructSimpleEmailMessageForEmailAddressVerification(final User user, final String token);
    
    void sendEmail(MailDTO mailDTO, String templateName) throws MessagingException, IOException, TemplateException, EmailTemplateNotFound;

    void sendSimpleMessage(String to, String subject, String text);
    
    void sendSimpleMessage(SimpleMailMessage email);
    
    void sendNewsLetters();
    
    SimpleMailMessage constructResetTokenEmail(String token, User user);
}
