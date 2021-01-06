package com.wuxp.codegen.util;

import static com.wuxp.codegen.model.constant.SpringAnnotationClassConstant.SPRING_CONTROLLER_ANNOTATIONS;
import static com.wuxp.codegen.model.constant.SpringAnnotationClassConstant.SPRING_MAPPING_ANNOTATIONS;

import com.wuxp.codegen.core.parser.JavaClassParser;
import com.wuxp.codegen.model.languages.java.JavaClassMeta;
import com.wuxp.codegen.model.languages.java.JavaMethodMeta;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 过滤spring controller 的方法或属性
 *
 * @author wuxp
 */
@Slf4j
public final class SpringControllerFilterUtils {


  private static final JavaClassParser JAVA_PARSER = new JavaClassParser(true);

  private SpringControllerFilterUtils() {
  }

  /**
   * 过滤方法
   *
   * @param classMeta 类元数据
   */
  public static void filterMethods(JavaClassMeta classMeta) {
    //判断是否为spring的控制器
    Class<?> clazz = classMeta.getClazz();
    boolean isSpringController = Arrays.stream(SPRING_CONTROLLER_ANNOTATIONS)
        .map(aClass -> clazz.getAnnotation(aClass) != null)
        .filter(b -> b).findFirst()
        .orElse(false);
    if (!isSpringController) {
      return;
    }

    //抓取超类的方法
    List<JavaMethodMeta> methodMetas = Arrays.stream(classMeta.getMethodMetas()).collect(Collectors.toList());
    Class<?> superclass = clazz.getSuperclass();
    boolean isNoneSupper = superclass == null || Object.class.equals(superclass);
    if (!isNoneSupper) {
      JavaMethodMeta[] javaMethodMetas = JAVA_PARSER.parse(superclass).getMethodMetas();
      methodMetas.addAll(Arrays.asList(javaMethodMetas));
    }

    classMeta.setMethodMetas(getStringJavaMethodMetaMap(methodMetas)
        .stream()
        .filter(javaMethodMeta -> {
          //过滤掉非mapping的方法
          return findSpringControllerMappingAnnotation(javaMethodMeta) != null;
        }).toArray(JavaMethodMeta[]::new));
  }

  /**
   * 过滤掉控制器中请求uri和请求方法相同的方法
   *
   * @param methodMetas
   * @return
   */
  private static List<JavaMethodMeta> getStringJavaMethodMetaMap(List<JavaMethodMeta> methodMetas) {
    Map<String/*请求方法加上方法名称或RequestMapping的value*/, JavaMethodMeta> javaMethodMetaMap = new LinkedHashMap<>();

    //过滤路径相同的方法
    methodMetas.stream()
        .filter(Objects::nonNull)
        .forEach(javaMethodMeta -> {
          Annotation annotation = findSpringControllerMappingAnnotation(javaMethodMeta);
          if (annotation == null) {
            return;
          }
          String[] value = null;
          RequestMethod[] methods = null;
          if (annotation.annotationType().equals(RequestMapping.class)) {

            value = ((RequestMapping) annotation).value();
            methods = ((RequestMapping) annotation).method();
            if (methods.length == 0) {
              methods = new RequestMethod[]{RequestMethod.GET, RequestMethod.POST};
            }
          }
          if (annotation.annotationType().equals(PostMapping.class)) {

            value = ((PostMapping) annotation).value();
            methods = new RequestMethod[]{RequestMethod.POST};
          }
          if (annotation.annotationType().equals(GetMapping.class)) {

            value = ((GetMapping) annotation).value();
            methods = new RequestMethod[]{RequestMethod.GET};
          }
          if (annotation.annotationType().equals(DeleteMapping.class)) {

            value = ((DeleteMapping) annotation).value();
            methods = new RequestMethod[]{RequestMethod.DELETE};
          }

          if (annotation.annotationType().equals(PutMapping.class)) {

            value = ((PutMapping) annotation).value();
            methods = new RequestMethod[]{RequestMethod.PUT};
          }

          if (annotation.annotationType().equals(PatchMapping.class)) {

            value = ((PatchMapping) annotation).value();
            methods = new RequestMethod[]{RequestMethod.PATCH};
          }
          assert value != null;
          String name = value.length == 0 ? javaMethodMeta.getName() : value[0];
          name = StringUtils.hasText(name) ? name : javaMethodMeta.getName();
          javaMethodMetaMap.put(MessageFormat.format("{0}@{1}",
              Arrays.stream(methods)
                  .map(Enum::name)
                  .collect(Collectors.joining("@")), name),
              javaMethodMeta);
        });
    if (javaMethodMetaMap.size() > 0) {
      return new ArrayList<>(javaMethodMetaMap.values());
    }
    return methodMetas;
  }

  /**
   * 查找 spring controller mapping注解
   *
   * @param javaMethodMeta
   * @return
   */
  private static Annotation findSpringControllerMappingAnnotation(JavaMethodMeta javaMethodMeta) {
    Annotation annotation = Arrays.stream(SPRING_MAPPING_ANNOTATIONS)
        .map(aClass -> javaMethodMeta.getAnnotation(aClass))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);

    return annotation;
  }
}
