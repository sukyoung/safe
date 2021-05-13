QUnit.module('lodash.get and lodash.result');
lodashStable.each([
    'get',
    'result'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` should get string keyed property values', function (assert) {
        assert.expect(2);
        var object = { 'a': 1 };
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            assert.strictEqual(func(object, path), 1);
        });
    });
    QUnit.test('`_.' + methodName + '` should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var object = {
                '-0': 'a',
                '0': 'b'
            }, props = [
                -0,
                Object(-0),
                0,
                Object(0)
            ];
        var actual = lodashStable.map(props, function (key) {
            return func(object, key);
        });
        assert.deepEqual(actual, [
            'a',
            'a',
            'b',
            'b'
        ]);
    });
    QUnit.test('`_.' + methodName + '` should get symbol keyed property values', function (assert) {
        assert.expect(1);
        if (Symbol) {
            var object = {};
            object[symbol] = 1;
            assert.strictEqual(func(object, symbol), 1);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` should get deep property values', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': 2 } };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), 2);
        });
    });
    QUnit.test('`_.' + methodName + '` should get a key over a path', function (assert) {
        assert.expect(2);
        var object = {
            'a.b': 1,
            'a': { 'b': 2 }
        };
        lodashStable.each([
            'a.b',
            ['a.b']
        ], function (path) {
            assert.strictEqual(func(object, path), 1);
        });
    });
    QUnit.test('`_.' + methodName + '` should not coerce array paths to strings', function (assert) {
        assert.expect(1);
        var object = {
            'a,b,c': 3,
            'a': { 'b': { 'c': 4 } }
        };
        assert.strictEqual(func(object, [
            'a',
            'b',
            'c'
        ]), 4);
    });
    QUnit.test(__str_top__ + methodName + '` should not ignore empty brackets', function (assert) {
        assert.expect(1);
        var object = { 'a': { '': 1 } };
        assert.strictEqual(func(object, 'a[]'), 1);
    });
    QUnit.test('`_.' + methodName + '` should handle empty paths', function (assert) {
        assert.expect(4);
        lodashStable.each([
            [
                '',
                ''
            ],
            [
                [],
                ['']
            ]
        ], function (pair) {
            assert.strictEqual(func({}, pair[0]), undefined);
            assert.strictEqual(func({ '': 3 }, pair[1]), 3);
        });
    });
    QUnit.test('`_.' + methodName + '` should handle complex paths', function (assert) {
        assert.expect(2);
        var object = { 'a': { '-1.23': { '["b"]': { 'c': { '[\'d\']': { '\ne\n': { 'f': { 'g': 8 } } } } } } } };
        var paths = [
            'a[-1.23]["[\\"b\\"]"].c[\'[\\\'d\\\']\'][\ne\n][f].g',
            [
                'a',
                '-1.23',
                '["b"]',
                'c',
                '[\'d\']',
                '\ne\n',
                'f',
                'g'
            ]
        ];
        lodashStable.each(paths, function (path) {
            assert.strictEqual(func(object, path), 8);
        });
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(4);
        lodashStable.each([
            'constructor',
            ['constructor']
        ], function (path) {
            assert.strictEqual(func(null, path), undefined);
            assert.strictEqual(func(undefined, path), undefined);
        });
    });
    QUnit.test('`_.' + methodName + '` should return `undefined` for deep paths when `object` is nullish', function (assert) {
        assert.expect(2);
        var values = [
                null,
                undefined
            ], expected = lodashStable.map(values, noop), paths = [
                'constructor.prototype.valueOf',
                [
                    'constructor',
                    'prototype',
                    'valueOf'
                ]
            ];
        lodashStable.each(paths, function (path) {
            var actual = lodashStable.map(values, function (value) {
                return func(value, path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should return `undefined` if parts of `path` are missing', function (assert) {
        assert.expect(2);
        var object = {
            'a': [
                ,
                null
            ]
        };
        lodashStable.each([
            'a[1].b.c',
            [
                'a',
                __str_top__,
                'b',
                'c'
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), undefined);
        });
    });
    QUnit.test('`_.' + methodName + '` should be able to return `null` values', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': null } };
        lodashStable.each([
            'a.b',
            [
                __str_top__,
                'b'
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), null);
        });
    });
    QUnit.test('`_.' + methodName + '` should follow `path` over non-plain objects', function (assert) {
        assert.expect(2);
        var paths = [
            'a.b',
            [
                'a',
                'b'
            ]
        ];
        lodashStable.each(paths, function (path) {
            numberProto.a = { 'b': 2 };
            assert.strictEqual(func(0, path), 2);
            delete numberProto.a;
        });
    });
    QUnit.test('`_.' + methodName + '` should return the default value for `undefined` values', function (assert) {
        assert.expect(2);
        var object = { 'a': {} }, values = empties.concat(true, new Date(), 1, /x/, 'a'), expected = lodashStable.map(values, function (value) {
                return [
                    value,
                    value
                ];
            });
        lodashStable.each([
            __str_top__,
            [
                'a',
                'b'
            ]
        ], function (path) {
            var actual = lodashStable.map(values, function (value) {
                return [
                    func(object, path, value),
                    func(null, path, value)
                ];
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should return the default value when `path` is empty', function (assert) {
        assert.expect(1);
        assert.strictEqual(func({}, [], 'a'), 'a');
    });
});