function testcase() {
        var errObj = new Error("ErrorMessage");
        errObj.name = "ErrorName";
        return errObj.toString() === "ErrorName: ErrorMessage";
    }
var __result1 = testcase()
var __expect1 = true
