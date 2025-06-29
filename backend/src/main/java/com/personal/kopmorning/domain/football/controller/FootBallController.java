package com.personal.kopmorning.domain.football.controller;

import com.personal.kopmorning.domain.football.dto.response.TeamsResponse;
import com.personal.kopmorning.domain.football.service.FootBallService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/football")
@RequiredArgsConstructor
public class FootBallController {
    private final FootBallService footBallService;

    @GetMapping("/teams/{competition}/{season}")
    public Mono<RsData<TeamsResponse>> getTeams(@PathVariable String competition,
                                                @PathVariable Long season) {
        return footBallService.getTeams(competition, season)
                .map(data -> new RsData<>("200", "팀 목록 호출 성공", data));
    }

    @PostMapping("/team/save")
    public RsData<?> saveTeam() {
        footBallService.saveTeams();
        return new RsData<>(
                "200",
                "팀 정보 최신화 성공"
        );
    }
}
