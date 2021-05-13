QUnit.module('pick methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var expected = {
            'a': __num_top__,
            'c': __num_top__
        }, func = _[methodName], isPick = methodName == __str_top__, object = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__,
            'd': __num_top__
        }, resolve = lodashStable.nthArg(__num_top__);
    if (methodName == __str_top__) {
        resolve = function (object, props) {
            props = lodashStable.castArray(props);
            return function (value) {
                return lodashStable.some(props, function (key) {
                    key = lodashStable.isSymbol(key) ? key : lodashStable.toString(key);
                    return object[key] === value;
                });
            };
        };
    }
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        assert.deepEqual(func(object, resolve(object, __str_top__)), { 'a': __num_top__ });
        assert.deepEqual(func(object, resolve(object, [
            __str_top__,
            __str_top__
        ])), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.prototype = object;
        var foo = new Foo();
        assert.deepEqual(func(foo, resolve(foo, [
            __str_top__,
            __str_top__
        ])), expected);
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
            ], expected = [
                { '-0': __str_top__ },
                { '-0': __str_top__ },
                { '0': __str_top__ },
                { '0': __str_top__ }
            ];
        var actual = lodashStable.map(props, function (key) {
            return func(object, resolve(object, key));
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        function Foo() {
            this[symbol] = __num_top__;
        }
        if (Symbol) {
            var symbol2 = Symbol(__str_top__);
            Foo.prototype[symbol2] = __num_top__;
            var symbol3 = Symbol(__str_top__);
            defineProperty(Foo.prototype, symbol3, {
                'configurable': __bool_top__,
                'enumerable': __bool_top__,
                'writable': __bool_top__,
                'value': __num_top__
            });
            var foo = new Foo(), actual = func(foo, resolve(foo, [
                    symbol,
                    symbol2,
                    symbol3
                ]));
            assert.strictEqual(actual[symbol], __num_top__);
            assert.strictEqual(actual[symbol2], __num_top__);
            if (isPick) {
                assert.strictEqual(actual[symbol3], __num_top__);
            } else {
                assert.notOk(symbol3 in actual);
            }
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(func(array, resolve(array, __str_top__)), { '1': __num_top__ });
    });
});