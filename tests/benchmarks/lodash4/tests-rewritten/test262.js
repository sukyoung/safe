QUnit.module('lodash.transform');
(function () {
    function Foo() {
        this.a = __num_top__;
        this.b = __num_top__;
        this.c = __num_top__;
    }
    QUnit.test('should create an object with the same `[[Prototype]]` as `object` when `accumulator` is nullish', function (assert) {
        assert.expect(4);
        var accumulators = [
                ,
                null,
                undefined
            ], object = new Foo(), expected = lodashStable.map(accumulators, stubTrue);
        var iteratee = function (result, value, key) {
            result[key] = square(value);
        };
        var mapper = function (accumulator, index) {
            return index ? _.transform(object, iteratee, accumulator) : _.transform(object, iteratee);
        };
        var results = lodashStable.map(accumulators, mapper);
        var actual = lodashStable.map(results, function (result) {
            return result instanceof Foo;
        });
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(accumulators, lodashStable.constant({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }));
        actual = lodashStable.map(results, lodashStable.toPlainObject);
        assert.deepEqual(actual, expected);
        object = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        };
        actual = lodashStable.map(accumulators, mapper);
        assert.deepEqual(actual, expected);
        object = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        expected = lodashStable.map(accumulators, lodashStable.constant([
            __num_top__,
            __num_top__,
            __num_top__
        ]));
        actual = lodashStable.map(accumulators, mapper);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should create regular arrays from typed arrays', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(typedArrays, stubTrue);
        var actual = lodashStable.map(typedArrays, function (type) {
            var Ctor = root[type], array = Ctor ? new Ctor(new ArrayBuffer(__num_top__)) : [];
            return lodashStable.isArray(_.transform(array, noop));
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should support an `accumulator` value', function (assert) {
        assert.expect(6);
        var values = [
                new Foo(),
                [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ],
                {
                    'a': __num_top__,
                    'b': __num_top__,
                    'c': __num_top__
                }
            ], expected = lodashStable.map(values, lodashStable.constant([
                __num_top__,
                __num_top__,
                __num_top__
            ]));
        var actual = lodashStable.map(values, function (value) {
            return _.transform(value, function (result, value) {
                result.push(square(value));
            }, []);
        });
        assert.deepEqual(actual, expected);
        var object = {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__
            }, expected = [
                object,
                {
                    '0': __num_top__,
                    '1': __num_top__,
                    '2': __num_top__
                },
                object
            ];
        actual = lodashStable.map(values, function (value) {
            return _.transform(value, function (result, value, key) {
                result[key] = square(value);
            }, {});
        });
        assert.deepEqual(actual, expected);
        lodashStable.each([
            [],
            {}
        ], function (accumulator) {
            var actual = lodashStable.map(values, function (value) {
                return _.transform(value, noop, accumulator);
            });
            assert.ok(lodashStable.every(actual, function (result) {
                return result === accumulator;
            }));
            assert.strictEqual(_.transform(null, null, accumulator), accumulator);
        });
    });
    QUnit.test('should treat sparse arrays as dense', function (assert) {
        assert.expect(1);
        var actual = _.transform(Array(__num_top__), function (result, value, index) {
            result[index] = String(value);
        });
        assert.deepEqual(actual, [__str_top__]);
    });
    QUnit.test('should work without an `iteratee`', function (assert) {
        assert.expect(1);
        assert.ok(_.transform(new Foo()) instanceof Foo);
    });
    QUnit.test('should ensure `object` is an object before using its `[[Prototype]]`', function (assert) {
        assert.expect(2);
        var Ctors = [
                Boolean,
                Boolean,
                Number,
                Number,
                Number,
                String,
                String
            ], values = [
                __bool_top__,
                __bool_top__,
                __num_top__,
                __num_top__,
                NaN,
                __str_top__,
                __str_top__
            ], expected = lodashStable.map(values, stubObject);
        var results = lodashStable.map(values, function (value) {
            return _.transform(value);
        });
        assert.deepEqual(results, expected);
        expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(results, function (value, index) {
            return value instanceof Ctors[index];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should ensure `object` constructor is a function before using its `[[Prototype]]`', function (assert) {
        assert.expect(1);
        Foo.prototype.constructor = null;
        assert.notOk(_.transform(new Foo()) instanceof Foo);
        Foo.prototype.constructor = Foo;
    });
    QUnit.test('should create an empty object when given a falsey `object`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubObject);
        var actual = lodashStable.map(falsey, function (object, index) {
            return index ? _.transform(object) : _.transform();
        });
        assert.deepEqual(actual, expected);
    });
    lodashStable.each({
        'array': [
            __num_top__,
            __num_top__,
            __num_top__
        ],
        'object': {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }
    }, function (object, key) {
        QUnit.test(__str_top__ + key, function (assert) {
            assert.expect(2);
            var args;
            _.transform(object, function () {
                args || (args = slice.call(arguments));
            });
            var first = args[__num_top__];
            if (key == __str_top__) {
                assert.ok(first !== object && lodashStable.isArray(first));
                assert.deepEqual(args, [
                    first,
                    __num_top__,
                    __num_top__,
                    object
                ]);
            } else {
                assert.ok(first !== object && lodashStable.isPlainObject(first));
                assert.deepEqual(args, [
                    first,
                    __num_top__,
                    __str_top__,
                    object
                ]);
            }
        });
    });
    QUnit.test('should create an object from the same realm as `object`', function (assert) {
        assert.expect(1);
        var objects = lodashStable.filter(realm, function (value) {
            return lodashStable.isObject(value) && !lodashStable.isElement(value);
        });
        var expected = lodashStable.map(objects, stubTrue);
        var actual = lodashStable.map(objects, function (object) {
            var Ctor = object.constructor, result = _.transform(object);
            if (result === object) {
                return __bool_top__;
            }
            if (lodashStable.isTypedArray(object)) {
                return result instanceof Array;
            }
            return result instanceof Ctor || !(new Ctor() instanceof Ctor);
        });
        assert.deepEqual(actual, expected);
    });
}());