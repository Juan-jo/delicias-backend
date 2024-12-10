package com.delivery.app.configs.auditable.config;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.utils.TimezoneUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AuditingConfig implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        TimezoneUtil timezoneUtil = applicationContext.getBean(TimezoneUtil.class);
        AuditableEntity.setTimezoneUtil(timezoneUtil);
    }
}
