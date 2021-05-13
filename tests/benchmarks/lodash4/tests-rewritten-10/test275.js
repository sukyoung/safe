QUnit.module('lodash.unset');
(function () {
    QUnit.test('should unset property values', function (assert) {
        assert.expect(4);
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            var object = {
                'a': 1,
                'c': 2
            };
            assert.strictEqual(_.unset(object, path), true);
            assert.deepEqual(object, { 'c': 2 });
        });
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var props = [
                -0,
                Object(-0),
                0,
                Object(0)
            ], expected = lodashStable.map(props, lodashStable.constant([
                true,
                false
            ]));
        var actual = lodashStable.map(props, function (key) {
            var object = {
                '-0': 'a',
                '0': 'b'
            };
            return [
                _.unset(object, key),
                lodashStable.toString(key) in object
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should unset symbol keyed property values', function (assert) {
        assert.expect(2);
        if (Symbol) {
            var object = {};
            object[symbol] = 1;
            assert.strictEqual(_.unset(object, symbol), true);
            assert.notOk(symbol in object);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should unset deep property values', function (assert) {
        assert.expect(4);
        lodashStable.each([
            'a.b',
            [
                __str_top__,
                'b'
            ]
        ], function (path) {
            var object = { 'a': { 'b': null } };
            assert.strictEqual(_.unset(object, path), true);
            assert.deepEqual(object, { 'a': {} });
        });
    });
    QUnit.test('should handle complex paths', function (assert) {
        assert.expect(4);
        var paths = [
            __str_top__,
            [
                'a',
                '-1.23',
                '["b"]',
                'c',
                '[\'d\']',
                '\ne\n',
                'f',
                __str_top__
            ]
        ];
        lodashStable.each(paths, function (path) {
            var object = { 'a': { '-1.23': { '["b"]': { 'c': { '[\'d\']': { '\ne\n': { 'f': { 'g': __num_top__ } } } } } } } };
            assert.strictEqual(_.unset(object, path), true);
            assert.notOk('g' in object.a[-1.23]['["b"]'].c['[\'d\']']['\ne\n'].f);
        });
    });
    QUnit.test('should return `true` for nonexistent paths', function (assert) {
        assert.expect(5);
        var object = { 'a': { 'b': { 'c': null } } };
        lodashStable.each([
            'z',
            'a.z',
            'a.b.z',
            'a.b.c.z'
        ], function (path) {
            assert.strictEqual(_.unset(object, path), true);
        });
        assert.deepEqual(object, { 'a': { 'b': { 'c': null } } });
    });
    QUnit.test('should not error when `object` is nullish', function (assert) {
        assert.expect(1);
        var values = [
                null,
                undefined
            ], expected = [
                [
                    true,
                    true
                ],
                [
                    true,
                    true
                ]
            ];
        var actual = lodashStable.map(values, function (value) {
            try {
                return [
                    _.unset(value, 'a.b'),
                    _.unset(value, [
                        __str_top__,
                        'b'
                    ])
                ];
            } catch (e) {
                return e.message;
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should follow `path` over non-plain objects', function (assert) {
        assert.expect(8);
        var object = { 'a': '' }, paths = [
                __str_top__,
                [
                    'constructor',
                    'prototype',
                    'a'
                ]
            ];
        lodashStable.each(paths, function (path) {
            numberProto.a = 1;
            var actual = _.unset(0, path);
            assert.strictEqual(actual, __bool_top__);
            assert.notOk(__str_top__ in numberProto);
            delete numberProto.a;
        });
        lodashStable.each([
            'a.replace.b',
            [
                'a',
                'replace',
                __str_top__
            ]
        ], function (path) {
            stringProto.replace.b = 1;
            var actual = _.unset(object, path);
            assert.strictEqual(actual, true);
            assert.notOk(__str_top__ in stringProto.replace);
            delete stringProto.replace.b;
        });
    });
    QUnit.test('should return `false` for non-configurable properties', function (assert) {
        assert.expect(1);
        var object = {};
        if (!isStrict) {
            defineProperty(object, 'a', {
                'configurable': false,
                'enumerable': true,
                'writable': true,
                'value': 1
            });
            assert.strictEqual(_.unset(object, 'a'), false);
        } else {
            skipAssert(assert);
        }
    });
}());