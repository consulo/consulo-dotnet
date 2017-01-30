// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:StringPropertyType interface.
 *
 * @author VISTALL
 */
public interface StringProperty extends DomElement
{

	/**
	 * Returns the value of the simple content.
	 *
	 * @return the value of the simple content.
	 */
	@NotNull
	@Required
	String getValue();

	/**
	 * Sets the value of the simple content.
	 *
	 * @param value the new value to set
	 */
	void setValue(@NotNull String value);


	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="StringPropertyType_Condition" _locComment="" -->Optional expression evaluated to determine whether the property should be evaluated
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
	 * <!-- _locID_text="ImportGroupType_Label" _locComment="" -->Optional expression. Used to identify or order system and user elements
	 * </pre>
	 *
	 * @return the value of the Label child.
	 */
	@NotNull
	GenericAttributeValue<String> getLabel();


}
