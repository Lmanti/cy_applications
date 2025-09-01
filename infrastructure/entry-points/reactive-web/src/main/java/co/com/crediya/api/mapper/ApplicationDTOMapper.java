package co.com.crediya.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.crediya.api.dto.CreateApplicationDTO;
import co.com.crediya.model.application.Application;

@Mapper(componentModel = "spring")
public interface ApplicationDTOMapper {
    @Mapping(target = "applicationId", ignore = true)
    @Mapping(target = "loanStatusId", ignore = true)
    Application toModel(CreateApplicationDTO createApplicationDTO);
}
