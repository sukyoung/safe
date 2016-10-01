  function testcase() 
  {
    var arr = [1, 2, 3, ];
    Object.defineProperty(arr, "length", {
      writable : false
    });
    Object.defineProperties(arr, {
      "1" : {
        value : "abc"
      }
    });
    return arr[0] === 1 && arr[1] === "abc" && arr[2] === 3;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  