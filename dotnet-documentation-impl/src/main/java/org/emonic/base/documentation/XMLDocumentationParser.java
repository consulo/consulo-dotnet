/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.emonic.base.documentation;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.intellij.openapi.vfs.VirtualFile;

public class XMLDocumentationParser
{
	public static ITypeDocumentation findTypeDocumentation(VirtualFile file, String memberName)
	{
		TypeHandler handler = new TypeHandler(memberName);
		try
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(file.getInputStream(), handler);
		}
		catch(SAXException ignored)
		{
		}
		catch(FileNotFoundException ignored)
		{
		}
		catch(IOException ignored)
		{
		}
		catch(ParserConfigurationException ignored)
		{
		}
		catch(FactoryConfigurationError ignored)
		{
		}
		return handler.getTypeDocumentation();
	}

	static class TypeHandler extends DefaultHandler
	{

		private String myMemberName;

		private StringBuffer buffer;

		private boolean found = false;

		private XMLTypeDocumentation type;

		private XMLDocumentation current;

		private String parameterName;

		TypeHandler(String memberName)
		{
			myMemberName = memberName;
		}

		public ITypeDocumentation getTypeDocumentation()
		{
			return type;
		}

		@Override
		public void startElement(
				String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if(qName.equals("member"))
			{
				String name = attributes.getValue("name");
				if(name != null)
				{
					if(myMemberName.equals(name))
					{
						type = new XMLTypeDocumentation(myMemberName);
						current = type;
						found = true;
					}
					else if(found)
					{
						throw new SAXException("End");
					}
				}
			}
			else if(found)
			{
				if(qName.equals("summary") || qName.equals("returns") || qName.equals("value"))
				{
					buffer = new StringBuffer();
				}
				else if(qName.equals("param"))
				{
					parameterName = attributes.getValue("name");
					buffer = new StringBuffer();
				}
				else if(qName.equals("see"))
				{
					String cref = attributes.getValue("cref");
					if(cref == null)
					{
						buffer.append(attributes.getValue("langword"));
					}
					else
					{
						buffer.append(cref.substring(2));
					}
					buffer.append(' ');
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		{
			if(found)
			{
				if(qName.equals("summary"))
				{
					current.setSummary(buffer.toString().trim());
				}
				else if(qName.equals("returns"))
				{
					current.setReturns(buffer.toString().trim());
				}
				else if(qName.equals("value"))
				{
					current.setValue(buffer.toString().trim());
				}
				else if(qName.equals("param"))
				{
					current.addParameter(parameterName, buffer.toString());
				}
			}
		}

		@Override
		public void characters(char ch[], int start, int length)
		{
			if(found)
			{
				for(int i = 0; i < length; i++)
				{
					if(!Character.isWhitespace(ch[start + i]))
					{
						String string = new String(ch, start + i, length - i).trim();
						String[] split = string.split("\\s"); //$NON-NLS-1$
						for(int j = 0; j < split.length; j++)
						{
							if(!split[j].equals(""))
							{ //$NON-NLS-1$
								buffer.append(split[j]).append(' ');
							}
						}
						break;
					}
				}
			}
		}
	}
}
