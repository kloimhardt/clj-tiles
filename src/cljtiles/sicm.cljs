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

(def bindings {'up st/up
               '* gn/*
               'valid? s/valid?
               'kind? kind?})

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

(defn func? [] false #_(instance? sicmutils.function.Function %)) ;;hack

(s/def ::sfunction func?)
(s/def ::nu-sy-eex-dr-fn-up-sfn (s/or :nsedfu ::nu-sy-eex-dr-fn-up :sf ::sfunction))
(s/def ::up-fnargs (s/cat :a-uf ::nu-sy-eex-dr-fn-up-sfn
                          :opt (s/? (s/cat
                                      :b-uf ::nu-sy-eex-dr-fn-up-sfn
                                      :c-uf (s/? ::nu-sy-eex-dr-fn-up-sfn)))))
(s/valid? ::up (st/up 4 5))
