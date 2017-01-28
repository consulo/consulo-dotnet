// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:LIBElemType interface.
 *
 * @author VISTALL
 */
public interface LIB extends DomElement, Task
{

	/**
	 * Returns the value of the AcceptableNonZeroExitCodes child.
	 *
	 * @return the value of the AcceptableNonZeroExitCodes child.
	 */
	@NotNull
	GenericAttributeValue<String> getAcceptableNonZeroExitCodes();


	/**
	 * Returns the value of the ActiveToolSwitchesValues child.
	 *
	 * @return the value of the ActiveToolSwitchesValues child.
	 */
	@NotNull
	GenericAttributeValue<String> getActiveToolSwitchesValues();


	/**
	 * Returns the value of the AdditionalDependencies child.
	 *
	 * @return the value of the AdditionalDependencies child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalDependencies();


	/**
	 * Returns the value of the AdditionalLibraryDirectories child.
	 *
	 * @return the value of the AdditionalLibraryDirectories child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalLibraryDirectories();


	/**
	 * Returns the value of the AdditionalOptions child.
	 *
	 * @return the value of the AdditionalOptions child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalOptions();


	/**
	 * Returns the value of the DisplayLibrary child.
	 *
	 * @return the value of the DisplayLibrary child.
	 */
	@NotNull
	GenericAttributeValue<String> getDisplayLibrary();


	/**
	 * Returns the value of the EnvironmentVariables child.
	 *
	 * @return the value of the EnvironmentVariables child.
	 */
	@NotNull
	GenericAttributeValue<String> getEnvironmentVariables();


	/**
	 * Returns the value of the ErrorReporting child.
	 *
	 * @return the value of the ErrorReporting child.
	 */
	@NotNull
	GenericAttributeValue<String> getErrorReporting();


	/**
	 * Returns the value of the ExcludedInputPaths child.
	 *
	 * @return the value of the ExcludedInputPaths child.
	 */
	@NotNull
	GenericAttributeValue<String> getExcludedInputPaths();


	/**
	 * Returns the value of the ExportNamedFunctions child.
	 *
	 * @return the value of the ExportNamedFunctions child.
	 */
	@NotNull
	GenericAttributeValue<String> getExportNamedFunctions();


	/**
	 * Returns the value of the ForceSymbolReferences child.
	 *
	 * @return the value of the ForceSymbolReferences child.
	 */
	@NotNull
	GenericAttributeValue<String> getForceSymbolReferences();


	/**
	 * Returns the value of the IgnoreAllDefaultLibraries child.
	 *
	 * @return the value of the IgnoreAllDefaultLibraries child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getIgnoreAllDefaultLibraries();


	/**
	 * Returns the value of the IgnoreSpecificDefaultLibraries child.
	 *
	 * @return the value of the IgnoreSpecificDefaultLibraries child.
	 */
	@NotNull
	GenericAttributeValue<String> getIgnoreSpecificDefaultLibraries();


	/**
	 * Returns the value of the LinkLibraryDependencies child.
	 *
	 * @return the value of the LinkLibraryDependencies child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLinkLibraryDependencies();


	/**
	 * Returns the value of the LinkTimeCodeGeneration child.
	 *
	 * @return the value of the LinkTimeCodeGeneration child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLinkTimeCodeGeneration();


	/**
	 * Returns the value of the LogStandardErrorAsError child.
	 *
	 * @return the value of the LogStandardErrorAsError child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLogStandardErrorAsError();


	/**
	 * Returns the value of the MinimalRebuildFromTracking child.
	 *
	 * @return the value of the MinimalRebuildFromTracking child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getMinimalRebuildFromTracking();


	/**
	 * Returns the value of the MinimumRequiredVersion child.
	 *
	 * @return the value of the MinimumRequiredVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getMinimumRequiredVersion();


	/**
	 * Returns the value of the ModuleDefinitionFile child.
	 *
	 * @return the value of the ModuleDefinitionFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getModuleDefinitionFile();


	/**
	 * Returns the value of the OutputFile child.
	 *
	 * @return the value of the OutputFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getOutputFile();


	/**
	 * Returns the value of the PathOverride child.
	 *
	 * @return the value of the PathOverride child.
	 */
	@NotNull
	GenericAttributeValue<String> getPathOverride();


	/**
	 * Returns the value of the RemoveObjects child.
	 *
	 * @return the value of the RemoveObjects child.
	 */
	@NotNull
	GenericAttributeValue<String> getRemoveObjects();


	/**
	 * Returns the value of the SkippedExecution child.
	 *
	 * @return the value of the SkippedExecution child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSkippedExecution();


	/**
	 * Returns the value of the Sources child.
	 *
	 * @return the value of the Sources child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getSources();


	/**
	 * Returns the value of the SourcesCompiled child.
	 *
	 * @return the value of the SourcesCompiled child.
	 */
	@NotNull
	GenericAttributeValue<String> getSourcesCompiled();


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
	 * Returns the value of the SubSystem child.
	 *
	 * @return the value of the SubSystem child.
	 */
	@NotNull
	GenericAttributeValue<String> getSubSystem();


	/**
	 * Returns the value of the SuppressStartupBanner child.
	 *
	 * @return the value of the SuppressStartupBanner child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSuppressStartupBanner();


	/**
	 * Returns the value of the TargetMachine child.
	 *
	 * @return the value of the TargetMachine child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetMachine();


	/**
	 * Returns the value of the Timeout child.
	 *
	 * @return the value of the Timeout child.
	 */
	@NotNull
	GenericAttributeValue<String> getTimeout();


	/**
	 * Returns the value of the TLogReadFiles child.
	 *
	 * @return the value of the TLogReadFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getTLogReadFiles();


	/**
	 * Returns the value of the TLogWriteFiles child.
	 *
	 * @return the value of the TLogWriteFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getTLogWriteFiles();


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
	 * Returns the value of the TrackedInputFilesToIgnore child.
	 *
	 * @return the value of the TrackedInputFilesToIgnore child.
	 */
	@NotNull
	GenericAttributeValue<String> getTrackedInputFilesToIgnore();


	/**
	 * Returns the value of the TrackedOutputFilesToIgnore child.
	 *
	 * @return the value of the TrackedOutputFilesToIgnore child.
	 */
	@NotNull
	GenericAttributeValue<String> getTrackedOutputFilesToIgnore();


	/**
	 * Returns the value of the TrackerLogDirectory child.
	 *
	 * @return the value of the TrackerLogDirectory child.
	 */
	@NotNull
	GenericAttributeValue<String> getTrackerLogDirectory();


	/**
	 * Returns the value of the TrackFileAccess child.
	 *
	 * @return the value of the TrackFileAccess child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTrackFileAccess();


	/**
	 * Returns the value of the TreatLibWarningAsErrors child.
	 *
	 * @return the value of the TreatLibWarningAsErrors child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTreatLibWarningAsErrors();


	/**
	 * Returns the value of the UseUnicodeResponseFiles child.
	 *
	 * @return the value of the UseUnicodeResponseFiles child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUseUnicodeResponseFiles();


	/**
	 * Returns the value of the Verbose child.
	 *
	 * @return the value of the Verbose child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getVerbose();


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
