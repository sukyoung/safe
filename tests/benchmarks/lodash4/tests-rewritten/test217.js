QUnit.module('set methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isUpdate = /^update/.test(methodName);
    var oldValue = __num_top__, value = __num_top__, updater = isUpdate ? lodashStable.constant(value) : value;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var object = { 'a': oldValue }, actual = func(object, path, updater);
            assert.strictEqual(actual, object);
            assert.strictEqual(object.a, value);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var props = [
                -__num_top__,
                Object(-__num_top__),
                __num_top__,
                Object(__num_top__)
            ], expected = lodashStable.map(props, lodashStable.constant(value));
        var actual = lodashStable.map(props, function (key) {
            var object = {
                '-0': __str_top__,
                '0': __str_top__
            };
            func(object, key, updater);
            return object[lodashStable.toString(key)];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        if (Symbol) {
            var object = {};
            object[symbol] = __num_top__;
            assert.strictEqual(_.unset(object, symbol), __bool_top__);
            assert.notOk(symbol in object);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var object = { 'a': { 'b': oldValue } }, actual = func(object, path, updater);
            assert.strictEqual(actual, object);
            assert.strictEqual(object.a.b, value);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var object = { 'a.b': oldValue }, actual = func(object, path, updater);
            assert.strictEqual(actual, object);
            assert.deepEqual(object, { 'a.b': value });
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
            'a,b,c': __num_top__,
            'a': { 'b': { 'c': __num_top__ } }
        };
        func(object, [
            __str_top__,
            __str_top__,
            __str_top__
        ], updater);
        assert.strictEqual(object.a.b.c, value);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {};
        func(object, __str_top__, updater);
        assert.deepEqual(object, { 'a': { '': value } });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        lodashStable.each([
            [
                __str_top__,
                __str_top__
            ],
            [
                [],
                [__str_top__]
            ]
        ], function (pair, index) {
            var object = {};
            func(object, pair[__num_top__], updater);
            assert.deepEqual(object, index ? {} : { '': value });
            func(object, pair[__num_top__], updater);
            assert.deepEqual(object, { '': value });
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = { 'a': { '1.23': { '["b"]': { 'c': { '[\'d\']': { '\ne\n': { 'f': { 'g': oldValue } } } } } } } };
        var paths = [
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ];
        lodashStable.each(paths, function (path) {
            func(object, path, updater);
            assert.strictEqual(object.a[-__num_top__][__str_top__].c[__str_top__][__str_top__].f.g, value);
            object.a[-__num_top__][__str_top__].c[__str_top__][__str_top__].f.g = oldValue;
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(6);
        var object = {};
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
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
            assert.notOk(__str_top__ in object.a);
            delete object.a;
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
                    func(value, __str_top__, updater),
                    func(value, [
                        __str_top__,
                        __str_top__
                    ], updater)
                ];
            } catch (e) {
                return e.message;
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var object = { 'a': __str_top__ };
            func(object, path, updater);
            assert.deepEqual(object, { 'a': { 'b': __num_top__ } });
        });
        ;
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {};
        func(object, [
            __str_top__,
            __str_top__,
            __str_top__
        ], updater);
        assert.deepEqual(object, { '1a': { '2b': { '3c': value } } });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            [__str_top__],
            { 'a': __num_top__ },
            NaN
        ], function (value) {
            var object = {}, pass = __bool_top__, updater = isUpdate ? lodashStable.constant(value) : value;
            defineProperty(object, __str_top__, {
                'configurable': __bool_top__,
                'enumerable': __bool_top__,
                'get': lodashStable.constant(value),
                'set': function () {
                    pass = __bool_top__;
                }
            });
            func(object, __str_top__, updater);
            assert.ok(pass);
        });
    });
});