// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:GenerateProjectPriFileElemType interface.
 *
 * @author VISTALL
 */
public interface GenerateProjectPriFile extends DomElement, ToolTask
{

	/**
	 * Returns the value of the MakePriExeFullPath child.
	 *
	 * @return the value of the MakePriExeFullPath child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getMakePriExeFullPath();


	/**
	 * Returns the value of the PriConfigXmlPath child.
	 *
	 * @return the value of the PriConfigXmlPath child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getPriConfigXmlPath();


	/**
	 * Returns the value of the IndexFilesForQualifiersCollection child.
	 *
	 * @return the value of the IndexFilesForQualifiersCollection child.
	 */
	@NotNull
	GenericAttributeValue<String> getIndexFilesForQualifiersCollection();


	/**
	 * Returns the value of the ProjectPriIndexName child.
	 *
	 * @return the value of the ProjectPriIndexName child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getProjectPriIndexName();


	/**
	 * Returns the value of the MappingFileFormat child.
	 *
	 * @return the value of the MappingFileFormat child.
	 */
	@NotNull
	GenericAttributeValue<String> getMappingFileFormat();


	/**
	 * Returns the value of the InsertReverseMap child.
	 *
	 * @return the value of the InsertReverseMap child.
	 */
	@NotNull
	GenericAttributeValue<String> getInsertReverseMap();


	/**
	 * Returns the value of the ProjectDirectory child.
	 *
	 * @return the value of the ProjectDirectory child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getProjectDirectory();


	/**
	 * Returns the value of the OutputFileName child.
	 *
	 * @return the value of the OutputFileName child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getOutputFileName();


	/**
	 * Returns the value of the MakePriExtensionPath child.
	 *
	 * @return the value of the MakePriExtensionPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getMakePriExtensionPath();


	/**
	 * Returns the value of the QualifiersPath child.
	 *
	 * @return the value of the QualifiersPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getQualifiersPath();


	/**
	 * Returns the value of the GeneratedFilesListPath child.
	 *
	 * @return the value of the GeneratedFilesListPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getGeneratedFilesListPath();


	/**
	 * Returns the value of the AdditionalMakepriExeParameters child.
	 *
	 * @return the value of the AdditionalMakepriExeParameters child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalMakepriExeParameters();


	/**
	 * Returns the value of the MultipleQualifiersPerDimensionFoundPath child.
	 *
	 * @return the value of the MultipleQualifiersPerDimensionFoundPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getMultipleQualifiersPerDimensionFoundPath();


	/**
	 * Returns the value of the IntermediateExtension child.
	 *
	 * @return the value of the IntermediateExtension child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getIntermediateExtension();


	/**
	 * Returns the value of the ExitCode child.
	 *
	 * @return the value of the ExitCode child.
	 */
	@NotNull
	GenericAttributeValue<String> getExitCode();


	/**
	 * Returns the value of the YieldDuringToolExecution child.
	 *
	 * @return the value of the YieldDuringToolExecution child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getYieldDuringToolExecution();


	/**
	 * Returns the value of the UseCommandProcessor child.
	 *
	 * @return the value of the UseCommandProcessor child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUseCommandProcessor();


	/**
	 * Returns the value of the EchoOff child.
	 *
	 * @return the value of the EchoOff child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getEchoOff();


	/**
	 * Returns the value of the ToolExe child.
	 *
	 * @return the value of the ToolExe child.
	 */
	@NotNull
	GenericAttributeValue<String> getToolExe();


	/**
	 * Returns the value of the ToolPath child.
	 *
	 * @return the value of the ToolPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getToolPath();


	/**
	 * Returns the value of the EnvironmentVariables child.
	 *
	 * @return the value of the EnvironmentVariables child.
	 */
	@NotNull
	GenericAttributeValue<String> getEnvironmentVariables();


	/**
	 * Returns the value of the Timeout child.
	 *
	 * @return the value of the Timeout child.
	 */
	@NotNull
	GenericAttributeValue<String> getTimeout();


	/**
	 * Returns the value of the StandardErrorImportance child.
	 *
	 * @return the value of the StandardErrorImportance child.
	 */
	@NotNull
	GenericAttributeValue<String> getStandardErrorImportance();


	/**
	 * Returns the value of the StandardOutputImportance child.
	 *
	 * @return the value of the StandardOutputImportance child.
	 */
	@NotNull
	GenericAttributeValue<String> getStandardOutputImportance();


	/**
	 * Returns the value of the LogStandardErrorAsError child.
	 *
	 * @return the value of the LogStandardErrorAsError child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getLogStandardErrorAsError();


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
