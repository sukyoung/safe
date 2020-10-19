QUnit.module('strict mode checks');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isBindAll = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__ + (isStrict ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        var object = freeze({
                'a': undefined,
                'b': function () {
                }
            }), pass = !isStrict;
        try {
            func(object, isBindAll ? __str_top__ : { 'a': __num_top__ });
        } catch (e) {
            pass = !pass;
        }
        assert.ok(pass);
    });
});