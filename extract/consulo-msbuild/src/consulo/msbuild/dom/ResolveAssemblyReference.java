// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:ResolveAssemblyReferenceElemType interface.
 *
 * @author VISTALL
 */
public interface ResolveAssemblyReference extends DomElement, Task
{

	/**
	 * Returns the value of the AllowedAssemblyExtensions child.
	 *
	 * @return the value of the AllowedAssemblyExtensions child.
	 */
	@NotNull
	GenericAttributeValue<String> getAllowedAssemblyExtensions();


	/**
	 * Returns the value of the AllowedGlobalAssemblyNamePrefix child.
	 *
	 * @return the value of the AllowedGlobalAssemblyNamePrefix child.
	 */
	@NotNull
	GenericAttributeValue<String> getAllowedGlobalAssemblyNamePrefix();


	/**
	 * Returns the value of the AllowedRelatedFileExtensions child.
	 *
	 * @return the value of the AllowedRelatedFileExtensions child.
	 */
	@NotNull
	GenericAttributeValue<String> getAllowedRelatedFileExtensions();


	/**
	 * Returns the value of the AppConfigFile child.
	 *
	 * @return the value of the AppConfigFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getAppConfigFile();


	/**
	 * Returns the value of the Assemblies child.
	 *
	 * @return the value of the Assemblies child.
	 */
	@NotNull
	GenericAttributeValue<String> getAssemblies();


	/**
	 * Returns the value of the AssemblyFiles child.
	 *
	 * @return the value of the AssemblyFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getAssemblyFiles();


	/**
	 * Returns the value of the AutoUnify child.
	 *
	 * @return the value of the AutoUnify child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getAutoUnify();


	/**
	 * Returns the value of the CandidateAssemblyFiles child.
	 *
	 * @return the value of the CandidateAssemblyFiles child.
	 */
	@NotNull
	GenericAttributeValue<String> getCandidateAssemblyFiles();


	/**
	 * Returns the value of the FilesWritten child.
	 *
	 * @return the value of the FilesWritten child.
	 */
	@NotNull
	GenericAttributeValue<String> getFilesWritten();


	/**
	 * Returns the value of the FindDependencies child.
	 *
	 * @return the value of the FindDependencies child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getFindDependencies();


	/**
	 * Returns the value of the FindRelatedFiles child.
	 *
	 * @return the value of the FindRelatedFiles child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getFindRelatedFiles();


	/**
	 * Returns the value of the FindSatellites child.
	 *
	 * @return the value of the FindSatellites child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getFindSatellites();


	/**
	 * Returns the value of the FindSerializationAssemblies child.
	 *
	 * @return the value of the FindSerializationAssemblies child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getFindSerializationAssemblies();


	/**
	 * Returns the value of the FullFrameworkAssemblyTables child.
	 *
	 * @return the value of the FullFrameworkAssemblyTables child.
	 */
	@NotNull
	GenericAttributeValue<String> getFullFrameworkAssemblyTables();


	/**
	 * Returns the value of the FullFrameworkFolders child.
	 *
	 * @return the value of the FullFrameworkFolders child.
	 */
	@NotNull
	GenericAttributeValue<String> getFullFrameworkFolders();


	/**
	 * Returns the value of the FullTargetFrameworkSubsetNames child.
	 *
	 * @return the value of the FullTargetFrameworkSubsetNames child.
	 */
	@NotNull
	GenericAttributeValue<String> getFullTargetFrameworkSubsetNames();


	/**
	 * Returns the value of the IgnoreDefaultInstalledAssemblySubsetTables child.
	 *
	 * @return the value of the IgnoreDefaultInstalledAssemblySubsetTables child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getIgnoreDefaultInstalledAssemblySubsetTables();


	/**
	 * Returns the value of the IgnoreDefaultInstalledAssemblyTables child.
	 *
	 * @return the value of the IgnoreDefaultInstalledAssemblyTables child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getIgnoreDefaultInstalledAssemblyTables();


	/**
	 * Returns the value of the InstalledAssemblySubsetTables child.
	 *
	 * @return the value of the InstalledAssemblySubsetTables child.
	 */
	@NotNull
	GenericAttributeValue<String> getInstalledAssemblySubsetTables();


	/**
	 * Returns the value of the InstalledAssemblyTables child.
	 *
	 * @return the value of the InstalledAssemblyTables child.
	 */
	@NotNull
	GenericAttributeValue<String> getInstalledAssemblyTables();


	/**
	 * Returns the value of the ProfileName child.
	 *
	 * @return the value of the ProfileName child.
	 */
	@NotNull
	GenericAttributeValue<String> getProfileName();


	/**
	 * Returns the value of the PublicKeysRestrictedForGlobalLocation child.
	 *
	 * @return the value of the PublicKeysRestrictedForGlobalLocation child.
	 */
	@NotNull
	GenericAttributeValue<String> getPublicKeysRestrictedForGlobalLocation();


	/**
	 * Returns the value of the SearchPaths child.
	 *
	 * @return the value of the SearchPaths child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getSearchPaths();


	/**
	 * Returns the value of the Silent child.
	 *
	 * @return the value of the Silent child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getSilent();


	/**
	 * Returns the value of the StateFile child.
	 *
	 * @return the value of the StateFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getStateFile();


	/**
	 * Returns the value of the TargetedRuntimeVersion child.
	 *
	 * @return the value of the TargetedRuntimeVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetedRuntimeVersion();


	/**
	 * Returns the value of the TargetFrameworkDirectories child.
	 *
	 * @return the value of the TargetFrameworkDirectories child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrameworkDirectories();


	/**
	 * Returns the value of the TargetFrameworkMoniker child.
	 *
	 * @return the value of the TargetFrameworkMoniker child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrameworkMoniker();


	/**
	 * Returns the value of the TargetFrameworkMonikerDisplayName child.
	 *
	 * @return the value of the TargetFrameworkMonikerDisplayName child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrameworkMonikerDisplayName();


	/**
	 * Returns the value of the TargetFrameworkSubsets child.
	 *
	 * @return the value of the TargetFrameworkSubsets child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrameworkSubsets();


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
