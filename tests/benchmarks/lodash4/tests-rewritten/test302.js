QUnit.module('lodash(...) methods that return new wrapped values');
(function () {
    var funcs = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    lodashStable.each(funcs, function (methodName) {
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(2);
            if (!isNpm) {
                var value = methodName == __str_top__ ? __str_top__ : [
                        __num_top__,
                        __num_top__,
                        __num_top__
                    ], wrapped = _(value), actual = wrapped[methodName]();
                assert.ok(actual instanceof _);
                assert.notStrictEqual(actual, wrapped);
            } else {
                skipAssert(assert, 2);
            }
        });
    });
}());