package com.together.backend.domain.notification.model.notification.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationDayResponse {
    private Boolean enabled;
    private Integer daysBefore;
}
