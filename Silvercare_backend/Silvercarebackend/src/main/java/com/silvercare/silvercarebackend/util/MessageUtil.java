package com.silvercare.silvercarebackend.util;

import com.silvercare.silvercarebackend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSource messageSource;

    public String getMessage(String key, HttpServletRequest request) {
        Locale locale = RequestContextUtils.getLocale(request);
        return messageSource.getMessage(key, null, locale);
    }

    public String getMessageForUser(String key, User user) {
        Locale locale = switch (user.getLanguagePreference()) {
            case "zh" -> Locale.CHINESE;
            case "es" -> new Locale("es");
            case "vi" -> new Locale("vi");
            default -> Locale.ENGLISH;
        };
        return messageSource.getMessage(key, null, locale);
    }
}
