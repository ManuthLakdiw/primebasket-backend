package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.user.*;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.entity.types.AddressType;
import dev.manuthlakdiw.primebasketbackend.projection.UserSummaryProjection;

import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

public interface UserService {

    UserDetailResponse getMyProfile(String email);

    String updatePersonalInfo(String email, UpdatePersonalInfoRequest request);

    String updatePassword(String email, UpdatePasswordRequest request);

    void addAddress(String email, AddressRequest request);

    void updateAddress(String email, AddressRequest request);

    void deleteAddress(String email, AddressType addressType);

    PageResponse<UserAdminResponse> getAllCustomerAccounts(int page, int size);

    String toggleUserActivation(UUID userId);

    UserFullDetailResponse getUserFullDetails(UUID userId);


}
