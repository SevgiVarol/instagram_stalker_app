package dev.niekirk.com.instagram4android.requests.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Login Payload
 * @author Ozan Karaali
 *
 */
@Getter
@Setter
@ToString(callSuper = true)
@Builder
public class InstagramLoginTwoFactorPayload {
    private String username;
    private String phone_id;
    private String _csrftoken;
    private String guid;
    private String device_id;
    private String verification_code;
    private String two_factor_identifier;
    private String password;
    private int login_attempt_account = 0;


}
