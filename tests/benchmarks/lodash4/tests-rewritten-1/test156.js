QUnit.module('lodash.merge');
(function () {
    QUnit.test('should merge `source` into `object`', function (assert) {
        assert.expect(1);
        var names = {
            'characters': [
                { 'name': 'barney' },
                { 'name': 'fred' }
            ]
        };
        var ages = {
            'characters': [
                { 'age': 36 },
                { 'age': 40 }
            ]
        };
        var heights = {
            'characters': [
                { 'height': '5\'4"' },
                { 'height': '5\'5"' }
            ]
        };
        var expected = {
            'characters': [
                {
                    'name': 'barney',
                    'age': 36,
                    'height': '5\'4"'
                },
                {
                    'name': 'fred',
                    'age': 40,
                    'height': '5\'5"'
                }
            ]
        };
        assert.deepEqual(_.merge(names, ages, heights), expected);
    });
    QUnit.test('should merge sources containing circular references', function (assert) {
        assert.expect(2);
        var object = {
            'foo': { 'a': 1 },
            'bar': { 'a': 2 }
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
        var expected = { 'a': 4 }, actual = _.merge({ 'a': 1 }, { 'a': 2 }, { 'a': 3 }, expected);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should merge onto function `object` values', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        var source = { 'a': 1 }, actual = _.merge(Foo, source);
        assert.strictEqual(actual, Foo);
        assert.strictEqual(Foo.a, 1);
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
            }, source2 = { 'a': { 'b': 2 } }, expected = { 'a': { 'b': 2 } }, actual = _.merge({}, source1, source2);
        assert.deepEqual(actual, expected);
        assert.notOk('b' in source1.a);
        actual = _.merge(source1, source2);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should merge onto non-plain `object` values', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        var object = new Foo(), actual = _.merge(object, { 'a': 1 });
        assert.strictEqual(actual, object);
        assert.strictEqual(object.a, 1);
    });
    QUnit.test('should treat sparse array sources as dense', function (assert) {
        assert.expect(2);
        var array = [1];
        array[2] = 3;
        var actual = _.merge([], array), expected = array.slice();
        expected[1] = undefined;
        assert.ok('1' in actual);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should merge `arguments` objects', function (assert) {
        assert.expect(7);
        var object1 = { 'value': args }, object2 = { 'value': { '3': 4 } }, expected = {
                '0': 1,
                '1': 2,
                '2': 3,
                '3': 4
            }, actual = _.merge(object1, object2);
        assert.notOk('3' in args);
        assert.notOk(_.isArguments(actual.value));
        assert.deepEqual(actual.value, expected);
        object1.value = args;
        actual = _.merge(object2, object1);
        assert.notOk(_.isArguments(actual.value));
        assert.deepEqual(actual.value, expected);
        expected = {
            '0': 1,
            '1': 2,
            '2': 3
        };
        actual = _.merge({}, object1);
        assert.notOk(_.isArguments(actual.value));
        assert.deepEqual(actual.value, expected);
    });
    QUnit.test('should merge typed arrays', function (assert) {
        assert.expect(4);
        var array1 = [0], array2 = [
                0,
                0
            ], array3 = [
                __num_top__,
                0,
                0,
                0
            ], array4 = [
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
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
            ], buffer = ArrayBuffer && new ArrayBuffer(8);
        var expected = lodashStable.map(typedArrays, function (type, index) {
            var array = arrays[index].slice();
            array[0] = 1;
            return root[type] ? { 'value': array } : false;
        });
        var actual = lodashStable.map(typedArrays, function (type) {
            var Ctor = root[type];
            return Ctor ? _.merge({ 'value': new Ctor(buffer) }, { 'value': [1] }) : false;
        });
        assert.ok(lodashStable.isArray(actual));
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(typedArrays, function (type, index) {
            var array = arrays[index].slice();
            array.push(1);
            return root[type] ? { 'value': array } : false;
        });
        actual = lodashStable.map(typedArrays, function (type, index) {
            var Ctor = root[type], array = lodashStable.range(arrays[index].length);
            array.push(1);
            return Ctor ? _.merge({ 'value': array }, { 'value': new Ctor(buffer) }) : false;
        });
        assert.ok(lodashStable.isArray(actual));
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should assign `null` values', function (assert) {
        assert.expect(1);
        var actual = _.merge({ 'a': 1 }, { 'a': null });
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
            var buffer = new Buffer([1]), actual = _.merge({}, { 'value': buffer }).value;
            assert.ok(lodashStable.isBuffer(actual));
            assert.strictEqual(actual[0], buffer[0]);
            assert.notStrictEqual(actual, buffer);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should deep clone array/typed-array/plain-object source values', function (assert) {
        assert.expect(1);
        var typedArray = Uint8Array ? new Uint8Array([1]) : { 'buffer': [1] };
        var props = [
                '0',
                'buffer',
                'a'
            ], values = [
                [{ 'a': 1 }],
                typedArray,
                { 'a': [1] }
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value, index) {
            var key = props[index], object = _.merge({}, { 'value': value }), subValue = value[key], newValue = object.value, newSubValue = newValue[key];
            return newValue !== value && newSubValue !== subValue && lodashStable.isEqual(newValue, value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not augment source objects', function (assert) {
        assert.expect(6);
        var source1 = { 'a': [{ 'a': 1 }] }, source2 = { 'a': [{ 'b': 2 }] }, actual = _.merge({}, source1, source2);
        assert.deepEqual(source1.a, [{ 'a': 1 }]);
        assert.deepEqual(source2.a, [{ 'b': 2 }]);
        assert.deepEqual(actual.a, [{
                'a': 1,
                'b': 2
            }]);
        var source1 = {
                'a': [[
                        1,
                        2,
                        3
                    ]]
            }, source2 = {
                'a': [[
                        3,
                        4
                    ]]
            }, actual = _.merge({}, source1, source2);
        assert.deepEqual(source1.a, [[
                1,
                2,
                3
            ]]);
        assert.deepEqual(source2.a, [[
                3,
                4
            ]]);
        assert.deepEqual(actual.a, [[
                3,
                4,
                3
            ]]);
    });
    QUnit.test('should merge plain objects onto non-plain objects', function (assert) {
        assert.expect(4);
        function Foo(object) {
            lodashStable.assign(this, object);
        }
        var object = { 'a': 1 }, actual = _.merge(new Foo(), object);
        assert.ok(actual instanceof Foo);
        assert.deepEqual(actual, new Foo(object));
        actual = _.merge([new Foo()], [object]);
        assert.ok(actual[0] instanceof Foo);
        assert.deepEqual(actual, [new Foo(object)]);
    });
    QUnit.test('should not overwrite existing values with `undefined` values of object sources', function (assert) {
        assert.expect(1);
        var actual = _.merge({ 'a': 1 }, {
            'a': undefined,
            'b': undefined
        });
        assert.deepEqual(actual, {
            'a': 1,
            'b': undefined
        });
    });
    QUnit.test('should not overwrite existing values with `undefined` values of array sources', function (assert) {
        assert.expect(2);
        var array = [1];
        array[2] = 3;
        var actual = _.merge([
                4,
                5,
                6
            ], array), expected = [
                1,
                5,
                3
            ];
        assert.deepEqual(actual, expected);
        array = [
            1,
            ,
            3
        ];
        array[1] = undefined;
        actual = _.merge([
            4,
            5,
            6
        ], array);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should skip merging when `object` and `source` are the same value', function (assert) {
        assert.expect(1);
        var object = {}, pass = true;
        defineProperty(object, 'a', {
            'configurable': true,
            'enumerable': true,
            'get': function () {
                pass = false;
            },
            'set': function () {
                pass = false;
            }
        });
        _.merge(object, object);
        assert.ok(pass);
    });
    QUnit.test('should convert values to arrays when merging arrays of `source`', function (assert) {
        assert.expect(2);
        var object = {
                'a': {
                    '1': 'y',
                    'b': 'z',
                    'length': 2
                }
            }, actual = _.merge(object, { 'a': ['x'] });
        assert.deepEqual(actual, {
            'a': [
                'x',
                'y'
            ]
        });
        actual = _.merge({ 'a': {} }, { 'a': [] });
        assert.deepEqual(actual, { 'a': [] });
    });
    QUnit.test('should not convert strings to arrays when merging arrays of `source`', function (assert) {
        assert.expect(1);
        var object = { 'a': 'abcde' }, actual = _.merge(object, {
                'a': [
                    'x',
                    'y',
                    'z'
                ]
            });
        assert.deepEqual(actual, {
            'a': [
                'x',
                'y',
                'z'
            ]
        });
    });
    QUnit.test('should not error on DOM elements', function (assert) {
        assert.expect(1);
        var object1 = { 'el': document && document.createElement('div') }, object2 = { 'el': document && document.createElement('div') }, pairs = [
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
                return _.merge(pair[0], pair[1]).el === pair[1].el;
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
}());