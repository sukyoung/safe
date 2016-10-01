//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     obj.verifySetFunction = "data";
//     var get_func = (function () 
//     {
//       return obj.verifySetFunction;
//     });
//     var set_func = (function (value) 
//     {
//       obj.verifySetFunction = value;
//     });
//     Object.defineProperty(obj, "foo", {
//       get : get_func,
//       set : set_func,
//       enumerable : false,
//       configurable : true
//     });
//     Object.defineProperty(obj, "foo", {
//       enumerable : true
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "foo", get_func, set_func, "verifySetFunction", 
//     true, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
