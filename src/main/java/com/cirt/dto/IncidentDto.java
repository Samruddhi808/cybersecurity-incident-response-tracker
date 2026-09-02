package com.cirt.dto;

import com.cirt.model.Category;
import com.cirt.model.Severity;
import com.cirt.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating and updating an Incident.
 *
 * This separates the HTTP request data from the JPA entity,
 * keeping validation annotations in one place and the entity clean.
 */
public class IncidentDto {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Severity is required")
    private Severity severity;

    @NotBlank(message = "Reporter name is required")
    private String reportedBy;

    // Optional fields
    private String assignedTo;
    private Status status;
    private String resolution;

    // ------ Getters and Setters ------

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}
