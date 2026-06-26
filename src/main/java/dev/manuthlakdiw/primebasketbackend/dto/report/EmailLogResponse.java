package dev.manuthlakdiw.primebasketbackend.dto.report;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record EmailLogResponse(
        String id,
        String recipientEmail,
        String subject,
        String status,
        String sentAt,
        String errorMessage
) {
}
