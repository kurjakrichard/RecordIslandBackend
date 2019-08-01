package com.progmatic.recordislandbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import com.progmatic.recordislandbackend.service.UserService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MySavedRequestAwareAuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private Logger logger = LoggerFactory.getLogger(MySavedRequestAwareAuthenticationSuccessHandler.class);
    
    
    @Autowired
    UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws ServletException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        
        Map<String, Object> userNameResponse = new HashMap<>();
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        User user = (User) userService.loadUserByUsername(authentication.getName());
        userNameResponse.put("username", user.getUsername());
        userNameResponse.put("email", user.getEmail());
        userNameResponse.put("lastFmUsername", user.getLastFmAccountName());
        userNameResponse.put("newsLetter", user.isHasNewsLetterSubscription());
       
        try {
            userService.updateLastLoginDate(authentication.getName());
        } catch (UserNotFoundException ex) {
            logger.error(ex.getMessage());
        }
        
        response.getWriter().write(objectMapper.writeValueAsString(userNameResponse));
        response.getWriter().flush();
        response.getWriter().close();

        SavedRequest savedRequest
                = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            clearAuthenticationAttributes(request);
            return;
        }
        String targetUrlParam = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl()
                || (targetUrlParam != null
                && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }

        clearAuthenticationAttributes(request);
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}
