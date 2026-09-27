package com.ianvink.application.guards;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;

/**
 * When the user tries to navigate to a non-existent route, send them to the
 * login screen
 */
public class IllegalRouteGuard extends Div implements HasErrorParameter<NotFoundException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {

        event.getUI().access(() -> {
            event.getUI().navigate("");
        });

        // HTTP status 302 (Moved temporarily) because the request is redirected
        return HttpServletResponse.SC_MOVED_TEMPORARILY;
    }
}
