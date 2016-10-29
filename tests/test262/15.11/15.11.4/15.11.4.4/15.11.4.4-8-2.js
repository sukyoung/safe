function testcase() {
        var errObj = new Error();
        errObj.name = "";
        return errObj.toString() === "";
    }
var __result1 = testcase()
var __expect1 = true

