package dev.manuthlakdiw.primebasketbackend.entity;

import dev.manuthlakdiw.primebasketbackend.entity.types.MailStatusType;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "email_logs")
public class EmailLogEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(columnDefinition = "text")
    private String body;

    @Enumerated(EnumType.STRING)
    private MailStatusType status;

    @Lob
    @Column(columnDefinition = "text")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;


}
