QUnit.module('lodash.words');
(function () {
    QUnit.test('should match words containing Latin Unicode letters', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(burredLetters, function (letter) {
            return [letter];
        });
        var actual = lodashStable.map(burredLetters, function (letter) {
            return _.words(letter);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should support a `pattern`', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.words(__str_top__, /ab|cd/g), [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__, __str_top__), [__str_top__]);
    });
    QUnit.test('should work with compound words', function (assert) {
        assert.expect(12);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with compound words containing diacritical marks', function (assert) {
        assert.expect(3);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.words(__str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should not treat contractions as separate words', function (assert) {
        assert.expect(4);
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
            lodashStable.times(__num_top__, function (index) {
                var actual = lodashStable.map(postfixes, function (postfix) {
                    var string = __str_top__ + apos + postfix + __str_top__;
                    return _.words(string[index ? __str_top__ : __str_top__]());
                });
                var expected = lodashStable.map(postfixes, function (postfix) {
                    var words = [
                        __str_top__,
                        __str_top__ + apos + postfix,
                        __str_top__
                    ];
                    return lodashStable.map(words, function (word) {
                        return word[index ? __str_top__ : __str_top__]();
                    });
                });
                assert.deepEqual(actual, expected);
            });
        });
    });
    QUnit.test('should not treat ordinal numbers as separate words', function (assert) {
        assert.expect(2);
        var ordinals = [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ];
        lodashStable.times(__num_top__, function (index) {
            var expected = lodashStable.map(ordinals, function (ordinal) {
                return [ordinal[index ? __str_top__ : __str_top__]()];
            });
            var actual = lodashStable.map(expected, function (words) {
                return _.words(words[__num_top__]);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should not treat mathematical operators as words', function (assert) {
        assert.expect(1);
        var operators = [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ], expected = lodashStable.map(operators, stubArray), actual = lodashStable.map(operators, _.words);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not treat punctuation as words', function (assert) {
        assert.expect(1);
        var marks = [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ];
        var expected = lodashStable.map(marks, stubArray), actual = lodashStable.map(marks, _.words);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var strings = lodashStable.map([
                __str_top__,
                __str_top__,
                __str_top__
            ], Object), actual = lodashStable.map(strings, _.words);
        assert.deepEqual(actual, [
            [__str_top__],
            [__str_top__],
            [__str_top__]
        ]);
    });
    QUnit.test('should prevent ReDoS', function (assert) {
        assert.expect(2);
        var largeWordLen = __num_top__, largeWord = _.repeat(__str_top__, largeWordLen), maxMs = __num_top__, startTime = lodashStable.now();
        assert.deepEqual(_.words(largeWord + __str_top__), [
            largeWord,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        var endTime = lodashStable.now(), timeSpent = endTime - startTime;
        assert.ok(timeSpent < maxMs, __str_top__ + timeSpent + __str_top__);
    });
}());