package dev.niekirk.com.instagram4android.requests.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 *
 * Data class which represents response of {@link dev.niekirk.com.instagram4android.InstagramGetStoryViewersRequest}
 *
 * @author Daniele Pancottini
 * @ported by Vioxini
 *
 */

@Getter
@Setter
@ToString(callSuper = true)
public class InstagramGetStoryViewersResult extends StatusResult {

    private List<InstagramUser> users;
    private String next_max_id;
    private int user_count;
    private int total_viewer_count;
    private int total_screenshot_count;
    private InstagramItem updated_media;

}
