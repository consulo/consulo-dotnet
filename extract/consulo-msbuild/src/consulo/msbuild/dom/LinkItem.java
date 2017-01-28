// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:LinkItem interface.
 *
 * @author VISTALL
 */
public interface LinkItem extends DomElement, SimpleItem
{

	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="SimpleItemType_Condition" _locComment="" -->Optional expression evaluated to determine whether the items should be evaluated
	 * </pre>
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	GenericAttributeValue<String> getCondition();


	/**
	 * Returns the value of the Include child.
	 * <pre>
	 * <h3>Attribute null:Include documentation</h3>
	 * <!-- _locID_text="SimpleItemType_Include" _locComment="" -->Semi-colon separated list of files (wildcards are allowed) or other item names to include in this item list
	 * </pre>
	 *
	 * @return the value of the Include child.
	 */
	@NotNull
	GenericAttributeValue<String> getInclude();


	/**
	 * Returns the value of the Exclude child.
	 * <pre>
	 * <h3>Attribute null:Exclude documentation</h3>
	 * <!-- _locID_text="SimpleItemType_Exclude" _locComment="" -->Semi-colon separated list of files (wildcards are allowed) or other item names to exclude from the Include list
	 * </pre>
	 *
	 * @return the value of the Exclude child.
	 */
	@NotNull
	GenericAttributeValue<String> getExclude();


	/**
	 * Returns the value of the Remove child.
	 * <pre>
	 * <h3>Attribute null:Remove documentation</h3>
	 * <!-- _locID_text="SimpleItemType_Remove" _locComment="" -->Semi-colon separated list of files (wildcards are allowed) or other item names to remove from the existing list contents
	 * </pre>
	 *
	 * @return the value of the Remove child.
	 */
	@NotNull
	GenericAttributeValue<String> getRemove();


	/**
	 * Returns the value of the Update child.
	 * <pre>
	 * <h3>Attribute null:Update documentation</h3>
	 * <!-- _locID_text="SimpleItemType_Remove" _locComment="" -->Semi-colon separated list of files (wildcards are allowed) or other item names to be updated with the metadata from contained in
	 * this xml element
	 * </pre>
	 *
	 * @return the value of the Update child.
	 */
	@NotNull
	GenericAttributeValue<String> getUpdate();


	/**
	 * Returns the value of the Label child.
	 * <pre>
	 * <h3>Attribute null:Label documentation</h3>
	 * <!-- _locID_text="ImportGroupType_Label" _locComment="" -->Optional expression. Used to identify or order system and user elements
	 * </pre>
	 *
	 * @return the value of the Label child.
	 */
	@NotNull
	GenericAttributeValue<String> getLabel();


	/**
	 * Returns the list of AdditionalDependencies children.
	 *
	 * @return the list of AdditionalDependencies children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAdditionalDependencieses();

	/**
	 * Adds new child to the list of AdditionalDependencies children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAdditionalDependencies();


	/**
	 * Returns the list of OutputFile children.
	 *
	 * @return the list of OutputFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getOutputFiles();

	/**
	 * Adds new child to the list of OutputFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addOutputFile();


	/**
	 * Returns the list of AssemblyDebug children.
	 *
	 * @return the list of AssemblyDebug children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAssemblyDebugs();

	/**
	 * Adds new child to the list of AssemblyDebug children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAssemblyDebug();


	/**
	 * Returns the list of SubSystem children.
	 *
	 * @return the list of SubSystem children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSubSystems();

	/**
	 * Adds new child to the list of SubSystem children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSubSystem();


	/**
	 * Returns the list of ShowProgress children.
	 *
	 * @return the list of ShowProgress children.
	 */
	@NotNull
	List<GenericDomValue<String>> getShowProgresses();

	/**
	 * Adds new child to the list of ShowProgress children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addShowProgress();


	/**
	 * Returns the list of GenerateDebugInformation children.
	 *
	 * @return the list of GenerateDebugInformation children.
	 */
	@NotNull
	List<GenericDomValue<String>> getGenerateDebugInformations();

	/**
	 * Adds new child to the list of GenerateDebugInformation children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addGenerateDebugInformation();


	/**
	 * Returns the list of EnableCOMDATFolding children.
	 *
	 * @return the list of EnableCOMDATFolding children.
	 */
	@NotNull
	List<GenericDomValue<String>> getEnableCOMDATFoldings();

