// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:LinkElemType interface.
 *
 * @author VISTALL
 */
public interface Link extends DomElement, Task
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
	 * Returns the value of the AdditionalManifestDependencies child.
	 *
	 * @return the value of the AdditionalManifestDependencies child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalManifestDependencies();


	/**
	 * Returns the value of the AdditionalOptions child.
	 *
	 * @return the value of the AdditionalOptions child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalOptions();


	/**
	 * Returns the value of the AddModuleNamesToAssembly child.
	 *
	 * @return the value of the AddModuleNamesToAssembly child.
	 */
	@NotNull
	GenericAttributeValue<String> getAddModuleNamesToAssembly();


	/**
	 * Returns the value of the AllowIsolation child.
	 *
	 * @return the value of the AllowIsolation child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getAllowIsolation();


	/**
	 * Returns the value of the AssemblyDebug child.
	 *
	 * @return the value of the AssemblyDebug child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getAssemblyDebug();


	/**
	 * Returns the value of the AssemblyLinkResource child.
	 *
	 * @return the value of the AssemblyLinkResource child.
	 */
	@NotNull
	GenericAttributeValue<String> getAssemblyLinkResource();


	/**
	 * Returns the value of the BaseAddress child.
	 *
	 * @return the value of the BaseAddress child.
	 */
	@NotNull
	GenericAttributeValue<String> getBaseAddress();


	/**
	 * Returns the value of the CLRImageType child.
	 *
	 * @return the value of the CLRImageType child.
	 */
	@NotNull
	GenericAttributeValue<String> getCLRImageType();


	/**
	 * Returns the value of the CLRSupportLastError child.
	 *
	 * @return the value of the CLRSupportLastError child.
	 */
	@NotNull
	GenericAttributeValue<String> getCLRSupportLastError();


	/**
	 * Returns the value of the CLRThreadAttribute child.
	 *
	 * @return the value of the CLRThreadAttribute child.
	 */
	@NotNull
	GenericAttributeValue<String> getCLRThreadAttribute();


	/**
	 * Returns the value of the CLRUnmanagedCodeCheck child.
	 *
	 * @return the value of the CLRUnmanagedCodeCheck child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getCLRUnmanagedCodeCheck();


	/**
	 * Returns the value of the CreateHotPatchableImage child.
	 *
	 * @return the value of the CreateHotPatchableImage child.
	 */
	@NotNull
	GenericAttributeValue<String> getCreateHotPatchableImage();


	/**
	 * Returns the value of the DataExecutionPrevention child.
	 *
	 * @return the value of the DataExecutionPrevention child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getDataExecutionPrevention();


	/**
	 * Returns the value of the DelayLoadDLLs child.
	 *
	 * @return the value of the DelayLoadDLLs child.
	 */
	@NotNull
	GenericAttributeValue<String> getDelayLoadDLLs();


	/**
	 * Returns the value of the DelaySign child.
	 *
	 * @return the value of the DelaySign child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getDelaySign();


	/**
	 * Returns the value of the Driver child.
	 *
	 * @return the value of the Driver child.
	 */
	@NotNull
	GenericAttributeValue<String> getDriver();


	/**
	 * Returns the value of the EmbedManagedResourceFile child.
	 *
	 * @return the value of the EmbedManagedResourceFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getEmbedManagedResourceFile();


	/**
	 * Returns the value of the EnableCOMDATFolding child.
	 *
	 * @return the value of the EnableCOMDATFolding child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getEnableCOMDATFolding();


	/**
	 * Returns the value of the EnableUAC child.
	 *
	 * @return the value of the EnableUAC child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getEnableUAC();


	/**
	 * Returns the value of the EntryPointSymbol child.
	 *
	 * @return the value of the EntryPointSymbol child.
	 */
	@NotNull
	GenericAttributeValue<String> getEntryPointSymbol();


	/**
	 * Returns the value of the EnvironmentVariables child.
	 *
	 * @return the value of the EnvironmentVariables child.
	 */
	@NotNull
	GenericAttributeValue<String> getEnvironmentVariables();


	/**
	 * Returns the value of the ExcludedInputPaths child.
	 *
	 * @return the value of the ExcludedInputPaths child.
	 */
	@NotNull
	GenericAttributeValue<String> getExcludedInputPaths();


	/**
	 * Returns the value of the FixedBaseAddress child.
	 *
	 * @return the value of the FixedBaseAddress child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getFixedBaseAddress();


	/**
	 * Returns the value of the ForceFileOutput child.
	 *
	 * @return the value of the ForceFileOutput child.
	 */
	@NotNull
	GenericAttributeValue<String> getForceFileOutput();


	/**
	 * Returns the value of the ForceSymbolReferences child.
	 *
	 * @return the value of the ForceSymbolReferences child.
	 */
	@NotNull
	GenericAttributeValue<String> getForceSymbolReferences();


	/**
	 * Returns the value of the FunctionOrder child.
	 *
	 * @return the value of the FunctionOrder child.
	 */
	@NotNull
	GenericAttributeValue<String> getFunctionOrder();


	/**
	 * Returns the value of the GenerateDebugInformation child.
	 *
	 * @return the value of the GenerateDebugInformation child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getGenerateDebugInformation();


	/**
	 * Returns the value of the GenerateManifest child.
	 *
	 * @return the value of the GenerateManifest child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getGenerateManifest();


	/**
	 * Returns the value of the GenerateMapFile child.
	 *
	 * @return the value of the GenerateMapFile child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getGenerateMapFile();


	/**
	 * Returns the value of the HeapCommitSize child.
	 *
	 * @return the value of the HeapCommitSize child.
	 */
	@NotNull
	GenericAttributeValue<String> getHeapCommitSize();


	/**
	 * Returns the value of the HeapReserveSize child.
	 *
	 * @return the value of the HeapReserveSize child.
	 */
	@NotNull
	GenericAttributeValue<String> getHeapReserveSize();


	/**
	 * Returns the value of the IgnoreAllDefaultLibraries child.
	 *
	 * @return the value of the IgnoreAllDefaultLibraries child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getIgnoreAllDefaultLibraries();


	/**
	 * Returns the value of the IgnoreEmbeddedIDL child.
	 *
	 * @return the value of the IgnoreEmbeddedIDL child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getIgnoreEmbeddedIDL();


	/**
	 * Returns the value of the IgnoreImportLibrary child.
	 *
	 * @return the value of the IgnoreImportLibrary child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getIgnoreImportLibrary();


	/**
	 * Returns the value of the IgnoreSpecificDefaultLibraries child.
	 *
	 * @return the value of the IgnoreSpecificDefaultLibraries child.
	 */
	@NotNull
	GenericAttributeValue<String> getIgnoreSpecificDefaultLibraries();


	/**
	 * Returns the value of the ImageHasSafeExceptionHandlers child.
	 *
	 * @return the value of the ImageHasSafeExceptionHandlers child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getImageHasSafeExceptionHandlers();


	/**
	 * Returns the value of the ImportLibrary child.
	 *
	 * @return the value of the ImportLibrary child.
	 */
	@NotNull
	GenericAttributeValue<String> getImportLibrary();


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
	 * Returns the value of the LargeAddressAware child.
	 *
	 * @return the value of the LargeAddressAware child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLargeAddressAware();


	/**
	 * Returns the value of the LinkDLL child.
	 *
	 * @return the value of the LinkDLL child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLinkDLL();


	/**
	 * Returns the value of the LinkErrorReporting child.
	 *
	 * @return the value of the LinkErrorReporting child.
	 */
	@NotNull
	GenericAttributeValue<String> getLinkErrorReporting();


	/**
	 * Returns the value of the LinkIncremental child.
	 *
	 * @return the value of the LinkIncremental child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLinkIncremental();


	/**
	 * Returns the value of the LinkLibraryDependencies child.
	 *
	 * @return the value of the LinkLibraryDependencies child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLinkLibraryDependencies();


	/**
	 * Returns the value of the LinkStatus child.
	 *
	 * @return the value of the LinkStatus child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLinkStatus();


	/**
	 * Returns the value of the LinkTimeCodeGeneration child.
	 *
	 * @return the value of the LinkTimeCodeGeneration child.
	 */
	@NotNull
	GenericAttributeValue<String> getLinkTimeCodeGeneration();


	/**
	 * Returns the value of the LogStandardErrorAsError child.
	 *
	 * @return the value of the LogStandardErrorAsError child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLogStandardErrorAsError();


	/**
	 * Returns the value of the ManifestFile child.
	 *
	 * @return the value of the ManifestFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getManifestFile();


	/**
	 * Returns the value of the MapExports child.
	 *
	 * @return the value of the MapExports child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getMapExports();


	/**
	 * Returns the value of the MapFileName child.
	 *
	 * @return the value of the MapFileName child.
	 */
	@NotNull
	GenericAttributeValue<String> getMapFileName();


	/**
	 * Returns the value of the MergedIDLBaseFileName child.
	 *
	 * @return the value of the MergedIDLBaseFileName child.
	 */
	@NotNull
	GenericAttributeValue<String> getMergedIDLBaseFileName();


	/**
	 * Returns the value of the MergeSections child.
	 *
	 * @return the value of the MergeSections child.
	 */
	@NotNull
	GenericAttributeValue<String> getMergeSections();


	/**
	 * Returns the value of the MidlCommandFile child.
	 *
	 * @return the value of the MidlCommandFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getMidlCommandFile();


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
	 * Returns the value of the MSDOSStubFileName child.
	 *
	 * @return the value of the MSDOSStubFileName child.
	 */
	@NotNull
	GenericAttributeValue<String> getMSDOSStubFileName();


	/**
	 * Returns the value of the NoEntryPoint child.
	 *
	 * @return the value of the NoEntryPoint child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getNoEntryPoint();


	/**
	 * Returns the value of the ObjectFiles child.
	 *
	 * @return the value of the ObjectFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getObjectFiles();


	/**
	 * Returns the value of the OptimizeReferences child.
	 *
	 * @return the value of the OptimizeReferences child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOptimizeReferences();


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
	 * Returns the value of the PerUserRedirection child.
	 *
	 * @return the value of the PerUserRedirection child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getPerUserRedirection();


	/**
	 * Returns the value of the PreprocessOutput child.
	 *
	 * @return the value of the PreprocessOutput child.
	 */
	@NotNull
	GenericAttributeValue<String> getPreprocessOutput();


	/**
	 * Returns the value of the PreventDllBinding child.
	 *
	 * @return the value of the PreventDllBinding child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getPreventDllBinding();


	/**
	 * Returns the value of the Profile child.
	 *
	 * @return the value of the Profile child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getProfile();


	/**
	 * Returns the value of the ProfileGuidedDatabase child.
	 *
	 * @return the value of the ProfileGuidedDatabase child.
	 */
	@NotNull
	GenericAttributeValue<String> getProfileGuidedDatabase();


	/**
	 * Returns the value of the ProgramDatabaseFile child.
	 *
	 * @return the value of the ProgramDatabaseFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getProgramDatabaseFile();


	/**
	 * Returns the value of the RandomizedBaseAddress child.
	 *
	 * @return the value of the RandomizedBaseAddress child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getRandomizedBaseAddress();


	/**
	 * Returns the value of the RegisterOutput child.
	 *
	 * @return the value of the RegisterOutput child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getRegisterOutput();


	/**
	 * Returns the value of the SectionAlignment child.
	 *
	 * @return the value of the SectionAlignment child.
	 */
	@NotNull
	GenericAttributeValue<String> getSectionAlignment();


	/**
	 * Returns the value of the SetChecksum child.
	 *
	 * @return the value of the SetChecksum child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSetChecksum();


	/**
	 * Returns the value of the ShowProgress child.
	 *
	 * @return the value of the ShowProgress child.
	 */
	@NotNull
	GenericAttributeValue<String> getShowProgress();


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
	 * Returns the value of the SpecifySectionAttributes child.
	 *
	 * @return the value of the SpecifySectionAttributes child.
	 */
	@NotNull
	GenericAttributeValue<String> getSpecifySectionAttributes();


	/**
	 * Returns the value of the StackCommitSize child.
	 *
	 * @return the value of the StackCommitSize child.
	 */
	@NotNull
	GenericAttributeValue<String> getStackCommitSize();


	/**
	 * Returns the value of the StackReserveSize child.
	 *
	 * @return the value of the StackReserveSize child.
	 */
	@NotNull
	GenericAttributeValue<String> getStackReserveSize();


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
	 * Returns the value of the StripPrivateSymbols child.
	 *
	 * @return the value of the StripPrivateSymbols child.
	 */
	@NotNull
	GenericAttributeValue<String> getStripPrivateSymbols();


	/**
	 * Returns the value of the SubSystem child.
	 *
	 * @return the value of the SubSystem child.
	 */
	@NotNull
	GenericAttributeValue<String> getSubSystem();


	/**
	 * Returns the value of the SupportNobindOfDelayLoadedDLL child.
	 *
	 * @return the value of the SupportNobindOfDelayLoadedDLL child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSupportNobindOfDelayLoadedDLL();


	/**
	 * Returns the value of the SupportUnloadOfDelayLoadedDLL child.
	 *
	 * @return the value of the SupportUnloadOfDelayLoadedDLL child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSupportUnloadOfDelayLoadedDLL();


	/**
	 * Returns the value of the SuppressStartupBanner child.
	 *
	 * @return the value of the SuppressStartupBanner child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSuppressStartupBanner();


	/**
	 * Returns the value of the SwapRunFromCD child.
	 *
	 * @return the value of the SwapRunFromCD child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSwapRunFromCD();


	/**
	 * Returns the value of the SwapRunFromNET child.
	 *
	 * @return the value of the SwapRunFromNET child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSwapRunFromNET();


	/**
	 * Returns the value of the TargetMachine child.
	 *
	 * @return the value of the TargetMachine child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetMachine();


	/**
	 * Returns the value of the TerminalServerAware child.
	 *
	 * @return the value of the TerminalServerAware child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTerminalServerAware();


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
	 * Returns the value of the TreatLinkerWarningAsErrors child.
	 *
	 * @return the value of the TreatLinkerWarningAsErrors child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTreatLinkerWarningAsErrors();


	/**
	 * Returns the value of the TurnOffAssemblyGeneration child.
	 *
	 * @return the value of the TurnOffAssemblyGeneration child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTurnOffAssemblyGeneration();


	/**
	 * Returns the value of the TypeLibraryFile child.
	 *
	 * @return the value of the TypeLibraryFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getTypeLibraryFile();


	/**
	 * Returns the value of the TypeLibraryResourceID child.
	 *
	 * @return the value of the TypeLibraryResourceID child.
	 */
	@NotNull
	GenericAttributeValue<String> getTypeLibraryResourceID();


	/**
	 * Returns the value of the UACExecutionLevel child.
	 *
	 * @return the value of the UACExecutionLevel child.
	 */
	@NotNull
	GenericAttributeValue<String> getUACExecutionLevel();


	/**
	 * Returns the value of the UACUIAccess child.
	 *
	 * @return the value of the UACUIAccess child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUACUIAccess();


	/**
	 * Returns the value of the UseLibraryDependencyInputs child.
	 *
	 * @return the value of the UseLibraryDependencyInputs child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUseLibraryDependencyInputs();


	/**
	 * Returns the value of the Version child.
	 *
	 * @return the value of the Version child.
	 */
	@NotNull
	GenericAttributeValue<String> getVersion();


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
