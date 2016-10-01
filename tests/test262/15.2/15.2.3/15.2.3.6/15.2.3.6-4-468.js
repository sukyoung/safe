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
//     obj.prop = "overrideData";
//     var propertyDefineCorrect = obj.hasOwnProperty("prop");
//     var desc = Object.getOwnPropertyDescriptor(obj, "prop");
//     return propertyDefineCorrect && desc.set === setFunc && verifySetFunc === "overrideData";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
