package org.example.authorization;

import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Map;

/**
 * @Author Roc
 * @Date 2025/1/16 14:19
 */
@Transient
public class DeviceClientAuthenticationToken extends OAuth2ClientAuthenticationToken {
    public DeviceClientAuthenticationToken(String clientId, ClientAuthenticationMethod clientAuthenticationMethod, Object credentials, Map<String, Object> additionalParameters) {
        super(clientId, clientAuthenticationMethod, credentials, additionalParameters);
    }

    public DeviceClientAuthenticationToken(RegisteredClient registeredClient, ClientAuthenticationMethod clientAuthenticationMethod, Object credentials) {
        super(registeredClient, clientAuthenticationMethod, credentials);
    }
}
