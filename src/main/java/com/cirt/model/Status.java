package com.cirt.model;

/**
 * Tracks the lifecycle stage of a cybersecurity incident.
 *
 * Workflow progression:
 *   OPEN → INVESTIGATING → CONTAINED → RESOLVED → CLOSED
 */
public enum Status {
    OPEN,
    INVESTIGATING,
    CONTAINED,
    RESOLVED,
    CLOSED
}
