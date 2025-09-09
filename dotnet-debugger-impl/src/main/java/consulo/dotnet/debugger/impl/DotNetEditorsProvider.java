package consulo.dotnet.debugger.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.debugger.DotNetDebuggerProvider;
import consulo.dotnet.debugger.DotNetDebuggerProviders;
import consulo.dotnet.module.extension.DotNetModuleLangExtension;
import consulo.execution.configuration.ModuleRunConfiguration;
import consulo.execution.configuration.RunProfile;
import consulo.execution.debug.XDebugSession;
import consulo.execution.debug.XSourcePosition;
import consulo.execution.debug.evaluation.XDebuggerEditorsProviderBase;
import consulo.language.Language;
import consulo.language.file.LanguageFileType;
import consulo.language.plain.PlainTextFileType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiFileFactory;
import consulo.language.psi.PsiManager;
import consulo.module.Module;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author VISTALL
 * @since 06.06.14
 */
public class DotNetEditorsProvider extends XDebuggerEditorsProviderBase {
    private final XDebugSession mySession;
    private final LanguageFileType myLanguageFileType;

    public DotNetEditorsProvider(@Nonnull LanguageFileType languageFileType) {
        myLanguageFileType = languageFileType;
        mySession = null;
    }

    public DotNetEditorsProvider(@Nullable XDebugSession session, @Nullable RunProfile runProfile) {
        mySession = session;
        myLanguageFileType = findLanguageFileType(runProfile);
    }

    @Nonnull
    private static LanguageFileType findLanguageFileType(@Nullable RunProfile runProfile) {
        if (runProfile instanceof ModuleRunConfiguration moduleRunConfiguration) {
            Module[] modules = moduleRunConfiguration.getModules();
            for (Module module : modules) {
                DotNetModuleLangExtension extension = module.getExtension(DotNetModuleLangExtension.class);
                if (extension != null) {
                    return extension.getFileType();
                }
            }
        }

        if (runProfile instanceof DotNetConfurationWithDefaultDebugFileType confurationWithDefaultDebugFileType) {
            return confurationWithDefaultDebugFileType.getDefaultDebuggerFileType();
        }

        return PlainTextFileType.INSTANCE;
    }

    @Override
    @RequiredReadAction
    protected PsiFile createExpressionCodeFragment(@Nonnull Project project, @Nonnull String text, @Nullable PsiElement context, boolean isPhysical) {
        if (context == null) {
            XDebugSession session = mySession;
            XSourcePosition currentPosition = session == null ? null : session.getCurrentPosition();
            if (currentPosition != null) {
                VirtualFile file = currentPosition.getFile();
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                if (psiFile != null) {
                    context = psiFile.findElementAt(currentPosition.getOffset());
                }
            }
        }

        DotNetDebuggerProvider debuggerProvider = context == null ? null : DotNetDebuggerProviders.findByPsiFile(context.getContainingFile());
        if (debuggerProvider == null) {
            debuggerProvider = DotNetDebuggerProvider.getProvider(myLanguageFileType.getLanguage());
        }

        if (debuggerProvider == null) {
            return PsiFileFactory.getInstance(project).createFileFromText("test." + myLanguageFileType.getDefaultExtension(), myLanguageFileType, text);
        }

        return debuggerProvider.createExpressionCodeFragment(project, context, text, isPhysical);
    }

    @Override
    @Nonnull
    @RequiredReadAction
    public Collection<Language> getSupportedLanguages(@Nonnull Project project, @Nullable XSourcePosition sourcePosition) {
        if (sourcePosition == null) {
            return List.of(myLanguageFileType.getLanguage());
        }

        DotNetDebuggerProvider provider = DotNetDebuggerProviders.findByVirtualFile(project, sourcePosition.getFile());
        if (provider == null) {
            return List.of(myLanguageFileType.getLanguage());
        }
        return List.of(provider.getEditorLanguage());
    }

    @Nonnull
    @Override
    public FileType getFileType() {
        return myLanguageFileType;
    }
}
