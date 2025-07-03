package com.haiphamcoder.integrated.domain.model;

import java.util.Set;

public enum EmailTemplate {
    OTP("email-otp.html", Set.of("username", "otp", "expireMinutes")),
    WELCOME("welcome-email.html", Set.of("username"));
    
    private final String templateFile;
    private final Set<String> requiredVariables;

    EmailTemplate(String templateFile, Set<String> requiredVariables) {
        this.templateFile = templateFile;
        this.requiredVariables = requiredVariables;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public Set<String> getRequiredVariables() {
        return requiredVariables;
    }

    public static EmailTemplate fromTemplateFile(String templateFile) {
        for (EmailTemplate template : values()) {
            if (template.getTemplateFile().equals(templateFile)) {
                return template;
            }
        }
        throw new IllegalArgumentException("Unknown template file: " + templateFile);
    }
}