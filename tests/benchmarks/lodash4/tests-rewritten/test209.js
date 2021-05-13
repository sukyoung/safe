QUnit.module('lodash.get and lodash.result');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = { 'a': __num_top__ };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.strictEqual(func(object, path), __num_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                '-0': __str_top__,
                '0': __str_top__
            }, props = [
                -__num_top__,
                Object(-__num_top__),
                __num_top__,
                Object(__num_top__)
            ];
        var actual = lodashStable.map(props, function (key) {
            return func(object, key);
        });
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (Symbol) {
            var object = {};
            object[symbol] = __num_top__;
            assert.strictEqual(func(object, symbol), __num_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': __num_top__ } };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), __num_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = {
            'a.b': __num_top__,
            'a': { 'b': __num_top__ }
        };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.strictEqual(func(object, path), __num_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
            'a,b,c': __num_top__,
            'a': { 'b': { 'c': __num_top__ } }
        };
        assert.strictEqual(func(object, [
            __str_top__,
            __str_top__,
            __str_top__
        ]), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = { 'a': { '': __num_top__ } };
        assert.strictEqual(func(object, __str_top__), __num_top__);
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
        ], function (pair) {
            assert.strictEqual(func({}, pair[__num_top__]), undefined);
            assert.strictEqual(func({ '': __num_top__ }, pair[__num_top__]), __num_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = { 'a': { '-1.23': { '["b"]': { 'c': { '[\'d\']': { '\ne\n': { 'f': { 'g': __num_top__ } } } } } } } };
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
            assert.strictEqual(func(object, path), __num_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.strictEqual(func(null, path), undefined);
            assert.strictEqual(func(undefined, path), undefined);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var values = [
                null,
                undefined
            ], expected = lodashStable.map(values, noop), paths = [
                __str_top__,
                [
                    __str_top__,
                    __str_top__,
                    __str_top__
                ]
            ];
        lodashStable.each(paths, function (path) {
            var actual = lodashStable.map(values, function (value) {
                return func(value, path);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = {
            'a': [
                ,
                null
            ]
        };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), undefined);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': null } };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.strictEqual(func(object, path), null);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var paths = [
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ];
        lodashStable.each(paths, function (path) {
            numberProto.a = { 'b': __num_top__ };
            assert.strictEqual(func(__num_top__, path), __num_top__);
            delete numberProto.a;
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = { 'a': {} }, values = empties.concat(__bool_top__, new Date(), __num_top__, /x/, __str_top__), expected = lodashStable.map(values, function (value) {
                return [
                    value,
                    value
                ];
            });
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
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
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func({}, [], __str_top__), __str_top__);
    });
});