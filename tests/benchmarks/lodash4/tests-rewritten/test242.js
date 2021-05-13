QUnit.module('lodash.takeWhile');
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
    QUnit.test('should take elements while `predicate` returns truthy', function (assert) {
        assert.expect(1);
        var actual = _.takeWhile(array, function (n) {
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
        _.takeWhile(array, function () {
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
        assert.deepEqual(_.takeWhile(objects, { 'b': __num_top__ }), objects.slice(__num_top__, __num_top__));
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.takeWhile(objects, [
            __str_top__,
            __num_top__
        ]), objects.slice(__num_top__, __num_top__));
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.takeWhile(objects, __str_top__), objects.slice(__num_top__, __num_top__));
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE), predicate = function (n) {
                    return n < __num_top__;
                }, expected = _.takeWhile(array, predicate), wrapped = _(array).takeWhile(predicate);
            assert.deepEqual(wrapped.value(), expected);
            assert.deepEqual(wrapped.reverse().value(), expected.slice().reverse());
            assert.strictEqual(wrapped.last(), _.last(expected));
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should work in a lazy sequence with `take`', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE);
            var actual = _(array).takeWhile(function (n) {
                return n < __num_top__;
            }).take(__num_top__).takeWhile(function (n) {
                return n == __num_top__;
            }).value();
            assert.deepEqual(actual, [__num_top__]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should provide correct `predicate` arguments in a lazy sequence', function (assert) {
        assert.expect(5);
        if (!isNpm) {
            var args, array = lodashStable.range(LARGE_ARRAY_SIZE + __num_top__), expected = [
                    __num_top__,
                    __num_top__,
                    lodashStable.map(array.slice(__num_top__), square)
                ];
            _(array).slice(__num_top__).takeWhile(function (value, index, array) {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, [
                __num_top__,
                __num_top__,
                array.slice(__num_top__)
            ]);
            _(array).slice(__num_top__).map(square).takeWhile(function (value, index, array) {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, expected);
            _(array).slice(__num_top__).map(square).takeWhile(function (value, index) {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, expected);
            _(array).slice(__num_top__).map(square).takeWhile(function (value) {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, [__num_top__]);
            _(array).slice(__num_top__).map(square).takeWhile(function () {
                args = slice.call(arguments);
            }).value();
            assert.deepEqual(args, expected);
        } else {
            skipAssert(assert, 5);
        }
    });
}());