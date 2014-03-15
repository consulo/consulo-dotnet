public static class Operators
{

#region String
	// string + object = string
	public static string operator +(string param1, object param2)
	{
		return "";
	}

	// object + string = string
	public static string operator +(object param1, string param2)
	{
		return "";
	}
#endregion

#region Int
	// int + int = int
	public static int operator +(int param1, int param2)
	{
		return 0;
	}

	// int - int = int
	public static int operator -(int param1, int param2)
	{
		return 0;
	}

	// int * int = int
	public static int operator *(int param1, int param2)
	{
		return 0;
	}

	// int / int = int
	public static int operator /(int param1, int param2)
	{
		return 0;
	}

	// int > int = bool
	public static bool operator >(int param1, int param2)
	{
		return 0;
	}

	// int > int = bool
	public static bool operator >=(int param1, int param2)
	{
		return 0;
	}

	// int < int = bool
	public static bool operator <(int param1, int param2)
	{
		return 0;
	}

	// int <= int = bool
	public static bool operator <=(int param1, int param2)
	{
		return 0;
	}
#endregion

	// object == object = bool
	public static bool operator ==(object param1, object param2)
	{
		return false;
	}

	// object != object = bool
	public static bool operator !=(object param1, object param2)
	{
		return false;
	}
}