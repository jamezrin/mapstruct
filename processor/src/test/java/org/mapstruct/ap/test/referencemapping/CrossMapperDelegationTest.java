/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.referencemapping;

import org.mapstruct.ap.test.referencemapping.mappers.CrossMapperTestMapper;
import org.mapstruct.ap.test.referencemapping.mappers.PersonAddressMapper;
import org.mapstruct.ap.test.referencemapping.structs.Address;
import org.mapstruct.ap.test.referencemapping.structs.AddressDto;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for cross-mapper delegation with ReferenceMapping annotation.
 *
 * @author MapStruct Authors
 */
@WithClasses({
    Address.class,
    AddressDto.class,
    PersonAddressMapper.class,
    CrossMapperTestMapper.class
})
public class CrossMapperDelegationTest {

    @ProcessorTest
    @IssueKey("TBD")
    public void shouldDelegateToCrossMapperMethod() {
        // given
        Address address = new Address( "123 Main St", "Springfield", "12345" );

        // when
        AddressDto result = CrossMapperTestMapper.INSTANCE.convertAddress( address );

        // then
        assertThat( result ).isNotNull();
        assertThat( result.getStreet() ).isEqualTo( "123 Main St" );
        assertThat( result.getCity() ).isEqualTo( "Springfield" );
        assertThat( result.getZipCode() ).isEqualTo( "12345" );
    }
}
