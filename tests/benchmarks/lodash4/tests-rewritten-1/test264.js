QUnit.module('uncommon symbols');
(function () {
    var flag = '\uD83C\uDDFA\uD83C\uDDF8', heart = '\u2764' + emojiVar, hearts = '\uD83D\uDC95', comboGlyph = '\uD83D\uDC68‍' + heart + '‍\uD83D\uDC8B‍\uD83D\uDC68', hashKeycap = '#' + emojiVar + '\u20E3', leafs = '\uD83C\uDF42', mic = '\uD83C\uDF99', noMic = mic + '\u20E0', raisedHand = '\u270B' + emojiVar, rocket = '\uD83D\uDE80', thumbsUp = '\uD83D\uDC4D';
    QUnit.test('should account for astral symbols', function (assert) {
        assert.expect(34);
        var allHearts = _.repeat(hearts, 10), chars = hearts + comboGlyph, string = 'A ' + leafs + ', ' + comboGlyph + ', and ' + rocket, trimChars = comboGlyph + hearts, trimString = trimChars + string + trimChars;
        assert.strictEqual(_.camelCase(hearts + ' the ' + leafs), hearts + __str_top__ + leafs);
        assert.strictEqual(_.camelCase(string), 'a' + leafs + comboGlyph + 'And' + rocket);
        assert.strictEqual(_.capitalize(rocket), rocket);
        assert.strictEqual(_.pad(string, 16), ' ' + string + '  ');
        assert.strictEqual(_.padStart(string, 16), '   ' + string);
        assert.strictEqual(_.padEnd(string, 16), string + '   ');
        assert.strictEqual(_.pad(string, 16, chars), hearts + string + chars);
        assert.strictEqual(_.padStart(string, 16, chars), chars + hearts + string);
        assert.strictEqual(_.padEnd(string, 16, chars), string + chars + hearts);
        assert.strictEqual(_.size(string), 13);
        assert.deepEqual(_.split(string, ' '), [
            'A',
            leafs + ',',
            comboGlyph + ',',
            'and',
            rocket
        ]);
        assert.deepEqual(_.split(string, ' ', 3), [
            'A',
            leafs + ',',
            comboGlyph + ','
        ]);
        assert.deepEqual(_.split(string, undefined), [string]);
        assert.deepEqual(_.split(string, undefined, -1), [string]);
        assert.deepEqual(_.split(string, undefined, 0), []);
        var expected = [
            'A',
            ' ',
            leafs,
            ',',
            ' ',
            comboGlyph,
            ',',
            ' ',
            'a',
            'n',
            'd',
            ' ',
            rocket
        ];
        assert.deepEqual(_.split(string, ''), expected);
        assert.deepEqual(_.split(string, '', 6), expected.slice(0, 6));
        assert.deepEqual(_.toArray(string), expected);
        assert.strictEqual(_.trim(trimString, chars), string);
        assert.strictEqual(_.trimStart(trimString, chars), string + trimChars);
        assert.strictEqual(_.trimEnd(trimString, chars), trimChars + string);
        assert.strictEqual(_.truncate(string, { 'length': 13 }), string);
        assert.strictEqual(_.truncate(string, { 'length': 6 }), 'A ' + leafs + '...');
        assert.deepEqual(_.words(string), [
            'A',
            leafs,
            comboGlyph,
            'and',
            rocket
        ]);
        assert.deepEqual(_.toArray(hashKeycap), [hashKeycap]);
        assert.deepEqual(_.toArray(noMic), [noMic]);
        lodashStable.times(2, function (index) {
            var separator = index ? RegExp(hearts) : hearts, options = {
                    'length': 4,
                    'separator': separator
                }, actual = _.truncate(string, options);
            assert.strictEqual(actual, 'A...');
            assert.strictEqual(actual.length, 4);
            actual = _.truncate(allHearts, options);
            assert.strictEqual(actual, hearts + '...');
            assert.strictEqual(actual.length, 5);
        });
    });
    QUnit.test('should account for combining diacritical marks', function (assert) {
        assert.expect(1);
        var values = lodashStable.map(comboMarks, function (mark) {
            return 'o' + mark;
        });
        var expected = lodashStable.map(values, function (value) {
            return [
                1,
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
                1,
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
        var pair = flag.match(/\ud83c[\udde6-\uddff]/g), regionals = pair.join(' ');
        assert.strictEqual(_.size(flag), 1);
        assert.strictEqual(_.size(regionals), 3);
        assert.deepEqual(_.toArray(flag), [flag]);
        assert.deepEqual(_.toArray(regionals), [
            pair[0],
            ' ',
            pair[1]
        ]);
        assert.deepEqual(_.words(flag), [flag]);
        assert.deepEqual(_.words(regionals), [
            pair[0],
            pair[1]
        ]);
    });
    QUnit.test('should account for variation selectors', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.size(heart), 1);
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
                1,
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
        var pair = hearts.split(''), surrogates = pair[0] + ' ' + pair[1];
        assert.strictEqual(_.size(surrogates), 3);
        assert.deepEqual(_.toArray(surrogates), [
            pair[0],
            ' ',
            pair[1]
        ]);
        assert.deepEqual(_.words(surrogates), []);
    });
    QUnit.test('should match side by side fitzpatrick modifiers separately ', function (assert) {
        assert.expect(1);
        var string = fitzModifiers[0] + fitzModifiers[0];
        assert.deepEqual(_.toArray(string), [
            fitzModifiers[0],
            fitzModifiers[0]
        ]);
    });
}());