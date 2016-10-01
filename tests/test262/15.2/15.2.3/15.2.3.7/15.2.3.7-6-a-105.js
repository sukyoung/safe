//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function get_func() 
//     {
//       return 10;
//     }
//     function set_func() 
//     {
//       return 10;
//     }
//     Object.defineProperty(obj, "property", {
//       get : get_func,
//       set : set_func,
//       enumerable : true,
//       configurable : true
//     });
//     Object.defineProperties(obj, {
//       property : {
//         set : undefined
//       }
//     });
//     var hasProperty = obj.hasOwnProperty("property");
//     var verifyGet = false;
//     verifyGet = (obj.property === 10);
//     var verifySet = false;
//     var desc = Object.getOwnPropertyDescriptor(obj, "property");
//     verifySet = (typeof desc.set === 'undefined');
//     var verifyEnumerable = false;
//     for(var p in obj)
//     {
//       if (p === "property")
//       {
//         verifyEnumerable = true;
//       }
//     }
//     var verifyConfigurable = false;
//     delete obj.property;
//     verifyConfigurable = obj.hasOwnProperty("property");
//     return hasProperty && verifyGet && verifySet && verifyEnumerable && ! verifyConfigurable;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
