package com.shawnma.gwt.i18n.tool;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class I18nGen extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        // Build a new class, that implements a "paintScreen" method
        JClassType classType;

        try {
            classType = context.getTypeOracle().getType(typeName);

            // Here you would retrieve the metadata based on typeName for this Screen
            SourceWriter src = getSourceWriter(classType, context, logger);
            String resource =  classType.getName();
            if (src != null) {
                JMethod[] methods = classType.getMethods();
                for (JMethod m : methods) {
                    JParameter[] params = m.getParameters();
                    String decl = m.getReadableDeclaration(false, true, true, true, true);
                    src.print(decl);
                    src.print("{return StringTranslator.translate(\"");
                    src.print(resource + "\",\"");
                    src.print(m.getName() + "\"");
                    if (params != null) {
                        for (JParameter p : params) {
                            src.print("," + p.getName());
                        }
                    }
                    src.println(");}\n");
                }

                src.commit(logger);
            }
            return typeName + "Generated";

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
        String packageName = classType.getPackage().getName();
        String simpleName = classType.getSimpleSourceName() + "Generated";
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
        composer.addImplementedInterface(classType.getQualifiedSourceName());

        // Need to add whatever imports your generated class needs.
        composer.addImport("com.innopath.gwt.i18n.client.StringTranslator");

        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        if (printWriter == null) { // already exists.
            return null;
        } else {
            SourceWriter sw = composer.createSourceWriter(context, printWriter);
            return sw;
        }
    }

}
