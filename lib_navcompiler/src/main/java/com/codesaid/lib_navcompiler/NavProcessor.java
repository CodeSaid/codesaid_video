package com.codesaid.lib_navcompiler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codesaid.lib_navannotation.ActivityDestination;
import com.codesaid.lib_navannotation.FragmentDestination;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Created By codesaid
 * On :2020-05-04 02:06
 * Package Name: com.codesaid.lib_navcompiler
 * desc:
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.codesaid.lib_navannotation.ActivityDestination", "com.codesaid.lib_navannotation.FragmentDestination"})
public class NavProcessor extends AbstractProcessor {

    private Messager mMessager = null;
    private Filer mFiler = null;
    private static final String OUTPUT_FILE_NAME = "destination.json";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }

    @SuppressWarnings("CharsetObjectCanBeUsed")
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> fragmentElements = roundEnvironment.getElementsAnnotatedWith(FragmentDestination.class);
        Set<? extends Element> activityElements = roundEnvironment.getElementsAnnotatedWith(ActivityDestination.class);

        if (!fragmentElements.isEmpty() || !activityElements.isEmpty()) {
            HashMap<String, JSONObject> destMap = new HashMap<>();
            handleDestination(fragmentElements, FragmentDestination.class, destMap);
            handleDestination(activityElements, ActivityDestination.class, destMap);

            //app/src/main/assets

            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            try {
                FileObject resource = mFiler.createResource(StandardLocation.CLASS_OUTPUT,
                        "", OUTPUT_FILE_NAME);
                String resourcePath = resource.toUri().getPath();
                mMessager.printMessage(Diagnostic.Kind.NOTE, "resourcePath: " + resourcePath);
                String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
                String assetsPath = appPath + "src/main/assets/";

                File file = new File(assetsPath);
                if (!file.exists()) {
                    file.mkdirs();
                }

                File outputFile = new File(file, OUTPUT_FILE_NAME);
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                outputFile.createNewFile();
                String content = JSON.toJSONString(destMap);

                fos = new FileOutputStream(outputFile);
                writer = new OutputStreamWriter(fos, "UTF-8");
                writer.write(content);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return true;
    }

    private void handleDestination(Set<? extends Element> elements
            , Class<? extends Annotation> annotationClassZ, HashMap<String, JSONObject> destMap) {

        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;

            String pageUrl = null;
            String className = typeElement.getQualifiedName().toString();
            int id = Math.abs(className.hashCode());
            boolean needLogin = false;
            boolean asStarter = false;
            boolean isFragment = false;

            Annotation annotation = typeElement.getAnnotation(annotationClassZ);
            if (annotation instanceof FragmentDestination) {
                FragmentDestination dest = (FragmentDestination) annotation;
                pageUrl = dest.pageUrl();
                needLogin = dest.needLogin();
                asStarter = dest.asStarter();
                isFragment = true;
            } else if (annotation instanceof ActivityDestination) {
                ActivityDestination dest = (ActivityDestination) annotation;
                pageUrl = dest.pageUrl();
                needLogin = dest.needLogin();
                asStarter = dest.asStarter();
                isFragment = false;
            }

            if (destMap.containsKey(pageUrl)) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的 pageUrl: " + className);
            } else {
                JSONObject json = new JSONObject();
                json.put("id", id);
                json.put("className", className);
                json.put("pageUrl", pageUrl);
                json.put("needLogin", needLogin);
                json.put("asStarter", asStarter);
                json.put("isFragment", isFragment);

                destMap.put(pageUrl, json);
            }
        }
    }
}
