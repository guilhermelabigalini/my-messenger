using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using System;

namespace my.messenger.common.Messaging.Impl
{
    public class JsonUtil
    {
        private static JsonSerializerSettings settings;

        static JsonUtil()
        {
            settings = new JsonSerializerSettings();
            settings.NullValueHandling = NullValueHandling.Ignore;
            settings.DateFormatString = "yyyy'-'MM'-'dd'T'HH':'mm':'ss.fffK";
            settings.ContractResolver = new CamelCasePropertyNamesContractResolver();
        }

        public static byte[] ToBytes(object msg)
        {
            return System.Text.Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(msg, settings));
        }

        public static T FromBytes<T>(byte[] msg)
        {
            string str = System.Text.Encoding.UTF8.GetString(msg);
            return JsonConvert.DeserializeObject<T>(str, settings);
        }

        public static T FromString<T>(string str)
        {
            return JsonConvert.DeserializeObject<T>(str, settings);
        }

        public static string ToString(object msg)
        {
            return JsonConvert.SerializeObject(msg, settings);
        }
    }
}