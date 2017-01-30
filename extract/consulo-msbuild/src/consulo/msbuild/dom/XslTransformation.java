// Generated on Sat Jan 28 04:58:20 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:XslTransformationElemType interface.
 *
 * @author VISTALL
 */
public interface XslTransformation extends DomElement, Task
{

	/**
	 * Returns the value of the OutputPaths child.
	 *
	 * @return the value of the OutputPaths child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getOutputPaths();


	/**
	 * Returns the value of the Parameters child.
	 *
	 * @return the value of the Parameters child.
	 */
	@NotNull
	GenericAttributeValue<String> getParameters();


	/**
	 * Returns the value of the XmlContent child.
	 *
	 * @return the value of the XmlContent child.
	 */
	@NotNull
	GenericAttributeValue<String> getXmlContent();


	/**
	 * Returns the value of the XmlInputPaths child.
	 *
	 * @return the value of the XmlInputPaths child.
	 */
	@NotNull
	GenericAttributeValue<String> getXmlInputPaths();


	/**
	 * Returns the value of the XslCompiledDllPath child.
	 *
	 * @return the value of the XslCompiledDllPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getXslCompiledDllPath();


	/**
	 * Returns the value of the XslContent child.
	 *
	 * @return the value of the XslContent child.
	 */
	@NotNull
	GenericAttributeValue<String> getXslContent();


	/**
	 * Returns the value of the XslInputPath child.
	 *
	 * @return the value of the XslInputPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getXslInputPath();


	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="TaskType_Condition" _locComment="" -->Optional expression evaluated to determine whether the task should be executed
	 * </pre>
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	GenericAttributeValue<String> getCondition();


	/**
	 * Returns the value of the ContinueOnError child.
	 * <pre>
	 * <h3>Attribute null:ContinueOnError documentation</h3>
	 * <!-- _locID_text="TaskType_ContinueOnError" _locComment="" -->Optional boolean indicating whether a recoverable task error should be ignored. Default false
	 * </pre>
	 *
	 * @return the value of the ContinueOnError child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getContinueOnError();


	/**
	 * Returns the value of the Architecture child.
	 * <pre>
	 * <h3>Attribute null:Architecture documentation</h3>
	 * <!-- _locID_text="TaskType_Architecture" _locComment="" -->Defines the bitness of the task if it must be run specifically in a 32bit or 64bit process. If not specified, it will run with the
	 * bitness of the build process.  If there are multiple tasks defined in UsingTask with the same name but with different Architecture attribute values, the value of the Architecture attribute
	 * specified here will be used to match and select the correct task
	 * </pre>
	 *
	 * @return the value of the Architecture child.
	 */
	@NotNull
	GenericAttributeValue<String> getArchitecture();


	/**
	 * Returns the value of the Runtime child.
	 * <pre>
	 * <h3>Attribute null:Runtime documentation</h3>
	 * <!-- _locID_text="TaskType_Runtime" _locComment="" -->Defines the .NET runtime of the task. This must be specified if the task must run on a specific version of the .NET runtime. If not
	 * specified, the task will run on the runtime being used by the build process. If there are multiple tasks defined in UsingTask with the same name but with different Runtime attribute values,
	 * the value of the Runtime attribute specified here will be used to match and select the correct task
	 * </pre>
	 *
	 * @return the value of the Runtime child.
	 */
	@NotNull
	GenericAttributeValue<String> getRuntime();


	/**
	 * Returns the list of Output children.
	 * <pre>
	 * <h3>Element http://schemas.microsoft.com/developer/msbuild/2003:Output documentation</h3>
	 * <!-- _locID_text="TaskType_Output" _locComment="" -->Optional element specifying a specific task output to be gathered
	 * </pre>
	 *
	 * @return the list of Output children.
	 */
	@NotNull
	List<Output> getOutputs();

	/**
	 * Adds new child to the list of Output children.
	 *
	 * @return created child
	 */
	Output addOutput();


}
