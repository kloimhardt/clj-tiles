(ns cljtiles.org.four)

;; 4Cl #57 implement comp

(defn my-comp [& functions]
  (reduce (fn [f g]
            (fn [& args]
              (f (apply g args))))
          functions))

(= (inc (inc 0))
   ((my-comp inc inc) 0))
