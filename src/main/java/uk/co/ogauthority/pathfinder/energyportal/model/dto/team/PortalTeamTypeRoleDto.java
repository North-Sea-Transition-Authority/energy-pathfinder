package uk.co.ogauthority.pathfinder.energyportal.model.dto.team;

public record PortalTeamTypeRoleDto(
    String rolePortalTeamType,
    String roleName,
    String roleTitle,
    String roleDescription,
    int roleDisplaySequence
) {
}