package org.mustbe.consulo.dotnet.dll.vfs.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Function;
import edu.arizona.cs.mbel.mbel.GenericParamDef;
import edu.arizona.cs.mbel.mbel.GenericParamOwner;
import edu.arizona.cs.mbel.mbel.MethodDef;
import edu.arizona.cs.mbel.mbel.Property;
import edu.arizona.cs.mbel.mbel.TypeDef;
import edu.arizona.cs.mbel.signature.MethodAttributes;
import edu.arizona.cs.mbel.signature.TypeAttributes;

/**
 * @author VISTALL
 * @since 12.12.13.
 */
public class StubToStringBuilder
{
	private static final char GENERIC_MARKER_IN_NAME = '`';
	private static final String CONSTRUCTOR_NAME = ".ctor";
	private static final String STATIC_CONSTRUCTOR_NAME = ".cctor";
	private static final String ARRAY_PROPERTY_NAME = "Item";

	private static Map<String, String> SPECIAL_METHOD_NAMES = new HashMap<String, String>()
	{
		{
			put("op_Addition", "+");
			put("op_Subtraction", "-");
			put("op_Multiply", "*");
			put("op_Division", "/");
			put("op_Modulus", "%");
			put("op_BitwiseAnd", "&");
			put("op_BitwiseOr", "|");
			put("op_ExclusiveOr", "^");
			put("op_LeftShift", "<<");
			put("op_RightShift", ">>");
			put("op_Equality", "==");
			put("op_Inequality", "!=");
			put("op_LessThan", "<");
			put("op_LessThanOrEqual", "<=");
			put("op_GreaterThan", ">");
			put("op_GreaterThanOrEqual", ">=");
			put("op_OnesComplement", "~");
			put("op_LogicalNot", "!");
		}
	};

	private TypeDef myTypeDef;
	private StubBlock myRoot;

	public StubToStringBuilder(TypeDef typeDef)
	{
		myTypeDef = typeDef;

		StubBlock namespaceBlock = processNamespace();
		if(namespaceBlock != null)
		{
			myRoot = namespaceBlock;
		}

		StubBlock typeBlock = processType();
		if(namespaceBlock != null)
		{
			namespaceBlock.getBlocks().add(typeBlock);
		}
		else
		{
			myRoot = typeBlock;
		}
	}

	private String getUserTypeDefName()
	{
		String name = myTypeDef.getName();
		int i = name.lastIndexOf(GENERIC_MARKER_IN_NAME);
		if(i > 0)
		{
			name = name.substring(0, i);
		}
		return name;
	}

	@NotNull
	private StubBlock processType()
	{
		StringBuilder builder =  new StringBuilder();
		if(isSet(myTypeDef.getFlags(), TypeAttributes.VisibilityMask, TypeAttributes.Public))
		{
			builder.append("public ");
		}
		else
		{
			builder.append("internal ");
		}

		if(isSet(myTypeDef.getFlags(), TypeAttributes.Sealed))
		{
			builder.append("sealed ");
		}

		if(isSet(myTypeDef.getFlags(), TypeAttributes.Abstract))
		{
			builder.append("abstract ");
		}

		if(myTypeDef.isEnum())
		{
			builder.append("enum ");
		}
		else if(myTypeDef.isValueType())
		{
			builder.append("struct ");
		}
		else if(isSet(myTypeDef.getFlags(), TypeAttributes.Interface))
		{
			builder.append("interface ");
		}
		else
		{
			builder.append("class ");
		}
		builder.append(getUserTypeDefName());

		processGenericParameterList(myTypeDef, builder);

		StubBlock stubBlock = new StubBlock(builder.toString(), '{', '}');
		processMembers(stubBlock);
		return stubBlock;
	}

	private void processMembers(StubBlock parent)
	{
		for(Property property : myTypeDef.getProperties())
		{
			StubBlock stubBlock = processProperty(property);

			parent.getBlocks().add(stubBlock);
		}

		for(MethodDef methodDef : myTypeDef.getMethods())
		{
			StubBlock stubBlock = processMethod(methodDef);

			if(stubBlock == null)
			{
				continue;
			}
			parent.getBlocks().add(stubBlock);
		}
	}

	private StubBlock processProperty(Property property)
	{
		StringBuilder builder = new StringBuilder();

		builder.append("int");
		builder.append(" ");
		builder.append(property.getName());

		StubBlock stubBlock = new StubBlock(builder.toString(), '{', '}');
		return stubBlock;
	}

	private StubBlock processMethod(MethodDef methodDef)
	{
		String name = methodDef.getName();
		if(isSet(methodDef.getFlags(), MethodAttributes.SpecialName))
		{
			// dont show properties methods
			if(name.startsWith("get_") || name.startsWith("set_") || name.equals(STATIC_CONSTRUCTOR_NAME))
			{
				return null;
			}

			String operator = SPECIAL_METHOD_NAMES.get(name);
			if(operator != null)
			{
				name = operator;
			}
		}

		StringBuilder builder = new StringBuilder();

		if(name.equals(CONSTRUCTOR_NAME))
		{
			builder.append(getUserTypeDefName());
		}
		else
		{
			if(SPECIAL_METHOD_NAMES.containsValue(name))
			{
				builder.append("operator ");
			}

			builder.append(name);
		}

		processGenericParameterList(methodDef, builder);

		builder.append("()");

		StubBlock stubBlock = new StubBlock(builder.toString(), '{', '}');
		return stubBlock;
	}

	private static void processGenericParameterList(GenericParamOwner owner, StringBuilder builder)
	{
		List<GenericParamDef> genericParams = owner.getGenericParams();
		if(genericParams.isEmpty())
		{
			return;
		}

		String text = StringUtil.join(genericParams, new Function<GenericParamDef, String>()
		{
			@Override
			public String fun(GenericParamDef genericParamDef)
			{
				return genericParamDef.getName();
			}
		}, ", ");
		builder.append("<").append(text).append(">");
	}

	private boolean isSet(long value, int mod)
	{
		return (value & mod) == mod;
	}

	private boolean isSet(long value, int mod, int v)
	{
		return (value & mod) == v;
	}

	@Nullable
	private StubBlock processNamespace()
	{
		String namespace = myTypeDef.getNamespace();
		if(StringUtil.isEmpty(namespace))
		{
			return null;
		}

		return new StubBlock("namespace " + namespace, '{', '}');
	}

	@NotNull
	public String gen()
	{
		assert myRoot != null;

		StringBuilder builder = new StringBuilder();
		builder.append("// Generated by decompiler from Consulo .NET plugin\n\n");

		processBlock(builder, myRoot, 0);

		return builder.toString();
	}

	private static void processBlock(StringBuilder builder, StubBlock root, int index)
	{
		builder.append(StringUtil.repeatSymbol('\t', index));
		builder.append(root.getStartText());
		builder.append('\n');
		builder.append(StringUtil.repeatSymbol('\t', index));
		builder.append(root.getIndents()[0]);
		builder.append('\n');

		List<StubBlock> blocks = root.getBlocks();
		for(int i = 0; i < blocks.size(); i++)
		{
			if(i != 0)
			{
				builder.append('\n');
			}
			StubBlock stubBlock = blocks.get(i);
			processBlock(builder, stubBlock, index + 1);
		}

		builder.append(StringUtil.repeatSymbol('\t', index));
		builder.append(root.getIndents()[1]);
		builder.append('\n');
	}
}
