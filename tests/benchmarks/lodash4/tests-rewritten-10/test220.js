QUnit.module('lodash.slice');
(function () {
    var array = [
        1,
        2,
        3
    ];
    QUnit.test('should use a default `start` of `0` and a default `end` of `length`', function (assert) {
        assert.expect(2);
        var actual = _.slice(array);
        assert.deepEqual(actual, array);
        assert.notStrictEqual(actual, array);
    });
    QUnit.test('should work with a positive `start`', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.slice(array, 1), [
            2,
            3
        ]);
        assert.deepEqual(_.slice(array, 1, __num_top__), [
            2,
            3
        ]);
    });
    QUnit.test('should work with a `start` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            3,
            4,
            Math.pow(__num_top__, 32),
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
        assert.deepEqual(_.slice(array, -1), [3]);
    });
    QUnit.test('should work with a negative `start` <= negative `length`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            -3,
            -4,
            -Infinity
        ], function (start) {
            assert.deepEqual(_.slice(array, start), array);
        });
    });
    QUnit.test('should work with `start` >= `end`', function (assert) {
        assert.expect(2);
        lodashStable.each([
            2,
            3
        ], function (start) {
            assert.deepEqual(_.slice(array, start, __num_top__), []);
        });
    });
    QUnit.test('should work with a positive `end`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.slice(array, 0, 1), [1]);
    });
    QUnit.test('should work with a `end` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            3,
            4,
            Math.pow(2, 32),
            Infinity
        ], function (end) {
            assert.deepEqual(_.slice(array, 0, end), array);
        });
    });
    QUnit.test('should treat falsey `end` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? array : [];
        });
        var actual = lodashStable.map(falsey, function (end, index) {
            return index ? _.slice(array, 0, end) : _.slice(array, __num_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with a negative `end`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.slice(array, 0, -1), [
            1,
            2
        ]);
    });
    QUnit.test('should work with a negative `end` <= negative `length`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            -3,
            -4,
            -Infinity
        ], function (end) {
            assert.deepEqual(_.slice(array, 0, end), []);
        });
    });
    QUnit.test('should coerce `start` and `end` to integers', function (assert) {
        assert.expect(1);
        var positions = [
            [
                0.1,
                1.6
            ],
            [
                '0',
                1
            ],
            [
                0,
                '1'
            ],
            ['1'],
            [
                NaN,
                1
            ],
            [
                1,
                NaN
            ]
        ];
        var actual = lodashStable.map(positions, function (pos) {
            return _.slice.apply(_, [array].concat(pos));
        });
        assert.deepEqual(actual, [
            [1],
            [1],
            [1],
            [
                2,
                __num_top__
            ],
            [1],
            []
        ]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(2);
        var array = [
                [1],
                [
                    2,
                    3
                ]
            ], actual = lodashStable.map(array, _.slice);
        assert.deepEqual(actual, array);
        assert.notStrictEqual(actual, array);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(38);
        if (!isNpm) {
            var array = lodashStable.range(1, LARGE_ARRAY_SIZE + 1), length = array.length, wrapped = _(array);
            lodashStable.each([
                'map',
                'filter'
            ], function (methodName) {
                assert.deepEqual(wrapped[methodName]().slice(0, -1).value(), array.slice(0, -1));
                assert.deepEqual(wrapped[methodName]().slice(1).value(), array.slice(1));
                assert.deepEqual(wrapped[methodName]().slice(__num_top__, 3).value(), array.slice(1, __num_top__));
                assert.deepEqual(wrapped[methodName]().slice(-1).value(), array.slice(-1));
                assert.deepEqual(wrapped[methodName]().slice(length).value(), array.slice(length));
                assert.deepEqual(wrapped[methodName]().slice(3, 2).value(), array.slice(3, 2));
                assert.deepEqual(wrapped[methodName]().slice(0, -length).value(), array.slice(0, -length));
                assert.deepEqual(wrapped[methodName]().slice(0, null).value(), array.slice(0, null));
                assert.deepEqual(wrapped[methodName]().slice(0, length).value(), array.slice(0, length));
                assert.deepEqual(wrapped[methodName]().slice(-length).value(), array.slice(-length));
                assert.deepEqual(wrapped[methodName]().slice(null).value(), array.slice(null));
                assert.deepEqual(wrapped[methodName]().slice(0, 1).value(), array.slice(0, 1));
                assert.deepEqual(wrapped[methodName]().slice(NaN, '1').value(), array.slice(NaN, '1'));
                assert.deepEqual(wrapped[methodName]().slice(0.1, 1.1).value(), array.slice(0.1, __num_top__));
                assert.deepEqual(wrapped[methodName]().slice('0', 1).value(), array.slice('0', 1));
                assert.deepEqual(wrapped[methodName]().slice(0, '1').value(), array.slice(0, '1'));
                assert.deepEqual(wrapped[methodName]().slice('1').value(), array.slice('1'));
                assert.deepEqual(wrapped[methodName]().slice(NaN, __num_top__).value(), array.slice(NaN, 1));
                assert.deepEqual(wrapped[methodName]().slice(1, NaN).value(), array.slice(1, NaN));
            });
        } else {
            skipAssert(assert, 38);
        }
    });
}());