package gt.edu.umg.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record AdminRolesRequestDto(@NotEmpty Set<Long> roleIds) {}
