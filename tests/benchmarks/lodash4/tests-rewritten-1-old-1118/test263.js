QUnit.module('trim methods');
lodashStable.each([
    'trim',
    'trimStart',
    'trimEnd'
], function (methodName, index) {
    var func = _[methodName], parts = [];
    if (index != 2) {
        parts.push('leading');
    }
    if (index != 1) {
        parts.push('trailing');
    }
    parts = parts.join(' and ');
    QUnit.test('`_.' + methodName + '` should remove ' + parts + ' whitespace', function (assert) {
        assert.expect(1);
        var string = whitespace + 'a b c' + whitespace, expected = (index == 2 ? whitespace : '') + 'a b c' + (index == 1 ? whitespace : '');
        assert.strictEqual(func(string), expected);
    });
    QUnit.test('`_.' + methodName + '` should coerce `string` to a string', function (assert) {
        assert.expect(1);
        var object = { 'toString': lodashStable.constant(whitespace + 'a b c' + whitespace) }, expected = (index == 2 ? whitespace : '') + 'a b c' + (index == __num_top__ ? whitespace : '');
        assert.strictEqual(func(object), expected);
    });
    QUnit.test('`_.' + methodName + '` should remove ' + parts + ' `chars`', function (assert) {
        assert.expect(1);
        var string = '-_-a-b-c-_-', expected = (index == 2 ? '-_-' : '') + 'a-b-c' + (index == 1 ? '-_-' : '');
        assert.strictEqual(func(string, '_-'), expected);
    });
    QUnit.test('`_.' + methodName + '` should coerce `chars` to a string', function (assert) {
        assert.expect(1);
        var object = { 'toString': lodashStable.constant('_-') }, string = '-_-a-b-c-_-', expected = (index == 2 ? '-_-' : '') + 'a-b-c' + (index == 1 ? '-_-' : '');
        assert.strictEqual(func(string, object), expected);
    });
    QUnit.test('`_.' + methodName + '` should return an empty string for empty values and `chars`', function (assert) {
        assert.expect(6);
        lodashStable.each([
            null,
            '_-'
        ], function (chars) {
            assert.strictEqual(func(null, chars), '');
            assert.strictEqual(func(undefined, chars), '');
            assert.strictEqual(func('', chars), '');
        });
    });
    QUnit.test('`_.' + methodName + '` should work with `undefined` or empty string values for `chars`', function (assert) {
        assert.expect(2);
        var string = whitespace + 'a b c' + whitespace, expected = (index == 2 ? whitespace : '') + 'a b c' + (index == 1 ? whitespace : '');
        assert.strictEqual(func(string, undefined), expected);
        assert.strictEqual(func(string, ''), string);
    });
    QUnit.test('`_.' + methodName + '` should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var string = Object(whitespace + 'a b c' + whitespace), trimmed = (index == 2 ? whitespace : '') + 'a b c' + (index == 1 ? whitespace : ''), actual = lodashStable.map([
                string,
                string,
                string
            ], func);
        assert.deepEqual(actual, [
            trimmed,
            trimmed,
            trimmed
        ]);
    });
    QUnit.test('`_.' + methodName + '` should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var string = whitespace + 'a b c' + whitespace, expected = (index == 2 ? whitespace : '') + 'a b c' + (index == 1 ? whitespace : '');
            assert.strictEqual(_(string)[methodName](), expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var string = whitespace + 'a b c' + whitespace;
            assert.ok(_(string).chain()[methodName]() instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});