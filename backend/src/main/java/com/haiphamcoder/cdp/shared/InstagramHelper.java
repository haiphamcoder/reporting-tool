package com.haiphamcoder.cdp.shared;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.InstagramUser;
import com.facebook.ads.sdk.Page;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class InstagramHelper {
    public static APINodeList<InstagramUser> getInstagramAccounts(String accessToken, String pageId) {
        APIContext context = new APIContext(accessToken).enableDebug(false);
        try {
            return new Page.APIRequestGetInstagramAccounts(pageId, context).execute();
        } catch (APIException e) {
            log.error("Failed to get Instagram accounts", e);
            return null;
        }
    }
}