	/**
	 * Adds new child to the list of EnableCOMDATFolding children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addEnableCOMDATFolding();


	/**
	 * Returns the list of OptimizeReferences children.
	 *
	 * @return the list of OptimizeReferences children.
	 */
	@NotNull
	List<GenericDomValue<String>> getOptimizeReferenceses();

	/**
	 * Adds new child to the list of OptimizeReferences children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addOptimizeReferences();


	/**
	 * Returns the list of Version children.
	 *
	 * @return the list of Version children.
	 */
	@NotNull
	List<GenericDomValue<String>> getVersions();

	/**
	 * Adds new child to the list of Version children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addVersion();


	/**
	 * Returns the list of Driver children.
	 *
	 * @return the list of Driver children.
	 */
	@NotNull
	List<GenericDomValue<String>> getDrivers();

	/**
	 * Adds new child to the list of Driver children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addDriver();


	/**
	 * Returns the list of RandomizedBaseAddress children.
	 *
	 * @return the list of RandomizedBaseAddress children.
	 */
	@NotNull
	List<GenericDomValue<String>> getRandomizedBaseAddresses();

	/**
	 * Adds new child to the list of RandomizedBaseAddress children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addRandomizedBaseAddress();


	/**
	 * Returns the list of SuppressStartupBanner children.
	 *
	 * @return the list of SuppressStartupBanner children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSuppressStartupBanners();

	/**
	 * Adds new child to the list of SuppressStartupBanner children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSuppressStartupBanner();


	/**
	 * Returns the list of AdditionalLibraryDirectories children.
	 *
	 * @return the list of AdditionalLibraryDirectories children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAdditionalLibraryDirectorieses();

	/**
	 * Adds new child to the list of AdditionalLibraryDirectories children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAdditionalLibraryDirectories();


	/**
	 * Returns the list of Profile children.
	 *
	 * @return the list of Profile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getProfiles();

	/**
	 * Adds new child to the list of Profile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addProfile();


	/**
	 * Returns the list of LinkStatus children.
	 *
	 * @return the list of LinkStatus children.
	 */
	@NotNull
	List<GenericDomValue<String>> getLinkStatuses();

	/**
	 * Adds new child to the list of LinkStatus children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addLinkStatus();


	/**
	 * Returns the list of FixedBaseAddress children.
	 *
	 * @return the list of FixedBaseAddress children.
	 */
	@NotNull
	List<GenericDomValue<String>> getFixedBaseAddresses();

	/**
	 * Adds new child to the list of FixedBaseAddress children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addFixedBaseAddress();


	/**
	 * Returns the list of DataExecutionPrevention children.
	 *
	 * @return the list of DataExecutionPrevention children.
	 */
	@NotNull
	List<GenericDomValue<String>> getDataExecutionPreventions();

	/**
	 * Adds new child to the list of DataExecutionPrevention children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addDataExecutionPrevention();


	/**
	 * Returns the list of SwapRunFromCD children.
	 *
	 * @return the list of SwapRunFromCD children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSwapRunFromCDs();

	/**
	 * Adds new child to the list of SwapRunFromCD children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSwapRunFromCD();


	/**
	 * Returns the list of SwapRunFromNET children.
	 *
	 * @return the list of SwapRunFromNET children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSwapRunFromNETs();

	/**
	 * Adds new child to the list of SwapRunFromNET children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSwapRunFromNET();


	/**
	 * Returns the list of RegisterOutput children.
	 *
	 * @return the list of RegisterOutput children.
	 */
	@NotNull
	List<GenericDomValue<String>> getRegisterOutputs();

	/**
	 * Adds new child to the list of RegisterOutput children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addRegisterOutput();


	/**
	 * Returns the list of AllowIsolation children.
	 *
	 * @return the list of AllowIsolation children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAllowIsolations();

	/**
	 * Adds new child to the list of AllowIsolation children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAllowIsolation();


	/**
	 * Returns the list of EnableUAC children.
	 *
	 * @return the list of EnableUAC children.
	 */
	@NotNull
	List<GenericDomValue<String>> getEnableUACs();

