// Generated on Sat Jan 28 04:58:19 MSK 2017
// DTD/Schema  :    http://schemas.microsoft.com/developer/msbuild/2003

package consulo.msbuild.dom;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * http://schemas.microsoft.com/developer/msbuild/2003:ItemDefinitionGroupType interface.
 * <pre>
 * <h3>Type http://schemas.microsoft.com/developer/msbuild/2003:ItemDefinitionGroupType documentation</h3>
 * <!-- _locID_text="ItemDefinitionGroupType" _locComment="" -->Groups item metadata definitions
 * </pre>
 *
 * @author VISTALL
 */
public interface ItemDefinitionGroup extends DomElement
{

	/**
	 * Returns the value of the Condition child.
	 * <pre>
	 * <h3>Attribute null:Condition documentation</h3>
	 * <!-- _locID_text="ItemDefinitionGroupType_Condition" _locComment="" -->Optional expression evaluated to determine whether the ItemDefinitionGroup should be used
	 * </pre>
	 *
	 * @return the value of the Condition child.
	 */
	@NotNull
	GenericAttributeValue<String> getCondition();


	/**
	 * Returns the value of the Label child.
	 * <pre>
	 * <h3>Attribute null:Label documentation</h3>
	 * <!-- _locID_text="ItemDefinitionGroupType_Label" _locComment="" -->Optional expression. Used to identify or order system and user elements
	 * </pre>
	 *
	 * @return the value of the Label child.
	 */
	@NotNull
	GenericAttributeValue<String> getLabel();


	/**
	 * Returns the list of Item children.
	 *
	 * @return the list of Item children.
	 */
	@NotNull
	List<SimpleItem> getItems();

	/**
	 * Adds new child to the list of Item children.
	 *
	 * @return created child
	 */
	SimpleItem addItem();


	/**
	 * Returns the list of Link children.
	 *
	 * @return the list of Link children.
	 */
	@NotNull
	List<LinkItem> getLinks();

	/**
	 * Adds new child to the list of Link children.
	 *
	 * @return created child
	 */
	LinkItem addLink();


	/**
	 * Returns the list of ResourceCompile children.
	 *
	 * @return the list of ResourceCompile children.
	 */
	@NotNull
	List<ResourceCompile> getResourceCompiles();

	/**
	 * Adds new child to the list of ResourceCompile children.
	 *
	 * @return created child
	 */
	ResourceCompile addResourceCompile();


	/**
	 * Returns the list of PreBuildEvent children.
	 *
	 * @return the list of PreBuildEvent children.
	 */
	@NotNull
	List<PreBuildEventItem> getPreBuildEvents();

	/**
	 * Adds new child to the list of PreBuildEvent children.
	 *
	 * @return created child
	 */
	PreBuildEventItem addPreBuildEvent();


	/**
	 * Returns the list of PostBuildEvent children.
	 *
	 * @return the list of PostBuildEvent children.
	 */
	@NotNull
	List<PostBuildEventItem> getPostBuildEvents();

	/**
	 * Adds new child to the list of PostBuildEvent children.
	 *
	 * @return created child
	 */
	PostBuildEventItem addPostBuildEvent();


}
