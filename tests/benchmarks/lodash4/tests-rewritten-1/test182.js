QUnit.module('lodash.parseInt');
(function () {
    QUnit.test('should accept a `radix`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.range(2, 37);
        var actual = lodashStable.map(expected, function (radix) {
            return _.parseInt('10', radix);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should use a radix of `10`, for non-hexadecimals, if `radix` is `undefined` or `0`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.parseInt('10'), 10);
        assert.strictEqual(_.parseInt('10', 0), 10);
        assert.strictEqual(_.parseInt('10', 10), 10);
        assert.strictEqual(_.parseInt('10', undefined), 10);
    });
    QUnit.test('should use a radix of `16`, for hexadecimals, if `radix` is `undefined` or `0`', function (assert) {
        assert.expect(8);
        lodashStable.each([
            '0x20',
            '0X20'
        ], function (string) {
            assert.strictEqual(_.parseInt(string), 32);
            assert.strictEqual(_.parseInt(string, 0), 32);
            assert.strictEqual(_.parseInt(string, 16), 32);
            assert.strictEqual(_.parseInt(string, undefined), 32);
        });
    });
    QUnit.test('should use a radix of `10` for string with leading zeros', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.parseInt('08'), 8);
        assert.strictEqual(_.parseInt('08', 10), 8);
    });
    QUnit.test('should parse strings with leading whitespace', function (assert) {
        assert.expect(2);
        var expected = [
            8,
            8,
            10,
            10,
            32,
            32,
            32,
            32
        ];
        lodashStable.times(2, function (index) {
            var actual = [], func = (index ? lodashBizarro || {} : _).parseInt;
            if (func) {
                lodashStable.times(2, function (otherIndex) {
                    var string = otherIndex ? '10' : '08';
                    actual.push(func(whitespace + string, 10), func(whitespace + string));
                });
                lodashStable.each([
                    '0x20',
                    '0X20'
                ], function (string) {
                    actual.push(func(whitespace + string), func(whitespace + string, 16));
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
        assert.strictEqual(_.parseInt('08', object), 8);
        assert.strictEqual(_.parseInt('0x20', object), 32);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(2);
        var strings = lodashStable.map([
                '6',
                '08',
                '10'
            ], Object), actual = lodashStable.map(strings, _.parseInt);
        assert.deepEqual(actual, [
            6,
            __num_top__,
            10
        ]);
        actual = lodashStable.map('123', _.parseInt);
        assert.deepEqual(actual, [
            1,
            2,
            3
        ]);
    });
}());