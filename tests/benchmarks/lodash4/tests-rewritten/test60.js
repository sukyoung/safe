lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    QUnit.module(__str_top__ + methodName);
    var array = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], func = _[methodName];
    var objects = [
        {
            'a': __num_top__,
            'b': __num_top__
        },
        {
            'a': __num_top__,
            'b': __num_top__
        },
        {
            'a': __num_top__,
            'b': __num_top__
        }
    ];
    var expected = {
        'find': [
            objects[__num_top__],
            undefined,
            objects[__num_top__]
        ],
        'findIndex': [
            __num_top__,
            -__num_top__,
            __num_top__
        ],
        'findKey': [
            __str_top__,
            undefined,
            __str_top__
        ],
        'findLast': [
            objects[__num_top__],
            undefined,
            objects[__num_top__]
        ],
        'findLastIndex': [
            __num_top__,
            -__num_top__,
            __num_top__
        ],
        'findLastKey': [
            __str_top__,
            undefined,
            __str_top__
        ]
    }[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(objects, function (object) {
            return object.a;
        }), expected[__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + expected[__num_top__] + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(objects, function (object) {
            return object.a === __num_top__;
        }), expected[__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(objects, { 'b': __num_top__ }), expected[__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(objects, [
            __str_top__,
            __num_top__
        ]), expected[__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(objects, __str_top__), expected[__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + expected[__num_top__] + __str_top__, function (assert) {
        assert.expect(1);
        var emptyValues = lodashStable.endsWith(methodName, __str_top__) ? lodashStable.reject(empties, lodashStable.isPlainObject) : empties, expecting = lodashStable.map(emptyValues, lodashStable.constant(expected[__num_top__]));
        var actual = lodashStable.map(emptyValues, function (value) {
            try {
                return func(value, { 'a': __num_top__ });
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expecting);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = {
            'find': __num_top__,
            'findIndex': __num_top__,
            'findKey': __str_top__,
            'findLast': __num_top__,
            'findLastIndex': __num_top__,
            'findLastKey': __str_top__
        }[methodName];
        if (!isNpm) {
            assert.strictEqual(_(array)[methodName](), expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(array).chain()[methodName]() instanceof _);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _(array).chain()[methodName]();
            assert.strictEqual(wrapped.__wrapped__, array);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var largeArray = lodashStable.range(__num_top__, LARGE_ARRAY_SIZE + __num_top__), smallArray = array;
            lodashStable.times(__num_top__, function (index) {
                var array = index ? largeArray : smallArray, wrapped = _(array).filter(isEven);
                assert.strictEqual(wrapped[methodName](), func(lodashStable.filter(array, isEven)));
            });
        } else {
            skipAssert(assert, 2);
        }
    });
});
_.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var args, array = [__str_top__];
        func(array, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [
            __str_top__,
            __num_top__,
            array
        ]);
    });
});
_.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }, function (n) {
            return n < __num_top__;
        });
        var expected = {
            'find': __num_top__,
            'findKey': __str_top__,
            'findLast': __num_top__,
            'findLastKey': __str_top__
        }[methodName];
        assert.strictEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var args, object = { 'a': __num_top__ };
        func(object, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [
            __num_top__,
            __str_top__,
            object
        ]);
    });
});