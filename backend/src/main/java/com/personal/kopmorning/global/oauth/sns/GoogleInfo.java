package com.personal.kopmorning.global.oauth.sns;

import com.personal.kopmorning.global.oauth.OAuthInfo;

import java.util.Map;

public class GoogleInfo implements OAuthInfo {
    private final Map<String, Object> attributes;

    public GoogleInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProviderName() {
        return (String) attributes.get("name");
    }
}
