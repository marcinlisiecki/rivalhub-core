package com.rivalhub.common;


import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();


        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setAdminUsers));

        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setAddedDate));

        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setEventTypeInOrganization));

        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setUserList));

        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setStationList));
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        return modelMapper;
    }
}
