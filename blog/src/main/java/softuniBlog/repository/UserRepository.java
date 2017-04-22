package softuniBlog.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.User;

import javax.jws.soap.SOAPBinding;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);


}