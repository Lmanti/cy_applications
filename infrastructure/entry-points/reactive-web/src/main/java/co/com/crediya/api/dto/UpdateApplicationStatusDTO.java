package co.com.crediya.api.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "DTO para actualizar el estado de una solicitud de crédito")
public class UpdateApplicationStatusDTO {
    @Schema(description = "ID del crédito", example = "fe38dd90-fe6a-4137-afab-5400b72c2ec9")
    private UUID applicationId;
    @Schema(description = "Nuevo estado del crédito (1: Pendiente, 2: Aprobado, etc.)", example = "2")
    private Integer loanStatusId;
}
