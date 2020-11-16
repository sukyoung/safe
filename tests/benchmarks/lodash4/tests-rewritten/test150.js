QUnit.module('lodash.matchesProperty');
(function () {
    QUnit.test('should create a function that performs a deep comparison between a property value and `srcValue`', function (assert) {
        assert.expect(6);
        var object = {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__
            }, matches = _.matchesProperty(__str_top__, __num_top__);
        assert.strictEqual(matches.length, __num_top__);
        assert.strictEqual(matches(object), __bool_top__);
        matches = _.matchesProperty(__str_top__, __num_top__);
        assert.strictEqual(matches(object), __bool_top__);
        matches = _.matchesProperty(__str_top__, {
            'a': __num_top__,
            'c': __num_top__
        });
        assert.strictEqual(matches({ 'a': object }), __bool_top__);
        matches = _.matchesProperty(__str_top__, {
            'c': __num_top__,
            'd': __num_top__
        });
        assert.strictEqual(matches(object), __bool_top__);
        object = {
            'a': {
                'b': {
                    'c': __num_top__,
                    'd': __num_top__
                },
                'e': __num_top__
            },
            'f': __num_top__
        };
        matches = _.matchesProperty(__str_top__, { 'b': { 'c': __num_top__ } });
        assert.strictEqual(matches(object), __bool_top__);
    });
    QUnit.test('should support deep paths', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': __num_top__ } };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, __num_top__);
            assert.strictEqual(matches(object), __bool_top__);
        });
    });
    QUnit.test('should work with a non-string `path`', function (assert) {
        assert.expect(2);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        lodashStable.each([
            __num_top__,
            [__num_top__]
        ], function (path) {
            var matches = _.matchesProperty(path, __num_top__);
            assert.strictEqual(matches(array), __bool_top__);
        });
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var object1 = { '-0': __str_top__ }, object2 = { '0': __str_top__ }, pairs = [
                [
                    object1,
                    object2
                ],
                [
                    object1,
                    object2
                ],
                [
                    object2,
                    object1
                ],
                [
                    object2,
                    object1
                ]
            ], props = [
                -__num_top__,
                Object(-__num_top__),
                __num_top__,
                Object(__num_top__)
            ], values = [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ], expected = lodashStable.map(props, lodashStable.constant([
                __bool_top__,
                __bool_top__
            ]));
        var actual = lodashStable.map(props, function (key, index) {
            var matches = _.matchesProperty(key, values[index]), pair = pairs[index];
            return [
                matches(pair[__num_top__]),
                matches(pair[__num_top__])
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should coerce `path` to a string', function (assert) {
        assert.expect(2);
        function fn() {
        }
        fn.toString = lodashStable.constant(__str_top__);
        var object = {
                'null': __num_top__,
                'undefined': __num_top__,
                'fn': __num_top__,
                '[object Object]': __num_top__
            }, paths = [
                null,
                undefined,
                fn,
                {}
            ], expected = lodashStable.map(paths, stubTrue);
        lodashStable.times(__num_top__, function (index) {
            var actual = lodashStable.map(paths, function (path) {
                var matches = _.matchesProperty(index ? [path] : path, object[path]);
                return matches(object);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should match a key over a path', function (assert) {
        assert.expect(2);
        var object = {
            'a.b': __num_top__,
            'a': { 'b': __num_top__ }
        };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var matches = _.matchesProperty(path, __num_top__);
            assert.strictEqual(matches(object), __bool_top__);
        });
    });
    QUnit.test('should return `false` when `object` is nullish', function (assert) {
        assert.expect(2);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var matches = _.matchesProperty(path, __num_top__);
            var actual = lodashStable.map(values, function (value, index) {
                try {
                    return index ? matches(value) : matches();
                } catch (e) {
                }
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should return `false` for deep paths when `object` is nullish', function (assert) {
        assert.expect(2);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, __num_top__);
            var actual = lodashStable.map(values, function (value, index) {
                try {
                    return index ? matches(value) : matches();
                } catch (e) {
                }
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('should return `false` if parts of `path` are missing', function (assert) {
        assert.expect(4);
        var object = {};
        lodashStable.each([
            __str_top__,
            __str_top__,
            [__str_top__],
            [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, __num_top__);
            assert.strictEqual(matches(object), __bool_top__);
        });
    });
    QUnit.test('should match inherited string keyed `srcValue` properties', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype.b = __num_top__;
        var object = { 'a': new Foo() };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            var matches = _.matchesProperty(path, { 'b': __num_top__ });
            assert.strictEqual(matches(object), __bool_top__);
        });
    });
    QUnit.test('should not match by inherited `srcValue` properties', function (assert) {
        assert.expect(2);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var objects = [
                { 'a': { 'a': __num_top__ } },
                {
                    'a': {
                        'a': __num_top__,
                        'b': __num_top__
                    }
                }
            ], expected = lodashStable.map(objects, stubTrue);
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.deepEqual(lodashStable.map(objects, _.matchesProperty(path, new Foo())), expected);
        });
    });
    QUnit.test('should compare a variety of values', function (assert) {
        assert.expect(2);
        var object1 = {
                'a': __bool_top__,
                'b': __bool_top__,
                'c': __str_top__,
                'd': __num_top__,
                'e': [__num_top__],
                'f': { 'g': __num_top__ }
            }, object2 = {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__,
                'd': __str_top__,
                'e': [__str_top__],
                'f': { 'g': __str_top__ }
            }, matches = _.matchesProperty(__str_top__, object1);
        assert.strictEqual(matches({ 'a': object1 }), __bool_top__);
        assert.strictEqual(matches({ 'a': object2 }), __bool_top__);
    });
    QUnit.test('should match `-0` as `0`', function (assert) {
        assert.expect(2);
        var matches = _.matchesProperty(__str_top__, -__num_top__);
        assert.strictEqual(matches({ 'a': __num_top__ }), __bool_top__);
        matches = _.matchesProperty(__str_top__, __num_top__);
        assert.strictEqual(matches({ 'a': -__num_top__ }), __bool_top__);
    });
    QUnit.test('should compare functions by reference', function (assert) {
        assert.expect(3);
        var object1 = { 'a': lodashStable.noop }, object2 = { 'a': noop }, object3 = { 'a': {} }, matches = _.matchesProperty(__str_top__, object1);
        assert.strictEqual(matches({ 'a': object1 }), __bool_top__);
        assert.strictEqual(matches({ 'a': object2 }), __bool_top__);
        assert.strictEqual(matches({ 'a': object3 }), __bool_top__);
    });
    QUnit.test('should work with a function for `srcValue`', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.a = __num_top__;
        Foo.b = function () {
        };
        Foo.c = __num_top__;
        var objects = [
                { 'a': { 'a': __num_top__ } },
                {
                    'a': {
                        'a': __num_top__,
                        'b': Foo.b,
                        'c': __num_top__
                    }
                }
            ], actual = lodashStable.map(objects, _.matchesProperty(__str_top__, Foo));
        assert.deepEqual(actual, [
            __bool_top__,
            __bool_top__
        ]);
    });
    QUnit.test('should work with a non-plain `srcValue`', function (assert) {
        assert.expect(1);
        function Foo(object) {
            lodashStable.assign(this, object);
        }
        var object = new Foo({
                'a': new Foo({
                    'b': __num_top__,
                    'c': __num_top__
                })
            }), matches = _.matchesProperty(__str_top__, { 'b': __num_top__ });
        assert.strictEqual(matches(object), __bool_top__);
    });
    QUnit.test('should partial match arrays', function (assert) {
        assert.expect(3);
        var objects = [
                { 'a': [__str_top__] },
                {
                    'a': [
                        __str_top__,
                        __str_top__
                    ]
                }
            ], actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, [__str_top__]));
        assert.deepEqual(actual, [objects[__num_top__]]);
        actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, [
            __str_top__,
            __str_top__
        ]));
        assert.deepEqual(actual, []);
        actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, [
            __str_top__,
            __str_top__
        ]));
        assert.deepEqual(actual, []);
    });
    QUnit.test('should partial match arrays with duplicate values', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'a': [
                        __num_top__,
                        __num_top__
                    ]
                },
                {
                    'a': [
                        __num_top__,
                        __num_top__
                    ]
                }
            ], actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, [
                __num_top__,
                __num_top__
            ]));
        assert.deepEqual(actual, [objects[__num_top__]]);
    });
    QUnit.test('should partial match arrays of objects', function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': [
                    {
                        'a': __num_top__,
                        'b': __num_top__
                    },
                    {
                        'a': __num_top__,
                        'b': __num_top__,
                        'c': __num_top__
                    }
                ]
            },
            {
                'a': [
                    {
                        'a': __num_top__,
                        'b': __num_top__
                    },
                    {
                        'a': __num_top__,
                        'b': __num_top__,
                        'c': __num_top__
                    }
                ]
            }
        ];
        var actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, [
            { 'a': __num_top__ },
            {
                'a': __num_top__,
                'b': __num_top__
            }
        ]));
        assert.deepEqual(actual, [objects[__num_top__]]);
    });
    QUnit.test('should partial match maps', function (assert) {
        assert.expect(3);
        if (Map) {
            var objects = [
                { 'a': new Map() },
                { 'a': new Map() }
            ];
            objects[__num_top__].a.set(__str_top__, __num_top__);
            objects[__num_top__].a.set(__str_top__, __num_top__);
            objects[__num_top__].a.set(__str_top__, __num_top__);
            var map = new Map();
            map.set(__str_top__, __num_top__);
            var actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, map));
            assert.deepEqual(actual, [objects[__num_top__]]);
            map.delete(__str_top__);
            actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, map));
            assert.deepEqual(actual, objects);
            map.set(__str_top__, __num_top__);
            actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, map));
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should partial match sets', function (assert) {
        assert.expect(3);
        if (Set) {
            var objects = [
                { 'a': new Set() },
                { 'a': new Set() }
            ];
            objects[__num_top__].a.add(__num_top__);
            objects[__num_top__].a.add(__num_top__);
            objects[__num_top__].a.add(__num_top__);
            var set = new Set();
            set.add(__num_top__);
            var actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, set));
            assert.deepEqual(actual, [objects[__num_top__]]);
            set.delete(__num_top__);
            actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, set));
            assert.deepEqual(actual, objects);
            set.add(__num_top__);
            actual = lodashStable.filter(objects, _.matchesProperty(__str_top__, set));
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should match `undefined` values', function (assert) {
        assert.expect(2);
        var objects = [
                { 'a': __num_top__ },
                {
                    'a': __num_top__,
                    'b': __num_top__
                },
                {
                    'a': __num_top__,
                    'b': undefined
                }
            ], actual = lodashStable.map(objects, _.matchesProperty(__str_top__, undefined)), expected = [
                __bool_top__,
                __bool_top__,
                __bool_top__
            ];
        assert.deepEqual(actual, expected);
        objects = [
            { 'a': { 'a': __num_top__ } },
            {
                'a': {
                    'a': __num_top__,
                    'b': __num_top__
                }
            },
            {
                'a': {
                    'a': __num_top__,
                    'b': undefined
                }
            }
        ];
        actual = lodashStable.map(objects, _.matchesProperty(__str_top__, { 'b': undefined }));
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should match `undefined` values of nested objects', function (assert) {
        assert.expect(4);
        var object = { 'a': { 'b': undefined } };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, undefined);
            assert.strictEqual(matches(object), __bool_top__);
        });
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, undefined);
            assert.strictEqual(matches(object), __bool_top__);
        });
    });
    QUnit.test('should match `undefined` values on primitives', function (assert) {
        assert.expect(2);
        numberProto.a = __num_top__;
        numberProto.b = undefined;
        try {
            var matches = _.matchesProperty(__str_top__, undefined);
            assert.strictEqual(matches(__num_top__), __bool_top__);
        } catch (e) {
            assert.ok(__bool_top__, e.message);
        }
        numberProto.a = {
            'b': __num_top__,
            'c': undefined
        };
        try {
            matches = _.matchesProperty(__str_top__, { 'c': undefined });
            assert.strictEqual(matches(__num_top__), __bool_top__);
        } catch (e) {
            assert.ok(__bool_top__, e.message);
        }
        delete numberProto.a;
        delete numberProto.b;
    });
    QUnit.test('should return `true` when comparing a `srcValue` of empty arrays and objects', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'a': [__num_top__],
                    'b': { 'c': __num_top__ }
                },
                {
                    'a': [
                        __num_top__,
                        __num_top__
                    ],
                    'b': { 'd': __num_top__ }
                }
            ], matches = _.matchesProperty(__str_top__, {
                'a': [],
                'b': {}
            });
        var actual = lodashStable.filter(objects, function (object) {
            return matches({ 'a': object });
        });
        assert.deepEqual(actual, objects);
    });
    QUnit.test('should not change behavior if `srcValue` is modified', function (assert) {
        assert.expect(9);
        lodashStable.each([
            {
                'a': {
                    'b': __num_top__,
                    'c': __num_top__
                }
            },
            {
                'a': __num_top__,
                'b': __num_top__
            },
            { 'a': __num_top__ }
        ], function (source, index) {
            var object = lodashStable.cloneDeep(source), matches = _.matchesProperty(__str_top__, source);
            assert.strictEqual(matches({ 'a': object }), __bool_top__);
            if (index) {
                source.a = __num_top__;
                source.b = __num_top__;
                source.c = __num_top__;
            } else {
                source.a.b = __num_top__;
                source.a.c = __num_top__;
                source.a.d = __num_top__;
            }
            assert.strictEqual(matches({ 'a': object }), __bool_top__);
            assert.strictEqual(matches({ 'a': source }), __bool_top__);
        });
    });
}());