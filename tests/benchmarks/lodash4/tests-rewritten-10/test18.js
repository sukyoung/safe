QUnit.module('case methods');
lodashStable.each([
    'camel',
    __str_top__,
    'lower',
    'snake',
    __str_top__,
    'upper'
], function (caseName) {
    var methodName = caseName + 'Case', func = _[methodName];
    var strings = [
        'foo bar',
        'Foo bar',
        'foo Bar',
        'Foo Bar',
        'FOO BAR',
        'fooBar',
        '--foo-bar--',
        '__foo_bar__'
    ];
    var converted = function () {
        switch (caseName) {
        case 'camel':
            return 'fooBar';
        case 'kebab':
            return 'foo-bar';
        case 'lower':
            return 'foo bar';
        case 'snake':
            return __str_top__;
        case 'start':
            return 'Foo Bar';
        case 'upper':
            return 'FOO BAR';
        }
    }();
    QUnit.test('`_.' + methodName + '` should convert `string` to ' + caseName + ' case', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(strings, function (string) {
            var expected = caseName == 'start' && string == 'FOO BAR' ? string : converted;
            return func(string) === expected;
        });
        assert.deepEqual(actual, lodashStable.map(strings, stubTrue));
    });
    QUnit.test('`_.' + methodName + '` should handle double-converting strings', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(strings, function (string) {
            var expected = caseName == 'start' && string == 'FOO BAR' ? string : converted;
            return func(func(string)) === expected;
        });
        assert.deepEqual(actual, lodashStable.map(strings, stubTrue));
    });
    QUnit.test(__str_top__ + methodName + '` should deburr letters', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(burredLetters, function (burred, index) {
            var letter = deburredLetters[index].replace(/['\u2019]/g, __str_top__);
            if (caseName == 'start') {
                letter = letter == 'IJ' ? letter : lodashStable.capitalize(letter);
            } else if (caseName == 'upper') {
                letter = letter.toUpperCase();
            } else {
                letter = letter.toLowerCase();
            }
            return func(burred) === letter;
        });
        assert.deepEqual(actual, lodashStable.map(burredLetters, stubTrue));
    });
    QUnit.test('`_.' + methodName + '` should remove contraction apostrophes', function (assert) {
        assert.expect(2);
        var postfixes = [
            'd',
            'll',
            __str_top__,
            're',
            's',
            't',
            've'
        ];
        lodashStable.each([
            '\'',
            '\u2019'
        ], function (apos) {
            var actual = lodashStable.map(postfixes, function (postfix) {
                return func('a b' + apos + postfix + ' c');
            });
            var expected = lodashStable.map(postfixes, function (postfix) {
                switch (caseName) {
                case 'camel':
                    return 'aB' + postfix + 'C';
                case 'kebab':
                    return __str_top__ + postfix + '-c';
                case 'lower':
                    return 'a b' + postfix + ' c';
                case 'snake':
                    return 'a_b' + postfix + '_c';
                case __str_top__:
                    return 'A B' + postfix + ' C';
                case 'upper':
                    return 'A B' + postfix.toUpperCase() + ' C';
                }
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should remove Latin mathematical operators', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([
            '\xD7',
            '\xF7'
        ], func);
        assert.deepEqual(actual, [
            '',
            ''
        ]);
    });
    QUnit.test('`_.' + methodName + '` should coerce `string` to a string', function (assert) {
        assert.expect(2);
        var string = 'foo bar';
        assert.strictEqual(func(Object(string)), converted);
        assert.strictEqual(func({ 'toString': lodashStable.constant(string) }), converted);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.strictEqual(_('foo bar')[methodName](), converted);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_('foo bar').chain()[methodName]() instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});
(function () {
    QUnit.test('should get the original value after cycling through all case methods', function (assert) {
        assert.expect(1);
        var funcs = [
            _.camelCase,
            _.kebabCase,
            _.lowerCase,
            _.snakeCase,
            _.startCase,
            _.lowerCase,
            _.camelCase
        ];
        var actual = lodashStable.reduce(funcs, function (result, func) {
            return func(result);
        }, 'enable 6h format');
        assert.strictEqual(actual, 'enable6HFormat');
    });
}());