package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.config.RecordIslandProperties;
import com.progmatic.recordislandbackend.dao.EmailTemplateRepository;
import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.MailDTO;
import com.progmatic.recordislandbackend.exception.EmailSendingException;
import com.progmatic.recordislandbackend.exception.EmailTemplateNotFound;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender emailSender;
    private final EmailTemplateRepository emailTemplateRepository;
    private final RecordIslandProperties recordIslandProperties;
    private final RecommendationsServiceImpl recommendationsServiceImpl;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender, EmailTemplateRepository emailTemplateRepository,
            RecordIslandProperties recordIslandProperties, RecommendationsServiceImpl recommendationsServiceImpl,
            UserService userService) {
        this.emailSender = emailSender;
        this.emailTemplateRepository = emailTemplateRepository;
        this.recordIslandProperties = recordIslandProperties;
        this.recommendationsServiceImpl = recommendationsServiceImpl;
        this.userService = userService;
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
        final String confirmationUrl = recordIslandProperties.getFrontend() + "/verify?token=" + token;
        final String message = "Successful registration!!!";
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom(recordIslandProperties.getOwnEmail());
        return email;
    }

    @Override
    @Scheduled(cron = "0 00 02 ? * FRI")
    public void sendNewsLetters() {
        String templateName = "newsLetterEmail";
        List<User> users = userService.getAllUserWithNewsLetterSubscription();

        for (User user : users) {
            try {
                recommendationsServiceImpl.updateUsersAlbumRecommendations(user);
            } catch (UserNotFoundException ex) {
                logger.error(ex.getMessage());
            }
            Set<Album> recommendedAlbums = user.getAlbumRecommendations();
            List<Album> collectedAlbums = recommendedAlbums.stream().limit(6).collect(Collectors.toList());

            MailDTO mailDTO = new MailDTO(recordIslandProperties.getOwnEmail(), user.getEmail(), user.getUsername(), "Weekly News");
            Map model = new HashMap();
            model.put("username", mailDTO.getName());
            model.put("signature", "RecordIsland Team");
            model.put("recommendations", collectedAlbums);
            mailDTO.setModel(model);

            try {
                sendEmail(mailDTO, templateName);
            } catch (MessagingException | IOException | TemplateException | EmailTemplateNotFound ex) {
                logger.error(ex.getMessage());
                throw new EmailSendingException(ex.getMessage());
            }
        }
    }

    public SimpleMailMessage constructResetTokenEmail(String token, User user) {
        String url = recordIslandProperties.getFrontend() + "/api/changePassword?id="
                + user.getId() + "&token=" + token;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Password Reset");
        email.setText("Link for password reset:" + url);
        email.setTo(user.getEmail());
        email.setFrom(recordIslandProperties.getOwnEmail());
        return email;
    }
}
