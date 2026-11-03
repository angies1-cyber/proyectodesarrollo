package gt.edu.umg.core.entities.Dtos.Response;

public record PageMetadataDto(
        long totalRecords,
        int page,
        int pageSize,
        int totalPages,
        boolean hasPreviousPage,
        boolean hasNextPage
) {}
