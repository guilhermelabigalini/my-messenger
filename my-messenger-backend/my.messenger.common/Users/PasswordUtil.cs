using System;
using System.Security.Cryptography;
using System.Text;

namespace my.messenger.common.Users
{
    internal class PasswordUtil
    {
        internal static string encode(string input)
        {
            using (var md5 = MD5.Create())
            {
                var result = md5.ComputeHash(Encoding.ASCII.GetBytes(input));
                return Convert.ToBase64String(result);
            }
        }
    }
}