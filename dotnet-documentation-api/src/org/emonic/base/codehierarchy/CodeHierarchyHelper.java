package org.emonic.base.codehierarchy;

import org.emonic.base.documentation.IDocumentation;

/**
 * Helper Functions used mainly in the namespace CodeHierarchy
 *
 * @author bb
 */
public class CodeHierarchyHelper
{

	/**
	 * Convert the signature to simpler form
	 *
	 * @param signature
	 * @return
	 */
	public static String convertSignature(String signature)
	{
		signature = signature.replaceAll("System.Boolean", "bool");
		signature = signature.replaceAll("System.Char", "char");
		signature = signature.replaceAll("System.SByte", "sbyte");
		signature = signature.replaceAll("System.Byte", "byte");
		signature = signature.replaceAll("System.Int16", "short");
		signature = signature.replaceAll("System.UInt16", "ushort");
		signature = signature.replaceAll("System.Int32", "int");
		signature = signature.replaceAll("System.UInt32", "uint");
		signature = signature.replaceAll("System.Int64", "long");
		signature = signature.replaceAll("System.UInt64", "ulong");
		signature = signature.replaceAll("System.Single", "float");
		signature = signature.replaceAll("System.Double", "double");
		signature = signature.replaceAll("System.String", "string");
		signature = signature.replaceAll("System.Object", "object");
		return signature;
	}

	public static String getFormText(IDocumentation documentation)
	{
		if(documentation == null)
		{
			return null;
		}
		StringBuffer buffer = new StringBuffer("<form>"); //$NON-NLS-1$
		synchronized(buffer)
		{
			String doc = documentation.getSummary();
			if(doc != null)
			{
				buffer.append("<p>"); //$NON-NLS-1$
				buffer.append("<b>Summary:</b>").append("<br/>").append(doc);
				buffer.append("</p>"); //$NON-NLS-1$
			}

			doc = documentation.getValue();
			if(doc != null)
			{
				buffer.append("<p>"); //$NON-NLS-1$
				buffer.append("<br/>"); //$NON-NLS-1$
				buffer.append("<b>Value:</b>").append("<br/>").append(doc);
				buffer.append("</p>"); //$NON-NLS-1$
			}

			IDocumentation[] params = documentation.getParams();
			if(params != null && params.length != 0)
			{
				buffer.append("<p>"); //$NON-NLS-1$
				buffer.append("<br/>"); //$NON-NLS-1$
				buffer.append("<b>Parameters:</b>");
				for(int i = 0; i < params.length; i++)
				{
					buffer.append("<br/>"); //$NON-NLS-1$
					buffer.append('\t');
					buffer.append("<b>").append(params[i].getName()).append( //$NON-NLS-1$
							"</b>"); //$NON-NLS-1$
					String summary = params[i].getSummary();
					if(summary != null)
					{
						buffer.append(" - ").append(params[i].getSummary()); //$NON-NLS-1$
					}
				}
				buffer.append("</p>"); //$NON-NLS-1$
			}

			doc = documentation.getReturns();
			if(doc != null)
			{
				buffer.append("<p>"); //$NON-NLS-1$
				buffer.append("<br/>"); //$NON-NLS-1$
				buffer.append("<b>Returns:</b>").append("<br/>").append(doc);
				buffer.append("</p>"); //$NON-NLS-1$
			}

			doc = documentation.getRemarks();
			if(doc != null)
			{
				buffer.append("<p>"); //$NON-NLS-1$
				buffer.append("<br/>"); //$NON-NLS-1$
				buffer.append("<b>Remarks:</b>").append("<br/>").append(doc);
				buffer.append("</p>"); //$NON-NLS-1$
			}

			doc = documentation.getSeeAlso();
			if(doc != null)
			{
				buffer.append("<p>"); //$NON-NLS-1$
				buffer.append("<br/>"); //$NON-NLS-1$
				buffer.append("<b>See Also:</b>").append("<br/>").append(doc);
				buffer.append("</p>"); //$NON-NLS-1$
			}

			if(buffer.length() == 6)
			{
				return null;
			}
			buffer.append("</form>"); //$NON-NLS-1$
			return buffer.toString();
		}
	}
}
