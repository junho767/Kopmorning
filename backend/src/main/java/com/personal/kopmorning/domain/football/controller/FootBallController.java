package com.personal.kopmorning.domain.football.controller;

import com.personal.kopmorning.domain.football.dto.response.GameResponse;
import com.personal.kopmorning.domain.football.dto.response.RankingResponse;
import com.personal.kopmorning.domain.football.dto.response.StandingResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
import com.personal.kopmorning.domain.football.responseCode.FootBallSuccessCode;
import com.personal.kopmorning.domain.football.service.FootBallService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/football")
@RequiredArgsConstructor
public class FootBallController {
    private final FootBallService footBallService;

    @PostMapping("/save")
    public RsData<?> save() {
        footBallService.saveTeamAndPlayer();
        footBallService.saveStanding();
        footBallService.saveFixtures();
        footBallService.saveTopScorer();

        log.info("✅ Write-Around 방식: DB만 저장");
        return new RsData<>(
                FootBallSuccessCode.SAVE_INFO.getCode(),
                FootBallSuccessCode.SAVE_INFO.getMessage()
        );
    }

    @GetMapping("/team")
    public RsData<List<TeamResponse>> getTeams() {
        return new RsData<>(
                FootBallSuccessCode.GET_TEAM_LIST.getCode(),
                FootBallSuccessCode.GET_TEAM_LIST.getMessage(),
                footBallService.getTeams()
        );
    }

    @GetMapping("/team/{team_id}")
    public RsData<TeamDetailResponse> getTeam(@PathVariable Long team_id) {
        return new RsData<>(
                FootBallSuccessCode.GET_TEAM_ONE.getCode(),
                FootBallSuccessCode.GET_TEAM_ONE.getMessage(),
                footBallService.getTeamById(team_id)
        );
    }

    @GetMapping("/standing")
    public RsData<StandingResponse> getStanding() {
        return new RsData<>(
                FootBallSuccessCode.GET_STANDING.getCode(),
                FootBallSuccessCode.GET_STANDING.getMessage(),
                footBallService.getStanding()
        );
    }

    @GetMapping("/matches")
    public RsData<List<GameResponse>> getMatches() {
        return new RsData<>(
                FootBallSuccessCode.GET_GAME_LIST.getCode(),
                FootBallSuccessCode.GET_GAME_LIST.getMessage(),
                footBallService.getGameList()
        );
    }

    @GetMapping("/ranking/{standard}")
    public RsData<List<RankingResponse>> getRanking(@PathVariable String standard) {
        return new RsData<>(
                FootBallSuccessCode.GET_RANKING_LIST.getCode(),
                FootBallSuccessCode.GET_RANKING_LIST.getMessage(),
                footBallService.getRanking(standard)
        );
    }
}
