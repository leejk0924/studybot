package org.jk.studybot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "text-channel")
public class TextChannelProperties {
    @Getter
    @Setter
    private String targetChannelName;
}
