QUnit.module('lodash.matches');
(function () {
    QUnit.test('should not change behavior if `source` is modified', function (assert) {
        assert.expect(9);
        var sources = [
            {
                'a': {
                    'b': __num_top__,
                    'c': __num_top__
                }
            },
            {
                'a': __num_top__,
                'b': __num_top__
            },
            { 'a': __num_top__ }
        ];
        lodashStable.each(sources, function (source, index) {
            var object = lodashStable.cloneDeep(source), par = _.matches(source);
            assert.strictEqual(par(object), __bool_top__);
            if (index) {
                source.a = __num_top__;
                source.b = __num_top__;
                source.c = __num_top__;
            } else {
                source.a.b = __num_top__;
                source.a.c = __num_top__;
                source.a.d = __num_top__;
            }
            assert.strictEqual(par(object), __bool_top__);
            assert.strictEqual(par(source), __bool_top__);
        });
    });
}());