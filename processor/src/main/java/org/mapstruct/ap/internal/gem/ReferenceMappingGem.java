/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.gem;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.mapstruct.tools.gem.Gem;
import org.mapstruct.tools.gem.GemValue;

/**
 * Gem for the {@link org.mapstruct.ReferenceMapping} annotation.
 *
 * @author MapStruct Authors
 */
public class ReferenceMappingGem implements Gem {

    private final GemValue<List<TypeMirror>> qualifiedBy;
    private final GemValue<List<String>> qualifiedByName;
    private final boolean isValid;
    private final AnnotationMirror mirror;

    private ReferenceMappingGem(GemValue<List<TypeMirror>> qualifiedBy, GemValue<List<String>> qualifiedByName,
                                boolean isValid, AnnotationMirror mirror) {
        this.qualifiedBy = qualifiedBy;
        this.qualifiedByName = qualifiedByName;
        this.isValid = isValid;
        this.mirror = mirror;
    }

    /**
     * Returns the ReferenceMappingGem for the given Element
     *
     * @param element the element we want to get the annotation from
     * @return the ReferenceMappingGem for the given element
     */
    public static ReferenceMappingGem instanceOn(Element element) {
        AnnotationMirror mirror = getAnnotationMirror( element );
        if ( mirror == null ) {
            return null;
        }

        GemValue<List<TypeMirror>> qualifiedBy = GemValue.createArray(
            getAnnotationValue( mirror, "qualifiedBy" ),
            getAnnotationValue( mirror, "qualifiedBy" ),
            TypeMirror.class
        );
        GemValue<List<String>> qualifiedByName = GemValue.createArray(
            getAnnotationValue( mirror, "qualifiedByName" ),
            getAnnotationValue( mirror, "qualifiedByName" ),
            String.class
        );

        return new ReferenceMappingGem( qualifiedBy, qualifiedByName, true, mirror );
    }

    /**
     * Gets the annotation mirror for the ReferenceMapping annotation from the element.
     *
     * @param element the element on which to search for the annotation mirror
     * @return the annotation mirror for the ReferenceMapping annotation from the element if it exists, {@code null} otherwise.
     */
    private static AnnotationMirror getAnnotationMirror(Element element) {
        if ( element == null ) {
            return null;
        }

        for ( AnnotationMirror annotationMirror : element.getAnnotationMirrors() ) {
            TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            if ( annotationElement.getQualifiedName().contentEquals( "org.mapstruct.ReferenceMapping" ) ) {
                return annotationMirror;
            }
        }

        return null;
    }

    /**
     * Gets the annotation value for the given annotation mirror and element name.
     *
     * @param mirror      the annotation mirror
     * @param elementName the name of the element
     * @return the annotation value or null if not found
     */
    private static javax.lang.model.element.AnnotationValue getAnnotationValue(AnnotationMirror mirror,
                                                                               String elementName) {
        if ( mirror == null ) {
            return null;
        }

        for ( java.util.Map.Entry<? extends javax.lang.model.element.ExecutableElement, ? extends javax.lang.model.element.AnnotationValue> entry : mirror.getElementValues()
            .entrySet() ) {
            if ( entry.getKey().getSimpleName().contentEquals( elementName ) ) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * @return the {@link GemValue} for {@link org.mapstruct.ReferenceMapping#qualifiedBy}
     */
    public GemValue<List<TypeMirror>> qualifiedBy() {
        return qualifiedBy;
    }

    /**
     * @return the {@link GemValue} for {@link org.mapstruct.ReferenceMapping#qualifiedByName}
     */
    public GemValue<List<String>> qualifiedByName() {
        return qualifiedByName;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public AnnotationMirror mirror() {
        return mirror;
    }
}
