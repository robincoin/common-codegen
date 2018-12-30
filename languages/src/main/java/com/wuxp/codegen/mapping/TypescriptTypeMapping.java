package com.wuxp.codegen.mapping;

import com.wuxp.codegen.core.parser.GenericParser;
import com.wuxp.codegen.core.parser.JavaClassParser;
import com.wuxp.codegen.languages.AbstractTypescriptParser;
import com.wuxp.codegen.model.CommonCodeGenClassMeta;
import com.wuxp.codegen.model.languages.java.JavaClassMeta;
import com.wuxp.codegen.model.languages.typescript.TypescriptClassMeta;
import com.wuxp.codegen.model.mapping.AbstractTypeMapping;
import com.wuxp.codegen.model.mapping.BaseTypeMapping;
import com.wuxp.codegen.model.mapping.TypeMapping;
import com.wuxp.codegen.model.utils.JavaTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 处理typescript的类型映射
 */
@Slf4j
public class TypescriptTypeMapping extends AbstractTypeMapping<TypescriptClassMeta> {


    {

        //设置基础的数据类型映射
        BASE_TYPE_MAPPING.put(Date.class, TypescriptClassMeta.DATE);
        BASE_TYPE_MAPPING.put(Boolean.class, TypescriptClassMeta.BOOLEAN);
        BASE_TYPE_MAPPING.put(String.class, TypescriptClassMeta.STRING);
        BASE_TYPE_MAPPING.put(Number.class, TypescriptClassMeta.NUMBER);
        BASE_TYPE_MAPPING.put(Map.class, TypescriptClassMeta.MAP);
        BASE_TYPE_MAPPING.put(Set.class, TypescriptClassMeta.SET);
        BASE_TYPE_MAPPING.put(List.class, TypescriptClassMeta.ARRAY);
        BASE_TYPE_MAPPING.put(Collection.class, TypescriptClassMeta.ARRAY);
        BASE_TYPE_MAPPING.put(void.class, TypescriptClassMeta.VOID);

    }

    /**
     * 基础类型映射器
     */
    protected TypeMapping<Class<?>, TypescriptClassMeta> baseTypeMapping = new BaseTypeMapping<TypescriptClassMeta>(BASE_TYPE_MAPPING);


    /**
     * java类的解析器
     * 默认解析所有的属性 方法
     */
    protected GenericParser<JavaClassMeta, Class<?>> javaParser = new JavaClassParser(false);

    protected AbstractTypescriptParser typescriptParser;


    public TypescriptTypeMapping(AbstractTypescriptParser typescriptParser) {
        this.typescriptParser = typescriptParser;
    }

    /**
     * @param classes 类型列表，大于一个表示有泛型
     * @return 类型描述字符串代码
     */
    @Override
    public List<TypescriptClassMeta> mapping(Class<?>... classes) {

        if (classes == null || classes.length == 0) {
            return null;
        }

        Class<?> clazz = classes[0];

        //1. 类型转换，如果是简单的java类型，则尝试做装换
        //2. 处理枚举类型
        //3. 循环获取泛型
        //4. 处理复杂的数据类型（自定义的java类）

        List<TypescriptClassMeta> list = new ArrayList<>(4);

        if (JavaTypeUtil.isComplex(clazz) || clazz.isEnum()) {
            //复杂的数据类型或枚举
            CommonCodeGenClassMeta meta = this.typescriptParser.parse(this.javaParser.parse(clazz));
            if (meta == null) {
                return list;
            }
            TypescriptClassMeta typescriptClassMeta = new TypescriptClassMeta();
            BeanUtils.copyProperties(meta, typescriptClassMeta);
            list.add(typescriptClassMeta);
        } else {
            Class<?> upConversionType = this.tryUpConversionType(clazz);
            if (upConversionType != null) {
                list.add(baseTypeMapping.mapping(upConversionType));
            } else if (clazz.isArray()) {
                //数组
                list.add(TypescriptClassMeta.ARRAY);
                list.addAll(this.mapping(clazz.getComponentType()));
                return list;
            } else {
                throw new RuntimeException("Not Found clazz" + clazz.getName() + " mapping type");
            }
        }

        //处理泛型
        list.addAll(Arrays.stream(classes)
                .filter(c -> c != clazz).map(this::mapping)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));

        return list;

    }

    /**
     * 尝试向上转换类型
     *
     * @param clazz
     * @return
     */
    protected Class<?> tryUpConversionType(Class<?> clazz) {
        if (JavaTypeUtil.isNumber(clazz)) {
            //数值类型
            return Number.class;
        } else if (JavaTypeUtil.isString(clazz)) {
            return String.class;
        } else if (JavaTypeUtil.isBoolean(clazz)) {
            return Boolean.class;
        } else if (JavaTypeUtil.isDate(clazz)) {
            return Date.class;
        } else if (JavaTypeUtil.isVoid(clazz)) {
            return void.class;
        } else if (JavaTypeUtil.isSet(clazz)) {
            return Set.class;
        } else if (JavaTypeUtil.isList(clazz)) {
            return List.class;
        } else if (JavaTypeUtil.isCollection(clazz)) {
            return Collection.class;
        } else if (JavaTypeUtil.isMap(clazz)) {
            return Map.class;
        }
        return null;
    }
}