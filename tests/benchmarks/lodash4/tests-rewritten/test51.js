QUnit.module('lodash.dropWhile');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__,
        __num_top__
    ];
    var objects = [
        {
            'a': __num_top__,
            'b': __num_top__
        },
        {
            'a': __num_top__,
            'b': __num_top__
        },
        {
            'a': __num_top__,
            'b': __num_top__
        }
    ];
    QUnit.test('should drop elements while `predicate` returns truthy', function (assert) {
        assert.expect(1);
        var actual = _.dropWhile(array, function (n) {
            return n < __num_top__;
        });
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should provide correct `predicate` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.dropWhile(array, function () {
            args = slice.call(arguments);
        });
        assert.deepEqual(args, [
            __num_top__,
            __num_top__,
            array
        ]);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.dropWhile(objects, { 'b': __num_top__ }), objects.slice(__num_top__));
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.dropWhile(objects, [
            __str_top__,
            __num_top__
        ]), objects.slice(__num_top__));
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.dropWhile(objects, __str_top__), objects.slice(__num_top__));
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var array = lodashStable.range(__num_top__, LARGE_ARRAY_SIZE + __num_top__), predicate = function (n) {
                    return n < __num_top__;
                }, expected = _.dropWhile(array, predicate), wrapped = _(array).dropWhile(predicate);
            assert.deepEqual(wrapped.value(), expected);
            assert.deepEqual(wrapped.reverse().value(), expected.slice().reverse());
            assert.strictEqual(wrapped.last(), _.last(expected));
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should work in a lazy sequence with `drop`', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var array = lodashStable.range(__num_top__, LARGE_ARRAY_SIZE + __num_top__);
            var actual = _(array).dropWhile(function (n) {
                return n == __num_top__;
            }).drop().dropWhile(function (n) {
                return n == __num_top__;
            }).value();
            assert.deepEqual(actual, array.slice(__num_top__));
        } else {
            skipAssert(assert);
        }
    });
}());