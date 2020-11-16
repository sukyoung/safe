QUnit.module('lodash(...) methods that return the wrapped modified array');
(function () {
    var funcs = [
        'push',
        __str_top__,
        __str_top__,
        __str_top__
    ];
    lodashStable.each(funcs, function (methodName) {
        QUnit.test(__str_top__ + methodName + '` should return a new wrapper', function (assert) {
            assert.expect(2);
            if (!isNpm) {
                var array = [
                        1,
                        __num_top__,
                        3
                    ], wrapped = _(array), actual = wrapped[methodName]();
                assert.ok(actual instanceof _);
                assert.notStrictEqual(actual, wrapped);
            } else {
                skipAssert(assert, 2);
            }
        });
    });
}());