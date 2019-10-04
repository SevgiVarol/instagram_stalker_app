package dev.niekirk.com.instagram4android.requests.internal;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.LinkedHashMap;
import java.util.Map;

import dev.niekirk.com.instagram4android.requests.InstagramPostRequest;
import dev.niekirk.com.instagram4android.requests.payload.StatusResult;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * Sync Features Request
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@AllArgsConstructor
public class InstagramZeroRatingTokenRequest extends InstagramPostRequest<StatusResult> {

    @Override
    public String getUrl() {
        return "zr/token/result/";
    }

    @Override
    @SneakyThrows
    public String getPayload() {

        Map<String, Object> likeMap = new LinkedHashMap<>();
        likeMap.put("token_hash", "");

        ObjectMapper mapper = new ObjectMapper();
        String payloadJson = mapper.writeValueAsString(likeMap);

        return payloadJson;
    }

    @Override
    @SneakyThrows
    public StatusResult parseResult(int statusCode, String content) {
        return new StatusResult();
    }

    /**
     * @return if request must be logged in
     */
    @Override
    public boolean requiresLogin() {
        return false;
    }
}
