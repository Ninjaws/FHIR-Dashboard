package com.ianvink.application.views.admin;

import java.util.ArrayList;
import java.util.List;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.ianvink.application.entities.UserPermissionsEntity;
import com.ianvink.application.services.UserService;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "user-permissions", layout = AdminLayout.class)
public class UserPermissionsListView extends VerticalLayout {

        private final UserService userService;
        private final Grid<UserPermissionsEntity> grid = new Grid<>(UserPermissionsEntity.class, false);

        public UserPermissionsListView(UserService userService) {
                this.userService = userService;

                setSizeFull();
                setPadding(true);
                HorizontalLayout headerBar = new HorizontalLayout();
                headerBar.setWidthFull();
                headerBar.setAlignItems(Alignment.CENTER);

                com.vaadin.flow.component.html.Span subtitle = new com.vaadin.flow.component.html.Span(
                                "User Permissions");
                subtitle.getStyle()
                                .set("font-size", "var(--lumo-font-size-l)")
                                .set("color", "var(--lumo-contrast-60pct)")
                                .set("font-weight", "500");
                headerBar.add(subtitle);
                add(headerBar);

                configureGrid();
                add(grid);

                updateList();
        }

        private void configureGrid() {
                grid.setSizeFull();
                grid.addColumn(p -> p.getUser().getFirstName() + " " + p.getUser().getLastName())
                                .setHeader("User")
                                .setKey("user");
                record PermissionColumn(
                                String header,
                                String key,
                                Function<UserPermissionsEntity, Boolean> getter,
                                BiConsumer<UserPermissionsEntity, Boolean> setter) {
                }
                var columns = List.of(
                                new PermissionColumn("First name", "canviewFirstname", p -> p.canViewFirstName(),
                                                (p, val) -> p.setCanViewFirstName(val)),
                                new PermissionColumn("Last name", "canviewLastname", p -> p.canViewLastName(),
                                                (p, val) -> p.setCanViewLastName(val)),
                                new PermissionColumn("Email", "canviewEmail", p -> p.canViewEmail(),
                                                (p, val) -> p.setCanViewEmail(val)),
                                new PermissionColumn("Phone number", "canviewPhonenumber", p -> p.canViewPhoneNumber(),
                                                (p, val) -> p.setCanViewPhoneNumber(val)),
                                new PermissionColumn("Address", "canviewAddress", p -> p.canViewAddress(),
                                                (p, val) -> p.setCanViewAddress(val)),
                                new PermissionColumn("BSN", "canviewBsn", p -> p.canViewBsn(),
                                                (p, val) -> p.setCanViewBsn(val)));
                for (PermissionColumn col : columns) {
                        grid.addComponentColumn(p -> {
                                Checkbox cb = new Checkbox(col.getter().apply(p));
                                cb.addValueChangeListener(e -> {
                                        col.setter().accept(p, e.getValue());
                                        userService.saveUserPermissions(p);
                                });
                                return cb;
                        }).setHeader(col.header()).setKey(col.key());
                }
                grid.getColumns().forEach(col -> col.setAutoWidth(true));
        }

        private void updateList() {
                List<UserPermissionsEntity> users = this.userService.getAllUserPermissions();
                if (users.size() == 0) {
                        grid.setItems(new ArrayList<>());
                        return;
                }
                grid.setItems(users);
        }
}
