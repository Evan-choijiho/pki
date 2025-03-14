package com.peloton.boilerplate.model.dto.common;

import org.mapstruct.*;

@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenericMapper<D, E> {
    D toDto(E e);

    E toEntity(D d);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateNotNullFromDto(D dto, @MappingTarget E entity);
}
