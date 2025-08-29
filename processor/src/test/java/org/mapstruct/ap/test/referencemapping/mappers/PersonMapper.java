/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.referencemapping.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReferenceMapping;
import org.mapstruct.ap.test.referencemapping.structs.Person;
import org.mapstruct.ap.test.referencemapping.structs.PersonDto;
import org.mapstruct.factory.Mappers;

/**
 * Test mapper for ReferenceMapping annotation functionality.
 *
 * @author MapStruct Authors
 */
@Mapper
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper( PersonMapper.class );

    /**
     * Regular mapping method that serves as the target for reference mapping.
     */
    PersonDto toPersonDto(Person person);

    /**
     * Method annotated with @ReferenceMapping should delegate to the existing toPersonDto method.
     */
    @ReferenceMapping
    PersonDto convertPersonToDto(Person person);

    /**
     * Another reference mapping method with qualifiers.
     */
    @ReferenceMapping(qualifiedByName = "personMapping")
    PersonDto mapPersonToDto(Person person);

    /**
     * Update method that should also be referenceable.
     */
    void updatePersonDto(Person person, @org.mapstruct.MappingTarget PersonDto personDto);

    /**
     * Reference mapping for update method.
     */
    @ReferenceMapping
    void updatePersonDtoReference(Person person, @org.mapstruct.MappingTarget PersonDto personDto);
}
