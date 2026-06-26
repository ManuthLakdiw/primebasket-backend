package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.report.EmailLogResponse;
import dev.manuthlakdiw.primebasketbackend.entity.EmailLogEntity;
import dev.manuthlakdiw.primebasketbackend.repository.EmailLogRepository;
import dev.manuthlakdiw.primebasketbackend.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class EmailLogServiceImpl implements EmailLogService {

    private final EmailLogRepository emailLogRepository;

    @Override
    @Cacheable(value = "emailLogs", key = "#page + '-' + #size")
    public PageResponse<EmailLogResponse> getEmailLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<EmailLogEntity> logPage = emailLogRepository.findAll(pageable);

        Page<EmailLogResponse> dtoPage = logPage.map(log -> new EmailLogResponse(
                String.valueOf(log.getId()),
                log.getRecipientEmail(),
                log.getSubject(),
                log.getStatus() != null ? log.getStatus().name() : "UNKNOWN",
                log.getCreatedAt() != null ? log.getCreatedAt().toString() : "N/A",
                log.getErrorMessage()
        ));

        return PageResponse.from(dtoPage);
    }
}
