using System;

class A
{

}

class Program
{
    static void Main()
    {
        Console.Write("Hello world!");
    }
}

class Test
{
	static void test<M>() where M : A
	{
	}
}