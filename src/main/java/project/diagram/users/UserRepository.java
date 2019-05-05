package project.diagram.users;

import org.springframework.data.mongodb.repository.MongoRepository;
import project.diagram.security.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
}

