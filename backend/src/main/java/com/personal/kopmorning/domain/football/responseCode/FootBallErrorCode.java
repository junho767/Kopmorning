package com.personal.kopmorning.domain.football.responseCode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FootBallErrorCode {

    TEAM_NOT_FOUND("404", "해당 팀이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    PLAYER_NOT_FOUND("404", "해당 선수가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    TEAM_API_ERROR("500", "팀 정보를 불러오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    STANDING_API_ERROR("500", "순위 정보를 불러오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PLAYER_API_ERROR("500", "선수 정보를 저장하는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    FootBallErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
