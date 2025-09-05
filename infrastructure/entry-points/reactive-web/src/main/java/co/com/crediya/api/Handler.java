package co.com.crediya.api;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.api.dto.CreateApplicationDTO;
import co.com.crediya.api.mapper.ApplicationDTOMapper;
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
}
