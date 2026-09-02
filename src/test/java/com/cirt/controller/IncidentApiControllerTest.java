package com.cirt.controller;

import com.cirt.dto.IncidentDto;
import com.cirt.model.Category;
import com.cirt.model.Incident;
import com.cirt.model.Severity;
import com.cirt.model.Status;
import com.cirt.service.IncidentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice tests for IncidentApiController using @WebMvcTest.
 *
 * Only the web layer is loaded — no database, no full Spring context.
 * IncidentService is mocked with @MockBean.
 */
@WebMvcTest(IncidentApiController.class)
@DisplayName("IncidentApiController Tests")
class IncidentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncidentService incidentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Incident sampleIncident;
    private IncidentDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleIncident = new Incident();
        sampleIncident.setId(1L);
        sampleIncident.setTitle("SQL Injection Attempt");
        sampleIncident.setDescription("Detected SQL injection in login form.");
        sampleIncident.setCategory(Category.UNAUTHORIZED_ACCESS);
        sampleIncident.setSeverity(Severity.CRITICAL);
        sampleIncident.setStatus(Status.OPEN);
        sampleIncident.setReportedBy("Security Bot");
        sampleIncident.setCreatedAt(LocalDateTime.now());
        sampleIncident.setUpdatedAt(LocalDateTime.now());

        sampleDto = new IncidentDto();
        sampleDto.setTitle("SQL Injection Attempt");
        sampleDto.setDescription("Detected SQL injection in login form.");
        sampleDto.setCategory(Category.UNAUTHORIZED_ACCESS);
        sampleDto.setSeverity(Severity.CRITICAL);
        sampleDto.setReportedBy("Security Bot");
    }

    // ------ GET /api/incidents ------

    @Test
    @DisplayName("GET /api/incidents: returns 200 with list of incidents")
    void getAllIncidents_returnsOkWithList() throws Exception {
        when(incidentService.getAllIncidents()).thenReturn(List.of(sampleIncident));

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("SQL Injection Attempt")))
                .andExpect(jsonPath("$[0].severity", is("CRITICAL")));
    }

    @Test
    @DisplayName("GET /api/incidents: returns empty list when no incidents exist")
    void getAllIncidents_withNoIncidents_returnsEmptyList() throws Exception {
        when(incidentService.getAllIncidents()).thenReturn(List.of());

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/incidents?search=...: calls searchIncidents with query")
    void getAllIncidents_withSearchParam_callsSearchService() throws Exception {
        when(incidentService.searchIncidents("injection")).thenReturn(List.of(sampleIncident));

        mockMvc.perform(get("/api/incidents").param("search", "injection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(incidentService).searchIncidents("injection");
        verify(incidentService, never()).getAllIncidents();
    }

    // ------ GET /api/incidents/{id} ------

    @Test
    @DisplayName("GET /api/incidents/{id}: returns 200 with incident when found")
    void getIncidentById_withValidId_returnsOk() throws Exception {
        when(incidentService.getIncidentById(1L)).thenReturn(sampleIncident);

        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("SQL Injection Attempt")));
    }

    @Test
    @DisplayName("GET /api/incidents/{id}: returns 404 when incident not found")
    void getIncidentById_withNonExistentId_returnsNotFound() throws Exception {
        when(incidentService.getIncidentById(999L))
                .thenThrow(new RuntimeException("Incident not found with id: 999"));

        mockMvc.perform(get("/api/incidents/999"))
                .andExpect(status().isNotFound());
    }

    // ------ POST /api/incidents ------

    @Test
    @DisplayName("POST /api/incidents: returns 201 Created with valid payload")
    void createIncident_withValidPayload_returnsCreated() throws Exception {
        when(incidentService.createIncident(any(IncidentDto.class))).thenReturn(sampleIncident);

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("SQL Injection Attempt")));
    }

    @Test
    @DisplayName("POST /api/incidents: returns 400 Bad Request when title is missing")
    void createIncident_withMissingTitle_returnsBadRequest() throws Exception {
        IncidentDto invalidDto = new IncidentDto();
        // title is missing (required)
        invalidDto.setDescription("Some description");
        invalidDto.setCategory(Category.MALWARE);
        invalidDto.setSeverity(Severity.HIGH);
        invalidDto.setReportedBy("John");

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/incidents: returns 400 when required fields are all missing")
    void createIncident_withEmptyPayload_returnsBadRequest() throws Exception {
        IncidentDto emptyDto = new IncidentDto();

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDto)))
                .andExpect(status().isBadRequest());
    }

    // ------ PUT /api/incidents/{id} ------

    @Test
    @DisplayName("PUT /api/incidents/{id}: returns 200 with updated incident")
    void updateIncident_withValidPayload_returnsOk() throws Exception {
        when(incidentService.updateIncident(eq(1L), any(IncidentDto.class)))
                .thenReturn(sampleIncident);

        mockMvc.perform(put("/api/incidents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/incidents/{id}: returns 404 when incident not found")
    void updateIncident_withNonExistentId_returnsNotFound() throws Exception {
        when(incidentService.updateIncident(eq(999L), any(IncidentDto.class)))
                .thenThrow(new RuntimeException("Incident not found with id: 999"));

        mockMvc.perform(put("/api/incidents/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isNotFound());
    }

    // ------ DELETE /api/incidents/{id} ------

    @Test
    @DisplayName("DELETE /api/incidents/{id}: returns 204 No Content on success")
    void deleteIncident_withValidId_returnsNoContent() throws Exception {
        doNothing().when(incidentService).deleteIncident(1L);

        mockMvc.perform(delete("/api/incidents/1"))
                .andExpect(status().isNoContent());

        verify(incidentService).deleteIncident(1L);
    }

    @Test
    @DisplayName("DELETE /api/incidents/{id}: returns 404 when incident not found")
    void deleteIncident_withNonExistentId_returnsNotFound() throws Exception {
        doThrow(new RuntimeException("Incident not found with id: 999"))
                .when(incidentService).deleteIncident(999L);

        mockMvc.perform(delete("/api/incidents/999"))
                .andExpect(status().isNotFound());
    }

    // ------ GET /api/incidents/stats ------

    @Test
    @DisplayName("GET /api/incidents/stats: returns dashboard statistics")
    void getStats_returnsStatisticsMap() throws Exception {
        when(incidentService.getDashboardStats()).thenReturn(
                Map.of("total", 5L, "open", 2L, "critical", 1L, "resolved", 1L));

        mockMvc.perform(get("/api/incidents/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(5)))
                .andExpect(jsonPath("$.open", is(2)));
    }

    // ------ GET /api/incidents/alerts ------

    @Test
    @DisplayName("GET /api/incidents/alerts: returns alert incidents")
    void getAlerts_returnsListOfAlertIncidents() throws Exception {
        when(incidentService.getAlertIncidents()).thenReturn(List.of(sampleIncident));

        mockMvc.perform(get("/api/incidents/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
