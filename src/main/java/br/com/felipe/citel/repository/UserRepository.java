package br.com.felipe.citel.repository;

import br.com.felipe.citel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
