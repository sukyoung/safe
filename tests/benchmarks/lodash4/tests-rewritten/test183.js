QUnit.module('partial methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isPartial = methodName == __str_top__, ph = func.placeholder;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var par = func(identity, __str_top__);
        assert.strictEqual(par(), __str_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var fn = function (a, b) {
                return [
                    a,
                    b
                ];
            }, par = func(fn, __str_top__), expected = isPartial ? [
                __str_top__,
                __str_top__
            ] : [
                __str_top__,
                __str_top__
            ];
        assert.deepEqual(par(__str_top__), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var fn = function () {
                return arguments.length;
            }, par = func(fn);
        assert.strictEqual(par(), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var par = func(identity);
        assert.strictEqual(par(__str_top__), __str_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        var fn = function () {
                return slice.call(arguments);
            }, par = func(fn, ph, __str_top__, ph);
        assert.deepEqual(par(__str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(par(__str_top__), [
            __str_top__,
            __str_top__,
            undefined
        ]);
        assert.deepEqual(par(), [
            undefined,
            __str_top__,
            undefined
        ]);
        if (isPartial) {
            assert.deepEqual(par(__str_top__, __str_top__, __str_top__), [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ]);
        } else {
            par = func(fn, ph, __str_top__, ph);
            assert.deepEqual(par(__str_top__, __str_top__, __str_top__), [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ]);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var _ph = _.placeholder = {}, fn = function () {
                    return slice.call(arguments);
                }, par = func(fn, _ph, __str_top__, ph), expected = isPartial ? [
                    __str_top__,
                    __str_top__,
                    ph,
                    __str_top__
                ] : [
                    __str_top__,
                    __str_top__,
                    __str_top__,
                    ph
                ];
            assert.deepEqual(par(__str_top__, __str_top__), expected);
            delete _.placeholder;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var fn = function (a, b, c) {
            }, par = func(fn, __str_top__);
        assert.strictEqual(par.length, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        function Foo(value) {
            return value && object;
        }
        var object = {}, par = func(Foo);
        assert.ok(new par() instanceof Foo);
        assert.strictEqual(new par(__bool_top__), object);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        function greet(greeting, name) {
            return greeting + __str_top__ + name;
        }
        var par1 = func(greet, __str_top__), par2 = func(par1, __str_top__), par3 = func(par1, __str_top__);
        assert.strictEqual(par1(__str_top__), isPartial ? __str_top__ : __str_top__);
        assert.strictEqual(par2(), isPartial ? __str_top__ : __str_top__);
        assert.strictEqual(par3(), isPartial ? __str_top__ : __str_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var fn = function (a, b, c) {
                return a + b + c;
            }, curried = _.curry(func(fn, __num_top__), __num_top__);
        assert.strictEqual(curried(__num_top__, __num_top__), __num_top__);
        assert.strictEqual(curried(__num_top__)(__num_top__), __num_top__);
    });
    QUnit.test('should work with placeholders and curried functions', function (assert) {
        assert.expect(1);
        var fn = function () {
                return slice.call(arguments);
            }, curried = _.curry(fn), par = func(curried, ph, __str_top__, ph, __str_top__);
        assert.deepEqual(par(__str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
});