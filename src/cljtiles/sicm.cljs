(ns cljtiles.sicm
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as ts]
            [sicmutils.value :as vl]
            [sicmutils.numsymb :as ny]
            [sicmutils.generic :as gn]
            [sicmutils.structure :as st]
            [sicmutils.simplify :as sp] ;;necessary to load simplify multifunction
            [sicmutils.value :as vl]
            [sicmutils.function :as fu] ;;necessary for ((gn/* 5 (fn[x] x)) 4)
            [sicmutils.expression :as ex]
            [sicmutils.calculus.derivative :as dr]
            [sicmutils.numerical.minimize :as mn]
            [sicmutils.mechanics.lagrange :as lg]
            [sicmutils.expression.render :as render]
            [clojure.string :as cs]))

(comment

  (def spsi (filter #(cs/starts-with? (str %) ":cljtiles") (map first (s/registry))))
  (filter #(s/valid? % (st/up 3 4)) sps)
  (s/valid? ::sfunction (st/up 3 4))
(kind? kind?)

  )

(def sps [::nu "Number" ::fn "Function" ::sy "Symbol" ::up "Column Vector"])

(defn kind? [e]
  (second (first (filter #(s/valid? (first %) e) (partition 2 sps)))))

(defn tex [x]
  (str "\\["  (render/->TeX (gn/simplify x)) "\\]"))

(def bindings {'up st/up
               '+ gn/+
               '- gn/-
               '* gn/*
               '/ gn//
               'square gn/square
               'Lagrange-equations lg/Lagrange-equations

               'valid? s/valid?
               'kind? kind?
               'tex tex})

(s/def ::fn fn?)
(s/def ::nu number?)
(s/def ::sy symbol?)
(s/def ::nu-sy (s/or :nu ::nu :sy ::sy))
(s/def ::expression
      (s/or :exp2 ::expression-2 :exp-q ::expression-q :exp-U ::expression-U
            :exp3 ::expression-3 :exp2-7 ::expression-gt2 :ns ::nu-sy))

(s/def ::expression-gt2
      (s/and seq?
             #(> (count %) 2)
             (s/cat :a-gt2 #{'* '+} :rest (s/+ ::expression))))
(s/def ::expression-3
      (s/and seq?
             #(= (count %) 3)
             (s/cat :a-3 #{'- '/ 'expt} :rest (s/+ ::expression))))
(s/def ::expression-U
      (s/and seq?
             #(= (count %) 2)
             (s/cat :a-U #{'U '(D U) '((expt D 2) U)}
                    :rest ::expression)))

(s/def ::expression-2
      (s/and seq?
             #(= (count %) 2)
             (s/cat :a-2 #{'sin 'cos '- 'sqrt 'atan} :rest ::expression)))

(s/def ::expression-q
      (s/and seq?
             #(= (count %) 2)
             (s/cat :a-q #{'q '(D q) '((expt D 2) q)
                           'x '(D x) '((expt D 2) x)
                           'y '(D y) '((expt D 2) y)
                           'z '(D z) '((expt D 2) z)
                           'r '(D r) '((expt D 2) r)
                           'phi '(D phi) '((expt D 2) phi)
                           'theta '(D theta) '((expt D 2) theta)}
                    :rest ::nu-sy)))

(s/def ::type #{:sicmutils.expression/numerical-expression})
(s/def ::eexpression (s/keys :req-un [::type ::expression]))
(s/def ::nu-sy-eex (s/or :ns ::nu-sy :eex ::eexpression))

(defn the-terms [f] nil #_(.terms f)) ;; a hack as .terms does not cljs compile

(s/def ::differential (s/and #(instance?
                              sicmutils.calculus.derivative.Differential %)
                            #(s/valid? ::nu-sy-eex (second (first (the-terms %))))))

(s/def ::nu-sy-eex-dr (s/or :ns ::nu-sy :dr ::differential :eex ::eexpression))

(s/def ::up-args (s/cat :a-up ::nu-sy-eex-dr-fn-up
                        :b-up ::nu-sy-eex-dr-fn-up
                        :c-up (s/? ::nu-sy-eex-dr-fn-up)))

(s/def ::up (s/and #(instance? sicmutils.structure.Structure %)
                   #(> (count %) 1)
                   #(< (count %) 4)
                   #(#{:sicmutils.structure/up} (vl/kind %))
                   #(s/valid? ::up-args (seq %))))

(s/def ::nu-sy-eex-dr-fn-up (s/or :nse ::nu-sy-eex :fn fn? :up ::up :dr ::differential))
(s/def ::dow-args (s/cat :a-dow ::nu-sy-eex-dr-fn-up
                         :b-dow ::nu-sy-eex-dr-fn-up
                         :c-dow (s/? ::nu-sy-eex-dr-fn-up)))

(s/def ::dow (s/and #(instance? sicmutils.structure.Structure %)
                    #(> (count %) 1)
                    #(< (count %) 4)
                    #(#{:sicmutils.structure/down} (vl/kind %))
                    #(s/valid? ::dow-args (seq %))))

(s/def ::nu-sy-eex-dr-fn-up-dow (s/or :nsedfu ::nu-sy-eex-dr-fn-up :dow ::dow))

(s/def ::sfunction #(instance? sicmutils.function.Function %))
(s/def ::nu-sy-eex-dr-fn-up-sfn (s/or :nsedfu ::nu-sy-eex-dr-fn-up :sf ::sfunction))
(s/def ::up-fnargs (s/cat :a-uf ::nu-sy-eex-dr-fn-up-sfn
                          :opt (s/? (s/cat
                                     :b-uf ::nu-sy-eex-dr-fn-up-sfn
                                     :c-uf (s/? ::nu-sy-eex-dr-fn-up-sfn)))))

(s/fdef ny/mul :args (s/cat :a-mul ::expression :b-mul ::expression))
(s/fdef gn/bin* :args (s/cat :a-bmul ::nu-sy-eex-dr-fn-up-dow :b-bmul ::nu-sy-eex-dr-fn-up-dow))
(s/fdef ny/add :args (s/cat :a-add ::expression :b-add ::expression))
(s/fdef gn/bin+ :args (s/cat :a-badd ::nu-sy-eex-dr-fn-up :b-badd ::nu-sy-eex-dr-fn-up))
(s/fdef ny/sub :args (s/cat :a-sub ::expression :b-sub ::expression))
(s/fdef gn/bin- :args (s/cat :a-sub ::nu-sy-eex-dr-fn-up-dow :b-sub ::nu-sy-eex-dr-fn-up-dow))
(s/fdef ny/div :args (s/cat :a-div ::expression :b-div ::expression))
(s/fdef gn/bin-div :args (s/cat :a-bdiv ::nu-sy-eex-dr-fn-up :b-bdiv ::nu-sy-eex-dr-fn-up))
(s/fdef gn/square :args (s/cat :a-squre (s/or :nse ::nu-sy-eex :up ::up :dr ::differential)))
(s/fdef ny/expt :args (s/cat :a-expt (s/or :dr ::differential :up ::up :exp ::expression) :expt2 int?))
(s/fdef ny/sine :args (s/cat :a (s/or :dr ::differential :ex ::expression)))
(s/fdef gn/sin :args (s/cat :a ::nu-sy-eex-dr))
(s/fdef ny/cosine :args (s/cat :a (s/or :dr ::differential :ex ::expression)))
(s/fdef gn/cos :args (s/cat :a ::nu-sy-eex-dr))
(s/fdef st/up :args ::up-fnargs)

(s/fdef lg/Lagrangian-action
  :args (s/cat :lagrangian fn? :test-path fn? :start-time number? :end-time number?))

(s/fdef mn/minimize
  :args (s/alt
         :three (s/cat :a3-min fn? :b3-min number? :c3-min number?)
         :four (s/cat :a4-min fn? :b4-min number? :c4-min number? :d4-min any?)))

(s/fdef lg/make-path :args
        (s/cat :t0 number? :q0 number?
               :t1 number? :q1 number?
               :qs (s/spec #(s/valid? (s/+ number?) (into [] %)))))

(s/fdef lg/parametric-path-action :args
        (s/cat :a1 fn? :a2 number? :a3 number? :a4 number? :a5 number?))

(s/fdef mn/multidimensional-minimize :args
        (s/alt :two (s/cat :a1 fn? :a2 (s/spec (s/+ number?)))
               :three (s/cat :a1 fn? :a2 (s/spec (s/+ number?)) :a3 any?)))

(s/fdef lg/find-path :args (s/cat :a1 fn? :a2 number?
                                  :a3 number? :a4 number? :a5 number? :a6 int?))

(s/fdef lg/linear-interpolants :args (s/cat :a1 number? :a2 number? :a3 int?))
(s/fdef lg/L-harmonic :args (s/cat :a1 ::nu-sy :a2 ::nu-sy))
(s/fdef lg/Lagrange-equations :args (s/cat :a1 fn?))
(s/fdef lg/L-uniform-acceleration :args (s/cat :a1 ::nu-sy :a2 ::nu-sy))
(s/fdef lg/L-central-rectangular :args
        (s/cat :a1 ::nu-sy-eex :a2 (s/or :o1 ::sfunction :o2 fn?)))

(s/fdef lg/L-central-polar :args
        (s/cat :a1 ::nu-sy-eex :a2 (s/or :o1 ::sfunction :o2 fn?)))

(s/fdef lg/F->C :args (s/cat :a fn?))
(s/fdef lg/L-free-particle :args (s/cat :a ::nu-sy))
(s/fdef lg/Gamma :args (s/cat :a fn?))
(s/fdef lg/->local :args (s/cat :a symbol?
                                :b (s/or :s symbol? :u ::up)
                                :c (s/or :s symbol? :u ::up)))

(s/def ::up-down-expression
  (s/and seq?
         #(> (count %) 2)
         #(< (count %) 5)
         (s/cat :a-udex #{'up 'down}
                :rest (s/+ (s/or :e ::expression :udex ::up-down-expression)))))

;;simplify-spec-full is not used, it checks the result of a simplify call
;;but this check would not be deactiveted with (ts/unstrument)
#_(defn simplify-spec-full [x]
  (let [s (ts/with-instrument-disabled (gn/simplify x))]
    (do
      (if-not  (s/valid? (s/or :e ::expression :udex ::up-down-expression) s)
        (throw (Exception. "simplify result does not conform to spec")))
      s)))

(defn simplify-spec [x]
  (ts/with-instrument-disabled (gn/simplify x)))

(s/fdef simplify-spec :args (s/cat :a (s/or :e ::eexpression :u ::up :d ::dow)))

;;If you like to activate spec: (ts/instrument) If you like to deactivate spec: (ts/unstrument)

;;(ts/instrument)
