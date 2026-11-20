package org.studentdiscounts.search.dto.response;

import java.util.List;

public record ScheduleGroupDto(List<Integer> daysOfWeek, List<IntervalDto> intervals) {

    public record IntervalDto(String timeFrom, String timeUntil) {}
}
