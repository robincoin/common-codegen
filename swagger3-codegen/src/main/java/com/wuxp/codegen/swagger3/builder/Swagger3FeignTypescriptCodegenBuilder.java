package com.wuxp.codegen.swagger3.builder;

import com.wuxp.codegen.AbstractDragonCodegenBuilder;
import com.wuxp.codegen.core.CodeGenerator;
import com.wuxp.codegen.core.macth.IgnoreClassCodeGenMatcher;
import com.wuxp.codegen.core.parser.LanguageParser;
import com.wuxp.codegen.core.strategy.TemplateStrategy;
import com.wuxp.codegen.dragon.DragonSimpleTemplateStrategy;
import com.wuxp.codegen.enums.CodeRuntimePlatform;
import com.wuxp.codegen.model.CommonCodeGenClassMeta;
import com.wuxp.codegen.model.LanguageDescription;
import com.wuxp.codegen.swagger3.Swagger3CodeGenerator;
import com.wuxp.codegen.swagger3.Swagger3FeignSdkGenMatchingStrategy;
import com.wuxp.codegen.swagger3.languages.Swagger3FeignSdkTypescriptParser;
import com.wuxp.codegen.templates.FreemarkerTemplateLoader;
import com.wuxp.codegen.templates.TemplateLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Swagger3FeignTypescriptCodegenBuilder extends AbstractDragonCodegenBuilder {


    protected Swagger3FeignTypescriptCodegenBuilder() {
        super();
    }


    public static Swagger3FeignTypescriptCodegenBuilder builder() {
        return new Swagger3FeignTypescriptCodegenBuilder();
    }

    @Override
    public CodeGenerator buildCodeGenerator() {
        if (this.codeRuntimePlatform == null) {
            this.codeRuntimePlatform = CodeRuntimePlatform.BROWSER;
        }

        this.initTypeMapping();
        //实例化语言解析器
        LanguageParser languageParser = new Swagger3FeignSdkTypescriptParser(
                packageMapStrategy,
                new Swagger3FeignSdkGenMatchingStrategy(this.ignoreMethods),
                this.codeDetects);

        languageParser.addCodeGenMatchers(new IgnoreClassCodeGenMatcher(ignoreClasses));

        //实例化模板加载器
        TemplateLoader templateLoader = new FreemarkerTemplateLoader(LanguageDescription.TYPESCRIPT, this.templateFileVersion, this.getSharedVariables());

        TemplateStrategy<CommonCodeGenClassMeta> templateStrategy = new DragonSimpleTemplateStrategy(
                templateLoader,
                this.outPath,
                LanguageDescription.TYPESCRIPT.getSuffixName(),
                this.isDeletedOutputDirectory);

        return new Swagger3CodeGenerator(
                this.scanPackages,
                this.ignorePackages,
                this.includeClasses,
                this.ignoreClasses,
                languageParser,
                templateStrategy,
                this.looseMode,
                this.enableFieldUnderlineStyle);
    }
}