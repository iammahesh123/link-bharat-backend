package com.linkbharat.linkbharatbackend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {
    private String name;
    private String headLine;
    private String avatarUrl;
    private String coverageImageUrl;
    private String location;
    private int connections;
    private String about;
    private Set<String> skills = new HashSet<>();
    private boolean isConnected;
    private boolean isPending;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCoverageImageUrl() {
        return coverageImageUrl;
    }

    public void setCoverageImageUrl(String coverageImageUrl) {
        this.coverageImageUrl = coverageImageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    // ── Onboarding ──────────────────────────────────────────────────────────
    private Boolean isOnboardingComplete;

    public Boolean getIsOnboardingComplete() {
        return isOnboardingComplete;
    }

    public void setIsOnboardingComplete(Boolean isOnboardingComplete) {
        this.isOnboardingComplete = isOnboardingComplete;
    }
}
