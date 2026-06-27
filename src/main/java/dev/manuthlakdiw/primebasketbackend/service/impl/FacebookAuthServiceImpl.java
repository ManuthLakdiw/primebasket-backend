package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.service.FacebookAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
public class FacebookAuthServiceImpl implements FacebookAuthService {
    @Value("${facebook.app.id}")
    private String appId;

    @Value("${facebook.app.secret}")
    private String appSecret;

    @Override
    public Map<String, Object> verifyToken(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            String debugTokenUrl = "https://graph.facebook.com/debug_token?input_token=" + accessToken + "&access_token=" + appId + "|" + appSecret;
            Map<String, Object> debugResponse = restTemplate.getForObject(debugTokenUrl, Map.class);

            if (debugResponse != null && debugResponse.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) debugResponse.get("data");

                Boolean isValid = (Boolean) data.get("is_valid");

                String tokenAppId = String.valueOf(data.get("app_id"));

                if (isValid == null || !isValid || !appId.equals(tokenAppId)) {
                    throw new RuntimeException("Security Breach: Invalid Facebook Token or App ID mismatch!");
                }
            } else {
                throw new RuntimeException("Failed to validate Facebook token structure");
            }

            String url = "https://graph.facebook.com/me?fields=id,email,first_name,last_name&access_token=" + accessToken;
            return restTemplate.getForObject(url, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("Facebook Authentication Failed: " + e.getMessage());
        }
    }
}
