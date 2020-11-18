QUnit.module('keys methods');
lodashStable.each([
    'keys',
    'keysIn'
], function (methodName) {
    var func = _[methodName], isKeys = methodName == 'keys';
    QUnit.test('`_.' + methodName + '` should return the string keyed property names of `object`', function (assert) {
        assert.expect(1);
        var actual = func({
            'a': 1,
            'b': 1
        }).sort();
        assert.deepEqual(actual, [
            'a',
            'b'
        ]);
    });
    QUnit.test('`_.' + methodName + '` should ' + (isKeys ? 'not ' : '') + 'include inherited string keyed properties', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var expected = isKeys ? ['a'] : [
                'a',
                'b'
            ], actual = func(new Foo()).sort();
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should treat sparse arrays as dense', function (assert) {
        assert.expect(1);
        var array = [1];
        array[2] = 3;
        var actual = func(array).sort();
        assert.deepEqual(actual, [
            '0',
            '1',
            '2'
        ]);
    });
    QUnit.test('`_.' + methodName + '` should return keys for custom properties on arrays', function (assert) {
        assert.expect(1);
        var array = [1];
        array.a = 1;
        var actual = func(array).sort();
        assert.deepEqual(actual, [
            '0',
            'a'
        ]);
    });
    QUnit.test('`_.' + methodName + '` should ' + (isKeys ? 'not ' : '') + 'include inherited string keyed properties of arrays', function (assert) {
        assert.expect(1);
        arrayProto.a = 1;
        var expected = isKeys ? ['0'] : [
                '0',
                'a'
            ], actual = func([1]).sort();
        assert.deepEqual(actual, expected);
        delete arrayProto.a;
    });
    QUnit.test('`_.' + methodName + '` should work with `arguments` objects', function (assert) {
        assert.expect(1);
        var values = [
                args,
                strictArgs
            ], expected = lodashStable.map(values, lodashStable.constant([
                '0',
                '1',
                '2'
            ]));
        var actual = lodashStable.map(values, function (value) {
            return func(value).sort();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should return keys for custom properties on `arguments` objects', function (assert) {
        assert.expect(1);
        var values = [
                args,
                strictArgs
            ], expected = lodashStable.map(values, lodashStable.constant([
                '0',
                '1',
                '2',
                'a'
            ]));
        var actual = lodashStable.map(values, function (value) {
            value.a = 1;
            var result = func(value).sort();
            delete value.a;
            return result;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should ' + (isKeys ? 'not ' : '') + 'include inherited string keyed properties of `arguments` objects', function (assert) {
        assert.expect(1);
        var values = [
                args,
                strictArgs
            ], expected = lodashStable.map(values, lodashStable.constant(isKeys ? [
                '0',
                '1',
                '2'
            ] : [
                '0',
                '1',
                '2',
                'a'
            ]));
        var actual = lodashStable.map(values, function (value) {
            objectProto.a = 1;
            var result = func(value).sort();
            delete objectProto.a;
            return result;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should work with string objects', function (assert) {
        assert.expect(1);
        var actual = func(Object('abc')).sort();
        assert.deepEqual(actual, [
            '0',
            '1',
            '2'
        ]);
    });
    QUnit.test('`_.' + methodName + '` should return keys for custom properties on string objects', function (assert) {
        assert.expect(1);
        var object = Object('a');
        object.a = 1;
        var actual = func(object).sort();
        assert.deepEqual(actual, [
            '0',
            'a'
        ]);
    });
    QUnit.test('`_.' + methodName + '` should ' + (isKeys ? 'not ' : '') + 'include inherited string keyed properties of string objects', function (assert) {
        assert.expect(1);
        stringProto.a = 1;
        var expected = isKeys ? ['0'] : [
                '0',
                'a'
            ], actual = func(Object('a')).sort();
        assert.deepEqual(actual, expected);
        delete stringProto.a;
    });
    QUnit.test('`_.' + methodName + '` should work with array-like objects', function (assert) {
        assert.expect(1);
        var object = {
                '0': 'a',
                'length': 1
            }, actual = func(object).sort();
        assert.deepEqual(actual, [
            '0',
            'length'
        ]);
    });
    QUnit.test('`_.' + methodName + '` should coerce primitives to objects (test in IE 9)', function (assert) {
        assert.expect(2);
        var expected = lodashStable.map(primitives, function (value) {
            return typeof value == __str_top__ ? ['0'] : [];
        });
        var actual = lodashStable.map(primitives, func);
        assert.deepEqual(actual, expected);
        numberProto.a = 1;
        assert.deepEqual(func(0), isKeys ? [] : ['a']);
        delete numberProto.a;
    });
    QUnit.test('`_.' + methodName + '` skips the `constructor` property on prototype objects', function (assert) {
        assert.expect(3);
        function Foo() {
        }
        Foo.prototype.a = 1;
        var expected = ['a'];
        assert.deepEqual(func(Foo.prototype), expected);
        Foo.prototype = {
            'constructor': Foo,
            'a': 1
        };
        assert.deepEqual(func(Foo.prototype), expected);
        var Fake = { 'prototype': {} };
        Fake.prototype.constructor = Fake;
        assert.deepEqual(func(Fake.prototype), ['constructor']);
    });
    QUnit.test('`_.' + methodName + '` should return an empty array when `object` is nullish', function (assert) {
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubArray);
        var actual = lodashStable.map(values, function (value, index) {
            objectProto.a = 1;
            var result = index ? func(value) : func();
            delete objectProto.a;
            return result;
        });
        assert.deepEqual(actual, expected);
    });
});