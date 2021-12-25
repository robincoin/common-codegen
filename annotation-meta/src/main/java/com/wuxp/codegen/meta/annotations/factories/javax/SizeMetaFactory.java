package com.wuxp.codegen.meta.annotations.factories.javax;

import com.wuxp.codegen.meta.annotations.factories.AbstractAnnotationMetaFactory;
import com.wuxp.codegen.meta.annotations.factories.AnnotationMate;
import com.wuxp.codegen.model.CommonCodeGenAnnotation;

import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * javax 验证注解处理
 *
 * @author wuxp
 * @see Size
 */
public class SizeMetaFactory extends AbstractAnnotationMetaFactory<Size, SizeMetaFactory.SizeMate> {


    @Override
    public SizeMate factory(Size annotation) {
        return super.newProxyMate(annotation, SizeMate.class);
    }


    public abstract static class SizeMate implements AnnotationMate, Size {

        @Override
        public CommonCodeGenAnnotation toAnnotation(Field annotationOwner) {
            CommonCodeGenAnnotation annotation = new CommonCodeGenAnnotation();
            annotation.setName(Size.class.getName());
            Map<String, String> namedArguments = new HashMap<>();
            namedArguments.put("message", this.message());
            namedArguments.put("max", MessageFormat.format("{0}", this.max()));
            namedArguments.put("min", MessageFormat.format("{0}", this.min()));
            annotation.setNamedArguments(namedArguments);
            return annotation;
        }

        @Override
        public String toComment(Field annotationOwner) {

            return MessageFormat.format("{0} 约束条件：输入字符串的最小长度为：{1}，输入字符串的最大长度为：{2}", annotationOwner.getName(), this.min(), this.max());
        }
    }
}
