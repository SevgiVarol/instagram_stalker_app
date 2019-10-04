package dev.niekirk.com.instagram4android.requests;

import dev.niekirk.com.instagram4android.requests.payload.InstagramGetMediaLikersResult;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * Created by Charlie on 13/06/17.
 */

@AllArgsConstructor
public class InstagramGetMediaLikersRequest extends InstagramGetRequest<InstagramGetMediaLikersResult> {

    private long mediaId;
    private String maxId;
    @Override
    public String getUrl() {
        String url = "media/" + mediaId + "/likers/";
        if (maxId != null && !maxId.isEmpty()) {
            url += "?max_id=" + maxId;
        }
        return url;
    }

    @Override
    @SneakyThrows
    public InstagramGetMediaLikersResult parseResult(int statusCode, String content) {
        return parseJson(statusCode, content, InstagramGetMediaLikersResult.class);
    }
}
