QUnit.module('lodash.keyBy');
(function () {
    var array = [
        {
            'dir': __str_top__,
            'code': __num_top__
        },
        {
            'dir': __str_top__,
            'code': __num_top__
        }
    ];
    QUnit.test('should transform keys by `iteratee`', function (assert) {
        assert.expect(1);
        var expected = {
            'a': {
                'dir': __str_top__,
                'code': __num_top__
            },
            'd': {
                'dir': __str_top__,
                'code': __num_top__
            }
        };
        var actual = _.keyBy(array, function (object) {
            return String.fromCharCode(object.code);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant({
                '4': __num_top__,
                '6': __num_top__
            }));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.keyBy(array, value) : _.keyBy(array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var expected = {
                'left': {
                    'dir': __str_top__,
                    'code': __num_top__
                },
                'right': {
                    'dir': __str_top__,
                    'code': __num_top__
                }
            }, actual = _.keyBy(array, __str_top__);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should only add values to own, not inherited, properties', function (assert) {
        assert.expect(2);
        var actual = _.keyBy([
            __num_top__,
            __num_top__,
            __num_top__
        ], function (n) {
            return Math.floor(n) > __num_top__ ? __str_top__ : __str_top__;
        });
        assert.deepEqual(actual.constructor, __num_top__);
        assert.deepEqual(actual.hasOwnProperty, __num_top__);
    });
    QUnit.test('should work with a number for `iteratee`', function (assert) {
        assert.expect(2);
        var array = [
            [
                __num_top__,
                __str_top__
            ],
            [
                __num_top__,
                __str_top__
            ],
            [
                __num_top__,
                __str_top__
            ]
        ];
        assert.deepEqual(_.keyBy(array, __num_top__), {
            '1': [
                __num_top__,
                __str_top__
            ],
            '2': [
                __num_top__,
                __str_top__
            ]
        });
        assert.deepEqual(_.keyBy(array, __num_top__), {
            'a': [
                __num_top__,
                __str_top__
            ],
            'b': [
                __num_top__,
                __str_top__
            ]
        });
    });
    QUnit.test('should work with an object for `collection`', function (assert) {
        assert.expect(1);
        var actual = _.keyBy({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }, Math.floor);
        assert.deepEqual(actual, {
            '4': __num_top__,
            '6': __num_top__
        });
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE).concat(lodashStable.range(Math.floor(LARGE_ARRAY_SIZE / __num_top__), LARGE_ARRAY_SIZE), lodashStable.range(Math.floor(LARGE_ARRAY_SIZE / __num_top__), LARGE_ARRAY_SIZE));
            var actual = _(array).keyBy().map(square).filter(isEven).take().value();
            assert.deepEqual(actual, _.take(_.filter(_.map(_.keyBy(array), square), isEven)));
        } else {
            skipAssert(assert);
        }
    });
}());