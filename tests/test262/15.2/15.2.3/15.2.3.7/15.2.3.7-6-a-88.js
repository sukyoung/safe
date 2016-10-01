//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function get_Func() 
//     {
//       return 0;
//     }
//     Object.defineProperty(obj, "foo", {
//       set : undefined,
//       get : get_Func,
//       enumerable : false,
//       configurable : false
//     });
//     function set_Func() 
//     {
//       
//     }
//     try
// {      Object.defineProperties(obj, {
//         foo : {
//           set : set_Func
//         }
//       });
//       return false;}
//     catch (e)
// {      var verifyEnumerable = false;
//       for(var p in obj)
//       {
//         if (p === "foo")
//         {
//           verifyEnumerable = true;
//         }
//       }
//       var desc = Object.getOwnPropertyDescriptor(obj, "foo");
//       var verifyConfigurable = false;
//       delete obj.foo;
//       verifyConfigurable = obj.hasOwnProperty("foo");
//       return e instanceof TypeError && ! verifyEnumerable && verifyConfigurable && typeof (desc.set) === "undefined";}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
