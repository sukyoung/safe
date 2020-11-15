QUnit.module('lodash.split');
(function () {
    QUnit.test('should split a string by `separator`', function (assert) {
        assert.expect(3);
        var string = 'abcde';
        assert.deepEqual(_.split(string, __str_top__), [
            'ab',
            __str_top__
        ]);
        assert.deepEqual(_.split(string, /[bd]/), [
            'a',
            'c',
            __str_top__
        ]);
        assert.deepEqual(_.split(string, '', 2), [
            'a',
            __str_top__
        ]);
    });
    QUnit.test('should return an array containing an empty string for empty values', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined,
                ''
            ], expected = lodashStable.map(values, lodashStable.constant(['']));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.split(value) : _.split();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var strings = [
                'abc',
                __str_top__,
                'ghi'
            ], actual = lodashStable.map(strings, _.split);
        assert.deepEqual(actual, [
            ['abc'],
            [__str_top__],
            [__str_top__]
        ]);
    });
    QUnit.test('should allow mixed string and array prototype methods', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _(__str_top__);
            assert.strictEqual(wrapped.split(__str_top__).join(','), 'a,c');
        } else {
            skipAssert(assert);
        }
    });
}());