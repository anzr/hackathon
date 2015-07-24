package com.example.android.animationsdemo;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.google.api.client.util.Preconditions;

/**
 * Created by Anurag on 7/12/2015.
 */
public class VSOTokenResponse extends GenericJson {

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getScope() {
        return scope;
    }

    /** Access token issued by the authorization server. */
    @Key("access_token")
    private String accessToken;

    /**
     * Token type (as specified in <a href="http://tools.ietf.org/html/rfc6749#section-7.1">Access
     * Token Types</a>).
     */
    @Key("token_type")
    private String tokenType;

    /**
     * Lifetime in seconds of the access token (for example 3600 for an hour) or {@code null} for
     * none.
     */
    //@Key("expires_in")
    private Long expiresInSeconds;

    /**
     * Refresh token which can be used to obtain new access tokens using {@link RefreshTokenRequest}
     * or {@code null} for none.
     */
    @Key("refresh_token")
    private String refreshToken;

    /**
     * Scope of the access token as specified in <a
     * href="http://tools.ietf.org/html/rfc6749#section-3.3">Access Token Scope</a> or {@code null}
     * for none.
     */
    @Key
    private String scope;

    /**
     * Sets the access token issued by the authorization server.
     *
     * <p>
     * Overriding is only supported for the purpose of calling the super implementation and changing
     * the return type, but nothing else.
     * </p>
     */
    public VSOTokenResponse setAccessToken(String accessToken) {
        this.accessToken = Preconditions.checkNotNull(accessToken);
        return this;
    }

    /**
     * Sets the token type (as specified in <a
     * href="http://tools.ietf.org/html/rfc6749#section-7.1">Access Token Types</a>).
     *
     * <p>
     * Overriding is only supported for the purpose of calling the super implementation and changing
     * the return type, but nothing else.
     * </p>
     */
    public VSOTokenResponse setTokenType(String tokenType) {
        this.tokenType = Preconditions.checkNotNull(tokenType);
        return this;
    }


    /**
     * Sets the lifetime in seconds of the access token (for example 3600 for an hour) or {@code null}
     * for none.
     *
     * <p>
     * Overriding is only supported for the purpose of calling the super implementation and changing
     * the return type, but nothing else.
     * </p>
     */
    public VSOTokenResponse setExpiresInSeconds(Long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
        return this;
    }

    /**
     * Sets the refresh token which can be used to obtain new access tokens using the same
     * authorization grant or {@code null} for none.
     *
     * <p>
     * Overriding is only supported for the purpose of calling the super implementation and changing
     * the return type, but nothing else.
     * </p>
     */
    public VSOTokenResponse setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    /**
     * Sets the scope of the access token or {@code null} for none.
     *
     * <p>
     * Overriding is only supported for the purpose of calling the super implementation and changing
     * the return type, but nothing else.
     * </p>
     */
    public VSOTokenResponse setScope(String scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public VSOTokenResponse clone() {
        return (VSOTokenResponse) super.clone();
    }

    @Override
    public VSOTokenResponse set(String fieldName, Object value) {
        if (fieldName.equals("expires_in")) {
            if (value instanceof String) {
                try {
                    expiresInSeconds = Long.parseLong((String) value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Value of expires_in is not a number: " + value);
                }
            } else if (value instanceof Number) {
                expiresInSeconds = ((Number) value).longValue();
            } else {
                throw new IllegalArgumentException("Unknown value type for expires_in: " + value.getClass().getName());
            }
        }

        return (VSOTokenResponse) super.set(fieldName, value);
    }
}
