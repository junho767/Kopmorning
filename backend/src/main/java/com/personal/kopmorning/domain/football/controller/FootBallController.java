package com.personal.kopmorning.domain.football.controller;

import com.personal.kopmorning.domain.football.dto.response.PlayerDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
import com.personal.kopmorning.domain.football.service.FootBallService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/football")
@RequiredArgsConstructor
public class FootBallController {
    private final FootBallService footBallService;

    @PostMapping("/save")
    public RsData<?> save() {
        footBallService.saveFootBallData();
        return new RsData<>(
                "200",
                "정보 최신화 성공"
        );
    }

    @GetMapping("/team")
    public RsData<List<TeamResponse>> getTeams() {
        return new RsData<>(
                "200",
                "팀 목록 가져오기 성공",
                footBallService.getTeams()
        );
    }

    @GetMapping("/team/{team_id}")
    public RsData<TeamDetailResponse> getTeam(@PathVariable Long team_id) {
        return new RsData<>(
                "200",
                "팀 조회 성공",
                footBallService.getTeamById(team_id)
        );
    }

    @GetMapping("/player/{player_id}")
    public RsData<PlayerDetailResponse> getPlayers(@PathVariable Long player_id) {
        return new RsData<>(
                "200",
                "선수 상세 정보 조회 성공",
                footBallService.getPlayer(player_id)
        );
    }
}
