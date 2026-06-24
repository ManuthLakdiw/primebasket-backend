package dev.manuthlakdiw.primebasketbackend.repository;

import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.projection.UserSecurityProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u.password AS password, u.email AS email, u.role AS role, u.isActivated AS activated FROM UserEntity u WHERE u.email = :email")
    Optional<UserSecurityProjection> findSecuredUserByEmail(String email);

    Optional<UserEntity> findUserEntityByEmail(String email);

    boolean existsByTelephone(String telephone);

    Optional<UserEntity> findByEmail(String email);
}
