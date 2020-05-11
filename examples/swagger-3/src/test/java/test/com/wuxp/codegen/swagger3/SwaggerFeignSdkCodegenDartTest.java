package test.com.wuxp.codegen.swagger3;

import com.wuxp.codegen.api.WuxpApiEnhancedProcessor;
import com.wuxp.codegen.dragon.strategy.TypescriptPackageMapStrategy;
import com.wuxp.codegen.model.CommonCodeGenClassMeta;
import com.wuxp.codegen.model.LanguageDescription;
import com.wuxp.codegen.model.TemplateFileVersion;
import com.wuxp.codegen.model.languages.dart.DartClassMeta;
import com.wuxp.codegen.model.languages.typescript.TypescriptClassMeta;
import com.wuxp.codegen.swagger3.builder.Swagger3FeignDartCodegenBuilder;
import com.wuxp.codegen.swagger3.builder.Swagger3FeignTypescriptCodegenBuilder;
import com.wuxp.codegen.swagger3.example.controller.OrderController;
import com.wuxp.codegen.swagger3.example.evt.BaseQueryEvt;
import com.wuxp.codegen.swagger3.example.resp.PageInfo;
import com.wuxp.codegen.swagger3.example.resp.ServiceQueryResponse;
import com.wuxp.codegen.swagger3.example.resp.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

/**
 * 测试swagger 生成  dart的 feign api sdk
 */
@Slf4j
public class SwaggerFeignSdkCodegenDartTest {


    @Test
    public void testCodeGenDartApiSdk() {

        //设置基础数据类型的映射关系
        Map<Class<?>, CommonCodeGenClassMeta> baseTypeMapping = new HashMap<>();
        baseTypeMapping.put(ServiceQueryResponse.class, DartClassMeta.FUTRUE);
        baseTypeMapping.put(ServiceResponse.class, DartClassMeta.FUTRUE);

        //自定义的类型映射
        Map<Class<?>, Class<?>[]> customTypeMapping = new HashMap<>();
        customTypeMapping.put(ServiceQueryResponse.class, new Class<?>[]{ServiceResponse.class, PageInfo.class});

        //包名映射关系
        Map<String, String> packageMap = new LinkedHashMap<>();

        //控制器的包所在
//        packageMap.put("com.wuxp.codegen.swagger3.controller", "services");
        packageMap.put("com.wuxp.codegen.swagger3.**.controller", "{0}services");
        packageMap.put("com.wuxp.codegen.swagger3.**.evt", "evt");
        packageMap.put("com.wuxp.codegen.swagger3.**.domain", "domain");
        packageMap.put("com.wuxp.codegen.swagger3.**.resp", "resp");
        packageMap.put("com.wuxp.codegen.swagger3.**.enums", "enums");
        //其他类（DTO、VO等）所在的包
//        packageMap.put("com.wuxp.codegen.swagger3.example", "");

        String language = LanguageDescription.DART.getName();
        String[] outPaths = {"codegen-result", language.toLowerCase(), "src", "api"};

        //要进行生成的源代码包名列表
        String[] packagePaths = {"com.wuxp.codegen.swagger3.**.controller"};


        Map<String, Object> classNameTransformers = new HashMap<>();
        classNameTransformers.put(OrderController.class.getSimpleName(), "OrderFeignClient");

        Map<Class<?>, List<String>> ignoreFields = new HashMap<Class<?>, List<String>>() {{
            put(BaseQueryEvt.class, Arrays.asList("queryPage"));
        }};

        Map<DartClassMeta, List<String>> typeAlias = new HashMap<DartClassMeta, List<String>>() {{
            put(DartClassMeta.BUILT_LIST, Arrays.asList("PageInfo"));
        }};

        Swagger3FeignDartCodegenBuilder.builder()
                .ignoreFields(ignoreFields)
                .typeAlias(typeAlias)
                .baseTypeMapping(baseTypeMapping)
                .customJavaTypeMapping(customTypeMapping)
                .packageMapStrategy(new TypescriptPackageMapStrategy(packageMap, classNameTransformers))
                .outPath(Paths.get(System.getProperty("user.dir")).resolveSibling(String.join(File.separator, outPaths)).toString())
                .scanPackages(packagePaths)
                .isDeletedOutputDirectory(true)
                .languageEnhancedProcessor(new WuxpApiEnhancedProcessor())
                .buildCodeGenerator()
                .generate();

    }


}
