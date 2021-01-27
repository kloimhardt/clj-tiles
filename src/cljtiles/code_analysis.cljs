(ns cljtiles.code-analysis)

(defn inspect-form [s fname]
  (when (coll? s)
    (if (= (first s) fname)
      s
      (reduce (fn [v vs] (or v (inspect-form vs fname))) nil s))))

(defn inspect-froms [edn-code fname]
  (map (fn [form]
         (when (and (list? form)
                    (> (count form) 2)
                    (= (first form) 'defn))
           (inspect-form (nth form 2) fname)))
       edn-code))


(defn inspect-form-2 [s fname]
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
