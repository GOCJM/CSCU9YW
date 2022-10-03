package welcome.repository;

import org.springframework.data.repository.CrudRepository;
import welcome.model.Welcome;

public interface WelcomeRepository extends CrudRepository<Welcome, String> {
    Welcome getWelcomeByLang(String lang);
}
