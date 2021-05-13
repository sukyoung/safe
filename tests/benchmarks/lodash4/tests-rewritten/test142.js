QUnit.module('lodash.findLastIndex and lodash.lastIndexOf');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var array = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], func = _[methodName], resolve = methodName == __str_top__ ? lodashStable.curry(lodashStable.eq) : identity;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(__num_top__)), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(__num_top__), __num_top__), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                __num_top__,
                __num_top__,
                Math.pow(__num_top__, __num_top__),
                Infinity
            ], expected = lodashStable.map(values, lodashStable.constant([
                -__num_top__,
                __num_top__,
                -__num_top__
            ]));
        var actual = lodashStable.map(values, function (fromIndex) {
            return [
                func(array, resolve(undefined), fromIndex),
                func(array, resolve(__num_top__), fromIndex),
                func(array, resolve(__str_top__), fromIndex)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(__num_top__), -__num_top__), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                -__num_top__,
                -__num_top__,
                -Infinity
            ], expected = lodashStable.map(values, stubZero);
        var actual = lodashStable.map(values, function (fromIndex) {
            return func(array, resolve(__num_top__), fromIndex);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? __num_top__ : -__num_top__;
        });
        var actual = lodashStable.map(falsey, function (fromIndex) {
            return func(array, resolve(__num_top__), fromIndex);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(__num_top__), __num_top__), __num_top__);
    });
});