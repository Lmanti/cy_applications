package co.com.crediya.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.api.dto.CreateApplicationDTO;
import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.record.ApplicationRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

@Configuration
public class RouterRest {
    private final String applicationsBaseUrl = "/api/v1/solicitud";

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = applicationsBaseUrl, 
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getAllApplications",
                tags = {"Solicitudes"},
                summary = "Obtener todas las solicitudes de crédito",
                description = "Retorna una lista con todos las solicitudes de crédito registradas",
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Lista de solicitudes de crédito obtenida exitosamente",
                        content = @Content(schema = @Schema(implementation = Application.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = applicationsBaseUrl, 
            method = RequestMethod.POST,
            operation = @Operation(
                operationId = "createApplication",
                tags = {"Solicitudes"},
                summary = "Crear una nueva solicitud de crédito",
                description = "Crea una nueva solicitud de crédito en el sistema",
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateApplicationDTO.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201", 
                        description = "Solicitud de crédito creada exitosamente",
                        content = @Content(schema = @Schema(implementation = ApplicationRecord.class))
                    ),
                    @ApiResponse(
                        responseCode = "400", 
                        description = "Datos de solicitud de crédito inválidos"
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(applicationsBaseUrl), handler::getAllApplications)
                .andRoute(POST(applicationsBaseUrl), handler::createApplication);
                // .and(route(GET("/api/otherusercase/path"), handler::listenGETOtherUseCase));
    }
}
