package com.wuxp.codegen.swagger.annotations;

import com.wuxp.codegen.annotation.processor.AbstractAnnotationProcessor;
import com.wuxp.codegen.annotation.processor.AnnotationMate;
import io.swagger.annotations.ApiOperation;

/**
 * swagger 注解处理
 *
 * @see ApiOperation
 */
public class ApiOperationProcessor extends AbstractAnnotationProcessor<ApiOperation, ApiOperationProcessor.ApiOperationMeta> {


    @Override
    public ApiOperationMeta process(ApiOperation annotation) {
        return this.newProxyMate(annotation, ApiOperationMeta.class);
    }

    public static abstract class ApiOperationMeta implements AnnotationMate<ApiOperation>, ApiOperation {


        @Override
        public String toComment() {
            return this.value();
        }
    }
}
