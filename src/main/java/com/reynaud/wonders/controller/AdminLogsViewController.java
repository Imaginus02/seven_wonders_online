package com.reynaud.wonders.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving the admin logs viewer page
 * Accessible only to users with ROLE_ADMIN
 */
@Controller
public class AdminLogsViewController {

    /**
     * Serve the admin logs viewer page
     * Only accessible to administrators
     */
    @GetMapping("/admin/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminLogsPage() {
        return "admin-logs";
    }
}
