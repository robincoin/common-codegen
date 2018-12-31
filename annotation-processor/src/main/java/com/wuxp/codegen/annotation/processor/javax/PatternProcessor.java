package com.wuxp.codegen.annotation.processor.javax;

import com.wuxp.codegen.annotation.processor.AbstractAnnotationProcessor;
import com.wuxp.codegen.annotation.processor.AnnotationMate;

import javax.validation.constraints.Pattern;

/**
 * javax 验证注解处理
 *
 * @see Pattern
 */
public class PatternProcessor extends AbstractAnnotationProcessor<Pattern, PatternProcessor.PatternMate> {


    @Override
    public PatternMate process(Pattern annotation) {

        return super.newProxyMate(annotation, PatternMate.class);
    }


    public static abstract class PatternMate implements AnnotationMate<Pattern>, Pattern {

        @Override
        public String toComment() {

            return "";
        }
    }
}
