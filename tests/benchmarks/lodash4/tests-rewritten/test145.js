QUnit.module('lodash.mapKeys');
(function () {
    var array = [
            __num_top__,
            __num_top__
        ], object = {
            'a': __num_top__,
            'b': __num_top__
        };
    QUnit.test('should map keys in `object` to a new object', function (assert) {
        assert.expect(1);
        var actual = _.mapKeys(object, String);
        assert.deepEqual(actual, {
            '1': __num_top__,
            '2': __num_top__
        });
    });
    QUnit.test('should treat arrays like objects', function (assert) {
        assert.expect(1);
        var actual = _.mapKeys(array, String);
        assert.deepEqual(actual, {
            '1': __num_top__,
            '2': __num_top__
        });
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var actual = _.mapKeys({ 'a': { 'b': __str_top__ } }, __str_top__);
        assert.deepEqual(actual, { 'c': { 'b': __str_top__ } });
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var object = {
                'a': __num_top__,
                'b': __num_top__
            }, values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant({
                '1': __num_top__,
                '2': __num_top__
            }));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.mapKeys(object, value) : _.mapKeys(object);
        });
        assert.deepEqual(actual, expected);
    });
}());