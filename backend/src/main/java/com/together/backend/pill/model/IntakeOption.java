package com.together.backend.pill.model;

import lombok.Getter;

@Getter
public enum IntakeOption {

    OPTION1("21일 복용 + 7일 휴약", 21, 0, 7),
    OPTION2("21일 복용 + 7일 위약", 21, 7, 0),
    OPTION3("24일 복용 + 4일 휴약", 24, 0, 4),
    OPTION4("28일 단일 성분 복용", 28, 0, 0),
    OPTION5("24일 복용 + 4일 위약", 24, 4, 0),
    OPTION6("84일 복용 + 7일 위약", 84, 7, 0);


    private final String name;
    private final int realDays;
    private final int fakeDays;
    private final int breakDays;

    private IntakeOption(String name, int realDays, int fakeDays, int breakDays) {
        this.name = name;
        this.realDays = realDays;
        this.fakeDays = fakeDays;
        this.breakDays = breakDays;

    }
}
