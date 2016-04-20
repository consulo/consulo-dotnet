package consulo.dotnet.microsoft.debugger.protocol.serverMessage;

import consulo.dotnet.microsoft.debugger.protocol.TypeRef;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class GetLocalsRequestResult
{
	public class LocalInfo
	{
		public int Index;
		public TypeRef Type;
		public String Name;
	}

	public LocalInfo[] Locals = new LocalInfo[0];
}
