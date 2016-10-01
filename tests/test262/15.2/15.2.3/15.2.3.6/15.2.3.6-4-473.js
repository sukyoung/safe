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
//       enumerable : true,
//       configurable : false
//     });
//     var result1 = false;
//     var desc1 = Object.getOwnPropertyDescriptor(obj, "prop");
//     for(var p1 in obj)
//     {
//       if (p1 === "prop")
//       {
//         result1 = true;
//       }
//     }
//     try
// {      Object.defineProperty(obj, "prop", {
//         enumerable : false
//       });
//       return false;}
//     catch (e)
// {      var result2 = false;
//       var desc2 = Object.getOwnPropertyDescriptor(obj, "prop");
//       for(var p2 in obj)
//       {
//         if (p2 === "prop")
//         {
//           result2 = true;
//         }
//       }
//       return result1 && result2 && desc1.enumerable === true && desc2.enumerable === true && e instanceof TypeError;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
