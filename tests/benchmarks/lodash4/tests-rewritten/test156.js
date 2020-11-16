QUnit.module('lodash.merge');
(function () {
    QUnit.test('should merge `source` into `object`', function (assert) {
        assert.expect(1);
        var names = {
            'characters': [
                { 'name': __str_top__ },
                { 'name': __str_top__ }
            ]
        };
        var ages = {
            'characters': [
                { 'age': __num_top__ },
                { 'age': __num_top__ }
            ]
        };
        var heights = {
            'characters': [
                { 'height': __str_top__ },
                { 'height': __str_top__ }
            ]
        };
        var expected = {
            'characters': [
                {
                    'name': __str_top__,
                    'age': __num_top__,
                    'height': __str_top__
                },
                {
                    'name': __str_top__,
                    'age': __num_top__,
                    'height': __str_top__
                }
            ]
        };
        assert.deepEqual(_.merge(names, ages, heights), expected);
    });
    QUnit.test('should merge sources containing circular references', function (assert) {
        assert.expect(2);
        var object = {
            'foo': { 'a': __num_top__ },
            'bar': { 'a': __num_top__ }
        };
        var source = {
            'foo': { 'b': { 'c': { 'd': {} } } },
            'bar': {}
        };
        source.foo.b.c.d = source;
        source.bar.b = source.foo.b;
        var actual = _.merge(object, source);
        assert.notStrictEqual(actual.bar.b, actual.foo.b);
        assert.strictEqual(actual.foo.b.c.d, actual.foo.b.c.d.foo.b.c.d);
    });
    QUnit.test('should work with four arguments', function (assert) {
        assert.expect(1);
        var expected = { 'a': __num_top__ }, actual = _.merge({ 'a': __num_top__ }, { 'a': __num_top__ }, { 'a': __num_top__ }, expected);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should merge onto function `object` values', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        var source = { 'a': __num_top__ }, actual = _.merge(Foo, source);
        assert.strictEqual(actual, Foo);
        assert.strictEqual(Foo.a, __num_top__);
    });
    QUnit.test('should merge first source object properties to function', function (assert) {
        assert.expect(1);
        var fn = function () {
            }, object = { 'prop': {} }, actual = _.merge({ 'prop': fn }, object);
        assert.deepEqual(actual, object);
    });
    QUnit.test('should merge first and second source object properties to function', function (assert) {
        assert.expect(1);
        var fn = function () {
            }, object = { 'prop': {} }, actual = _.merge({ 'prop': fn }, { 'prop': fn }, object);
        assert.deepEqual(actual, object);
    });
    QUnit.test('should not merge onto function values of sources', function (assert) {
        assert.expect(3);
        var source1 = {
                'a': function () {
                }
            }, source2 = { 'a': { 'b': __num_top__ } }, expected = { 'a': { 'b': __num_top__ } }, actual = _.merge({}, source1, source2);
        assert.deepEqual(actual, expected);
        assert.notOk(__str_top__ in source1.a);
        actual = _.merge(source1, source2);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should merge onto non-plain `object` values', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        var object = new Foo(), actual = _.merge(object, { 'a': __num_top__ });
        assert.strictEqual(actual, object);
        assert.strictEqual(object.a, __num_top__);
    });
    QUnit.test('should treat sparse array sources as dense', function (assert) {
        assert.expect(2);
        var array = [__num_top__];
        array[__num_top__] = __num_top__;
        var actual = _.merge([], array), expected = array.slice();
        expected[__num_top__] = undefined;
        assert.ok(__str_top__ in actual);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should merge `arguments` objects', function (assert) {
        assert.expect(7);
        var object1 = { 'value': args }, object2 = { 'value': { '3': __num_top__ } }, expected = {
                '0': __num_top__,
                '1': __num_top__,
                '2': __num_top__,
                '3': __num_top__
            }, actual = _.merge(object1, object2);
        assert.notOk(__str_top__ in args);
        assert.notOk(_.isArguments(actual.value));
        assert.deepEqual(actual.value, expected);
        object1.value = args;
        actual = _.merge(object2, object1);
        assert.notOk(_.isArguments(actual.value));
        assert.deepEqual(actual.value, expected);
        expected = {
            '0': __num_top__,
            '1': __num_top__,
            '2': __num_top__
        };
        actual = _.merge({}, object1);
        assert.notOk(_.isArguments(actual.value));
        assert.deepEqual(actual.value, expected);
    });
    QUnit.test('should merge typed arrays', function (assert) {
        assert.expect(4);
        var array1 = [__num_top__], array2 = [
                __num_top__,
                __num_top__
            ], array3 = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ], array4 = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ];
        var arrays = [
                array2,
                array1,
                array4,
                array3,
                array2,
                array4,
                array4,
                array3,
                array2
            ], buffer = ArrayBuffer && new ArrayBuffer(__num_top__);
        var expected = lodashStable.map(typedArrays, function (type, index) {
            var array = arrays[index].slice();
            array[__num_top__] = __num_top__;
            return root[type] ? { 'value': array } : __bool_top__;
        });
        var actual = lodashStable.map(typedArrays, function (type) {
            var Ctor = root[type];
            return Ctor ? _.merge({ 'value': new Ctor(buffer) }, { 'value': [__num_top__] }) : __bool_top__;
        });
        assert.ok(lodashStable.isArray(actual));
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(typedArrays, function (type, index) {
            var array = arrays[index].slice();
            array.push(__num_top__);
            return root[type] ? { 'value': array } : __bool_top__;
        });
        actual = lodashStable.map(typedArrays, function (type, index) {
            var Ctor = root[type], array = lodashStable.range(arrays[index].length);
            array.push(__num_top__);
            return Ctor ? _.merge({ 'value': array }, { 'value': new Ctor(buffer) }) : __bool_top__;
        });
        assert.ok(lodashStable.isArray(actual));
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should assign `null` values', function (assert) {
        assert.expect(1);
        var actual = _.merge({ 'a': __num_top__ }, { 'a': null });
        assert.strictEqual(actual.a, null);
    });
    QUnit.test('should assign non array/buffer/typed-array/plain-object source values directly', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        var values = [
                new Foo(),
                new Boolean(),
                new Date(),
                Foo,
                new Number(),
                new String(),
                new RegExp()
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value) {
            var object = _.merge({}, {
                'a': value,
                'b': { 'c': value }
            });
            return object.a === value && object.b.c === value;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should clone buffer source values', function (assert) {
        assert.expect(3);
        if (Buffer) {
            var buffer = new Buffer([__num_top__]), actual = _.merge({}, { 'value': buffer }).value;
            assert.ok(lodashStable.isBuffer(actual));
            assert.strictEqual(actual[__num_top__], buffer[__num_top__]);
            assert.notStrictEqual(actual, buffer);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should deep clone array/typed-array/plain-object source values', function (assert) {
        assert.expect(1);
        var typedArray = Uint8Array ? new Uint8Array([__num_top__]) : { 'buffer': [__num_top__] };
        var props = [
                __str_top__,
                __str_top__,
                __str_top__
            ], values = [
                [{ 'a': __num_top__ }],
                typedArray,
                { 'a': [__num_top__] }
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value, index) {
            var key = props[index], object = _.merge({}, { 'value': value }), subValue = value[key], newValue = object.value, newSubValue = newValue[key];
            return newValue !== value && newSubValue !== subValue && lodashStable.isEqual(newValue, value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not augment source objects', function (assert) {
        assert.expect(6);
        var source1 = { 'a': [{ 'a': __num_top__ }] }, source2 = { 'a': [{ 'b': __num_top__ }] }, actual = _.merge({}, source1, source2);
        assert.deepEqual(source1.a, [{ 'a': __num_top__ }]);
        assert.deepEqual(source2.a, [{ 'b': __num_top__ }]);
        assert.deepEqual(actual.a, [{
                'a': __num_top__,
                'b': __num_top__
            }]);
        var source1 = {
                'a': [[
                        __num_top__,
                        __num_top__,
                        __num_top__
                    ]]
            }, source2 = {
                'a': [[
                        __num_top__,
                        __num_top__
                    ]]
            }, actual = _.merge({}, source1, source2);
        assert.deepEqual(source1.a, [[
                __num_top__,
                __num_top__,
                __num_top__
            ]]);
        assert.deepEqual(source2.a, [[
                __num_top__,
                __num_top__
            ]]);
        assert.deepEqual(actual.a, [[
                __num_top__,
                __num_top__,
                __num_top__
            ]]);
    });
    QUnit.test('should merge plain objects onto non-plain objects', function (assert) {
        assert.expect(4);
        function Foo(object) {
            lodashStable.assign(this, object);
        }
        var object = { 'a': __num_top__ }, actual = _.merge(new Foo(), object);
        assert.ok(actual instanceof Foo);
        assert.deepEqual(actual, new Foo(object));
        actual = _.merge([new Foo()], [object]);
        assert.ok(actual[__num_top__] instanceof Foo);
        assert.deepEqual(actual, [new Foo(object)]);
    });
    QUnit.test('should not overwrite existing values with `undefined` values of object sources', function (assert) {
        assert.expect(1);
        var actual = _.merge({ 'a': __num_top__ }, {
            'a': undefined,
            'b': undefined
        });
        assert.deepEqual(actual, {
            'a': __num_top__,
            'b': undefined
        });
    });
    QUnit.test('should not overwrite existing values with `undefined` values of array sources', function (assert) {
        assert.expect(2);
        var array = [__num_top__];
        array[__num_top__] = __num_top__;
        var actual = _.merge([
                __num_top__,
                __num_top__,
                __num_top__
            ], array), expected = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        assert.deepEqual(actual, expected);
        array = [
            __num_top__,
            ,
            __num_top__
        ];
        array[__num_top__] = undefined;
        actual = _.merge([
            __num_top__,
            __num_top__,
            __num_top__
        ], array);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should skip merging when `object` and `source` are the same value', function (assert) {
        assert.expect(1);
        var object = {}, pass = __bool_top__;
        defineProperty(object, __str_top__, {
            'configurable': __bool_top__,
            'enumerable': __bool_top__,
            'get': function () {
                pass = __bool_top__;
            },
            'set': function () {
                pass = __bool_top__;
            }
        });
        _.merge(object, object);
        assert.ok(pass);
    });
    QUnit.test('should convert values to arrays when merging arrays of `source`', function (assert) {
        assert.expect(2);
        var object = {
                'a': {
                    '1': __str_top__,
                    'b': __str_top__,
                    'length': __num_top__
                }
            }, actual = _.merge(object, { 'a': [__str_top__] });
        assert.deepEqual(actual, {
            'a': [
                __str_top__,
                __str_top__
            ]
        });
        actual = _.merge({ 'a': {} }, { 'a': [] });
        assert.deepEqual(actual, { 'a': [] });
    });
    QUnit.test('should not convert strings to arrays when merging arrays of `source`', function (assert) {
        assert.expect(1);
        var object = { 'a': __str_top__ }, actual = _.merge(object, {
                'a': [
                    __str_top__,
                    __str_top__,
                    __str_top__
                ]
            });
        assert.deepEqual(actual, {
            'a': [
                __str_top__,
                __str_top__,
                __str_top__
            ]
        });
    });
    QUnit.test('should not error on DOM elements', function (assert) {
        assert.expect(1);
        var object1 = { 'el': document && document.createElement(__str_top__) }, object2 = { 'el': document && document.createElement(__str_top__) }, pairs = [
                [
                    {},
                    object1
                ],
                [
                    object1,
                    object2
                ]
            ], expected = lodashStable.map(pairs, stubTrue);
        var actual = lodashStable.map(pairs, function (pair) {
            try {
                return _.merge(pair[__num_top__], pair[__num_top__]).el === pair[__num_top__].el;
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
}());