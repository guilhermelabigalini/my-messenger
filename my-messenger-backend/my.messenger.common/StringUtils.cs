using System;

namespace my.messenger.common
{
    class StringUtils
    {
        internal static bool hasText(string username)
        {
            return !String.IsNullOrEmpty(username);
        }
    }
}