	/**
	 * Adds new child to the list of EnableUAC children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addEnableUAC();


	/**
	 * Returns the list of UACExecutionLevel children.
	 *
	 * @return the list of UACExecutionLevel children.
	 */
	@NotNull
	List<GenericDomValue<String>> getUACExecutionLevels();

	/**
	 * Adds new child to the list of UACExecutionLevel children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addUACExecutionLevel();


	/**
	 * Returns the list of UACUIAccess children.
	 *
	 * @return the list of UACUIAccess children.
	 */
	@NotNull
	List<GenericDomValue<String>> getUACUIAccesses();

	/**
	 * Adds new child to the list of UACUIAccess children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addUACUIAccess();


	/**
	 * Returns the list of PreventDllBinding children.
	 *
	 * @return the list of PreventDllBinding children.
	 */
	@NotNull
	List<GenericDomValue<String>> getPreventDllBindings();

	/**
	 * Adds new child to the list of PreventDllBinding children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addPreventDllBinding();


	/**
	 * Returns the list of IgnoreStandardIncludePath children.
	 *
	 * @return the list of IgnoreStandardIncludePath children.
	 */
	@NotNull
	List<GenericDomValue<String>> getIgnoreStandardIncludePaths();

	/**
	 * Adds new child to the list of IgnoreStandardIncludePath children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addIgnoreStandardIncludePath();


	/**
	 * Returns the list of GenerateMapFile children.
	 *
	 * @return the list of GenerateMapFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getGenerateMapFiles();

	/**
	 * Adds new child to the list of GenerateMapFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addGenerateMapFile();


	/**
	 * Returns the list of IgnoreEmbeddedIDL children.
	 *
	 * @return the list of IgnoreEmbeddedIDL children.
	 */
	@NotNull
	List<GenericDomValue<String>> getIgnoreEmbeddedIDLs();

	/**
	 * Adds new child to the list of IgnoreEmbeddedIDL children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addIgnoreEmbeddedIDL();


	/**
	 * Returns the list of TypeLibraryResourceID children.
	 *
	 * @return the list of TypeLibraryResourceID children.
	 */
	@NotNull
	List<GenericDomValue<String>> getTypeLibraryResourceIDs();

	/**
	 * Adds new child to the list of TypeLibraryResourceID children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addTypeLibraryResourceID();


	/**
	 * Returns the list of LinkErrorReporting children.
	 *
	 * @return the list of LinkErrorReporting children.
	 */
	@NotNull
	List<GenericDomValue<String>> getLinkErrorReportings();

	/**
	 * Adds new child to the list of LinkErrorReporting children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addLinkErrorReporting();


	/**
	 * Returns the list of MapExports children.
	 *
	 * @return the list of MapExports children.
	 */
	@NotNull
	List<GenericDomValue<String>> getMapExportses();

	/**
	 * Adds new child to the list of MapExports children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addMapExports();


	/**
	 * Returns the list of TargetMachine children.
	 *
	 * @return the list of TargetMachine children.
	 */
	@NotNull
	List<GenericDomValue<String>> getTargetMachines();

	/**
	 * Adds new child to the list of TargetMachine children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addTargetMachine();


	/**
	 * Returns the list of TreatLinkerWarningAsErrors children.
	 *
	 * @return the list of TreatLinkerWarningAsErrors children.
	 */
	@NotNull
	List<GenericDomValue<String>> getTreatLinkerWarningAsErrorses();

	/**
	 * Adds new child to the list of TreatLinkerWarningAsErrors children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addTreatLinkerWarningAsErrors();


	/**
	 * Returns the list of ForceFileOutput children.
	 *
	 * @return the list of ForceFileOutput children.
	 */
	@NotNull
	List<GenericDomValue<String>> getForceFileOutputs();

	/**
	 * Adds new child to the list of ForceFileOutput children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addForceFileOutput();


	/**
	 * Returns the list of CreateHotPatchableImage children.
	 *
	 * @return the list of CreateHotPatchableImage children.
	 */
	@NotNull
	List<GenericDomValue<String>> getCreateHotPatchableImages();

