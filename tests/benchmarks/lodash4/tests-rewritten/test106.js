QUnit.module('lodash.isEqual');
(function () {
    var symbol1 = Symbol ? Symbol(__str_top__) : __bool_top__, symbol2 = Symbol ? Symbol(__str_top__) : __bool_top__;
    QUnit.test('should compare primitives', function (assert) {
        assert.expect(1);
        var pairs = [
            [
                __num_top__,
                __num_top__,
                __bool_top__
            ],
            [
                __num_top__,
                Object(__num_top__),
                __bool_top__
            ],
            [
                __num_top__,
                __str_top__,
                __bool_top__
            ],
            [
                __num_top__,
                __num_top__,
                __bool_top__
            ],
            [
                -__num_top__,
                -__num_top__,
                __bool_top__
            ],
            [
                __num_top__,
                __num_top__,
                __bool_top__
            ],
            [
                __num_top__,
                Object(__num_top__),
                __bool_top__
            ],
            [
                Object(__num_top__),
                Object(__num_top__),
                __bool_top__
            ],
            [
                -__num_top__,
                __num_top__,
                __bool_top__
            ],
            [
                __num_top__,
                __str_top__,
                __bool_top__
            ],
            [
                __num_top__,
                null,
                __bool_top__
            ],
            [
                NaN,
                NaN,
                __bool_top__
            ],
            [
                NaN,
                Object(NaN),
                __bool_top__
            ],
            [
                Object(NaN),
                Object(NaN),
                __bool_top__
            ],
            [
                NaN,
                __str_top__,
                __bool_top__
            ],
            [
                NaN,
                Infinity,
                __bool_top__
            ],
            [
                __str_top__,
                __str_top__,
                __bool_top__
            ],
            [
                __str_top__,
                Object(__str_top__),
                __bool_top__
            ],
            [
                Object(__str_top__),
                Object(__str_top__),
                __bool_top__
            ],
            [
                __str_top__,
                __str_top__,
                __bool_top__
            ],
            [
                __str_top__,
                [__str_top__],
                __bool_top__
            ],
            [
                __bool_top__,
                __bool_top__,
                __bool_top__
            ],
            [
                __bool_top__,
                Object(__bool_top__),
                __bool_top__
            ],
            [
                Object(__bool_top__),
                Object(__bool_top__),
                __bool_top__
            ],
            [
                __bool_top__,
                __num_top__,
                __bool_top__
            ],
            [
                __bool_top__,
                __str_top__,
                __bool_top__
            ],
            [
                __bool_top__,
                __bool_top__,
                __bool_top__
            ],
            [
                __bool_top__,
                Object(__bool_top__),
                __bool_top__
            ],
            [
                Object(__bool_top__),
                Object(__bool_top__),
                __bool_top__
            ],
            [
                __bool_top__,
                __num_top__,
                __bool_top__
            ],
            [
                __bool_top__,
                __str_top__,
                __bool_top__
            ],
            [
                symbol1,
                symbol1,
                __bool_top__
            ],
            [
                symbol1,
                Object(symbol1),
                __bool_top__
            ],
            [
                Object(symbol1),
                Object(symbol1),
                __bool_top__
            ],
            [
                symbol1,
                symbol2,
                __bool_top__
            ],
            [
                null,
                null,
                __bool_top__
            ],
            [
                null,
                undefined,
                __bool_top__
            ],
            [
                null,
                {},
                __bool_top__
            ],
            [
                null,
                __str_top__,
                __bool_top__
            ],
            [
                undefined,
                undefined,
                __bool_top__
            ],
            [
                undefined,
                null,
                __bool_top__
            ],
            [
                undefined,
                __str_top__,
                __bool_top__
            ]
        ];
        var expected = lodashStable.map(pairs, function (pair) {
            return pair[__num_top__];
        });
        var actual = lodashStable.map(pairs, function (pair) {
            return _.isEqual(pair[__num_top__], pair[__num_top__]);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should compare arrays', function (assert) {
        assert.expect(6);
        var array1 = [
                __bool_top__,
                null,
                __num_top__,
                __str_top__,
                undefined
            ], array2 = [
                __bool_top__,
                null,
                __num_top__,
                __str_top__,
                undefined
            ];
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1 = [
            [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            new Date(__num_top__, __num_top__, __num_top__),
            /x/,
            { 'e': __num_top__ }
        ];
        array2 = [
            [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            new Date(__num_top__, __num_top__, __num_top__),
            /x/,
            { 'e': __num_top__ }
        ];
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1 = [__num_top__];
        array1[__num_top__] = __num_top__;
        array2 = [__num_top__];
        array2[__num_top__] = undefined;
        array2[__num_top__] = __num_top__;
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1 = [
            Object(__num_top__),
            __bool_top__,
            Object(__str_top__),
            /x/,
            new Date(__num_top__, __num_top__, __num_top__),
            [
                __str_top__,
                __str_top__,
                [Object(__str_top__)]
            ],
            { 'a': __num_top__ }
        ];
        array2 = [
            __num_top__,
            Object(__bool_top__),
            __str_top__,
            /x/,
            new Date(__num_top__, __num_top__, __num_top__),
            [
                __str_top__,
                Object(__str_top__),
                [__str_top__]
            ],
            { 'a': __num_top__ }
        ];
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1 = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        array2 = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1 = [
            __num_top__,
            __num_top__
        ];
        array2 = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
    });
    QUnit.test('should treat arrays with identical values but different non-index properties as equal', function (assert) {
        assert.expect(3);
        var array1 = [
                __num_top__,
                __num_top__,
                __num_top__
            ], array2 = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        array1.every = array1.filter = array1.forEach = array1.indexOf = array1.lastIndexOf = array1.map = array1.some = array1.reduce = array1.reduceRight = null;
        array2.concat = array2.join = array2.pop = array2.reverse = array2.shift = array2.slice = array2.sort = array2.splice = array2.unshift = null;
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1 = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        array1.a = __num_top__;
        array2 = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        array2.b = __num_top__;
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1 = /c/.exec(__str_top__);
        array2 = [__str_top__];
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
    });
    QUnit.test('should compare sparse arrays', function (assert) {
        assert.expect(3);
        var array = Array(__num_top__);
        assert.strictEqual(_.isEqual(array, Array(__num_top__)), __bool_top__);
        assert.strictEqual(_.isEqual(array, [undefined]), __bool_top__);
        assert.strictEqual(_.isEqual(array, Array(__num_top__)), __bool_top__);
    });
    QUnit.test('should compare plain objects', function (assert) {
        assert.expect(5);
        var object1 = {
                'a': __bool_top__,
                'b': null,
                'c': __num_top__,
                'd': __str_top__,
                'e': undefined
            }, object2 = {
                'a': __bool_top__,
                'b': null,
                'c': __num_top__,
                'd': __str_top__,
                'e': undefined
            };
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        object1 = {
            'a': [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            'b': new Date(__num_top__, __num_top__, __num_top__),
            'c': /x/,
            'd': { 'e': __num_top__ }
        };
        object2 = {
            'a': [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            'b': new Date(__num_top__, __num_top__, __num_top__),
            'c': /x/,
            'd': { 'e': __num_top__ }
        };
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        object1 = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        };
        object2 = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        };
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        object1 = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        };
        object2 = {
            'd': __num_top__,
            'e': __num_top__,
            'f': __num_top__
        };
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        object1 = {
            'a': __num_top__,
            'b': __num_top__
        };
        object2 = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        };
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
    });
    QUnit.test('should compare objects regardless of key order', function (assert) {
        assert.expect(1);
        var object1 = {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__
            }, object2 = {
                'c': __num_top__,
                'a': __num_top__,
                'b': __num_top__
            };
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
    });
    QUnit.test('should compare nested objects', function (assert) {
        assert.expect(1);
        var object1 = {
            'a': [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            'b': __bool_top__,
            'c': Object(__num_top__),
            'd': __str_top__,
            'e': {
                'f': [
                    __str_top__,
                    Object(__str_top__),
                    __str_top__
                ],
                'g': Object(__bool_top__),
                'h': new Date(__num_top__, __num_top__, __num_top__),
                'i': noop,
                'j': __str_top__
            }
        };
        var object2 = {
            'a': [
                __num_top__,
                Object(__num_top__),
                __num_top__
            ],
            'b': Object(__bool_top__),
            'c': __num_top__,
            'd': Object(__str_top__),
            'e': {
                'f': [
                    __str_top__,
                    __str_top__,
                    __str_top__
                ],
                'g': __bool_top__,
                'h': new Date(__num_top__, __num_top__, __num_top__),
                'i': noop,
                'j': __str_top__
            }
        };
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
    });
    QUnit.test('should compare object instances', function (assert) {
        assert.expect(4);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.a = __num_top__;
        function Bar() {
            this.a = __num_top__;
        }
        Bar.prototype.a = __num_top__;
        assert.strictEqual(_.isEqual(new Foo(), new Foo()), __bool_top__);
        assert.strictEqual(_.isEqual(new Foo(), new Bar()), __bool_top__);
        assert.strictEqual(_.isEqual({ 'a': __num_top__ }, new Foo()), __bool_top__);
        assert.strictEqual(_.isEqual({ 'a': __num_top__ }, new Bar()), __bool_top__);
    });
    QUnit.test('should compare objects with constructor properties', function (assert) {
        assert.expect(5);
        assert.strictEqual(_.isEqual({ 'constructor': __num_top__ }, { 'constructor': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isEqual({ 'constructor': __num_top__ }, { 'constructor': __str_top__ }), __bool_top__);
        assert.strictEqual(_.isEqual({ 'constructor': [__num_top__] }, { 'constructor': [__num_top__] }), __bool_top__);
        assert.strictEqual(_.isEqual({ 'constructor': [__num_top__] }, { 'constructor': [__str_top__] }), __bool_top__);
        assert.strictEqual(_.isEqual({ 'constructor': Object }, {}), __bool_top__);
    });
    QUnit.test('should compare arrays with circular references', function (assert) {
        assert.expect(6);
        var array1 = [], array2 = [];
        array1.push(array1);
        array2.push(array2);
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1.push(__str_top__);
        array2.push(__str_top__);
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1.push(__str_top__);
        array2.push(__str_top__);
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1 = [
            __str_top__,
            __str_top__,
            __str_top__
        ];
        array1[__num_top__] = array1;
        array2 = [
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__
            ],
            __str_top__
        ];
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1 = [[[]]];
        array1[__num_top__][__num_top__][__num_top__] = array1;
        array2 = [];
        array2[__num_top__] = array2;
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        assert.strictEqual(_.isEqual(array2, array1), __bool_top__);
    });
    QUnit.test('should have transitive equivalence for circular references of arrays', function (assert) {
        assert.expect(3);
        var array1 = [], array2 = [array1], array3 = [array2];
        array1[__num_top__] = array1;
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        assert.strictEqual(_.isEqual(array2, array3), __bool_top__);
        assert.strictEqual(_.isEqual(array1, array3), __bool_top__);
    });
    QUnit.test('should compare objects with circular references', function (assert) {
        assert.expect(6);
        var object1 = {}, object2 = {};
        object1.a = object1;
        object2.a = object2;
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        object1.b = __num_top__;
        object2.b = Object(__num_top__);
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        object1.c = Object(__num_top__);
        object2.c = Object(__num_top__);
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        object1 = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        };
        object1.b = object1;
        object2 = {
            'a': __num_top__,
            'b': {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__
            },
            'c': __num_top__
        };
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        object1 = { self: { self: { self: {} } } };
        object1.self.self.self = object1;
        object2 = { self: {} };
        object2.self = object2;
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        assert.strictEqual(_.isEqual(object2, object1), __bool_top__);
    });
    QUnit.test('should have transitive equivalence for circular references of objects', function (assert) {
        assert.expect(3);
        var object1 = {}, object2 = { 'a': object1 }, object3 = { 'a': object2 };
        object1.a = object1;
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        assert.strictEqual(_.isEqual(object2, object3), __bool_top__);
        assert.strictEqual(_.isEqual(object1, object3), __bool_top__);
    });
    QUnit.test('should compare objects with multiple circular references', function (assert) {
        assert.expect(3);
        var array1 = [{}], array2 = [{}];
        (array1[__num_top__].a = array1).push(array1);
        (array2[__num_top__].a = array2).push(array2);
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1[__num_top__].b = __num_top__;
        array2[__num_top__].b = Object(__num_top__);
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
        array1[__num_top__].c = Object(__num_top__);
        array2[__num_top__].c = Object(__num_top__);
        assert.strictEqual(_.isEqual(array1, array2), __bool_top__);
    });
    QUnit.test('should compare objects with complex circular references', function (assert) {
        assert.expect(1);
        var object1 = {
            'foo': { 'b': { 'c': { 'd': {} } } },
            'bar': { 'a': __num_top__ }
        };
        var object2 = {
            'foo': { 'b': { 'c': { 'd': {} } } },
            'bar': { 'a': __num_top__ }
        };
        object1.foo.b.c.d = object1;
        object1.bar.b = object1.foo.b;
        object2.foo.b.c.d = object2;
        object2.bar.b = object2.foo.b;
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
    });
    QUnit.test('should compare objects with shared property values', function (assert) {
        assert.expect(1);
        var object1 = {
            'a': [
                __num_top__,
                __num_top__
            ]
        };
        var object2 = {
            'a': [
                __num_top__,
                __num_top__
            ],
            'b': [
                __num_top__,
                __num_top__
            ]
        };
        object1.b = object1.a;
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
    });
    QUnit.test('should treat objects created by `Object.create(null)` like plain objects', function (assert) {
        assert.expect(2);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.constructor = null;
        var object1 = create(null);
        object1.a = __num_top__;
        var object2 = { 'a': __num_top__ };
        assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        assert.strictEqual(_.isEqual(new Foo(), object2), __bool_top__);
    });
    QUnit.test('should avoid common type coercions', function (assert) {
        assert.expect(9);
        assert.strictEqual(_.isEqual(__bool_top__, Object(__bool_top__)), __bool_top__);
        assert.strictEqual(_.isEqual(Object(__bool_top__), Object(__num_top__)), __bool_top__);
        assert.strictEqual(_.isEqual(__bool_top__, Object(__str_top__)), __bool_top__);
        assert.strictEqual(_.isEqual(Object(__num_top__), Object(__str_top__)), __bool_top__);
        assert.strictEqual(_.isEqual(__num_top__, __str_top__), __bool_top__);
        assert.strictEqual(_.isEqual(__num_top__, __bool_top__), __bool_top__);
        assert.strictEqual(_.isEqual(__num_top__, new Date(__num_top__, __num_top__, __num_top__)), __bool_top__);
        assert.strictEqual(_.isEqual(__str_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.isEqual(__num_top__, __str_top__), __bool_top__);
    });
    QUnit.test('should compare `arguments` objects', function (assert) {
        assert.expect(2);
        var args1 = function () {
                return arguments;
            }(), args2 = function () {
                return arguments;
            }(), args3 = function () {
                return arguments;
            }(__num_top__, __num_top__);
        assert.strictEqual(_.isEqual(args1, args2), __bool_top__);
        assert.strictEqual(_.isEqual(args1, args3), __bool_top__);
    });
    QUnit.test('should treat `arguments` objects like `Object` objects', function (assert) {
        assert.expect(4);
        var object = {
            '0': __num_top__,
            '1': __num_top__,
            '2': __num_top__
        };
        function Foo() {
        }
        Foo.prototype = object;
        assert.strictEqual(_.isEqual(args, object), __bool_top__);
        assert.strictEqual(_.isEqual(object, args), __bool_top__);
        assert.strictEqual(_.isEqual(args, new Foo()), __bool_top__);
        assert.strictEqual(_.isEqual(new Foo(), args), __bool_top__);
    });
    QUnit.test('should compare array buffers', function (assert) {
        assert.expect(2);
        if (ArrayBuffer) {
            var buffer = new Int8Array([-__num_top__]).buffer;
            assert.strictEqual(_.isEqual(buffer, new Uint8Array([__num_top__]).buffer), __bool_top__);
            assert.strictEqual(_.isEqual(buffer, new ArrayBuffer(__num_top__)), __bool_top__);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should compare array views', function (assert) {
        assert.expect(2);
        lodashStable.times(__num_top__, function (index) {
            var ns = index ? realm : root;
            var pairs = lodashStable.map(arrayViews, function (type, viewIndex) {
                var otherType = arrayViews[(viewIndex + __num_top__) % arrayViews.length], CtorA = ns[type] || function (n) {
                        this.n = n;
                    }, CtorB = ns[otherType] || function (n) {
                        this.n = n;
                    }, bufferA = ns[type] ? new ns.ArrayBuffer(__num_top__) : __num_top__, bufferB = ns[otherType] ? new ns.ArrayBuffer(__num_top__) : __num_top__, bufferC = ns[otherType] ? new ns.ArrayBuffer(__num_top__) : __num_top__;
                return [
                    new CtorA(bufferA),
                    new CtorA(bufferA),
                    new CtorB(bufferB),
                    new CtorB(bufferC)
                ];
            });
            var expected = lodashStable.map(pairs, lodashStable.constant([
                __bool_top__,
                __bool_top__,
                __bool_top__
            ]));
            var actual = lodashStable.map(pairs, function (pair) {
                return [
                    _.isEqual(pair[__num_top__], pair[__num_top__]),
                    _.isEqual(pair[__num_top__], pair[__num_top__]),
                    _.isEqual(pair[__num_top__], pair[__num_top__])
                ];
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should compare buffers', function (assert) {
        assert.expect(3);
        if (Buffer) {
            var buffer = new Buffer([__num_top__]);
            assert.strictEqual(_.isEqual(buffer, new Buffer([__num_top__])), __bool_top__);
            assert.strictEqual(_.isEqual(buffer, new Buffer([__num_top__])), __bool_top__);
            assert.strictEqual(_.isEqual(buffer, new Uint8Array([__num_top__])), __bool_top__);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should compare date objects', function (assert) {
        assert.expect(4);
        var date = new Date(__num_top__, __num_top__, __num_top__);
        assert.strictEqual(_.isEqual(date, new Date(__num_top__, __num_top__, __num_top__)), __bool_top__);
        assert.strictEqual(_.isEqual(new Date(__str_top__), new Date(__str_top__)), __bool_top__);
        assert.strictEqual(_.isEqual(date, new Date(__num_top__, __num_top__, __num_top__)), __bool_top__);
        assert.strictEqual(_.isEqual(date, { 'getTime': lodashStable.constant(+date) }), __bool_top__);
    });
    QUnit.test('should compare error objects', function (assert) {
        assert.expect(1);
        var pairs = lodashStable.map([
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ], function (type, index, errorTypes) {
            var otherType = errorTypes[++index % errorTypes.length], CtorA = root[type], CtorB = root[otherType];
            return [
                new CtorA(__str_top__),
                new CtorA(__str_top__),
                new CtorB(__str_top__),
                new CtorB(__str_top__)
            ];
        });
        var expected = lodashStable.map(pairs, lodashStable.constant([
            __bool_top__,
            __bool_top__,
            __bool_top__
        ]));
        var actual = lodashStable.map(pairs, function (pair) {
            return [
                _.isEqual(pair[__num_top__], pair[__num_top__]),
                _.isEqual(pair[__num_top__], pair[__num_top__]),
                _.isEqual(pair[__num_top__], pair[__num_top__])
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should compare functions', function (assert) {
        assert.expect(2);
        function a() {
            return __num_top__ + __num_top__;
        }
        function b() {
            return __num_top__ + __num_top__;
        }
        assert.strictEqual(_.isEqual(a, a), __bool_top__);
        assert.strictEqual(_.isEqual(a, b), __bool_top__);
    });
    QUnit.test('should compare maps', function (assert) {
        assert.expect(8);
        if (Map) {
            lodashStable.each([
                [
                    map,
                    new Map()
                ],
                [
                    map,
                    realm.map
                ]
            ], function (maps) {
                var map1 = maps[__num_top__], map2 = maps[__num_top__];
                map1.set(__str_top__, __num_top__);
                map2.set(__str_top__, __num_top__);
                assert.strictEqual(_.isEqual(map1, map2), __bool_top__);
                map1.set(__str_top__, __num_top__);
                map2.set(__str_top__, __num_top__);
                assert.strictEqual(_.isEqual(map1, map2), __bool_top__);
                map1.delete(__str_top__);
                map1.set(__str_top__, __num_top__);
                assert.strictEqual(_.isEqual(map1, map2), __bool_top__);
                map2.delete(__str_top__);
                assert.strictEqual(_.isEqual(map1, map2), __bool_top__);
                map1.clear();
                map2.clear();
            });
        } else {
            skipAssert(assert, 8);
        }
    });
    QUnit.test('should compare maps with circular references', function (assert) {
        assert.expect(2);
        if (Map) {
            var map1 = new Map(), map2 = new Map();
            map1.set(__str_top__, map1);
            map2.set(__str_top__, map2);
            assert.strictEqual(_.isEqual(map1, map2), __bool_top__);
            map1.set(__str_top__, __num_top__);
            map2.set(__str_top__, __num_top__);
            assert.strictEqual(_.isEqual(map1, map2), __bool_top__);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should compare promises by reference', function (assert) {
        assert.expect(4);
        if (promise) {
            lodashStable.each([
                [
                    promise,
                    Promise.resolve(__num_top__)
                ],
                [
                    promise,
                    realm.promise
                ]
            ], function (promises) {
                var promise1 = promises[__num_top__], promise2 = promises[__num_top__];
                assert.strictEqual(_.isEqual(promise1, promise2), __bool_top__);
                assert.strictEqual(_.isEqual(promise1, promise1), __bool_top__);
            });
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should compare regexes', function (assert) {
        assert.expect(5);
        assert.strictEqual(_.isEqual(/x/gim, /x/gim), __bool_top__);
        assert.strictEqual(_.isEqual(/x/gim, /x/mgi), __bool_top__);
        assert.strictEqual(_.isEqual(/x/gi, /x/g), __bool_top__);
        assert.strictEqual(_.isEqual(/x/, /y/), __bool_top__);
        assert.strictEqual(_.isEqual(/x/g, {
            'global': __bool_top__,
            'ignoreCase': __bool_top__,
            'multiline': __bool_top__,
            'source': __str_top__
        }), __bool_top__);
    });
    QUnit.test('should compare sets', function (assert) {
        assert.expect(8);
        if (Set) {
            lodashStable.each([
                [
                    set,
                    new Set()
                ],
                [
                    set,
                    realm.set
                ]
            ], function (sets) {
                var set1 = sets[__num_top__], set2 = sets[__num_top__];
                set1.add(__num_top__);
                set2.add(__num_top__);
                assert.strictEqual(_.isEqual(set1, set2), __bool_top__);
                set1.add(__num_top__);
                set2.add(__num_top__);
                assert.strictEqual(_.isEqual(set1, set2), __bool_top__);
                set1.delete(__num_top__);
                set1.add(__num_top__);
                assert.strictEqual(_.isEqual(set1, set2), __bool_top__);
                set2.delete(__num_top__);
                assert.strictEqual(_.isEqual(set1, set2), __bool_top__);
                set1.clear();
                set2.clear();
            });
        } else {
            skipAssert(assert, 8);
        }
    });
    QUnit.test('should compare sets with circular references', function (assert) {
        assert.expect(2);
        if (Set) {
            var set1 = new Set(), set2 = new Set();
            set1.add(set1);
            set2.add(set2);
            assert.strictEqual(_.isEqual(set1, set2), __bool_top__);
            set1.add(__num_top__);
            set2.add(__num_top__);
            assert.strictEqual(_.isEqual(set1, set2), __bool_top__);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should compare symbol properties', function (assert) {
        assert.expect(3);
        if (Symbol) {
            var object1 = { 'a': __num_top__ }, object2 = { 'a': __num_top__ };
            object1[symbol1] = { 'a': { 'b': __num_top__ } };
            object2[symbol1] = { 'a': { 'b': __num_top__ } };
            defineProperty(object2, symbol2, {
                'configurable': __bool_top__,
                'enumerable': __bool_top__,
                'writable': __bool_top__,
                'value': __num_top__
            });
            assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
            object2[symbol1] = { 'a': __num_top__ };
            assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
            delete object2[symbol1];
            object2[Symbol(__str_top__)] = { 'a': { 'b': __num_top__ } };
            assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should compare wrapped values', function (assert) {
        assert.expect(32);
        var stamp = +new Date();
        var values = [
            [
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ]
            ],
            [
                __bool_top__,
                __bool_top__,
                __bool_top__
            ],
            [
                new Date(stamp),
                new Date(stamp),
                new Date(stamp - __num_top__)
            ],
            [
                {
                    'a': __num_top__,
                    'b': __num_top__
                },
                {
                    'a': __num_top__,
                    'b': __num_top__
                },
                {
                    'a': __num_top__,
                    'b': __num_top__
                }
            ],
            [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            [
                NaN,
                NaN,
                Infinity
            ],
            [
                /x/,
                /x/,
                /x/i
            ],
            [
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ];
        lodashStable.each(values, function (vals) {
            if (!isNpm) {
                var wrapped1 = _(vals[__num_top__]), wrapped2 = _(vals[__num_top__]), actual = wrapped1.isEqual(wrapped2);
                assert.strictEqual(actual, __bool_top__);
                assert.strictEqual(_.isEqual(_(actual), _(__bool_top__)), __bool_top__);
                wrapped1 = _(vals[__num_top__]);
                wrapped2 = _(vals[__num_top__]);
                actual = wrapped1.isEqual(wrapped2);
                assert.strictEqual(actual, __bool_top__);
                assert.strictEqual(_.isEqual(_(actual), _(__bool_top__)), __bool_top__);
            } else {
                skipAssert(assert, 4);
            }
        });
    });
    QUnit.test('should compare wrapped and non-wrapped values', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var object1 = _({
                    'a': __num_top__,
                    'b': __num_top__
                }), object2 = {
                    'a': __num_top__,
                    'b': __num_top__
                };
            assert.strictEqual(object1.isEqual(object2), __bool_top__);
            assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
            object1 = _({
                'a': __num_top__,
                'b': __num_top__
            });
            object2 = {
                'a': __num_top__,
                'b': __num_top__
            };
            assert.strictEqual(object1.isEqual(object2), __bool_top__);
            assert.strictEqual(_.isEqual(object1, object2), __bool_top__);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should work as an iteratee for `_.every`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.every([
            __num_top__,
            __num_top__,
            __num_top__
        ], lodashStable.partial(_.isEqual, __num_top__));
        assert.ok(actual);
    });
    QUnit.test('should not error on DOM elements', function (assert) {
        assert.expect(1);
        if (document) {
            var element1 = document.createElement(__str_top__), element2 = element1.cloneNode(__bool_top__);
            try {
                assert.strictEqual(_.isEqual(element1, element2), __bool_top__);
            } catch (e) {
                assert.ok(__bool_top__, e.message);
            }
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `true` for like-objects from different documents', function (assert) {
        assert.expect(4);
        if (realm.object) {
            assert.strictEqual(_.isEqual([__num_top__], realm.array), __bool_top__);
            assert.strictEqual(_.isEqual([__num_top__], realm.array), __bool_top__);
            assert.strictEqual(_.isEqual({ 'a': __num_top__ }, realm.object), __bool_top__);
            assert.strictEqual(_.isEqual({ 'a': __num_top__ }, realm.object), __bool_top__);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should return `false` for objects with custom `toString` methods', function (assert) {
        assert.expect(1);
        var primitive, object = {
                'toString': function () {
                    return primitive;
                }
            }, values = [
                __bool_top__,
                null,
                __num_top__,
                __str_top__,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value) {
            primitive = value;
            return _.isEqual(object, value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.strictEqual(_(__str_top__).isEqual(__str_top__), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(__str_top__).chain().isEqual(__str_top__) instanceof _);
        } else {
            skipAssert(assert);
        }
    });
}());