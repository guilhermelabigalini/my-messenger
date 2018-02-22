using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common
{
    internal class Identifier
    {
        public static string New()
        {
            return Guid.NewGuid().ToString("N");
        }
    }
}
