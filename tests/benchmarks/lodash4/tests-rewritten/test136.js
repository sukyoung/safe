QUnit.module('keys methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isKeys = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func({
            'a': __num_top__,
            'b': __num_top__
        }).sort();
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isKeys ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var expected = isKeys ? [__str_top__] : [
                __str_top__,
                __str_top__
            ], actual = func(new Foo()).sort();
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [__num_top__];
        array[__num_top__] = __num_top__;
        var actual = func(array).sort();
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [__num_top__];
        array.a = __num_top__;
        var actual = func(array).sort();
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isKeys ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        arrayProto.a = __num_top__;
        var expected = isKeys ? [__str_top__] : [
                __str_top__,
                __str_top__
            ], actual = func([__num_top__]).sort();
        assert.deepEqual(actual, expected);
        delete arrayProto.a;
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                args,
                strictArgs
            ], expected = lodashStable.map(values, lodashStable.constant([
                __str_top__,
                __str_top__,
                __str_top__
            ]));
        var actual = lodashStable.map(values, function (value) {
            return func(value).sort();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                args,
                strictArgs
            ], expected = lodashStable.map(values, lodashStable.constant([
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ]));
        var actual = lodashStable.map(values, function (value) {
            value.a = __num_top__;
            var result = func(value).sort();
            delete value.a;
            return result;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isKeys ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                args,
                strictArgs
            ], expected = lodashStable.map(values, lodashStable.constant(isKeys ? [
                __str_top__,
                __str_top__,
                __str_top__
            ] : [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ]));
        var actual = lodashStable.map(values, function (value) {
            objectProto.a = __num_top__;
            var result = func(value).sort();
            delete objectProto.a;
            return result;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func(Object(__str_top__)).sort();
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = Object(__str_top__);
        object.a = __num_top__;
        var actual = func(object).sort();
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isKeys ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        stringProto.a = __num_top__;
        var expected = isKeys ? [__str_top__] : [
                __str_top__,
                __str_top__
            ], actual = func(Object(__str_top__)).sort();
        assert.deepEqual(actual, expected);
        delete stringProto.a;
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                '0': __str_top__,
                'length': __num_top__
            }, actual = func(object).sort();
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var expected = lodashStable.map(primitives, function (value) {
            return typeof value == __str_top__ ? [__str_top__] : [];
        });
        var actual = lodashStable.map(primitives, func);
        assert.deepEqual(actual, expected);
        numberProto.a = __num_top__;
        assert.deepEqual(func(__num_top__), isKeys ? [] : [__str_top__]);
        delete numberProto.a;
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        function Foo() {
        }
        Foo.prototype.a = __num_top__;
        var expected = [__str_top__];
        assert.deepEqual(func(Foo.prototype), expected);
        Foo.prototype = {
            'constructor': Foo,
            'a': __num_top__
        };
        assert.deepEqual(func(Foo.prototype), expected);
        var Fake = { 'prototype': {} };
        Fake.prototype.constructor = Fake;
        assert.deepEqual(func(Fake.prototype), [__str_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubArray);
        var actual = lodashStable.map(values, function (value, index) {
            objectProto.a = __num_top__;
            var result = index ? func(value) : func();
            delete objectProto.a;
            return result;
        });
        assert.deepEqual(actual, expected);
    });
});