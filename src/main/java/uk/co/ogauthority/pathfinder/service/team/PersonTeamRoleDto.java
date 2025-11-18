package uk.co.ogauthority.pathfinder.service.team;

public record PersonTeamRoleDto(
    int personId,
    long resId,
    String portalTeamType,
    String role
) {
}
