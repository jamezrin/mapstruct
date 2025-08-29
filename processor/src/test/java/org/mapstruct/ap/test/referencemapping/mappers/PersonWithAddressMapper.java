/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.referencemapping.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReferenceMapping;
import org.mapstruct.ap.test.referencemapping.structs.PersonWithAddress;
import org.mapstruct.ap.test.referencemapping.structs.PersonWithAddressDto;
import org.mapstruct.factory.Mappers;

/**
 * Cross-mapper delegation test mapper that uses AddressMapper.
 * This demonstrates the main use case for @ReferenceMapping: delegating to methods
 * on other mappers that are dependencies/used mappers.
 *
 * @author MapStruct Authors
 */
@Mapper(uses = AddressMapper.class)
public interface PersonWithAddressMapper {

    PersonWithAddressMapper INSTANCE = Mappers.getMapper( PersonWithAddressMapper.class );

    /**
     * Standard mapping method that maps the person and delegates address mapping to AddressMapper.
     */
    @Mapping(target = "address", source = "address")
    PersonWithAddressDto toPersonWithAddressDto(PersonWithAddress person);

    /**
     * Reference mapping method that should delegate the entire mapping to another method.
     * This should ideally delegate to toPersonWithAddressDto, but leverage @ReferenceMapping
     * to avoid code duplication.
     */
    @ReferenceMapping
    PersonWithAddressDto convertPersonWithAddress(PersonWithAddress person);

    /**
     * Reference mapping with qualifier to delegate to a specific named method.
     * This demonstrates using qualifiedByName to resolve to the detailed address mapping.
     */
    @ReferenceMapping(qualifiedByName = "detailedAddressMapping")
    PersonWithAddressDto convertPersonWithDetailedAddress(PersonWithAddress person);

    /**
     * Update method that delegates to existing update functionality.
     */
    void updatePersonWithAddressDto(PersonWithAddress person,
                                    @org.mapstruct.MappingTarget PersonWithAddressDto personDto);

    /**
     * Reference update method that should delegate to the update method above.
     */
    @ReferenceMapping
    void updatePersonWithAddressDtoReference(PersonWithAddress person,
                                             @org.mapstruct.MappingTarget PersonWithAddressDto personDto);
}
