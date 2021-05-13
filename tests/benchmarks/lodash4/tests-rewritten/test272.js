QUnit.module('uniqBy methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isSorted = methodName == __str_top__, objects = [
            { 'a': __num_top__ },
            { 'a': __num_top__ },
            { 'a': __num_top__ },
            { 'a': __num_top__ },
            { 'a': __num_top__ },
            { 'a': __num_top__ }
        ];
    if (isSorted) {
        objects = _.sortBy(objects, __str_top__);
    }
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = isSorted ? [
            { 'a': __num_top__ },
            { 'a': __num_top__ },
            { 'a': __num_top__ }
        ] : objects.slice(__num_top__, __num_top__);
        var actual = func(objects, function (object) {
            return object.a;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with large arrays', function (assert) {
        assert.expect(2);
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, function () {
            return [
                __num_top__,
                __num_top__
            ];
        });
        var actual = func(largeArray, String);
        assert.strictEqual(actual[__num_top__], largeArray[__num_top__]);
        assert.deepEqual(actual, [[
                __num_top__,
                __num_top__
            ]]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var args;
        func(objects, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [objects[__num_top__]]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var expected = isSorted ? [
                { 'a': __num_top__ },
                { 'a': __num_top__ },
                { 'a': __num_top__ }
            ] : objects.slice(__num_top__, __num_top__), actual = func(objects, __str_top__);
        assert.deepEqual(actual, expected);
        var arrays = [
            [__num_top__],
            [__num_top__],
            [__num_top__],
            [__num_top__],
            [__num_top__],
            [__num_top__]
        ];
        if (isSorted) {
            arrays = lodashStable.sortBy(arrays, __num_top__);
        }
        expected = isSorted ? [
            [__num_top__],
            [__num_top__],
            [__num_top__]
        ] : arrays.slice(__num_top__, __num_top__);
        actual = func(arrays, __num_top__);
        assert.deepEqual(actual, expected);
    });
    lodashStable.each({
        'an array': [
            __num_top__,
            __str_top__
        ],
        'an object': { '0': __str_top__ },
        'a number': __num_top__,
        'a string': __str_top__
    }, function (iteratee, key) {
        QUnit.test(__str_top__ + methodName + __str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            var actual = func([
                [__str_top__],
                [__str_top__],
                [__str_top__]
            ], iteratee);
            assert.deepEqual(actual, [
                [__str_top__],
                [__str_top__]
            ]);
        });
    });
});