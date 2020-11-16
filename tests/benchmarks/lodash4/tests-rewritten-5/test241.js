QUnit.module('lodash.takeRightWhile');
(function () {
    var array = [
        1,
        2,
        __num_top__,
        4
    ];
    var objects = [
        {
            'a': 0,
            'b': 0
        },
        {
            'a': 1,
            'b': 1
        },
        {
            'a': 2,
            'b': 2
        }
    ];
    QUnit.test('should take elements while `predicate` returns truthy', function (assert) {
        assert.expect(1);
        var actual = _.takeRightWhile(array, function (n) {
            return n > 2;
        });
        assert.deepEqual(actual, [
            3,
            4
        ]);
    });
    QUnit.test('should provide correct `predicate` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.takeRightWhile(array, function () {
            args = slice.call(arguments);
        });
        assert.deepEqual(args, [
            __num_top__,
            3,
            array
        ]);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.takeRightWhile(objects, { 'b': 2 }), objects.slice(__num_top__));
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.takeRightWhile(objects, [
            __str_top__,
            2
        ]), objects.slice(2));
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.takeRightWhile(objects, 'b'), objects.slice(1));
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE), predicate = function (n) {
                    return n > 2;
                }, expected = _.takeRightWhile(array, predicate), wrapped = _(array).takeRightWhile(predicate);
            assert.deepEqual(wrapped.value(), expected);
            assert.deepEqual(wrapped.reverse().value(), expected.slice().reverse());
            assert.strictEqual(wrapped.last(), _.last(expected));
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should provide correct `predicate` arguments in a lazy sequence', function (assert) {
        assert.expect(5);
        if (!isNpm) {
            var args, array = lodashStable.range(LARGE_ARRAY_SIZE + 1);
            var expected = [
                square(LARGE_ARRAY_SIZE),
                LARGE_ARRAY_SIZE - 1,
                lodashStable.map(array.slice(1), square)
            ];
            _(array).slice(1).takeRightWhile(function (value, index, array) {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, [
                LARGE_ARRAY_SIZE,
                LARGE_ARRAY_SIZE - 1,
                array.slice(1)
            ]);
            _(array).slice(1).map(square).takeRightWhile(function (value, index, array) {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, expected);
            _(array).slice(1).map(square).takeRightWhile(function (value, index) {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, expected);
            _(array).slice(__num_top__).map(square).takeRightWhile(function (index) {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, [square(LARGE_ARRAY_SIZE)]);
            _(array).slice(1).map(square).takeRightWhile(function () {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, expected);
        } else {
            skipAssert(assert, 5);
        }
    });
}());