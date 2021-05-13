QUnit.module('lodash.filter');
(function () {
    var array = [
        1,
        2,
        3
    ];
    QUnit.test('should return elements `predicate` returns truthy for', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.filter(array, isEven), [__num_top__]);
    });
}());