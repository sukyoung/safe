QUnit.module('uncommon symbols');
(function () {
    var flag = __str_top__, heart = __str_top__ + emojiVar, hearts = __str_top__, comboGlyph = __str_top__ + heart + __str_top__, hashKeycap = __str_top__ + emojiVar + __str_top__, leafs = __str_top__, mic = __str_top__, noMic = mic + __str_top__, raisedHand = __str_top__ + emojiVar, rocket = __str_top__, thumbsUp = __str_top__;
    QUnit.test('should account for astral symbols', function (assert) {
        assert.expect(34);
        var allHearts = _.repeat(hearts, __num_top__), chars = hearts + comboGlyph, string = __str_top__ + leafs + __str_top__ + comboGlyph + __str_top__ + rocket, trimChars = comboGlyph + hearts, trimString = trimChars + string + trimChars;
        assert.strictEqual(_.camelCase(hearts + __str_top__ + leafs), hearts + __str_top__ + leafs);
        assert.strictEqual(_.camelCase(string), __str_top__ + leafs + comboGlyph + __str_top__ + rocket);
        assert.strictEqual(_.capitalize(rocket), rocket);
        assert.strictEqual(_.pad(string, __num_top__), __str_top__ + string + __str_top__);
        assert.strictEqual(_.padStart(string, __num_top__), __str_top__ + string);
        assert.strictEqual(_.padEnd(string, __num_top__), string + __str_top__);
        assert.strictEqual(_.pad(string, __num_top__, chars), hearts + string + chars);
        assert.strictEqual(_.padStart(string, __num_top__, chars), chars + hearts + string);
        assert.strictEqual(_.padEnd(string, __num_top__, chars), string + chars + hearts);
        assert.strictEqual(_.size(string), __num_top__);
        assert.deepEqual(_.split(string, __str_top__), [
            __str_top__,
            leafs + __str_top__,
            comboGlyph + __str_top__,
            __str_top__,
            rocket
        ]);
        assert.deepEqual(_.split(string, __str_top__, __num_top__), [
            __str_top__,
            leafs + __str_top__,
            comboGlyph + __str_top__
        ]);
        assert.deepEqual(_.split(string, undefined), [string]);
        assert.deepEqual(_.split(string, undefined, -__num_top__), [string]);
        assert.deepEqual(_.split(string, undefined, __num_top__), []);
        var expected = [
            __str_top__,
            __str_top__,
            leafs,
            __str_top__,
            __str_top__,
            comboGlyph,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            rocket
        ];
        assert.deepEqual(_.split(string, __str_top__), expected);
        assert.deepEqual(_.split(string, __str_top__, __num_top__), expected.slice(__num_top__, __num_top__));
        assert.deepEqual(_.toArray(string), expected);
        assert.strictEqual(_.trim(trimString, chars), string);
        assert.strictEqual(_.trimStart(trimString, chars), string + trimChars);
        assert.strictEqual(_.trimEnd(trimString, chars), trimChars + string);
        assert.strictEqual(_.truncate(string, { 'length': __num_top__ }), string);
        assert.strictEqual(_.truncate(string, { 'length': __num_top__ }), __str_top__ + leafs + __str_top__);
        assert.deepEqual(_.words(string), [
            __str_top__,
            leafs,
            comboGlyph,
            __str_top__,
            rocket
        ]);
        assert.deepEqual(_.toArray(hashKeycap), [hashKeycap]);
        assert.deepEqual(_.toArray(noMic), [noMic]);
        lodashStable.times(__num_top__, function (index) {
            var separator = index ? RegExp(hearts) : hearts, options = {
                    'length': __num_top__,
                    'separator': separator
                }, actual = _.truncate(string, options);
            assert.strictEqual(actual, __str_top__);
            assert.strictEqual(actual.length, __num_top__);
            actual = _.truncate(allHearts, options);
            assert.strictEqual(actual, hearts + __str_top__);
            assert.strictEqual(actual.length, __num_top__);
        });
    });
    QUnit.test('should account for combining diacritical marks', function (assert) {
        assert.expect(1);
        var values = lodashStable.map(comboMarks, function (mark) {
            return __str_top__ + mark;
        });
        var expected = lodashStable.map(values, function (value) {
            return [
                __num_top__,
                [value],
                [value]
            ];
        });
        var actual = lodashStable.map(values, function (value) {
            return [
                _.size(value),
                _.toArray(value),
                _.words(value)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should account for fitzpatrick modifiers', function (assert) {
        assert.expect(1);
        var values = lodashStable.map(fitzModifiers, function (modifier) {
            return thumbsUp + modifier;
        });
        var expected = lodashStable.map(values, function (value) {
            return [
                __num_top__,
                [value],
                [value]
            ];
        });
        var actual = lodashStable.map(values, function (value) {
            return [
                _.size(value),
                _.toArray(value),
                _.words(value)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should account for regional symbols', function (assert) {
        assert.expect(6);
        var pair = flag.match(/\ud83c[\udde6-\uddff]/g), regionals = pair.join(__str_top__);
        assert.strictEqual(_.size(flag), __num_top__);
        assert.strictEqual(_.size(regionals), __num_top__);
        assert.deepEqual(_.toArray(flag), [flag]);
        assert.deepEqual(_.toArray(regionals), [
            pair[__num_top__],
            __str_top__,
            pair[__num_top__]
        ]);
        assert.deepEqual(_.words(flag), [flag]);
        assert.deepEqual(_.words(regionals), [
            pair[__num_top__],
            pair[__num_top__]
        ]);
    });
    QUnit.test('should account for variation selectors', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.size(heart), __num_top__);
        assert.deepEqual(_.toArray(heart), [heart]);
        assert.deepEqual(_.words(heart), [heart]);
    });
    QUnit.test('should account for variation selectors with fitzpatrick modifiers', function (assert) {
        assert.expect(1);
        var values = lodashStable.map(fitzModifiers, function (modifier) {
            return raisedHand + modifier;
        });
        var expected = lodashStable.map(values, function (value) {
            return [
                __num_top__,
                [value],
                [value]
            ];
        });
        var actual = lodashStable.map(values, function (value) {
            return [
                _.size(value),
                _.toArray(value),
                _.words(value)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should match lone surrogates', function (assert) {
        assert.expect(3);
        var pair = hearts.split(__str_top__), surrogates = pair[__num_top__] + __str_top__ + pair[__num_top__];
        assert.strictEqual(_.size(surrogates), __num_top__);
        assert.deepEqual(_.toArray(surrogates), [
            pair[__num_top__],
            __str_top__,
            pair[__num_top__]
        ]);
        assert.deepEqual(_.words(surrogates), []);
    });
    QUnit.test('should match side by side fitzpatrick modifiers separately ', function (assert) {
        assert.expect(1);
        var string = fitzModifiers[__num_top__] + fitzModifiers[__num_top__];
        assert.deepEqual(_.toArray(string), [
            fitzModifiers[__num_top__],
            fitzModifiers[__num_top__]
        ]);
    });
}());