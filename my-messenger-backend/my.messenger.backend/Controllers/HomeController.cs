using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;

namespace my.messenger.backend.Controllers
{
    [Route("/")]
    public class HomeController : Controller
    {
        public string Index()
        {
            // typeof(RuntimeEnvironment).GetTypeInfo().Assembly.GetCustomAttribute<AssemblyFileVersionAttribute>().Version;

            var v = Assembly.GetExecutingAssembly().GetCustomAttribute<AssemblyFileVersionAttribute>().Version;

            return "Running @ " + Environment.MachineName + " ( " + DateTime.Now + " ) version " + v;
        }
    }
}