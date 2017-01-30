// Generated on Sat Jan 28 04:58:20 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:VCBuildElemType interface.
 *
 * @author VISTALL
 */
public interface VCBuild extends DomElement, Task
{

	/**
	 * Returns the value of the AdditionalLibPaths child.
	 *
	 * @return the value of the AdditionalLibPaths child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalLibPaths();


	/**
	 * Returns the value of the AdditionalLinkLibraryPaths child.
	 *
	 * @return the value of the AdditionalLinkLibraryPaths child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalLinkLibraryPaths();


	/**
	 * Returns the value of the AdditionalOptions child.
	 *
	 * @return the value of the AdditionalOptions child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalOptions();


	/**
	 * Returns the value of the Clean child.
	 *
	 * @return the value of the Clean child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getClean();


	/**
	 * Returns the value of the Configuration child.
	 *
	 * @return the value of the Configuration child.
	 */
	@NotNull
	GenericAttributeValue<String> getConfiguration();


	/**
	 * Returns the value of the Override child.
	 *
	 * @return the value of the Override child.
	 */
	@NotNull
	GenericAttributeValue<String> getOverride();


	/**
	 * Returns the value of the Platform child.
	 *
	 * @return the value of the Platform child.
	 */
	@NotNull
	GenericAttributeValue<String> getPlatform();


	/**
	 * Returns the value of the Projects child.
	 *
	 * @return the value of the Projects child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getProjects();


	/**
	 * Returns the value of the Rebuild child.
	 *
	 * @return the value of the Rebuild child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getRebuild();


	/**
	 * Returns the value of the SolutionFile child.
	 *
	 * @return the value of the SolutionFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getSolutionFile();


	/**
	 * Returns the value of the Timeout child.
	 *
	 * @return the value of the Timeout child.
	 */
	@NotNull
	GenericAttributeValue<String> getTimeout();


	/**
	 * Returns the value of the ToolPath child.
	 *
	 * @return the value of the ToolPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getToolPath();


	/**
	 * Returns the value of the UseEnvironment child.
	 *
	 * @return the value of the UseEnvironment child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUseEnvironment();


	/**
	 * Returns the value of the UserEnvironment child.
	 *
	 * @return the value of the UserEnvironment child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUserEnvironment();


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
