QUnit.module('lodash.matches');
(function () {
    QUnit.test('should not change behavior if `source` is modified', function (assert) {
        assert.expect(9);
        var sources = [
            {
                'a': {
                    'b': 2,
                    'c': __num_top__
                }
            },
            {
                'a': __num_top__,
                'b': 2
            },
            { 'a': 1 }
        ];
        lodashStable.each(sources, function (source, index) {
            var object = lodashStable.cloneDeep(source), par = _.matches(source);
            assert.strictEqual(par(object), true);
            if (index) {
                source.a = 2;
                source.b = 1;
                source.c = 3;
            } else {
                source.a.b = __num_top__;
                source.a.c = __num_top__;
                source.a.d = 3;
            }
            assert.strictEqual(par(object), true);
            assert.strictEqual(par(source), __bool_top__);
        });
    });
}());