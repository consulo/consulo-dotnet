/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consulo.dotnet.module.extension;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.concurrent.coroutine.ReadLock;
import consulo.disposer.Disposable;
import consulo.disposer.Disposer;
import consulo.dotnet.DotNetBundle;
import consulo.dotnet.DotNetTarget;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.language.psi.PsiElement;
import consulo.localize.LocalizeValue;
import consulo.module.ui.extension.ModuleExtensionBundleBoxBuilder;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.DumbService;
import consulo.project.Project;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.InputBoxBuilder;
import consulo.ui.InputProblem;
import consulo.ui.Label;
import consulo.ui.ListBox;
import consulo.ui.TextAttribute;
import consulo.ui.TextBox;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.toolbar.AddAction;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilderFactory;
import consulo.ui.layout.DockLayout;
import consulo.ui.layout.LabeledLayout;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.ui.util.FormBuilder;
import consulo.util.concurrent.coroutine.Coroutine;
import consulo.util.concurrent.coroutine.CoroutineScope;
import consulo.util.lang.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author VISTALL
 * @since 2014-07-31
 */
public final class DotNetConfigurationPanel {
    private DotNetConfigurationPanel() {
    }

    @RequiredUIAccess
    public static Component create(
        DotNetMutableModuleExtension<?> extension,
        DotNetElementQualifierProducer qualifierProducer,
        List<String> variables,
        Disposable uiDisposable,
        Runnable updater
    ) {
        ModuleExtensionBundleBoxBuilder<DotNetMutableModuleExtension<?>> sdkBoxBuilder =
            ModuleExtensionBundleBoxBuilder.create(extension, uiDisposable, updater);
        sdkBoxBuilder.sdkTypeClass(extension.getSdkTypeClass());
        sdkBoxBuilder.sdkPointerFunc(DotNetSimpleMutableModuleExtension::getInheritableSdk);

        TextBox fileNameBox = TextBox.create(extension.getFileName());
        fileNameBox.setPlaceholder(LocalizeValue.of(DotNetModuleExtension.DEFAULT_FILE_NAME));
        fileNameBox.addValueListener(event -> extension.setFileName(StringUtil.notNullize(event.getValue())));

        TextBox outputDirBox = TextBox.create(extension.getOutputDir());
        outputDirBox.addValueListener(event -> extension.setOutputDir(StringUtil.notNullize(event.getValue())));

        ComboBox<DotNetTarget> targetBox = ComboBox.create(DotNetTarget.values());
        targetBox.setTextRenderer(target -> target == null ? LocalizeValue.empty() : target.getDescription());
        targetBox.setValue(extension.getTarget());
        targetBox.addValueListener(event -> {
            DotNetTarget target = event.getValue();
            if (target != null) {
                extension.setTarget(target);
            }
        });

        ComboBox<DotNetMainTypeItem> mainTypeBox = createMainTypeBox(extension, qualifierProducer, uiDisposable);

        TextBox namespaceBox = TextBox.create(extension.getNamespacePrefix());
        namespaceBox.addValueListener(event -> extension.setNamespacePrefix(StringUtil.notNullize(event.getValue())));

        Label namespaceLabel = Label.create(LocalizeValue.of(DotNetBundle.message("namespace.label")));

        FormBuilder formBuilder = FormBuilder.create();
        formBuilder.addLabeled(LocalizeValue.of(DotNetBundle.message("file.label")), fileNameBox);
        formBuilder.addLabeled(LocalizeValue.of(DotNetBundle.message("output.dir.label")), outputDirBox);
        formBuilder.addLabeled(LocalizeValue.of(DotNetBundle.message("target.label")), targetBox);
        formBuilder.addLabeled(LocalizeValue.of(DotNetBundle.message("main.type.label")), mainTypeBox);
        formBuilder.addLabeled(namespaceLabel, namespaceBox);

        CheckBox debugInfoBox =
            CheckBox.create(LocalizeValue.of(DotNetBundle.message("generate.debug.info.label")), extension.isAllowDebugInfo());
        debugInfoBox.addValueListener(event -> extension.setAllowDebugInfo(Boolean.TRUE.equals(event.getValue())));

        CheckBox allowSourceRootsBox =
            CheckBox.create(LocalizeValue.of(DotNetBundle.message("allow.source.roots.label")), extension.isAllowSourceRoots());
        namespaceLabel.setVisible(!extension.isAllowSourceRoots());
        namespaceBox.setVisible(!extension.isAllowSourceRoots());
        allowSourceRootsBox.addValueListener(event -> {
            boolean allowSourceRoots = Boolean.TRUE.equals(event.getValue());
            extension.setAllowSourceRoots(allowSourceRoots);
            namespaceLabel.setVisible(!allowSourceRoots);
            namespaceBox.setVisible(!allowSourceRoots);

            updater.run();
        });

        VerticalLayout topLayout = VerticalLayout.create();
        topLayout.add(sdkBoxBuilder.build());
        topLayout.add(debugInfoBox);
        topLayout.add(allowSourceRootsBox);
        topLayout.add(formBuilder.build());

        DockLayout layout = DockLayout.create();
        layout.top(topLayout);
        layout.center(createVariableList(variables));
        return layout;
    }

