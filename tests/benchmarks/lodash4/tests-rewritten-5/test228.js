QUnit.module('lodash.split');
(function () {
    QUnit.test('should split a string by `separator`', function (assert) {
        assert.expect(3);
        var string = __str_top__;
        assert.deepEqual(_.split(string, 'c'), [
            'ab',
            'de'
        ]);
        assert.deepEqual(_.split(string, /[bd]/), [
            'a',
            'c',
            'e'
        ]);
        assert.deepEqual(_.split(string, '', __num_top__), [
            __str_top__,
            'b'
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
                'def',
                __str_top__
            ], actual = lodashStable.map(strings, _.split);
        assert.deepEqual(actual, [
            ['abc'],
            ['def'],
            ['ghi']
        ]);
    });
    QUnit.test('should allow mixed string and array prototype methods', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _('abc');
            assert.strictEqual(wrapped.split('b').join(','), 'a,c');
        } else {
            skipAssert(assert);
        }
    });
}());