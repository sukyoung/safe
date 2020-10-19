QUnit.module('xor methods');
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
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var actual = func([
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
        actual = func([
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, []);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [__num_top__], actual = func(array, array, array);
        assert.deepEqual(actual, []);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var actual = func([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
        actual = func([
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [__num_top__];
        assert.notStrictEqual(func(array), array);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [__num_top__];
        assert.deepEqual(func(array, __num_top__, null, { '0': __num_top__ }), array);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var array = [
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(func(array, __num_top__, { '0': __num_top__ }, null), array);
        assert.deepEqual(func(null, array, null, [
            __num_top__,
            __num_top__
        ]), [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(func(array, null, args, null), [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
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
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE + __num_top__), wrapped = _(array).slice(__num_top__)[methodName]([
                    LARGE_ARRAY_SIZE,
                    LARGE_ARRAY_SIZE + __num_top__
                ]);
            var actual = lodashStable.map([
                __str_top__,
                __str_top__
            ], function (methodName) {
                return wrapped[methodName]();
            });
            assert.deepEqual(actual, [
                __num_top__,
                LARGE_ARRAY_SIZE + __num_top__
            ]);
        } else {
            skipAssert(assert);
        }
    });
});