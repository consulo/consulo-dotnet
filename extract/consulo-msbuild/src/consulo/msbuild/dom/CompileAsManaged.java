// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:CompileAsManagedElemType interface.
 *
 * @author VISTALL
 */
public interface CompileAsManaged extends DomElement
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
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	GenericAttributeValue<String> getCondition();


}
