// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:PropertyGroupType interface.
 * <pre>
 * <h3>Type http://schemas.microsoft.com/developer/msbuild/2003:PropertyGroupType documentation</h3>
 * <!-- _locID_text="PropertyGroupType" _locComment="" -->Groups property definitions
 * </pre>
 *
 * @author VISTALL
 */
public interface PropertyGroup extends DomElement
{

	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="PropertyGroupType_Condition" _locComment="" -->Optional expression evaluated to determine whether the PropertyGroup should be used
	 * </pre>
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	GenericAttributeValue<String> getCondition();


	/**
	 * Returns the value of the Label child.
	 * <pre>
	 * <h3>Attribute null:Label documentation</h3>
	 * <!-- _locID_text="PropertyGroupType_Label" _locComment="" -->Optional expression. Used to identify or order system and user elements
	 * </pre>
	 *
	 * @return the value of the Label child.
	 */
	@NotNull
	GenericAttributeValue<String> getLabel();


	/**
	 * Returns the list of Property children.
	 *
	 * @return the list of Property children.
	 */
	@NotNull
	List<GenericDomValue<String>> getProperties();

	/**
	 * Adds new child to the list of Property children.
	 *
	 * @return created child
	 */
	GenericDomValue<String> addProperty();


}
