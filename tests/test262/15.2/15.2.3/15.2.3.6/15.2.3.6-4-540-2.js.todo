//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = [];
//     obj.verifySetFunction = "data";
//     var getFunc = (function () 
//     {
//       return obj.verifySetFunction;
//     });
//     var setFunc = (function (value) 
//     {
//       obj.verifySetFunction = value;
//     });
//     Object.defineProperty(obj, "0", {
//       get : getFunc,
//       set : setFunc,
//       configurable : false
//     });
//     var result = false;
//     try
// {      Object.defineProperty(obj, "0", {
//         get : (function () 
//         {
//           return 100;
//         })
//       });}
//     catch (e)
// {      result = e instanceof TypeError && accessorPropertyAttributesAreCorrect(obj, "0", getFunc, setFunc, "verifySetFunction", false, 
//       false);}
// 
//     try
// {      Object.defineProperty(obj, "0", {
//         set : (function (value) 
//         {
//           obj.verifySetFunction1 = value;
//         })
//       });}
//     catch (e1)
// {      return result && e1 instanceof TypeError && accessorPropertyAttributesAreCorrect(obj, "0", getFunc, setFunc, "verifySetFunction", false, 
//       false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
