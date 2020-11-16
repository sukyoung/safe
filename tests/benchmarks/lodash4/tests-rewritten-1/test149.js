QUnit.module('matches methods');
lodashStable.each([
    'matches',
    'isMatch'
], function (methodName) {
    var isMatches = methodName == 'matches';
    function matches(source) {
        return isMatches ? _.matches(source) : function (object) {
            return _.isMatch(object, source);
        };
    }
    QUnit.test('`_.' + methodName + '` should perform a deep comparison between `source` and `object`', function (assert) {
        assert.expect(5);
        var object = {
                'a': 1,
                'b': 2,
                'c': 3
            }, par = matches({ 'a': 1 });
        assert.strictEqual(par(object), true);
        par = matches({ 'b': 1 });
        assert.strictEqual(par(object), false);
        par = matches({
            'a': 1,
            'c': 3
        });
        assert.strictEqual(par(object), true);
        par = matches({
            'c': 3,
            'd': 4
        });
        assert.strictEqual(par(object), false);
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
        par = matches({ 'a': { 'b': { 'c': 1 } } });
        assert.strictEqual(par(object), true);
    });
    QUnit.test('`_.' + methodName + '` should match inherited string keyed `object` properties', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var object = { 'a': new Foo() }, par = matches({ 'a': { 'b': 2 } });
        assert.strictEqual(par(object), true);
    });
    QUnit.test('`_.' + methodName + '` should not match by inherited `source` properties', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var objects = [
                { 'a': 1 },
                {
                    'a': 1,
                    'b': __num_top__
                }
            ], source = new Foo(), actual = lodashStable.map(objects, matches(source)), expected = lodashStable.map(objects, stubTrue);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should compare a variety of `source` property values', function (assert) {
        assert.expect(2);
        var object1 = {
                'a': false,
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
            }, par = matches(object1);
        assert.strictEqual(par(object1), true);
        assert.strictEqual(par(object2), false);
    });
    QUnit.test('`_.' + methodName + '` should match `-0` as `0`', function (assert) {
        assert.expect(2);
        var object1 = { 'a': -0 }, object2 = { 'a': 0 }, par = matches(object1);
        assert.strictEqual(par(object2), true);
        par = matches(object2);
        assert.strictEqual(par(object1), true);
    });
    QUnit.test('`_.' + methodName + '` should compare functions by reference', function (assert) {
        assert.expect(3);
        var object1 = { 'a': lodashStable.noop }, object2 = { 'a': noop }, object3 = { 'a': {} }, par = matches(object1);
        assert.strictEqual(par(object1), true);
        assert.strictEqual(par(object2), false);
        assert.strictEqual(par(object3), false);
    });
    QUnit.test('`_.' + methodName + '` should work with a function for `object`', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.a = {
            'b': 2,
            'c': 3
        };
        var par = matches({ 'a': { 'b': 2 } });
        assert.strictEqual(par(Foo), true);
    });
    QUnit.test('`_.' + methodName + '` should work with a function for `source`', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.a = 1;
        Foo.b = function () {
        };
        Foo.c = 3;
        var objects = [
                { 'a': 1 },
                {
                    'a': 1,
                    'b': Foo.b,
                    'c': 3
                }
            ], actual = lodashStable.map(objects, matches(Foo));
        assert.deepEqual(actual, [
            false,
            true
        ]);
    });
    QUnit.test('`_.' + methodName + '` should work with a non-plain `object`', function (assert) {
        assert.expect(1);
        function Foo(object) {
            lodashStable.assign(this, object);
        }
        var object = new Foo({
                'a': new Foo({
                    'b': 2,
                    'c': 3
                })
            }), par = matches({ 'a': { 'b': 2 } });
        assert.strictEqual(par(object), true);
    });
    QUnit.test('`_.' + methodName + '` should partial match arrays', function (assert) {
        assert.expect(3);
        var objects = [
                { 'a': ['b'] },
                {
                    'a': [
                        'c',
                        'd'
                    ]
                }
            ], actual = lodashStable.filter(objects, matches({ 'a': ['d'] }));
        assert.deepEqual(actual, [objects[1]]);
        actual = lodashStable.filter(objects, matches({
            'a': [
                'b',
                'd'
            ]
        }));
        assert.deepEqual(actual, []);
        actual = lodashStable.filter(objects, matches({
            'a': [
                'd',
                'b'
            ]
        }));
        assert.deepEqual(actual, []);
    });
    QUnit.test('`_.' + methodName + '` should partial match arrays with duplicate values', function (assert) {
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
            ], actual = lodashStable.filter(objects, matches({
                'a': [
                    2,
                    2
                ]
            }));
        assert.deepEqual(actual, [objects[1]]);
    });
    QUnit.test('should partial match arrays of objects', function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': [
                    {
                        'b': 1,
                        'c': 2
                    },
                    {
                        'b': 4,
                        'c': 5,
                        'd': 6
                    }
                ]
            },
            {
                'a': [
                    {
                        'b': 1,
                        'c': 2
                    },
                    {
                        'b': 4,
                        'c': 6,
                        'd': 7
                    }
                ]
            }
        ];
        var actual = lodashStable.filter(objects, matches({
            'a': [
                { 'b': 1 },
                {
                    'b': 4,
                    'c': 5
                }
            ]
        }));
        assert.deepEqual(actual, [objects[0]]);
    });
    QUnit.test('`_.' + methodName + '` should partial match maps', function (assert) {
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
            var actual = lodashStable.filter(objects, matches({ 'a': map }));
            assert.deepEqual(actual, [objects[1]]);
            map.delete('b');
            actual = lodashStable.filter(objects, matches({ 'a': map }));
            assert.deepEqual(actual, objects);
            map.set('c', 3);
            actual = lodashStable.filter(objects, matches({ 'a': map }));
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('`_.' + methodName + '` should partial match sets', function (assert) {
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
            var actual = lodashStable.filter(objects, matches({ 'a': set }));
            assert.deepEqual(actual, [objects[1]]);
            set.delete(2);
            actual = lodashStable.filter(objects, matches({ 'a': set }));
            assert.deepEqual(actual, objects);
            set.add(3);
            actual = lodashStable.filter(objects, matches({ 'a': set }));
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('`_.' + methodName + '` should match `undefined` values', function (assert) {
        assert.expect(3);
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
            ], actual = lodashStable.map(objects, matches({ 'b': undefined })), expected = [
                false,
                false,
                true
            ];
        assert.deepEqual(actual, expected);
        actual = lodashStable.map(objects, matches({
            'a': 1,
            'b': undefined
        }));
        assert.deepEqual(actual, expected);
        objects = [
            { 'a': { 'b': 2 } },
            {
                'a': {
                    'b': 2,
                    'c': 3
                }
            },
            {
                'a': {
                    'b': 2,
                    'c': undefined
                }
            }
        ];
        actual = lodashStable.map(objects, matches({ 'a': { 'c': undefined } }));
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should match `undefined` values on primitives', function (assert) {
        assert.expect(3);
        numberProto.a = 1;
        numberProto.b = undefined;
        try {
            var par = matches({ 'b': undefined });
            assert.strictEqual(par(1), true);
        } catch (e) {
            assert.ok(false, e.message);
        }
        try {
            par = matches({
                'a': 1,
                'b': undefined
            });
            assert.strictEqual(par(1), true);
        } catch (e) {
            assert.ok(false, e.message);
        }
        numberProto.a = {
            'b': 1,
            'c': undefined
        };
        try {
            par = matches({ 'a': { 'c': undefined } });
            assert.strictEqual(par(1), true);
        } catch (e) {
            assert.ok(false, e.message);
        }
        delete numberProto.a;
        delete numberProto.b;
    });
    QUnit.test('`_.' + methodName + '` should return `false` when `object` is nullish', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse), par = matches({ 'a': 1 });
        var actual = lodashStable.map(values, function (value, index) {
            try {
                return index ? par(value) : par();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should return `true` when comparing an empty `source`', function (assert) {
        assert.expect(1);
        var object = { 'a': 1 }, expected = lodashStable.map(empties, stubTrue);
        var actual = lodashStable.map(empties, function (value) {
            var par = matches(value);
            return par(object);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should return `true` when comparing an empty `source` to a nullish `object`', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubTrue), par = matches({});
        var actual = lodashStable.map(values, function (value, index) {
            try {
                return index ? par(value) : par();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should return `true` when comparing a `source` of empty arrays and objects', function (assert) {
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
            ], actual = lodashStable.filter(objects, matches({
                'a': [],
                'b': {}
            }));
        assert.deepEqual(actual, objects);
    });
});