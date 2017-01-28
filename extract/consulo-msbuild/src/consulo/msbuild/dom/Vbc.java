// Generated on Sat Jan 28 04:58:20 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:VbcElemType interface.
 *
 * @author VISTALL
 */
public interface Vbc extends DomElement, Task
{

	/**
	 * Returns the value of the AdditionalLibPaths child.
	 *
	 * @return the value of the AdditionalLibPaths child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalLibPaths();


	/**
	 * Returns the value of the AddModules child.
	 *
	 * @return the value of the AddModules child.
	 */
	@NotNull
	GenericAttributeValue<String> getAddModules();


	/**
	 * Returns the value of the BaseAddress child.
	 *
	 * @return the value of the BaseAddress child.
	 */
	@NotNull
	GenericAttributeValue<String> getBaseAddress();


	/**
	 * Returns the value of the CodePage child.
	 *
	 * @return the value of the CodePage child.
	 */
	@NotNull
	GenericAttributeValue<String> getCodePage();


	/**
	 * Returns the value of the DebugType child.
	 *
	 * @return the value of the DebugType child.
	 */
	@NotNull
	GenericAttributeValue<String> getDebugType();


	/**
	 * Returns the value of the DefineConstants child.
	 *
	 * @return the value of the DefineConstants child.
	 */
	@NotNull
	GenericAttributeValue<String> getDefineConstants();


	/**
	 * Returns the value of the DelaySign child.
	 *
	 * @return the value of the DelaySign child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getDelaySign();


	/**
	 * Returns the value of the DisabledWarnings child.
	 *
	 * @return the value of the DisabledWarnings child.
	 */
	@NotNull
	GenericAttributeValue<String> getDisabledWarnings();


	/**
	 * Returns the value of the DocumentationFile child.
	 *
	 * @return the value of the DocumentationFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getDocumentationFile();


	/**
	 * Returns the value of the EmitDebugInformation child.
	 *
	 * @return the value of the EmitDebugInformation child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getEmitDebugInformation();


	/**
	 * Returns the value of the EnvironmentVariables child.
	 *
	 * @return the value of the EnvironmentVariables child.
	 */
	@NotNull
	GenericAttributeValue<String> getEnvironmentVariables();


	/**
	 * Returns the value of the ErrorReport child.
	 *
	 * @return the value of the ErrorReport child.
	 */
	@NotNull
	GenericAttributeValue<String> getErrorReport();


	/**
	 * Returns the value of the FileAlignment child.
	 *
	 * @return the value of the FileAlignment child.
	 */
	@NotNull
	GenericAttributeValue<String> getFileAlignment();


	/**
	 * Returns the value of the GenerateDocumentation child.
	 *
	 * @return the value of the GenerateDocumentation child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getGenerateDocumentation();


	/**
	 * Returns the value of the Imports child.
	 *
	 * @return the value of the Imports child.
	 */
	@NotNull
	GenericAttributeValue<String> getImports();


	/**
	 * Returns the value of the KeyContainer child.
	 *
	 * @return the value of the KeyContainer child.
	 */
	@NotNull
	GenericAttributeValue<String> getKeyContainer();


	/**
	 * Returns the value of the KeyFile child.
	 *
	 * @return the value of the KeyFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getKeyFile();


	/**
	 * Returns the value of the LangVersion child.
	 *
	 * @return the value of the LangVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getLangVersion();


	/**
	 * Returns the value of the VBRuntime child.
	 *
	 * @return the value of the VBRuntime child.
	 */
	@NotNull
	GenericAttributeValue<String> getVBRuntime();


	/**
	 * Returns the value of the LinkResources child.
	 *
	 * @return the value of the LinkResources child.
	 */
	@NotNull
	GenericAttributeValue<String> getLinkResources();


	/**
	 * Returns the value of the LogStandardErrorAsError child.
	 *
	 * @return the value of the LogStandardErrorAsError child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLogStandardErrorAsError();


	/**
	 * Returns the value of the MainEntryPoint child.
	 *
	 * @return the value of the MainEntryPoint child.
	 */
	@NotNull
	GenericAttributeValue<String> getMainEntryPoint();


	/**
	 * Returns the value of the ModuleAssemblyName child.
	 *
	 * @return the value of the ModuleAssemblyName child.
	 */
	@NotNull
	GenericAttributeValue<String> getModuleAssemblyName();


