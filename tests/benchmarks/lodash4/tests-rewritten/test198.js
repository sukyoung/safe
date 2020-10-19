QUnit.module('range methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isRange = methodName == __str_top__;
    function resolve(range) {
        return isRange ? range : range.reverse();
    }
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        assert.deepEqual(func(__num_top__), resolve([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]));
        assert.deepEqual(func(-__num_top__), resolve([
            __num_top__,
            -__num_top__,
            -__num_top__,
            -__num_top__
        ]));
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        assert.deepEqual(func(__num_top__, __num_top__), resolve([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]));
        assert.deepEqual(func(__num_top__, __num_top__), resolve([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]));
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        assert.deepEqual(func(__num_top__, -__num_top__, -__num_top__), resolve([
            __num_top__,
            -__num_top__,
            -__num_top__,
            -__num_top__
        ]));
        assert.deepEqual(func(__num_top__, __num_top__, -__num_top__), resolve([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]));
        assert.deepEqual(func(__num_top__, __num_top__, __num_top__), resolve([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]));
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func(__num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func(__num_top__, __num_top__, __num_top__), [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        assert.deepEqual(func(__num_top__, -__num_top__, -__num_top__), resolve([
            __num_top__,
            -__num_top__,
            -__num_top__,
            -__num_top__
        ]));
        assert.deepEqual(func(__num_top__, __num_top__, -__num_top__), resolve([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]));
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func(-__num_top__, __num_top__);
        assert.strictEqual(__num_top__ / actual[__num_top__], -Infinity);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(13);
        lodashStable.each(falsey, function (value, index) {
            if (index) {
                assert.deepEqual(func(value), []);
                assert.deepEqual(func(value, __num_top__), [__num_top__]);
            } else {
                assert.deepEqual(func(), []);
            }
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = [
            func(__str_top__),
            func(__str_top__, __num_top__),
            func(__num_top__, __num_top__, __str_top__),
            func(NaN),
            func(NaN, NaN)
        ];
        assert.deepEqual(actual, [
            [__num_top__],
            [__num_top__],
            [__num_top__],
            [],
            []
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], object = {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__
            }, expected = lodashStable.map([
                [__num_top__],
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ]
            ], resolve);
        lodashStable.each([
            array,
            object
        ], function (collection) {
            var actual = lodashStable.map(collection, func);
            assert.deepEqual(actual, expected);
        });
    });
});