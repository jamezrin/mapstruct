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

@Mapper
public abstract class IntraMapperWarningTestMapper {

    // Test: Should give WARNING - intra-mapper delegation
    @ReferenceMapping
    public abstract AddressDto testIntraMapping(Address address);

    // Target method for delegation
    public AddressDto toAddressDto(Address address) {
        if ( address == null ) {
            return null;
        }
        AddressDto dto = new AddressDto();
        dto.setStreet( address.getStreet() );
        dto.setCity( address.getCity() );
        return dto;
    }
}
