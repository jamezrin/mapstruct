/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.referencemapping.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ap.test.referencemapping.structs.Address;
import org.mapstruct.ap.test.referencemapping.structs.AddressDto;
import org.mapstruct.factory.Mappers;

/**
 * Dedicated mapper for Address mappings that will be used by other mappers.
 * This represents a common scenario where specialized mappers handle specific types.
 *
 * @author MapStruct Authors
 */
@Mapper
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper( AddressMapper.class );

    /**
     * Standard address mapping method.
     */
    AddressDto toAddressDto(Address address);

    /**
     * Named address mapping method for qualified references.
     */
    @Named("detailedAddressMapping")
    AddressDto toDetailedAddressDto(Address address);

    /**
     * Update method for address mapping.
     */
    void updateAddressDto(Address address, @org.mapstruct.MappingTarget AddressDto addressDto);
}
