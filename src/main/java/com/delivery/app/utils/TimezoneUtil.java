package com.delivery.app.utils;
import com.delivery.app.configs.DeliciasAppProperties;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
@Component
public class TimezoneUtil {
    private final DeliciasAppProperties properties;

    public TimezoneUtil(DeliciasAppProperties properties) {
        this.properties = properties;
    }

    public ZoneOffset getZoneOffset() {
        int offset = properties.getTimezone(); // Obtiene el timezone como entero
        return ZoneOffset.ofHours(offset);
    }
}
