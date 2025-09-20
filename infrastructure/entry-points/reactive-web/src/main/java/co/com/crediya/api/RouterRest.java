package co.com.crediya.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.api.dto.CreateApplicationDTO;
import co.com.crediya.api.dto.UpdateApplicationStatusDTO;
import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.criteria.PageResult;
import co.com.crediya.model.application.record.ApplicationRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
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
                security = { @SecurityRequirement(name = "bearer-jwt") },
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
        ),
        @RouterOperation(
            path = applicationsBaseUrl + "/filtrarPaginado", 
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getByCriteriaPaginated",
                tags = {"Solicitudes"},
                summary = "Obtener todas las solicitudes de crédito por criteria",
                description = "Retorna una lista con todos las solicitudes de crédito registradas que cumplan con los criterios",
                parameters = {
                    @Parameter(
                        name = "userEmail", 
                        description = "E-mail del usuario",
                        in = ParameterIn.QUERY,
                        required = false,
                        schema = @Schema(type = "string")
                    ),
                    @Parameter(
                        name = "loanTypeId", 
                        description = "Tipo de crédito",
                        in = ParameterIn.QUERY,
                        required = false,
                        schema = @Schema(type = "integer", format = "int16")
                    ),
                    @Parameter(
                        name = "loanStatusId", 
                        description = "Estado del crédito",
                        in = ParameterIn.QUERY,
                        required = false,
                        schema = @Schema(type = "integer", format = "int16")
                    ),
                    @Parameter(
                        name = "sortBy", 
                        description = "Ordenado por",
                        in = ParameterIn.QUERY,
                        required = false,
                        schema = @Schema(type = "string")
                    ),
                    @Parameter(
                        name = "sortDirection", 
                        description = "Sentido del ordenamiento",
                        in = ParameterIn.QUERY,
                        required = false,
                        schema = @Schema(type = "string")
                    ),
                    @Parameter(
                        name = "page", 
                        description = "Número de página",
                        in = ParameterIn.QUERY,
                        required = false,
                        schema = @Schema(type = "integer", format = "int16")
                    ),
                    @Parameter(
                        name = "size", 
                        description = "Tamaño de página",
                        in = ParameterIn.QUERY,
                        required = false,
                        schema = @Schema(type = "integer", format = "int16")
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Lista de solicitudes de crédito obtenida exitosamente",
                        content = @Content(schema = @Schema(implementation = PageResult.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = applicationsBaseUrl, 
            method = RequestMethod.PUT,
            operation = @Operation(
                operationId = "updateApplicationStatus",
                tags = {"Solicitudes"},
                summary = "Actualizar el estado de una solicitud de crédito",
                description = "Actualiza el estado de una solicitud de crédito en el sistema",
                security = { @SecurityRequirement(name = "bearer-jwt") },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateApplicationStatusDTO.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201", 
                        description = "Solicitud de crédito actualizada exitosamente",
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
            .andRoute(POST(applicationsBaseUrl), handler::createApplication)
            .andRoute(GET(applicationsBaseUrl + "/filtrarPaginado"), handler::getByCriteriaPaginated)
            .andRoute(PUT(applicationsBaseUrl), handler::updateApplicationStatus);
    }
}
