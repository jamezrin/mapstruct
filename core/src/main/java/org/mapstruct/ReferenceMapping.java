/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Delegates the annotated mapping method to an existing method without duplicating the mapping logic.
 * <p>
 * This annotation is designed to avoid code duplication by generating delegation calls to existing mapping methods,
 * either in the same mapper or in used/dependency mappers. Instead of generating duplicate mapping implementations,
 * it produces simple method calls like {@code return existingMethod(parameter);}.
 * </p>
 *
 * <p><b>Primary Use Cases:</b></p>
 * <ul>
 * <li><strong>Cross-mapper delegation:</strong> Delegate to methods in used mappers specified via
 * {@code @Mapper(uses = ...)}</li>
 * <li><strong>Method aliasing:</strong> Create alternative method names that delegate to existing
 * implementations</li>
 * <li><strong>API consistency:</strong> Provide consistent method signatures across different mappers</li>
 * </ul>
 *
 * <p><b>Restrictions and Requirements:</b></p>
 * <ul>
 * <li><strong>Abstract methods only:</strong> Can only be used on abstract methods in classes or non-default methods
 * in interfaces</li>
 * <li><strong>No other mapping annotations:</strong> Cannot be combined with {@code @Mapping},
 * {@code @BeanMapping}, or other mapping annotations</li>
 * <li><strong>Compatible signatures:</strong> The target method must have compatible parameter and return
 * types</li>
 * </ul>
 *
 * <p><b>Usage Examples:</b></p>
 * <pre><code>
 * &#064;Mapper(uses = UserMapper.class)
 * public interface OrderMapper {
 *
 *     // Delegate to a method in the used UserMapper
 *     &#064;ReferenceMapping(qualifiedByName = "toUserDto")
 *     UserDto convertUser(User user);
 *
 *     // Delegate to a compatible method by signature matching
 *     &#064;ReferenceMapping
 *     OrderDto mapOrder(Order order);
 * }
 * </code></pre>
 *
 * <p><strong>Note:</strong> While intra-mapper delegation (within the same mapper) is supported,
 * the primary intended use case is cross-mapper delegation. Consider calling methods directly
 * instead of using delegation within the same mapper.</p>
 *
 * @author MapStruct Authors
 * @since 1.7
 */
@Retention(CLASS)
@Target({ METHOD })
public @interface ReferenceMapping {
    /**
     * A qualifier can be specified to aid the selection process of a suitable delegate method. This is useful when
     * multiple methods in the target mapper have compatible signatures and you want to specify which one should be
     * used for delegation.
     * <p>
     * A qualifier is a custom annotation and can be placed on the target method that should be used as the delegate.
     *
     * @return the qualifiers
     * @see Qualifier
     */
    Class<? extends Annotation>[] qualifiedBy() default {};

    /**
     * Similar to {@link #qualifiedBy()}, but used in combination with {@code @}{@link Named} to specify the target
     * delegate method by name instead of using a custom qualifier annotation.
     *
     * @return the qualifier names
     * @see Named
     */
    String[] qualifiedByName() default {};
}
