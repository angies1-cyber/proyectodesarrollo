package gt.edu.umg.auth.dto;

import jakarta.validation.constraints.NotNull;

public record AdminEnabledRequestDto(@NotNull Boolean enabled) {}
