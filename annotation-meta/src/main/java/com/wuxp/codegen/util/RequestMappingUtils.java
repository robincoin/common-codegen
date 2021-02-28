package com.wuxp.codegen.util;

import com.wuxp.codegen.annotation.processors.spring.RequestMappingProcessor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wuxp
 */
public final class RequestMappingUtils {

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final RequestMappingProcessor REQUEST_MAPPING_PROCESSOR = new RequestMappingProcessor();

    private RequestMappingUtils() {
    }

    public static String combinePath(RequestMappingProcessor.RequestMappingMate annotationMate, Method annotationOwner) {
        Class<?> declaringClass = annotationOwner.getDeclaringClass();
        Optional<RequestMappingProcessor.RequestMappingMate> requestAnnotation = findRequestMappingAnnotation(declaringClass.getAnnotations());
        String[] clazzMappingValues = requestAnnotation.map(RequestMappingProcessor.RequestMappingMate::getPath).orElse(null);
        String[] v2 = annotationMate.getPath();
        return combinePath(clazzMappingValues, v2);
    }

    /**
     * 合并url
     *
     * @param patterns      类上的{@link org.springframework.web.bind.annotation.RequestMapping}注解的 value
     * @param otherPatterns 方法上的{@link org.springframework.web.bind.annotation.RequestMapping}注解的 value
     * @return 合并后的请url，可能会返回null
     */
    public static String combinePath(String[] patterns, String[] otherPatterns) {
        boolean patternIsEmpty = patterns == null || patterns.length == 0;
        boolean otherIsEmpty = otherPatterns == null || otherPatterns.length == 0;
        if (patternIsEmpty && otherIsEmpty) {
            return null;
        }
        if (patternIsEmpty) {
            return otherPatterns[0];
        }
        if (otherIsEmpty) {
            return patterns[0];
        }
        return PATH_MATCHER.combine(patterns[0], otherPatterns[0]);
    }

    public static Optional<RequestMappingProcessor.RequestMappingMate> findRequestMappingAnnotation(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .map(annotation -> {
                    if (RequestMappingProcessor.isSupportAnnotationType(annotation)) {
                        return REQUEST_MAPPING_PROCESSOR.process(annotation);
                    }
                    return null;
                }).filter(Objects::nonNull)
                .findFirst();
    }


    public static Optional<PathVariable> findPathVariable(Annotation[] annotations) {
        return findAnnotations(annotations, PathVariable.class);
    }

    public static Optional<RequestParam> findRequestParam(Object annotationOwner) {
        if (annotationOwner instanceof Parameter) {
            return findRequestParam(((Parameter) annotationOwner).getAnnotations());
        }
        return Optional.empty();

    }

    public static Optional<RequestParam> findRequestParam(Annotation[] annotations) {
        return findAnnotations(annotations, RequestParam.class);
    }

    public static Optional<RequestHeader> findRequestHeader(Annotation[] annotations) {
        return findAnnotations(annotations, RequestHeader.class);
    }

    private static <T> Optional<T> findAnnotations(Annotation[] annotations, Class<T> annotationType) {
        return Arrays.stream(annotations)
                .filter(annotation -> annotationType.equals(annotation.annotationType()))
                .map(annotation -> (T) annotation)
                .findFirst();
    }
}
