// The sources of Sample.dll and SampleFacade.dll (see build.sh)
using System;
using System.Collections.Generic;

namespace Sample.Api
{
    public class Widget { public class Nested { } }
    public interface IWidget { }
    public struct Point { }
    public enum Color { Red }
    public delegate void Handler(object o);
    public abstract class Shape { }
    public sealed class MarkerAttribute : Attribute { }
    public class Box<T> { }
    public class Box<T, U> { }
    internal class Hidden { }

    public static class WidgetExtensions
    {
        public static int Count(this Widget widget) => 0;
        public static void Helper(Widget widget) { }
        internal static int Hidden(this Widget widget) => 0;
    }
}

public class GlobalType { }
