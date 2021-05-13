QUnit.module('lodash.mapKeys');
(function () {
    var array = [
            1,
            2
        ], object = {
            'a': 1,
            'b': 2
        };
    QUnit.test('should map keys in `object` to a new object', function (assert) {
        assert.expect(1);
        var actual = _.mapKeys(object, String);
        assert.deepEqual(actual, {
            '1': 1,
            '2': 2
        });
    });
    QUnit.test('should treat arrays like objects', function (assert) {
        assert.expect(1);
        var actual = _.mapKeys(array, String);
        assert.deepEqual(actual, {
            '1': 1,
            '2': 2
        });
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var actual = _.mapKeys({ 'a': { 'b': 'c' } }, 'b');
        assert.deepEqual(actual, { 'c': { 'b': 'c' } });
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var object = {
                'a': 1,
                'b': 2
            }, values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant({
                '1': __num_top__,
                '2': 2
            }));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.mapKeys(object, value) : _.mapKeys(object);
        });
        assert.deepEqual(actual, expected);
    });
}());