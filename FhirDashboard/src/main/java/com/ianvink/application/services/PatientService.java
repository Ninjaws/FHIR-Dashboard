package com.ianvink.application.services;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {

    @Autowired
    private IGenericClient fhirClient;

    public List<Patient> fetchAllPatients() {
        List<Patient> allPatients = new ArrayList<>();

        Bundle bundle = fhirClient.search()
                .forResource(Patient.class)
                .count(500)
                .returnBundle(Bundle.class)
                .execute();

        while (bundle != null) {
            bundle.getEntry().forEach(entry -> {
                if (entry.getResource() instanceof Patient) {
                    allPatients.add((Patient) entry.getResource());
                }
            });

            if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                bundle = fhirClient.loadPage().next(bundle).execute();
            } else {
                bundle = null;
            }
        }

        return allPatients;
    }
}
