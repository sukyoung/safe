QUnit.module('lodash.filter');
(function () {
    var array = [
        __num_top__,
        2,
        3
    ];
    QUnit.test('should return elements `predicate` returns truthy for', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.filter(array, isEven), [2]);
    });
}());