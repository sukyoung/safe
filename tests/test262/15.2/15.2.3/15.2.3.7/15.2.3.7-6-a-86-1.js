//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var set_func = (function (value) 
//     {
//       obj.setVerifyHelpProp = value;
//     });
//     Object.defineProperty(obj, "foo", {
//       set : set_func,
//       configurable : false
//     });
//     set_func = (function (value) 
//     {
//       obj.setVerifyHelpProp1 = value;
//     });
//     try
// {      Object.defineProperties(obj, {
//         foo : {
//           set : set_func
//         }
//       });}
//     catch (e)
// {      return accessorPropertyAttributesAreCorrect(obj, "foo", undefined, set_func, "setVerifyHelpProp", 
//       false, 
//       false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
