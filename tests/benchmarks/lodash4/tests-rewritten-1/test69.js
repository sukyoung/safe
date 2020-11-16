QUnit.module('flatten methods');
(function () {
    var array = [
            1,
            [
                2,
                [
                    3,
                    [4]
                ],
                5
            ]
        ], methodNames = [
            'flatten',
            'flattenDeep',
            'flattenDepth'
        ];
    QUnit.test('should flatten `arguments` objects', function (assert) {
        assert.expect(3);
        var array = [
            args,
            [args]
        ];
        assert.deepEqual(_.flatten(array), [
            1,
            2,
            3,
            args
        ]);
        assert.deepEqual(_.flattenDeep(array), [
            1,
            2,
            3,
            1,
            2,
            3
        ]);
        assert.deepEqual(_.flattenDepth(array, 2), [
            1,
            2,
            3,
            1,
            2,
            3
        ]);
    });
    QUnit.test('should treat sparse arrays as dense', function (assert) {
        assert.expect(6);
        var array = [
                [
                    1,
                    2,
                    3
                ],
                Array(3)
            ], expected = [
                1,
                2,
                3
            ];
        expected.push(undefined, undefined, undefined);
        lodashStable.each(methodNames, function (methodName) {
            var actual = _[methodName](array);
            assert.deepEqual(actual, expected);
            assert.ok('4' in actual);
        });
    });
    QUnit.test('should flatten objects with a truthy `Symbol.isConcatSpreadable` value', function (assert) {
        assert.expect(1);
        if (Symbol && Symbol.isConcatSpreadable) {
            var object = {
                    '0': 'a',
                    'length': 1
                }, array = [object], expected = lodashStable.map(methodNames, lodashStable.constant(['a']));
            object[Symbol.isConcatSpreadable] = true;
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
        lodashStable.times(3, function (index) {
            var expected = Array(500000);
            try {
                var func = _.flatten;
                if (index == 1) {
                    func = _.flattenDeep;
                } else if (index == 2) {
                    func = _.flattenDepth;
                }
                assert.deepEqual(func([expected]), expected);
            } catch (e) {
                assert.ok(false, e.message);
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
        assert.deepEqual(_.flattenDepth(array, 2), [[[]]]);
    });
    QUnit.test('should support flattening of nested arrays', function (assert) {
        assert.expect(3);
        assert.deepEqual(_.flatten(array), [
            1,
            2,
            [
                3,
                [4]
            ],
            5
        ]);
        assert.deepEqual(_.flattenDeep(array), [
            1,
            2,
            3,
            4,
            5
        ]);
        assert.deepEqual(_.flattenDepth(array, 2), [
            1,
            2,
            3,
            [4],
            5
        ]);
    });
    QUnit.test('should return an empty array for non array-like objects', function (assert) {
        assert.expect(3);
        var expected = [], nonArray = { '0': 'a' };
        assert.deepEqual(_.flatten(nonArray), expected);
        assert.deepEqual(_.flattenDeep(nonArray), expected);
        assert.deepEqual(_.flattenDepth(nonArray, 2), expected);
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var wrapped = _(array), actual = wrapped.flatten();
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.value(), [
                1,
                __num_top__,
                [
                    3,
                    [4]
                ],
                5
            ]);
            actual = wrapped.flattenDeep();
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.value(), [
                1,
                2,
                3,
                4,
                5
            ]);
            actual = wrapped.flattenDepth(2);
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.value(), [
                1,
                2,
                3,
                [4],
                5
            ]);
        } else {
            skipAssert(assert, 6);
        }
    });
}());