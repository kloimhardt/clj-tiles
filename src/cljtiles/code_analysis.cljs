(ns cljtiles.code-analysis)

(defn inspect-form [s fname]
  (when (coll? s)
    (if (= (first s) fname)
      s
      (reduce (fn [v vs] (or v (inspect-form vs fname))) nil s))))

(defn inspect-froms [edn-code fname]
  (map (fn [form]
         (when (and (list? form)
                    (= 4 (count form))
                    (= (first form) 'defn))
           (inspect-form (nth form 2) fname)))
       edn-code))

