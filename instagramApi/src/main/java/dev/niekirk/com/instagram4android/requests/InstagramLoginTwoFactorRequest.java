package dev.niekirk.com.instagram4android.requests;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginTwoFactorPayload;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * Login TwoFactorRequest
 *
 * @author Ozan Karaali
 *
 */
@AllArgsConstructor
public class InstagramLoginTwoFactorRequest extends InstagramPostRequest<InstagramLoginResult> {

    private InstagramLoginTwoFactorPayload payload;

    @Override
    public String getUrl() {
        return "accounts/two_factor_login/";
    }

    @Override
    @SneakyThrows
    public String getPayload() {
        ObjectMapper mapper = new ObjectMapper();
        String payloadJson = mapper.writeValueAsString(payload);

        return payloadJson;
    }

    @Override
    @SneakyThrows
    public InstagramLoginResult parseResult(int statusCode, String content) {
        return parseJson(statusCode, content, InstagramLoginResult.class);
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

}