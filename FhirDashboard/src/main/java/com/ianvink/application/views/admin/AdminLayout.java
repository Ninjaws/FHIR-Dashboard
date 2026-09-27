package com.ianvink.application.views.admin;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class AdminLayout extends AppLayout {

    public AdminLayout() {
        H2 appName = new H2("Access Control");
        appName.getStyle()
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("padding", "var(--lumo-space-s)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Users", "users", VaadinIcon.USERS.create()));
        nav.addItem(new SideNavItem("User Permissions", UserPermissionsListView.class, VaadinIcon.KEY.create()));
        nav.getStyle().set("margin-top", "var(--lumo-space-xs)");
        addToDrawer(appName, new Scroller(nav));
        setPrimarySection(Section.DRAWER);
    }
}
