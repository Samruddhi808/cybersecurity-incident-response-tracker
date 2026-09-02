package com.cirt.service;

import com.cirt.dto.IncidentDto;
import com.cirt.model.Category;
import com.cirt.model.Incident;
import com.cirt.model.Severity;
import com.cirt.model.Status;

import java.util.List;
import java.util.Map;

/**
 * Business logic contract for Incident operations.
 * The controller layer depends only on this interface, not on the implementation.
 */
public interface IncidentService {

    Incident createIncident(IncidentDto dto);

    List<Incident> getAllIncidents();

    Incident getIncidentById(Long id);

    Incident updateIncident(Long id, IncidentDto dto);

    Incident updateStatus(Long id, Status newStatus);

    void deleteIncident(Long id);

    /**
     * Full-text search across title and description fields.
     * Returns all incidents if query is blank.
     */
    List<Incident> searchIncidents(String query);

    /**
     * Filter incidents by any combination of severity, status, and category.
     * Null values mean "no filter on this field".
     */
    List<Incident> filterIncidents(Severity severity, Status status, Category category);

    /**
     * Returns counts used by the dashboard.
     * Keys: "total", "open", "critical", "resolved"
     */
    Map<String, Long> getDashboardStats();

    /**
     * Returns incidents that need immediate attention:
     * - CRITICAL severity not yet resolved/closed
     * - HIGH severity without an assignee
     * - Any incident unresolved for more than 24 hours
     * Duplicates across categories are removed.
     */
    List<Incident> getAlertIncidents();
}
