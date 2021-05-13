QUnit.module('matches methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var isMatches = methodName == __str_top__;
    function matches(source) {
        return isMatches ? _.matches(source) : function (object) {
            return _.isMatch(object, source);
        };
    }
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(5);
        var object = {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__
            }, par = matches({ 'a': __num_top__ });
        assert.strictEqual(par(object), __bool_top__);
        par = matches({ 'b': __num_top__ });
        assert.strictEqual(par(object), __bool_top__);
        par = matches({
            'a': __num_top__,
            'c': __num_top__
        });
        assert.strictEqual(par(object), __bool_top__);
        par = matches({
            'c': __num_top__,
            'd': __num_top__
        });
        assert.strictEqual(par(object), __bool_top__);
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
        par = matches({ 'a': { 'b': { 'c': __num_top__ } } });
        assert.strictEqual(par(object), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var object = { 'a': new Foo() }, par = matches({ 'a': { 'b': __num_top__ } });
        assert.strictEqual(par(object), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var objects = [
                { 'a': __num_top__ },
                {
                    'a': __num_top__,
                    'b': __num_top__
                }
            ], source = new Foo(), actual = lodashStable.map(objects, matches(source)), expected = lodashStable.map(objects, stubTrue);
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
            }, par = matches(object1);
        assert.strictEqual(par(object1), __bool_top__);
        assert.strictEqual(par(object2), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object1 = { 'a': -__num_top__ }, object2 = { 'a': __num_top__ }, par = matches(object1);
        assert.strictEqual(par(object2), __bool_top__);
        par = matches(object2);
        assert.strictEqual(par(object1), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var object1 = { 'a': lodashStable.noop }, object2 = { 'a': noop }, object3 = { 'a': {} }, par = matches(object1);
        assert.strictEqual(par(object1), __bool_top__);
        assert.strictEqual(par(object2), __bool_top__);
        assert.strictEqual(par(object3), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.a = {
            'b': __num_top__,
            'c': __num_top__
        };
        var par = matches({ 'a': { 'b': __num_top__ } });
        assert.strictEqual(par(Foo), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.a = __num_top__;
        Foo.b = function () {
        };
        Foo.c = __num_top__;
        var objects = [
                { 'a': __num_top__ },
                {
                    'a': __num_top__,
                    'b': Foo.b,
                    'c': __num_top__
                }
            ], actual = lodashStable.map(objects, matches(Foo));
        assert.deepEqual(actual, [
            __bool_top__,
            __bool_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo(object) {
            lodashStable.assign(this, object);
        }
        var object = new Foo({
                'a': new Foo({
                    'b': __num_top__,
                    'c': __num_top__
                })
            }), par = matches({ 'a': { 'b': __num_top__ } });
        assert.strictEqual(par(object), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var objects = [
                { 'a': [__str_top__] },
                {
                    'a': [
                        __str_top__,
                        __str_top__
                    ]
                }
            ], actual = lodashStable.filter(objects, matches({ 'a': [__str_top__] }));
        assert.deepEqual(actual, [objects[__num_top__]]);
        actual = lodashStable.filter(objects, matches({
            'a': [
                __str_top__,
                __str_top__
            ]
        }));
        assert.deepEqual(actual, []);
        actual = lodashStable.filter(objects, matches({
            'a': [
                __str_top__,
                __str_top__
            ]
        }));
        assert.deepEqual(actual, []);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
            ], actual = lodashStable.filter(objects, matches({
                'a': [
                    __num_top__,
                    __num_top__
                ]
            }));
        assert.deepEqual(actual, [objects[__num_top__]]);
    });
    QUnit.test('should partial match arrays of objects', function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': [
                    {
                        'b': __num_top__,
                        'c': __num_top__
                    },
                    {
                        'b': __num_top__,
                        'c': __num_top__,
                        'd': __num_top__
                    }
                ]
            },
            {
                'a': [
                    {
                        'b': __num_top__,
                        'c': __num_top__
                    },
                    {
                        'b': __num_top__,
                        'c': __num_top__,
                        'd': __num_top__
                    }
                ]
            }
        ];
        var actual = lodashStable.filter(objects, matches({
            'a': [
                { 'b': __num_top__ },
                {
                    'b': __num_top__,
                    'c': __num_top__
                }
            ]
        }));
        assert.deepEqual(actual, [objects[__num_top__]]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
            var actual = lodashStable.filter(objects, matches({ 'a': map }));
            assert.deepEqual(actual, [objects[__num_top__]]);
            map.delete(__str_top__);
            actual = lodashStable.filter(objects, matches({ 'a': map }));
            assert.deepEqual(actual, objects);
            map.set(__str_top__, __num_top__);
            actual = lodashStable.filter(objects, matches({ 'a': map }));
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
            var actual = lodashStable.filter(objects, matches({ 'a': set }));
            assert.deepEqual(actual, [objects[__num_top__]]);
            set.delete(__num_top__);
            actual = lodashStable.filter(objects, matches({ 'a': set }));
            assert.deepEqual(actual, objects);
            set.add(__num_top__);
            actual = lodashStable.filter(objects, matches({ 'a': set }));
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
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
            ], actual = lodashStable.map(objects, matches({ 'b': undefined })), expected = [
                __bool_top__,
                __bool_top__,
                __bool_top__
            ];
        assert.deepEqual(actual, expected);
        actual = lodashStable.map(objects, matches({
            'a': __num_top__,
            'b': undefined
        }));
        assert.deepEqual(actual, expected);
        objects = [
            { 'a': { 'b': __num_top__ } },
            {
                'a': {
                    'b': __num_top__,
                    'c': __num_top__
                }
            },
            {
                'a': {
                    'b': __num_top__,
                    'c': undefined
                }
            }
        ];
        actual = lodashStable.map(objects, matches({ 'a': { 'c': undefined } }));
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        numberProto.a = __num_top__;
        numberProto.b = undefined;
        try {
            var par = matches({ 'b': undefined });
            assert.strictEqual(par(__num_top__), __bool_top__);
        } catch (e) {
            assert.ok(__bool_top__, e.message);
        }
        try {
            par = matches({
                'a': __num_top__,
                'b': undefined
            });
            assert.strictEqual(par(__num_top__), __bool_top__);
        } catch (e) {
            assert.ok(__bool_top__, e.message);
        }
        numberProto.a = {
            'b': __num_top__,
            'c': undefined
        };
        try {
            par = matches({ 'a': { 'c': undefined } });
            assert.strictEqual(par(__num_top__), __bool_top__);
        } catch (e) {
            assert.ok(__bool_top__, e.message);
        }
        delete numberProto.a;
        delete numberProto.b;
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse), par = matches({ 'a': __num_top__ });
        var actual = lodashStable.map(values, function (value, index) {
            try {
                return index ? par(value) : par();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = { 'a': __num_top__ }, expected = lodashStable.map(empties, stubTrue);
        var actual = lodashStable.map(empties, function (value) {
            var par = matches(value);
            return par(object);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
            ], actual = lodashStable.filter(objects, matches({
                'a': [],
                'b': {}
            }));
        assert.deepEqual(actual, objects);
    });
});