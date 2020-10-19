QUnit.module('lodash.find and lodash.includes');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isIncludes = methodName == __str_top__, resolve = methodName == __str_top__ ? lodashStable.curry(lodashStable.eq) : identity;
    lodashStable.each({
        'an `arguments` object': args,
        'an array': [
            __num_top__,
            __num_top__,
            __num_top__
        ]
    }, function (collection, key) {
        var values = lodashStable.toArray(collection);
        QUnit.test(__str_top__ + methodName + __str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var expected = [
                isIncludes || values[__num_top__],
                isIncludes ? __bool_top__ : undefined
            ];
            var actual = [
                func(collection, resolve(values[__num_top__]), __num_top__),
                func(collection, resolve(values[__num_top__]), __num_top__)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + methodName + __str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var indexes = [
                __num_top__,
                __num_top__,
                Math.pow(__num_top__, __num_top__),
                Infinity
            ];
            var expected = lodashStable.map(indexes, function () {
                var result = isIncludes ? __bool_top__ : undefined;
                return [
                    result,
                    result,
                    result
                ];
            });
            var actual = lodashStable.map(indexes, function (fromIndex) {
                return [
                    func(collection, resolve(__num_top__), fromIndex),
                    func(collection, resolve(undefined), fromIndex),
                    func(collection, resolve(__str_top__), fromIndex)
                ];
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + methodName + __str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var expected = lodashStable.map(falsey, lodashStable.constant(isIncludes || values[__num_top__]));
            var actual = lodashStable.map(falsey, function (fromIndex) {
                return func(collection, resolve(values[__num_top__]), fromIndex);
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + methodName + __str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var expected = [
                isIncludes || values[__num_top__],
                isIncludes || values[__num_top__],
                isIncludes ? __bool_top__ : undefined
            ];
            var actual = [
                func(collection, resolve(values[__num_top__]), __num_top__),
                func(collection, resolve(values[__num_top__]), NaN),
                func(collection, resolve(values[__num_top__]), __str_top__)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + methodName + __str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var expected = [
                isIncludes || values[__num_top__],
                isIncludes ? __bool_top__ : undefined
            ];
            var actual = [
                func(collection, resolve(values[__num_top__]), -__num_top__),
                func(collection, resolve(values[__num_top__]), -__num_top__)
            ];
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + methodName + __str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var indexes = [
                    -__num_top__,
                    -__num_top__,
                    -Infinity
                ], expected = lodashStable.map(indexes, lodashStable.constant(isIncludes || values[__num_top__]));
            var actual = lodashStable.map(indexes, function (fromIndex) {
                return func(collection, resolve(values[__num_top__]), fromIndex);
            });
            assert.deepEqual(actual, expected);
        });
    });
});