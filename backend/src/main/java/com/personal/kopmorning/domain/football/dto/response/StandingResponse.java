package com.personal.kopmorning.domain.football.dto.response;

import com.personal.kopmorning.domain.football.entity.Standing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandingResponse {
    List<Standing> standings;
}
