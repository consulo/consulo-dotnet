using System;

class Program
{
    static void Main()
    {
        Console.Write("Hello world!");
    }
}

class Test
{
	static void classConst<C>() where C : class
	{
	}

	static void structConst<S>() where S : struct
	{
	}

	static void newConst<N>() where N : new()
	{
	}
}