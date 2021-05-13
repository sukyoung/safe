QUnit.module('lodash.isEqualWith');
(function () {
    QUnit.test('should provide correct `customizer` arguments', function (assert) {
        assert.expect(1);
        var argsList = [], object1 = {
                'a': [
                    __num_top__,
                    __num_top__
                ],
                'b': null
            }, object2 = {
                'a': [
                    __num_top__,
                    __num_top__
                ],
                'b': null
            };
        object1.b = object2;
        object2.b = object1;
        var expected = [
            [
                object1,
                object2
            ],
            [
                object1.a,
                object2.a,
                __str_top__,
                object1,
                object2
            ],
            [
                object1.a[__num_top__],
                object2.a[__num_top__],
                __num_top__,
                object1.a,
                object2.a
            ],
            [
                object1.a[__num_top__],
                object2.a[__num_top__],
                __num_top__,
                object1.a,
                object2.a
            ],
            [
                object1.b,
                object2.b,
                __str_top__,
                object1.b,
                object2.b
            ]
        ];
        _.isEqualWith(object1, object2, function (assert) {
            var length = arguments.length, args = slice.call(arguments, __num_top__, length - (length > __num_top__ ? __num_top__ : __num_top__));
            argsList.push(args);
        });
        assert.deepEqual(argsList, expected);
    });
    QUnit.test('should handle comparisons when `customizer` returns `undefined`', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.isEqualWith(__str_top__, __str_top__, noop), __bool_top__);
        assert.strictEqual(_.isEqualWith([__str_top__], [__str_top__], noop), __bool_top__);
        assert.strictEqual(_.isEqualWith({ '0': __str_top__ }, { '0': __str_top__ }, noop), __bool_top__);
    });
    QUnit.test('should not handle comparisons when `customizer` returns `true`', function (assert) {
        assert.expect(3);
        var customizer = function (value) {
            return _.isString(value) || undefined;
        };
        assert.strictEqual(_.isEqualWith(__str_top__, __str_top__, customizer), __bool_top__);
        assert.strictEqual(_.isEqualWith([__str_top__], [__str_top__], customizer), __bool_top__);
        assert.strictEqual(_.isEqualWith({ '0': __str_top__ }, { '0': __str_top__ }, customizer), __bool_top__);
    });
    QUnit.test('should not handle comparisons when `customizer` returns `false`', function (assert) {
        assert.expect(3);
        var customizer = function (value) {
            return _.isString(value) ? __bool_top__ : undefined;
        };
        assert.strictEqual(_.isEqualWith(__str_top__, __str_top__, customizer), __bool_top__);
        assert.strictEqual(_.isEqualWith([__str_top__], [__str_top__], customizer), __bool_top__);
        assert.strictEqual(_.isEqualWith({ '0': __str_top__ }, { '0': __str_top__ }, customizer), __bool_top__);
    });
    QUnit.test('should return a boolean value even when `customizer` does not', function (assert) {
        assert.expect(2);
        var actual = _.isEqualWith(__str_top__, __str_top__, stubC);
        assert.strictEqual(actual, __bool_top__);
        var values = _.without(falsey, undefined), expected = lodashStable.map(values, stubFalse);
        actual = [];
        lodashStable.each(values, function (value) {
            actual.push(_.isEqualWith(__str_top__, __str_top__, lodashStable.constant(value)));
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should ensure `customizer` is a function', function (assert) {
        assert.expect(1);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], eq = _.partial(_.isEqualWith, array), actual = lodashStable.map([
                array,
                [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ]
            ], eq);
        assert.deepEqual(actual, [
            __bool_top__,
            __bool_top__
        ]);
    });
    QUnit.test('should call `customizer` for values maps and sets', function (assert) {
        assert.expect(2);
        var value = { 'a': { 'b': __num_top__ } };
        if (Map) {
            var map1 = new Map();
            map1.set(__str_top__, value);
            var map2 = new Map();
            map2.set(__str_top__, value);
        }
        if (Set) {
            var set1 = new Set();
            set1.add(value);
            var set2 = new Set();
            set2.add(value);
        }
        lodashStable.each([
            [
                map1,
                map2
            ],
            [
                set1,
                set2
            ]
        ], function (pair, index) {
            if (pair[__num_top__]) {
                var argsList = [], array = lodashStable.toArray(pair[__num_top__]);
                var expected = [
                    [
                        pair[__num_top__],
                        pair[__num_top__]
                    ],
                    [
                        array[__num_top__],
                        array[__num_top__],
                        __num_top__,
                        array,
                        array
                    ],
                    [
                        array[__num_top__][__num_top__],
                        array[__num_top__][__num_top__],
                        __num_top__,
                        array[__num_top__],
                        array[__num_top__]
                    ],
                    [
                        array[__num_top__][__num_top__],
                        array[__num_top__][__num_top__],
                        __num_top__,
                        array[__num_top__],
                        array[__num_top__]
                    ]
                ];
                if (index) {
                    expected.length = __num_top__;
                }
                _.isEqualWith(pair[__num_top__], pair[__num_top__], function () {
                    var length = arguments.length, args = slice.call(arguments, __num_top__, length - (length > __num_top__ ? __num_top__ : __num_top__));
                    argsList.push(args);
                });
                assert.deepEqual(argsList, expected, index ? __str_top__ : __str_top__);
            } else {
                skipAssert(assert);
            }
        });
    });
}());