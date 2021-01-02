package test.com.wuxp.codegen.swagger2;

import com.wuxp.codegen.core.ClientProviderType;
import com.wuxp.codegen.core.CodeGenerator;
import com.wuxp.codegen.core.parser.LanguageParser;
import com.wuxp.codegen.core.strategy.PackageMapStrategy;
import com.wuxp.codegen.core.strategy.TemplateStrategy;
import com.wuxp.codegen.dragon.DragonSimpleTemplateStrategy;
import com.wuxp.codegen.dragon.strategy.TypescriptPackageMapStrategy;
import com.wuxp.codegen.model.CommonCodeGenClassMeta;
import com.wuxp.codegen.model.LanguageDescription;
import com.wuxp.codegen.model.TemplateFileVersion;
import com.wuxp.codegen.model.languages.typescript.TypescriptClassMeta;
import com.wuxp.codegen.model.mapping.AbstractTypeMapping;
import com.wuxp.codegen.swagger2.Swagger2CodeGenerator;
import com.wuxp.codegen.swagger2.Swagger2FeignSdkGenMatchingStrategy;
import com.wuxp.codegen.swagger2.builder.Swagger2FeignTypescriptCodegenBuilder;
import com.wuxp.codegen.swagger2.example.controller.BaseController;
import com.wuxp.codegen.swagger2.example.evt.BaseEvt;
import com.wuxp.codegen.swagger2.example.resp.PageInfo;
import com.wuxp.codegen.swagger2.example.resp.ServiceQueryResponse;
import com.wuxp.codegen.swagger2.example.resp.ServiceResponse;
import com.wuxp.codegen.swagger2.languages.Swagger2FeignSdkTypescriptParser;
import com.wuxp.codegen.templates.FreemarkerTemplateLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.File;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 测试swagger 生成  typescript的 feign api sdk
 */
@Slf4j
public class Swagger2FeignSdkCodegenTypescriptTest {


    private CodeGenerator codeGenerator;

    @Before
    public void before() {

        //设置基础数据类型的映射关系
        AbstractTypeMapping.setBaseTypeMapping(ServiceQueryResponse.class, TypescriptClassMeta.PROMISE);
        AbstractTypeMapping.setBaseTypeMapping(ServiceResponse.class, TypescriptClassMeta.PROMISE);

        //自定义的类型映射
        AbstractTypeMapping.setCustomizeJavaTypeMapping(ServiceQueryResponse.class, new Class<?>[]{ServiceResponse.class, PageInfo.class});

        //包名映射关系
        Map<String, String> packageMap = new LinkedHashMap<>();

        //控制器的包所在
        packageMap.put("com.wuxp.codegen.swagger2.controller", "services");
        //其他类（DTO、VO等）所在的包
        packageMap.put("com.wuxp.codegen.swagger2.example", "");

        //实例化包名映射策略
        PackageMapStrategy packageMapStrategy = new TypescriptPackageMapStrategy(packageMap);

        //实例化语言解析器
        LanguageParser languageParser = new Swagger2FeignSdkTypescriptParser(packageMapStrategy, new Swagger2FeignSdkGenMatchingStrategy(), null);

        String language = LanguageDescription.TYPESCRIPT.getName();

        //实例化模板加载器
        HashMap<String, Object> sharedVariables = new HashMap<>();
        FreemarkerTemplateLoader templateLoader = new FreemarkerTemplateLoader(ClientProviderType.TYPESCRIPT_FEIGN, TemplateFileVersion.V_1_0_0, sharedVariables);


        String[] outPaths = {"codegen-result", language.toLowerCase(), "src", "api"};

        TemplateStrategy<CommonCodeGenClassMeta> templateStrategy = new DragonSimpleTemplateStrategy(
                templateLoader,
                Paths.get(System.getProperty("user.dir")).resolveSibling(String.join(File.separator, outPaths)).toString(),
                LanguageDescription.TYPESCRIPT.getSuffixName(), true);

        //要进行生成的源代码包名列表
        String[] packagePaths = {"com.wuxp.codegen.swagger2.example.controller"};

        //创建代码生成器
        this.codeGenerator = new Swagger2CodeGenerator(packagePaths, languageParser, templateStrategy, false);
    }


    @Test
    public void testCodeGenApi() {

        //生成
        codeGenerator.generate();

//        AntPathMatcher antPathMatcher = new AntPathMatcher();
////        boolean match1 = antPathMatcher.match("/path/**/?z", "/path/x/y/z/xyz");
////        boolean match2 = antPathMatcher.match("/foo/{id}/bar", "/foo/1/bar");
//        boolean match2 = antPathMatcher.match("/**/*.jsp", "/app/dir/file.jsp");
////        boolean match3 = antPathMatcher.match("/**/example", "/app/foo/example1");
////        boolean match4 = antPathMatcher.match("/app/**", "/app/foo/example1");
//        System.out.println(match2);
////        System.out.println(match1+"   "+match2);

    }

