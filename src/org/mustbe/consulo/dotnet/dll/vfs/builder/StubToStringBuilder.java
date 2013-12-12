package org.mustbe.consulo.dotnet.dll.vfs.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetClasses;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Function;
import edu.arizona.cs.mbel.mbel.GenericParamDef;
import edu.arizona.cs.mbel.mbel.GenericParamOwner;
import edu.arizona.cs.mbel.mbel.MethodDef;
import edu.arizona.cs.mbel.mbel.Property;
import edu.arizona.cs.mbel.mbel.TypeDef;
import edu.arizona.cs.mbel.mbel.TypeRef;
import edu.arizona.cs.mbel.signature.MethodAttributes;
import edu.arizona.cs.mbel.signature.ParameterSignature;
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

	private static List<String> SKIPPED = new ArrayList<String>()
	{
		{
			add("System.Runtime.Serialization.ISerializable.GetObjectData");
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

	// System.MulticastDelegate
	// Invoke
	@NotNull
	private StubBlock processType()
	{
		TypeRef superClass = myTypeDef.getSuperClass();
		if(superClass != null && Comparing.equal(DotNetClasses.System_MulticastDelegate, superClass.getFullName()))
		{
			for(MethodDef methodDef : myTypeDef.getMethods())
			{
				if("Invoke".equals(methodDef.getName()))
				{
					return processMethod(methodDef, getUserTypeDefName(), true);
				}

			}
			assert true;
		}

		StringBuilder builder = new StringBuilder();
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

		StubBlock stubBlock = new StubBlock(builder.toString(), null, '{', '}');
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
			StubBlock stubBlock = processMethod(methodDef, methodDef.getName(), false);

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

		builder.append(TypeBuilder.typeToString(property.getSignature().getType()));
		builder.append(" ");
		builder.append(property.getName());

		StubBlock stubBlock = new StubBlock(builder.toString(), null, '{', '}');
		return stubBlock;
	}

	private StubBlock processMethod(MethodDef methodDef, String name, boolean delegate)
	{
		for(String prefix : SKIPPED)
		{
			if(StringUtil.equals(name, prefix))
			{
				return null;
			}
		}

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

		if(isSet(methodDef.getFlags(), MethodAttributes.MemberAccessMask, MethodAttributes.Public))
		{
			builder.append("public ");
		}
		else if(isSet(methodDef.getFlags(), MethodAttributes.MemberAccessMask, MethodAttributes.Private))
		{
			builder.append("private ");

		}
		else
		{
			builder.append("internal ");
		}

		boolean noBody = false;
		if(delegate)
		{
			builder.append("delegate ");
			noBody = true;
		}
		else
		{
			if(isSet(methodDef.getFlags(), MethodAttributes.Abstract))
			{
				builder.append("abstract ");
				noBody = true;
			}

			if(isSet(methodDef.getFlags(), MethodAttributes.Static))
			{
				builder.append("static ");
			}

			if(isSet(methodDef.getFlags(), MethodAttributes.Virtual))
			{
				builder.append("virtual ");
			}
		}

		if(isSet(methodDef.getFlags(), MethodAttributes.Final))
		{
			//builder.append("final "); //TODO [VISTALL] final  ? maybe sealed ?
		}

		if(name.equals(CONSTRUCTOR_NAME))
		{
			builder.append(getUserTypeDefName());
		}
		else
		{
			builder.append(TypeBuilder.typeToString(methodDef.getSignature().getReturnType().getType())).append(" ");

			if(SPECIAL_METHOD_NAMES.containsValue(name))
			{
				builder.append("operator ");
			}

			builder.append(name);
		}

		processGenericParameterList(methodDef, builder);

		processParameterList(methodDef.getSignature().getParameters(), builder);

		if(noBody)
		{
			return new StubBlock(builder.toString(), null);
		}
		else
		{
			return new StubBlock(builder.toString(), "// Bodies decompilation is not supported\n", '{', '}');
		}
	}

	private static void processParameterList(ParameterSignature[] owner, StringBuilder builder)
	{
		String text = StringUtil.join(owner, new Function<ParameterSignature, String>()
		{
			@Override
			public String fun(ParameterSignature paramDef)
			{
				StringBuilder p = new StringBuilder();
				p.append(TypeBuilder.typeToString(paramDef.getType()));
				p.append(" ");
				p.append(paramDef.getParameterInfo().getName());
				return p.toString();
			}
		}, ", ");
		builder.append("(").append(text).append(")");
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

		return new StubBlock("namespace " + namespace, null, '{', '}');
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
		char[] indents = root.getIndents();

		if(indents.length > 0)
		{
			builder.append('\n');
			builder.append(StringUtil.repeatSymbol('\t', index));
			builder.append(indents[0]);
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

			String innerText = root.getInnerText();
			if(innerText != null)
			{
				builder.append(StringUtil.repeatSymbol('\t', index + 1)).append(innerText);
			}

			builder.append(StringUtil.repeatSymbol('\t', index));
			builder.append(indents[1]);
		}
		else
		{
			builder.append(";");
		}
		builder.append('\n');
	}
}
