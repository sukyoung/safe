QUnit.module('math operator methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isAddSub = methodName == __str_top__ || methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__ + (isAddSub ? __num_top__ : __num_top__) + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(), isAddSub ? __num_top__ : __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        assert.strictEqual(func(__num_top__), __num_top__);
        assert.strictEqual(func(__num_top__, undefined), __num_top__);
        assert.strictEqual(func(undefined, __num_top__), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var values = [
                __num_top__,
                __str_top__,
                -__num_top__,
                __str_top__
            ], expected = [
                [
                    __num_top__,
                    Infinity
                ],
                [
                    __str_top__,
                    Infinity
                ],
                [
                    -__num_top__,
                    -Infinity
                ],
                [
                    __str_top__,
                    -Infinity
                ]
            ];
        lodashStable.times(__num_top__, function (index) {
            var actual = lodashStable.map(values, function (value) {
                var result = index ? func(undefined, value) : func(value);
                return [
                    result,
                    __num_top__ / result
                ];
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        assert.deepEqual(func(__num_top__, {}), NaN);
        assert.deepEqual(func({}, __num_top__), NaN);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        if (Symbol) {
            assert.deepEqual(func(__num_top__, symbol), NaN);
            assert.deepEqual(func(symbol, __num_top__), NaN);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = _(__num_top__)[methodName](__num_top__);
            assert.notOk(actual instanceof _);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = _(__num_top__).chain()[methodName](__num_top__);
            assert.ok(actual instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});