// Generated on Sat Jan 28 04:58:20 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:UsingTaskType interface.
 * <pre>
 * <h3>Type http://schemas.microsoft.com/developer/msbuild/2003:UsingTaskType documentation</h3>
 * <!-- _locID_text="UsingTaskType" _locComment="" -->Defines the assembly containing a task's implementation, or contains the implementation itself.
 * </pre>
 *
 * @author VISTALL
 */
public interface UsingTask extends DomElement
{

	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="UsingTaskType_Condition" _locComment="" -->Optional expression evaluated to determine whether the declaration should be evaluated
	 * </pre>
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	GenericAttributeValue<String> getCondition();


	/**
	 * Returns the value of the AssemblyName child.
	 * <pre>
	 * <h3>Attribute null:AssemblyName documentation</h3>
	 * <!-- _locID_text="UsingTaskType_AssemblyName" _locComment="" -->Optional name of assembly containing the task. Either AssemblyName or AssemblyFile must be used
	 * </pre>
	 *
	 * @return the value of the AssemblyName child.
	 */
	@NotNull
	GenericAttributeValue<String> getAssemblyName();


	/**
	 * Returns the value of the AssemblyFile child.
	 * <pre>
	 * <h3>Attribute null:AssemblyFile documentation</h3>
	 * <!-- _locID_text="UsingTaskType_AssemblyFile" _locComment="" -->Optional path to assembly containing the task. Either AssemblyName or AssemblyFile must be used
	 * </pre>
	 *
	 * @return the value of the AssemblyFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getAssemblyFile();


	/**
	 * Returns the value of the TaskName child.
	 * <pre>
	 * <h3>Attribute null:TaskName documentation</h3>
	 * <!-- _locID_text="UsingTaskType_TaskName" _locComment="" -->Name of task class in the assembly
	 * </pre>
	 *
	 * @return the value of the TaskName child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getTaskName();


	/**
	 * Returns the value of the TaskFactory child.
	 * <pre>
	 * <h3>Attribute null:TaskFactory documentation</h3>
	 * <!-- _locID_text="UsingTaskType_TaskFactory" _locComment="" -->Name of the task factory class in the assembly
	 * </pre>
	 *
	 * @return the value of the TaskFactory child.
	 */
	@NotNull
	GenericAttributeValue<String> getTaskFactory();


	/**
	 * Returns the value of the Architecture child.
	 * <pre>
	 * <h3>Attribute null:Architecture documentation</h3>
	 * <!-- _locID_text="UsingTaskType_Architecture" _locComment="" -->Defines the architecture of the task host that this task should be run in.  Currently supported values:  x86, x64,
	 * CurrentArchitecture, and * (any).  If Architecture is not specified, either the task will be run within the MSBuild process, or the task host will be launched using the architecture of the
	 * parent MSBuild process
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
	 * <!-- _locID_text="UsingTaskType_Runtime" _locComment="" -->Defines the .NET runtime version of the task host that this task should be run in.  Currently supported values:  CLR2, CLR4,
	 * CurrentRuntime, and * (any).  If Runtime is not specified, either the task will be run within the MSBuild process, or the task host will be launched using the runtime of the parent MSBuild
	 * process
	 * </pre>
	 *
	 * @return the value of the Runtime child.
	 */
	@NotNull
	GenericAttributeValue<String> getRuntime();


	/**
	 * Returns the value of the ParameterGroup child.
	 *
	 * @return the value of the ParameterGroup child.
	 */
	@NotNull
	ParameterGroup getParameterGroup();


	/**
	 * Returns the value of the Task child.
	 *
	 * @return the value of the Task child.
	 */
	@NotNull
	UsingTaskBody getTask();


}
