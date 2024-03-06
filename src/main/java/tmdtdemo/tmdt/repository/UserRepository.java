package tmdtdemo.tmdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tmdtdemo.tmdt.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByUsername(String username);
}
