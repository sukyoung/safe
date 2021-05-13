QUnit.module('lodash.template');
(function () {
    QUnit.test('should escape values in "escape" delimiters', function (assert) {
        assert.expect(1);
        var strings = [
                __str_top__,
                __str_top__,
                __str_top__
            ], expected = lodashStable.map(strings, lodashStable.constant(__str_top__)), data = { 'value': __str_top__ };
        var actual = lodashStable.map(strings, function (string) {
            return _.template(string)(data);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not reference `_.escape` when "escape" delimiters are not used', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__);
        assert.strictEqual(compiled({}), __str_top__);
    });
    QUnit.test('should evaluate JavaScript in "evaluate" delimiters', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__);
        var data = {
                'collection': {
                    'a': __str_top__,
                    'b': __str_top__
                }
            }, actual = compiled(data);
        assert.strictEqual(actual, __str_top__);
    });
    QUnit.test('should support "evaluate" delimiters with single line comments (test production builds)', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__), data = { 'value': __bool_top__ };
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should support referencing variables declared in "evaluate" delimiters from other delimiters', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__), data = { 'a': { 'value': __num_top__ } };
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should interpolate data properties in "interpolate" delimiters', function (assert) {
        assert.expect(1);
        var strings = [
                __str_top__,
                __str_top__,
                __str_top__
            ], expected = lodashStable.map(strings, lodashStable.constant(__str_top__)), data = { 'a': __str_top__ };
        var actual = lodashStable.map(strings, function (string) {
            return _.template(string)(data);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should support "interpolate" delimiters with escaped values', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__), data = { 'a': __bool_top__ };
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should support "interpolate" delimiters containing ternary operators', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__), data = { 'value': __str_top__ };
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should support "interpolate" delimiters containing global values', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__);
        try {
            var actual = compiled();
        } catch (e) {
        }
        assert.strictEqual(actual, __str_top__);
    });
    QUnit.test('should support complex "interpolate" delimiters', function (assert) {
        assert.expect(22);
        lodashStable.forOwn({
            '<%= a + b %>': __str_top__,
            '<%= b - a %>': __str_top__,
            '<%= a = b %>': __str_top__,
            '<%= !a %>': __str_top__,
            '<%= ~a %>': __str_top__,
            '<%= a * b %>': __str_top__,
            '<%= a / b %>': __str_top__,
            '<%= a % b %>': __str_top__,
            '<%= a >> b %>': __str_top__,
            '<%= a << b %>': __str_top__,
            '<%= a & b %>': __str_top__,
            '<%= a ^ b %>': __str_top__,
            '<%= a | b %>': __str_top__,
            '<%= {}.toString.call(0) %>': numberTag,
            '<%= a.toFixed(2) %>': __str_top__,
            '<%= obj["a"] %>': __str_top__,
            '<%= delete a %>': __str_top__,
            '<%= "a" in obj %>': __str_top__,
            '<%= obj instanceof Object %>': __str_top__,
            '<%= new Boolean %>': __str_top__,
            '<%= typeof a %>': __str_top__,
            '<%= void a %>': __str_top__
        }, function (value, key) {
            var compiled = _.template(key), data = {
                    'a': __num_top__,
                    'b': __num_top__
                };
            assert.strictEqual(compiled(data), value, key);
        });
    });
    QUnit.test('should support ES6 template delimiters', function (assert) {
        assert.expect(2);
        var data = { 'value': __num_top__ };
        assert.strictEqual(_.template(__str_top__)(data), __str_top__);
        assert.strictEqual(_.template(__str_top__)(data), __str_top__);
    });
    QUnit.test('should support the "imports" option', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__, { 'imports': { 'a': __num_top__ } });
        assert.strictEqual(compiled({}), __str_top__);
    });
    QUnit.test('should support the "variable" options', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__ + __str_top__ + __str_top__, { 'variable': __str_top__ });
        var data = {
            'a': [
                __num_top__,
                __num_top__,
                __num_top__
            ]
        };
        try {
            assert.strictEqual(compiled(data), __str_top__);
        } catch (e) {
            assert.ok(__bool_top__, e.message);
        }
    });
    QUnit.test('should support custom delimiters', function (assert) {
        assert.expect(2);
        lodashStable.times(__num_top__, function (index) {
            var settingsClone = lodashStable.clone(_.templateSettings);
            var settings = lodashStable.assign(index ? _.templateSettings : {}, {
                'escape': /\{\{-([\s\S]+?)\}\}/g,
                'evaluate': /\{\{([\s\S]+?)\}\}/g,
                'interpolate': /\{\{=([\s\S]+?)\}\}/g
            });
            var expected = __str_top__, compiled = _.template(__str_top__, index ? null : settings), data = {
                    'collection': [
                        __str_top__,
                        __str_top__
                    ]
                };
            assert.strictEqual(compiled(data), expected);
            lodashStable.assign(_.templateSettings, settingsClone);
        });
    });
    QUnit.test('should support custom delimiters containing special characters', function (assert) {
        assert.expect(2);
        lodashStable.times(__num_top__, function (index) {
            var settingsClone = lodashStable.clone(_.templateSettings);
            var settings = lodashStable.assign(index ? _.templateSettings : {}, {
                'escape': /<\?-([\s\S]+?)\?>/g,
                'evaluate': /<\?([\s\S]+?)\?>/g,
                'interpolate': /<\?=([\s\S]+?)\?>/g
            });
            var expected = __str_top__, compiled = _.template(__str_top__, index ? null : settings), data = {
                    'collection': [
                        __str_top__,
                        __str_top__
                    ]
                };
            assert.strictEqual(compiled(data), expected);
            lodashStable.assign(_.templateSettings, settingsClone);
        });
    });
    QUnit.test('should use a `with` statement by default', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__), actual = compiled({
                'index': __num_top__,
                'collection': [
                    __str_top__,
                    __str_top__,
                    __str_top__
                ]
            });
        assert.strictEqual(actual, __str_top__);
    });
    QUnit.test('should use `_.templateSettings.imports._.templateSettings`', function (assert) {
        assert.expect(1);
        var lodash = _.templateSettings.imports._, settingsClone = lodashStable.clone(lodash.templateSettings);
        lodash.templateSettings = lodashStable.assign(lodash.templateSettings, { 'interpolate': /\{\{=([\s\S]+?)\}\}/g });
        var compiled = _.template(__str_top__);
        assert.strictEqual(compiled({ 'a': __num_top__ }), __str_top__);
        if (settingsClone) {
            lodashStable.assign(lodash.templateSettings, settingsClone);
        } else {
            delete lodash.templateSettings;
        }
    });
    QUnit.test('should fallback to `_.templateSettings`', function (assert) {
        assert.expect(1);
        var lodash = _.templateSettings.imports._, delimiter = _.templateSettings.interpolate;
        _.templateSettings.imports._ = { 'escape': lodashStable.escape };
        _.templateSettings.interpolate = /\{\{=([\s\S]+?)\}\}/g;
        var compiled = _.template(__str_top__);
        assert.strictEqual(compiled({ 'a': __num_top__ }), __str_top__);
        _.templateSettings.imports._ = lodash;
        _.templateSettings.interpolate = delimiter;
    });
    QUnit.test('should ignore `null` delimiters', function (assert) {
        assert.expect(3);
        var delimiter = {
            'escape': /\{\{-([\s\S]+?)\}\}/g,
            'evaluate': /\{\{([\s\S]+?)\}\}/g,
            'interpolate': /\{\{=([\s\S]+?)\}\}/g
        };
        lodashStable.forOwn({
            'escape': __str_top__,
            'evaluate': __str_top__,
            'interpolate': __str_top__
        }, function (value, key) {
            var settings = {
                'escape': null,
                'evaluate': null,
                'interpolate': null
            };
            settings[key] = delimiter[key];
            var expected = __str_top__, compiled = _.template(value + __str_top__, settings), data = { 'a': __num_top__ };
            assert.strictEqual(compiled(data), expected);
        });
    });
    QUnit.test('should work without delimiters', function (assert) {
        assert.expect(1);
        var expected = __str_top__;
        assert.strictEqual(_.template(expected)({}), expected);
    });
    QUnit.test('should work with `this` references', function (assert) {
        assert.expect(2);
        var compiled = _.template(__str_top__);
        assert.strictEqual(compiled(), __str_top__);
        var object = { 'b': __str_top__ };
        object.compiled = _.template(__str_top__, { 'variable': __str_top__ });
        assert.strictEqual(object.compiled(), __str_top__);
    });
    QUnit.test('should work with backslashes', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__), data = { 'a': __str_top__ };
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should work with escaped characters in string literals', function (assert) {
        assert.expect(2);
        var compiled = _.template(__str_top__);
        assert.strictEqual(compiled(), __str_top__);
        var data = { 'a': __str_top__ };
        compiled = _.template(__str_top__);
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should handle \\u2028 & \\u2029 characters', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__);
        assert.strictEqual(compiled(), __str_top__);
    });
    QUnit.test('should work with statements containing quotes', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__);
        var data = { 'a': __str_top__ };
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should work with templates containing newlines and comments', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__);
        assert.strictEqual(compiled({ 'value': __num_top__ }), __str_top__);
    });
    QUnit.test('should tokenize delimiters', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__), data = { 'type': __num_top__ };
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should evaluate delimiters once', function (assert) {
        assert.expect(1);
        var actual = [], compiled = _.template(__str_top__), data = {
                'func': function (value) {
                    actual.push(value);
                }
            };
        compiled(data);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should match delimiters before escaping text', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__, { 'evaluate': /<<(.*?)>>/g });
        assert.strictEqual(compiled(), __str_top__);
    });
    QUnit.test('should resolve nullish values to an empty string', function (assert) {
        assert.expect(3);
        var compiled = _.template(__str_top__), data = { 'a': null };
        assert.strictEqual(compiled(data), __str_top__);
        data = { 'a': undefined };
        assert.strictEqual(compiled(data), __str_top__);
        data = { 'a': {} };
        compiled = _.template(__str_top__);
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should return an empty string for empty values', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined,
                __str_top__
            ], expected = lodashStable.map(values, stubString), data = { 'a': __num_top__ };
        var actual = lodashStable.map(values, function (value, index) {
            var compiled = index ? _.template(value) : _.template();
            return compiled(data);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should parse delimiters without newlines', function (assert) {
        assert.expect(1);
        var expected = __str_top__, compiled = _.template(expected, { 'evaluate': /<<(.+?)>>/g }), data = { 'value': __bool_top__ };
        assert.strictEqual(compiled(data), expected);
    });
    QUnit.test('should support recursive calls', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__), data = {
                'a': __str_top__,
                'b': __str_top__,
                'c': __str_top__
            };
        assert.strictEqual(compiled(data), __str_top__);
    });
    QUnit.test('should coerce `text` to a string', function (assert) {
        assert.expect(1);
        var object = { 'toString': lodashStable.constant(__str_top__) }, data = { 'a': __num_top__ };
        assert.strictEqual(_.template(object)(data), __str_top__);
    });
    QUnit.test('should not modify the `options` object', function (assert) {
        assert.expect(1);
        var options = {};
        _.template(__str_top__, options);
        assert.deepEqual(options, {});
    });
    QUnit.test('should not modify `_.templateSettings` when `options` are given', function (assert) {
        assert.expect(2);
        var data = { 'a': __num_top__ };
        assert.notOk(__str_top__ in _.templateSettings);
        _.template(__str_top__, {}, data);
        assert.notOk(__str_top__ in _.templateSettings);
        delete _.templateSettings.a;
    });
    QUnit.test('should not error for non-object `data` and `options` values', function (assert) {
        assert.expect(2);
        _.template(__str_top__)(__num_top__);
        assert.ok(__bool_top__, __str_top__);
        _.template(__str_top__, __num_top__)(__num_top__);
        assert.ok(__bool_top__, __str_top__);
    });
    QUnit.test('should expose the source on compiled templates', function (assert) {
        assert.expect(1);
        var compiled = _.template(__str_top__), values = [
                String(compiled),
                compiled.source
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.includes(value, __str_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should expose the source on SyntaxErrors', function (assert) {
        assert.expect(1);
        try {
            _.template(__str_top__);
        } catch (e) {
            var source = e.source;
        }
        assert.ok(lodashStable.includes(source, __str_top__));
    });
    QUnit.test('should not include sourceURLs in the source', function (assert) {
        assert.expect(1);
        var options = { 'sourceURL': __str_top__ }, compiled = _.template(__str_top__, options), values = [
                compiled.source,
                undefined
            ];
        try {
            _.template(__str_top__, options);
        } catch (e) {
            values[__num_top__] = e.source;
        }
        var expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.includes(value, __str_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not let a sourceURL inject code', function (assert) {
        assert.expect(1);
        var actual, expected = __str_top__;
        try {
            actual = _.template(expected, { 'sourceURL': __str_top__ })();
        } catch (e) {
        }
        assert.equal(actual, expected);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                __str_top__,
                __str_top__,
                __str_top__
            ], compiles = lodashStable.map(array, _.template), data = {
                'a': __str_top__,
                'b': __str_top__,
                'c': __str_top__
            };
        var actual = lodashStable.map(compiles, function (compiled) {
            return compiled(data);
        });
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
}());