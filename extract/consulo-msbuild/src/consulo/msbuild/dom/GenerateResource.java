// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:GenerateResourceElemType interface.
 *
 * @author VISTALL
 */
public interface GenerateResource extends DomElement, Task
{

	/**
	 * Returns the value of the AdditionalInputs child.
	 *
	 * @return the value of the AdditionalInputs child.
	 */
	@NotNull
	GenericAttributeValue<String> getAdditionalInputs();


	/**
	 * Returns the value of the ExcludedInputPaths child.
	 *
	 * @return the value of the ExcludedInputPaths child.
	 */
	@NotNull
	GenericAttributeValue<String> getExcludedInputPaths();


	/**
	 * Returns the value of the ExecuteAsTool child.
	 *
	 * @return the value of the ExecuteAsTool child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getExecuteAsTool();


	/**
	 * Returns the value of the MinimalRebuildFromTracking child.
	 *
	 * @return the value of the MinimalRebuildFromTracking child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getMinimalRebuildFromTracking();


	/**
	 * Returns the value of the NeverLockTypeAssemblies child.
	 *
	 * @return the value of the NeverLockTypeAssemblies child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getNeverLockTypeAssemblies();


	/**
	 * Returns the value of the OutputResources child.
	 *
	 * @return the value of the OutputResources child.
	 */
	@NotNull
	GenericAttributeValue<String> getOutputResources();


	/**
	 * Returns the value of the PublicClass child.
	 *
	 * @return the value of the PublicClass child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getPublicClass();


	/**
	 * Returns the value of the References child.
	 *
	 * @return the value of the References child.
	 */
	@NotNull
	GenericAttributeValue<String> getReferences();


	/**
	 * Returns the value of the SdkToolsPath child.
	 *
	 * @return the value of the SdkToolsPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getSdkToolsPath();


	/**
	 * Returns the value of the Sources child.
	 *
	 * @return the value of the Sources child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getSources();


	/**
	 * Returns the value of the StateFile child.
	 *
	 * @return the value of the StateFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getStateFile();


	/**
	 * Returns the value of the StronglyTypedClassName child.
	 *
	 * @return the value of the StronglyTypedClassName child.
	 */
	@NotNull
	GenericAttributeValue<String> getStronglyTypedClassName();


	/**
	 * Returns the value of the StronglyTypedFileName child.
	 *
	 * @return the value of the StronglyTypedFileName child.
	 */
	@NotNull
	GenericAttributeValue<String> getStronglyTypedFileName();


	/**
	 * Returns the value of the StronglyTypedLanguage child.
	 *
	 * @return the value of the StronglyTypedLanguage child.
	 */
	@NotNull
	GenericAttributeValue<String> getStronglyTypedLanguage();


	/**
	 * Returns the value of the StronglyTypedManifestPrefix child.
	 *
	 * @return the value of the StronglyTypedManifestPrefix child.
	 */
	@NotNull
	GenericAttributeValue<String> getStronglyTypedManifestPrefix();


	/**
	 * Returns the value of the StronglyTypedNamespace child.
	 *
	 * @return the value of the StronglyTypedNamespace child.
	 */
	@NotNull
	GenericAttributeValue<String> getStronglyTypedNamespace();


	/**
	 * Returns the value of the TrackerLogDirectory child.
	 *
	 * @return the value of the TrackerLogDirectory child.
	 */
	@NotNull
	GenericAttributeValue<String> getTrackerLogDirectory();


	/**
	 * Returns the value of the TrackFileAccess child.
	 *
	 * @return the value of the TrackFileAccess child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getTrackFileAccess();


	/**
	 * Returns the value of the UseSourcePath child.
	 *
	 * @return the value of the UseSourcePath child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUseSourcePath();


	/**
	 * Returns the value of the ExtractResWFiles child.
	 *
	 * @return the value of the ExtractResWFiles child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getExtractResWFiles();


	/**
	 * Returns the value of the OutputDirectory child.
	 *
	 * @return the value of the OutputDirectory child.
	 */
	@NotNull
	GenericAttributeValue<String> getOutputDirectory();


	/**
	 * Returns the value of the MSBuildRuntime child.
	 *
	 * @return the value of the MSBuildRuntime child.
	 */
	@NotNull
	GenericAttributeValue<String> getMSBuildRuntime();


	/**
	 * Returns the value of the MSBuildArchitecture child.
	 *
	 * @return the value of the MSBuildArchitecture child.
	 */
	@NotNull
	GenericAttributeValue<String> getMSBuildArchitecture();


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
