package com.personal.kopmorning.domain.football.controller;

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

    // todo : 지난 시즌은 어떤 식으로 보이게 할 것인지 고민 해봐야 할 듯
//    @GetMapping("/teams/{competition}/{season}")
//    public Mono<RsData<TeamsDTO>> getTeams(@PathVariable String competition,
//                                           @PathVariable Long season) {
//        return footBallService.getTeams(competition, season)
//                .map(data -> new RsData<>("200", "팀 목록 호출 성공", data));
//    }

//    @GetMapping("/player/{PlayerId}")
//    public RsData<PlayerResponse> getPlayer(@PathVariable Long PlayerId) {
//
//        return new RsData<>(
//                "200",
//                "선수 정보 호출 성공 - DB로 부터",
//                footBallService.getPlayer(PlayerId)
//        );
//    }

    @PostMapping("/save")
    public RsData<?> saveTeam() {
        footBallService.saveTeams();
        return new RsData<>(
                "200",
                "정보 최신화 성공"
        );
    }
}
