QUnit.module('lodash.matchesProperty');
(function () {
    QUnit.test('should create a function that performs a deep comparison between a property value and `srcValue`', function (assert) {
        assert.expect(6);
        var object = {
                'a': 1,
                'b': 2,
                'c': 3
            }, matches = _.matchesProperty('a', 1);
        assert.strictEqual(matches.length, 1);
        assert.strictEqual(matches(object), true);
        matches = _.matchesProperty('b', 3);
        assert.strictEqual(matches(object), false);
        matches = _.matchesProperty('a', {
            'a': 1,
            'c': 3
        });
        assert.strictEqual(matches({ 'a': object }), true);
        matches = _.matchesProperty('a', {
            'c': 3,
            'd': 4
        });
        assert.strictEqual(matches(object), false);
        object = {
            'a': {
                'b': {
                    'c': 1,
                    'd': 2
                },
                'e': 3
            },
            'f': 4
        };
        matches = _.matchesProperty('a', { 'b': { 'c': 1 } });
        assert.strictEqual(matches(object), true);
    });
    QUnit.test('should support deep paths', function (assert) {
        assert.expect(2);
        var object = { 'a': { 'b': 2 } };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, 2);
            assert.strictEqual(matches(object), true);
        });
    });
    QUnit.test('should work with a non-string `path`', function (assert) {
        assert.expect(2);
        var array = [
            1,
            2,
            3
        ];
        lodashStable.each([
            1,
            [1]
        ], function (path) {
            var matches = _.matchesProperty(path, 2);
            assert.strictEqual(matches(array), true);
        });
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var object1 = { '-0': 'a' }, object2 = { '0': 'b' }, pairs = [
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
                -0,
                Object(-0),
                0,
                Object(0)
            ], values = [
                'a',
                'a',
                'b',
                'b'
            ], expected = lodashStable.map(props, lodashStable.constant([
                true,
                false
            ]));
        var actual = lodashStable.map(props, function (key, index) {
            var matches = _.matchesProperty(key, values[index]), pair = pairs[index];
            return [
                matches(pair[0]),
                matches(pair[1])
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should coerce `path` to a string', function (assert) {
        assert.expect(2);
        function fn() {
        }
        fn.toString = lodashStable.constant('fn');
        var object = {
                'null': 1,
                'undefined': 2,
                'fn': 3,
                '[object Object]': 4
            }, paths = [
                null,
                undefined,
                fn,
                {}
            ], expected = lodashStable.map(paths, stubTrue);
        lodashStable.times(2, function (index) {
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
            'a.b': 1,
            'a': { 'b': 2 }
        };
        lodashStable.each([
            'a.b',
            ['a.b']
        ], function (path) {
            var matches = _.matchesProperty(path, 1);
            assert.strictEqual(matches(object), true);
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
            'constructor',
            ['constructor']
        ], function (path) {
            var matches = _.matchesProperty(path, 1);
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
            'constructor.prototype.valueOf',
            [
                'constructor',
                'prototype',
                'valueOf'
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, 1);
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
            'a',
            'a[1].b.c',
            ['a'],
            [
                'a',
                '1',
                'b',
                'c'
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, 1);
            assert.strictEqual(matches(object), false);
        });
    });
    QUnit.test('should match inherited string keyed `srcValue` properties', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype.b = 2;
        var object = { 'a': new Foo() };
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            var matches = _.matchesProperty(path, { 'b': 2 });
            assert.strictEqual(matches(object), true);
        });
    });
    QUnit.test('should not match by inherited `srcValue` properties', function (assert) {
        assert.expect(2);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var objects = [
                { 'a': { 'a': 1 } },
                {
                    'a': {
                        'a': 1,
                        'b': 2
                    }
                }
            ], expected = lodashStable.map(objects, stubTrue);
        lodashStable.each([
            'a',
            ['a']
        ], function (path) {
            assert.deepEqual(lodashStable.map(objects, _.matchesProperty(path, new Foo())), expected);
        });
    });
    QUnit.test('should compare a variety of values', function (assert) {
        assert.expect(2);
        var object1 = {
                'a': __bool_top__,
                'b': true,
                'c': '3',
                'd': 4,
                'e': [5],
                'f': { 'g': 6 }
            }, object2 = {
                'a': 0,
                'b': 1,
                'c': 3,
                'd': '4',
                'e': ['5'],
                'f': { 'g': '6' }
            }, matches = _.matchesProperty('a', object1);
        assert.strictEqual(matches({ 'a': object1 }), true);
        assert.strictEqual(matches({ 'a': object2 }), false);
    });
    QUnit.test('should match `-0` as `0`', function (assert) {
        assert.expect(2);
        var matches = _.matchesProperty('a', -0);
        assert.strictEqual(matches({ 'a': 0 }), true);
        matches = _.matchesProperty('a', 0);
        assert.strictEqual(matches({ 'a': -0 }), true);
    });
    QUnit.test('should compare functions by reference', function (assert) {
        assert.expect(3);
        var object1 = { 'a': lodashStable.noop }, object2 = { 'a': noop }, object3 = { 'a': {} }, matches = _.matchesProperty('a', object1);
        assert.strictEqual(matches({ 'a': object1 }), true);
        assert.strictEqual(matches({ 'a': object2 }), false);
        assert.strictEqual(matches({ 'a': object3 }), false);
    });
    QUnit.test('should work with a function for `srcValue`', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.a = 1;
        Foo.b = function () {
        };
        Foo.c = 3;
        var objects = [
                { 'a': { 'a': 1 } },
                {
                    'a': {
                        'a': 1,
                        'b': Foo.b,
                        'c': 3
                    }
                }
            ], actual = lodashStable.map(objects, _.matchesProperty('a', Foo));
        assert.deepEqual(actual, [
            false,
            true
        ]);
    });
    QUnit.test('should work with a non-plain `srcValue`', function (assert) {
        assert.expect(1);
        function Foo(object) {
            lodashStable.assign(this, object);
        }
        var object = new Foo({
                'a': new Foo({
                    'b': 1,
                    'c': 2
                })
            }), matches = _.matchesProperty('a', { 'b': 1 });
        assert.strictEqual(matches(object), true);
    });
    QUnit.test('should partial match arrays', function (assert) {
        assert.expect(3);
        var objects = [
                { 'a': ['b'] },
                {
                    'a': [
                        'c',
                        'd'
                    ]
                }
            ], actual = lodashStable.filter(objects, _.matchesProperty('a', ['d']));
        assert.deepEqual(actual, [objects[1]]);
        actual = lodashStable.filter(objects, _.matchesProperty('a', [
            'b',
            'd'
        ]));
        assert.deepEqual(actual, []);
        actual = lodashStable.filter(objects, _.matchesProperty('a', [
            'd',
            'b'
        ]));
        assert.deepEqual(actual, []);
    });
    QUnit.test('should partial match arrays with duplicate values', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'a': [
                        1,
                        2
                    ]
                },
                {
                    'a': [
                        2,
                        2
                    ]
                }
            ], actual = lodashStable.filter(objects, _.matchesProperty('a', [
                2,
                2
            ]));
        assert.deepEqual(actual, [objects[1]]);
    });
    QUnit.test('should partial match arrays of objects', function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': [
                    {
                        'a': 1,
                        'b': 2
                    },
                    {
                        'a': 4,
                        'b': 5,
                        'c': 6
                    }
                ]
            },
            {
                'a': [
                    {
                        'a': 1,
                        'b': 2
                    },
                    {
                        'a': 4,
                        'b': 6,
                        'c': 7
                    }
                ]
            }
        ];
        var actual = lodashStable.filter(objects, _.matchesProperty('a', [
            { 'a': 1 },
            {
                'a': 4,
                'b': 5
            }
        ]));
        assert.deepEqual(actual, [objects[0]]);
    });
    QUnit.test('should partial match maps', function (assert) {
        assert.expect(3);
        if (Map) {
            var objects = [
                { 'a': new Map() },
                { 'a': new Map() }
            ];
            objects[0].a.set('a', 1);
            objects[1].a.set('a', 1);
            objects[1].a.set('b', 2);
            var map = new Map();
            map.set('b', 2);
            var actual = lodashStable.filter(objects, _.matchesProperty('a', map));
            assert.deepEqual(actual, [objects[1]]);
            map.delete('b');
            actual = lodashStable.filter(objects, _.matchesProperty('a', map));
            assert.deepEqual(actual, objects);
            map.set('c', 3);
            actual = lodashStable.filter(objects, _.matchesProperty('a', map));
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
            objects[0].a.add(1);
            objects[1].a.add(1);
            objects[1].a.add(2);
            var set = new Set();
            set.add(2);
            var actual = lodashStable.filter(objects, _.matchesProperty('a', set));
            assert.deepEqual(actual, [objects[1]]);
            set.delete(2);
            actual = lodashStable.filter(objects, _.matchesProperty('a', set));
            assert.deepEqual(actual, objects);
            set.add(3);
            actual = lodashStable.filter(objects, _.matchesProperty('a', set));
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should match `undefined` values', function (assert) {
        assert.expect(2);
        var objects = [
                { 'a': 1 },
                {
                    'a': 1,
                    'b': 1
                },
                {
                    'a': 1,
                    'b': undefined
                }
            ], actual = lodashStable.map(objects, _.matchesProperty('b', undefined)), expected = [
                false,
                false,
                true
            ];
        assert.deepEqual(actual, expected);
        objects = [
            { 'a': { 'a': 1 } },
            {
                'a': {
                    'a': 1,
                    'b': 1
                }
            },
            {
                'a': {
                    'a': 1,
                    'b': undefined
                }
            }
        ];
        actual = lodashStable.map(objects, _.matchesProperty('a', { 'b': undefined }));
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should match `undefined` values of nested objects', function (assert) {
        assert.expect(4);
        var object = { 'a': { 'b': undefined } };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, undefined);
            assert.strictEqual(matches(object), true);
        });
        lodashStable.each([
            'a.a',
            [
                'a',
                'a'
            ]
        ], function (path) {
            var matches = _.matchesProperty(path, undefined);
            assert.strictEqual(matches(object), false);
        });
    });
    QUnit.test('should match `undefined` values on primitives', function (assert) {
        assert.expect(2);
        numberProto.a = 1;
        numberProto.b = undefined;
        try {
            var matches = _.matchesProperty('b', undefined);
            assert.strictEqual(matches(1), true);
        } catch (e) {
            assert.ok(false, e.message);
        }
        numberProto.a = {
            'b': 1,
            'c': undefined
        };
        try {
            matches = _.matchesProperty('a', { 'c': undefined });
            assert.strictEqual(matches(1), true);
        } catch (e) {
            assert.ok(false, e.message);
        }
        delete numberProto.a;
        delete numberProto.b;
    });
    QUnit.test('should return `true` when comparing a `srcValue` of empty arrays and objects', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'a': [1],
                    'b': { 'c': 1 }
                },
                {
                    'a': [
                        2,
                        3
                    ],
                    'b': { 'd': 2 }
                }
            ], matches = _.matchesProperty('a', {
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
                    'b': 2,
                    'c': 3
                }
            },
            {
                'a': 1,
                'b': 2
            },
            { 'a': 1 }
        ], function (source, index) {
            var object = lodashStable.cloneDeep(source), matches = _.matchesProperty('a', source);
            assert.strictEqual(matches({ 'a': object }), true);
            if (index) {
                source.a = 2;
                source.b = 1;
                source.c = 3;
            } else {
                source.a.b = 1;
                source.a.c = 2;
                source.a.d = 3;
            }
            assert.strictEqual(matches({ 'a': object }), true);
            assert.strictEqual(matches({ 'a': source }), false);
        });
    });
}());