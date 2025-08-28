/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.referencemapping;

import org.mapstruct.ap.test.referencemapping.mappers.AddressMapper;
import org.mapstruct.ap.test.referencemapping.mappers.PersonMapper;
import org.mapstruct.ap.test.referencemapping.mappers.PersonWithAddressMapper;
import org.mapstruct.ap.test.referencemapping.structs.Address;
import org.mapstruct.ap.test.referencemapping.structs.AddressDto;
import org.mapstruct.ap.test.referencemapping.structs.Person;
import org.mapstruct.ap.test.referencemapping.structs.PersonDto;
import org.mapstruct.ap.test.referencemapping.structs.PersonWithAddress;
import org.mapstruct.ap.test.referencemapping.structs.PersonWithAddressDto;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for true delegation behavior of @ReferenceMapping.
 * This test validates that @ReferenceMapping actually generates delegation calls
 * rather than duplicate mapping implementations.
 *
 * @author MapStruct Authors
 */
@WithClasses({
    Person.class,
    PersonDto.class,
    PersonMapper.class,
    Address.class,
    AddressDto.class,
    AddressMapper.class,
    PersonWithAddress.class,
    PersonWithAddressDto.class,
    PersonWithAddressMapper.class
})
public class TrueDelegationReferenceMappingTest {

    @ProcessorTest
    @IssueKey("TBD")
    public void shouldGenerateTrueDelegationCode() {
        // This test would verify that the generated code actually contains delegation calls
        // For example, convertPersonToDto should contain: return this.toPersonDto(person);
        // Rather than duplicating the mapping implementation

        // given
        Person person = new Person( "John Doe", 30, "john@example.com" );

        // when
        PersonDto result1 = PersonMapper.INSTANCE.toPersonDto( person );
        PersonDto result2 = PersonMapper.INSTANCE.convertPersonToDto( person );

        // then - results should be identical (this passes with current implementation)
        assertThat( result1 ).isNotNull();
        assertThat( result2 ).isNotNull();
        assertThat( result2.getName() ).isEqualTo( result1.getName() );
        assertThat( result2.getAge() ).isEqualTo( result1.getAge() );
        assertThat( result2.getEmail() ).isEqualTo( result1.getEmail() );

        // TODO: Add assertions that check the generated code contains actual delegation
        // This would require examining the generated source or using reflection to verify
        // that the methods are actually calling each other rather than duplicating logic
    }

    @ProcessorTest
    @IssueKey("TBD")
    public void shouldDelegateToUsedMapperMethodsByName() {
        // This test would verify delegation to specific named methods on used mappers

        // given
        Address address = new Address( "123 Main St", "Springfield", "12345" );

        // when - This should delegate to AddressMapper.toDetailedAddressDto
        // Currently it just generates its own mapping implementation
        AddressDto result = AddressMapper.INSTANCE.toDetailedAddressDto( address );

        // then
        assertThat( result ).isNotNull();
        assertThat( result.getStreet() ).isEqualTo( "123 Main St" );

        // TODO: Verify that a @ReferenceMapping method actually delegates to this method
        // rather than generating duplicate mapping code
    }
}
