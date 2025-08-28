/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.referencemapping.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ap.test.referencemapping.structs.Address;
import org.mapstruct.ap.test.referencemapping.structs.AddressDto;
import org.mapstruct.factory.Mappers;

/**
 * Mapper that provides address mapping methods to be used by other mappers.
 *
 * @author MapStruct Authors
 */
@Mapper
public interface PersonAddressMapper {

    PersonAddressMapper INSTANCE = Mappers.getMapper( PersonAddressMapper.class );

    /**
     * Address mapping method that can be referenced by other mappers.
     */
    AddressDto toAddressDto(Address address);
}
