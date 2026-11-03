package gt.edu.umg.core.entities.Dtos.Response;

import java.util.List;

public record PageResponseDto<T>(
        List<T> data,
        PageMetadataDto metadata
) {}
