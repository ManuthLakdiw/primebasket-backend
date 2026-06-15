package dev.manuthlakdiw.primebasketbackend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface GoogleAuthService {
    GoogleIdToken.Payload verifyToken(String idTokenString) throws GeneralSecurityException, IOException;
}
