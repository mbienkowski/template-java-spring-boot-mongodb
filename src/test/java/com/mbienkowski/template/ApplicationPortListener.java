package com.mbienkowski.template;

import lombok.Getter;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ApplicationPortListener {

    @Getter
    private int serverPort;

    @EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        serverPort = event.getWebServer().getPort();
    }

}
