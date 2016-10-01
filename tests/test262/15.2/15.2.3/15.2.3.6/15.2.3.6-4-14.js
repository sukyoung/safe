//   TODO getter/setter
//   function testcase() 
//   {
//     var o = {
//       
//     };
//     o["foo"] = 101;
//     var getter = (function () 
//     {
//       return 1;
//     });
//     var d1 = {
//       get : getter
//     };
//     Object.defineProperty(o, "foo", d1);
//     var d2 = Object.getOwnPropertyDescriptor(o, "foo");
//     if (d2.get === getter && d2.enumerable === true && d2.configurable === true)
//     {
//       return true;
//     }
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
