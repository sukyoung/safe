function testcase() {
        var errObj = new Error("ErrorMessage");
        errObj.name = "";
        return errObj.toString() === "ErrorMessage";
    }
var __result1 = testcase()
var __expect1 = true
