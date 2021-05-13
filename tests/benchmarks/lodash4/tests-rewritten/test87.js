QUnit.module('lodash.includes');
(function () {
    lodashStable.each({
        'an `arguments` object': arguments,
        'an array': [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ],
        'an object': {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__,
            'd': __num_top__
        },
        'a string': __str_top__
    }, function (collection, key) {
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            assert.strictEqual(_.includes(collection, __num_top__), __bool_top__);
        });
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            assert.strictEqual(_.includes(collection, __num_top__), __bool_top__);
        });
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            assert.strictEqual(_.includes(collection, __num_top__, __num_top__), __bool_top__);
        });
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            if (!isNpm) {
                assert.strictEqual(_(collection).includes(__num_top__), __bool_top__);
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(1);
            if (!isNpm) {
                assert.ok(_(collection).chain().includes(__num_top__) instanceof _);
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each({
        'literal': __str_top__,
        'object': Object(__str_top__)
    }, function (collection, key) {
        QUnit.test(__str_top__ + key + __str_top__, function (assert) {
            assert.expect(2);
            assert.strictEqual(_.includes(collection, __str_top__), __bool_top__);
            assert.strictEqual(_.includes(collection, __str_top__), __bool_top__);
        });
    });
    QUnit.test('should return `false` for empty collections', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubFalse);
        var actual = lodashStable.map(empties, function (value) {
            try {
                return _.includes(value);
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with a string and a `fromIndex` >= `length`', function (assert) {
        assert.expect(1);
        var string = __str_top__, length = string.length, indexes = [
                __num_top__,
                __num_top__,
                Math.pow(__num_top__, __num_top__),
                Infinity
            ];
        var expected = lodashStable.map(indexes, function (index) {
            return [
                __bool_top__,
                __bool_top__,
                index == length
            ];
        });
        var actual = lodashStable.map(indexes, function (fromIndex) {
            return [
                _.includes(string, __num_top__, fromIndex),
                _.includes(string, undefined, fromIndex),
                _.includes(string, __str_top__, fromIndex)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should match `NaN`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.includes([
            __num_top__,
            NaN,
            __num_top__
        ], NaN), __bool_top__);
    });
    QUnit.test('should match `-0` as `0`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.includes([-__num_top__], __num_top__), __bool_top__);
        assert.strictEqual(_.includes([__num_top__], -__num_top__), __bool_top__);
    });
    QUnit.test('should work as an iteratee for methods like `_.every`', function (assert) {
        assert.expect(1);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], values = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        assert.ok(lodashStable.every(values, lodashStable.partial(_.includes, array)));
    });
}(__num_top__, __num_top__, __num_top__, __num_top__));