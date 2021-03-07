(ns cljtiles.code-analysis
  (:require [clojure.walk :as w]))

(defn inspect-form [s fname]
  (when (coll? s)
    (if (= (first s) fname)
      s
      (reduce (fn [v vs] (or v (inspect-form vs fname))) nil s))))

#_(defn inspect-froms [edn-code fname]
  (map (fn [form]
         (when (and (list? form)
                    (> (count form) 2)
                    (= (first form) 'defn))
           (inspect-form (nth form 2) fname)))
       edn-code))


#_(defn inspect-form-2 [s fname]
  (when (coll? s)
    (if (= (first s) fname)
      s
      (when
          (reduce (fn [v vs] (or v (inspect-form-2 vs fname))) nil s)
        s))))

(comment

  (def a '(defn hu hi (ti x)))
  (inspect-form a 'ti) ;; => (ti x)
  (inspect-form-2 a 'ti) ;; => (defn hu hi (ti x))

  )

(defn insert-inspect [edn-code inspect-form place]
  (concat (take place edn-code)
          (conj (drop place edn-code) inspect-form)))

(defn trans-fn [edn-code i-fn-name]
  (if (coll? edn-code)
    (if-let [n (get {'fn 1 'defn 2} (first edn-code))]
      (if (> (count edn-code) n)
        (if-let [ifo (inspect-form (nth edn-code n) i-fn-name)]
          (let [cclean (w/postwalk-replace {ifo (last ifo)} edn-code)]
            (insert-inspect cclean ifo (inc n)))
          edn-code)
        edn-code)
      edn-code)
    edn-code))

(defn trans-defn-name [edn-code i-fn-name]
  (if (and (coll? edn-code) (= 'defn (first edn-code)))
    (let [sc (second edn-code)]
      (if (and (list? sc) (= i-fn-name (first sc)))
        (list i-fn-name  (list 'defn (last sc) (drop 2 edn-code)))
        edn-code))
    edn-code))

(defn prepare-fns [edn-code i-fn-name]
  (->> edn-code
       (w/postwalk #(trans-fn % i-fn-name))
       (mapv #(trans-defn-name % i-fn-name))))

(comment

  (def a '(defn f [p1 [[[(ti p)]]]] 1))

  (def c '(fn [p1 [[[(ti p)]]]] 1))
  (def b '(defn f [p1 [[[p]]]] (fn [q1 [[[[(ti q)]]]]] 1)))

  (def ifo (inspect-form c 'ti))
  (def cclean (w/postwalk-replace {ifo (last ifo)} c))
  (insert-inspect cclean ifo)

  (def uu [a b c])

  (prepare-fns uu 'ti)
  (map #(trans-fn % 'ti) uu)
  (trans-fn a 'ti)

  (trans-fn c 'ti)

  (coll? c)

  (def g '(defn (ti f) [p1 [[[(ti p)]]]] 1))
  (let [sc (second g)]
    (if (and (list? sc) (= 'ti (first sc)))
      (list 'ti  (list 'defn (last sc) (drop 2 g)))
      g))
  (trans-defn-name g 'ti)
  )
