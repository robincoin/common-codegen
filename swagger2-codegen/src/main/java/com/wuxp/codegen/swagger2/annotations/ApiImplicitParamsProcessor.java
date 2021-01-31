package com.wuxp.codegen.swagger2.annotations;

import com.wuxp.codegen.annotation.processors.AbstractAnnotationProcessor;
import com.wuxp.codegen.annotation.processors.AnnotationMate;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * swagger2 注解处理
 *
 * @author wuxp
 * @see ApiImplicitParams
 */
public class ApiImplicitParamsProcessor extends AbstractAnnotationProcessor<ApiImplicitParams, ApiImplicitParamsProcessor.ApiImplicitParamsMate> {


    @Override
    public ApiImplicitParamsMate process(ApiImplicitParams annotation) {
        return this.newProxyMate(annotation, ApiImplicitParamsMate.class);
    }

    public abstract static class ApiImplicitParamsMate implements AnnotationMate, ApiImplicitParams {

        private final ApiImplicitParams apiImplicitParams;

        private final ApiImplicitParamProcessor apiImplicitParamProcessor = new ApiImplicitParamProcessor();

         ApiImplicitParamsMate(ApiImplicitParams apiImplicitParams) {
            this.apiImplicitParams = apiImplicitParams;
        }

        @Override
        public ApiImplicitParam[] value() {
            return getApiImplicitParams();
        }


        @Override
        public String toComment(Method annotationOwner) {
            ApiImplicitParam[] value = getApiImplicitParams();

            List<String> comments = new ArrayList<>();
            comments.add("<pre>");
            comments.add("参数列表：");
            comments.addAll(Arrays.stream(value)
                    .map(item -> {
                        ApiImplicitParamProcessor.ApiImplicitParamMate mate = (ApiImplicitParamProcessor.ApiImplicitParamMate) item;
                        return String.format("参数名称：%s，参数说明：%s", mate.name(), mate.toComment(annotationOwner));
                    }).collect(Collectors.toList()));
            comments.add("</pre>");
            return String.join(MULTILINE_COMMENT_TAG, comments);
        }

        private ApiImplicitParam[] getApiImplicitParams() {
            ApiImplicitParam[] value = apiImplicitParams.value();
            return Arrays.stream(value)
                    .map(apiImplicitParamProcessor::process)
                    .toArray(ApiImplicitParam[]::new);
        }
    }
}
