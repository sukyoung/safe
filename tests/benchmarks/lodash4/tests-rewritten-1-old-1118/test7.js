QUnit.module('lodash.ary');
(function () {
    function fn(a, b, c) {
        return slice.call(arguments);
    }
    QUnit.test('should cap the number of arguments provided to `func`', function (assert) {
        assert.expect(2);
        var actual = lodashStable.map([
            '6',
            '8',
            '10'
        ], _.ary(parseInt, 1));
        assert.deepEqual(actual, [
            6,
            8,
            10
        ]);
        var capped = _.ary(fn, 2);
        assert.deepEqual(capped('a', 'b', 'c', 'd'), [
            'a',
            'b'
        ]);
    });
    QUnit.test('should use `func.length` if `n` is not given', function (assert) {
        assert.expect(1);
        var capped = _.ary(fn);
        assert.deepEqual(capped('a', 'b', 'c', 'd'), [
            'a',
            'b',
            'c'
        ]);
    });
    QUnit.test('should treat a negative `n` as `0`', function (assert) {
        assert.expect(1);
        var capped = _.ary(fn, -1);
        try {
            var actual = capped('a');
        } catch (e) {
        }
        assert.deepEqual(actual, []);
    });
    QUnit.test('should coerce `n` to an integer', function (assert) {
        assert.expect(1);
        var values = [
                '1',
                1.6,
                'xyz'
            ], expected = [
                ['a'],
                ['a'],
                []
            ];
        var actual = lodashStable.map(values, function (n) {
            var capped = _.ary(fn, n);
            return capped('a', 'b');
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not force a minimum argument count', function (assert) {
        assert.expect(1);
        var args = [
                'a',
                'b',
                'c'
            ], capped = _.ary(fn, 3);
        var expected = lodashStable.map(args, function (arg, index) {
            return args.slice(0, index);
        });
        var actual = lodashStable.map(expected, function (array) {
            return capped.apply(undefined, array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(1);
        var capped = _.ary(function (a, b) {
                return this;
            }, 1), object = { 'capped': capped };
        assert.strictEqual(object.capped(), object);
    });
    QUnit.test('should use the existing `ary` if smaller', function (assert) {
        assert.expect(1);
        var capped = _.ary(_.ary(fn, 1), 2);
        assert.deepEqual(capped('a', 'b', 'c'), ['a']);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var funcs = lodashStable.map([fn], _.ary), actual = funcs[0]('a', 'b', 'c');
        assert.deepEqual(actual, [
            'a',
            'b',
            'c'
        ]);
    });
    QUnit.test('should work when combined with other methods that use metadata', function (assert) {
        assert.expect(2);
        var array = [
                __str_top__,
                'b',
                'c'
            ], includes = _.curry(_.rearg(_.ary(_.includes, 2), 1, 0), 2);
        assert.strictEqual(includes('b')(array, 2), true);
        if (!isNpm) {
            includes = _(_.includes).ary(2).rearg(1, 0).curry(2).value();
            assert.strictEqual(includes('b')(array, 2), true);
        } else {
            skipAssert(assert);
        }
    });
}());