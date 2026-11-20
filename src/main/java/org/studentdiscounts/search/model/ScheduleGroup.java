package org.studentdiscounts.search.model;

import java.util.List;

public record ScheduleGroup(List<Integer> daysOfWeek, List<Interval> intervals) {

    public record Interval(String timeFrom, String timeUntil){}
}
