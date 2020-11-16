QUnit.module('flatten methods');
(function () {
    var array = [
            __num_top__,
            [
                __num_top__,
                [
                    __num_top__,
                    [__num_top__]
                ],
                __num_top__
            ]
        ], methodNames = [
            __str_top__,
            __str_top__,
            __str_top__
        ];
    QUnit.test('should flatten `arguments` objects', function (assert) {
        assert.expect(3);
        var array = [
            args,
            [args]
        ];
        assert.deepEqual(_.flatten(array), [
            __num_top__,
            __num_top__,
            __num_top__,
            args
        ]);
        assert.deepEqual(_.flattenDeep(array), [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(_.flattenDepth(array, __num_top__), [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should treat sparse arrays as dense', function (assert) {
        assert.expect(6);
        var array = [
                [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ],
                Array(__num_top__)
            ], expected = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        expected.push(undefined, undefined, undefined);
        lodashStable.each(methodNames, function (methodName) {
            var actual = _[methodName](array);
            assert.deepEqual(actual, expected);
            assert.ok(__str_top__ in actual);
        });
    });
    QUnit.test('should flatten objects with a truthy `Symbol.isConcatSpreadable` value', function (assert) {
        assert.expect(1);
        if (Symbol && Symbol.isConcatSpreadable) {
            var object = {
                    '0': __str_top__,
                    'length': __num_top__
                }, array = [object], expected = lodashStable.map(methodNames, lodashStable.constant([__str_top__]));
            object[Symbol.isConcatSpreadable] = __bool_top__;
            var actual = lodashStable.map(methodNames, function (methodName) {
                return _[methodName](array);
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should work with extremely large arrays', function (assert) {
        assert.expect(3);
        lodashStable.times(__num_top__, function (index) {
            var expected = Array(__num_top__);
            try {
                var func = _.flatten;
                if (index == __num_top__) {
                    func = _.flattenDeep;
                } else if (index == __num_top__) {
                    func = _.flattenDepth;
                }
                assert.deepEqual(func([expected]), expected);
            } catch (e) {
                assert.ok(__bool_top__, e.message);
            }
        });
    });
    QUnit.test('should work with empty arrays', function (assert) {
        assert.expect(3);
        var array = [
            [],
            [[]],
            [
                [],
                [[[]]]
            ]
        ];
        assert.deepEqual(_.flatten(array), [
            [],
            [],
            [[[]]]
        ]);
        assert.deepEqual(_.flattenDeep(array), []);
        assert.deepEqual(_.flattenDepth(array, __num_top__), [[[]]]);
    });
    QUnit.test('should support flattening of nested arrays', function (assert) {
        assert.expect(3);
        assert.deepEqual(_.flatten(array), [
            __num_top__,
            __num_top__,
            [
                __num_top__,
                [__num_top__]
            ],
            __num_top__
        ]);
        assert.deepEqual(_.flattenDeep(array), [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(_.flattenDepth(array, __num_top__), [
            __num_top__,
            __num_top__,
            __num_top__,
            [__num_top__],
            __num_top__
        ]);
    });
    QUnit.test('should return an empty array for non array-like objects', function (assert) {
        assert.expect(3);
        var expected = [], nonArray = { '0': __str_top__ };
        assert.deepEqual(_.flatten(nonArray), expected);
        assert.deepEqual(_.flattenDeep(nonArray), expected);
        assert.deepEqual(_.flattenDepth(nonArray, __num_top__), expected);
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var wrapped = _(array), actual = wrapped.flatten();
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.value(), [
                __num_top__,
                __num_top__,
                [
                    __num_top__,
                    [__num_top__]
                ],
                __num_top__
            ]);
            actual = wrapped.flattenDeep();
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.value(), [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ]);
            actual = wrapped.flattenDepth(__num_top__);
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.value(), [
                __num_top__,
                __num_top__,
                __num_top__,
                [__num_top__],
                __num_top__
            ]);
        } else {
            skipAssert(assert, 6);
        }
    });
}());