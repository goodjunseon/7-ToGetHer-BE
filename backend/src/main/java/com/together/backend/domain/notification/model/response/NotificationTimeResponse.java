package com.together.backend.domain.notification.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTimeResponse {
    private Boolean enabled;
    private String time;
}
