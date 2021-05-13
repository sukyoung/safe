QUnit.module('set methods');
lodashStable.each([
    'update',
    'updateWith',
    __str_top__,
    'setWith'
], function (methodName) {
    var func = _[methodName], isUpdate = /^update/.test(methodName);
    var oldValue = 1, value = 2, updater = isUpdate ? lodashStable.constant(value) : value;
    QUnit.test('`_.' + methodName + '` should set property values', function (assert) {
        assert.expect(4);
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            var object = { 'a': oldValue }, actual = func(object, path, updater);
            assert.strictEqual(actual, object);
            assert.strictEqual(object.a, value);
        });
    });
    QUnit.test('`_.' + methodName + '` should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var props = [
                -0,
                Object(-0),
                0,
                Object(0)
            ], expected = lodashStable.map(props, lodashStable.constant(value));
        var actual = lodashStable.map(props, function (key) {
            var object = {
                '-0': 'a',
                '0': 'b'
            };
            func(object, key, updater);
            return object[lodashStable.toString(key)];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should unset symbol keyed property values', function (assert) {
        assert.expect(2);
        if (Symbol) {
            var object = {};
            object[symbol] = __num_top__;
            assert.strictEqual(_.unset(object, symbol), true);
            assert.notOk(symbol in object);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('`_.' + methodName + '` should set deep property values', function (assert) {
        assert.expect(4);
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            var object = { 'a': { 'b': oldValue } }, actual = func(object, path, updater);
            assert.strictEqual(actual, object);
            assert.strictEqual(object.a.b, value);
        });
    });
    QUnit.test(__str_top__ + methodName + '` should set a key over a path', function (assert) {
        assert.expect(4);
        lodashStable.each([
            'a.b',
            ['a.b']
        ], function (path) {
            var object = { 'a.b': oldValue }, actual = func(object, path, updater);
            assert.strictEqual(actual, object);
            assert.deepEqual(object, { 'a.b': value });
        });
    });
    QUnit.test('`_.' + methodName + '` should not coerce array paths to strings', function (assert) {
        assert.expect(1);
        var object = {
            'a,b,c': 1,
            'a': { 'b': { 'c': 1 } }
        };
        func(object, [
            'a',
            'b',
            'c'
        ], updater);
        assert.strictEqual(object.a.b.c, value);
    });
    QUnit.test('`_.' + methodName + '` should not ignore empty brackets', function (assert) {
        assert.expect(1);
        var object = {};
        func(object, 'a[]', updater);
        assert.deepEqual(object, { 'a': { '': value } });
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
        ], function (pair, index) {
            var object = {};
            func(object, pair[0], updater);
            assert.deepEqual(object, index ? {} : { '': value });
            func(object, pair[1], updater);
            assert.deepEqual(object, { '': value });
        });
    });
    QUnit.test('`_.' + methodName + '` should handle complex paths', function (assert) {
        assert.expect(2);
        var object = { 'a': { '1.23': { '["b"]': { 'c': { '[\'d\']': { '\ne\n': { 'f': { 'g': oldValue } } } } } } } };
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
            func(object, path, updater);
            assert.strictEqual(object.a[-1.23]['["b"]'].c['[\'d\']']['\ne\n'].f.g, value);
            object.a[-1.23][__str_top__].c['[\'d\']']['\ne\n'].f.g = oldValue;
        });
    });
    QUnit.test('`_.' + methodName + '` should create parts of `path` that are missing', function (assert) {
        assert.expect(6);
        var object = {};
        lodashStable.each([
            'a[1].b.c',
            [
                __str_top__,
                '1',
                'b',
                'c'
            ]
        ], function (path) {
            var actual = func(object, path, updater);
            assert.strictEqual(actual, object);
            assert.deepEqual(actual, {
                'a': [
                    undefined,
                    { 'b': { 'c': value } }
                ]
            });
            assert.notOk('0' in object.a);
            delete object.a;
        });
    });
    QUnit.test('`_.' + methodName + '` should not error when `object` is nullish', function (assert) {
        assert.expect(1);
        var values = [
                null,
                undefined
            ], expected = [
                [
                    null,
                    null
                ],
                [
                    undefined,
                    undefined
                ]
            ];
        var actual = lodashStable.map(values, function (value) {
            try {
                return [
                    func(value, 'a.b', updater),
                    func(value, [
                        'a',
                        'b'
                    ], updater)
                ];
            } catch (e) {
                return e.message;
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should overwrite primitives in the path', function (assert) {
        assert.expect(2);
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            var object = { 'a': '' };
            func(object, path, updater);
            assert.deepEqual(object, { 'a': { 'b': 2 } });
        });
        ;
    });
    QUnit.test('`_.' + methodName + '` should not create an array for missing non-index property names that start with numbers', function (assert) {
        assert.expect(1);
        var object = {};
        func(object, [
            '1a',
            '2b',
            '3c'
        ], updater);
        assert.deepEqual(object, { '1a': { '2b': { '3c': value } } });
    });
    QUnit.test('`_.' + methodName + '` should not assign values that are the same as their destinations', function (assert) {
        assert.expect(4);
        lodashStable.each([
            'a',
            ['a'],
            { 'a': 1 },
            NaN
        ], function (value) {
            var object = {}, pass = true, updater = isUpdate ? lodashStable.constant(value) : value;
            defineProperty(object, 'a', {
                'configurable': true,
                'enumerable': true,
                'get': lodashStable.constant(value),
                'set': function () {
                    pass = false;
                }
            });
            func(object, 'a', updater);
            assert.ok(pass);
        });
    });
});