	/**
	 * Adds new child to the list of CreateHotPatchableImage children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addCreateHotPatchableImage();


	/**
	 * Returns the list of SpecifySectionAttributes children.
	 *
	 * @return the list of SpecifySectionAttributes children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSpecifySectionAttributeses();

	/**
	 * Adds new child to the list of SpecifySectionAttributes children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSpecifySectionAttributes();


	/**
	 * Returns the list of MSDOSStubFileName children.
	 *
	 * @return the list of MSDOSStubFileName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getMSDOSStubFileNames();

	/**
	 * Adds new child to the list of MSDOSStubFileName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addMSDOSStubFileName();


	/**
	 * Returns the list of IgnoreAllDefaultLibraries children.
	 *
	 * @return the list of IgnoreAllDefaultLibraries children.
	 */
	@NotNull
	List<GenericDomValue<String>> getIgnoreAllDefaultLibrarieses();

	/**
	 * Adds new child to the list of IgnoreAllDefaultLibraries children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addIgnoreAllDefaultLibraries();


	/**
	 * Returns the list of IgnoreSpecificDefaultLibraries children.
	 *
	 * @return the list of IgnoreSpecificDefaultLibraries children.
	 */
	@NotNull
	List<GenericDomValue<String>> getIgnoreSpecificDefaultLibrarieses();

	/**
	 * Adds new child to the list of IgnoreSpecificDefaultLibraries children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addIgnoreSpecificDefaultLibraries();


	/**
	 * Returns the list of ModuleDefinitionFile children.
	 *
	 * @return the list of ModuleDefinitionFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getModuleDefinitionFiles();

	/**
	 * Adds new child to the list of ModuleDefinitionFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addModuleDefinitionFile();


	/**
	 * Returns the list of AddModuleNamesToAssembly children.
	 *
	 * @return the list of AddModuleNamesToAssembly children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAddModuleNamesToAssemblies();

	/**
	 * Adds new child to the list of AddModuleNamesToAssembly children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAddModuleNamesToAssembly();


	/**
	 * Returns the list of EmbedManagedResourceFile children.
	 *
	 * @return the list of EmbedManagedResourceFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getEmbedManagedResourceFiles();

	/**
	 * Adds new child to the list of EmbedManagedResourceFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addEmbedManagedResourceFile();


	/**
	 * Returns the list of ForceSymbolReferences children.
	 *
	 * @return the list of ForceSymbolReferences children.
	 */
	@NotNull
	List<GenericDomValue<String>> getForceSymbolReferenceses();

	/**
	 * Adds new child to the list of ForceSymbolReferences children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addForceSymbolReferences();


	/**
	 * Returns the list of DelayLoadDLLs children.
	 *
	 * @return the list of DelayLoadDLLs children.
	 */
	@NotNull
	List<GenericDomValue<String>> getDelayLoadDLLses();

	/**
	 * Adds new child to the list of DelayLoadDLLs children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addDelayLoadDLLs();


	/**
	 * Returns the list of AssemblyLinkResource children.
	 *
	 * @return the list of AssemblyLinkResource children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAssemblyLinkResources();

	/**
	 * Adds new child to the list of AssemblyLinkResource children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAssemblyLinkResource();


	/**
	 * Returns the list of AdditionalManifestDependencies children.
	 *
	 * @return the list of AdditionalManifestDependencies children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAdditionalManifestDependencieses();

	/**
	 * Adds new child to the list of AdditionalManifestDependencies children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAdditionalManifestDependencies();


	/**
	 * Returns the list of StripPrivateSymbols children.
	 *
	 * @return the list of StripPrivateSymbols children.
	 */
	@NotNull
	List<GenericDomValue<String>> getStripPrivateSymbolses();

	/**
	 * Adds new child to the list of StripPrivateSymbols children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addStripPrivateSymbols();


	/**
	 * Returns the list of MapFileName children.
	 *
	 * @return the list of MapFileName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getMapFileNames();

	/**
	 * Adds new child to the list of MapFileName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addMapFileName();


	/**
	 * Returns the list of MinimumRequiredVersion children.
	 *
	 * @return the list of MinimumRequiredVersion children.
	 */
	@NotNull
	List<GenericDomValue<String>> getMinimumRequiredVersions();

