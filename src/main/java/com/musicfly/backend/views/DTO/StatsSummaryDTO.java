package com.musicfly.backend.views.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class StatsSummaryDTO {
    private Long totalUsers;
    private Long totalSongs;
    private Long totalStreams;
}
