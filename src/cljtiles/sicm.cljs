#_"SPDX-License-Identifier: GPL-3.0"

(ns cljtiles.sicm
  (:refer-clojure :exclude [+ * / -])
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as ts]
            [sicmutils.value :as vl]
            [sicmutils.numsymb :as ny]
            [sicmutils.generic :as gn]
            [sicmutils.ratio :as rt]
            [sicmutils.structure :as st]
            [sicmutils.simplify :as sp] ;;necessary to load simplify multifunction
            [sicmutils.function :as fu] ;;necessary for ((gn/* 5 (fn[x] x)) 4)
            [sicmutils.expression :as ex]
            [sicmutils.differential :as dr]
            [sicmutils.numerical.minimize :as mn]
            [sicmutils.mechanics.lagrange :as lg]
            [sicmutils.expression.render :as render]
            [sicmutils.abstract.function :as af :include-macros true]
            [sci.impl.vars]
            [reagent.ratom]))

(defn tex [x]
  (binding [render/*TeX-sans-serif-symbols* false
            render/*TeX-vertical-down-tuples* false]
    (str "\\["  (render/->TeX (gn/simplify x)) "\\]")))

(defn inline-tex [x]
  (binding [render/*TeX-sans-serif-symbols* false
            render/*TeX-vertical-down-tuples* false]
    (render/->TeX (gn/simplify x))))

(def sps [::string "Text" ::nil "Nothing" ::nu "Number" #_::sfunction #_"SicmFunction" ::sci-var "Definition" ::fn "Function" ::sy "Symbol" ::up "ColumnVector" ::dow "RowVector" ::differential "Differential" ::literal-expression "Expression" ::literal-function "LiteralFunction" ::hash-table "HashTable" ::list "List" ::clojure-vector "Collection" ::boolean "Boolean" ::ratio "Ratio" ::keyword "Keyword" ::ratom "ReagentAtom"])

(defn function-name [e]
  ;;does not work with tree-shaking and shadow-cljs simple optimization
  ;;i.e. always return the :else branch
  (cond
    (= (subs (str e) 0 18) "function cljs$core")
    (str "ClojureCoreFunction \\ " (subs (.-name e) 10))
    (= (subs (str e) 0 18) "function sicmutils")
    (str "SicmutilsFunction \\ " (subs (.-name e) 10))
    :else "Function"))

(defn classify [e]
  (first (filter #(s/valid? (first %) e) (partition 2 sps))))

(defn kind-s? [e]
  (let [[spc text] (classify e)
        differential (fn [^dr/Differential e1] (let [t (.-terms  e1)] [(second (first t)) (last (last t))]))]
    (cond
      (= spc ::up)
      (str text "\\ " (inline-tex (apply st/up (map kind-s? e))))
      (= spc ::dow)
      (str text "\\ " (inline-tex (apply st/down (map kind-s? e))))
      (= spc ::differential)
      (str text ":\\ " (apply #(str %1 "\\rightarrow " %2) (map kind-s? (differential e))))
      (= spc ::list)
      (str text "(" (apply str (map #(str (kind-s? %) "\\ ") e)) ")")
      (= spc ::clojure-vector)
      (str text "[" (apply str (map #(str (kind-s? %) "\\ ") e)) "]")
      (= spc ::hash-table)
      (str text "\\{" (apply str (map (fn [[k v]] (str (kind-s? k)  "\\ " (kind-s? v) "\\ ")) e))"\\}")
      (= spc ::fn) (let [a (fu/arity e)
                         n (second a)
                         s (if (> n 1) "s" "")]
                     (str (function-name e) ",\\ " (subs (str (first a)) 1)
                          "\\ " n "\\ parameter" s))
      (= spc ::sci-var) (str text "\\ " (subs (str e) 7))
      (= spc ::ratom) "ReagentAtom"
      (= spc ::keyword) (str text "\\ " e)
      :else
      (if text
        (str text "\\ " (inline-tex e))
        (str "UnknownType" "\\ " e)))))

(defn kind? [e]
  (tex (kind-s? e)))

(comment
  (s/valid? ::up (let [t (.-terms (first (first @u)))] [(second (first t)) (last (last t))]))
  (s/explain ::differential (first (first @u)))

  (s/valid? ::literal-expression (first (last @u)))

  (instance? sicmutils.expression/Literal (last @u))
  (s/valid? ::nu (second (first (.-terms (first (first @u))))))
  (s/valid? ::nu-sy-eex-eliteral (second (first (.-terms (first (first @u))))))

  (instance? sicmutils.abstract.function/Function (last @u))
  (s/explain ::differential (first (first @u))))

(def bindings {'sicmutils-double sicmutils.util/double
               ;;'L-free-particle-sicm lg/L-free-particle ;; changed for nex line, see sicm issue #271
               ;;'Lagrangian-action-sicm lg/Lagrangian-action ;; not needed since sicmutils 0.21.1
               })

;;necessary for sicmutils version < 0.15.0
#_(def bindings {'up st/up
               'down st/down
               '+ gn/+
               '- gn/-
               '* gn/*
               '/ gn//
               'square gn/square
               'Lagrange-equations lg/Lagrange-equations
               'literal-function af/literal-function
               'sin gn/sin
               'cos gn/cos
               'compose fu/compose
               'F->C lg/F->C

               'valid? s/valid?
               'kind? kind?
               'tex tex})

(s/def ::ratom #(instance? reagent.ratom/RAtom %))
(s/def ::keyword keyword?)
(s/def ::ratio rt/ratio?)
(s/def ::clojure-vector vector?)
(s/def ::boolean boolean?)
(s/def ::sci-var #(instance? sci.impl.vars/SciVar %))
(s/def ::list seq?)
(s/def ::hash-table map?)
(s/def ::string string?)
(s/def ::nil nil?)
(s/def ::literal-expression #(instance? sicmutils.expression/Literal %))
(s/def ::literal-function #(instance? sicmutils.abstract.function/Function %))
(s/def ::fn #(or (instance? cljs.core/MultiFn %) (fn? %)))

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

(s/def ::nu-sy-eex-eliteral (s/or :nsx ::nu-sy-eex :lex ::literal-expression))

(defn the-terms [^dr/Differential f] (.-terms f))

(s/def ::differential (s/and #(instance?
                               sicmutils.differential.Differential %)
                             #(s/valid? ::nu-sy-eex-eliteral (second (first (the-terms %))))))

(s/def ::nu-sy-eex-dr (s/or :ns ::nu-sy :dr ::differential :eex ::eexpression))

(s/def ::up-args (s/cat :a-up ::nu-sy-eex-dr-fn-up
                        :b-up ::nu-sy-eex-dr-fn-up
                        :c-up (s/? ::nu-sy-eex-dr-fn-up)))

(s/def ::up (s/and #(instance? sicmutils.structure.Structure %)
                   #(> (count %) 0)
                   #(< (count %) 4)
                   #(#{:sicmutils.structure/up} (vl/kind %))
                   ;;#(s/valid? ::up-args (seq %)) ;;hack removed
                   ))

(s/def ::nu-sy-eex-dr-fn-up (s/or :nse ::nu-sy-eex :fn fn? :up ::up :dr ::differential))
(s/def ::dow-args (s/cat :a-dow ::nu-sy-eex-dr-fn-up
                         :b-dow ::nu-sy-eex-dr-fn-up
                         :c-dow (s/? ::nu-sy-eex-dr-fn-up)))

(s/def ::dow (s/and #(instance? sicmutils.structure.Structure %)
                    #(> (count %) 0)
                    #(< (count %) 4)
                    #(#{:sicmutils.structure/down} (vl/kind %))
                    ;; #(s/valid? ::dow-args (seq %)) ;;hack-removed
                    ))

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
