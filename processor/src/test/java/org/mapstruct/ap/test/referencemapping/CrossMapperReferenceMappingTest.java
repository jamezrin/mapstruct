/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.referencemapping;

import org.mapstruct.ap.test.referencemapping.mappers.AddressMapper;
import org.mapstruct.ap.test.referencemapping.mappers.PersonWithAddressMapper;
import org.mapstruct.ap.test.referencemapping.structs.Address;
import org.mapstruct.ap.test.referencemapping.structs.AddressDto;
import org.mapstruct.ap.test.referencemapping.structs.PersonWithAddress;
import org.mapstruct.ap.test.referencemapping.structs.PersonWithAddressDto;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for ReferenceMapping annotation functionality with cross-mapper delegation.
 * This covers the main use case where @ReferenceMapping delegates to methods on
 * other mappers that are dependencies/used mappers.
 *
 * @author MapStruct Authors
 */
@WithClasses({
    Address.class,
    AddressDto.class,
    AddressMapper.class,
    PersonWithAddress.class,
    PersonWithAddressDto.class,
    PersonWithAddressMapper.class
})
public class CrossMapperReferenceMappingTest {

    @ProcessorTest
    @IssueKey("TBD")
    public void shouldDelegateToUsedMapperMethods() {
        // given
        Address address = new Address( "123 Main St", "Springfield", "12345" );
        PersonWithAddress person = new PersonWithAddress( "John Doe", 30, "john@example.com", address );

        // when - call both the regular method and the reference method
        PersonWithAddressDto result1 = PersonWithAddressMapper.INSTANCE.toPersonWithAddressDto( person );
        PersonWithAddressDto result2 = PersonWithAddressMapper.INSTANCE.convertPersonWithAddress( person );

        // then - both should produce identical results
        assertThat( result1 ).isNotNull();
        assertThat( result2 ).isNotNull();

        // Verify person properties
        assertThat( result1.getName() ).isEqualTo( "John Doe" );
        assertThat( result1.getAge() ).isEqualTo( 30 );
        assertThat( result1.getEmail() ).isEqualTo( "john@example.com" );

        // Verify address was properly mapped via AddressMapper
        assertThat( result1.getAddress() ).isNotNull();
        assertThat( result1.getAddress().getStreet() ).isEqualTo( "123 Main St" );
        assertThat( result1.getAddress().getCity() ).isEqualTo( "Springfield" );
        assertThat( result1.getAddress().getZipCode() ).isEqualTo( "12345" );

        // Both methods should produce identical results
        assertThat( result2.getName() ).isEqualTo( result1.getName() );
        assertThat( result2.getAge() ).isEqualTo( result1.getAge() );
        assertThat( result2.getEmail() ).isEqualTo( result1.getEmail() );
        assertThat( result2.getAddress().getStreet() ).isEqualTo( result1.getAddress().getStreet() );
        assertThat( result2.getAddress().getCity() ).isEqualTo( result1.getAddress().getCity() );
        assertThat( result2.getAddress().getZipCode() ).isEqualTo( result1.getAddress().getZipCode() );
    }

    @ProcessorTest
    @IssueKey("TBD")
    public void shouldDelegateToQualifiedNamedMethod() {
        // given
        Address address = new Address( "456 Oak Ave", "Metropolis", "67890" );
        PersonWithAddress person = new PersonWithAddress( "Jane Smith", 25, "jane@example.com", address );

        // when - call the reference method with qualifiedByName
        PersonWithAddressDto result1 = PersonWithAddressMapper.INSTANCE.toPersonWithAddressDto( person );
        PersonWithAddressDto result2 = PersonWithAddressMapper.INSTANCE.convertPersonWithDetailedAddress( person );

        // then - should properly delegate to the named method
        assertThat( result1 ).isNotNull();
        assertThat( result2 ).isNotNull();

        // Both should have the same basic structure
        assertThat( result2.getName() ).isEqualTo( "Jane Smith" );
        assertThat( result2.getAge() ).isEqualTo( 25 );
        assertThat( result2.getEmail() ).isEqualTo( "jane@example.com" );
        assertThat( result2.getAddress() ).isNotNull();
    }

    @ProcessorTest
    @IssueKey("TBD")
    public void shouldDelegateUpdateMethodsAcrossMappers() {
        // given
        Address address = new Address( "789 Pine St", "Gotham", "54321" );
        PersonWithAddress person = new PersonWithAddress( "Bob Johnson", 35, "bob@example.com", address );
        PersonWithAddressDto target1 = new PersonWithAddressDto();
        PersonWithAddressDto target2 = new PersonWithAddressDto();

        // when - call both update methods
        PersonWithAddressMapper.INSTANCE.updatePersonWithAddressDto( person, target1 );
        PersonWithAddressMapper.INSTANCE.updatePersonWithAddressDtoReference( person, target2 );

        // then - both should produce identical results
        assertThat( target1.getName() ).isEqualTo( "Bob Johnson" );
        assertThat( target1.getAge() ).isEqualTo( 35 );
        assertThat( target1.getEmail() ).isEqualTo( "bob@example.com" );

        // Verify address was properly updated
        assertThat( target1.getAddress() ).isNotNull();
        assertThat( target1.getAddress().getStreet() ).isEqualTo( "789 Pine St" );
        assertThat( target1.getAddress().getCity() ).isEqualTo( "Gotham" );
        assertThat( target1.getAddress().getZipCode() ).isEqualTo( "54321" );

        // Both methods should produce identical results
        assertThat( target2.getName() ).isEqualTo( target1.getName() );
        assertThat( target2.getAge() ).isEqualTo( target1.getAge() );
        assertThat( target2.getEmail() ).isEqualTo( target1.getEmail() );
        assertThat( target2.getAddress().getStreet() ).isEqualTo( target1.getAddress().getStreet() );
        assertThat( target2.getAddress().getCity() ).isEqualTo( target1.getAddress().getCity() );
        assertThat( target2.getAddress().getZipCode() ).isEqualTo( target1.getAddress().getZipCode() );
    }
}
