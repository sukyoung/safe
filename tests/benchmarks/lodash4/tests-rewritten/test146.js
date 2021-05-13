QUnit.module('lodash.mapValues');
(function () {
    var array = [
            __num_top__,
            __num_top__
        ], object = {
            'a': __num_top__,
            'b': __num_top__
        };
    QUnit.test('should map values in `object` to a new object', function (assert) {
        assert.expect(1);
        var actual = _.mapValues(object, String);
        assert.deepEqual(actual, {
            'a': __str_top__,
            'b': __str_top__
        });
    });
    QUnit.test('should treat arrays like objects', function (assert) {
        assert.expect(1);
        var actual = _.mapValues(array, String);
        assert.deepEqual(actual, {
            '0': __str_top__,
            '1': __str_top__
        });
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var actual = _.mapValues({ 'a': { 'b': __num_top__ } }, __str_top__);
        assert.deepEqual(actual, { 'a': __num_top__ });
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
            ], expected = lodashStable.map(values, lodashStable.constant([
                __bool_top__,
                __bool_top__
            ]));
        var actual = lodashStable.map(values, function (value, index) {
            var result = index ? _.mapValues(object, value) : _.mapValues(object);
            return [
                lodashStable.isEqual(result, object),
                result === object
            ];
        });
        assert.deepEqual(actual, expected);
    });
}());