	/**
	 * Adds new child to the list of MinimumRequiredVersion children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addMinimumRequiredVersion();


	/**
	 * Returns the list of HeapReserveSize children.
	 *
	 * @return the list of HeapReserveSize children.
	 */
	@NotNull
	List<GenericDomValue<String>> getHeapReserveSizes();

	/**
	 * Adds new child to the list of HeapReserveSize children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addHeapReserveSize();


	/**
	 * Returns the list of HeapCommitSize children.
	 *
	 * @return the list of HeapCommitSize children.
	 */
	@NotNull
	List<GenericDomValue<String>> getHeapCommitSizes();

	/**
	 * Adds new child to the list of HeapCommitSize children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addHeapCommitSize();


	/**
	 * Returns the list of StackReserveSize children.
	 *
	 * @return the list of StackReserveSize children.
	 */
	@NotNull
	List<GenericDomValue<String>> getStackReserveSizes();

	/**
	 * Adds new child to the list of StackReserveSize children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addStackReserveSize();


	/**
	 * Returns the list of StackCommitSize children.
	 *
	 * @return the list of StackCommitSize children.
	 */
	@NotNull
	List<GenericDomValue<String>> getStackCommitSizes();

	/**
	 * Adds new child to the list of StackCommitSize children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addStackCommitSize();


	/**
	 * Returns the list of LargeAddressAware children.
	 *
	 * @return the list of LargeAddressAware children.
	 */
	@NotNull
	List<GenericDomValue<String>> getLargeAddressAwares();

	/**
	 * Adds new child to the list of LargeAddressAware children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addLargeAddressAware();


	/**
	 * Returns the list of TerminalServerAware children.
	 *
	 * @return the list of TerminalServerAware children.
	 */
	@NotNull
	List<GenericDomValue<String>> getTerminalServerAwares();

	/**
	 * Adds new child to the list of TerminalServerAware children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addTerminalServerAware();


	/**
	 * Returns the list of FunctionOrder children.
	 *
	 * @return the list of FunctionOrder children.
	 */
	@NotNull
	List<GenericDomValue<String>> getFunctionOrders();

	/**
	 * Adds new child to the list of FunctionOrder children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addFunctionOrder();


	/**
	 * Returns the list of ProfileGuidedDatabase children.
	 *
	 * @return the list of ProfileGuidedDatabase children.
	 */
	@NotNull
	List<GenericDomValue<String>> getProfileGuidedDatabases();

	/**
	 * Adds new child to the list of ProfileGuidedDatabase children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addProfileGuidedDatabase();


	/**
	 * Returns the list of LinkTimeCodeGeneration children.
	 *
	 * @return the list of LinkTimeCodeGeneration children.
	 */
	@NotNull
	List<GenericDomValue<String>> getLinkTimeCodeGenerations();

	/**
	 * Adds new child to the list of LinkTimeCodeGeneration children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addLinkTimeCodeGeneration();


	/**
	 * Returns the list of MidlCommandFile children.
	 *
	 * @return the list of MidlCommandFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getMidlCommandFiles();

	/**
	 * Adds new child to the list of MidlCommandFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addMidlCommandFile();


	/**
	 * Returns the list of MergedIDLBaseFileName children.
	 *
	 * @return the list of MergedIDLBaseFileName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getMergedIDLBaseFileNames();

	/**
	 * Adds new child to the list of MergedIDLBaseFileName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addMergedIDLBaseFileName();


	/**
	 * Returns the list of TypeLibraryFile children.
	 *
	 * @return the list of TypeLibraryFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getTypeLibraryFiles();

	/**
	 * Adds new child to the list of TypeLibraryFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addTypeLibraryFile();


	/**
	 * Returns the list of EntryPointSymbol children.
	 *
	 * @return the list of EntryPointSymbol children.
	 */
	@NotNull
	List<GenericDomValue<String>> getEntryPointSymbols();

	/**
	 * Adds new child to the list of EntryPointSymbol children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addEntryPointSymbol();


	/**
	 * Returns the list of BaseAddress children.
	 *
	 * @return the list of BaseAddress children.
	 */
	@NotNull
	List<GenericDomValue<String>> getBaseAddresses();

