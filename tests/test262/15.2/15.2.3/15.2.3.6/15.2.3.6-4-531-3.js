//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = (function () 
//     {
//       return arguments;
//     })();
//     obj.verifySetFunction = "data";
//     Object.defineProperty(obj, "property", {
//       get : (function () 
//       {
//         return obj.verifySetFunction;
//       }),
//       set : (function (value) 
//       {
//         obj.verifySetFunction = value;
//       }),
//       configurable : true
//     });
//     obj.verifySetFunction1 = "data1";
//     var getFunc = (function () 
//     {
//       return obj.verifySetFunction1;
//     });
//     var setFunc = (function (value) 
//     {
//       obj.verifySetFunction1 = value;
//     });
//     Object.defineProperty(obj, "property", {
//       get : getFunc,
//       set : setFunc
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "property", getFunc, setFunc, "verifySetFunction1", 
//     false, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
