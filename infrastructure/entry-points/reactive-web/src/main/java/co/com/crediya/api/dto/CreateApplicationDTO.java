package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "DTO para crear una nueva solicitud de crédito")
public class CreateApplicationDTO {
    @Schema(description = "Número de identificación del usuario", example = "1234567890")
    private Long userIdNumber;
    @Schema(description = "Monto del crédito", example = "2500000.0")
    private Double loanAmount;
    @Schema(description = "Intereses (EA) del crédito", example = "12.5")
    private Double loanTerm;
    @Schema(description = "Tipo de crédito (1: Libre inversión, 2: Hipotecario, etc.)", example = "1")
    private Integer loanTypeId;
}