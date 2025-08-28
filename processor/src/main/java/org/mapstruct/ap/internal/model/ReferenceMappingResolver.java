/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.ExecutableElement;

import org.mapstruct.ap.internal.gem.ReferenceMappingGem;
import org.mapstruct.ap.internal.model.common.ParameterBinding;
import org.mapstruct.ap.internal.model.common.Parameter;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.source.SourceMethod;

/**
 * Service for resolving method references for @ReferenceMapping annotations.
 * This service finds compatible methods that can be delegated to.
 *
 * @author MapStruct Authors
 */
public class ReferenceMappingResolver {

    private final MappingBuilderContext mappingContext;

    public ReferenceMappingResolver(MappingBuilderContext mappingContext) {
        this.mappingContext = mappingContext;
    }

    /**
     * Resolves a compatible method for delegation based on the reference mapping annotation.
     *
     * @param sourceMethod        the method annotated with @ReferenceMapping
     * @param referenceMappingGem the @ReferenceMapping annotation
     * @return the resolved method reference, or null if no compatible method found
     */
    public MethodReference resolveMethodReference(SourceMethod sourceMethod,
                                                  ReferenceMappingGem referenceMappingGem) {
        List<String> qualifiedNames = referenceMappingGem.qualifiedByName() != null ?
            referenceMappingGem.qualifiedByName().get() : null;
        String qualifiedName = qualifiedNames != null && !qualifiedNames.isEmpty() ?
            qualifiedNames.get( 0 ) : null;
        boolean useQualifiedName = qualifiedName != null;

        // Try to find a compatible method
        return findCompatibleMethod( sourceMethod, qualifiedName, useQualifiedName );
    }

    private MethodReference findMethodByQualifiedName(SourceMethod sourceMethod, String qualifiedName) {
        // First search in used mappers (cross-mapper delegation - primary use case)
        MethodReference crossMapperMethod = findInUsedMappers( sourceMethod, qualifiedName, true );
        if ( crossMapperMethod != null ) {
            return crossMapperMethod;
        }

        // Search in the same mapper as fallback
        for ( SourceMethod candidate : mappingContext.getSourceModel() ) {
            if ( candidate != sourceMethod &&
                hasNamedAnnotation( candidate, qualifiedName ) &&
                isCompatibleSignature( sourceMethod, candidate ) ) {
                return createSelfReference( candidate );
            }
        }

        return null;
    }

    private MethodReference findCompatibleMethod(SourceMethod sourceMethod, String qualifiedName,
                                                 boolean useQualifiedName) {
        // First, try to find a method in used mappers (cross-mapper delegation)
        MethodReference crossMapperMethod = findInUsedMappers( sourceMethod, qualifiedName, useQualifiedName );
        if ( crossMapperMethod != null ) {
            return crossMapperMethod;
        }

        // If no cross-mapper method found, search in the same mapper as fallback
        for ( SourceMethod candidate : mappingContext.getSourceModel() ) {
            if ( candidate != sourceMethod &&
                !candidate.isReferenceMapping() && // Don't delegate to another reference mapping
                isCompatibleSignature( sourceMethod, candidate ) ) {
                return createSelfReference( candidate );
            }
        }

        return null;
    }

    private MethodReference findInUsedMappers(SourceMethod sourceMethod, String qualifiedName,
                                              boolean useQualifiedName) {
        // Check if we have used mappers
        List<MapperReference> mapperReferences = mappingContext.getMapperReferences();

        if ( mapperReferences.isEmpty() ) {
            // No used mappers - this is expected for intra-mapper scenarios
            return null;
        }

        // Iterate through all used mappers (from @Mapper(uses = {...}))
        for ( MapperReference mapperRef : mapperReferences ) {
            Type mapperType = mapperRef.getType();

            // Get TypeElement from the mapper Type
            if ( mapperType.getTypeElement() == null ) {
                continue;
            }

            // Get all methods from the referenced mapper using ElementUtils
            List<ExecutableElement> methods = mappingContext.getElementUtils()
                .getAllEnclosedExecutableElements( mapperType.getTypeElement() );


            for ( ExecutableElement executableElement : methods ) {
                // Skip private methods and constructors
                if ( executableElement.getModifiers().contains( javax.lang.model.element.Modifier.PRIVATE ) ||
                    executableElement.getKind() != javax.lang.model.element.ElementKind.METHOD ) {
                    continue;
                }

                // Check each method for compatibility
                String methodName = executableElement.getSimpleName().toString();

                // Create a SourceMethod for comparison
                SourceMethod candidateMethod = createSourceMethodFromElement( mapperType, executableElement );
                if ( candidateMethod != null ) {
                    // Check if this method has compatible signature
                    if ( hasCompatibleSignature( sourceMethod, candidateMethod, qualifiedName, useQualifiedName ) ) {
                        // Create and return a cross-mapper reference
                        return createCrossMapperReference( candidateMethod, mapperRef );
                    }
                }
            }
        }

        return null;
    }

