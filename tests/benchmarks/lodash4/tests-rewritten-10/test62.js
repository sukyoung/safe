QUnit.module('lodash.find and lodash.includes');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isIncludes = methodName == 'includes', resolve = methodName == 'find' ? lodashStable.curry(lodashStable.eq) : identity;
    lodashStable.each({
        'an `arguments` object': args,
        'an array': [
            1,
            2,
            3
        ]
    }, function (collection, key) {
        var values = lodashStable.toArray(collection);
        QUnit.test('`_.' + methodName + '` should work with ' + key + ' and a positive `fromIndex`', function (assert) {
            assert.expect(1);
            var expected = [
                isIncludes || values[2],
                isIncludes ? false : undefined
            ];
            var actual = [
                func(collection, resolve(values[2]), 2),
                func(collection, resolve(values[1]), __num_top__)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test('`_.' + methodName + '` should work with ' + key + ' and a `fromIndex` >= `length`', function (assert) {
            assert.expect(1);
            var indexes = [
                4,
                __num_top__,
                Math.pow(2, 32),
                Infinity
            ];
            var expected = lodashStable.map(indexes, function () {
                var result = isIncludes ? false : undefined;
                return [
                    result,
                    result,
                    result
                ];
            });
            var actual = lodashStable.map(indexes, function (fromIndex) {
                return [
                    func(collection, resolve(1), fromIndex),
                    func(collection, resolve(undefined), fromIndex),
                    func(collection, resolve(__str_top__), fromIndex)
                ];
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test('`_.' + methodName + '` should work with ' + key + __str_top__, function (assert) {
            assert.expect(1);
            var expected = lodashStable.map(falsey, lodashStable.constant(isIncludes || values[0]));
            var actual = lodashStable.map(falsey, function (fromIndex) {
                return func(collection, resolve(values[0]), fromIndex);
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + methodName + '` should work with ' + key + ' and coerce `fromIndex` to an integer', function (assert) {
            assert.expect(1);
            var expected = [
                isIncludes || values[0],
                isIncludes || values[0],
                isIncludes ? false : undefined
            ];
            var actual = [
                func(collection, resolve(values[0]), 0.1),
                func(collection, resolve(values[__num_top__]), NaN),
                func(collection, resolve(values[0]), '1')
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test('`_.' + methodName + '` should work with ' + key + ' and a negative `fromIndex`', function (assert) {
            assert.expect(1);
            var expected = [
                isIncludes || values[__num_top__],
                isIncludes ? false : undefined
            ];
            var actual = [
                func(collection, resolve(values[2]), -__num_top__),
                func(collection, resolve(values[1]), -1)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test('`_.' + methodName + '` should work with ' + key + ' and a negative `fromIndex` <= `-length`', function (assert) {
            assert.expect(1);
            var indexes = [
                    -4,
                    -6,
                    -Infinity
                ], expected = lodashStable.map(indexes, lodashStable.constant(isIncludes || values[0]));
            var actual = lodashStable.map(indexes, function (fromIndex) {
                return func(collection, resolve(values[0]), fromIndex);
            });
            assert.deepEqual(actual, expected);
        });
    });
});