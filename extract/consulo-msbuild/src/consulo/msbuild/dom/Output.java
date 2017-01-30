// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:OutputElemType interface.
 *
 * @author VISTALL
 */
public interface Output extends DomElement
{

	/**
	 * Returns the value of the TaskParameter child.
	 * <pre>
	 * <h3>Attribute null:TaskParameter documentation</h3>
	 * <!-- _locID_text="TaskType_Output_TaskParameter" _locComment="" -->Task parameter to gather. Matches the name of a .NET Property on the task class that has an [Output] attribute
	 * </pre>
	 *
	 * @return the value of the TaskParameter child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getTaskParameter();


	/**
	 * Returns the value of the ItemName child.
	 * <pre>
	 * <h3>Attribute null:ItemName documentation</h3>
	 * <!-- _locID_text="TaskType_Output_ItemName" _locComment="" -->Optional name of an item list to put the gathered outputs into. Either ItemName or PropertyName must be specified
	 * </pre>
	 *
	 * @return the value of the ItemName child.
	 */
	@NotNull
	GenericAttributeValue<String> getItemName();


	/**
	 * Returns the value of the PropertyName child.
	 * <pre>
	 * <h3>Attribute null:PropertyName documentation</h3>
	 * <!-- _locID_text="TaskType_Output_PropertyName" _locComment="" -->Optional name of a property to put the gathered output into. Either PropertyName or ItemName must be specified
	 * </pre>
	 *
	 * @return the value of the PropertyName child.
	 */
	@NotNull
	GenericAttributeValue<String> getPropertyName();


	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="TaskType_Output_Condition" _locComment="" -->Optional expression evaluated to determine whether the output should be gathered
	 * </pre>
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	GenericAttributeValue<String> getCondition();


}
