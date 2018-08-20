/* TinySort 1.4.29
 * Copyright (c) 2008-2012 Ron Valstar http://www.sjeiti.com/
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 */
(function (c) {
    var e = !1, f = null, j = parseFloat, g = Math.min, i = /(-?\d+\.?\d*)$/g, h = [], d = [];
    c.tinysort = {
        id: "TinySort",
        version: "1.4.29",
        copyright: "Copyright (c) 2008-2012 Ron Valstar",
        uri: "http://tinysort.sjeiti.com/",
        licensed: {
            MIT: "http://www.opensource.org/licenses/mit-license.php",
            GPL: "http://www.gnu.org/licenses/gpl.html"
        },
        plugin: function (k, l) {
            h.push(k);
            d.push(l)
        },
        defaults: {
            order: "asc",
            attr: f,
            data: f,
            useVal: e,
            place: "start",
            returns: e,
            cases: e,
            forceStrings: e,
            sortFunction: f
        }
    };
    c.fn.extend({
        tinysort: function (o, k) {
            if (o && typeof(o) != "string") {
                k = o;
                o = f
            }
            var p = c.extend({}, c.tinysort.defaults, k), u, D = this, z = c(this).length, E = {}, r = !(!o || o == ""), s = !(p.attr === f || p.attr == ""), y = p.data !== f, l = r && o[0] == ":", m = l ? D.filter(o) : D, t = p.sortFunction, x = p.order == "asc" ? 1 : -1, n = [];
            c.each(h, function (G, H) {
                H.call(H, p)
            });
            if (!t) {
                t = p.order == "rand" ? function () {
                    return Math.random() < 0.5 ? 1 : -1
                } : function (O, M) {
                    var N = e, J = !p.cases ? a(O.s) : O.s, I = !p.cases ? a(M.s) : M.s;
                    if (!p.forceStrings) {
                        var H = J && J.match(i), P = I && I.match(i);
                        if (H && P) {
                            var L = J.substr(0, J.length - H[0].length), K = I.substr(0, I.length - P[0].length);
                            if (L == K) {
                                N = !e;
                                J = j(H[0]);
                                I = j(P[0])
                            }
                        }
                    }
                    var G = x * (J < I ? -1 : (J > I ? 1 : 0));
                    c.each(d, function (Q, R) {
                        G = R.call(R, N, J, I, G)
                    });
                    return G
                }
            }
            D.each(function (I, J) {
                var K = c(J), G = r ? (l ? m.filter(J) : K.find(o)) : K, L = y ? "" + G.data(p.data) : (s ? G.attr(p.attr) : (p.useVal ? G.val() : G.text())), H = K.parent();
                if (!E[H]) {
                    E[H] = {s: [], n: []}
                }
                if (G.length > 0) {
                    E[H].s.push({s: L, e: K, n: I})
                } else {
                    E[H].n.push({e: K, n: I})
                }
            });
            for (u in E) {
                E[u].s.sort(t)
            }
            for (u in E) {
                var A = E[u], C = [], F = z, w = [0, 0], B;
                switch (p.place) {
                    case"first":
                        c.each(A.s, function (G, H) {
                            F = g(F, H.n)
                        });
                        break;
                    case"org":
                        c.each(A.s, function (G, H) {
                            C.push(H.n)
                        });
                        break;
                    case"end":
                        F = A.n.length;
                        break;
                    default:
                        F = 0
                }
                for (B = 0; B < z; B++) {
                    var q = b(C, B) ? !e : B >= F && B < F + A.s.length, v = (q ? A.s : A.n)[w[q ? 0 : 1]].e;
                    v.parent().append(v);
                    if (q || !p.returns) {
                        n.push(v.get(0))
                    }
                    w[q ? 0 : 1]++
                }
            }
            D.length = 0;
            Array.prototype.push.apply(D, n);
            return D
        }
    });
    function a(k) {
        return k && k.toLowerCase ? k.toLowerCase() : k
    }

    function b(m, p) {
        for (var o = 0, k = m.length; o < k; o++) {
            if (m[o] == p) {
                return !e
            }
        }
        return e
    }

    c.fn.TinySort = c.fn.Tinysort = c.fn.tsort = c.fn.tinysort
})(jQuery);
/* Array.prototype.indexOf for IE (issue #26) */
if (!Array.prototype.indexOf) {
    Array.prototype.indexOf = function (b) {
        var a = this.length, c = Number(arguments[1]) || 0;
        c = c < 0 ? Math.ceil(c) : Math.floor(c);
        if (c < 0) {
            c += a
        }
        for (; c < a; c++) {
            if (c in this && this[c] === b) {
                return c
            }
        }
        return -1
    }
}
;