// Generated on Sat Jan 28 04:58:20 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:WhenType interface.
 * <pre>
 * <h3>Type http://schemas.microsoft.com/developer/msbuild/2003:WhenType documentation</h3>
 * <!-- _locID_text="WhenType" _locComment="" -->Groups PropertyGroup and/or ItemGroup elements
 * </pre>
 *
 * @author VISTALL
 */
public interface When extends DomElement
{

	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="WhenType_Condition" _locComment="" -->Optional expression evaluated to determine whether the child PropertyGroups and/or ItemGroups should be used
	 * </pre>
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getCondition();


	/**
	 * Returns the value of the PropertyGroup child.
	 *
	 * @return the value of the PropertyGroup child.
	 */
	@NotNull
	@Required
	PropertyGroup getPropertyGroup();


	/**
	 * Returns the value of the ItemGroup child.
	 *
	 * @return the value of the ItemGroup child.
	 */
	@NotNull
	@Required
	ItemGroup getItemGroup();


	/**
	 * Returns the value of the Choose child.
	 *
	 * @return the value of the Choose child.
	 */
	@NotNull
	@Required
	Choose getChoose();


}
