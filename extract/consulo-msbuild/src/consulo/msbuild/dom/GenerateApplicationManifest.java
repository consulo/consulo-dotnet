// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:GenerateApplicationManifestElemType interface.
 *
 * @author VISTALL
 */
public interface GenerateApplicationManifest extends DomElement, Task
{

	/**
	 * Returns the value of the AssemblyName child.
	 *
	 * @return the value of the AssemblyName child.
	 */
	@NotNull
	GenericAttributeValue<String> getAssemblyName();


	/**
	 * Returns the value of the AssemblyVersion child.
	 *
	 * @return the value of the AssemblyVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getAssemblyVersion();


	/**
	 * Returns the value of the ClrVersion child.
	 *
	 * @return the value of the ClrVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getClrVersion();


	/**
	 * Returns the value of the ConfigFile child.
	 *
	 * @return the value of the ConfigFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getConfigFile();


	/**
	 * Returns the value of the Dependencies child.
	 *
	 * @return the value of the Dependencies child.
	 */
	@NotNull
	GenericAttributeValue<String> getDependencies();


	/**
	 * Returns the value of the Description child.
	 *
	 * @return the value of the Description child.
	 */
	@NotNull
	GenericAttributeValue<String> getDescription();


	/**
	 * Returns the value of the EntryPoint child.
	 *
	 * @return the value of the EntryPoint child.
	 */
	@NotNull
	GenericAttributeValue<String> getEntryPoint();


	/**
	 * Returns the value of the ErrorReportUrl child.
	 *
	 * @return the value of the ErrorReportUrl child.
	 */
	@NotNull
	GenericAttributeValue<String> getErrorReportUrl();


	/**
	 * Returns the value of the FileAssociations child.
	 *
	 * @return the value of the FileAssociations child.
	 */
	@NotNull
	GenericAttributeValue<String> getFileAssociations();


	/**
	 * Returns the value of the Files child.
	 *
	 * @return the value of the Files child.
	 */
	@NotNull
	GenericAttributeValue<String> getFiles();


	/**
	 * Returns the value of the HostInBrowser child.
	 *
	 * @return the value of the HostInBrowser child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getHostInBrowser();


	/**
	 * Returns the value of the IconFile child.
	 *
	 * @return the value of the IconFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getIconFile();


	/**
	 * Returns the value of the InputManifest child.
	 *
	 * @return the value of the InputManifest child.
	 */
	@NotNull
	GenericAttributeValue<String> getInputManifest();


	/**
	 * Returns the value of the IsolatedComReferences child.
	 *
	 * @return the value of the IsolatedComReferences child.
	 */
	@NotNull
	GenericAttributeValue<String> getIsolatedComReferences();


	/**
	 * Returns the value of the ManifestType child.
	 *
	 * @return the value of the ManifestType child.
	 */
	@NotNull
	GenericAttributeValue<String> getManifestType();


	/**
	 * Returns the value of the MaxTargetPath child.
	 *
	 * @return the value of the MaxTargetPath child.
	 */
	@NotNull
	GenericAttributeValue<String> getMaxTargetPath();


	/**
	 * Returns the value of the OSVersion child.
	 *
	 * @return the value of the OSVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getOSVersion();


	/**
	 * Returns the value of the OutputManifest child.
	 *
	 * @return the value of the OutputManifest child.
	 */
	@NotNull
	GenericAttributeValue<String> getOutputManifest();


	/**
	 * Returns the value of the Platform child.
	 *
	 * @return the value of the Platform child.
	 */
	@NotNull
	GenericAttributeValue<String> getPlatform();


	/**
	 * Returns the value of the Product child.
	 *
	 * @return the value of the Product child.
	 */
	@NotNull
	GenericAttributeValue<String> getProduct();


	/**
	 * Returns the value of the Publisher child.
	 *
	 * @return the value of the Publisher child.
	 */
	@NotNull
	GenericAttributeValue<String> getPublisher();


	/**
	 * Returns the value of the RequiresMinimumFramework35SP1 child.
	 *
	 * @return the value of the RequiresMinimumFramework35SP1 child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getRequiresMinimumFramework35SP1();


	/**
	 * Returns the value of the SuiteName child.
	 *
	 * @return the value of the SuiteName child.
	 */
	@NotNull
	GenericAttributeValue<String> getSuiteName();


	/**
	 * Returns the value of the SupportUrl child.
	 *
	 * @return the value of the SupportUrl child.
	 */
	@NotNull
	GenericAttributeValue<String> getSupportUrl();


	/**
	 * Returns the value of the TargetCulture child.
	 *
	 * @return the value of the TargetCulture child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetCulture();


	/**
	 * Returns the value of the TargetFrameworkMoniker child.
	 *
	 * @return the value of the TargetFrameworkMoniker child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrameworkMoniker();


	/**
	 * Returns the value of the TargetFrameworkProfile child.
	 *
	 * @return the value of the TargetFrameworkProfile child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrameworkProfile();


	/**
	 * Returns the value of the TargetFrameworkSubset child.
	 *
	 * @return the value of the TargetFrameworkSubset child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrameworkSubset();


	/**
	 * Returns the value of the TargetFrameworkVersion child.
	 *
	 * @return the value of the TargetFrameworkVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getTargetFrameworkVersion();


	/**
	 * Returns the value of the TrustInfoFile child.
	 *
	 * @return the value of the TrustInfoFile child.
	 */
	@NotNull
	GenericAttributeValue<String> getTrustInfoFile();


	/**
	 * Returns the value of the UseApplicationTrust child.
	 *
	 * @return the value of the UseApplicationTrust child.
	 */
	@NotNull
	GenericAttributeValue<Boolean> getUseApplicationTrust();


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
