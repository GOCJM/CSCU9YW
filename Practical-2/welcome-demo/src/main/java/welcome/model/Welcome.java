// A resource, living the model sub-package.
// Implemented as POJO (plain old Java object) in Bean style, that is,
// with an argument-less constructor and getters/setters for all fields.

package welcome.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

public class Welcome {

    private String lang;
    private String msg;

    public Welcome(String lang, String name) {
        handleParameters(lang,name);
    }

    private void handleParameters(String lang, String name) {
        HashMap<String, String> languages = new HashMap<>(Map.of(
                "en", "Welcome",
                "fr", "Bienvenue",
                "de", "Willkommen"));

        boolean isNameValid = name != null && !name.trim().isEmpty();

        // Default to English if language is unknown and throw error
        if (!languages.containsKey(lang)) {
            this.lang = "en";
            String ERROR_UNKNOWN_LANGUAGE = "Sorry, I do not speak your language";
            this.msg = isNameValid ? String.format("%s, %s!", ERROR_UNKNOWN_LANGUAGE,name) : ERROR_UNKNOWN_LANGUAGE;
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,String.format("Error: '%s' is not supported.\n%s",lang,this.msg));
        } else {
            this.lang = lang;
            String greeting = languages.get(lang);
            this.msg = isNameValid ? String.format("%s, %s!",greeting, name) : String.format("%s!",greeting);
        }
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Welcome{" +
                "lang=\"" + lang + "\"," +
                "msg=\"" + msg + "\"}";
    }

}
