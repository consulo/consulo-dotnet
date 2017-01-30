// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:MidlElemType interface.
 *
 * @author VISTALL
 */
public interface MIDL extends DomElement, Task
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
	 * Returns the list of MkTypLibCompatible children.
	 *
	 * @return the list of MkTypLibCompatible children.
	 */
	@NotNull
	List<GenericDomValue<String>> getMkTypLibCompatibles();

	/**
	 * Adds new child to the list of MkTypLibCompatible children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addMkTypLibCompatible();


	/**
	 * Returns the list of ValidateAllParameters children.
	 *
	 * @return the list of ValidateAllParameters children.
	 */
	@NotNull
	List<GenericDomValue<Boolean>> getValidateAllParameterses();

	/**
	 * Adds new child to the list of ValidateAllParameters children.
	 *
	 * @return created child
	 */
	GenericDomValue<Boolean> addValidateAllParameters();


	/**
	 * Returns the list of PreprocessorDefinitions children.
	 *
	 * @return the list of PreprocessorDefinitions children.
	 */
	@NotNull
	List<GenericDomValue<String>> getPreprocessorDefinitionses();

	/**
	 * Adds new child to the list of PreprocessorDefinitions children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addPreprocessorDefinitions();


	/**
	 * Returns the list of TypeLibraryName children.
	 *
	 * @return the list of TypeLibraryName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getTypeLibraryNames();

	/**
	 * Adds new child to the list of TypeLibraryName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addTypeLibraryName();


	/**
	 * Returns the list of ErrorCheckRefPointers children.
	 *
	 * @return the list of ErrorCheckRefPointers children.
	 */
	@NotNull
	List<GenericDomValue<String>> getErrorCheckRefPointerses();

	/**
	 * Adds new child to the list of ErrorCheckRefPointers children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addErrorCheckRefPointers();


	/**
	 * Returns the list of ErrorCheckStubData children.
	 *
	 * @return the list of ErrorCheckStubData children.
	 */
	@NotNull
	List<GenericDomValue<String>> getErrorCheckStubDatas();

	/**
	 * Adds new child to the list of ErrorCheckStubData children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addErrorCheckStubData();


	/**
	 * Returns the list of ErrorCheckBounds children.
	 *
	 * @return the list of ErrorCheckBounds children.
	 */
	@NotNull
	List<GenericDomValue<String>> getErrorCheckBoundses();

	/**
	 * Adds new child to the list of ErrorCheckBounds children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addErrorCheckBounds();


	/**
	 * Returns the list of HeaderFileName children.
	 *
	 * @return the list of HeaderFileName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getHeaderFileNames();

	/**
	 * Adds new child to the list of HeaderFileName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addHeaderFileName();


	/**
	 * Returns the list of ErrorCheckEnumRange children.
	 *
	 * @return the list of ErrorCheckEnumRange children.
	 */
	@NotNull
	List<GenericDomValue<String>> getErrorCheckEnumRanges();

	/**
	 * Adds new child to the list of ErrorCheckEnumRange children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addErrorCheckEnumRange();


	/**
	 * Returns the list of ErrorCheckAllocations children.
	 *
	 * @return the list of ErrorCheckAllocations children.
	 */
	@NotNull
	List<GenericDomValue<String>> getErrorCheckAllocationses();

	/**
	 * Adds new child to the list of ErrorCheckAllocations children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addErrorCheckAllocations();


	/**
	 * Returns the list of WarnAsError children.
	 *
	 * @return the list of WarnAsError children.
	 */
	@NotNull
	List<GenericDomValue<String>> getWarnAsErrors();

	/**
	 * Adds new child to the list of WarnAsError children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addWarnAsError();


	/**
	 * Returns the list of GenerateTypeLibrary children.
	 *
	 * @return the list of GenerateTypeLibrary children.
	 */
	@NotNull
	List<GenericDomValue<String>> getGenerateTypeLibraries();

	/**
	 * Adds new child to the list of GenerateTypeLibrary children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addGenerateTypeLibrary();


	/**
	 * Returns the list of AdditionalIncludeDirectories children.
	 *
	 * @return the list of AdditionalIncludeDirectories children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAdditionalIncludeDirectorieses();

	/**
	 * Adds new child to the list of AdditionalIncludeDirectories children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAdditionalIncludeDirectories();


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
	 * Returns the list of DefaultCharType children.
	 *
	 * @return the list of DefaultCharType children.
	 */
	@NotNull
	List<GenericDomValue<String>> getDefaultCharTypes();

	/**
	 * Adds new child to the list of DefaultCharType children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addDefaultCharType();


	/**
	 * Returns the list of TargetEnvironment children.
	 *
	 * @return the list of TargetEnvironment children.
	 */
	@NotNull
	List<GenericDomValue<String>> getTargetEnvironments();

	/**
	 * Adds new child to the list of TargetEnvironment children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addTargetEnvironment();


	/**
	 * Returns the list of GenerateStublessProxies children.
	 *
	 * @return the list of GenerateStublessProxies children.
	 */
	@NotNull
	List<GenericDomValue<String>> getGenerateStublessProxieses();

	/**
	 * Adds new child to the list of GenerateStublessProxies children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addGenerateStublessProxies();


	/**
	 * Returns the list of SuppressCompilerWarnings children.
	 *
	 * @return the list of SuppressCompilerWarnings children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSuppressCompilerWarningses();

	/**
	 * Adds new child to the list of SuppressCompilerWarnings children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSuppressCompilerWarnings();


	/**
	 * Returns the list of ApplicationConfigurationMode children.
	 *
	 * @return the list of ApplicationConfigurationMode children.
	 */
	@NotNull
	List<GenericDomValue<String>> getApplicationConfigurationModes();

	/**
	 * Adds new child to the list of ApplicationConfigurationMode children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addApplicationConfigurationMode();


	/**
	 * Returns the list of LocaleID children.
	 *
	 * @return the list of LocaleID children.
	 */
	@NotNull
	List<GenericDomValue<String>> getLocaleIDs();

	/**
	 * Adds new child to the list of LocaleID children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addLocaleID();


	/**
	 * Returns the list of OutputDirectory children.
	 *
	 * @return the list of OutputDirectory children.
	 */
	@NotNull
	List<GenericDomValue<String>> getOutputDirectories();

	/**
	 * Adds new child to the list of OutputDirectory children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addOutputDirectory();


	/**
	 * Returns the list of DllDataFileName children.
	 *
	 * @return the list of DllDataFileName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getDllDataFileNames();

	/**
	 * Adds new child to the list of DllDataFileName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addDllDataFileName();


	/**
	 * Returns the list of InterfaceIdentifierFileName children.
	 *
	 * @return the list of InterfaceIdentifierFileName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getInterfaceIdentifierFileNames();

	/**
	 * Adds new child to the list of InterfaceIdentifierFileName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addInterfaceIdentifierFileName();


	/**
	 * Returns the list of ProxyFileName children.
	 *
	 * @return the list of ProxyFileName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getProxyFileNames();

	/**
	 * Adds new child to the list of ProxyFileName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addProxyFileName();


	/**
	 * Returns the list of GenerateClientFiles children.
	 *
	 * @return the list of GenerateClientFiles children.
	 */
	@NotNull
	List<GenericDomValue<String>> getGenerateClientFileses();

	/**
	 * Adds new child to the list of GenerateClientFiles children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addGenerateClientFiles();


	/**
	 * Returns the list of GenerateServerFiles children.
	 *
	 * @return the list of GenerateServerFiles children.
	 */
	@NotNull
	List<GenericDomValue<String>> getGenerateServerFileses();

	/**
	 * Adds new child to the list of GenerateServerFiles children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addGenerateServerFiles();


	/**
	 * Returns the list of ClientStubFile children.
	 *
	 * @return the list of ClientStubFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getClientStubFiles();

	/**
	 * Adds new child to the list of ClientStubFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addClientStubFile();


	/**
	 * Returns the list of ServerStubFile children.
	 *
	 * @return the list of ServerStubFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getServerStubFiles();

	/**
	 * Adds new child to the list of ServerStubFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addServerStubFile();


	/**
	 * Returns the list of TypeLibFormat children.
	 *
	 * @return the list of TypeLibFormat children.
	 */
	@NotNull
	List<GenericDomValue<String>> getTypeLibFormats();

	/**
	 * Adds new child to the list of TypeLibFormat children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addTypeLibFormat();


	/**
	 * Returns the list of CPreprocessOptions children.
	 *
	 * @return the list of CPreprocessOptions children.
	 */
	@NotNull
	List<GenericDomValue<String>> getCPreprocessOptionses();

	/**
	 * Adds new child to the list of CPreprocessOptions children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addCPreprocessOptions();


	/**
	 * Returns the list of UndefinePreprocessorDefinitions children.
	 *
	 * @return the list of UndefinePreprocessorDefinitions children.
	 */
	@NotNull
	List<GenericDomValue<String>> getUndefinePreprocessorDefinitionses();

	/**
	 * Adds new child to the list of UndefinePreprocessorDefinitions children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addUndefinePreprocessorDefinitions();


	/**
	 * Returns the list of EnableErrorChecks children.
	 *
	 * @return the list of EnableErrorChecks children.
	 */
	@NotNull
	List<GenericDomValue<String>> getEnableErrorCheckses();

	/**
	 * Adds new child to the list of EnableErrorChecks children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addEnableErrorChecks();


	/**
	 * Returns the list of RedirectOutputAndErrors children.
	 *
	 * @return the list of RedirectOutputAndErrors children.
	 */
	@NotNull
	List<GenericDomValue<String>> getRedirectOutputAndErrorses();

	/**
	 * Adds new child to the list of RedirectOutputAndErrors children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addRedirectOutputAndErrors();


	/**
	 * Returns the list of AdditionalOptions children.
	 *
	 * @return the list of AdditionalOptions children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAdditionalOptionses();

	/**
	 * Adds new child to the list of AdditionalOptions children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAdditionalOptions();


	/**
	 * Returns the list of StructMemberAlignment children.
	 *
	 * @return the list of StructMemberAlignment children.
	 */
	@NotNull
	List<GenericDomValue<String>> getStructMemberAlignments();

	/**
	 * Adds new child to the list of StructMemberAlignment children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addStructMemberAlignment();


}
