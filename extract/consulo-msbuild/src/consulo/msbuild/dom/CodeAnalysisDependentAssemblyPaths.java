// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:CodeAnalysisDependentAssemblyPathsElemType interface.
 *
 * @author VISTALL
 */
public interface CodeAnalysisDependentAssemblyPaths extends DomElement
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
	 * Returns the value of the Include child.
	 * <pre>
	 * <h3>Attribute null:Include documentation</h3>
	 * <!-- _locID_text="CodeAnalysisDependentAssemblyPaths_Include" _locComment="" -->A fully qualified path to a directory containing reference assemblies to be used by Code Analysis.
	 * </pre>
	 *
	 * @return the value of the Include child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getInclude();


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


}
