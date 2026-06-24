package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Repository
public interface CartRepository  extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUserEmail(String email);
}
