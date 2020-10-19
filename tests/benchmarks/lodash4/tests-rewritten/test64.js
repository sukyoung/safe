QUnit.module('lodash.findLast');
(function () {
    var resolve = lodashStable.curry(lodashStable.eq);
    lodashStable.each({
        'an `arguments` object': args,
        'an array': [
            __num_top__,
            __num_top__,
            __num_top__
        ]
    }, function (collection, key) {
        var values = lodashStable.toArray(collection);
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var expected = [
                values[__num_top__],
                undefined
            ];
            var actual = [
                _.findLast(collection, resolve(values[__num_top__]), __num_top__),
                _.findLast(collection, resolve(values[__num_top__]), __num_top__)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var indexes = [
                __num_top__,
                __num_top__,
                Math.pow(__num_top__, __num_top__),
                Infinity
            ];
            var expected = lodashStable.map(indexes, lodashStable.constant([
                values[__num_top__],
                undefined,
                undefined
            ]));
            var actual = lodashStable.map(indexes, function (fromIndex) {
                return [
                    _.findLast(collection, resolve(__num_top__), fromIndex),
                    _.findLast(collection, resolve(undefined), fromIndex),
                    _.findLast(collection, resolve(__str_top__), fromIndex)
                ];
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var expected = lodashStable.map(falsey, function (value) {
                return value === undefined ? values[__num_top__] : undefined;
            });
            var actual = lodashStable.map(falsey, function (fromIndex) {
                return _.findLast(collection, resolve(values[__num_top__]), fromIndex);
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var expected = [
                values[__num_top__],
                values[__num_top__],
                undefined
            ];
            var actual = [
                _.findLast(collection, resolve(values[__num_top__]), __num_top__),
                _.findLast(collection, resolve(values[__num_top__]), NaN),
                _.findLast(collection, resolve(values[__num_top__]), __str_top__)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var expected = [
                values[__num_top__],
                undefined
            ];
            var actual = [
                _.findLast(collection, resolve(values[__num_top__]), -__num_top__),
                _.findLast(collection, resolve(values[__num_top__]), -__num_top__)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var indexes = [
                    -__num_top__,
                    -__num_top__,
                    -Infinity
                ], expected = lodashStable.map(indexes, lodashStable.constant(values[__num_top__]));
            var actual = lodashStable.map(indexes, function (fromIndex) {
                return _.findLast(collection, resolve(values[__num_top__]), fromIndex);
            });
            assert.deepEqual(actual, expected);
        });
    });
}());