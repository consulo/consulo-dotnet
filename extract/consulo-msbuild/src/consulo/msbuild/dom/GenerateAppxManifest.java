// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:GenerateAppxManifestElemType interface.
 *
 * @author VISTALL
 */
public interface GenerateAppxManifest extends DomElement, Task
{

	/**
	 * Returns the value of the ApplicationExecutableName child.
	 *
	 * @return the value of the ApplicationExecutableName child.
	 */
	@NotNull
	GenericAttributeValue<String> getApplicationExecutableName();


	/**
	 * Returns the value of the AppxManifestInput child.
	 *
	 * @return the value of the AppxManifestInput child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getAppxManifestInput();


	/**
	 * Returns the value of the CertificateThumbprint child.
	 *
	 * @return the value of the CertificateThumbprint child.
	 */
	@NotNull
	GenericAttributeValue<String> getCertificateThumbprint();


	/**
	 * Returns the value of the CertificateFile child.
	 *
	 * @return the value of the CertificateFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getCertificateFile();


	/**
	 * Returns the value of the PackageArchitecture child.
	 *
	 * @return the value of the PackageArchitecture child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getPackageArchitecture();


	/**
	 * Returns the value of the FrameworkSdkReferences child.
	 *
	 * @return the value of the FrameworkSdkReferences child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getFrameworkSdkReferences();


	/**
	 * Returns the value of the NonFrameworkSdkReferences child.
	 *
	 * @return the value of the NonFrameworkSdkReferences child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getNonFrameworkSdkReferences();


	/**
	 * Returns the value of the AppxManifestOutput child.
	 *
	 * @return the value of the AppxManifestOutput child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getAppxManifestOutput();


	/**
	 * Returns the value of the DefaultResourceLanguage child.
	 *
	 * @return the value of the DefaultResourceLanguage child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getDefaultResourceLanguage();


	/**
	 * Returns the value of the QualifiersPath child.
	 *
	 * @return the value of the QualifiersPath child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getQualifiersPath();


	/**
	 * Returns the value of the ManagedWinmdInprocImplementation child.
	 *
	 * @return the value of the ManagedWinmdInprocImplementation child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getManagedWinmdInprocImplementation();


	/**
	 * Returns the value of the WinmdFiles child.
	 *
	 * @return the value of the WinmdFiles child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getWinmdFiles();


	/**
	 * Returns the value of the SDKWinmdFiles child.
	 *
	 * @return the value of the SDKWinmdFiles child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getSDKWinmdFiles();


	/**
	 * Returns the value of the OSMinVersion child.
	 *
	 * @return the value of the OSMinVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getOSMinVersion();


	/**
	 * Returns the value of the OSMaxVersionTested child.
	 *
	 * @return the value of the OSMaxVersionTested child.
	 */
	@NotNull
	GenericAttributeValue<String> getOSMaxVersionTested();


	/**
	 * Returns the value of the OSMinVersionReplaceManifestVersion child.
	 *
	 * @return the value of the OSMinVersionReplaceManifestVersion child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOSMinVersionReplaceManifestVersion();


	/**
	 * Returns the value of the OSMaxVersionTestedReplaceManifestVersion child.
	 *
	 * @return the value of the OSMaxVersionTestedReplaceManifestVersion child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getOSMaxVersionTestedReplaceManifestVersion();


	/**
	 * Returns the value of the EnableSigningChecks child.
	 *
	 * @return the value of the EnableSigningChecks child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getEnableSigningChecks();


	/**
	 * Returns the value of the ManifestMetadata child.
	 *
	 * @return the value of the ManifestMetadata child.
	 */
	@NotNull
	GenericAttributeValue<String> getManifestMetadata();


	/**
	 * Returns the value of the TargetPlatformIdentifier child.
	 *
	 * @return the value of the TargetPlatformIdentifier child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetPlatformIdentifier();


	/**
	 * Returns the value of the PackageSigningEnabled child.
	 *
	 * @return the value of the PackageSigningEnabled child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getPackageSigningEnabled();


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
