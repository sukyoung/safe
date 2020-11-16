QUnit.module('indexOf methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isIndexOf = !/last/i.test(methodName), isSorted = /^sorted/.test(methodName);
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, lodashStable.constant(-__num_top__));
        var actual = lodashStable.map(falsey, function (array, index) {
            try {
                return index ? func(array) : func();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(5);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], empty = [];
        assert.strictEqual(func(array, __num_top__), -__num_top__);
        assert.strictEqual(func(array, __num_top__, __bool_top__), -__num_top__);
        assert.strictEqual(func(array, undefined, __bool_top__), -__num_top__);
        assert.strictEqual(func(empty, undefined), -__num_top__);
        assert.strictEqual(func(empty, undefined, __bool_top__), -__num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [];
        array[-__num_top__] = __num_top__;
        assert.strictEqual(func(array, undefined), -__num_top__);
        assert.strictEqual(func(array, __num_top__, __bool_top__), -__num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var array = isSorted ? [
            __num_top__,
            __num_top__,
            NaN,
            NaN
        ] : [
            __num_top__,
            NaN,
            __num_top__,
            NaN,
            __num_top__,
            NaN
        ];
        if (isSorted) {
            assert.strictEqual(func(array, NaN, __bool_top__), isIndexOf ? __num_top__ : __num_top__);
            skipAssert(assert, 2);
        } else {
            assert.strictEqual(func(array, NaN), isIndexOf ? __num_top__ : __num_top__);
            assert.strictEqual(func(array, NaN, __num_top__), isIndexOf ? __num_top__ : __num_top__);
            assert.strictEqual(func(array, NaN, -__num_top__), isIndexOf ? __num_top__ : __num_top__);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        assert.strictEqual(func([-__num_top__], __num_top__), __num_top__);
        assert.strictEqual(func([__num_top__], -__num_top__), __num_top__);
    });
});