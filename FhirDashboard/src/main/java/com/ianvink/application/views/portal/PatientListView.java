package com.ianvink.application.views.portal;

import com.ianvink.application.entities.UserEntity;
import com.ianvink.application.entities.UserPermissionsEntity;
import com.ianvink.application.services.PatientService;
import com.ianvink.application.services.UserService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.time.Period;

@Route(value = "patients", layout = PortalLayout.class)
public class PatientListView extends VerticalLayout implements BeforeEnterObserver {

    private final PatientService patientService;
    private final UserService userService;
    private UserPermissionsEntity userPermissions;
    private final Grid<Patient> grid = new Grid<>(Patient.class, false);

    public PatientListView(PatientService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService = userService;

        setSizeFull();
        setPadding(true);

        HorizontalLayout headerBar = new HorizontalLayout();
        headerBar.setWidthFull();
        headerBar.setAlignItems(Alignment.CENTER);
        com.vaadin.flow.component.html.Span subtitle = new com.vaadin.flow.component.html.Span(
                "Patients");
        subtitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("color", "var(--lumo-contrast-60pct)")
                .set("font-weight", "500");

        headerBar.add(subtitle);
        add(headerBar, grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserEntity currentUser = VaadinSession.getCurrent().getAttribute(UserEntity.class);

        if (currentUser == null) {
            // Reroute already happens in the PortalLayout
            return;
        }

        this.userPermissions = this.userService.getUserPermissions(currentUser.getId())
                .orElseGet(() -> {
                    UserPermissionsEntity defaultPermissions = new UserPermissionsEntity();
                    defaultPermissions.setCanViewFirstName(false);
                    defaultPermissions.setCanViewLastName(false);
                    defaultPermissions.setCanViewEmail(false);
                    defaultPermissions.setCanViewPhoneNumber(false);
                    defaultPermissions.setCanViewAddress(false);
                    defaultPermissions.setCanViewBsn(false);
                    return defaultPermissions;
                });

        grid.removeAllColumns();
        buildGrid();
        grid.setItems(patientService.fetchAllPatients());
    }

    private void buildGrid() {
        grid.setSizeFull();

        grid.addColumn(this::getMaskedName)
                .setHeader("Name")
                .setKey("name")
                .setSortable(true);

        grid.addColumn(patient -> patient.hasGender() ? patient.getGender().toCode() : "N/A")
                .setHeader("Gender")
                .setKey("gender");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        grid.addColumn(patient -> {
            if (patient.hasBirthDate()) {
                return patient.getBirthDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(dateFormatter);
            }
            return "N/A";
        }).setHeader("Birth Date")
                .setKey("birthDate");

        grid.addColumn(this::calculateAge)
                .setHeader("Age")
                .setKey("age")
                .setSortable(true);

        if (this.userPermissions.canViewEmail()) {
            grid.addColumn(patient -> patient.hasTelecom() ? patient.getTelecom().stream()
                    .filter(t -> t.getSystem() == ContactPointSystem.EMAIL)
                    .findFirst()
                    .map(cp -> cp.getValue())
                    .orElse("") : "")
                    .setHeader("Email")
                    .setKey("email");
        }

        if (this.userPermissions.canViewPhoneNumber()) {
            grid.addColumn(patient -> patient.hasTelecom() ? patient.getTelecom().stream()
                    .filter(t -> t.getSystem() == ContactPointSystem.PHONE)
                    .findFirst()
                    .map(cp -> cp.getValue())
                    .orElse("-") : "-")
                    .setHeader("Phone")
                    .setKey("phone");
        }

        if (this.userPermissions.canViewAddress()) {
            grid.addColumn(patient -> getAddress(patient))
                    .setHeader("Address")
                    .setKey("address");
        }

        if (this.userPermissions.canViewBsn()) {
            grid.addColumn(patient -> getBsn(patient))
                    .setHeader("BSN")
                    .setKey("bsn");
        }

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private String getMaskedName(Patient patient) {
        if (!patient.hasName())
            return "Unknown";
        HumanName name = patient.getNameFirstRep();

        boolean showFirst = this.userPermissions.canViewFirstName();
        boolean showLast = this.userPermissions.canViewLastName();

        String first = "";
        if (name.hasGiven() && !name.getGiven().isEmpty()) {
            String given = name.getGiven().get(0).getValue();
            first = showFirst ? given : toInitial(given);
        }

        String last = "";
        if (name.hasFamily()) {
            String family = name.getFamily();
            last = showLast ? family : toInitial(family);
        }

        return (first + " " + last).trim();
    }

    private String toInitial(String name) {
        if (name == null || name.isBlank()) {
            return "?";
        }
        return Arrays.stream(name.trim().split("\\s+"))
                .filter(word -> !word.isBlank())
                .map(word -> {
                    String firstLetter = word.substring(0, 1);
                    return word.equals(word.toLowerCase()) ? firstLetter.toLowerCase() + "."
                            : firstLetter.toUpperCase() + ".";
                })
                .collect(Collectors.joining());
    }

    private String getAddress(Patient patient) {
        if (!patient.hasAddress())
            return "";
        Address addr = patient.getAddressFirstRep();
        StringBuilder sb = new StringBuilder();
        if (addr.hasLine()) {
            String line = addr.getLine().stream()
                    .map(t -> t.getValue())
                    .collect(Collectors.joining(", "));
            sb.append(line);
        }
        if (addr.hasCity()) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(addr.getCity());
        }
        if (addr.hasPostalCode()) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(addr.getPostalCode());
        }
        return sb.toString();
    }

    private String getBsn(Patient patient) {
        if (!patient.hasIdentifier())
            return "";
        return patient.getIdentifier().stream()
                .filter(id -> "http://fhir.nl".equals(id.getSystem()))
                .map(id -> id.getValue())
                .findFirst()
                .orElse(patient.getIdentifierFirstRep().getValue()); // fallback: first identifier
    }

    private String calculateAge(Patient patient) {
        if (patient.hasBirthDate()) {
            LocalDate birthDate = patient.getBirthDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate now = LocalDate.now();

            return String.valueOf(Period.between(birthDate, now).getYears());
        }
        return "N/A";
    }
}
