package dev.niekirk.com.instagram4android.requests.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by root on 08/06/17.
 */

@Getter
@Setter
@ToString(callSuper = true)
public class InstagramLoginResult extends StatusResult {

    private InstagramLoggedUser logged_in_user;
    private InstagramTwoFactorInfo two_factor_info;
    private InstagramChallenge challenge;

}
