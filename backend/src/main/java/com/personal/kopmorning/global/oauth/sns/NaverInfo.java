package com.personal.kopmorning.global.oauth.sns;

import com.personal.kopmorning.global.oauth.OAuthInfo;

import java.util.Map;

public class NaverInfo implements OAuthInfo {
    private final Map<String, Object> attributes;
    private final Map<String, Object> response;

    public NaverInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return (String) response.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getProviderName() {
        return (String) response.get("name");
    }
}
