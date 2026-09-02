package com.cirt.controller;

import com.cirt.dto.IncidentDto;
import com.cirt.model.Category;
import com.cirt.model.Incident;
import com.cirt.model.Severity;
import com.cirt.model.Status;
import com.cirt.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller — returns JSON responses.
 *
 * Base path: /api/incidents
 *
 * Endpoints:
 *   GET    /api/incidents              → list all (supports ?search=, ?severity=, ?status=, ?category=)
 *   GET    /api/incidents/{id}         → get one
 *   POST   /api/incidents              → create
 *   PUT    /api/incidents/{id}         → update
 *   DELETE /api/incidents/{id}         → delete
 *   GET    /api/incidents/stats        → dashboard statistics
 *   GET    /api/incidents/alerts       → alert incidents
 *   PATCH  /api/incidents/{id}/status  → update status only
 */
@RestController
@RequestMapping("/api/incidents")
public class IncidentApiController {

    private final IncidentService incidentService;

    public IncidentApiController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // ------ GET all (with optional search/filter) ------

    @GetMapping
    public ResponseEntity<List<Incident>> getAllIncidents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {

        List<Incident> incidents;

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasFilter = (severity != null && !severity.isBlank())
                          || (status   != null && !status.isBlank())
                          || (category != null && !category.isBlank());

        if (hasSearch) {
            incidents = incidentService.searchIncidents(search);
        } else if (hasFilter) {
            Severity sev = parseEnum(Severity.class, severity);
            Status   sta = parseEnum(Status.class,   status);
            Category cat = parseEnum(Category.class, category);
            incidents = incidentService.filterIncidents(sev, sta, cat);
        } else {
            incidents = incidentService.getAllIncidents();
        }

        return ResponseEntity.ok(incidents);
    }

    // ------ GET by id ------

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(incidentService.getIncidentById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ------ POST create ------

    @PostMapping
    public ResponseEntity<Incident> createIncident(@Valid @RequestBody IncidentDto dto) {
        Incident created = incidentService.createIncident(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------ PUT update ------

    @PutMapping("/{id}")
    public ResponseEntity<Incident> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody IncidentDto dto) {
        try {
            Incident updated = incidentService.updateIncident(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ------ DELETE ------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        try {
            incidentService.deleteIncident(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ------ PATCH status ------

    @PatchMapping("/{id}/status")
    public ResponseEntity<Incident> updateStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
        try {
            Incident updated = incidentService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ------ GET stats ------

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(incidentService.getDashboardStats());
    }

    // ------ GET alerts ------

    @GetMapping("/alerts")
    public ResponseEntity<List<Incident>> getAlerts() {
        return ResponseEntity.ok(incidentService.getAlertIncidents());
    }

    // ------ Helper ------

    private <T extends Enum<T>> T parseEnum(Class<T> enumType, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