	/**
	 * Returns the value of the NoConfig child.
	 *
	 * @return the value of the NoConfig child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getNoConfig();


	/**
	 * Returns the value of the NoLogo child.
	 *
	 * @return the value of the NoLogo child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getNoLogo();


	/**
	 * Returns the value of the NoStandardLib child.
	 *
	 * @return the value of the NoStandardLib child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getNoStandardLib();


	/**
	 * Returns the value of the NoVBRuntimeReference child.
	 *
	 * @return the value of the NoVBRuntimeReference child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getNoVBRuntimeReference();


	/**
	 * Returns the value of the NoWarnings child.
	 *
	 * @return the value of the NoWarnings child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getNoWarnings();


	/**
	 * Returns the value of the NoWin32Manifest child.
	 *
	 * @return the value of the NoWin32Manifest child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getNoWin32Manifest();


	/**
	 * Returns the value of the Optimize child.
	 *
	 * @return the value of the Optimize child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOptimize();


	/**
	 * Returns the value of the OptionCompare child.
	 *
	 * @return the value of the OptionCompare child.
	 */
	@NotNull
	GenericAttributeValue<String> getOptionCompare();


	/**
	 * Returns the value of the OptionExplicit child.
	 *
	 * @return the value of the OptionExplicit child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOptionExplicit();


	/**
	 * Returns the value of the OptionInfer child.
	 *
	 * @return the value of the OptionInfer child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOptionInfer();


	/**
	 * Returns the value of the OptionStrict child.
	 *
	 * @return the value of the OptionStrict child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOptionStrict();


	/**
	 * Returns the value of the OptionStrictType child.
	 *
	 * @return the value of the OptionStrictType child.
	 */
	@NotNull
	GenericAttributeValue<String> getOptionStrictType();


	/**
	 * Returns the value of the OutputAssembly child.
	 *
	 * @return the value of the OutputAssembly child.
	 */
	@NotNull
	GenericAttributeValue<String> getOutputAssembly();


	/**
	 * Returns the value of the Platform child.
	 *
	 * @return the value of the Platform child.
	 */
	@NotNull
	GenericAttributeValue<String> getPlatform();


	/**
	 * Returns the value of the References child.
	 *
	 * @return the value of the References child.
	 */
	@NotNull
	GenericAttributeValue<String> getReferences();


	/**
	 * Returns the value of the RemoveIntegerChecks child.
	 *
	 * @return the value of the RemoveIntegerChecks child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getRemoveIntegerChecks();


	/**
	 * Returns the value of the Resources child.
	 *
	 * @return the value of the Resources child.
	 */
	@NotNull
	GenericAttributeValue<String> getResources();


	/**
	 * Returns the value of the ResponseFiles child.
	 *
	 * @return the value of the ResponseFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getResponseFiles();


	/**
	 * Returns the value of the RootNamespace child.
	 *
	 * @return the value of the RootNamespace child.
	 */
	@NotNull
	GenericAttributeValue<String> getRootNamespace();


	/**
	 * Returns the value of the SdkPath child.
	 *
	 * @return the value of the SdkPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getSdkPath();


	/**
	 * Returns the value of the Sources child.
	 *
	 * @return the value of the Sources child.
	 */
	@NotNull
	GenericAttributeValue<String> getSources();


	/**
	 * Returns the value of the StandardErrorImportance child.
	 *
	 * @return the value of the StandardErrorImportance child.
	 */
	@NotNull
	GenericAttributeValue<String> getStandardErrorImportance();


	/**
	 * Returns the value of the StandardOutputImportance child.
	 *
	 * @return the value of the StandardOutputImportance child.
	 */
	@NotNull
	GenericAttributeValue<String> getStandardOutputImportance();


	/**
	 * Returns the value of the TargetCompactFramework child.
	 *
	 * @return the value of the TargetCompactFramework child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTargetCompactFramework();


	/**
	 * Returns the value of the TargetType child.
	 *
	 * @return the value of the TargetType child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetType();


	/**
	 * Returns the value of the Timeout child.
	 *
	 * @return the value of the Timeout child.
	 */
	@NotNull
	GenericAttributeValue<String> getTimeout();


	/**
	 * Returns the value of the ToolExe child.
	 *
	 * @return the value of the ToolExe child.
	 */
	@NotNull
	GenericAttributeValue<String> getToolExe();


