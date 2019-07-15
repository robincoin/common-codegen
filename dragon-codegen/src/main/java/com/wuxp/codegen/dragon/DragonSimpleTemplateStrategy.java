package com.wuxp.codegen.dragon;

import com.wuxp.codegen.core.constant.FeignApiSdkTemplateName;
import com.wuxp.codegen.core.strategy.TemplateStrategy;
import com.wuxp.codegen.dragon.path.PathResolve;
import com.wuxp.codegen.model.CommonCodeGenClassMeta;
import com.wuxp.codegen.model.CommonCodeGenMethodMeta;
import com.wuxp.codegen.model.enums.ClassType;
import com.wuxp.codegen.templates.TemplateLoader;
import com.wuxp.codegen.utils.FileUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.MessageFormat;


/**
 * 模板处理策略
 * <p>
 * 负责将数据和模板合并，生成最终的文件
 * </p>
 */
@Slf4j
public class DragonSimpleTemplateStrategy implements TemplateStrategy<CommonCodeGenClassMeta> {

    //在LAST_MODIFIED_MINUTE内生成的文件不在生成
    public static final float LAST_MODIFIED_MINUTE = 0.1f;


    /**
     * 模板加载器
     */
    protected TemplateLoader<Template> templateLoader;

    /**
     * 输出的根目录
     */
    protected String outputPath;

    /**
     * 文件扩展名称
     */
    protected String extName;

    public DragonSimpleTemplateStrategy(TemplateLoader<Template> templateLoader,
                                        String outputPath,
                                        String extName,
                                        boolean isDeletedOutputDirectory) {
        this.templateLoader = templateLoader;
        this.outputPath = outputPath.endsWith(File.separator) ? outputPath : outputPath + File.separator;
        this.extName = extName;

        if (isDeletedOutputDirectory) {
            //删除原本的目录
            boolean r = FileUtil.deleteDirectory(this.outputPath);
            log.info("删除原本的输出目录{}，删除{}", this.outputPath, r ? "成功" : "失败");
        }

    }

    @Override
    public void build(CommonCodeGenClassMeta data) {

        //根据是否为接口类型的元数据还是dto的类型的元数据加载不同的模板
        String templateName = this.getTemplate(data);


        Template template = this.templateLoader.load(templateName);
        if (template == null) {
            log.warn("没有找到模板{}", templateName);
            return;
        }

        String packagePath = data.getPackagePath();


        String output = Paths.get(MessageFormat.format("{0}{1}.{2}", this.outputPath, this.normalizationFilePath(packagePath), this.extName)).toString();

        //如果生成的文件没有文件名称，即输出如今形如 /a/b/.extName的格式
        if (output.contains(MessageFormat.format("{0}.{1}", File.separator, this.extName))) {
            log.warn("类{}，的生成输入路径有误,{}", data.getName(), output);

        }

        File file = new File(output);
        if (file.exists()) {
            if (System.currentTimeMillis() - file.lastModified() <= LAST_MODIFIED_MINUTE * 60 * 1000) {
                log.warn("文件{}在{}分钟内已经生成过，跳过生成", output, LAST_MODIFIED_MINUTE);
                return;
            }
        }
        FileUtil.createDirectory(output.substring(0, output.lastIndexOf(File.separator)));
        log.info("生成类{}的文件，输出到{}目录", data.getName(), output);
        Writer writer = null;
        try {
            //输出
            writer = new OutputStreamWriter(new FileOutputStream(output),
                    StandardCharsets.UTF_8);
            //添加自定义方法
            template.process(data, writer);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected String getTemplate(CommonCodeGenClassMeta data) {
        String templateName = null;
        CommonCodeGenMethodMeta[] methodMetas = data.getMethodMetas();
        if (methodMetas == null || methodMetas.length == 0) {
            //DTO or enum
            if (ClassType.ENUM.equals(data.getClassType())) {
                templateName = FeignApiSdkTemplateName.API_ENUM_TEMPLATE_NAME;
            } else {
                //区分请求对象还是响应对象
                templateName = FeignApiSdkTemplateName.API_REQUEST_TEMPLATE_NAME;
            }
        } else {
            //api 接口
            templateName = FeignApiSdkTemplateName.API_SERVICE_TEMPLATE_NAME;

        }
        return templateName;
    }

    protected String normalizationFilePath(String packagePath) {

        return packagePath.replaceAll("\\.", PathResolve.RIGHT_SLASH);

    }
}