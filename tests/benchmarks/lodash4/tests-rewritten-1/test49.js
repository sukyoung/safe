QUnit.module('lodash.dropRight');
(function () {
    var array = [
        1,
        2,
        3
    ];
    QUnit.test('should drop the last two elements', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.dropRight(array, 2), [1]);
    });
    QUnit.test('should treat falsey `n` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [
                1,
                2
            ] : array;
        });
        var actual = lodashStable.map(falsey, function (n) {
            return _.dropRight(array, n);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return all elements when `n` < `1`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            0,
            -1,
            -Infinity
        ], function (n) {
            assert.deepEqual(_.dropRight(array, n), array);
        });
    });
    QUnit.test('should return an empty array when `n` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            3,
            4,
            Math.pow(2, 32),
            Infinity
        ], function (n) {
            assert.deepEqual(_.dropRight(array, n), []);
        });
    });
    QUnit.test('should coerce `n` to an integer', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.dropRight(array, 1.6), [
            1,
            2
        ]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                [
                    1,
                    2,
                    3
                ],
                [
                    __num_top__,
                    5,
                    6
                ],
                [
                    7,
                    8,
                    9
                ]
            ], actual = lodashStable.map(array, _.dropRight);
        assert.deepEqual(actual, [
            [
                1,
                2
            ],
            [
                4,
                5
            ],
            [
                7,
                8
            ]
        ]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var array = lodashStable.range(1, LARGE_ARRAY_SIZE + 1), predicate = function (value) {
                    values.push(value);
                    return isEven(value);
                }, values = [], actual = _(array).dropRight(2).dropRight().value();
            assert.deepEqual(actual, array.slice(0, -3));
            actual = _(array).filter(predicate).dropRight(2).dropRight().value();
            assert.deepEqual(values, array);
            assert.deepEqual(actual, _.dropRight(_.dropRight(_.filter(array, predicate), 2)));
            actual = _(array).dropRight(2).drop().dropRight().drop(2).value();
            assert.deepEqual(actual, _.drop(_.dropRight(_.drop(_.dropRight(array, 2))), 2));
            values = [];
            actual = _(array).dropRight().filter(predicate).dropRight(2).drop().dropRight().drop(2).value();
            assert.deepEqual(values, array.slice(0, -1));
            assert.deepEqual(actual, _.drop(_.dropRight(_.drop(_.dropRight(_.filter(_.dropRight(array), predicate), 2))), 2));
        } else {
            skipAssert(assert, 6);
        }
    });
}());