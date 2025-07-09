package com.together.backend.domain.notification.model.notification.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class NotificationEnabledRequest {
    private Boolean enabled;
}
