package com.wuxp.codegen.swagger2.builder;

import com.wuxp.codegen.AbstractDragonCodegenBuilder;
import com.wuxp.codegen.annotation.processor.spring.RequestMappingProcessor;
import com.wuxp.codegen.core.ClientProviderType;
import com.wuxp.codegen.core.CodeGenerator;
import com.wuxp.codegen.core.event.DisruptorCodeGenPublisher;
import com.wuxp.codegen.core.macth.IgnoreClassCodeGenMatcher;
import com.wuxp.codegen.core.parser.LanguageParser;
import com.wuxp.codegen.core.strategy.TemplateStrategy;
import com.wuxp.codegen.disruptor.DartFeignCodeGenEventHandler;
import com.wuxp.codegen.dragon.DragonSimpleTemplateStrategy;
import com.wuxp.codegen.languages.AbstractDartParser;
import com.wuxp.codegen.model.CommonCodeGenClassMeta;
import com.wuxp.codegen.model.LanguageDescription;
import com.wuxp.codegen.model.languages.dart.DartClassMeta;
import com.wuxp.codegen.swagger2.Swagger2CodeGenerator;
import com.wuxp.codegen.swagger2.Swagger2FeignSdkGenMatchingStrategy;
import com.wuxp.codegen.swagger2.languages.Swagger2FeignSdkDartParser;
import com.wuxp.codegen.templates.FreemarkerTemplateLoader;
import com.wuxp.codegen.templates.TemplateLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author wuxp
 */
@Slf4j
public class Swagger2FeignDartCodegenBuilder extends AbstractDragonCodegenBuilder {

    private Map<Class<?>, List<String>> ignoreFields;

    /**
     * sdk索引文件名称
     */
    private String sdkIndexFileName = "feign_sdk";

    /**
     * 类型别名
     */
    private Map<DartClassMeta, List<String>> typeAlias = Collections.emptyMap();

    public static Swagger2FeignDartCodegenBuilder builder() {
        return new Swagger2FeignDartCodegenBuilder();
    }

    public Swagger2FeignDartCodegenBuilder ignoreFields(Map<Class<?>, List<String>> ignoreFields) {
        this.ignoreFields = ignoreFields;
        return this;
    }

    public Swagger2FeignDartCodegenBuilder typeAlias(Map<DartClassMeta, List<String>> typeAlias) {
        this.typeAlias = typeAlias;
        return this;
    }

    public Swagger2FeignDartCodegenBuilder sdkIndexFileName(String sdkIndexFileName) {
        this.sdkIndexFileName = sdkIndexFileName;
        return this;
    }


    @Override
    public CodeGenerator buildCodeGenerator() {
        if (this.languageDescription == null) {
            this.languageDescription = LanguageDescription.DART;
        }

        if (this.clientProviderType == null) {
            this.clientProviderType = ClientProviderType.DART_FEIGN;
        }
        this.initTypeMapping();
        //实例化语言解析器
        LanguageParser languageParser = new Swagger2FeignSdkDartParser(
                packageMapStrategy,
                new Swagger2FeignSdkGenMatchingStrategy(this.ignoreMethods),
                this.codeDetects,
                this.ignoreFields);
        languageParser.addCodeGenMatchers(new IgnoreClassCodeGenMatcher(ignoreClasses));
        languageParser.setLanguageEnhancedProcessor(this.languageEnhancedProcessor);

        //实例化模板加载器
        TemplateLoader templateLoader = new FreemarkerTemplateLoader(this.clientProviderType, this.templateFileVersion, this.getSharedVariables());

        TemplateStrategy<CommonCodeGenClassMeta> templateStrategy = new DragonSimpleTemplateStrategy(
                templateLoader,
                this.outPath,
                this.languageDescription.getSuffixName(),
                this.isDeletedOutputDirectory,
                filepath -> {
                    if (StringUtils.hasText(filepath)) {
                        return AbstractDartParser.dartFileNameConverter(filepath);
                    }
                    return filepath;
                });

        Thread mainThread = Thread.currentThread();

        DartFeignCodeGenEventHandler dartFeignCodeGenEventHandler = new DartFeignCodeGenEventHandler(templateLoader, this.outPath, mainThread);
        dartFeignCodeGenEventHandler.setSdkIndexFileName(this.sdkIndexFileName);
        dartFeignCodeGenEventHandler.setTypeAlias(this.typeAlias);
        RequestMappingProcessor.setSupportAuthenticationType(true);

        return new Swagger2CodeGenerator(
                this.scanPackages,
                this.ignorePackages,
                this.includeClasses,
                this.ignoreClasses,
                languageParser,
                templateStrategy,
                this.looseMode,
                this.enableFieldUnderlineStyle,
                new DisruptorCodeGenPublisher(dartFeignCodeGenEventHandler));
    }
}