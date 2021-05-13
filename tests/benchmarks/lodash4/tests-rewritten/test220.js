QUnit.module('lodash.slice');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should use a default `start` of `0` and a default `end` of `length`', function (assert) {
        assert.expect(2);
        var actual = _.slice(array);
        assert.deepEqual(actual, array);
        assert.notStrictEqual(actual, array);
    });
    QUnit.test('should work with a positive `start`', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.slice(array, __num_top__), [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(_.slice(array, __num_top__, __num_top__), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with a `start` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __num_top__,
            __num_top__,
            Math.pow(__num_top__, __num_top__),
            Infinity
        ], function (start) {
            assert.deepEqual(_.slice(array, start), []);
        });
    });
    QUnit.test('should treat falsey `start` values as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, lodashStable.constant(array));
        var actual = lodashStable.map(falsey, function (start) {
            return _.slice(array, start);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with a negative `start`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.slice(array, -__num_top__), [__num_top__]);
    });
    QUnit.test('should work with a negative `start` <= negative `length`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            -__num_top__,
            -__num_top__,
            -Infinity
        ], function (start) {
            assert.deepEqual(_.slice(array, start), array);
        });
    });
    QUnit.test('should work with `start` >= `end`', function (assert) {
        assert.expect(2);
        lodashStable.each([
            __num_top__,
            __num_top__
        ], function (start) {
            assert.deepEqual(_.slice(array, start, __num_top__), []);
        });
    });
    QUnit.test('should work with a positive `end`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.slice(array, __num_top__, __num_top__), [__num_top__]);
    });
    QUnit.test('should work with a `end` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __num_top__,
            __num_top__,
            Math.pow(__num_top__, __num_top__),
            Infinity
        ], function (end) {
            assert.deepEqual(_.slice(array, __num_top__, end), array);
        });
    });
    QUnit.test('should treat falsey `end` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? array : [];
        });
        var actual = lodashStable.map(falsey, function (end, index) {
            return index ? _.slice(array, __num_top__, end) : _.slice(array, __num_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with a negative `end`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.slice(array, __num_top__, -__num_top__), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with a negative `end` <= negative `length`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            -__num_top__,
            -__num_top__,
            -Infinity
        ], function (end) {
            assert.deepEqual(_.slice(array, __num_top__, end), []);
        });
    });
    QUnit.test('should coerce `start` and `end` to integers', function (assert) {
        assert.expect(1);
        var positions = [
            [
                __num_top__,
                __num_top__
            ],
            [
                __str_top__,
                __num_top__
            ],
            [
                __num_top__,
                __str_top__
            ],
            [__str_top__],
            [
                NaN,
                __num_top__
            ],
            [
                __num_top__,
                NaN
            ]
        ];
        var actual = lodashStable.map(positions, function (pos) {
            return _.slice.apply(_, [array].concat(pos));
        });
        assert.deepEqual(actual, [
            [__num_top__],
            [__num_top__],
            [__num_top__],
            [
                __num_top__,
                __num_top__
            ],
            [__num_top__],
            []
        ]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(2);
        var array = [
                [__num_top__],
                [
                    __num_top__,
                    __num_top__
                ]
            ], actual = lodashStable.map(array, _.slice);
        assert.deepEqual(actual, array);
        assert.notStrictEqual(actual, array);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(38);
        if (!isNpm) {
            var array = lodashStable.range(__num_top__, LARGE_ARRAY_SIZE + __num_top__), length = array.length, wrapped = _(array);
            lodashStable.each([
                __str_top__,
                __str_top__
            ], function (methodName) {
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, -__num_top__).value(), array.slice(__num_top__, -__num_top__));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__).value(), array.slice(__num_top__));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, __num_top__).value(), array.slice(__num_top__, __num_top__));
                assert.deepEqual(wrapped[methodName]().slice(-__num_top__).value(), array.slice(-__num_top__));
                assert.deepEqual(wrapped[methodName]().slice(length).value(), array.slice(length));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, __num_top__).value(), array.slice(__num_top__, __num_top__));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, -length).value(), array.slice(__num_top__, -length));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, null).value(), array.slice(__num_top__, null));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, length).value(), array.slice(__num_top__, length));
                assert.deepEqual(wrapped[methodName]().slice(-length).value(), array.slice(-length));
                assert.deepEqual(wrapped[methodName]().slice(null).value(), array.slice(null));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, __num_top__).value(), array.slice(__num_top__, __num_top__));
                assert.deepEqual(wrapped[methodName]().slice(NaN, __str_top__).value(), array.slice(NaN, __str_top__));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, __num_top__).value(), array.slice(__num_top__, __num_top__));
                assert.deepEqual(wrapped[methodName]().slice(__str_top__, __num_top__).value(), array.slice(__str_top__, __num_top__));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, __str_top__).value(), array.slice(__num_top__, __str_top__));
                assert.deepEqual(wrapped[methodName]().slice(__str_top__).value(), array.slice(__str_top__));
                assert.deepEqual(wrapped[methodName]().slice(NaN, __num_top__).value(), array.slice(NaN, __num_top__));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, NaN).value(), array.slice(__num_top__, NaN));
            });
        } else {
            skipAssert(assert, 38);
        }
    });
}());