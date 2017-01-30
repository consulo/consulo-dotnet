// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:ManifestElemType interface.
 *
 * @author VISTALL
 */
public interface Manifest extends DomElement, SimpleItem
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
	 * Returns the list of AssemblyIdentity children.
	 *
	 * @return the list of AssemblyIdentity children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAssemblyIdentities();

	/**
	 * Adds new child to the list of AssemblyIdentity children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAssemblyIdentity();


	/**
	 * Returns the list of AdditionalManifestFiles children.
	 *
	 * @return the list of AdditionalManifestFiles children.
	 */
	@NotNull
	List<GenericDomValue<String>> getAdditionalManifestFileses();

	/**
	 * Adds new child to the list of AdditionalManifestFiles children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addAdditionalManifestFiles();


	/**
	 * Returns the list of InputResourceManifests children.
	 *
	 * @return the list of InputResourceManifests children.
	 */
	@NotNull
	List<GenericDomValue<String>> getInputResourceManifestses();

	/**
	 * Adds new child to the list of InputResourceManifests children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addInputResourceManifests();


	/**
	 * Returns the list of EnableDPIAwareness children.
	 *
	 * @return the list of EnableDPIAwareness children.
	 */
	@NotNull
	List<GenericDomValue<String>> getEnableDPIAwarenesses();

	/**
	 * Adds new child to the list of EnableDPIAwareness children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addEnableDPIAwareness();


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
	 * Returns the list of OutputManifestFile children.
	 *
	 * @return the list of OutputManifestFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getOutputManifestFiles();

	/**
	 * Adds new child to the list of OutputManifestFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addOutputManifestFile();


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
	 * Returns the list of VerboseOutput children.
	 *
	 * @return the list of VerboseOutput children.
	 */
	@NotNull
	List<GenericDomValue<String>> getVerboseOutputs();

	/**
	 * Adds new child to the list of VerboseOutput children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addVerboseOutput();


	/**
	 * Returns the list of ResourceOutputFileName children.
	 *
	 * @return the list of ResourceOutputFileName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getResourceOutputFileNames();

	/**
	 * Adds new child to the list of ResourceOutputFileName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addResourceOutputFileName();


	/**
	 * Returns the list of GenerateCatalogFiles children.
	 *
	 * @return the list of GenerateCatalogFiles children.
	 */
	@NotNull
	List<GenericDomValue<String>> getGenerateCatalogFileses();

	/**
	 * Adds new child to the list of GenerateCatalogFiles children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addGenerateCatalogFiles();


	/**
	 * Returns the list of DependencyInformationFile children.
	 *
	 * @return the list of DependencyInformationFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getDependencyInformationFiles();

	/**
	 * Adds new child to the list of DependencyInformationFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addDependencyInformationFile();


	/**
	 * Returns the list of ManifestFromManagedAssembly children.
	 *
	 * @return the list of ManifestFromManagedAssembly children.
	 */
	@NotNull
	List<GenericDomValue<String>> getManifestFromManagedAssemblies();

	/**
	 * Adds new child to the list of ManifestFromManagedAssembly children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addManifestFromManagedAssembly();


	/**
	 * Returns the list of SuppressDependencyElement children.
	 *
	 * @return the list of SuppressDependencyElement children.
	 */
	@NotNull
	List<GenericDomValue<String>> getSuppressDependencyElements();

	/**
	 * Adds new child to the list of SuppressDependencyElement children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addSuppressDependencyElement();


	/**
	 * Returns the list of GenerateCategoryTags children.
	 *
	 * @return the list of GenerateCategoryTags children.
	 */
	@NotNull
	List<GenericDomValue<String>> getGenerateCategoryTagses();

	/**
	 * Adds new child to the list of GenerateCategoryTags children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addGenerateCategoryTags();


	/**
	 * Returns the list of RegistrarScriptFile children.
	 *
	 * @return the list of RegistrarScriptFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getRegistrarScriptFiles();

	/**
	 * Adds new child to the list of RegistrarScriptFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addRegistrarScriptFile();


	/**
	 * Returns the list of ComponentFileName children.
	 *
	 * @return the list of ComponentFileName children.
	 */
	@NotNull
	List<GenericDomValue<String>> getComponentFileNames();

	/**
	 * Adds new child to the list of ComponentFileName children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addComponentFileName();


	/**
	 * Returns the list of ReplacementsFile children.
	 *
	 * @return the list of ReplacementsFile children.
	 */
	@NotNull
	List<GenericDomValue<String>> getReplacementsFiles();

	/**
	 * Adds new child to the list of ReplacementsFile children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addReplacementsFile();


	/**
	 * Returns the list of UpdateFileHashesSearchPath children.
	 *
	 * @return the list of UpdateFileHashesSearchPath children.
	 */
	@NotNull
	List<GenericDomValue<String>> getUpdateFileHashesSearchPaths();

	/**
	 * Adds new child to the list of UpdateFileHashesSearchPath children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addUpdateFileHashesSearchPath();


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
	 * Returns the list of OutputResourceManifests children.
	 *
	 * @return the list of OutputResourceManifests children.
	 */
	@NotNull
	List<OutputResourceManifests> getOutputResourceManifestses();

	/**
	 * Adds new child to the list of OutputResourceManifests children.
	 *
	 * @return created child
	 */
	OutputResourceManifests addOutputResourceManifests();


}
