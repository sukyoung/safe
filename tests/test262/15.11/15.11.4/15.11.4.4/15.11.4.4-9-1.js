function testcase() {
        var errObj = new Error();
        errObj.name = "ErrorName";
        return errObj.toString() === "ErrorName";
    }
var __result1 = testcase()
var __expect1 = true

