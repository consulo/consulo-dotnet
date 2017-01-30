// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:ResolveComReferenceElemType interface.
 *
 * @author VISTALL
 */
public interface ResolveComReference extends DomElement, Task
{

	/**
	 * Returns the value of the DelaySign child.
	 *
	 * @return the value of the DelaySign child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getDelaySign();


	/**
	 * Returns the value of the ExecuteAsTool child.
	 *
	 * @return the value of the ExecuteAsTool child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getExecuteAsTool();


	/**
	 * Returns the value of the IncludeVersionInInteropName child.
	 *
	 * @return the value of the IncludeVersionInInteropName child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getIncludeVersionInInteropName();


	/**
	 * Returns the value of the KeyContainer child.
	 *
	 * @return the value of the KeyContainer child.
	 */
	@NotNull
	GenericAttributeValue<String> getKeyContainer();


	/**
	 * Returns the value of the KeyFile child.
	 *
	 * @return the value of the KeyFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getKeyFile();


	/**
	 * Returns the value of the NoClassMembers child.
	 *
	 * @return the value of the NoClassMembers child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getNoClassMembers();


	/**
	 * Returns the value of the ResolvedAssemblyReferences child.
	 *
	 * @return the value of the ResolvedAssemblyReferences child.
	 */
	@NotNull
	GenericAttributeValue<String> getResolvedAssemblyReferences();


	/**
	 * Returns the value of the ResolvedFiles child.
	 *
	 * @return the value of the ResolvedFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getResolvedFiles();


	/**
	 * Returns the value of the ResolvedModules child.
	 *
	 * @return the value of the ResolvedModules child.
	 */
	@NotNull
	GenericAttributeValue<String> getResolvedModules();


	/**
	 * Returns the value of the SdkToolsPath child.
	 *
	 * @return the value of the SdkToolsPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getSdkToolsPath();


	/**
	 * Returns the value of the StateFile child.
	 *
	 * @return the value of the StateFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getStateFile();


	/**
	 * Returns the value of the TargetFrameworkVersion child.
	 *
	 * @return the value of the TargetFrameworkVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrameworkVersion();


	/**
	 * Returns the value of the TargetProcessorArchitecture child.
	 *
	 * @return the value of the TargetProcessorArchitecture child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetProcessorArchitecture();


	/**
	 * Returns the value of the TypeLibFiles child.
	 *
	 * @return the value of the TypeLibFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getTypeLibFiles();


	/**
	 * Returns the value of the TypeLibNames child.
	 *
	 * @return the value of the TypeLibNames child.
	 */
	@NotNull
	GenericAttributeValue<String> getTypeLibNames();


	/**
	 * Returns the value of the WrapperOutputDirectory child.
	 *
	 * @return the value of the WrapperOutputDirectory child.
	 */
	@NotNull
	GenericAttributeValue<String> getWrapperOutputDirectory();


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
