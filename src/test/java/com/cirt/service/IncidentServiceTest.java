package com.cirt.service;

import com.cirt.dto.IncidentDto;
import com.cirt.model.Category;
import com.cirt.model.Incident;
import com.cirt.model.Severity;
import com.cirt.model.Status;
import com.cirt.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IncidentServiceImpl.
 *
 * Uses Mockito to mock IncidentRepository — no database or Spring context required.
 * Each test is focused on a single piece of business logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IncidentService Unit Tests")
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    private IncidentDto sampleDto;
    private Incident sampleIncident;

    @BeforeEach
    void setUp() {
        // Reusable sample DTO for create/update tests
        sampleDto = new IncidentDto();
        sampleDto.setTitle("Test Phishing Attack");
        sampleDto.setDescription("Suspicious email with malicious link detected.");
        sampleDto.setCategory(Category.PHISHING);
        sampleDto.setSeverity(Severity.HIGH);
        sampleDto.setReportedBy("Alice Smith");
        sampleDto.setAssignedTo("Bob Jones");

        // Reusable sample Incident entity
        sampleIncident = new Incident();
        sampleIncident.setId(1L);
        sampleIncident.setTitle("Test Phishing Attack");
        sampleIncident.setDescription("Suspicious email with malicious link detected.");
        sampleIncident.setCategory(Category.PHISHING);
        sampleIncident.setSeverity(Severity.HIGH);
        sampleIncident.setStatus(Status.OPEN);
        sampleIncident.setReportedBy("Alice Smith");
        sampleIncident.setAssignedTo("Bob Jones");
        sampleIncident.setCreatedAt(LocalDateTime.now());
        sampleIncident.setUpdatedAt(LocalDateTime.now());
    }

    // ------ createIncident ------

    @Test
    @DisplayName("createIncident: saves incident and returns result")
    void createIncident_withValidDto_savesAndReturnsIncident() {
        when(incidentRepository.save(any(Incident.class))).thenReturn(sampleIncident);

        Incident result = incidentService.createIncident(sampleDto);

        assertNotNull(result);
        assertEquals("Test Phishing Attack", result.getTitle());
        verify(incidentRepository, times(1)).save(any(Incident.class));
    }

    @Test
    @DisplayName("createIncident: always sets status to OPEN regardless of DTO")
    void createIncident_alwaysSetsStatusToOpen() {
        // Even if someone passes a DTO with a different status, creation must force OPEN
        sampleDto.setStatus(Status.RESOLVED);

        when(incidentRepository.save(argThat(incident -> incident.getStatus() == Status.OPEN)))
                .thenReturn(sampleIncident);

        Incident result = incidentService.createIncident(sampleDto);

        assertNotNull(result);
        // Verify save was called with an incident that has OPEN status
        verify(incidentRepository).save(argThat(i -> i.getStatus() == Status.OPEN));
    }

    @Test
    @DisplayName("createIncident: maps all DTO fields to entity correctly")
    void createIncident_mapsAllDtoFieldsCorrectly() {
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> {
            // Return the exact incident that was passed to save
            return invocation.getArgument(0);
        });

        Incident result = incidentService.createIncident(sampleDto);

        assertEquals(sampleDto.getTitle(),       result.getTitle());
        assertEquals(sampleDto.getDescription(), result.getDescription());
        assertEquals(sampleDto.getCategory(),    result.getCategory());
        assertEquals(sampleDto.getSeverity(),    result.getSeverity());
        assertEquals(sampleDto.getReportedBy(),  result.getReportedBy());
        assertEquals(sampleDto.getAssignedTo(),  result.getAssignedTo());
        assertEquals(Status.OPEN,                result.getStatus());
    }

    // ------ getIncidentById ------

    @Test
    @DisplayName("getIncidentById: returns incident when found")
    void getIncidentById_withExistingId_returnsIncident() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(sampleIncident));

        Incident result = incidentService.getIncidentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getIncidentById: throws RuntimeException for non-existent ID")
    void getIncidentById_withNonExistentId_throwsRuntimeException() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> incidentService.getIncidentById(999L));

        assertTrue(ex.getMessage().contains("999"));
    }

    // ------ updateStatus ------

    @Test
    @DisplayName("updateStatus: updates and saves the new status")
    void updateStatus_withValidId_updatesStatusCorrectly() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(sampleIncident));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));

        Incident result = incidentService.updateStatus(1L, Status.INVESTIGATING);

        assertEquals(Status.INVESTIGATING, result.getStatus());
        verify(incidentRepository).save(any(Incident.class));
    }

    @Test
    @DisplayName("updateStatus: throws RuntimeException for non-existent ID")
    void updateStatus_withNonExistentId_throwsRuntimeException() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> incidentService.updateStatus(999L, Status.RESOLVED));
    }

    // ------ searchIncidents ------

    @Test
    @DisplayName("searchIncidents: calls repository search for non-empty query")
    void searchIncidents_withValidQuery_callsRepositorySearch() {
        String query = "phishing";
        when(incidentRepository.searchByTitleOrDescription(query)).thenReturn(List.of(sampleIncident));

        List<Incident> results = incidentService.searchIncidents(query);

        assertEquals(1, results.size());
        verify(incidentRepository, times(1)).searchByTitleOrDescription(query);
    }

    @Test
    @DisplayName("searchIncidents: returns all incidents for blank query")
    void searchIncidents_withBlankQuery_returnsAllIncidents() {
        when(incidentRepository.findAll(any(Sort.class))).thenReturn(List.of(sampleIncident));

        List<Incident> results = incidentService.searchIncidents("   ");

        assertEquals(1, results.size());
        verify(incidentRepository, never()).searchByTitleOrDescription(anyString());
    }

    @Test
    @DisplayName("searchIncidents: returns all incidents for null query")
    void searchIncidents_withNullQuery_returnsAllIncidents() {
        when(incidentRepository.findAll(any(Sort.class))).thenReturn(List.of(sampleIncident));

        List<Incident> results = incidentService.searchIncidents(null);

        assertEquals(1, results.size());
        verify(incidentRepository, never()).searchByTitleOrDescription(anyString());
    }

    // ------ filterIncidents ------

    @Test
    @DisplayName("filterIncidents: returns only HIGH severity incidents when severity filter applied")
    void filterIncidents_withSeverityFilter_returnsMatchingIncidents() {
        Incident lowIncident = new Incident();
        lowIncident.setId(2L);
        lowIncident.setSeverity(Severity.LOW);
        lowIncident.setStatus(Status.OPEN);
        lowIncident.setCategory(Category.MALWARE);
        lowIncident.setCreatedAt(LocalDateTime.now());

        when(incidentRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(sampleIncident, lowIncident));

        List<Incident> results = incidentService.filterIncidents(Severity.HIGH, null, null);

        assertEquals(1, results.size());
        assertEquals(Severity.HIGH, results.get(0).getSeverity());
    }

    @Test
    @DisplayName("filterIncidents: null filters return all incidents")
    void filterIncidents_withNullFilters_returnsAllIncidents() {
        when(incidentRepository.findAll(any(Sort.class))).thenReturn(List.of(sampleIncident));

        List<Incident> results = incidentService.filterIncidents(null, null, null);

        assertEquals(1, results.size());
    }

    // ------ getDashboardStats ------

    @Test
    @DisplayName("getDashboardStats: returns correct counts from repository")
    void getDashboardStats_returnsCorrectStatistics() {
        when(incidentRepository.count()).thenReturn(10L);
        when(incidentRepository.countByStatus(Status.OPEN)).thenReturn(4L);
        when(incidentRepository.countBySeverity(Severity.CRITICAL)).thenReturn(2L);
        when(incidentRepository.countByStatus(Status.RESOLVED)).thenReturn(3L);

        Map<String, Long> stats = incidentService.getDashboardStats();

        assertEquals(10L, stats.get("total"));
        assertEquals(4L,  stats.get("open"));
        assertEquals(2L,  stats.get("critical"));
        assertEquals(3L,  stats.get("resolved"));
    }

    // ------ getAlertIncidents ------

    @Test
    @DisplayName("getAlertIncidents: includes CRITICAL unresolved incidents")
    void getAlertIncidents_includesCriticalUnresolved() {
        Incident criticalIncident = new Incident();
        criticalIncident.setId(10L);
        criticalIncident.setSeverity(Severity.CRITICAL);
        criticalIncident.setStatus(Status.OPEN);
        criticalIncident.setCreatedAt(LocalDateTime.now());

        when(incidentRepository.findBySeverityAndStatusNotIn(
                eq(Severity.CRITICAL), anyList()))
                .thenReturn(List.of(criticalIncident));
        when(incidentRepository.findBySeverityAndUnassigned(Severity.HIGH))
                .thenReturn(List.of());
        when(incidentRepository.findOverdueIncidents(anyList(), any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<Incident> alerts = incidentService.getAlertIncidents();

        assertFalse(alerts.isEmpty());
        assertTrue(alerts.stream().anyMatch(i -> i.getSeverity() == Severity.CRITICAL));
    }

    @Test
    @DisplayName("getAlertIncidents: includes HIGH severity unassigned incidents")
    void getAlertIncidents_includesHighSeverityUnassigned() {
        Incident highUnassigned = new Incident();
        highUnassigned.setId(11L);
        highUnassigned.setSeverity(Severity.HIGH);
        highUnassigned.setStatus(Status.INVESTIGATING);
        highUnassigned.setAssignedTo(null);
        highUnassigned.setCreatedAt(LocalDateTime.now());

        when(incidentRepository.findBySeverityAndStatusNotIn(
                eq(Severity.CRITICAL), anyList()))
                .thenReturn(List.of());
        when(incidentRepository.findBySeverityAndUnassigned(Severity.HIGH))
                .thenReturn(List.of(highUnassigned));
        when(incidentRepository.findOverdueIncidents(anyList(), any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<Incident> alerts = incidentService.getAlertIncidents();

        assertFalse(alerts.isEmpty());
        assertNull(alerts.get(0).getAssignedTo());
    }

    @Test
    @DisplayName("getAlertIncidents: deduplicates incidents appearing in multiple alert categories")
    void getAlertIncidents_deduplicatesAcrossCategories() {
        // Same incident qualifies as both CRITICAL and overdue
        Incident dualAlert = new Incident();
        dualAlert.setId(12L);
        dualAlert.setSeverity(Severity.CRITICAL);
        dualAlert.setStatus(Status.OPEN);
        dualAlert.setCreatedAt(LocalDateTime.now().minusHours(48));

        when(incidentRepository.findBySeverityAndStatusNotIn(
                eq(Severity.CRITICAL), anyList()))
                .thenReturn(List.of(dualAlert));
        when(incidentRepository.findBySeverityAndUnassigned(Severity.HIGH))
                .thenReturn(List.of());
        when(incidentRepository.findOverdueIncidents(anyList(), any(LocalDateTime.class)))
                .thenReturn(List.of(dualAlert));  // same incident appears again

        List<Incident> alerts = incidentService.getAlertIncidents();

        // Must appear only once despite matching two criteria
        assertEquals(1, alerts.size());
    }

    @Test
    @DisplayName("getAlertIncidents: returns empty list when no alert conditions are met")
    void getAlertIncidents_withNoAlerts_returnsEmptyList() {
        when(incidentRepository.findBySeverityAndStatusNotIn(
                eq(Severity.CRITICAL), anyList()))
                .thenReturn(List.of());
        when(incidentRepository.findBySeverityAndUnassigned(Severity.HIGH))
                .thenReturn(List.of());
        when(incidentRepository.findOverdueIncidents(anyList(), any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<Incident> alerts = incidentService.getAlertIncidents();

        assertTrue(alerts.isEmpty());
    }
}
