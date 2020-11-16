QUnit.module('lodash.invertBy');
(function () {
    var object = {
        'a': 1,
        'b': 2,
        'c': __num_top__
    };
    QUnit.test('should transform keys by `iteratee`', function (assert) {
        assert.expect(1);
        var expected = {
            'group1': [
                'a',
                __str_top__
            ],
            'group2': ['b']
        };
        var actual = _.invertBy(object, function (value) {
            return __str_top__ + value;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant({
                '1': [
                    __str_top__,
                    'c'
                ],
                '2': ['b']
            }));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.invertBy(object, value) : _.invertBy(object);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should only add multiple values to own, not inherited, properties', function (assert) {
        assert.expect(1);
        var object = {
                'a': __str_top__,
                'b': 'constructor'
            }, expected = {
                'hasOwnProperty': [__str_top__],
                'constructor': [__str_top__]
            };
        assert.ok(lodashStable.isEqual(_.invertBy(object), expected));
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _(object).invertBy();
            assert.ok(wrapped instanceof _);
            assert.deepEqual(wrapped.value(), {
                '1': [
                    'a',
                    __str_top__
                ],
                '2': [__str_top__]
            });
        } else {
            skipAssert(assert, 2);
        }
    });
}());