QUnit.module('case methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (caseName) {
    var methodName = caseName + __str_top__, func = _[methodName];
    var strings = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var converted = function () {
        switch (caseName) {
        case __str_top__:
            return __str_top__;
        case __str_top__:
            return __str_top__;
        case __str_top__:
            return __str_top__;
        case __str_top__:
            return __str_top__;
        case __str_top__:
            return __str_top__;
        case __str_top__:
            return __str_top__;
        }
    }();
    QUnit.test(__str_top__ + methodName + __str_top__ + caseName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(strings, function (string) {
            var expected = caseName == __str_top__ && string == __str_top__ ? string : converted;
            return func(string) === expected;
        });
        assert.deepEqual(actual, lodashStable.map(strings, stubTrue));
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(strings, function (string) {
            var expected = caseName == __str_top__ && string == __str_top__ ? string : converted;
            return func(func(string)) === expected;
        });
        assert.deepEqual(actual, lodashStable.map(strings, stubTrue));
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(burredLetters, function (burred, index) {
            var letter = deburredLetters[index].replace(/['\u2019]/g, __str_top__);
            if (caseName == __str_top__) {
                letter = letter == __str_top__ ? letter : lodashStable.capitalize(letter);
            } else if (caseName == __str_top__) {
                letter = letter.toUpperCase();
            } else {
                letter = letter.toLowerCase();
            }
            return func(burred) === letter;
        });
        assert.deepEqual(actual, lodashStable.map(burredLetters, stubTrue));
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var postfixes = [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ];
        lodashStable.each([
            __str_top__,
            __str_top__
        ], function (apos) {
            var actual = lodashStable.map(postfixes, function (postfix) {
                return func(__str_top__ + apos + postfix + __str_top__);
            });
            var expected = lodashStable.map(postfixes, function (postfix) {
                switch (caseName) {
                case __str_top__:
                    return __str_top__ + postfix + __str_top__;
                case __str_top__:
                    return __str_top__ + postfix + __str_top__;
                case __str_top__:
                    return __str_top__ + postfix + __str_top__;
                case __str_top__:
                    return __str_top__ + postfix + __str_top__;
                case __str_top__:
                    return __str_top__ + postfix + __str_top__;
                case __str_top__:
                    return __str_top__ + postfix.toUpperCase() + __str_top__;
                }
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([
            __str_top__,
            __str_top__
        ], func);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var string = __str_top__;
        assert.strictEqual(func(Object(string)), converted);
        assert.strictEqual(func({ 'toString': lodashStable.constant(string) }), converted);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.strictEqual(_(__str_top__)[methodName](), converted);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(__str_top__).chain()[methodName]() instanceof _);
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
        }, __str_top__);
        assert.strictEqual(actual, __str_top__);
    });
}());