QUnit.module('pull methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isPull = methodName == __str_top__;
    function pull(array, values) {
        return isPull ? func.apply(undefined, [array].concat(values)) : func(array, values);
    }
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], actual = pull(array, [
                __num_top__,
                __num_top__
            ]);
        assert.strictEqual(actual, array);
        assert.deepEqual(array, [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
        delete array[__num_top__];
        delete array[__num_top__];
        pull(array, [__num_top__]);
        assert.notOk(__str_top__ in array);
        assert.notOk(__str_top__ in array);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        delete array[__num_top__];
        pull(array, [undefined]);
        assert.deepEqual(array, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            NaN,
            __num_top__,
            NaN
        ];
        pull(array, [NaN]);
        assert.deepEqual(array, [
            __num_top__,
            __num_top__
        ]);
    });
});