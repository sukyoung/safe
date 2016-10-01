//   TODO getter/setter
//   function testcase() 
//   {
//     try
// {      Object.defineProperty(Array.prototype, "0", {
//         get : (function () 
//         {
//           return 11;
//         }),
//         configurable : true
//       });
//       var arrObj = [];
//       function getFunc() 
//       {
//         return arrObj.helpVerifySet;
//       }
//       function setFunc(value) 
//       {
//         arrObj.helpVerifySet = value;
//       }
//       Object.defineProperty(arrObj, "0", {
//         get : getFunc,
//         set : setFunc,
//         configurable : false
//       });
//       arrObj[0] = 13;
//       return accessorPropertyAttributesAreCorrect(arrObj, "0", getFunc, setFunc, "helpVerifySet", false, 
//       false);}
//     finally
// {      delete Array.prototype[0];}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