    @Test
    public void testCodeGenTypescriptApiByStater() {

        //设置基础数据类型的映射关系
        Map<Class<?>, CommonCodeGenClassMeta> baseTypeMapping = new HashMap<>();
        baseTypeMapping.put(ServiceQueryResponse.class, TypescriptClassMeta.PROMISE);
        baseTypeMapping.put(ServiceResponse.class, TypescriptClassMeta.PROMISE);

        //自定义的类型映射
        Map<Class<?>, Class<?>[]> customTypeMapping = new HashMap<>();
        customTypeMapping.put(ServiceQueryResponse.class, new Class<?>[]{ServiceResponse.class, PageInfo.class});

        //包名映射关系
        Map<String, String> packageMap = new LinkedHashMap<>();

        //控制器的包所在
//        packageMap.put("com.wuxp.codegen.swagger2.controller", "services");
        packageMap.put("com.wuxp.codegen.swagger2.**.controller", "{0}services");
        packageMap.put("com.wuxp.codegen.swagger2.**.evt", "evt");
        packageMap.put("com.wuxp.codegen.swagger2.**.domain", "domain");
        packageMap.put("com.wuxp.codegen.swagger2.**.resp", "resp");
        packageMap.put("com.wuxp.codegen.swagger2.**.enums", "enums");
        //其他类（DTO、VO等）所在的包
//        packageMap.put("com.wuxp.codegen.swagger2.example", "");

        String language = LanguageDescription.TYPESCRIPT.getName();
        String[] outPaths = {"codegen-result", language.toLowerCase(), ClientProviderType.TYPESCRIPT_FEIGN.name().toLowerCase(), "swagger2", "src", "api"};

        //要进行生成的源代码包名列表
        String[] packagePaths = {"com.wuxp.codegen.swagger2.**.controller"};

        Swagger2FeignTypescriptCodegenBuilder.builder()
                .baseTypeMapping(baseTypeMapping)
                .languageDescription(LanguageDescription.TYPESCRIPT)
                .clientProviderType(ClientProviderType.TYPESCRIPT_FEIGN)
                .customJavaTypeMapping(customTypeMapping)
                .packageMapStrategy(new TypescriptPackageMapStrategy(packageMap))
                .outPath(Paths.get(System.getProperty("user.dir")).resolveSibling(String.join(File.separator, outPaths)).toString())
                .scanPackages(packagePaths)
                .buildCodeGenerator()
                .generate();

    }

    protected PathMatcher pathMatcher = new AntPathMatcher();

    @Test
    public void testAntPathMatcher() {
        String name = BaseController.class.getName();
        String name1 = BaseEvt.class.getName();

        boolean pattern = pathMatcher.isPattern("com.wuxp.codegen.swagger2.**");
        boolean b = pathMatcher.match("com.wuxp.codegen.swagger2.**.controller**", name);
        boolean b3 = pathMatcher.match("com.wuxp.codegen.swagger2.example.controller**", name);
        String extractPathWithinPattern = pathMatcher.extractPathWithinPattern("com.wuxp.codegen.swagger2.**.controller**", name);
        Map<String, String> map = pathMatcher.extractUriTemplateVariables("com.wuxp.codegen.swagger2.**.controller**", name);
        Comparator<String> patternComparator = pathMatcher.getPatternComparator("com.wuxp.codegen.swagger2.**.controller**");
        boolean b2 = pathMatcher.match("com.wuxp.codegen.swagger2.**.controller**", name1);
        Assert.assertTrue(b);
        Assert.assertFalse(b2);


        //com.wuxp.codegen.swagger2\.+?(\w*)\.controller
        System.out.println(name);
        Pattern pattern1 = Pattern.compile("com.wuxp.codegen.swagger2\\.+?(.*)\\.controller");
//        Pattern pattern1=Pattern.compile("com.wuxp.codegen.swagger2\\.+?(.*)\\.controller");
//        Pattern pattern1=Pattern.compile("com.wuxp.codegen.swagger2\\.+?(\\w*)\\.controller\\w*");
//        Pattern pattern1=Pattern.compile("com.wuxp.codegen.swagger2.(\\w*?).controller");

        Matcher matcher = pattern1.matcher(name);
        System.out.println(matcher.groupCount());
        while (matcher.find()) {
            String group = matcher.group();
            System.out.println(group);
        }
//        String[] split = pattern1.split(name);
//        System.out.println(split);

        String[] strings = "com.wuxp.codegen.swagger2.**.controller**".split("\\*\\*");

        String s = name.replaceAll(strings[0], "");
        s = s.substring(0, s.indexOf(strings[1]));
        System.out.println(s);

    }
}