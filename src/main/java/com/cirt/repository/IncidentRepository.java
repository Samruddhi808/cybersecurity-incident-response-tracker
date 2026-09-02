package com.cirt.repository;

import com.cirt.model.Incident;
import com.cirt.model.Severity;
import com.cirt.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data access layer for Incident entities.
 *
 * Spring Data JPA auto-generates all standard CRUD methods.
 * Custom queries are added below for search and alert logic.
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // ------ Dashboard statistics ------

    long countByStatus(Status status);

    long countBySeverity(Severity severity);

    // ------ Search ------
    // Searches title and description using case-insensitive LIKE.
    // Enum fields (category, status) are not searched here — use filters instead.
    @Query("SELECT i FROM Incident i WHERE " +
           "LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY i.createdAt DESC")
    List<Incident> searchByTitleOrDescription(@Param("query") String query);

    // ------ Alert queries ------

    // CRITICAL incidents that are still active (not resolved or closed)
    List<Incident> findBySeverityAndStatusNotIn(Severity severity, List<Status> statuses);

    // HIGH (or any severity) incidents with no assigned person
    @Query("SELECT i FROM Incident i WHERE i.severity = :severity " +
           "AND (i.assignedTo IS NULL OR TRIM(i.assignedTo) = '')")
    List<Incident> findBySeverityAndUnassigned(@Param("severity") Severity severity);

    // Incidents not yet resolved/closed AND older than the given threshold
    @Query("SELECT i FROM Incident i WHERE i.status NOT IN :statuses " +
           "AND i.createdAt < :threshold ORDER BY i.createdAt ASC")
    List<Incident> findOverdueIncidents(
            @Param("statuses") List<Status> statuses,
            @Param("threshold") LocalDateTime threshold);
}
