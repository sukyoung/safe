QUnit.module('lodash.findLast');
(function () {
    var resolve = lodashStable.curry(lodashStable.eq);
    lodashStable.each({
        'an `arguments` object': args,
        'an array': [
            1,
            2,
            3
        ]
    }, function (collection, key) {
        var values = lodashStable.toArray(collection);
        QUnit.test('should work with ' + key + ' and a positive `fromIndex`', function (assert) {
            assert.expect(1);
            var expected = [
                values[1],
                undefined
            ];
            var actual = [
                _.findLast(collection, resolve(values[1]), 1),
                _.findLast(collection, resolve(values[2]), 1)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test('should work with ' + key + ' and a `fromIndex` >= `length`', function (assert) {
            assert.expect(1);
            var indexes = [
                4,
                6,
                Math.pow(2, 32),
                Infinity
            ];
            var expected = lodashStable.map(indexes, lodashStable.constant([
                values[0],
                undefined,
                undefined
            ]));
            var actual = lodashStable.map(indexes, function (fromIndex) {
                return [
                    _.findLast(collection, resolve(1), fromIndex),
                    _.findLast(collection, resolve(undefined), fromIndex),
                    _.findLast(collection, resolve(''), fromIndex)
                ];
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test('should work with ' + key + ' and treat falsey `fromIndex` values correctly', function (assert) {
            assert.expect(1);
            var expected = lodashStable.map(falsey, function (value) {
                return value === undefined ? values[3] : undefined;
            });
            var actual = lodashStable.map(falsey, function (fromIndex) {
                return _.findLast(collection, resolve(values[3]), fromIndex);
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test('should work with ' + key + ' and coerce `fromIndex` to an integer', function (assert) {
            assert.expect(1);
            var expected = [
                values[0],
                values[0],
                undefined
            ];
            var actual = [
                _.findLast(collection, resolve(values[0]), 0.1),
                _.findLast(collection, resolve(values[0]), NaN),
                _.findLast(collection, resolve(values[2]), '1')
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test('should work with ' + key + ' and a negative `fromIndex`', function (assert) {
            assert.expect(1);
            var expected = [
                values[1],
                undefined
            ];
            var actual = [
                _.findLast(collection, resolve(values[1]), -2),
                _.findLast(collection, resolve(values[2]), -2)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test('should work with ' + key + ' and a negative `fromIndex` <= `-length`', function (assert) {
            assert.expect(1);
            var indexes = [
                    -__num_top__,
                    -6,
                    -Infinity
                ], expected = lodashStable.map(indexes, lodashStable.constant(values[0]));
            var actual = lodashStable.map(indexes, function (fromIndex) {
                return _.findLast(collection, resolve(values[0]), fromIndex);
            });
            assert.deepEqual(actual, expected);
        });
    });
}());