//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var getFunc = (function () 
//     {
//       return 1001;
//     });
//     var verifySetFunc = "data";
//     var setFunc = (function (value) 
//     {
//       verifySetFunc = value;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : getFunc,
//       set : setFunc,
//       enumerable : false,
//       configurable : true
//     });
//     var desc1 = Object.getOwnPropertyDescriptor(obj, "prop");
//     Object.defineProperty(obj, "prop", {
//       configurable : false
//     });
//     var desc2 = Object.getOwnPropertyDescriptor(obj, "prop");
//     delete obj.prop;
//     return desc1.configurable === true && desc2.configurable === false && obj.hasOwnProperty("prop");
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
