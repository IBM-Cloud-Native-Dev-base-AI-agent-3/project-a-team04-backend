package com.himedia.project_a_team04_backend.entity.forum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ForumStatus {
    UPCOMING("진행 예정", "Upcoming"),
    IN_PROGRESS("진행 중", "In Progress"),
    CLOSED("마감", "Closed"),
    FINISHED("종료", "Finished");

    private final String ko;
    private final String en;

    public String getLabel(String locale) {
        return "en".equalsIgnoreCase(locale) ? en : ko;
    }
}
