//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = [];
//     obj.verifySetFunction = "data";
//     Object.defineProperty(obj, "prop", {
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
//     Object.defineProperty(obj, "prop", {
//       get : getFunc,
//       set : setFunc
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "prop", getFunc, setFunc, "verifySetFunction1", 
//     false, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
