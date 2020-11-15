QUnit.module('pad methods');
lodashStable.each([
    'pad',
    'padStart',
    'padEnd'
], function (methodName) {
    var func = _[methodName], isPad = methodName == 'pad', isStart = methodName == 'padStart', string = __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        assert.strictEqual(func(string, __num_top__), string);
        assert.strictEqual(func(string, __num_top__), string);
    });
    QUnit.test('`_.' + methodName + '` should treat negative `length` as `0`', function (assert) {
        assert.expect(2);
        lodashStable.each([
            0,
            -__num_top__
        ], function (length) {
            assert.strictEqual(func(string, length), string);
        });
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(2);
        lodashStable.each([
            '',
            '4'
        ], function (length) {
            var actual = length ? isStart ? ' abc' : __str_top__ : string;
            assert.strictEqual(func(string, length), actual);
        });
    });
    QUnit.test('`_.' + methodName + '` should treat nullish values as empty strings', function (assert) {
        assert.expect(6);
        lodashStable.each([
            undefined,
            '_-'
        ], function (chars) {
            var expected = chars ? isPad ? '__' : chars : '  ';
            assert.strictEqual(func(null, __num_top__, chars), expected);
            assert.strictEqual(func(undefined, 2, chars), expected);
            assert.strictEqual(func('', 2, chars), expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should return `string` when `chars` coerces to an empty string', function (assert) {
        assert.expect(1);
        var values = [
                '',
                Object(__str_top__)
            ], expected = lodashStable.map(values, lodashStable.constant(string));
        var actual = lodashStable.map(values, function (value) {
            return _.pad(string, 6, value);
        });
        assert.deepEqual(actual, expected);
    });
});