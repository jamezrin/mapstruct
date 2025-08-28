/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.referencemapping.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReferenceMapping;
import org.mapstruct.ap.test.referencemapping.structs.Address;
import org.mapstruct.ap.test.referencemapping.structs.AddressDto;
import org.mapstruct.factory.Mappers;

/**
 * Cross-mapper test mapper that uses PersonAddressMapper for address mapping delegation.
 *
 * @author MapStruct Authors
 */
@Mapper(uses = PersonAddressMapper.class)
public interface CrossMapperTestMapper {

    CrossMapperTestMapper INSTANCE = Mappers.getMapper( CrossMapperTestMapper.class );

    /**
     * Method that should delegate to PersonAddressMapper.toAddressDto via @ReferenceMapping.
     * This tests cross-mapper delegation (the main purpose of @ReferenceMapping).
     */
    @ReferenceMapping
    AddressDto convertAddress(Address address);
}
