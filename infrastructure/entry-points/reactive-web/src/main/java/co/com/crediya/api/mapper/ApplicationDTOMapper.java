package co.com.crediya.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.crediya.api.dto.CreateApplicationDTO;
import co.com.crediya.api.dto.UpdateApplicationStatusDTO;
import co.com.crediya.model.application.Application;

@Mapper(componentModel = "spring")
public interface ApplicationDTOMapper {
    @Mapping(target = "applicationId", ignore = true)
    @Mapping(target = "loanStatusId", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    Application toModel(CreateApplicationDTO createApplicationDTO);

    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "loanAmount", ignore = true)
    @Mapping(target = "loanTerm", ignore = true)
    @Mapping(target = "loanTypeId", ignore = true)
    @Mapping(target = "userIdNumber", ignore = true)
    Application toModel(UpdateApplicationStatusDTO updateApplicationStatusDTO);
}
