package com.cirt.controller;

import com.cirt.dto.IncidentDto;
import com.cirt.model.Category;
import com.cirt.model.Incident;
import com.cirt.model.Severity;
import com.cirt.model.Status;
import com.cirt.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MVC Controller — serves HTML pages via Thymeleaf templates.
 *
 * Routes:
 *   GET  /            → dashboard
 *   GET  /incidents   → incident list (with optional search/filter params)
 *   GET  /incidents/new           → create form
 *   POST /incidents               → save new incident
 *   GET  /incidents/{id}          → incident detail
 *   GET  /incidents/{id}/edit     → edit form
 *   POST /incidents/{id}/edit     → save edits
 *   POST /incidents/{id}/status   → update status only
 *   GET  /alerts                  → alert/exception view
 */
@Controller
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // ------ Dashboard ------

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stats", incidentService.getDashboardStats());
        model.addAttribute("incidents", incidentService.getAllIncidents());
        return "dashboard";
    }

    // ------ Incident List + Search + Filter ------

    @GetMapping("/incidents")
    public String listIncidents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            Model model) {

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

        model.addAttribute("incidents", incidents);
        model.addAttribute("search", search);
        model.addAttribute("selectedSeverity", severity);
        model.addAttribute("selectedStatus",   status);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("severities", Severity.values());
        model.addAttribute("statuses",   Status.values());
        model.addAttribute("categories", Category.values());
        return "incident-list";
    }

    // ------ Create Incident ------

    @GetMapping("/incidents/new")
    public String newIncidentForm(Model model) {
        model.addAttribute("incidentDto", new IncidentDto());
        model.addAttribute("categories", Category.values());
        model.addAttribute("severities", Severity.values());
        return "incident-form";
    }

    @PostMapping("/incidents")
    public String createIncident(
            @Valid @ModelAttribute("incidentDto") IncidentDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", Category.values());
            model.addAttribute("severities", Severity.values());
            return "incident-form";
        }
        Incident created = incidentService.createIncident(dto);
        return "redirect:/incidents/" + created.getId();
    }

    // ------ Incident Detail ------

    @GetMapping("/incidents/{id}")
    public String incidentDetail(@PathVariable Long id, Model model) {
        Incident incident = incidentService.getIncidentById(id);
        model.addAttribute("incident", incident);
        model.addAttribute("statuses", Status.values());
        return "incident-detail";
    }

    // ------ Edit Incident ------

    @GetMapping("/incidents/{id}/edit")
    public String editIncidentForm(@PathVariable Long id, Model model) {
        Incident incident = incidentService.getIncidentById(id);

        IncidentDto dto = new IncidentDto();
        dto.setTitle(incident.getTitle());
        dto.setDescription(incident.getDescription());
        dto.setCategory(incident.getCategory());
        dto.setSeverity(incident.getSeverity());
        dto.setStatus(incident.getStatus());
        dto.setReportedBy(incident.getReportedBy());
        dto.setAssignedTo(incident.getAssignedTo());
        dto.setResolution(incident.getResolution());

        model.addAttribute("incidentDto", dto);
        model.addAttribute("incidentId", id);
        model.addAttribute("categories", Category.values());
        model.addAttribute("severities", Severity.values());
        model.addAttribute("statuses",   Status.values());
        return "incident-form";
    }

    @PostMapping("/incidents/{id}/edit")
    public String updateIncident(
            @PathVariable Long id,
            @Valid @ModelAttribute("incidentDto") IncidentDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("incidentId", id);
            model.addAttribute("categories", Category.values());
            model.addAttribute("severities", Severity.values());
            model.addAttribute("statuses",   Status.values());
            return "incident-form";
        }
        incidentService.updateIncident(id, dto);
        return "redirect:/incidents/" + id;
    }

    // ------ Status Update (quick action from detail page) ------

    @PostMapping("/incidents/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam Status status) {

        incidentService.updateStatus(id, status);
        return "redirect:/incidents/" + id;
    }

    // ------ Alerts Page ------

    @GetMapping("/alerts")
    public String alertsPage(Model model) {
        model.addAttribute("alerts", incidentService.getAlertIncidents());
        return "alerts";
    }

    // ------ Helper ------

    /**
     * Safely parses a String to an enum value.
     * Returns null if the string is null, blank, or not a valid enum constant.
     */
    private <T extends Enum<T>> T parseEnum(Class<T> enumType, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