	/**
	 * Returns the value of the ToolPath child.
	 *
	 * @return the value of the ToolPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getToolPath();


	/**
	 * Returns the value of the TreatWarningsAsErrors child.
	 *
	 * @return the value of the TreatWarningsAsErrors child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTreatWarningsAsErrors();


	/**
	 * Returns the value of the UseHostCompilerIfAvailable child.
	 *
	 * @return the value of the UseHostCompilerIfAvailable child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUseHostCompilerIfAvailable();


	/**
	 * Returns the value of the Utf8Output child.
	 *
	 * @return the value of the Utf8Output child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUtf8Output();


	/**
	 * Returns the value of the Verbosity child.
	 *
	 * @return the value of the Verbosity child.
	 */
	@NotNull
	GenericAttributeValue<String> getVerbosity();


	/**
	 * Returns the value of the WarningsAsErrors child.
	 *
	 * @return the value of the WarningsAsErrors child.
	 */
	@NotNull
	GenericAttributeValue<String> getWarningsAsErrors();


	/**
	 * Returns the value of the WarningsNotAsErrors child.
	 *
	 * @return the value of the WarningsNotAsErrors child.
	 */
	@NotNull
	GenericAttributeValue<String> getWarningsNotAsErrors();


	/**
	 * Returns the value of the Win32Icon child.
	 *
	 * @return the value of the Win32Icon child.
	 */
	@NotNull
	GenericAttributeValue<String> getWin32Icon();


	/**
	 * Returns the value of the Win32Manifest child.
	 *
	 * @return the value of the Win32Manifest child.
	 */
	@NotNull
	GenericAttributeValue<String> getWin32Manifest();


	/**
	 * Returns the value of the Win32Resource child.
	 *
	 * @return the value of the Win32Resource child.
	 */
	@NotNull
	GenericAttributeValue<String> getWin32Resource();


	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="TaskType_Condition" _locComment="" -->Optional expression evaluated to determine whether the task should be executed
	 * </pre>
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	GenericAttributeValue<String> getCondition();


	/**
	 * Returns the value of the ContinueOnError child.
	 * <pre>
	 * <h3>Attribute null:ContinueOnError documentation</h3>
	 * <!-- _locID_text="TaskType_ContinueOnError" _locComment="" -->Optional boolean indicating whether a recoverable task error should be ignored. Default false
	 * </pre>
	 *
	 * @return the value of the ContinueOnError child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getContinueOnError();


	/**
	 * Returns the value of the Architecture child.
	 * <pre>
	 * <h3>Attribute null:Architecture documentation</h3>
	 * <!-- _locID_text="TaskType_Architecture" _locComment="" -->Defines the bitness of the task if it must be run specifically in a 32bit or 64bit process. If not specified, it will run with the
	 * bitness of the build process.  If there are multiple tasks defined in UsingTask with the same name but with different Architecture attribute values, the value of the Architecture attribute
	 * specified here will be used to match and select the correct task
	 * </pre>
	 *
	 * @return the value of the Architecture child.
	 */
	@NotNull
	GenericAttributeValue<String> getArchitecture();


	/**
	 * Returns the value of the Runtime child.
	 * <pre>
	 * <h3>Attribute null:Runtime documentation</h3>
	 * <!-- _locID_text="TaskType_Runtime" _locComment="" -->Defines the .NET runtime of the task. This must be specified if the task must run on a specific version of the .NET runtime. If not
	 * specified, the task will run on the runtime being used by the build process. If there are multiple tasks defined in UsingTask with the same name but with different Runtime attribute values,
	 * the value of the Runtime attribute specified here will be used to match and select the correct task
	 * </pre>
	 *
	 * @return the value of the Runtime child.
	 */
	@NotNull
	GenericAttributeValue<String> getRuntime();


	/**
	 * Returns the list of Output children.
	 * <pre>
	 * <h3>Element http://schemas.microsoft.com/developer/msbuild/2003:Output documentation</h3>
	 * <!-- _locID_text="TaskType_Output" _locComment="" -->Optional element specifying a specific task output to be gathered
	 * </pre>
	 *
	 * @return the list of Output children.
	 */
	@NotNull
	List<Output> getOutputs();

	/**
	 * Adds new child to the list of Output children.
	 *
	 * @return created child
	 */
	Output addOutput();


}
