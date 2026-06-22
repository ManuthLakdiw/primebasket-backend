package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.entity.PasskeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Repository
public interface PasskeyRepository extends JpaRepository<PasskeyEntity, UUID> {

    Optional<PasskeyEntity> findByCredentialId(String credentialId);

    List<PasskeyEntity> findByUserId(UUID userId);
}
