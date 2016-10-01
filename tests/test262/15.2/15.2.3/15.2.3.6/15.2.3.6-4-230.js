//   TODO getter/setter
//   function testcase() 
//   {
//     var arrObj = [];
//     arrObj.helpVerifySet = 10;
//     function getFunc() 
//     {
//       return arrObj.helpVerifySet;
//     }
//     function setFunc(value) 
//     {
//       arrObj.helpVerifySet = value;
//     }
//     Object.defineProperty(arrObj, "0", {
//       get : getFunc,
//       set : setFunc
//     });
//     Object.defineProperty(arrObj, "0", {
//       get : getFunc
//     });
//     return accessorPropertyAttributesAreCorrect(arrObj, "0", getFunc, setFunc, "helpVerifySet", false, 
//     false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
