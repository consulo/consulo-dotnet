// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:OnErrorType interface.
 * <pre>
 * <h3>Type http://schemas.microsoft.com/developer/msbuild/2003:OnErrorType documentation</h3>
 * <!-- _locID_text="OnErrorType" _locComment="" -->Specifies targets to execute in the event of a recoverable error
 * </pre>
 *
 * @author VISTALL
 */
public interface OnError extends DomElement
{

	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="OnErrorType_Condition" _locComment="" -->Optional expression evaluated to determine whether the targets should be executed
	 * </pre>
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	GenericAttributeValue<String> getCondition();


	/**
	 * Returns the value of the ExecuteTargets child.
	 * <pre>
	 * <h3>Attribute null:ExecuteTargets documentation</h3>
	 * <!-- _locID_text="OnErrorType_ExecuteTargets" _locComment="" -->Semi-colon separated list of targets to execute
	 * </pre>
	 *
	 * @return the value of the ExecuteTargets child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getExecuteTargets();


	/**
	 * Returns the value of the Label child.
	 * <pre>
	 * <h3>Attribute null:Label documentation</h3>
	 * <!-- _locID_text="ImportType_Label" _locComment="" -->Optional expression. Used to identify or order system and user elements
	 * </pre>
	 *
	 * @return the value of the Label child.
	 */
	@NotNull
	GenericAttributeValue<String> getLabel();


}
