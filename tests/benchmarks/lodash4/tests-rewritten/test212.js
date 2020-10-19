QUnit.module('round methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isCeil = methodName == __str_top__, isFloor = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func(__num_top__);
        assert.strictEqual(actual, isCeil ? __num_top__ : __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func(__num_top__, __num_top__);
        assert.strictEqual(actual, isCeil ? __num_top__ : __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var actual = func(__num_top__, __num_top__);
        assert.strictEqual(actual, isFloor ? __num_top__ : __num_top__);
        actual = func(__num_top__, __num_top__);
        assert.strictEqual(actual, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func(__num_top__, -__num_top__);
        assert.strictEqual(actual, isFloor ? __num_top__ : __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var actual = func(__num_top__, NaN);
        assert.strictEqual(actual, isCeil ? __num_top__ : __num_top__);
        var expected = isFloor ? __num_top__ : __num_top__;
        actual = func(__num_top__, __num_top__);
        assert.strictEqual(actual, expected);
        actual = func(__num_top__, __str_top__);
        assert.strictEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var actual = func(__num_top__, __num_top__);
        assert.deepEqual(actual, __num_top__);
        actual = func(__str_top__, __num_top__);
        assert.deepEqual(actual, NaN);
        actual = func(__str_top__, __num_top__);
        assert.deepEqual(actual, NaN);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                [__num_top__],
                [-__num_top__],
                [__str_top__],
                [__str_top__],
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    -__num_top__,
                    __num_top__
                ],
                [
                    __str_top__,
                    __num_top__
                ],
                [
                    __str_top__,
                    __num_top__
                ]
            ], expected = [
                Infinity,
                -Infinity,
                Infinity,
                -Infinity,
                Infinity,
                -Infinity,
                Infinity,
                -Infinity
            ];
        var actual = lodashStable.map(values, function (args) {
            return __num_top__ / func.apply(undefined, args);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var results = [
            _.round(__num_top__, __num_top__),
            _.round(MAX_SAFE_INTEGER, __num_top__)
        ];
        var expected = lodashStable.map(results, stubFalse), actual = lodashStable.map(results, lodashStable.isNaN);
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(6);
        var actual = func(Infinity);
        assert.strictEqual(actual, Infinity);
        actual = func(Infinity, __num_top__);
        assert.strictEqual(actual, Infinity);
        actual = func(Infinity, __num_top__);
        assert.strictEqual(actual, Infinity);
        actual = func(Infinity, -__num_top__);
        assert.strictEqual(actual, Infinity);
        actual = func(Infinity, __num_top__);
        assert.strictEqual(actual, isFloor ? Infinity : Infinity);
        actual = func(Infinity, __num_top__);
        assert.strictEqual(actual, isCeil ? Infinity : Infinity);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(6);
        var actual = func(-Infinity);
        assert.strictEqual(actual, -Infinity);
        actual = func(-Infinity, __num_top__);
        assert.strictEqual(actual, -Infinity);
        actual = func(-Infinity, __num_top__);
        assert.strictEqual(actual, -Infinity);
        actual = func(-Infinity, -__num_top__);
        assert.strictEqual(actual, -Infinity);
        actual = func(-Infinity, __num_top__);
        assert.strictEqual(actual, isFloor ? -Infinity : -Infinity);
        actual = func(-Infinity, __num_top__);
        assert.strictEqual(actual, isCeil ? -Infinity : -Infinity);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(6);
        var actual = func(NaN);
        assert.deepEqual(actual, NaN);
        actual = func(NaN, __num_top__);
        assert.deepEqual(actual, NaN);
        actual = func(NaN, __num_top__);
        assert.deepEqual(actual, NaN);
        actual = func(NaN, -__num_top__);
        assert.deepEqual(actual, NaN);
        actual = func(NaN, __num_top__);
        assert.deepEqual(actual, isFloor ? NaN : NaN);
        actual = func(NaN, __num_top__);
        assert.deepEqual(actual, isCeil ? NaN : NaN);
    });
});