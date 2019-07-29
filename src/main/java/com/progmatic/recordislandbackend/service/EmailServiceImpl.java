package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.controller.OnRegistrationCompleteEvent;
import com.progmatic.recordislandbackend.dao.EmailTemplateRepository;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.MailDTO;
import com.progmatic.recordislandbackend.exception.EmailTemplateNotFound;
import freemarker.cache.StringTemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Service
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender emailSender;

    private final EmailTemplateRepository emailTemplateRepository;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender, EmailTemplateRepository emailTemplateRepository1) {
        this.emailSender = emailSender;
        this.emailTemplateRepository = emailTemplateRepository1;
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendSimpleMessage(SimpleMailMessage email) {
        emailSender.send(email);
    }

    @Override
    public void sendEmail(MailDTO mailDTO, String templateName) throws MessagingException, IOException, TemplateException, EmailTemplateNotFound {
        Map model = new HashMap();
        model.put("username", mailDTO.getName());
        model.put("signature", "RecordIsland Team");
        model.put("confirmationUrl", mailDTO.getVerificationUrl() + mailDTO.getVerificationToken());

        mailDTO.setModel(model);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        String freemarkerTemplate = emailTemplateRepository.findByName(templateName)
                .orElseThrow(() -> new EmailTemplateNotFound(templateName + " (named) template does not exist!")).getTemplate();
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate(templateName, freemarkerTemplate);
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(stringLoader);
        Template template = cfg.getTemplate(templateName);

        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailDTO.getModel());

        mimeMessageHelper.setTo(mailDTO.getTo());
        mimeMessageHelper.setFrom(mailDTO.getFrom());
        mimeMessageHelper.setSubject(mailDTO.getSubject());
        mimeMessageHelper.setText(html, true);

        emailSender.send(message);
    }

    @Override
    public SimpleMailMessage constructSimpleEmailMessageForEmailAddressVerification(User user, String token) {
        final String recipientAddress = user.getEmail();
        final String subject = "Registration Confirmation";
        final String confirmationUrl = "http://localhost:4200/verify?token=" + token;
        final String message = "Successful registration!!!";
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom("record.islandhun@gmail.com");
        return email;
    }
}
