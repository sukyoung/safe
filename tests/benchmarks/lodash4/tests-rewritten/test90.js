QUnit.module('intersection methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                null,
                __num_top__
            ], expected = [
                __num_top__,
                __num_top__
            ];
        assert.deepEqual(func(array, args), expected);
        assert.deepEqual(func(args, array), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                -__num_top__,
                __num_top__
            ], expected = lodashStable.map(values, lodashStable.constant([__str_top__]));
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.map(func(values, [value]), lodashStable.toString);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __num_top__,
            NaN,
            __num_top__
        ], [
            NaN,
            __num_top__,
            NaN
        ]);
        assert.deepEqual(actual, [NaN]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                -__num_top__,
                __num_top__
            ], expected = lodashStable.map(values, lodashStable.constant([__str_top__]));
        var actual = lodashStable.map(values, function (value) {
            var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, lodashStable.constant(value));
            return lodashStable.map(func(values, largeArray), lodashStable.toString);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, stubNaN);
        assert.deepEqual(func([
            __num_top__,
            NaN,
            __num_top__
        ], largeArray), [NaN]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = {}, largeArray = lodashStable.times(LARGE_ARRAY_SIZE, lodashStable.constant(object));
        assert.deepEqual(func([object], largeArray), [object]);
        assert.deepEqual(func(lodashStable.range(LARGE_ARRAY_SIZE), [__num_top__]), [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var array = [
            __num_top__,
            __num_top__,
            null,
            __num_top__
        ];
        assert.deepEqual(func(array, __num_top__, { '0': __num_top__ }, null), []);
        assert.deepEqual(func(null, array, null, [
            __num_top__,
            __num_top__
        ]), []);
        assert.deepEqual(func(array, null, args, null), []);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _([
                __num_top__,
                __num_top__,
                __num_top__
            ])[methodName]([
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ]);
            assert.ok(wrapped instanceof _);
            assert.deepEqual(wrapped.value(), [
                __num_top__,
                __num_top__
            ]);
        } else {
            skipAssert(assert, 2);
        }
    });
});