package com.feed_grabber.core.role;

import com.feed_grabber.core.role.dto.RoleCreationDto;
import com.feed_grabber.core.role.dto.RoleDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {
    RoleMapper MAPPER = Mappers.getMapper(RoleMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "companyId", target = "company.id")
    Role roleDtoToModel(RoleCreationDto roleDto);

    RoleDetailsDto roleToRoleDto(Role role);
}
