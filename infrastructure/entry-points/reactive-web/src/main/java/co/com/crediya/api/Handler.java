package co.com.crediya.api;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.api.dto.CreateApplicationDTO;
import co.com.crediya.api.mapper.ApplicationDTOMapper;
import co.com.crediya.model.application.criteria.SearchCriteria;
import co.com.crediya.usecase.application.ApplicationUseCase;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final ApplicationUseCase applicationUseCase;
    private final ApplicationDTOMapper applicationMapper;

    public Mono<ServerResponse> createApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateApplicationDTO.class)
            .map(applicationMapper::toModel)
            .transform(applicationUseCase::saveApplication)
            .flatMap(createdApplication -> ServerResponse.created(URI.create("/applicationDetails/" + createdApplication.applicationId()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createdApplication));
    }

    public Mono<ServerResponse> getAllApplications(ServerRequest serverRequest) {
        return applicationUseCase.getAllApplications().collectList()
            .flatMap(applicationsList -> ServerResponse.ok().bodyValue(applicationsList));
    }

    public Mono<ServerResponse> getByCriteriaPaginated(ServerRequest request) {
        return extractSearchCriteria(request)
            .flatMap(criteria -> applicationUseCase.getByCriteriaPaginated(criteria))
            .flatMap(result -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(result));
    }

    private Mono<SearchCriteria> extractSearchCriteria(ServerRequest request) {
        return Mono.fromCallable(() -> {
            Map<String, Object> filters = new HashMap<>();
            
            request.queryParam("userIdNumber")
                .ifPresent(value -> filters.put("user_id_number", Long.valueOf(value)));
            request.queryParam("loanTypeId")
                .ifPresent(value -> filters.put("loan_type_id", Integer.valueOf(value)));
            request.queryParam("loanStatusId")
                .ifPresent(value -> filters.put("loan_status_id", Integer.valueOf(value)));
            
            String sortBy = request.queryParam("sortBy").orElse(null);
            String sortDirection = request.queryParam("sortDirection").orElse("ASC");
            int page = request.queryParam("page").map(Integer::valueOf).orElse(0);
            int size = request.queryParam("size").map(Integer::valueOf).orElse(10);
            
            return SearchCriteria.builder()
                .filters(filters.isEmpty() ? null : filters)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();
        });
    }
}
