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
public abstract class IntraMapperTestMapper {

    // Test intra-mapper reference (should show warning)
    @ReferenceMapping
    public abstract AddressDto convertAddressIntra(Address address);

    // The actual implementation that should be referenced
    public AddressDto toAddressDto(Address address) {
        if ( address == null ) {
            return null;
        }
        AddressDto addressDto = new AddressDto();
        addressDto.setStreet( address.getStreet() );
        addressDto.setCity( address.getCity() );
        return addressDto;
    }
}
