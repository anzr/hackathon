package com.example.android.animationsdemo;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class OAuth2Helper {

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private final CredentialStore credentialStore;

    private AuthorizationCodeFlow flow;

    private Oauth2Params oauth2Params;

    public OAuth2Helper(SharedPreferences sharedPreferences, Oauth2Params oauth2Params) {
        this.credentialStore = new SharedPreferencesCredentialStore(sharedPreferences);
        this.oauth2Params = oauth2Params;
        this.flow = new AuthorizationCodeFlow.Builder(oauth2Params.getAccessMethod(), HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl(oauth2Params.getTokenServerUrl()), new ClientParametersAuthentication(oauth2Params.getClientId(), oauth2Params.getClientSecret()), oauth2Params.getClientId(), oauth2Params.getAuthorizationServerEncodedUrl()).setCredentialStore(this.credentialStore).build();
    }

    public OAuth2Helper(SharedPreferences sharedPreferences) {
        this(sharedPreferences, Constants.OAUTH2PARAMS);
    }

    public String getAuthorizationUrl() {
        String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(oauth2Params.getRederictUri()).setScopes(convertScopesToString(oauth2Params.getScope())).build();
        return authorizationUrl;
    }

    public void retrieveAndStoreAccessToken(String authorizationCode) throws IOException {
        Log.i(Constants.TAG, "retrieveAndStoreAccessToken for code " + authorizationCode);
        VSOTokenResponse vsoTokenResponse;

        HttpResponse response = flow.newTokenRequest(authorizationCode)
                .setScopes(convertScopesToString(oauth2Params.getScope()))
                .setRedirectUri(oauth2Params.getRederictUri())
                .set("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                .set("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                .set("client_assertion", oauth2Params.getClientSecret())
                .set("assertion", authorizationCode)
                .executeUnparsed();

        vsoTokenResponse = response.parseAs(VSOTokenResponse.class);

        Log.i(Constants.TAG, "Found tokenResponse :");
        Log.i(Constants.TAG, "Access Token : " + vsoTokenResponse.getAccessToken());
        Log.i(Constants.TAG, "Refresh Token : " + vsoTokenResponse.getRefreshToken());

        TokenResponse tokenResponse = new TokenResponse().setAccessToken(vsoTokenResponse.getAccessToken())
                .setRefreshToken(vsoTokenResponse.getRefreshToken())
                .setExpiresInSeconds(vsoTokenResponse.getExpiresInSeconds())
                .setScope(vsoTokenResponse.getScope());
        flow.createAndStoreCredential(tokenResponse, oauth2Params.getUserId());
    }

    public String executeApiGetCall(String url) throws IOException {
        Log.i(Constants.TAG, "Executing API call at url " + url);
        return HTTP_TRANSPORT.createRequestFactory(loadCredential()).buildGetRequest(new GenericUrl(url)).execute().parseAsString();
    }

    public String executeApiPostCall(String posUrl, String body) throws IOException {
        //Log.i(Constants.TAG,"Executing API call at url " + this.oauth2Params.getApiUrl());

        GenericUrl url = new GenericUrl(posUrl);
        Log.i(Constants.TAG, "Executing API call at url " + url);
        HttpRequest httpRequest = HTTP_TRANSPORT.createRequestFactory(loadCredential()).buildPostRequest(url, ByteArrayContent.fromString(null, body));
        httpRequest.getHeaders().setContentType("application/json");

        return httpRequest.execute().parseAsString();
    }

    public String executeApiPatchCall(String patchUrl, String body) throws IOException {
        //Log.i(Constants.TAG,"Executing API call at url " + this.oauth2Params.getApiUrl());


        patchUrl = patchUrl.trim();
        GenericUrl url = new GenericUrl(patchUrl);
        Log.i(Constants.TAG, "Executing patch API call at url " + url);
        HttpRequest httpRequest = HTTP_TRANSPORT.createRequestFactory(loadCredential()).buildPostRequest(url, ByteArrayContent.fromString("application/json", body));
        Log.i(Constants.TAG, "http request sent " + httpRequest);
       // httpRequest.getHeaders().set("Content-Type","appl");
       // httpRequest.getHeaders().setAccept("X-HTTP-Method-Override:PATCH");
        return httpRequest.execute().parseAsString();
    }
//
//	public String executeApiPostCall() throws IOException {
//		Location loc = new Location();
//		loc.setLatitude(10);
//		loc.setLongitude(10);
//		HttpContent httpContent = new JsonHttpContent(JSON_FACTORY, loc).setWrapperKey("data"); 
//		return HTTP_TRANSPORT.createRequestFactory(loadCredential()).buildPostRequest(new GenericUrl(this.oauth2Params.getApiUrl()),httpContent).execute().parseAsString();
//	}


    public Credential loadCredential() throws IOException {
        return flow.loadCredential(oauth2Params.getUserId());
    }

    public void clearCredentials() throws IOException {
        Log.i(Constants.TAG, "Credentials for user ID=" + oauth2Params.getUserId() + " is cleared");
        flow.getCredentialStore().delete(oauth2Params.getUserId(), null);
    }

    private Collection<String> convertScopesToString(String scopesConcat) {
        String[] scopes = scopesConcat.split(",");
        Collection<String> collection = new ArrayList<>();
        Collections.addAll(collection, scopes);
        return collection;
    }
}