	/**
	 * Adds new child to the list of BaseAddress children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addBaseAddress();


	/**
	 * Returns the list of ProgramDatabaseFile children.
	 *
	 * @return the list of ProgramDatabaseFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getProgramDatabaseFiles();

	/**
	 * Adds new child to the list of ProgramDatabaseFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addProgramDatabaseFile();


	/**
	 * Returns the list of SupportUnloadOfDelayLoadedDLL children.
	 *
	 * @return the list of SupportUnloadOfDelayLoadedDLL children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSupportUnloadOfDelayLoadedDLLs();

	/**
	 * Adds new child to the list of SupportUnloadOfDelayLoadedDLL children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSupportUnloadOfDelayLoadedDLL();


	/**
	 * Returns the list of SupportNobindOfDelayLoadedDLL children.
	 *
	 * @return the list of SupportNobindOfDelayLoadedDLL children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSupportNobindOfDelayLoadedDLLs();

	/**
	 * Adds new child to the list of SupportNobindOfDelayLoadedDLL children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSupportNobindOfDelayLoadedDLL();


	/**
	 * Returns the list of ImportLibrary children.
	 *
	 * @return the list of ImportLibrary children.
	 */
	@NotNull
	List<GenericDomValue<String>> getImportLibraries();

	/**
	 * Adds new child to the list of ImportLibrary children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addImportLibrary();


	/**
	 * Returns the list of MergeSections children.
	 *
	 * @return the list of MergeSections children.
	 */
	@NotNull
	List<GenericDomValue<String>> getMergeSectionses();

	/**
	 * Adds new child to the list of MergeSections children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addMergeSections();


	/**
	 * Returns the list of CLRThreadAttribute children.
	 *
	 * @return the list of CLRThreadAttribute children.
	 */
	@NotNull
	List<GenericDomValue<String>> getCLRThreadAttributes();

	/**
	 * Adds new child to the list of CLRThreadAttribute children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addCLRThreadAttribute();


	/**
	 * Returns the list of CLRImageType children.
	 *
	 * @return the list of CLRImageType children.
	 */
	@NotNull
	List<GenericDomValue<String>> getCLRImageTypes();

	/**
	 * Adds new child to the list of CLRImageType children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addCLRImageType();


	/**
	 * Returns the list of KeyFile children.
	 *
	 * @return the list of KeyFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getKeyFiles();

	/**
	 * Adds new child to the list of KeyFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addKeyFile();


	/**
	 * Returns the list of KeyContainer children.
	 *
	 * @return the list of KeyContainer children.
	 */
	@NotNull
	List<GenericDomValue<String>> getKeyContainers();

	/**
	 * Adds new child to the list of KeyContainer children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addKeyContainer();


	/**
	 * Returns the list of DelaySign children.
	 *
	 * @return the list of DelaySign children.
	 */
	@NotNull
	List<GenericDomValue<String>> getDelaySigns();

	/**
	 * Adds new child to the list of DelaySign children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addDelaySign();


	/**
	 * Returns the list of CLRUnmanagedCodeCheck children.
	 *
	 * @return the list of CLRUnmanagedCodeCheck children.
	 */
	@NotNull
	List<GenericDomValue<String>> getCLRUnmanagedCodeChecks();

	/**
	 * Adds new child to the list of CLRUnmanagedCodeCheck children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addCLRUnmanagedCodeCheck();


	/**
	 * Returns the list of SectionAlignment children.
	 *
	 * @return the list of SectionAlignment children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSectionAlignments();

	/**
	 * Adds new child to the list of SectionAlignment children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSectionAlignment();


	/**
	 * Returns the list of CLRSupportLastError children.
	 *
	 * @return the list of CLRSupportLastError children.
	 */
	@NotNull
	List<GenericDomValue<String>> getCLRSupportLastErrors();

	/**
	 * Adds new child to the list of CLRSupportLastError children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addCLRSupportLastError();


	/**
	 * Returns the list of ImageHasSafeExceptionHandlers children.
	 *
	 * @return the list of ImageHasSafeExceptionHandlers children.
	 */
	@NotNull
	List<GenericDomValue<String>> getImageHasSafeExceptionHandlerses();

	/**
	 * Adds new child to the list of ImageHasSafeExceptionHandlers children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addImageHasSafeExceptionHandlers();


}
