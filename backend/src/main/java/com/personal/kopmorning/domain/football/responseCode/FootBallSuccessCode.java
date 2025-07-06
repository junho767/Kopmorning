package com.personal.kopmorning.domain.football.responseCode;

import lombok.Getter;

@Getter
public enum FootBallSuccessCode {
    SAVE_INFO("200-1", "데이터 최신화 성공"),
    SAVE_STANDING("200-2","순위표 최신화 성공"),
    GET_TEAM_LIST("200-3", "팀 목록 가져오기 성공"),
    GET_TEAM_ONE("200-4", "팀 세부 사항 조회 성공"),
    GET_PLAYER_INFO("200-5", "선수 상세 정보 조회 성공"),
    GET_STANDING("200-6", "순위표 조회 성공");

    private final String code;
    private final String message;

    FootBallSuccessCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
