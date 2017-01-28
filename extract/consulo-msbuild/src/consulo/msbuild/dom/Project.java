// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DefinesXml;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.NameStrategy;
import com.intellij.util.xml.NameStrategyForAttributes;
import com.intellij.util.xml.SubTagList;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:ProjectElemType interface.
 *
 * @author VISTALL
 */
@DefinesXml
@NameStrategy(MSBuildNameStrategy.class)
@NameStrategyForAttributes(MSBuildNameStrategy.class)
public interface Project extends DomElement
{

	/**
	 * Returns the value of the DefaultTargets child.
	 * <pre>
	 * <h3>Attribute null:DefaultTargets documentation</h3>
	 * <!-- _locID_text="Project_DefaultTargets" _locComment="" -->Optional semi-colon separated list of one or more targets that will be built if no targets are otherwise specified
	 * </pre>
	 *
	 * @return the value of the DefaultTargets child.
	 */
	@NotNull
	GenericAttributeValue<String> getDefaultTargets();


	/**
	 * Returns the value of the InitialTargets child.
	 * <pre>
	 * <h3>Attribute null:InitialTargets documentation</h3>
	 * <!-- _locID_text="Project_InitialTargets" _locComment="" -->Optional semi-colon separated list of targets that should always be built before any other targets
	 * </pre>
	 *
	 * @return the value of the InitialTargets child.
	 */
	@NotNull
	GenericAttributeValue<String> getInitialTargets();


	/**
	 * Returns the value of the ToolsVersion child.
	 * <pre>
	 * <h3>Attribute null:ToolsVersion documentation</h3>
	 * <!-- _locID_text="Project_ToolsVersion" _locComment="" -->Optional string describing the toolset version this project should normally be built with
	 * </pre>
	 *
	 * @return the value of the ToolsVersion child.
	 */
	@NotNull
	GenericAttributeValue<String> getToolsVersion();


	/**
	 * Returns the list of PropertyGroup children.
	 *
	 * @return the list of PropertyGroup children.
	 */
	@NotNull
	@SubTagList("PropertyGroup")
	List<PropertyGroup> getPropertyGroups();

	/**
	 * Adds new child to the list of PropertyGroup children.
	 *
	 * @return created child
	 */
	@SubTagList("PropertyGroup")
	PropertyGroup addPropertyGroup();


	/**
	 * Returns the list of ItemGroup children.
	 *
	 * @return the list of ItemGroup children.
	 */
	@NotNull
	@SubTagList("ItemGroup")
	List<ItemGroup> getItemGroups();

	/**
	 * Adds new child to the list of ItemGroup children.
	 *
	 * @return created child
	 */
	@SubTagList("ItemGroup")
	ItemGroup addItemGroup();


	/**
	 * Returns the list of ItemDefinitionGroup children.
	 *
	 * @return the list of ItemDefinitionGroup children.
	 */
	@NotNull
	@SubTagList("ItemDefinitionGroup")
	List<ItemDefinitionGroup> getItemDefinitionGroups();

	/**
	 * Adds new child to the list of ItemDefinitionGroup children.
	 *
	 * @return created child
	 */
	@SubTagList("ItemDefinitionGroup")
	ItemDefinitionGroup addItemDefinitionGroup();


	/**
	 * Returns the list of Choose children.
	 *
	 * @return the list of Choose children.
	 */
	@NotNull
	@SubTagList("Choose")
	List<Choose> getChooses();

	/**
	 * Adds new child to the list of Choose children.
	 *
	 * @return created child
	 */
	@SubTagList("Choose")
	Choose addChoose();


	/**
	 * Returns the list of UsingTask children.
	 *
	 * @return the list of UsingTask children.
	 */
	@NotNull
	@SubTagList("UsingTask")
	List<UsingTask> getUsingTasks();

	/**
	 * Adds new child to the list of UsingTask children.
	 *
	 * @return created child
	 */
	@SubTagList("UsingTask")
	UsingTask addUsingTask();


	/**
	 * Returns the list of ProjectExtensions children.
	 * <pre>
	 * <h3>Type http://schemas.microsoft.com/developer/msbuild/2003:ProjectExtensionsType documentation</h3>
	 * <!-- _locID_text="ProjectExtensionsType" _locComment="" -->Optional section used by MSBuild hosts, that may contain arbitrary XML content that is ignored by MSBuild itself
	 * </pre>
	 *
	 * @return the list of ProjectExtensions children.
	 */
	@NotNull
	@SubTagList("ProjectExtensions")
	List<GenericDomValue<String>> getProjectExtensionses();

	/**
	 * Adds new child to the list of ProjectExtensions children.
	 *
	 * @return created child
	 */
	@SubTagList("ProjectExtensions")
	GenericDomValue<String> addProjectExtensions();


	/**
	 * Returns the list of Target children.
	 *
	 * @return the list of Target children.
	 */
	@NotNull
	List<Target> getTargets();

	/**
	 * Adds new child to the list of Target children.
	 *
	 * @return created child
	 */
	Target addTarget();


	/**
	 * Returns the list of Import children.
	 *
	 * @return the list of Import children.
	 */
	@NotNull
	List<Import> getImports();

	/**
	 * Adds new child to the list of Import children.
	 *
	 * @return created child
	 */
	Import addImport();


	/**
	 * Returns the list of ImportGroup children.
	 *
	 * @return the list of ImportGroup children.
	 */
	@NotNull
	List<ImportGroup> getImportGroups();

	/**
	 * Adds new child to the list of ImportGroup children.
	 *
	 * @return created child
	 */
	ImportGroup addImportGroup();

}
