// Implementation of the business logic, living in the service sub-package.
// Discoverable for auto-configuration, thanks to the @Component annotation.

package welcome.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import welcome.model.Welcome;
import welcome.repository.WelcomeRepository;

import java.util.*;

@Component
public class WelcomeServiceImpl2 implements WelcomeService {

    // Very simple in-memory database; key is the lang field of class Welcome.
    // We have to be careful with this 'database'. In order to avoid objects
    // in the database being mutated accidentally, we must always copy objects
    // before insertion and retrieval.
    @Autowired
    private WelcomeRepository db;

    public WelcomeServiceImpl2(WelcomeRepository repository) {
        db = repository;
    }

    // Adds a welcome to the database, or overwrites an existing one.
    public void addWelcome(Welcome welcome) {
        if (welcome != null && welcome.getLang() != null) {
            db.save(welcome);
        }
    }

    // Returns a welcome in language lang, personalised to name.
    // Parameter lang must not be null, but name may be null.
    public Welcome getWelcome(String lang, String name) {
        Welcome welcome = db.getWelcomeByLang(lang);
        if (welcome == null) {
            return null;
        }
        if (name != null) {
            welcome.setMsg(welcome.getMsg() + ", " + name);
        }
        return welcome;
    }

    public List<Welcome> getAllWelcomes() {
        return new ArrayList<>((Collection) db.findAll());
    }

    public void removeWelcome(String lang) {
        if (lang != null) {
            db.deleteById(lang);
        }
    }

}
