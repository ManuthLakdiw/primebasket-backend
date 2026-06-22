package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.user.UpdatePasswordRequest;
import dev.manuthlakdiw.primebasketbackend.dto.user.UserDetailResponse;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.user.UpdatePersonalInfoRequest;
import dev.manuthlakdiw.primebasketbackend.projection.UserSummaryProjection;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

public interface UserService {

    UserDetailResponse getMyProfile(String email);

    PageResponse<UserSummaryProjection> getAllCustomerAccounts(int page, int size);

    String updatePersonalInfo(String email, UpdatePersonalInfoRequest request);

    String updatePassword(String email, UpdatePasswordRequest request);

}
