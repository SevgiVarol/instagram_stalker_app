package dev.niekirk.com.instagram4android.requests.internal;

import java.util.LinkedHashMap;
import java.util.Map;



import com.fasterxml.jackson.databind.ObjectMapper;


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
public class InstagramReadMsisdnHeaderRequest extends InstagramPostRequest<StatusResult> {

    @Override
    public String getUrl() {
        return "accounts/read_msisdn_header/";
    }

    @Override
    @SneakyThrows
    public String getPayload() {

        Map<String, Object> likeMap = new LinkedHashMap<>();
        likeMap.put("device_id", api.getUuid());
        likeMap.put("_csrftoken", api.getOrFetchCsrf(null));

        ObjectMapper mapper = new ObjectMapper();
        String payloadJson = mapper.writeValueAsString(likeMap);

        return payloadJson;
    }

    @Override
    @SneakyThrows
    public StatusResult parseResult(int statusCode, String content) {
        return parseJson(statusCode, content, StatusResult.class);
    }

    /**
     * @return if request must be logged in
     */
    @Override
    public boolean requiresLogin() {
        return false;
    }
}