QUnit.module('lodash.template');
(function () {
    QUnit.test('should escape values in "escape" delimiters', function (assert) {
        assert.expect(1);
        var strings = [
                '<p><%- value %></p>',
                '<p><%-value%></p>',
                '<p><%-\nvalue\n%></p>'
            ], expected = lodashStable.map(strings, lodashStable.constant('<p>&amp;&lt;&gt;&quot;&#39;/</p>')), data = { 'value': '&<>"\'/' };
        var actual = lodashStable.map(strings, function (string) {
            return _.template(string)(data);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not reference `_.escape` when "escape" delimiters are not used', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%= typeof __e %>');
        assert.strictEqual(compiled({}), __str_top__);
    });
    QUnit.test('should evaluate JavaScript in "evaluate" delimiters', function (assert) {
        assert.expect(1);
        var compiled = _.template('<ul><%      for (var key in collection) {        %><li><%= collection[key] %></li><%      } %></ul>');
        var data = {
                'collection': {
                    'a': 'A',
                    'b': 'B'
                }
            }, actual = compiled(data);
        assert.strictEqual(actual, '<ul><li>A</li><li>B</li></ul>');
    });
    QUnit.test('should support "evaluate" delimiters with single line comments (test production builds)', function (assert) {
        assert.expect(1);
        var compiled = _.template('<% // A code comment. %><% if (value) { %>yap<% } else { %>nope<% } %>'), data = { 'value': true };
        assert.strictEqual(compiled(data), 'yap');
    });
    QUnit.test('should support referencing variables declared in "evaluate" delimiters from other delimiters', function (assert) {
        assert.expect(1);
        var compiled = _.template('<% var b = a; %><%= b.value %>'), data = { 'a': { 'value': 1 } };
        assert.strictEqual(compiled(data), '1');
    });
    QUnit.test('should interpolate data properties in "interpolate" delimiters', function (assert) {
        assert.expect(1);
        var strings = [
                '<%= a %>BC',
                '<%=a%>BC',
                '<%=\na\n%>BC'
            ], expected = lodashStable.map(strings, lodashStable.constant('ABC')), data = { 'a': 'A' };
        var actual = lodashStable.map(strings, function (string) {
            return _.template(string)(data);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should support "interpolate" delimiters with escaped values', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%= a ? "a=\\"A\\"" : "" %>'), data = { 'a': true };
        assert.strictEqual(compiled(data), 'a="A"');
    });
    QUnit.test('should support "interpolate" delimiters containing ternary operators', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%= value ? value : "b" %>'), data = { 'value': 'a' };
        assert.strictEqual(compiled(data), 'a');
    });
    QUnit.test('should support "interpolate" delimiters containing global values', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%= typeof Math.abs %>');
        try {
            var actual = compiled();
        } catch (e) {
        }
        assert.strictEqual(actual, 'function');
    });
    QUnit.test('should support complex "interpolate" delimiters', function (assert) {
        assert.expect(22);
        lodashStable.forOwn({
            '<%= a + b %>': '3',
            '<%= b - a %>': '1',
            '<%= a = b %>': '2',
            '<%= !a %>': 'false',
            '<%= ~a %>': '-2',
            '<%= a * b %>': '2',
            '<%= a / b %>': '0.5',
            '<%= a % b %>': '1',
            '<%= a >> b %>': '0',
            '<%= a << b %>': '4',
            '<%= a & b %>': '0',
            '<%= a ^ b %>': '3',
            '<%= a | b %>': '3',
            '<%= {}.toString.call(0) %>': numberTag,
            '<%= a.toFixed(2) %>': '1.00',
            '<%= obj["a"] %>': '1',
            '<%= delete a %>': 'true',
            '<%= "a" in obj %>': 'true',
            '<%= obj instanceof Object %>': 'true',
            '<%= new Boolean %>': 'false',
            '<%= typeof a %>': 'number',
            '<%= void a %>': ''
        }, function (value, key) {
            var compiled = _.template(key), data = {
                    'a': 1,
                    'b': 2
                };
            assert.strictEqual(compiled(data), value, key);
        });
    });
    QUnit.test('should support ES6 template delimiters', function (assert) {
        assert.expect(2);
        var data = { 'value': 2 };
        assert.strictEqual(_.template('1${value}3')(data), '123');
        assert.strictEqual(_.template('${"{" + value + "\\}"}')(data), '{2}');
    });
    QUnit.test('should support the "imports" option', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%= a %>', { 'imports': { 'a': 1 } });
        assert.strictEqual(compiled({}), '1');
    });
    QUnit.test('should support the "variable" options', function (assert) {
        assert.expect(1);
        var compiled = _.template('<% _.each( data.a, function( value ) { %>' + '<%= value.valueOf() %>' + '<% }) %>', { 'variable': 'data' });
        var data = {
            'a': [
                1,
                2,
                3
            ]
        };
        try {
            assert.strictEqual(compiled(data), '123');
        } catch (e) {
            assert.ok(__bool_top__, e.message);
        }
    });
    QUnit.test('should support custom delimiters', function (assert) {
        assert.expect(2);
        lodashStable.times(2, function (index) {
            var settingsClone = lodashStable.clone(_.templateSettings);
            var settings = lodashStable.assign(index ? _.templateSettings : {}, {
                'escape': /\{\{-([\s\S]+?)\}\}/g,
                'evaluate': /\{\{([\s\S]+?)\}\}/g,
                'interpolate': /\{\{=([\s\S]+?)\}\}/g
            });
            var expected = '<ul><li>0: a &amp; A</li><li>1: b &amp; B</li></ul>', compiled = _.template('<ul>{{ _.each(collection, function(value, index) {}}<li>{{= index }}: {{- value }}</li>{{}); }}</ul>', index ? null : settings), data = {
                    'collection': [
                        'a & A',
                        'b & B'
                    ]
                };
            assert.strictEqual(compiled(data), expected);
            lodashStable.assign(_.templateSettings, settingsClone);
        });
    });
    QUnit.test('should support custom delimiters containing special characters', function (assert) {
        assert.expect(2);
        lodashStable.times(2, function (index) {
            var settingsClone = lodashStable.clone(_.templateSettings);
            var settings = lodashStable.assign(index ? _.templateSettings : {}, {
                'escape': /<\?-([\s\S]+?)\?>/g,
                'evaluate': /<\?([\s\S]+?)\?>/g,
                'interpolate': /<\?=([\s\S]+?)\?>/g
            });
            var expected = '<ul><li>0: a &amp; A</li><li>1: b &amp; B</li></ul>', compiled = _.template('<ul><? _.each(collection, function(value, index) { ?><li><?= index ?>: <?- value ?></li><? }); ?></ul>', index ? null : settings), data = {
                    'collection': [
                        'a & A',
                        'b & B'
                    ]
                };
            assert.strictEqual(compiled(data), expected);
            lodashStable.assign(_.templateSettings, settingsClone);
        });
    });
    QUnit.test('should use a `with` statement by default', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%= index %><%= collection[index] %><% _.each(collection, function(value, index) { %><%= index %><% }); %>'), actual = compiled({
                'index': 1,
                'collection': [
                    'a',
                    'b',
                    'c'
                ]
            });
        assert.strictEqual(actual, '1b012');
    });
    QUnit.test('should use `_.templateSettings.imports._.templateSettings`', function (assert) {
        assert.expect(1);
        var lodash = _.templateSettings.imports._, settingsClone = lodashStable.clone(lodash.templateSettings);
        lodash.templateSettings = lodashStable.assign(lodash.templateSettings, { 'interpolate': /\{\{=([\s\S]+?)\}\}/g });
        var compiled = _.template(__str_top__);
        assert.strictEqual(compiled({ 'a': 1 }), '1');
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
        var compiled = _.template('{{= a }}');
        assert.strictEqual(compiled({ 'a': 1 }), '1');
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
            'escape': '{{- a }}',
            'evaluate': '{{ print(a) }}',
            'interpolate': '{{= a }}'
        }, function (value, key) {
            var settings = {
                'escape': null,
                'evaluate': null,
                'interpolate': null
            };
            settings[key] = delimiter[key];
            var expected = '1 <%- a %> <% print(a) %> <%= a %>', compiled = _.template(value + ' <%- a %> <% print(a) %> <%= a %>', settings), data = { 'a': 1 };
            assert.strictEqual(compiled(data), expected);
        });
    });
    QUnit.test('should work without delimiters', function (assert) {
        assert.expect(1);
        var expected = 'abc';
        assert.strictEqual(_.template(expected)({}), expected);
    });
    QUnit.test('should work with `this` references', function (assert) {
        assert.expect(2);
        var compiled = _.template('a<%= this.String("b") %>c');
        assert.strictEqual(compiled(), 'abc');
        var object = { 'b': 'B' };
        object.compiled = _.template('A<%= this.b %>C', { 'variable': 'obj' });
        assert.strictEqual(object.compiled(), 'ABC');
    });
    QUnit.test('should work with backslashes', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%= a %> \\b'), data = { 'a': 'A' };
        assert.strictEqual(compiled(data), 'A \\b');
    });
    QUnit.test('should work with escaped characters in string literals', function (assert) {
        assert.expect(2);
        var compiled = _.template(__str_top__);
        assert.strictEqual(compiled(), '\'\n\r\t\u2028\u2029\\');
        var data = { 'a': 'A' };
        compiled = _.template('\'\n\r\t<%= a %>\u2028\u2029\\"');
        assert.strictEqual(compiled(data), '\'\n\r\tA\u2028\u2029\\"');
    });
    QUnit.test('should handle \\u2028 & \\u2029 characters', function (assert) {
        assert.expect(1);
        var compiled = _.template('\u2028<%= "\\u2028\\u2029" %>\u2029');
        assert.strictEqual(compiled(), '\u2028\u2028\u2029\u2029');
    });
    QUnit.test('should work with statements containing quotes', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%      if (a == \'A\' || a == "a") {        %>\'a\',"A"<%      } %>');
        var data = { 'a': 'A' };
        assert.strictEqual(compiled(data), '\'a\',"A"');
    });
    QUnit.test('should work with templates containing newlines and comments', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%\n      // A code comment.\n      if (value) { value += 3; }\n      %><p><%= value %></p>');
        assert.strictEqual(compiled({ 'value': 3 }), '<p>6</p>');
    });
    QUnit.test('should tokenize delimiters', function (assert) {
        assert.expect(1);
        var compiled = _.template('<span class="icon-<%= type %>2"></span>'), data = { 'type': 1 };
        assert.strictEqual(compiled(data), '<span class="icon-12"></span>');
    });
    QUnit.test('should evaluate delimiters once', function (assert) {
        assert.expect(1);
        var actual = [], compiled = _.template('<%= func("a") %><%- func("b") %><% func("c") %>'), data = {
                'func': function (value) {
                    actual.push(value);
                }
            };
        compiled(data);
        assert.deepEqual(actual, [
            'a',
            'b',
            'c'
        ]);
    });
    QUnit.test('should match delimiters before escaping text', function (assert) {
        assert.expect(1);
        var compiled = _.template('<<\n a \n>>', { 'evaluate': /<<(.*?)>>/g });
        assert.strictEqual(compiled(), '<<\n a \n>>');
    });
    QUnit.test('should resolve nullish values to an empty string', function (assert) {
        assert.expect(3);
        var compiled = _.template('<%= a %><%- a %>'), data = { 'a': null };
        assert.strictEqual(compiled(data), '');
        data = { 'a': undefined };
        assert.strictEqual(compiled(data), '');
        data = { 'a': {} };
        compiled = _.template('<%= a.b %><%- a.b %>');
        assert.strictEqual(compiled(data), '');
    });
    QUnit.test('should return an empty string for empty values', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined,
                ''
            ], expected = lodashStable.map(values, stubString), data = { 'a': 1 };
        var actual = lodashStable.map(values, function (value, index) {
            var compiled = index ? _.template(value) : _.template();
            return compiled(data);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should parse delimiters without newlines', function (assert) {
        assert.expect(1);
        var expected = '<<\nprint("<p>" + (value ? "yes" : "no") + "</p>")\n>>', compiled = _.template(expected, { 'evaluate': /<<(.+?)>>/g }), data = { 'value': true };
        assert.strictEqual(compiled(data), expected);
    });
    QUnit.test('should support recursive calls', function (assert) {
        assert.expect(1);
        var compiled = _.template('<%= a %><% a = _.template(c)(obj) %><%= a %>'), data = {
                'a': 'A',
                'b': 'B',
                'c': '<%= b %>'
            };
        assert.strictEqual(compiled(data), 'AB');
    });
    QUnit.test('should coerce `text` to a string', function (assert) {
        assert.expect(1);
        var object = { 'toString': lodashStable.constant('<%= a %>') }, data = { 'a': 1 };
        assert.strictEqual(_.template(object)(data), '1');
    });
    QUnit.test('should not modify the `options` object', function (assert) {
        assert.expect(1);
        var options = {};
        _.template('', options);
        assert.deepEqual(options, {});
    });
    QUnit.test('should not modify `_.templateSettings` when `options` are given', function (assert) {
        assert.expect(2);
        var data = { 'a': 1 };
        assert.notOk('a' in _.templateSettings);
        _.template('', {}, data);
        assert.notOk('a' in _.templateSettings);
        delete _.templateSettings.a;
    });
    QUnit.test('should not error for non-object `data` and `options` values', function (assert) {
        assert.expect(2);
        _.template('')(1);
        assert.ok(true, '`data` value');
        _.template('', 1)(1);
        assert.ok(true, '`options` value');
    });
    QUnit.test('should expose the source on compiled templates', function (assert) {
        assert.expect(1);
        var compiled = _.template('x'), values = [
                String(compiled),
                compiled.source
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.includes(value, '__p');
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should expose the source on SyntaxErrors', function (assert) {
        assert.expect(1);
        try {
            _.template('<% if x %>');
        } catch (e) {
            var source = e.source;
        }
        assert.ok(lodashStable.includes(source, '__p'));
    });
    QUnit.test('should not include sourceURLs in the source', function (assert) {
        assert.expect(1);
        var options = { 'sourceURL': '/a/b/c' }, compiled = _.template('x', options), values = [
                compiled.source,
                undefined
            ];
        try {
            _.template('<% if x %>', options);
        } catch (e) {
            values[1] = e.source;
        }
        var expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.includes(value, 'sourceURL');
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not let a sourceURL inject code', function (assert) {
        assert.expect(1);
        var actual, expected = 'no error';
        try {
            actual = _.template(expected, { 'sourceURL': '\u2028\u2029\n!this would err if it was executed!' })();
        } catch (e) {
        }
        assert.equal(actual, expected);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                '<%= a %>',
                '<%- b %>',
                '<% print(c) %>'
            ], compiles = lodashStable.map(array, _.template), data = {
                'a': 'one',
                'b': '"two"',
                'c': 'three'
            };
        var actual = lodashStable.map(compiles, function (compiled) {
            return compiled(data);
        });
        assert.deepEqual(actual, [
            'one',
            '&quot;two&quot;',
            'three'
        ]);
    });
}());