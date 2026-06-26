package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.report.EmailLogResponse;
import dev.manuthlakdiw.primebasketbackend.service.impl.EmailLogServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
@ApiController("/email-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EmailLogController {

    private final EmailLogServiceImpl emailLogService;

    @SecurityRequirement(name = "BearerAuth")
    @GetMapping
    public PageResponse<EmailLogResponse> getEmailLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return emailLogService.getEmailLogs(page, size);
    }
}
