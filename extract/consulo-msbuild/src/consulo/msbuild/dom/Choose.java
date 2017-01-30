// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:ChooseType interface.
 * <pre>
 * <h3>Type http://schemas.microsoft.com/developer/msbuild/2003:ChooseType documentation</h3>
 * <!-- _locID_text="ChooseType" _locComment="" -->Groups When and Otherwise elements
 * </pre>
 *
 * @author VISTALL
 */
public interface Choose extends DomElement
{

	/**
	 * Returns the value of the Label child.
	 * <pre>
	 * <h3>Attribute null:Label documentation</h3>
	 * <!-- _locID_text="ChooseType_Label" _locComment="" -->Optional expression. Used to identify or order system and user elements
	 * </pre>
	 *
	 * @return the value of the Label child.
	 */
	@NotNull
	GenericAttributeValue<String> getLabel();


	/**
	 * Returns the list of When children.
	 *
	 * @return the list of When children.
	 */
	@NotNull
	@Required
	List<When> getWhens();

	/**
	 * Adds new child to the list of When children.
	 *
	 * @return created child
	 */
	When addWhen();


	/**
	 * Returns the value of the Otherwise child.
	 *
	 * @return the value of the Otherwise child.
	 */
	@NotNull
	Otherwise getOtherwise();


}