    @RequiredUIAccess
    public static Component createVariableList(List<String> variables) {
        MutableFlatDataModel<String> model = FlatDataModel.of(variables);
        model.addListener(event -> {
            variables.clear();
            for (String variable : model) {
                variables.add(variable);
            }
        });

        ListBox<String> listBox = ListBox.create(model);

        Component decorated = ToolbarDecoratorBuilderFactory.getInstance()
            .create(listBox)
            .addOrReplaceAction(new AddAction<>() {
                @Override
                @RequiredUIAccess
                protected void doAdd(AnActionEvent e) {
                    UIAccess uiAccess = UIAccess.current();
                    InputBoxBuilder.text()
                        .title(LocalizeValue.of(DotNetBundle.message("new.variable.title")))
                        .text(LocalizeValue.of(DotNetBundle.message("new.variable.message")))
                        .validator(value -> {
                            String name = StringUtil.notNullize(value);
                            if (name.isEmpty()) {
                                return InputProblem.error(LocalizeValue.of(DotNetBundle.message("new.variable.empty")));
                            }
                            if (model.indexOf(name) >= 0) {
                                return InputProblem.error(LocalizeValue.of(DotNetBundle.message("new.variable.exists", name)));
                            }
                            return null;
                        })
                        .showAsync(listBox)
                        .whenCompleteAsync(
                            (value, error) -> {
                                if (error == null && !StringUtil.isEmpty(value) && model.indexOf(value) < 0) {
                                    model.add(value);
                                }
                            },
                            uiAccess
                        );
                }
            })
            .build();

        return LabeledLayout.create(LocalizeValue.of(DotNetBundle.message("preprocessor.variables.label")), decorated);
    }

    @RequiredUIAccess
    private static ComboBox<DotNetMainTypeItem> createMainTypeBox(
        DotNetMutableModuleExtension<?> extension,
        DotNetElementQualifierProducer qualifierProducer,
        Disposable uiDisposable
    ) {
        String currentMainType = extension.getMainType();

        List<DotNetMainTypeItem> initialItems = new ArrayList<>();
        initialItems.add(DotNetMainTypeItem.NONE);
        if (currentMainType != null) {
            initialItems.add(new DotNetMainTypeItem(currentMainType, null, true));
        }

        MutableFlatDataModel<DotNetMainTypeItem> model = FlatDataModel.of(initialItems);
        ComboBox<DotNetMainTypeItem> mainTypeBox = ComboBox.create(model);
        mainTypeBox.setRenderer((presentation, item) -> {
            DotNetMainTypeItem value = item.getValue();
            String qualifiedName = value == null ? null : value.qualifiedName();
            if (value == null || qualifiedName == null) {
                presentation.append(LocalizeValue.of(DotNetBundle.message("main.type.none")));
            }
            else if (value.valid()) {
                presentation.withIcon(value.icon());
                presentation.append(qualifiedName);
            }
            else {
                presentation.withIcon(PlatformIconGroup.toolbarUnknown());
                presentation.append(qualifiedName, TextAttribute.ERROR);
            }
        });
        mainTypeBox.setValue(initialItems.get(initialItems.size() - 1), false);
        mainTypeBox.setEnabled(false);
        mainTypeBox.addValueListener(event -> {
            if (!mainTypeBox.isEnabled()) {
                return;
            }

            DotNetMainTypeItem value = event.getValue();
            extension.setMainType(value == null ? null : value.qualifiedName());
        });

        Project project = extension.getProject();
        if (DumbService.isDumb(project)) {
            return mainTypeBox;
        }

        UIAccess uiAccess = UIAccess.current();
        CoroutineScope scope = CoroutineScope.of(project.coroutineContext());
        Disposer.register(uiDisposable, scope::cancel);

        Coroutine.first(ReadLock.<Object, List<DotNetMainTypeItem>>apply(ignored -> collectMainTypes(extension, qualifierProducer)))
            .runAsync(scope, null)
            .toFuture()
            .whenCompleteAsync((items, error) -> {
                if (error != null || items == null || project.isDisposed() || Disposer.isDisposed(uiDisposable)) {
                    return;
                }

                applyMainTypes(extension, mainTypeBox, model, items);
            }, uiAccess);

        return mainTypeBox;
    }

    @RequiredUIAccess
    private static void applyMainTypes(
        DotNetMutableModuleExtension<?> extension,
        ComboBox<DotNetMainTypeItem> mainTypeBox,
        MutableFlatDataModel<DotNetMainTypeItem> model,
        List<DotNetMainTypeItem> items
    ) {
        String mainType = extension.getMainType();

        List<DotNetMainTypeItem> newItems = new ArrayList<>(items);
        DotNetMainTypeItem selected = DotNetMainTypeItem.NONE;
        if (mainType != null) {
            selected = null;
            for (DotNetMainTypeItem newItem : newItems) {
                if (Objects.equals(mainType, newItem.qualifiedName())) {
                    selected = newItem;
                    break;
                }
            }

            if (selected == null) {
                selected = new DotNetMainTypeItem(mainType, null, false);
                newItems.add(selected);
            }
        }

        model.replaceAll(newItems);
        mainTypeBox.setValue(selected, false);
        mainTypeBox.setEnabled(true);
    }

    @RequiredReadAction
    private static List<DotNetMainTypeItem> collectMainTypes(
        DotNetMutableModuleExtension<?> extension,
        DotNetElementQualifierProducer qualifierProducer
    ) {
        List<DotNetMainTypeItem> items = new ArrayList<>();
        items.add(DotNetMainTypeItem.NONE);
        for (PsiElement element : extension.getEntryPointElements()) {
            if (!qualifierProducer.isMyElement(element)) {
                continue;
            }

            String qualifiedName = qualifierProducer.getQualifiedName(element);
            if (qualifiedName != null) {
                items.add(new DotNetMainTypeItem(qualifiedName, IconDescriptorUpdaters.getIcon(element, 0), true));
            }
        }
        return items;
    }
}
