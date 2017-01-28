// Generated on Sat Jan 28 04:58:18 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:AppxManifestImageFileNameQueryElemType interface.
 *
 * @author VISTALL
 */
public interface AppxManifestImageFileNameQuery extends DomElement, SimpleItem
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
	 * Returns the value of the DescriptionID child.
	 * <pre>
	 * <h3>Element http://schemas.microsoft.com/developer/msbuild/2003:DescriptionID documentation</h3>
	 * <!-- _locID_text="AppxManifestImageFileNameQuery_DescriptionID" _locComment="" -->ID of description string resource describing this type of the image.
	 * </pre>
	 *
	 * @return the value of the DescriptionID child.
	 */
	@NotNull
	@Required
	GenericDomValue<String> getDescriptionID();


	/**
	 * Returns the value of the ExpectedScaleDimensions child.
	 * <pre>
	 * <h3>Element http://schemas.microsoft.com/developer/msbuild/2003:ExpectedScaleDimensions documentation</h3>
	 * <!-- _locID_text="AppxManifestImageFileNameQuery_ExpectedScaleDimensions" _locComment="" -->Semicolon-delimited list of expected scale dimensions in format '{scale}:{width}x{height}'.
	 * </pre>
	 *
	 * @return the value of the ExpectedScaleDimensions child.
	 */
	@NotNull
	@Required
	GenericDomValue<String> getExpectedScaleDimensions();


	/**
	 * Returns the value of the ExpectedTargetSizes child.
	 * <pre>
	 * <h3>Element http://schemas.microsoft.com/developer/msbuild/2003:ExpectedTargetSizes documentation</h3>
	 * <!-- _locID_text="AppxManifestImageFileNameQuery_ExpectedScaleDimensions" _locComment="" -->Semicolon-delimited list of expected target sizes.
	 * </pre>
	 *
	 * @return the value of the ExpectedTargetSizes child.
	 */
	@NotNull
	GenericDomValue<String> getExpectedTargetSizes();


	/**
	 * Returns the value of the MaximumFileSize child.
	 * <pre>
	 * <h3>Element http://schemas.microsoft.com/developer/msbuild/2003:MaximumFileSize documentation</h3>
	 * <!-- _locID_text="AppxManifestImageFileNameQuery_ExpectedScaleDimensions" _locComment="" -->Maximum file size in bytes.
	 * </pre>
	 *
	 * @return the value of the MaximumFileSize child.
	 */
	@NotNull
	GenericDomValue<String> getMaximumFileSize();


}
