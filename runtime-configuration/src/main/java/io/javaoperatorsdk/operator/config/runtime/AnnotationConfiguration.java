
package io.javaoperatorsdk.operator.config.runtime;

import java.util.Set;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.CustomResourceDoneable;
import io.javaoperatorsdk.operator.api.Controller;
import io.javaoperatorsdk.operator.api.ControllerUtils;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.config.ControllerConfiguration;

public class AnnotationConfiguration<R extends CustomResource> implements ControllerConfiguration<R> {
    private final ResourceController<R> controller;
    private final Controller annotation;
    
    public AnnotationConfiguration(ResourceController<R> controller) {
        this.controller = controller;
        this.annotation = controller.getClass().getAnnotation(Controller.class);
    }
    
    @Override
    public String getName() {
        final var name = annotation.name();
        return Controller.NULL.equals(name) ? ControllerUtils.getDefaultNameFor(controller) : name;
    }
    
    @Override
    public String getCRDName() {
        return annotation.crdName();
    }
    
    @Override
    public String getFinalizer() {
        final String annotationFinalizerName = annotation.finalizerName();
        if (!Controller.NULL.equals(annotationFinalizerName)) {
            return annotationFinalizerName;
        }
        return ControllerUtils.getDefaultFinalizerName(annotation.crdName());
    }
    
    @Override
    public boolean isGenerationAware() {
        return annotation.generationAwareEventProcessing();
    }
    
    @Override
    public Class<R> getCustomResourceClass() {
        return ControllerToCustomResourceMappingsProvider.getCustomResourceClass(controller);
    }
    
    @Override
    public Class<? extends CustomResourceDoneable<R>> getDoneableClass() {
        return ControllerToCustomResourceMappingsProvider.getDoneableClassFor(getCustomResourceClass());
    }
    
    @Override
    public boolean isClusterScoped() {
        return annotation.isClusterScoped();
    }
    
    @Override
    public Set<String> getNamespaces() {
        return Set.of(annotation.namespaces());
    }
}