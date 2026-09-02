package com.cirt.service;

import com.cirt.dto.IncidentDto;
import com.cirt.model.Category;
import com.cirt.model.Incident;
import com.cirt.model.Severity;
import com.cirt.model.Status;
import com.cirt.repository.IncidentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of IncidentService.
 *
 * All database access goes through IncidentRepository.
 * Business rules (alert logic, default values) live here, not in the controller.
 */
@Service
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    // Constructor injection — preferred over @Autowired field injection
    public IncidentServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // ------ Create ------

    @Override
    public Incident createIncident(IncidentDto dto) {
        Incident incident = new Incident();
        incident.setTitle(dto.getTitle());
        incident.setDescription(dto.getDescription());
        incident.setCategory(dto.getCategory());
        incident.setSeverity(dto.getSeverity());
        incident.setStatus(Status.OPEN);  // always OPEN on creation
        incident.setReportedBy(dto.getReportedBy());
        incident.setAssignedTo(dto.getAssignedTo());
        // createdAt and updatedAt are set automatically by @PrePersist
        return incidentRepository.save(incident);
    }

    // ------ Read ------

    @Override
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));
    }

    // ------ Update ------

    @Override
    public Incident updateIncident(Long id, IncidentDto dto) {
        Incident incident = getIncidentById(id);
        incident.setTitle(dto.getTitle());
        incident.setDescription(dto.getDescription());
        incident.setCategory(dto.getCategory());
        incident.setSeverity(dto.getSeverity());
        incident.setReportedBy(dto.getReportedBy());
        incident.setAssignedTo(dto.getAssignedTo());
        incident.setResolution(dto.getResolution());
        if (dto.getStatus() != null) {
            incident.setStatus(dto.getStatus());
        }
        // updatedAt is refreshed automatically by @PreUpdate
        return incidentRepository.save(incident);
    }

    @Override
    public Incident updateStatus(Long id, Status newStatus) {
        Incident incident = getIncidentById(id);
        incident.setStatus(newStatus);
        return incidentRepository.save(incident);
    }

    // ------ Delete ------

    @Override
    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new RuntimeException("Incident not found with id: " + id);
        }
        incidentRepository.deleteById(id);
    }

    // ------ Search ------

    @Override
    public List<Incident> searchIncidents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllIncidents();
        }
        return incidentRepository.searchByTitleOrDescription(query.trim());
    }

    // ------ Filter ------

    /**
     * Filters incidents by severity, status, and/or category.
     * Any null parameter means "do not filter on this field".
     *
     * Implementation note: For this student project, filtering is done in-memory
     * after a single database query. This is straightforward and avoids Hibernate
     * null-parameter quirks with enum types in JPQL.
     * For a large production dataset, use JPA Specifications or a custom @Query.
     */
    @Override
    public List<Incident> filterIncidents(Severity severity, Status status, Category category) {
        List<Incident> all = incidentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        return all.stream()
                .filter(i -> severity == null || i.getSeverity() == severity)
                .filter(i -> status == null   || i.getStatus()   == status)
                .filter(i -> category == null || i.getCategory() == category)
                .collect(Collectors.toList());
    }

    // ------ Dashboard ------

    @Override
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("total",    incidentRepository.count());
        stats.put("open",     incidentRepository.countByStatus(Status.OPEN));
        stats.put("critical", incidentRepository.countBySeverity(Severity.CRITICAL));
        stats.put("resolved", incidentRepository.countByStatus(Status.RESOLVED));
        return stats;
    }

    // ------ Alerts ------

    @Override
    public List<Incident> getAlertIncidents() {
        List<Status> resolvedStatuses = List.of(Status.RESOLVED, Status.CLOSED);
        LocalDateTime overdueThreshold = LocalDateTime.now().minusHours(24);

        // 1. CRITICAL incidents still active
        List<Incident> criticalUnresolved =
                incidentRepository.findBySeverityAndStatusNotIn(Severity.CRITICAL, resolvedStatuses);

        // 2. HIGH severity with no assignee
        List<Incident> highUnassigned =
                incidentRepository.findBySeverityAndUnassigned(Severity.HIGH);

        // 3. Any incident open/investigating/contained for more than 24 hours
        List<Incident> overdue =
                incidentRepository.findOverdueIncidents(resolvedStatuses, overdueThreshold);

        // Merge and deduplicate — preserve insertion order
        Set<Long> seen = new LinkedHashSet<>();
        List<Incident> alerts = new ArrayList<>();

        for (Incident i : criticalUnresolved) {
            if (seen.add(i.getId())) alerts.add(i);
        }
        for (Incident i : highUnassigned) {
            if (seen.add(i.getId())) alerts.add(i);
        }
        for (Incident i : overdue) {
            if (seen.add(i.getId())) alerts.add(i);
        }

        return alerts;
    }
}
