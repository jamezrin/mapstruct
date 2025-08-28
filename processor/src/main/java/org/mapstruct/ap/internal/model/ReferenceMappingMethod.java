/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.ap.internal.gem.ReferenceMappingGem;
import org.mapstruct.ap.internal.model.common.Parameter;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.source.Method;
import org.mapstruct.ap.internal.model.source.SourceMethod;

/**
 * Represents a method that delegates to another method using @ReferenceMapping annotation.
 *
 * @author MapStruct Authors
 */
public class ReferenceMappingMethod extends MappingMethod {

    private final MethodReference delegateCall;
    private final List<Annotation> annotations;
    private final Method sourceMethod;

    public ReferenceMappingMethod(Method method,
                                  List<Annotation> annotations,
                                  Collection<String> existingVariableNames,
                                  List<LifecycleCallbackMethodReference> beforeMappingReferences,
                                  List<LifecycleCallbackMethodReference> afterMappingReferences,
                                  MethodReference delegateCall) {
        super( method, existingVariableNames, beforeMappingReferences, afterMappingReferences );
        this.annotations = annotations;
        this.delegateCall = delegateCall;
        this.sourceMethod = method;
    }

    public MethodReference getDelegateCall() {
        return delegateCall;
    }

    public String getDelegateCallString() {
        if ( delegateCall == null ) {
            return "/* No delegate call found */";
        }

        // Build a method call, including mapper reference for cross-mapper delegation
        StringBuilder call = new StringBuilder();

        // For cross-mapper delegation, include the mapper field name
        // For intra-mapper delegation, declaringMapper will be null, so no prefix needed
        if ( delegateCall.getDeclaringMapper() != null ) {
            String mapperVariableName = delegateCall.getDeclaringMapper().getVariableName();
            if ( mapperVariableName != null ) {
                call.append( mapperVariableName );
                call.append( "." );
            }
        }

        call.append( delegateCall.getName() );
        call.append( "( " );

        // For delegation, we need to pass ALL parameters from the current method to the delegate method
        // The methods should have matching signatures (verified during resolution)
        List<Parameter> currentParameters = getParameters();
        for ( int i = 0; i < currentParameters.size(); i++ ) {
            if ( i > 0 ) {
                call.append( ", " );
            }
            String paramName = currentParameters.get( i ).getName();
            call.append( paramName );
        }

        call.append( " )" );
        return call.toString();
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public Set<Type> getImportTypes() {
        Set<Type> types = super.getImportTypes();

        if ( delegateCall != null ) {
            types.addAll( delegateCall.getImportTypes() );
        }

        for ( Annotation annotation : annotations ) {
            types.addAll( annotation.getImportTypes() );
        }

        return types;
    }

    public boolean isDelegated() {
        return true; // @ReferenceMapping always delegates
    }

    public boolean isOverridden() {
        return sourceMethod.overridesMethod();
    }

    public static class Builder {

        private Method method;
        private MappingBuilderContext mappingContext;

        private static boolean isAbstractOrInterfaceMethod(SourceMethod method) {
            // Check if method is abstract in a class or non-default in an interface
            return method.getExecutable().getModifiers().contains( javax.lang.model.element.Modifier.ABSTRACT ) ||
                ( method.getDeclaringMapper() != null &&
                    method.getDeclaringMapper().isInterface() &&
                    !method.getExecutable().isDefault() );
        }

        private static boolean hasConflictingAnnotations(SourceMethod method) {
            // Check for common mapping annotations that conflict with @ReferenceMapping
            javax.lang.model.element.ExecutableElement executable = method.getExecutable();

            // Use annotation mirror approach to avoid ClassNotFoundException
            for ( javax.lang.model.element.AnnotationMirror annotation : executable.getAnnotationMirrors() ) {
                String annotationName = annotation.getAnnotationType().toString();
                if ( annotationName.equals( "org.mapstruct.Mapping" ) ||
                    annotationName.equals( "org.mapstruct.Mappings" ) ||
                    annotationName.equals( "org.mapstruct.BeanMapping" ) ||
                    annotationName.equals( "org.mapstruct.IterableMapping" ) ||
                    annotationName.equals( "org.mapstruct.MapMapping" ) ||
                    annotationName.equals( "org.mapstruct.ValueMapping" ) ||
                    annotationName.equals( "org.mapstruct.ValueMappings" ) ) {
                    return true;
                }
            }
            return false;
        }

        private static boolean isIntraMapperReference(MethodReference methodRef) {
            // Check if the reference is to a method in the same mapper (declaringMapper is null)
            return methodRef.getDeclaringMapper() == null;
        }

        public Builder mappingContext(MappingBuilderContext mappingContext) {
            this.mappingContext = mappingContext;
            return this;
        }

        public Builder sourceMethod(SourceMethod sourceMethod) {
            this.method = sourceMethod;
            return this;
        }

        public ReferenceMappingMethod build() {
            if ( method == null || mappingContext == null ) {
                return null;
            }

            // Check if this is a reference mapping method
            ReferenceMappingGem referenceMappingGem = ReferenceMappingGem.instanceOn( method.getExecutable() );
            if ( referenceMappingGem == null ) {
                return null;
            }

            SourceMethod sourceMethod = (SourceMethod) method;

            // Validation 1: Only allow on abstract methods or interface default methods
            if ( !isAbstractOrInterfaceMethod( sourceMethod ) ) {
                mappingContext.getMessager().printMessage(
                    sourceMethod.getExecutable(),
                    org.mapstruct.ap.internal.util.Message.REFERENCEMAPPING_ABSTRACT_METHOD_REQUIRED
                );
                return null;
            }

            // Validation 2: Check for conflicting annotations
            if ( hasConflictingAnnotations( sourceMethod ) ) {
                mappingContext.getMessager().printMessage(
                    sourceMethod.getExecutable(),
                    org.mapstruct.ap.internal.util.Message.REFERENCEMAPPING_CONFLICTING_ANNOTATIONS
                );
                return null;
            }

            // Resolve the referenced method
            ReferenceMappingResolver resolver = new ReferenceMappingResolver( mappingContext );
            MethodReference resolvedMethod = resolver.resolveMethodReference( sourceMethod, referenceMappingGem );

            // Validation 3: Warn about intra-mapper delegation - temporarily disabled
            // if (resolvedMethod != null && isIntraMapperReference(resolvedMethod)) {
            //     mappingContext.getMessager().printMessage(
            //         sourceMethod.getExecutable(),
            //         org.mapstruct.ap.internal.util.Message.REFERENCEMAPPING_SAME_MAPPER_WARNING
            // If no method was resolved, @ReferenceMapping cannot proceed
            if ( resolvedMethod == null ) {
                mappingContext.getMessager().printMessage(
                    method.getExecutable(),
                    referenceMappingGem.mirror(),
                    org.mapstruct.ap.internal.util.Message.REFERENCEMAPPING_NO_DELEGATE_METHOD_FOUND,
                    method.getName()
                );
                return null; // Fail to create the method, causing compilation to fail
            }

            List<Annotation> annotations = Collections.emptyList(); // TODO: get method annotations

            return new ReferenceMappingMethod(
                method,
                annotations,
                new HashSet<>(),
                Collections.emptyList(),
                Collections.emptyList(),
                resolvedMethod
            );
        }
    }
}
