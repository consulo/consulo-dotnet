// Generated on Sat Jan 28 04:58:18 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:CLElemType interface.
 *
 * @author VISTALL
 */
public interface CL extends DomElement, Task
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
	 * Returns the value of the AdditionalIncludeDirectories child.
	 *
	 * @return the value of the AdditionalIncludeDirectories child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalIncludeDirectories();


	/**
	 * Returns the value of the AdditionalOptions child.
	 *
	 * @return the value of the AdditionalOptions child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalOptions();


	/**
	 * Returns the value of the AdditionalUsingDirectories child.
	 *
	 * @return the value of the AdditionalUsingDirectories child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalUsingDirectories();


	/**
	 * Returns the value of the AssemblerListingLocation child.
	 *
	 * @return the value of the AssemblerListingLocation child.
	 */
	@NotNull
	GenericAttributeValue<String> getAssemblerListingLocation();


	/**
	 * Returns the value of the AssemblerOutput child.
	 *
	 * @return the value of the AssemblerOutput child.
	 */
	@NotNull
	GenericAttributeValue<String> getAssemblerOutput();


	/**
	 * Returns the value of the BasicRuntimeChecks child.
	 *
	 * @return the value of the BasicRuntimeChecks child.
	 */
	@NotNull
	GenericAttributeValue<String> getBasicRuntimeChecks();


	/**
	 * Returns the value of the BrowseInformation child.
	 *
	 * @return the value of the BrowseInformation child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getBrowseInformation();


	/**
	 * Returns the value of the BrowseInformationFile child.
	 *
	 * @return the value of the BrowseInformationFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getBrowseInformationFile();


	/**
	 * Returns the value of the BufferSecurityCheck child.
	 *
	 * @return the value of the BufferSecurityCheck child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getBufferSecurityCheck();


	/**
	 * Returns the value of the CallingConvention child.
	 *
	 * @return the value of the CallingConvention child.
	 */
	@NotNull
	GenericAttributeValue<String> getCallingConvention();


	/**
	 * Returns the value of the CompileAs child.
	 *
	 * @return the value of the CompileAs child.
	 */
	@NotNull
	GenericAttributeValue<String> getCompileAs();


	/**
	 * Returns the value of the CompileAsManaged child.
	 *
	 * @return the value of the CompileAsManaged child.
	 */
	@NotNull
	GenericAttributeValue<String> getCompileAsManaged();


	/**
	 * Returns the value of the CreateHotpatchableImage child.
	 *
	 * @return the value of the CreateHotpatchableImage child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getCreateHotpatchableImage();


	/**
	 * Returns the value of the DebugInformationFormat child.
	 *
	 * @return the value of the DebugInformationFormat child.
	 */
	@NotNull
	GenericAttributeValue<String> getDebugInformationFormat();


	/**
	 * Returns the value of the DisableLanguageExtensions child.
	 *
	 * @return the value of the DisableLanguageExtensions child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getDisableLanguageExtensions();


	/**
	 * Returns the value of the DisableSpecificWarnings child.
	 *
	 * @return the value of the DisableSpecificWarnings child.
	 */
	@NotNull
	GenericAttributeValue<String> getDisableSpecificWarnings();


	/**
	 * Returns the value of the EnableEnhancedInstructionSet child.
	 *
	 * @return the value of the EnableEnhancedInstructionSet child.
	 */
	@NotNull
	GenericAttributeValue<String> getEnableEnhancedInstructionSet();


	/**
	 * Returns the value of the EnableFiberSafeOptimizations child.
	 *
	 * @return the value of the EnableFiberSafeOptimizations child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getEnableFiberSafeOptimizations();


	/**
	 * Returns the value of the EnablePREfast child.
	 *
	 * @return the value of the EnablePREfast child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getEnablePREfast();


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
	 * Returns the value of the ExceptionHandling child.
	 *
	 * @return the value of the ExceptionHandling child.
	 */
	@NotNull
	GenericAttributeValue<String> getExceptionHandling();


	/**
	 * Returns the value of the ExcludedInputPaths child.
	 *
	 * @return the value of the ExcludedInputPaths child.
	 */
	@NotNull
	GenericAttributeValue<String> getExcludedInputPaths();


	/**
	 * Returns the value of the ExpandAttributedSource child.
	 *
	 * @return the value of the ExpandAttributedSource child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getExpandAttributedSource();


	/**
	 * Returns the value of the FavorSizeOrSpeed child.
	 *
	 * @return the value of the FavorSizeOrSpeed child.
	 */
	@NotNull
	GenericAttributeValue<String> getFavorSizeOrSpeed();


	/**
	 * Returns the value of the FloatingPointExceptions child.
	 *
	 * @return the value of the FloatingPointExceptions child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getFloatingPointExceptions();


	/**
	 * Returns the value of the FloatingPointModel child.
	 *
	 * @return the value of the FloatingPointModel child.
	 */
	@NotNull
	GenericAttributeValue<String> getFloatingPointModel();


	/**
	 * Returns the value of the ForceConformanceInForLoopScope child.
	 *
	 * @return the value of the ForceConformanceInForLoopScope child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getForceConformanceInForLoopScope();


	/**
	 * Returns the value of the ForcedIncludeFiles child.
	 *
	 * @return the value of the ForcedIncludeFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getForcedIncludeFiles();


	/**
	 * Returns the value of the ForcedUsingFiles child.
	 *
	 * @return the value of the ForcedUsingFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getForcedUsingFiles();


	/**
	 * Returns the value of the FunctionLevelLinking child.
	 *
	 * @return the value of the FunctionLevelLinking child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getFunctionLevelLinking();


	/**
	 * Returns the value of the GenerateXMLDocumentationFiles child.
	 *
	 * @return the value of the GenerateXMLDocumentationFiles child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getGenerateXMLDocumentationFiles();


	/**
	 * Returns the value of the IgnoreStandardIncludePath child.
	 *
	 * @return the value of the IgnoreStandardIncludePath child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getIgnoreStandardIncludePath();


	/**
	 * Returns the value of the InlineFunctionExpansion child.
	 *
	 * @return the value of the InlineFunctionExpansion child.
	 */
	@NotNull
	GenericAttributeValue<String> getInlineFunctionExpansion();


	/**
	 * Returns the value of the IntrinsicFunctions child.
	 *
	 * @return the value of the IntrinsicFunctions child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getIntrinsicFunctions();


	/**
	 * Returns the value of the LogStandardErrorAsError child.
	 *
	 * @return the value of the LogStandardErrorAsError child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLogStandardErrorAsError();


	/**
	 * Returns the value of the MinimalRebuild child.
	 *
	 * @return the value of the MinimalRebuild child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getMinimalRebuild();


	/**
	 * Returns the value of the MinimalRebuildFromTracking child.
	 *
	 * @return the value of the MinimalRebuildFromTracking child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getMinimalRebuildFromTracking();


	/**
	 * Returns the value of the MultiProcessorCompilation child.
	 *
	 * @return the value of the MultiProcessorCompilation child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getMultiProcessorCompilation();


	/**
	 * Returns the value of the ObjectFileName child.
	 *
	 * @return the value of the ObjectFileName child.
	 */
	@NotNull
	GenericAttributeValue<String> getObjectFileName();


	/**
	 * Returns the value of the ObjectFiles child.
	 *
	 * @return the value of the ObjectFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getObjectFiles();


	/**
	 * Returns the value of the OmitDefaultLibName child.
	 *
	 * @return the value of the OmitDefaultLibName child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOmitDefaultLibName();


	/**
	 * Returns the value of the OmitFramePointers child.
	 *
	 * @return the value of the OmitFramePointers child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOmitFramePointers();


	/**
	 * Returns the value of the OpenMPSupport child.
	 *
	 * @return the value of the OpenMPSupport child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOpenMPSupport();


	/**
	 * Returns the value of the Optimization child.
	 *
	 * @return the value of the Optimization child.
	 */
	@NotNull
	GenericAttributeValue<String> getOptimization();


	/**
	 * Returns the value of the PathOverride child.
	 *
	 * @return the value of the PathOverride child.
	 */
	@NotNull
	GenericAttributeValue<String> getPathOverride();


	/**
	 * Returns the value of the PrecompiledHeader child.
	 *
	 * @return the value of the PrecompiledHeader child.
	 */
	@NotNull
	GenericAttributeValue<String> getPrecompiledHeader();


	/**
	 * Returns the value of the PrecompiledHeaderFile child.
	 *
	 * @return the value of the PrecompiledHeaderFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getPrecompiledHeaderFile();


	/**
	 * Returns the value of the PrecompiledHeaderOutputFile child.
	 *
	 * @return the value of the PrecompiledHeaderOutputFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getPrecompiledHeaderOutputFile();


	/**
	 * Returns the value of the PreprocessKeepComments child.
	 *
	 * @return the value of the PreprocessKeepComments child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getPreprocessKeepComments();


	/**
	 * Returns the value of the PreprocessorDefinitions child.
	 *
	 * @return the value of the PreprocessorDefinitions child.
	 */
	@NotNull
	GenericAttributeValue<String> getPreprocessorDefinitions();


	/**
	 * Returns the value of the PreprocessOutput child.
	 *
	 * @return the value of the PreprocessOutput child.
	 */
	@NotNull
	GenericAttributeValue<String> getPreprocessOutput();


	/**
	 * Returns the value of the PreprocessSuppressLineNumbers child.
	 *
	 * @return the value of the PreprocessSuppressLineNumbers child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getPreprocessSuppressLineNumbers();


	/**
	 * Returns the value of the PreprocessToFile child.
	 *
	 * @return the value of the PreprocessToFile child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getPreprocessToFile();


	/**
	 * Returns the value of the ProcessorNumber child.
	 *
	 * @return the value of the ProcessorNumber child.
	 */
	@NotNull
	GenericAttributeValue<String> getProcessorNumber();


	/**
	 * Returns the value of the ProgramDataBaseFileName child.
	 *
	 * @return the value of the ProgramDataBaseFileName child.
	 */
	@NotNull
	GenericAttributeValue<String> getProgramDataBaseFileName();


	/**
	 * Returns the value of the RuntimeLibrary child.
	 *
	 * @return the value of the RuntimeLibrary child.
	 */
	@NotNull
	GenericAttributeValue<String> getRuntimeLibrary();


	/**
	 * Returns the value of the RuntimeTypeInfo child.
	 *
	 * @return the value of the RuntimeTypeInfo child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getRuntimeTypeInfo();


	/**
	 * Returns the value of the ShowIncludes child.
	 *
	 * @return the value of the ShowIncludes child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getShowIncludes();


	/**
	 * Returns the value of the SkippedExecution child.
	 *
	 * @return the value of the SkippedExecution child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSkippedExecution();


	/**
	 * Returns the value of the SmallerTypeCheck child.
	 *
	 * @return the value of the SmallerTypeCheck child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSmallerTypeCheck();


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
	 * Returns the value of the StringPooling child.
	 *
	 * @return the value of the StringPooling child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getStringPooling();


	/**
	 * Returns the value of the StructMemberAlignment child.
	 *
	 * @return the value of the StructMemberAlignment child.
	 */
	@NotNull
	GenericAttributeValue<String> getStructMemberAlignment();


	/**
	 * Returns the value of the SuppressStartupBanner child.
	 *
	 * @return the value of the SuppressStartupBanner child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSuppressStartupBanner();


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
	 * Returns the value of the TreatSpecificWarningsAsErrors child.
	 *
	 * @return the value of the TreatSpecificWarningsAsErrors child.
	 */
	@NotNull
	GenericAttributeValue<String> getTreatSpecificWarningsAsErrors();


	/**
	 * Returns the value of the TreatWarningAsError child.
	 *
	 * @return the value of the TreatWarningAsError child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTreatWarningAsError();


	/**
	 * Returns the value of the TreatWChar_tAsBuiltInType child.
	 *
	 * @return the value of the TreatWChar_tAsBuiltInType child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTreatWChar_tAsBuiltInType();


	/**
	 * Returns the value of the UndefineAllPreprocessorDefinitions child.
	 *
	 * @return the value of the UndefineAllPreprocessorDefinitions child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUndefineAllPreprocessorDefinitions();


	/**
	 * Returns the value of the UndefinePreprocessorDefinitions child.
	 *
	 * @return the value of the UndefinePreprocessorDefinitions child.
	 */
	@NotNull
	GenericAttributeValue<String> getUndefinePreprocessorDefinitions();


	/**
	 * Returns the value of the UseFullPaths child.
	 *
	 * @return the value of the UseFullPaths child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUseFullPaths();


	/**
	 * Returns the value of the UseUnicodeForAssemblerListing child.
	 *
	 * @return the value of the UseUnicodeForAssemblerListing child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUseUnicodeForAssemblerListing();


	/**
	 * Returns the value of the WarningLevel child.
	 *
	 * @return the value of the WarningLevel child.
	 */
	@NotNull
	GenericAttributeValue<String> getWarningLevel();


	/**
	 * Returns the value of the WholeProgramOptimization child.
	 *
	 * @return the value of the WholeProgramOptimization child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getWholeProgramOptimization();


	/**
	 * Returns the value of the XMLDocumentationFileName child.
	 *
	 * @return the value of the XMLDocumentationFileName child.
	 */
	@NotNull
	GenericAttributeValue<String> getXMLDocumentationFileName();


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
