QUnit.module('lodash.parseInt');
(function () {
    QUnit.test('should accept a `radix`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.range(__num_top__, __num_top__);
        var actual = lodashStable.map(expected, function (radix) {
            return _.parseInt(__str_top__, radix);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should use a radix of `10`, for non-hexadecimals, if `radix` is `undefined` or `0`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.parseInt(__str_top__), __num_top__);
        assert.strictEqual(_.parseInt(__str_top__, __num_top__), __num_top__);
        assert.strictEqual(_.parseInt(__str_top__, __num_top__), __num_top__);
        assert.strictEqual(_.parseInt(__str_top__, undefined), __num_top__);
    });
    QUnit.test('should use a radix of `16`, for hexadecimals, if `radix` is `undefined` or `0`', function (assert) {
        assert.expect(8);
        lodashStable.each([
            __str_top__,
            __str_top__
        ], function (string) {
            assert.strictEqual(_.parseInt(string), __num_top__);
            assert.strictEqual(_.parseInt(string, __num_top__), __num_top__);
            assert.strictEqual(_.parseInt(string, __num_top__), __num_top__);
            assert.strictEqual(_.parseInt(string, undefined), __num_top__);
        });
    });
    QUnit.test('should use a radix of `10` for string with leading zeros', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.parseInt(__str_top__), __num_top__);
        assert.strictEqual(_.parseInt(__str_top__, __num_top__), __num_top__);
    });
    QUnit.test('should parse strings with leading whitespace', function (assert) {
        assert.expect(2);
        var expected = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
        lodashStable.times(__num_top__, function (index) {
            var actual = [], func = (index ? lodashBizarro || {} : _).parseInt;
            if (func) {
                lodashStable.times(__num_top__, function (otherIndex) {
                    var string = otherIndex ? __str_top__ : __str_top__;
                    actual.push(func(whitespace + string, __num_top__), func(whitespace + string));
                });
                lodashStable.each([
                    __str_top__,
                    __str_top__
                ], function (string) {
                    actual.push(func(whitespace + string), func(whitespace + string, __num_top__));
                });
                assert.deepEqual(actual, expected);
            } else {
                skipAssert(assert);
            }
        });
    });
    QUnit.test('should coerce `radix` to a number', function (assert) {
        assert.expect(2);
        var object = { 'valueOf': stubZero };
        assert.strictEqual(_.parseInt(__str_top__, object), __num_top__);
        assert.strictEqual(_.parseInt(__str_top__, object), __num_top__);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(2);
        var strings = lodashStable.map([
                __str_top__,
                __str_top__,
                __str_top__
            ], Object), actual = lodashStable.map(strings, _.parseInt);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        actual = lodashStable.map(__str_top__, _.parseInt);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
}());