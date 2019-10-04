package dev.niekirk.com.instagram4android.requests;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserReelMediaFeedResult;

/**
 *
 * {@link InstagramGetRequest} class to get stories of a certain user pk
 *
 * @author Daniele Pancottini
 *
 */

@AllArgsConstructor
public class InstagramGetUserReelMediaFeedRequest extends InstagramGetRequest<InstagramUserReelMediaFeedResult> {

    private long userPk;

    @Override
    public String getPayload() {
        return null;
    }

    @Override
    public String getUrl() {
        return "feed/user/" + userPk + "/reel_media/";
    }

    @Override
    @SneakyThrows
    public InstagramUserReelMediaFeedResult parseResult(int resultCode, String content) {
        return parseJson(resultCode, content, InstagramUserReelMediaFeedResult.class);
    }

}