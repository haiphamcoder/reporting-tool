package com.haiphamcoder.reporting.shared;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.APIRequest;
import com.facebook.ads.sdk.APIResponse;
import com.facebook.ads.sdk.Page;
import com.facebook.ads.sdk.User;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class FacebookHelper {
    public static String getFacebookProfileId(String accessToken) {
        APIContext context = new APIContext(accessToken).enableDebug(false);
        try {
            APIResponse response = new APIRequest<>(context, "me", "/", "GET").execute();
            return response.getRawResponseAsJsonObject().get("id").getAsString();
        } catch (APIException e) {
            log.error("Failed to get Facebook profile ID", e);
            return null;
        }
    }

    public static APINodeList<Page> getFacebookPages(String accessToken) {
        APIContext context = new APIContext(accessToken).enableDebug(false);
        try {
            String userId = getFacebookProfileId(accessToken);
            if (userId == null) {
                return null;
            }
            return new User.APIRequestGetAccounts(userId, context).execute();
        } catch (APIException e) {
            log.error("Failed to get Facebook pages", e);
            return null;
        }
    }
}
