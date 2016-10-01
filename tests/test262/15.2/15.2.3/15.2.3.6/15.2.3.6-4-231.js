//   TODO getter/setter
//   function testcase() 
//   {
//     var arrObj = [];
//     arrObj.helpVerifySet = 10;
//     function getFunc1() 
//     {
//       return 20;
//     }
//     function getFunc2() 
//     {
//       return arrObj.helpVerifySet;
//     }
//     function setFunc(value) 
//     {
//       arrObj.helpVerifySet = value;
//     }
//     Object.defineProperty(arrObj, "0", {
//       get : getFunc1,
//       set : setFunc,
//       configurable : true
//     });
//     Object.defineProperty(arrObj, "0", {
//       get : getFunc2
//     });
//     return accessorPropertyAttributesAreCorrect(arrObj, "0", getFunc2, setFunc, "helpVerifySet", false, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
