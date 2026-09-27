package com.ianvink.application.views.admin;

import java.util.ArrayList;
import java.util.List;

import com.ianvink.application.entities.UserEntity;
import com.ianvink.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "users", layout = AdminLayout.class)
public class UserListView extends VerticalLayout {

    private final UserService userService;
    private final Grid<UserEntity> grid = new Grid<>(UserEntity.class, false);

    public UserListView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);

        HorizontalLayout headerBar = new HorizontalLayout();
        headerBar.setWidthFull();
        headerBar.setAlignItems(Alignment.CENTER);

        com.vaadin.flow.component.html.Span subtitle = new com.vaadin.flow.component.html.Span(
                "Users");
        subtitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("color", "var(--lumo-contrast-60pct)")
                .set("font-weight", "500");
        headerBar.add(subtitle);
        add(headerBar);

        configureGrid();
        add(grid);

        Button addUserButton = new Button("Add User", VaadinIcon.PLUS.create());
        addUserButton.getThemeNames().add("primary");
        addUserButton.getStyle()
                .set("margin-left", "auto")
                .set("cursor", "pointer");

        addUserButton.addClickListener(e -> openAddUserDialog());
        add(addUserButton);
        updateList();
    }

    private void openAddUserDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create New User");

        TextField firstNameField = new TextField("First Name");
        TextField lastNameField = new TextField("Last Name");
        firstNameField.setRequired(true);
        lastNameField.setRequired(true);
        FormLayout formLayout = new FormLayout(firstNameField, lastNameField);
        dialog.add(formLayout);

        Button saveButton = new Button("Save", e -> {
            if (firstNameField.isEmpty() || lastNameField.isEmpty()) {
                Notification.show("Please fill in all fields", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            this.userService.createUserWithDefaultPermissions(
                    firstNameField.getValue(),
                    lastNameField.getValue());
            Notification.show("User created successfully!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            updateList();
            dialog.close();
        });
        saveButton.getThemeNames().add("primary");
        saveButton.getStyle().set("cursor", "pointer");

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        cancelButton.getStyle().set("cursor", "pointer");

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(user -> user.getId())
                .setHeader("ID")
                .setKey("id");

        grid.addColumn(user -> user.getFirstName())
                .setHeader("First Name")
                .setKey("firstname");

        grid.addColumn(user -> user.getLastName())
                .setHeader("Last Name")
                .setKey("lastname");

        grid.addComponentColumn(user -> {
            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.getStyle().set("cursor", "pointer");
            deleteButton.setTooltipText("Remove user");
            deleteButton.addClickListener(e -> openDeleteConfirmationDialog(user));
            return deleteButton;
        }).setHeader("Actions").setKey("actions").setFlexGrow(0).setWidth("100px");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void openDeleteConfirmationDialog(UserEntity user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Delete User");
        com.vaadin.flow.component.html.Paragraph message = new com.vaadin.flow.component.html.Paragraph(
                String.format("Are you sure you want to delete user %s %s? This action cannot be undone.",
                        user.getFirstName(), user.getLastName()));
        dialog.add(message);

        Button confirmButton = new Button("Delete", e -> {
            try {
                this.userService.deleteUserAndPermissions(user.getId());
                Notification.show("User deleted successfully!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                updateList();
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Could not delete user: " + ex.getMessage(), 5000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        confirmButton.getStyle().set("cursor", "pointer");

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        cancelButton.getStyle().set("cursor", "pointer");

        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }

    private void updateList() {
        List<UserEntity> users = this.userService.getAllUsers();
        if (users.size() == 0) {
            grid.setItems(new ArrayList<>());
            return;
        }
        grid.setItems(users);
    }
}
