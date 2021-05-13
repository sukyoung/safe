QUnit.module('sortBy methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    function Pair(a, b, c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    var objects = [
        {
            'a': __str_top__,
            'b': __num_top__
        },
        {
            'a': __str_top__,
            'b': __num_top__
        },
        {
            'a': __str_top__,
            'b': __num_top__
        },
        {
            'a': __str_top__,
            'b': __num_top__
        }
    ];
    var stableArray = [
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(__num_top__, __num_top__, __num_top__),
        new Pair(undefined, __num_top__, __num_top__),
        new Pair(undefined, __num_top__, __num_top__),
        new Pair(undefined, __num_top__, __num_top__),
        new Pair(undefined, __num_top__, __num_top__),
        new Pair(undefined, __num_top__, __num_top__),
        new Pair(undefined, __num_top__, __num_top__)
    ];
    var stableObject = lodashStable.zipObject(__str_top__.split(__str_top__), stableArray);
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func(objects, [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__]
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func(objects, [
            __str_top__,
            function (object) {
                return object.b;
            }
        ]);
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__]
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        lodashStable.each([
            stableArray,
            stableObject
        ], function (value, index) {
            var actual = func(value, [
                __str_top__,
                __str_top__
            ]);
            assert.deepEqual(actual, stableArray, index ? __str_top__ : __str_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        try {
            var actual = func(objects.concat(null, undefined), [
                __str_top__,
                __str_top__
            ]);
        } catch (e) {
        }
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__],
            null,
            undefined
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var objects = [
            {
                'a': __str_top__,
                '0': __num_top__
            },
            {
                'a': __str_top__,
                '0': __num_top__
            },
            {
                'a': __str_top__,
                '0': __num_top__
            },
            {
                'a': __str_top__,
                '0': __num_top__
            }
        ];
        var funcs = [
            func,
            lodashStable.partialRight(func, __str_top__)
        ];
        lodashStable.each([
            __str_top__,
            __num_top__,
            [__num_top__]
        ], function (props, index) {
            var expected = lodashStable.map(funcs, lodashStable.constant(index ? [
                objects[__num_top__],
                objects[__num_top__],
                objects[__num_top__],
                objects[__num_top__]
            ] : [
                objects[__num_top__],
                objects[__num_top__],
                objects[__num_top__],
                objects[__num_top__]
            ]));
            var actual = lodashStable.map(funcs, function (func) {
                return lodashStable.reduce([props], func, objects);
            });
            assert.deepEqual(actual, expected);
        });
    });
});