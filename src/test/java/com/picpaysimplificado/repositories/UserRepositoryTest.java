package com.picpaysimplificado.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Should return User successfully from database")
    void findUserBydocumentCase1(){
        String document = "999999999-01";
        UserDTO data = new UserDTO("Maria", "Silva", document, new BigDecimal(10), "maria@email.com", "123", UserType.COMMON);
        this.createUser(data);

        Optional <User> foundUser = this.userRepository.findUserByDocument(document);

        assertThat(foundUser.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not get User from database when user not exists")
    void findUserBydocumentCase2(){
        String document = "999999999-01";
        UserDTO data = new UserDTO("Maria", "Silva", document, new BigDecimal(10), "maria@email.com", "123", UserType.COMMON);

        Optional <User> foundUser = this.userRepository.findUserByDocument(document);

        assertThat(foundUser.isEmpty()).isTrue();
    }

    private User createUser(UserDTO data){
        User newUser = new User(data);
        this.entityManager.persist(newUser);
        return newUser;
    }
}
