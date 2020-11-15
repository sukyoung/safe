QUnit.module('toPairs methods');
lodashStable.each([
    'toPairs',
    'toPairsIn'
], function (methodName) {
    var func = _[methodName], isToPairs = methodName == 'toPairs';
    QUnit.test('`_.' + methodName + '` should create an array of string keyed-value pairs', function (assert) {
        assert.expect(1);
        var object = {
                'a': 1,
                'b': 2
            }, actual = lodashStable.sortBy(func(object), 0);
        assert.deepEqual(actual, [
            [
                'a',
                1
            ],
            [
                __str_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('`_.' + methodName + '` should ' + (isToPairs ? 'not ' : __str_top__) + 'include inherited string keyed property values', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var expected = isToPairs ? [[
                    'a',
                    1
                ]] : [
                [
                    'a',
                    1
                ],
                [
                    'b',
                    2
                ]
            ], actual = lodashStable.sortBy(func(new Foo()), 0);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should convert objects with a `length` property', function (assert) {
        assert.expect(1);
        var object = {
                '0': 'a',
                '1': 'b',
                'length': 2
            }, actual = lodashStable.sortBy(func(object), 0);
        assert.deepEqual(actual, [
            [
                __str_top__,
                __str_top__
            ],
            [
                '1',
                'b'
            ],
            [
                __str_top__,
                2
            ]
        ]);
    });
    QUnit.test(__str_top__ + methodName + '` should convert maps', function (assert) {
        assert.expect(1);
        if (Map) {
            var map = new Map();
            map.set('a', __num_top__);
            map.set('b', 2);
            assert.deepEqual(func(map), [
                [
                    __str_top__,
                    1
                ],
                [
                    __str_top__,
                    2
                ]
            ]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` should convert sets', function (assert) {
        assert.expect(1);
        if (Set) {
            var set = new Set();
            set.add(1);
            set.add(2);
            assert.deepEqual(func(set), [
                [
                    1,
                    1
                ],
                [
                    2,
                    2
                ]
            ]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` should convert strings', function (assert) {
        assert.expect(2);
        lodashStable.each([
            'xo',
            Object('xo')
        ], function (string) {
            var actual = lodashStable.sortBy(func(string), 0);
            assert.deepEqual(actual, [
                [
                    '0',
                    'x'
                ],
                [
                    '1',
                    'o'
                ]
            ]);
        });
    });
});