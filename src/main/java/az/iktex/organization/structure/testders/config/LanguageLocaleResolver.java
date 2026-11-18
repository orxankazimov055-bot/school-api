package az.iktex.organization.structure.testders.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Component("localeResolver")
public class LanguageLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {

        String lang = request.getParameter("lang");

        if (lang == null || lang.isBlank()) {
            lang = request.getHeader("Accept-Language");
        }

        if (lang == null || lang.isBlank()) {
            return Locale.forLanguageTag("az");
        }

        return switch (lang.toLowerCase(Locale.ROOT)) {

            case "en", "eng", "english" ->
                    Locale.ENGLISH;

            case "ru", "rus", "russian" ->
                    Locale.forLanguageTag("ru");

            case "az", "aze", "azərbaycan" ->
                    Locale.forLanguageTag("az");

            default ->
                    Locale.forLanguageTag("az");
        };
    }

    @Override
    public void setLocale(
            HttpServletRequest request,
            HttpServletResponse response,
            Locale locale
    ) {
        // Dil her sorguda yeniden teyin olunur.
    }
}