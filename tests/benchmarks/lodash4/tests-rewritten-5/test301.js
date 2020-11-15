QUnit.module('lodash(...) methods that return the wrapped modified array');
(function () {
    var funcs = [
        __str_top__,
        'reverse',
        'sort',
        'unshift'
    ];
    lodashStable.each(funcs, function (methodName) {
        QUnit.test('`_(...).' + methodName + __str_top__, function (assert) {
            assert.expect(2);
            if (!isNpm) {
                var array = [
                        __num_top__,
                        __num_top__,
                        __num_top__
                    ], wrapped = _(array), actual = wrapped[methodName]();
                assert.ok(actual instanceof _);
                assert.notStrictEqual(actual, wrapped);
            } else {
                skipAssert(assert, 2);
            }
        });
    });
}());