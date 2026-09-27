package com.ianvink.application.views.portal;

import com.ianvink.application.entities.UserEntity;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;

public class PortalLayout extends AppLayout implements BeforeEnterObserver {

    private final Span userLabel = new Span("Loading...");
    private final SideNav nav = new SideNav();

    public PortalLayout() {
        H2 portalTitle = new H2("Patient Portal");
        portalTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("padding", "var(--lumo-space-s)");

        userLabel.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("font-weight", "bold")
                .set("color", "var(--lumo-primary-text-color)");

        Button changeUserButton = new Button("Change User", VaadinIcon.RETWEET.create());
        changeUserButton.getThemeNames().add("tertiary-inline");
        changeUserButton.getStyle().set("cursor", "pointer");
        changeUserButton.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute(UserEntity.class, null);
            UI.getCurrent().navigate("");
        });

        VerticalLayout profileWrapper = new VerticalLayout(userLabel, changeUserButton);
        profileWrapper.setPadding(true);
        profileWrapper.setSpacing(false);
        profileWrapper.getStyle()
                .set("padding", "var(--lumo-space-s)")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("margin-bottom", "var(--lumo-space-s)");

        addToDrawer(portalTitle, profileWrapper, new Scroller(nav));
        setPrimarySection(Section.DRAWER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserEntity currentUser = VaadinSession.getCurrent().getAttribute(UserEntity.class);

        if (currentUser == null) {
            event.forwardTo("");
            return;
        }
        userLabel.setText(currentUser.getFirstName() + " " + currentUser.getLastName());

        nav.removeAll();
        SideNavItem patientItem = new SideNavItem("Patients", PatientListView.class, VaadinIcon.LIST.create());
        nav.addItem(patientItem);
    }
}
