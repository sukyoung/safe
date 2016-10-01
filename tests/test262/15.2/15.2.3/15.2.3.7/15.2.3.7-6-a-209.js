//   TODO getter/setter
//   function testcase() 
//   {
//     var arr = [];
//     var get_func = (function () 
//     {
//       return "100";
//     });
//     var set_func = (function (value) 
//     {
//       arr.setVerifyHelpProp = value;
//     });
//     var descObj = {
//       get : get_func,
//       set : set_func,
//       enumerable : true,
//       configurable : true
//     };
//     var properties = {
//       "0" : descObj
//     };
//     Object.defineProperty(arr, "0", descObj);
//     Object.defineProperties(arr, properties);
//     return accessorPropertyAttributesAreCorrect(arr, "0", get_func, set_func, "setVerifyHelpProp", true, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
