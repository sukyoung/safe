QUnit.module('lodash.invertBy');
(function () {
    var object = {
        'a': 1,
        'b': 2,
        'c': 1
    };
    QUnit.test('should transform keys by `iteratee`', function (assert) {
        assert.expect(1);
        var expected = {
            'group1': [
                'a',
                'c'
            ],
            'group2': ['b']
        };
        var actual = _.invertBy(object, function (value) {
            return 'group' + value;
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
                    'a',
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
                'a': 'hasOwnProperty',
                'b': 'constructor'
            }, expected = {
                'hasOwnProperty': ['a'],
                'constructor': ['b']
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
                    'c'
                ],
                '2': [__str_top__]
            });
        } else {
            skipAssert(assert, 2);
        }
    });
}());