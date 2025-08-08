package com.personal.kopmorning.global.oauth.sns;

import com.personal.kopmorning.global.oauth.OAuthInfo;

import java.util.Map;

public class KaKaoInfo implements OAuthInfo {
    private final Map<String, Object> attributes;

    public KaKaoInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        Object idObj = attributes.get("id");
        return idObj != null ? String.valueOf(idObj) : null;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount != null) {
            return (String) kakaoAccount.get("email");
        }
        return null;
    }

    @Override
    public String getProviderName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties != null) {
            return (String) properties.get("nickname");
        }
        return null;
    }
}
