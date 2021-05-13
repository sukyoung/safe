QUnit.module('lodash.unset');
(function () {
    QUnit.test('should unset property values', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var object = {
                'a': __num_top__,
                'c': __num_top__
            };
            assert.strictEqual(_.unset(object, path), __bool_top__);
            assert.deepEqual(object, { 'c': __num_top__ });
        });
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var props = [
                -__num_top__,
                Object(-__num_top__),
                __num_top__,
                Object(__num_top__)
            ], expected = lodashStable.map(props, lodashStable.constant([
                __bool_top__,
                __bool_top__
            ]));
        var actual = lodashStable.map(props, function (key) {
            var object = {
                '-0': __str_top__,
                '0': __str_top__
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
            object[symbol] = __num_top__;
            assert.strictEqual(_.unset(object, symbol), __bool_top__);
            assert.notOk(symbol in object);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should unset deep property values', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var object = { 'a': { 'b': null } };
            assert.strictEqual(_.unset(object, path), __bool_top__);
            assert.deepEqual(object, { 'a': {} });
        });
    });
    QUnit.test('should handle complex paths', function (assert) {
        assert.expect(4);
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
            var object = { 'a': { '-1.23': { '["b"]': { 'c': { '[\'d\']': { '\ne\n': { 'f': { 'g': __num_top__ } } } } } } } };
            assert.strictEqual(_.unset(object, path), __bool_top__);
            assert.notOk(__str_top__ in object.a[-__num_top__][__str_top__].c[__str_top__][__str_top__].f);
        });
    });
    QUnit.test('should return `true` for nonexistent paths', function (assert) {
        assert.expect(5);
        var object = { 'a': { 'b': { 'c': null } } };
        lodashStable.each([
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ], function (path) {
            assert.strictEqual(_.unset(object, path), __bool_top__);
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
                    __bool_top__,
                    __bool_top__
                ],
                [
                    __bool_top__,
                    __bool_top__
                ]
            ];
        var actual = lodashStable.map(values, function (value) {
            try {
                return [
                    _.unset(value, __str_top__),
                    _.unset(value, [
                        __str_top__,
                        __str_top__
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
        var object = { 'a': __str_top__ }, paths = [
                __str_top__,
                [
                    __str_top__,
                    __str_top__,
                    __str_top__
                ]
            ];
        lodashStable.each(paths, function (path) {
            numberProto.a = __num_top__;
            var actual = _.unset(__num_top__, path);
            assert.strictEqual(actual, __bool_top__);
            assert.notOk(__str_top__ in numberProto);
            delete numberProto.a;
        });
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            stringProto.replace.b = __num_top__;
            var actual = _.unset(object, path);
            assert.strictEqual(actual, __bool_top__);
            assert.notOk(__str_top__ in stringProto.replace);
            delete stringProto.replace.b;
        });
    });
    QUnit.test('should return `false` for non-configurable properties', function (assert) {
        assert.expect(1);
        var object = {};
        if (!isStrict) {
            defineProperty(object, __str_top__, {
                'configurable': __bool_top__,
                'enumerable': __bool_top__,
                'writable': __bool_top__,
                'value': __num_top__
            });
            assert.strictEqual(_.unset(object, __str_top__), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());