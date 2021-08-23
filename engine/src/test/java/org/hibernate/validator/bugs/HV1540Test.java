/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.bugs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.ConvertGroup;
import org.hibernate.validator.testutil.TestForIssue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class HV1540Test {

	private static Validator validator;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	@TestForIssue(jiraKey = "HV-1540")
	public void testYourBug() {
		X yourEntity1 = new X();

		Object[] expectedPaths = {"a2c.a", "a2c.b", "b2c.c", "both2c.c"};
		Object[] actualPaths = validator.validate( yourEntity1, B.class )
				.stream()
				.map( ConstraintViolation::getPropertyPath )
				.map( Path::toString )
				.sorted()
				.toArray();

		System.out.println( "Expected: " + Arrays.toString( expectedPaths ) );
		System.out.println( "Actual: " + Arrays.toString( actualPaths ) );

		assertEquals( actualPaths, expectedPaths );
	}

	interface A {
	}

	interface B extends A {
	}

	interface C {
	}

	static class X {
		@Valid
		@ConvertGroup(from = A.class, to = C.class)
		Y a2c = new Y();
		@Valid
		@ConvertGroup(from = B.class, to = C.class)
		Y b2c = new Y();
		@Valid
		@ConvertGroup.List({
				@ConvertGroup(from = A.class, to = C.class),
				@ConvertGroup(from = B.class, to = C.class),
		})
		Y both2c = new Y();
	}

	static class Y {
		@NotNull(message = "A", groups = A.class)
		String a;
		@NotNull(message = "B", groups = B.class)
		String b;
		@NotNull(message = "C", groups = C.class)
		String c;
	}
}
