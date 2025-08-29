/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.referencemapping;

import org.mapstruct.ap.test.referencemapping.mappers.PersonMapper;
import org.mapstruct.ap.test.referencemapping.structs.Person;
import org.mapstruct.ap.test.referencemapping.structs.PersonDto;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for ReferenceMapping annotation functionality.
 *
 * @author MapStruct Authors
 */
@WithClasses({
    Person.class,
    PersonDto.class,
    PersonMapper.class
})
public class ReferenceMappingTest {

    @ProcessorTest
    @IssueKey("TBD")
    public void shouldDelegateToExistingMappingMethod() {
        // given
        Person person = new Person( "John Doe", 30, "john@example.com" );

        // when
        PersonDto result1 = PersonMapper.INSTANCE.toPersonDto( person );
        PersonDto result2 = PersonMapper.INSTANCE.convertPersonToDto( person );

        // then
        assertThat( result1 ).isNotNull();
        assertThat( result2 ).isNotNull();
        assertThat( result1.getName() ).isEqualTo( "John Doe" );
        assertThat( result1.getAge() ).isEqualTo( 30 );
        assertThat( result1.getEmail() ).isEqualTo( "john@example.com" );

        // Both methods should produce identical results
        assertThat( result2.getName() ).isEqualTo( result1.getName() );
        assertThat( result2.getAge() ).isEqualTo( result1.getAge() );
        assertThat( result2.getEmail() ).isEqualTo( result1.getEmail() );
    }

    @ProcessorTest
    @IssueKey("TBD")
    public void shouldDelegateToExistingMappingMethodWithQualifier() {
        // given
        Person person = new Person( "Jane Smith", 25, "jane@example.com" );

        // when
        PersonDto result1 = PersonMapper.INSTANCE.toPersonDto( person );
        PersonDto result2 = PersonMapper.INSTANCE.mapPersonToDto( person );

        // then
        assertThat( result1 ).isNotNull();
        assertThat( result2 ).isNotNull();

        // Both methods should produce identical results
        assertThat( result2.getName() ).isEqualTo( result1.getName() );
        assertThat( result2.getAge() ).isEqualTo( result1.getAge() );
        assertThat( result2.getEmail() ).isEqualTo( result1.getEmail() );
    }

    @ProcessorTest
    @IssueKey("TBD")
    public void shouldDelegateUpdateMethod() {
        // given
        Person person = new Person( "Bob Johnson", 35, "bob@example.com" );
        PersonDto target1 = new PersonDto();
        PersonDto target2 = new PersonDto();

        // when
        PersonMapper.INSTANCE.updatePersonDto( person, target1 );
        PersonMapper.INSTANCE.updatePersonDtoReference( person, target2 );

        // then
        assertThat( target1.getName() ).isEqualTo( "Bob Johnson" );
        assertThat( target1.getAge() ).isEqualTo( 35 );
        assertThat( target1.getEmail() ).isEqualTo( "bob@example.com" );

        // Both methods should produce identical results
        assertThat( target2.getName() ).isEqualTo( target1.getName() );
        assertThat( target2.getAge() ).isEqualTo( target1.getAge() );
        assertThat( target2.getEmail() ).isEqualTo( target1.getEmail() );
    }
}
