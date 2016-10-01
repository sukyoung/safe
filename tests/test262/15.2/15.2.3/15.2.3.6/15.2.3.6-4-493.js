//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var verifySetFunc = "data";
//     var setFunc = (function (value) 
//     {
//       verifySetFunc = value;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : undefined,
//       set : setFunc,
//       enumerable : false,
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
//       return desc1.hasOwnProperty("get") && ! desc2.hasOwnProperty("value") && e instanceof TypeError;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