    private SourceMethod createSourceMethodFromElement(Type declaringType, ExecutableElement executableElement) {
        try {
            // Get the DeclaredType for the declaring type
            javax.lang.model.type.DeclaredType declaredType =
                (javax.lang.model.type.DeclaredType) declaringType.getTypeMirror();

            // Create type information
            javax.lang.model.type.ExecutableType methodType =
                (javax.lang.model.type.ExecutableType) mappingContext.getTypeUtils()
                    .asMemberOf( declaredType, executableElement );

            // Create parameters
            List<Parameter> parameters = mappingContext.getTypeFactory()
                .getParameters( methodType, executableElement );

            // Create return type
            Type returnType = mappingContext.getTypeFactory().getReturnType( methodType );

            // Create exception types
            List<Type> exceptionTypes = mappingContext.getTypeFactory().getThrownTypes( methodType );

            // Build the SourceMethod with all required fields
            SourceMethod result = new SourceMethod.Builder()
                .setDeclaringMapper( declaringType )
                .setExecutable( executableElement )
                .setParameters( parameters )
                .setReturnType( returnType )
                .setExceptionTypes( exceptionTypes )
                .setTypeUtils( mappingContext.getTypeUtils() )
                .setTypeFactory( mappingContext.getTypeFactory() )
                .setPrototypeMethods( Collections.emptyList() ) // No prototype methods for cross-mapper references
                .setVerboseLogging( false )
                .build();

            return result;

        }
        catch ( Exception e ) {
            e.printStackTrace();
            // If we can't create the SourceMethod, skip this candidate
            return null;
        }
    }

    private boolean isValidCandidateMethod(SourceMethod sourceMethod, SourceMethod candidate,
                                           String qualifiedName, boolean useQualifiedName) {
        // Don't delegate to reference mapping methods
        if ( candidate.isReferenceMapping() ) {
            return false;
        }

        // Check signature compatibility
        if ( !isCompatibleSignature( sourceMethod, candidate ) ) {
            return false;
        }

        // If using qualified name, check for match
        if ( useQualifiedName ) {
            return hasNamedAnnotation( candidate, qualifiedName );
        }

        return true;
    }

    private MethodReference createCrossMapperReference(SourceMethod method, MapperReference declaringMapper) {
        // Create a method reference that points to a method in a used mapper

        List<ParameterBinding> parameterBindings = ParameterBinding.fromParameters( method.getParameters() );
        MethodReference result = MethodReference.forMapperReference( method, declaringMapper, parameterBindings );

        return result;
    }

    private boolean isCompatibleSignature(SourceMethod source, SourceMethod target) {
        // Check parameter count
        if ( source.getParameters().size() != target.getParameters().size() ) {
            return false;
        }

        // Check return type compatibility
        if ( !source.getReturnType().equals( target.getReturnType() ) ) {
            return false;
        }

        // Check parameter type compatibility
        for ( int i = 0; i < source.getParameters().size(); i++ ) {
            Parameter sourceParam = source.getParameters().get( i );
            Parameter targetParam = target.getParameters().get( i );

            if ( !sourceParam.getType().equals( targetParam.getType() ) ) {
                return false;
            }
        }

        return true;
    }

    private boolean hasNamedAnnotation(SourceMethod method, String name) {
        // For now, just check if the method name matches the qualified name
        // A more sophisticated implementation would check @Named annotations
        return method.getName().equals( name );
    }

    private boolean hasCompatibleSignature(SourceMethod sourceMethod, SourceMethod candidateMethod,
                                           String qualifiedName, boolean useQualifiedName) {
        // If using qualified name, check for exact match
        if ( useQualifiedName ) {
            return candidateMethod.getName().equals( qualifiedName );
        }

        // Check parameter count
        if ( sourceMethod.getParameters().size() != candidateMethod.getParameters().size() ) {
            return false;
        }

        // Check return type compatibility
        if ( !sourceMethod.getReturnType().equals( candidateMethod.getReturnType() ) ) {
            return false;
        }

        // Check parameter type compatibility
        for ( int i = 0; i < sourceMethod.getParameters().size(); i++ ) {
            Parameter sourceParam = sourceMethod.getParameters().get( i );
            Parameter candidateParam = candidateMethod.getParameters().get( i );

            if ( !sourceParam.getType().equals( candidateParam.getType() ) ) {
                return false;
            }
        }

        return true;
    }

    private MethodReference createSelfReference(SourceMethod method) {
        // Create a method reference that points to a method in the same mapper
        // We use null for declaringMapper to indicate it's in the same mapper
        List<ParameterBinding> parameterBindings = ParameterBinding.fromParameters( method.getParameters() );
        return MethodReference.forMapperReference( method, null, parameterBindings );
    }
}
