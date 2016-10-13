//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = (function () 
//     {
//       return arguments;
//     })();
//     obj.verifySetFunc = "data";
//     var getFunc = (function () 
//     {
//       return obj.verifySetFunc;
//     });
//     var setFunc = (function (value) 
//     {
//       obj.verifySetFunc = value;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : getFunc,
//       set : setFunc,
//       enumerable : true,
//       configurable : false
//     });
//     var desc1 = Object.getOwnPropertyDescriptor(obj, "prop");
//     try
// {      Object.defineProperty(obj, "prop", {
//         value : 1001
//       });
//       return false;}
//     catch (e)
// {      var desc2 = Object.getOwnPropertyDescriptor(obj, "prop");
//       return desc1.hasOwnProperty("get") && ! desc2.hasOwnProperty("value") && e instanceof TypeError && accessorPropertyAttributesAreCorrect(obj, "prop", getFunc, setFunc, "verifySetFunc", true, 
//       false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
