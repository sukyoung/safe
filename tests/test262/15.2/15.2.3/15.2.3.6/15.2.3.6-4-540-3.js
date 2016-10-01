//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = (function () 
//     {
//       return arguments;
//     })();
//     obj.verifySetFunction = "data";
//     var getFunc = (function () 
//     {
//       return obj.verifySetFunction;
//     });
//     var setFunc = (function (value) 
//     {
//       obj.verifySetFunction = value;
//     });
//     Object.defineProperty(obj, "property", {
//       get : getFunc,
//       set : setFunc,
//       configurable : false
//     });
//     var result = false;
//     try
// {      Object.defineProperty(obj, "property", {
//         get : (function () 
//         {
//           return 100;
//         })
//       });}
//     catch (e)
// {      result = e instanceof TypeError && accessorPropertyAttributesAreCorrect(obj, "property", getFunc, setFunc, "verifySetFunction", 
//       false, 
//       false);}
// 
//     try
// {      Object.defineProperty(obj, "property", {
//         set : (function (value) 
//         {
//           obj.verifySetFunction1 = value;
//         })
//       });}
//     catch (e1)
// {      return result && e1 instanceof TypeError && accessorPropertyAttributesAreCorrect(obj, "property", getFunc, setFunc, "verifySetFunction", 
//       false, 
//       false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
