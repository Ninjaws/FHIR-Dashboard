package com.ianvink.application.views.portal;

import com.ianvink.application.entities.UserEntity;
import com.ianvink.application.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class LoginView extends VerticalLayout {

    public LoginView(@Autowired UserService userService) {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background-color", "var(--lumo-shade-5pct)");

        VerticalLayout card = new VerticalLayout();
        card.setWidth("360px");
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle()
                .set("background-color", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-m)");

        H2 title = new H2("Portal Login");
        title.getStyle().set("margin-top", "0");
        Paragraph description = new Paragraph("Select a staff member to simulate a login session.");
        description.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-contrast-60pct)");

        Select<UserEntity> userSelect = new Select<>();
        userSelect.setLabel("Select User Profile");
        userSelect.setWidthFull();
        userSelect.setItemLabelGenerator(user -> user.getFirstName() + " " + user.getLastName());
        userSelect.setItems(userService.getAllUsers());

        Button loginButton = new Button("Log In", VaadinIcon.SIGN_IN.create());
        loginButton.getThemeNames().add("primary");
        loginButton.setWidthFull();
        loginButton.getStyle().set("cursor", "pointer");
        loginButton.addClickListener(e -> {
            UserEntity selectedUser = userSelect.getValue();
            if (selectedUser == null) {
                Notification.show("Please select a user to proceed.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute(UserEntity.class, selectedUser);

            UI.getCurrent().navigate(PatientListView.class);
        });
        card.add(title, description, userSelect, loginButton);
        add(card);
    }
}
