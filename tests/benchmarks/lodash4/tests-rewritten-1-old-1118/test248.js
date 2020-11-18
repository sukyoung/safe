QUnit.module('lodash.times');
(function () {
    QUnit.test('should coerce non-finite `n` values to `0`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            -Infinity,
            NaN,
            Infinity
        ], function (n) {
            assert.deepEqual(_.times(n), []);
        });
    });
    QUnit.test('should coerce `n` to an integer', function (assert) {
        assert.expect(1);
        var actual = _.times(2.6, _.identity);
        assert.deepEqual(actual, [
            0,
            1
        ]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.times(1, function (assert) {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [0]);
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                0,
                1,
                2
            ]));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.times(3, value) : _.times(3);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return an array of the results of each `iteratee` execution', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.times(__num_top__, doubled), [
            0,
            2,
            4
        ]);
    });
    QUnit.test('should return an empty array for falsey and negative `n` values', function (assert) {
        assert.expect(1);
        var values = falsey.concat(-1, -Infinity), expected = lodashStable.map(values, stubArray);
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.times(value) : _.times();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.deepEqual(_(3).times(), [
                0,
                1,
                2
            ]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(3).chain().times() instanceof _);
        } else {
            skipAssert(assert);
        }
    });
}());