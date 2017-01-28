// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:ResourceCompileElemType interface.
 *
 * @author VISTALL
 */
public interface ResourceCompile extends DomElement, SimpleItem
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
	@Attribute("Condition")
	GenericAttributeValue<String> getConditionAttr();


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
	@Attribute("Include")
	GenericAttributeValue<String> getIncludeAttr();


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
	@Attribute("Exclude")
	GenericAttributeValue<String> getExcludeAttr();


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
	@Attribute("Remove")
	GenericAttributeValue<String> getRemoveAttr();


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
	@Attribute("Update")
	GenericAttributeValue<String> getUpdateAttr();


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
	@Attribute("Label")
	GenericAttributeValue<String> getLabelAttr();


	/**
	 * Returns the list of Culture children.
	 *
	 * @return the list of Culture children.
	 */
	@NotNull
	List<GenericDomValue<String>> getCultures();

	/**
	 * Adds new child to the list of Culture children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addCulture();


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
	 * Returns the list of NullTerminateStrings children.
	 *
	 * @return the list of NullTerminateStrings children.
	 */
	@NotNull
	List<GenericDomValue<String>> getNullTerminateStringses();

	/**
	 * Adds new child to the list of NullTerminateStrings children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addNullTerminateStrings();


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


}
