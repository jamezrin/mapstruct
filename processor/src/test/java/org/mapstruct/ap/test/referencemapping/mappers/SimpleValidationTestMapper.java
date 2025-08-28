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
public abstract class SimpleValidationTestMapper {

    // Test: Should give ERROR - @ReferenceMapping on implemented method
    @ReferenceMapping
    public AddressDto implementedMethod(Address address) {
        return new AddressDto();
    }

    // Valid target method
    public abstract AddressDto validMethod(Address address);
}
