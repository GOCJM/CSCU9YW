// Implementation of the business logic, living in the service sub-package.
// Discoverable for auto-configuration, thanks to the @Component annotation.

package welcome.service;

import org.springframework.stereotype.Component;
import welcome.model.Welcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WelcomeServiceImpl2 implements WelcomeService {

    // Very simple in-memory database; key is the lang field of class Welcome.
    // We have to be careful with this 'database'. In order to avoid objects
    // in the database being mutated accidentally, we must always copy objects
    // before insertion and retrieval.
    private Map<String, Welcome> db;

    public WelcomeServiceImpl2() {
        db = new HashMap<>();
    }

    // Adds a welcome to the database, or overwrites an existing one.
    public void addWelcome(Welcome welcome) {
        if (welcome != null && welcome.getLang() != null) {
            // copying welcome to isolate objects in the database from changes
            welcome = new Welcome(welcome);
            db.put(welcome.getLang(), welcome);
        }
    }

    // Returns a welcome in language lang, personalised to name.
    // Parameter lang must not be null, but name may be null.
    public Welcome getWelcome(String lang, String name) {
        Welcome welcome = db.get(lang);
        if (welcome == null) {
            return null;
        }
        // copying welcome to protect objects in the database from changes
        welcome = new Welcome(welcome);
        if (name != null) {
            welcome.setMsg(welcome.getMsg() + ", " + name);
        }
        return welcome;
    }

    public List<Welcome> getAllWelcomes() {
        return new ArrayList<>(db.values());
    }

    public void removeWelcome(String lang) {
        if (lang != null) {
            db.remove(lang);
        }
    }

}
